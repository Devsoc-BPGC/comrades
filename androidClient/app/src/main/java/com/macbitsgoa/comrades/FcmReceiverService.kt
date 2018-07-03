package com.macbitsgoa.comrades


import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.macbitsgoa.comrades.FirebaseKeys.*
import com.macbitsgoa.comrades.persistance.Course
import com.macbitsgoa.comrades.persistance.DataRepository
import com.macbitsgoa.comrades.persistance.Material
import com.macbitsgoa.comrades.persistance.Person

/**
 * Listens to firebase messages.
 * TODO: move long running methods to some other mechanism.
 *
 * @author Rushikesh Jogdand
 */
@Suppress("ProtectedInFinal")
class FcmReceiverService : FirebaseMessagingService() {
    var repo: DataRepository? = null
    protected val tag = TAG_PREFIX + FcmReceiverService::class.java.simpleName

    override fun onMessageReceived(message: RemoteMessage) {
        repo = DataRepository(application) ?: return
        when (message.data[FCM_KEY_TYPE]) {
            FCM_TYPE_COURSE_UPDATE -> updateCourse(message.data)
            FCM_TYPE_MATERIAL_UPDATE -> updateMaterial(message.data)
            FCM_TYPE_USER_UPDATE -> updatePerson(message.data)
        }
    }

    private fun updateCourse(data: Map<String, String>) {
        val course: Course = Gson().fromJson(data[FCM_KEY_VALUE], Course::class.java)
        firebaseRootRef.child(USERS).child(course.addedById)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(e: DatabaseError) {
                        Log.e(tag, e.message, e.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val p: Person = snapshot.getValue(Person::class.java) ?: return
                        repo!!.insertLocally(p)
                        repo!!.insertLocally(course)
                    }
                })
    }

    private fun updatePerson(data: Map<String, String>) {
        val person: Person = Gson().fromJson(data[FCM_KEY_VALUE], Person::class.java)
        repo!!.insertLocally(person)
    }

    private fun updateMaterial(data: Map<String, String>) {
        val material: Material = Gson().fromJson(data[FCM_KEY_VALUE], Material::class.java)
        firebaseRootRef.child(USERS).child(material.authorId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(e: DatabaseError) {
                        Log.e(tag, e.message, e.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val person: Person = snapshot.getValue(Person::class.java) ?: return
                        repo!!.insertLocally(person)
                        repo!!.insertLocally(material)
                    }
                })
    }
}
