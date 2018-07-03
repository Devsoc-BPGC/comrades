package com.macbitsgoa.comrades

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macbitsgoa.comrades.persistance.Course
import java.util.*

/**
 * @author Rushikesh Jogdand.
 */
class CourseAdapter : RecyclerView.Adapter<CourseVh>() {

    private var courses: List<Course> = ArrayList(0)

    /**
     * Update the course list.
     */
    fun setCourses(c: MutableList<com.macbitsgoa.comrades.persistance.Course>) {
        courses = c
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseVh {
        return CourseVh(LayoutInflater.from(parent.context).inflate(R.layout.vh_course, parent, false))
    }

    override fun onBindViewHolder(holder: CourseVh, position: Int) {
        holder.populate(courses[position])
    }

    override fun getItemCount(): Int {
        return courses.size
    }

}
