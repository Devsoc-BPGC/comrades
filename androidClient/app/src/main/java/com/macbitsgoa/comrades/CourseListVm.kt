package com.macbitsgoa.comrades

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.macbitsgoa.comrades.persistance.Course
import com.macbitsgoa.comrades.persistance.DataRepository
import com.macbitsgoa.comrades.persistance.Person

@Suppress("ProtectedInFinal")
/**
 * For use by [MainActivity]
 * @author Rushikesh Jogdand.
 */
class CourseListVm(application: Application) : AndroidViewModel(application) {
    protected val repo: DataRepository = DataRepository(application)
    val courseList: LiveData<List<Course>> = repo.courses

    /**
     * Registers the user. This must be done before any user activity such as
     * downloading, uploading or course creation.
     */
    @SuppressLint("ApplySharedPref")
    fun registerSelf(account: GoogleSignInAccount) {
        val self = Person()
        self.name = account.displayName
        self.email = account.email!! // should not be null as email scope is requested at login
        self.photoUrl = account.photoUrl.toString()
        self.id = account.id!! // should not be null as accountId scope is requested at login
        repo.registerSelf(self)
    }

    /**
     * Unregister the user. This DOES NOT remove the user data from server database.
     * Currently (3-July-2018) following data is persisted for ever:
     * 1. Display Name
     * 2. Email
     * 3. Photo Url as provided by Google Account
     * 4. Account id unique to account - app combo.
     */
    fun signOut() = repo.signOut()
}
