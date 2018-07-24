package com.macbitsgoa.comrades.courseListFragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.macbitsgoa.comrades.R
import com.macbitsgoa.comrades.coursematerial.CourseActivity
import com.macbitsgoa.comrades.persistance.Course


/**
 * @author Rushikesh Jogdand.
 */
class CourseVh(private val rootView: View) : RecyclerView.ViewHolder(rootView) {
    private val nameTv: TextView = rootView.findViewById(R.id.tv_course_name)
    private val codeChip: Chip = rootView.findViewById(R.id.chip_course_code)

    /**
     * Populate the view.
     */
    fun populate(course: Course) {
        nameTv.text = course.name
        codeChip.chipText = course.code
        rootView.setOnClickListener { CourseActivity.show(rootView.context, course.id, course.name) }
    }
}
