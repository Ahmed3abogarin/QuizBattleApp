package com.vtol.quizbattleapp.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.vtol.quizbattleapp.util.Resource
import com.vtol.quizbattleapp.model.GameRoom
import com.vtol.quizbattleapp.model.Player
import com.vtol.quizbattleapp.model.PlayerData
import com.vtol.quizbattleapp.model.PlayerWithScore
import com.vtol.quizbattleapp.model.Quiz
import com.vtol.quizbattleapp.model.RoomWithQuiz
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Singleton
class GameRepository @Inject constructor(
    private val realtimeDb: DatabaseReference,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    // Observe all rooms in real-time
    fun observeRooms(): Flow<Resource<List<RoomWithQuiz>>> = callbackFlow {
        trySend(Resource.Loading())

        val listener = realtimeDb.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val rooms = snapshot.children.mapNotNull { it.getValue(GameRoom::class.java) }
                        val roomWithQuizzes = rooms.map { room ->
                            val quiz = getQuiz(room.quizId)
                            RoomWithQuiz(room, quiz)
                        }
                        trySend(Resource.Success(roomWithQuizzes))
                    } catch (e: Exception) {
                        trySend(Resource.Error(e.message ?: "Failed to load data"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        })

        val connListener = realtimeDb.child(".info/connected")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    if (!connected) trySend(Resource.Error("No internet connection"))
                }
                override fun onCancelled(error: DatabaseError) {}
            })


        awaitClose {
            realtimeDb.removeEventListener(listener)
            realtimeDb.removeEventListener(connListener)
        }
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

    suspend fun getQuizQuestions(quizId: String): Resource<Quiz> {
        return try {
            val snapshot = firestore
                .collection("Quizzes")
                .document(quizId)
                .get()
                .await()

            if (snapshot.exists()) {
                val quiz = snapshot.toObject(Quiz::class.java)
                if (quiz != null) Resource.Success(quiz) else Resource.Error("null")

            } else {
                Resource.Error("null")
            }
        } catch (e: Exception) {
            Log.e("Cosette", "Error fetching quiz: ${e.message}", e)
            Resource.Error(e.message.toString())
        }
    }

    fun observePlayersWithInfo(roomId: String): Flow<Resource<List<Player>>> = callbackFlow {
        trySend(Resource.Loading())

        val playersListener = realtimeDb.child("rooms").child(roomId).child("playerIds")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val ids = snapshot.children.mapNotNull { it.key }
                            if (ids.isEmpty()) {
                                trySend(Resource.Success(emptyList()))
                                return@launch
                            }

                            val usersSnap = firestore.collection("users")
                                .whereIn(FieldPath.documentId(), ids)
                                .get()
                                .await()
                            val players = usersSnap.toObjects(Player::class.java)
                            trySend(Resource.Success(players))
                        } catch (e: Exception) {
                            trySend(Resource.Error(e.message ?: "Failed to load players"))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Resource.Error(error.message))
                }
            })


        // check connectivity
        val connectionListener = realtimeDb.child(".info/connected")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    if (!connected) {
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(1500)
                        }
                        trySend(Resource.Error("No internet connection"))
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        awaitClose {
            realtimeDb.removeEventListener(connectionListener)
            realtimeDb.removeEventListener(playersListener)
        }
    }


    fun getUserName(): Flow<Resource<Player>> = flow {

        // why using flow instead of callbackFlow?
        // because I'm fetching once (not observing real-time changes) the user name won't change

        emit(Resource.Loading())

        val userId = auth.currentUser?.uid
        if (userId == null) {
            emit(Resource.Error("Player is not found"))
            return@flow
        }

        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val player = snapshot.toObject(Player::class.java)
            if (player != null) {
                emit(Resource.Success(player))
            } else {
                emit(Resource.Error("Player is not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load player"))
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


    fun continueAsGuest(player: Player): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val authResult = auth.signInAnonymously().await()
            val uid = authResult.user?.uid ?: throw Exception("User not found")

            firestore.collection("users")
                .document(uid)
                .set(player)
                .await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to continue as guest"))
        }
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
