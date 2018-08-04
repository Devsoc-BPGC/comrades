package com.macbitsgoa.comrades


import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.macbitsgoa.comrades.coursematerial.CourseActivity
import com.macbitsgoa.comrades.coursematerial.CourseActivity.KEY_COURSE_ID
import com.macbitsgoa.comrades.coursematerial.CourseActivity.KEY_COURSE_NAME
import com.macbitsgoa.comrades.notification.NotificationSettings.SETTINGS
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
    protected val tag = TAG_PREFIX + FcmReceiverService::class.java.simpleName

    override fun onMessageReceived(message: RemoteMessage) {
        val repo = DataRepository(application)
        when (message.data[FCM_KEY_TYPE]) {
            FCM_TYPE_COURSE_UPDATE -> updateCourse(message.data, repo)
            FCM_TYPE_MATERIAL_UPDATE -> updateMaterial(message.data, repo)
            FCM_TYPE_USER_UPDATE -> updatePerson(message.data, repo)
            FCM_TYPE_MATERIAL_ADDED -> materialAdded(message.data)
        }


    }

    private fun materialAdded(data: Map<String, String>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        if (prefs.getBoolean(SETTINGS, true) && FirebaseAuth.getInstance().uid != data["addedById"]) {
            val openCourseActivityIntent = Intent(this, CourseActivity::class.java)
            openCourseActivityIntent.putExtra(KEY_COURSE_ID, data["courseId"])
            openCourseActivityIntent.putExtra(KEY_COURSE_NAME, data["courseName"])
            val pendingIntent = PendingIntent.getActivity(this, 0, openCourseActivityIntent, 0)


            val mBuilder = NotificationCompat.Builder(this, "new noti")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("New File Added")
                    .setContentText(data["msg"])
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_open_in_new_black_24dp, "click to open",
                            pendingIntent)
            val notificationManager = NotificationManagerCompat.from(this)
            val notification = mBuilder.build()
            notificationManager.notify(0, notification)
        }
    }

    private fun updateCourse(data: Map<String, String>, repo: DataRepository) {
        val course: Course = Gson().fromJson(data[FCM_KEY_VALUE], Course::class.java)
        firebaseRootRef.child(USERS).child(course.addedById)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(e: DatabaseError) {
                        Log.e(tag, e.message, e.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val p: Person = snapshot.getValue(Person::class.java) ?: return
                        repo.insertLocally(p)
                        repo.insertLocally(course)
                    }
                })
    }

    private fun updatePerson(data: Map<String, String>, repo: DataRepository) {
        val person: Person = Gson().fromJson(data[FCM_KEY_VALUE], Person::class.java)
        repo.insertLocally(person)
    }

    private fun updateMaterial(data: Map<String, String>, repo: DataRepository) {
        val material: Material = Gson().fromJson(data[FCM_KEY_VALUE], Material::class.java)
        firebaseRootRef.child(USERS).child(material.authorId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(e: DatabaseError) {
                        Log.e(tag, e.message, e.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val person: Person = snapshot.getValue(Person::class.java) ?: return
                        repo.insertLocally(person)
                        repo.insertLocally(material)
                    }
                })
    }
}
