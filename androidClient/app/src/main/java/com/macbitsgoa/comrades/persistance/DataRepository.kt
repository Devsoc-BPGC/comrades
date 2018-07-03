package com.macbitsgoa.comrades.persistance

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.macbitsgoa.comrades.*

@Suppress("ProtectedInFinal")
/**
 * @author Rushikesh Jogdand.
 */
class DataRepository(application: Application) {
    private val dao: DataAccessObject = Database.getInstance(application).dao
    val courses: LiveData<List<Course>> = dao.courses
    protected val tag = TAG_PREFIX + DataRepository::class.java.simpleName
    protected val usersRef: DatabaseReference = firebaseRootRef.child(FirebaseKeys.USERS)
    private val coursesRef: DatabaseReference = firebaseRootRef.child(FirebaseKeys.COURSES)
    protected val sharedPref: SharedPreferences = defaultPref(application)

    init {
        if (!sharedPref.getBoolean(coursesAdded, false)) {
            val authors = mutableSetOf<String>()
            val courses = mutableSetOf<Course>()
            coursesRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(tag, p0.message, p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val course: Course = it.getValue(Course::class.java) ?: return@forEach
                        course.isFollowing = false
                        courses.add(course)
                        authors.add(course.addedById)
                        if (BuildConfig.DEBUG) {
                            Log.i(tag, "addedById")
                        }
                    }

                    FirebaseMessaging.getInstance().subscribeToTopic(FirebaseKeys.TOPIC_COURSE_UPDATES)
                    authors.forEach {
                        usersRef.child(it).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Log.e(tag, p0.message, p0.toException())
                            }

                            @SuppressLint("ApplySharedPref")
                            override fun onDataChange(p0: DataSnapshot) {
                                val person: Person = p0.getValue(Person::class.java) ?: return
                                insertLocally(person)
                                authors.remove(person.id)
                                if (BuildConfig.DEBUG) {
                                    Log.i(tag, "added person ${person.name} remaining ${authors.size}, empty = ${authors.isEmpty()}")
                                }
                                if (authors.isEmpty()) {
                                    // all authors added, now foreign key constraint should not fail
                                    courses.forEach {
                                        insertLocally(it)
                                        if (BuildConfig.DEBUG) {
                                            Log.i(tag, "added course ${it.name}")
                                        }
                                    }
                                    sharedPref.edit()
                                            .putBoolean(coursesAdded, true)
                                            .commit()
                                }
                            }
                        })
                    }
                }
            })
        }
    }

    // only fetches from local db, so make sure all required people are fetched
    // and subscribed to
    fun getPerson(id: String): LiveData<Person> = dao.getPerson(id)


    // only fetches from local db, so make sure all courses are fetched and
    // subscribed to
    fun getMaterialsOfCourse(courseId: String): LiveData<List<Material>> =
            dao.getMaterials(courseId)

    fun insertLocally(person: Person) {
        InsertPerson(dao).execute(person)
        FirebaseMessaging.getInstance().subscribeToTopic("user${person.id}")
    }

    fun insertLocally(course: Course) {
        InsertCourse(dao).execute(course)
        if (course.isFollowing) {
            FirebaseMessaging.getInstance().subscribeToTopic("course${course.id}")
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("course${course.id}")
        }
    }

    // course and author for this material are subscribed to, right?
    fun insertLocally(material: Material) {
        InsertMaterial(dao).execute(material)
    }

    private class InsertPerson(private val dao: DataAccessObject) : AsyncTask<Person, Void, Void>() {
        override fun doInBackground(vararg people: Person): Void? {
            dao.insertOrUpdate(people[0])
            return null
        }
    }

    private class InsertCourse(private val dao: DataAccessObject) : AsyncTask<Course, Void, Void>() {
        override fun doInBackground(vararg courses: Course): Void? {
            dao.insertOrUpdate(courses[0])
            return null
        }
    }

    private class InsertMaterial(private val dao: DataAccessObject) : AsyncTask<Material, Void, Void>() {
        override fun doInBackground(vararg materials: Material): Void? {
            dao.insertOrUpdate(materials[0])
            return null
        }
    }

    fun signOut() {
        sharedPref.edit().remove(selfKey).apply()
    }

    fun registerSelf(self: Person) {
        sharedPref.edit().putString(selfKey, Gson().toJson(self)).apply()
        usersRef.child(self.id).setValue(self)
        insertLocally(self)
    }

    fun createCourse(code: String, name: String) {
        val author: Person = Gson().fromJson(sharedPref.getString(selfKey, ""), Person::class.java)
        val course = Course()
        course.code = code
        course.name = name
        course.id = coursesRef.push().key ?: return
        course.isFollowing = true
        course.addedById = author.id
        // author is already added to db, isn't it?
        insertLocally(course)
        FirebaseMessaging.getInstance().subscribeToTopic("course${course.id}")
                .addOnCompleteListener {
                    if (BuildConfig.DEBUG) {
                        Log.i(tag, "Subscription to topic course${course.id} isSuccessful: ${it.isSuccessful}")
                    }
                }
        coursesRef.child(course.id).setValue(course)
    }
}
