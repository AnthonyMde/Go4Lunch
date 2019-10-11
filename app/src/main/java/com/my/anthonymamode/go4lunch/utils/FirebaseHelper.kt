package com.my.anthonymamode.go4lunch.utils

import androidx.lifecycle.LifecycleOwner
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

/**
 * Get options necessary to set up a firestore adapter. T is the type of object
 * the adapter will be used.
 * @param query firestore query used to get the matching objects T
 * @param lifecycleOwner your lifecycle owner (fragment or activity)
 * @return FirestoreRecyclerOptions options used by the firestore adapter
 */
inline fun <reified T> generateOptionForAdapter(
    query: Query,
    lifecycleOwner: LifecycleOwner
): FirestoreRecyclerOptions<T> {
    return FirestoreRecyclerOptions.Builder<T>()
        .setQuery(query, T::class.java)
        .setLifecycleOwner(lifecycleOwner)
        .build()
}
