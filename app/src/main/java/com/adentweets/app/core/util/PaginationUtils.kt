package com.adentweets.app.core.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object PaginationUtils {

    fun Query.paginateOnce(
        pageSize: Int = Constants.PAGE_SIZE
    ): Flow<List<DataSnapshot>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.take(pageSize).toList()
                trySend(items)
                close()
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        addListenerForSingleValueEvent(listener)
        awaitClose { removeEventListener(listener) }
    }

    fun Query.paginateAfter(
        lastItemKey: String,
        pageSize: Int = Constants.PAGE_SIZE
    ): Query {
        return this.orderByKey().startAfter(lastItemKey).limitToFirst(pageSize)
    }

    fun getCursorFromSnapshot(snapshot: DataSnapshot): String? {
        return snapshot.key
    }
}