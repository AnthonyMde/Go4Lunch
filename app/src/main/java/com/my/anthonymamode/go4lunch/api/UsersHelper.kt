package com.my.anthonymamode.go4lunch.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.my.anthonymamode.go4lunch.model.User

private const val USERS_COLLECTION_NAME = "users"

fun getUsersCollection(): CollectionReference {
    return FirebaseFirestore.getInstance().collection(USERS_COLLECTION_NAME)
}

fun createUser(uid: String, displayName: String?, email: String?, photoPath: String?): Task<Void> {
    val userToCreate = User(uid, displayName, email, photoPath)
    return getUsersCollection().document(uid).set(userToCreate)
}

fun deleteUser(uid: String): Task<Void> {
    return getUsersCollection().document(uid).delete()
}