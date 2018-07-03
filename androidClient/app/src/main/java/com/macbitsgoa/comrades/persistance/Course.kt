package com.macbitsgoa.comrades.persistance

import androidx.lifecycle.LiveData
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.RESTRICT
import androidx.room.Ignore
import com.google.firebase.database.Exclude

/**
 * Definition of table course.
 *
 * @author Rushikesh Jogdand.
 */
@Entity(primaryKeys = ["id"],
        foreignKeys = [ForeignKey(entity = Person::class,
                parentColumns = ["id"],
                childColumns = ["addedById"],
                onDelete = RESTRICT,
                onUpdate = 1)])
class Course {
    var name: String = ""

    var code: String = ""

    var id: String = ""

    var addedById: String = ""

    // Handy attribute. You will have to populate this manually.
    @Ignore
    @Exclude
    var addedBy: LiveData<Person>? = null

    @Exclude
    var isFollowing: Boolean = false
}
