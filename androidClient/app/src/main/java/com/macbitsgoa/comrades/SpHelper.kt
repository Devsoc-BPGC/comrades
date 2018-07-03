package com.macbitsgoa.comrades

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 * @author Rushikesh Jogdand.
 */

const val defaultPref = "comradesPrefs"
const val selfKey = "self"
const val coursesAdded = "coursesAdded"

/**
 * Get instance of [SharedPreferences] used in this app.
 */
fun defaultPref(context: Context): SharedPreferences =
        context.getSharedPreferences(defaultPref, MODE_PRIVATE)
