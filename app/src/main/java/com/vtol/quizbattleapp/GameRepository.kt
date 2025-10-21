package com.vtol.quizbattleapp

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.vtol.quizbattleapp.model.GameRoom
import com.vtol.quizbattleapp.model.Player
import com.vtol.quizbattleapp.model.Quiz
import kotlinx.coroutines.tasks.await

class GameRepository {

    private val realtimeDb = FirebaseDatabase.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()

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

    fun getRoom(roomId: String, onRoomsUpdate: (GameRoom) -> Unit) {
        realtimeDb.child("rooms").child(roomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rooms = snapshot.getValue(GameRoom::class.java) ?: GameRoom()
                onRoomsUpdate(rooms)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getPlayers(roomId: String, onPlayersUpdate: (List<String>) -> Unit){
        realtimeDb.child("rooms").child(roomId).child("playerIds").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playerIds = snapshot.children.map { it.key!! }
                Log.v("TOOL","${playerIds.size}")
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
}
