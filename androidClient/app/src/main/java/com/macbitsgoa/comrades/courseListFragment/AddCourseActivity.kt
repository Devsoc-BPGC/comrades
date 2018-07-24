package com.macbitsgoa.comrades.courseListFragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.macbitsgoa.comrades.R

/**
 * Always launch by this method.
 */
fun launchCourseChooser(source: CourseListFragment) {
    source.startActivity(Intent(source.activity, AddCourseActivity::class.java))
}

/**
 * The add course dialog.
 * See [reference](https://developer.android.com/guide/topics/ui/dialogs#ActivityAsDialog)
 *
 * @author Rushikesh Jogdand
 */
class AddCourseActivity : AppCompatActivity() {

    private var nameEt: TextInputEditText? = null
    private var streamIdEt: TextInputEditText? = null
    private var courseNumberEt: TextInputEditText? = null
    private var nameTil: TextInputLayout? = null
    private var streamIdTil: TextInputLayout? = null
    private var courseNumberTil: TextInputLayout? = null
    private val nameWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            nameTil!!.error = if (p0.toString() == "") "a course must have a name" else null
        }
    }
    private val streamWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            streamIdTil!!.error = if (p0.toString() == "") "a stream is a must" else null
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }
    private val courseWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            courseNumberTil!!.error = if (p0.toString() == "") "a course number is must" else null
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        nameEt = findViewById(R.id.et_course_name)
        streamIdEt = findViewById(R.id.et_stream_code)
        courseNumberEt = findViewById(R.id.et_course_number)
        nameTil = findViewById(R.id.til_course_name)
        streamIdTil = findViewById(R.id.til_stream_code)
        courseNumberTil = findViewById(R.id.til_course_number)

        findViewById<View>(R.id.btn_cancel).setOnClickListener { finish() }

        nameEt!!.addTextChangedListener(nameWatcher)

        streamIdEt!!.addTextChangedListener(streamWatcher)

        courseNumberEt!!.addTextChangedListener(courseWatcher)

        findViewById<View>(R.id.btn_add_course).setOnClickListener { addCourseIfPossible() }
    }

    private fun addCourseIfPossible() {
        var allFieldsSet = true
        val courseName = nameEt!!.text.toString()
        val streamId: String = streamIdEt!!.text.toString()
        val courseNumber: String = courseNumberEt!!.text.toString()
        if (courseName == "") {
            nameTil!!.error = "a course must have a name"
            allFieldsSet = false
        }
        if (streamId == "") {
            streamIdTil!!.error = "a stream is a must"
            allFieldsSet = false
        }
        if (courseNumber == "") {
            courseNumberTil!!.error = "a course must have a number"
            allFieldsSet = false
        }
        if (allFieldsSet) {
            ViewModelProviders.of(this).get(AddCourseVm::class.java).createCourse(courseName, "$streamId-$courseNumber")
            finish()
        }
    }
}
