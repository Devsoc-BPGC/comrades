package com.macbitsgoa.comrades.courseListFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.macbitsgoa.comrades.persistance.DataRepository

/**
 * @author Rushikesh Jogdand.
 */
class AddCourseVm(application: Application) : AndroidViewModel(application) {
    private val repo: DataRepository = DataRepository(application)

    /**
     * Creates course as per parameters and signed in user as author.
     */
    fun createCourse(name: String, code: String) = repo.createCourse(name, code)
}
