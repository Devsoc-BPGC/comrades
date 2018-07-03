package com.macbitsgoa.comrades

import com.macbitsgoa.comrades.persistance.Course

const val FCM_TYPE_MATERIAL_UPDATE = "materialUpdate"
const val FCM_TYPE_USER_UPDATE = "userUpdate"
const val FCM_TYPE_COURSE_UPDATE = "courseUpdate"
const val FCM_KEY_VALUE = "value"
const val FCM_KEY_TYPE = "type"
const val USERS = "users"
const val TOPIC_COURSE_UPDATES = "courseUpdates"
const val COURSES = "courses"
/**
 * Get fcm channel name corresponding to the course
 */
fun getTopicForCourse(course: Course) = "course${course.id}"
