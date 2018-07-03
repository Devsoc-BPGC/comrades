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

    @SuppressLint("ApplySharedPref")
    fun registerSelf(account: GoogleSignInAccount) {
        val self = Person()
        self.name = account.displayName
        self.email = account.email!!
        self.photoUrl = account.photoUrl.toString()
        self.id = account.id!!
        repo.registerSelf(self)
    }

    fun signOut() = repo.signOut()
}
