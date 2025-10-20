package com.vtol.quizbattleapp

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.vtol.quizbattleapp.model.GameRoom
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
}
