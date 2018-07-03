package com.macbitsgoa.comrades

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Comrades Helper Class.
 * @author Rushikesh Jogdand.
 */

const val TAG_PREFIX = "mac."
const val BITS_EMAIL_SUFFIX = ".bits-pilani.ac.in"
val firebaseRootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
