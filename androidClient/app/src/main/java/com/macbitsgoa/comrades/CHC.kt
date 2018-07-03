package com.macbitsgoa.comrades

import android.util.Log
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Comrades Helper Class.
 * @author Rushikesh Jogdand.
 */

const val TAG_PREFIX = "mac."
const val BITS_EMAIL_SUFFIX = ".bits-pilani.ac.in"
val firebaseRootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

/**
 * [ValueEventListener] logging the error and delegating the user to implement
 * only [ValueEventListener.onDataChange]
 */
abstract class FbListener : ValueEventListener {
    val tag = TAG_PREFIX + FbListener::class.java.simpleName
    override fun onCancelled(p0: DatabaseError) {
        Log.e(tag, p0.message, p0.toException())
    }
}
