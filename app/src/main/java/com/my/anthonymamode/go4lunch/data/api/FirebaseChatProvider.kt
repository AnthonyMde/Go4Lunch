package com.my.anthonymamode.go4lunch.data.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.my.anthonymamode.go4lunch.domain.Message

private const val CHAT_COLLECTION_NAME = "chats"
private const val MESSAGE_COLLECTION_NAME = "messages"

private fun getChatCollection(): CollectionReference {
    return Firebase.firestore.collection(CHAT_COLLECTION_NAME)
}

fun getChatMessages(uid: String, workmateUid: String): Query {
    val chatId = getUniqueChatId(uid, workmateUid)
    return getChatCollection()
        .document(chatId)
        .collection(MESSAGE_COLLECTION_NAME)
        .orderBy("dateCreated")
        .limit(50)
}

fun postMessage(uid: String, workmateUid: String, content: String): Task<DocumentReference> {
    val id = getUniqueChatId(uid, workmateUid)
    // TODO use real Date()
    val message = Message(uid, content, "07:51")
    return getChatCollection().document(id).collection(MESSAGE_COLLECTION_NAME).add(message)
}

/**
 * Return the unique id for a two user chat using natural ordering (based on ASCII order).
 */
fun getUniqueChatId(uid: String, workmateUid: String): String {
    val unsorted = listOf(uid, workmateUid)
    val sorted = unsorted.sortedWith(naturalOrder())
    return sorted.joinToString(separator = "")
}
