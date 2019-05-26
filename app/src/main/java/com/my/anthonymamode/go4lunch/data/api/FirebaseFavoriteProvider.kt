package com.my.anthonymamode.go4lunch.data.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val FAVORITES_COLLECTION_NAME = "favorites"

private fun getFavorites(): CollectionReference {
    return Firebase.firestore.collection(FAVORITES_COLLECTION_NAME)
}

fun setFavoriteRestaurant(uid: String, placeId: String): Task<Void> {
    val id = uid + placeId
    val data = HashMap<String, String>()
    data["userId"] = uid
    return getFavorites().document(id).set(data)
}

fun getFavoriteRestaurant(uid: String, placeId: String): Task<DocumentSnapshot> {
    val id = uid + placeId
    return getFavorites().document(id).get()
}

fun deleteFavoriteRestaurant(uid: String, placeId: String): Task<Void> {
    val id = uid + placeId
    return getFavorites().document(id).delete()
}