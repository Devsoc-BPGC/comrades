package com.macbitsgoa.comrades.persistance

import android.app.Application
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
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
    protected val usersRef: DatabaseReference = firebaseRootRef.child(USERS)
    private val coursesRef: DatabaseReference = firebaseRootRef.child(COURSES)
    protected val sharedPref: SharedPreferences = defaultPref(application)

    init {
        if (!sharedPref.getBoolean(coursesAdded, false)) {
            val authors = mutableSetOf<String>()
            val courses = mutableSetOf<Course>()
            coursesRef.addValueEventListener(object : FbListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val course: Course = it.getValue(Course::class.java) ?: return@forEach
                        courses.add(course)
                        authors.add(course.addedById)
                    }

                    authors.forEach {
                        usersRef.child(it).addValueEventListener(object : FbListener() {
                            override fun onDataChange(p: DataSnapshot) {
                                val person: Person = p.getValue(Person::class.java) ?: return
                                insertLocally(person)
                                authors.remove(person.id)
                                if (authors.isEmpty()) {
                                    // all authors added, now foreign key constraint should not fail
                                    courses.forEach {
                                        insertLocally(it)
                                    }
                                    sharedPref.edit()
                                            .putBoolean(coursesAdded, true)
                                            .apply()
                                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_COURSE_UPDATES)
                                }
                            }
                        })
                    }
                }
            })
        }
    }

    /**
     * Fetches the [Person] from local db.
     */
    fun getPerson(id: String): LiveData<Person> = dao.getPerson(id)


    /**
     * Fetches the [Material]s from local db.
     */
    fun getMaterialsOfCourse(courseId: String): LiveData<List<Material>> =
            dao.getMaterials(courseId)

    /**
     * Add the [Person] to local db.
     */
    fun insertLocally(person: Person) {
        InsertPerson(dao).execute(person)
        FirebaseMessaging.getInstance().subscribeToTopic("user${person.id}")
    }

    /**
     * Add the [Course] to local db.
     */
    fun insertLocally(course: Course) {
        InsertCourse(dao).execute(course)
        if (course.isFollowing) {
            FirebaseMessaging.getInstance().subscribeToTopic(getTopicForCourse(course))
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(getTopicForCourse(course))
        }
    }

    /**
     * Add the [Material] to local db.
     * Course and author for this material are subscribed to, right?
     */
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

    /**
     * Removes user from shared pref.
     */
    fun signOut() {
        sharedPref.edit().remove(selfKey).apply()
    }

    /**
     * Add user to shared pref, local db and firebase.
     */
    fun registerSelf(self: Person) {
        sharedPref.edit().putString(selfKey, Gson().toJson(self)).apply()
        usersRef.child(self.id).setValue(self)
        insertLocally(self)
    }

    /**
     * Creates course and saves the data to relevant places.
     */
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
