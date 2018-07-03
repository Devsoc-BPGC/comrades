package com.macbitsgoa.comrades

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Always launch by this method.
 */
fun launchCourseChooser(source: MainActivity) {
    source.startActivity(Intent(source, AddCourseActivity::class.java))
}

class AddCourseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        val nameEt: TextInputEditText = findViewById(R.id.et_course_name)
        val streamIdEt: TextInputEditText = findViewById(R.id.et_stream_code)
        val courseNumberEt: TextInputEditText = findViewById(R.id.et_course_number)
        val nameTil: TextInputLayout = findViewById(R.id.til_course_name)
        val streamIdTil: TextInputLayout = findViewById(R.id.til_stream_code)
        val courseNumberTil: TextInputLayout = findViewById(R.id.til_course_number)

        findViewById<View>(R.id.btn_cancel).setOnClickListener {
            finish()
        }

        nameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                nameTil.error = if (p0.toString() == "") "a course must have a name" else null
            }

        })

        streamIdEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                streamIdTil.error = if (p0.toString() == "") "a stream is a must" else null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        courseNumberEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                courseNumberTil.error = if (p0.toString() == "") "a course number is must" else null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        findViewById<View>(R.id.btn_add_course).setOnClickListener {
            var allFieldsSet = true
            val courseName = nameEt.text.toString()
            val streamId: String = streamIdEt.text.toString()
            val courseNumber: String = courseNumberEt.text.toString()
            if (courseName == "") {
                nameTil.error = "a course must have a name"
                allFieldsSet = false
            }
            if (streamId == "") {
                streamIdTil.error = "a stream is a must"
                allFieldsSet = false
            }
            if (courseNumber == "") {
                courseNumberTil.error = "a course must have a number"
                allFieldsSet = false
            }
            if (allFieldsSet) {
                ViewModelProviders.of(this).get(AddCourseVm::class.java).createCourse(courseName, "$streamId-$courseNumber")
                finish()
            }
        }
    }
}
