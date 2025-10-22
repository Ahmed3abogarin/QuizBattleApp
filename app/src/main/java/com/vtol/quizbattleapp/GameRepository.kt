package com.vtol.quizbattleapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.vtol.quizbattleapp.model.GameRoom
import com.vtol.quizbattleapp.model.Player
import com.vtol.quizbattleapp.model.PlayerData
import com.vtol.quizbattleapp.model.PlayerWithScore
import com.vtol.quizbattleapp.model.Quiz
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class GameRepository {

    private val realtimeDb = FirebaseDatabase.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Observe all rooms in real-time
    fun observeAllRooms(onRoomsUpdate: (List<GameRoom>) -> Unit) {
        realtimeDb.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rooms = snapshot.children.mapNotNull { it.getValue(GameRoom::class.java) }
                onRoomsUpdate(rooms)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Fetch a single quiz from Firestore (suspend)
    suspend fun getQuiz(quizId: String): Quiz? {
        return try {
            val snapshot = firestore.collection("Quizzes").document(quizId).get().await()
            snapshot.toObject(Quiz::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getQuizQuestions(quizId: String): Quiz? {
        return try {
            val snapshot = firestore
                .collection("Quizzes")
                .document(quizId)
                .get()
                .await()

            if (snapshot.exists()) {
                snapshot.toObject(Quiz::class.java)
            } else {
                null // no quiz found with this id
            }
        } catch (e: Exception) {
            Log.e("Cosette", "Error fetching quiz: ${e.message}", e)
            null // return null instead of empty Quiz()
        }
    }


    fun getRoom(roomId: String, onRoomsUpdate: (GameRoom) -> Unit) {
        realtimeDb.child("rooms").child(roomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rooms = snapshot.getValue(GameRoom::class.java) ?: GameRoom()
                onRoomsUpdate(rooms)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun observePlayers(roomId: String, onPlayersUpdate: (List<String>) -> Unit) {
        realtimeDb.child("rooms").child(roomId).child("playerIds")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playerIds = snapshot.children.map { it.key!! }
                    Log.v("TOOL", "${playerIds.size}")
                    onPlayersUpdate(playerIds)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun fetchPlayersInfo(ids: List<String>, onResult: (List<Player>) -> Unit) {
        Log.v("TOOL", "${ids.size}")
        if (ids.isEmpty()) {
            // Return empty list immediately if no IDs
            onResult(emptyList())
            return
        }

        firestore.collection("users").whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { snapshot ->
                val players = snapshot.toObjects(Player::class.java)
                onResult(players)
            }

    }

    fun getUserName(onResult: (Player?) -> Unit) {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            // Return empty list immediately if no IDs
            onResult(Player())
            return
        }

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val player = snapshot.toObject(Player::class.java)
                onResult(player)
            }

    }

    fun joinRoom(roomId: String) {
        val roomRef = realtimeDb.child("rooms").child(roomId).child("playerIds")
        val userId = auth.currentUser?.uid
        // Fetch the current player IDs
        roomRef.get().addOnSuccessListener { snapshot ->
            val currentPlayers = snapshot.children.map { it.key!! } // extract all UIDs

            if (userId != null) {


                // If user not already joined, add them
                if (!currentPlayers.contains(userId)) {


                    roomRef.child(userId).setValue(PlayerData(score = 0, hasFinished = false)).addOnSuccessListener {
                        roomRef.child(userId).onDisconnect().removeValue()
                    }


                }

            }


        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }


    fun continueAsQuest(player: Player) {
        auth.signInAnonymously()
            .addOnSuccessListener {
                Log.v("SIGNIN", "success")
                auth.currentUser?.let {
                    saveUserInfo(it.uid, player)
                }

            }.addOnFailureListener {
                Log.v("SIGNIN", it.message.toString())


            }
    }

    private fun saveUserInfo(userId: String, player: Player) {
        firestore.collection("users").document(userId).set(player)
    }

    fun signOut() {
        auth.signOut()
    }

    fun setScore(roomId: String, points: Int) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            realtimeDb.child("rooms")
                .child(roomId)
                .child("playerIds")
                .child(userId)
                .child("score").setValue(points)


        }

    }

    suspend fun loadResult(roomId: String): List<PlayerWithScore> {
        return coroutineScope {
            val scoresSnap = realtimeDb.child("rooms").child(roomId).child("playerIds").get().await()

            // Extract all player data properly
            val scoresMap = scoresSnap.children.mapNotNull { playerSnap ->
                val uid = playerSnap.key ?: return@mapNotNull null
                val playerData = playerSnap.getValue(PlayerData::class.java) ?: return@mapNotNull null
                uid to playerData
            }.toMap()

            // Fetch player names from Firestore concurrently
            scoresMap.map { (uid, playerData) ->
                async {
                    val doc = firestore.collection("users").document(uid).get().await()
                    val name = doc.getString("playerName") ?: "Unknown"
                    PlayerWithScore(name, playerData)
                }
            }.awaitAll()
                .sortedByDescending { it.playerData.score }
        }
    }


    fun removePlayerFromRoom(roomId: String) {
        val userId = auth.currentUser?.uid ?: return
        realtimeDb.child("rooms").child(roomId).child("playerIds").child(userId).removeValue()
    }


    fun checkAllFinished(roomId: String, onAllFinished: () -> Unit) {
        val ref = realtimeDb.child("rooms").child(roomId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(GameRoom::class.java)

                val allFinished = room?.playerIds?.values?.all { it.hasFinished } == true
                if (allFinished) {
                    onAllFinished()
                    ref.removeEventListener(this)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("QuizRepository", "Error: ${error.message}")
            }

        })
    }

    fun setPlayerFinished(roomId: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            realtimeDb.child("rooms")
                .child(roomId)
                .child("playerIds")
                .child(userId)
                .child("hasFinished").setValue(true)
        }
    }


}
