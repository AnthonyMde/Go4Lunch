package com.my.anthonymamode.go4lunch.data.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.my.anthonymamode.go4lunch.domain.Lunch
import com.my.anthonymamode.go4lunch.domain.User

private const val USERS_COLLECTION_NAME = "users"

private fun getUsersCollection(): CollectionReference {
    return Firebase.firestore.collection(USERS_COLLECTION_NAME)
}

fun getCurrentUser(uid: String): Task<DocumentSnapshot?> {
    return getUsersCollection().document(uid).get()
}

fun getCurrentUserData(uid: String): Task<User?> {
    return getUsersCollection().document(uid).get().continueWith {
        it.result?.toObject<User>()
    }
}

fun updateUser(
    uid: String,
    displayName: String?,
    email: String?,
    photoPath: String?,
    hasLunch: Boolean = false,
    lunch: Lunch? = null
): Task<Void> {
    val userToCreate = User(uid, displayName, email, photoPath, hasLunch, lunch)
    return getUsersCollection().document(uid).set(userToCreate)
}

fun updateUser(user: User): Task<Void> {
    return getUsersCollection().document(user.uid).set(user)
}

fun deleteUser(uid: String): Task<Void> {
    return getUsersCollection().document(uid).delete()
}

fun getUsersOrderedByLunch(): Query {
    return getUsersCollection().orderBy("hasLunch", Query.Direction.DESCENDING)
}

fun getUsersByLunchId(placeId: String): Query {
    return getUsersCollection().whereEqualTo("lunch.lunchOfTheDay", placeId)
}
