//import * as admin from 'firebase-admin'
const functions = require('firebase-functions');
const admin = require("firebase-admin");
let db;

admin.initializeApp(functions.config().firebase);
db = admin.database();

exports.notifyUserUpdate = functions.database.ref('/users/{id}')
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: 'userUpdate',
                value: JSON.stringify(newData)
            },
            topic: `user${newData.id}`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /users/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /users/${newData.id} update:`, error);
            throw error;
        });
    });

exports.notifyCourseUpdate = functions.database.ref('/courses/{id}')
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: 'courseUpdate',
                value: JSON.stringify(newData)
            },
            topic: `courseUpdates`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /courses/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /courses/${newData.id} update:`, error);
            throw error;
        });
    });

exports.notifyMaterialUpdate = functions.database.ref('/materials/{id}')
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: 'materialUpdate',
                value: JSON.stringify(newData)
            },
            topic: `course${newData.courseId}`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /materials/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /materials/${newData.id} update:`, error);
            throw error;
        });
    });


    exports.newCourseAdded = functions.database.ref(`/courses/{courseId}/`).onCreate((snapshot,context) => {
        const newItem = snapshot.val();

        return admin.database().ref(`/stats/`).once('value').then((snapshot,context)=>{
            var total_courses=snapshot.val().totalCourses;
            
            return admin.database().ref(`/users/${newItem.addedById}/`).once('value').then((snapshot,context) =>{
                var creator_photo=snapshot.val().photoUrl;
                var user_name=snapshot.val().name;
                
                admin.database().ref(`/recents/${newItem.id}/`).set({
                    addedById:newItem.addedById,
                    addedByPhoto:creator_photo,
                    addedBy:user_name,
                    courseId:newItem.id,
                    courseName:newItem.name,
                    timeStamp:admin.database.ServerValue.TIMESTAMP,
                    message:`${user_name} added a new course ${newItem.name}`,
                    type:`recent_course`
                });
                
                return admin.database().ref(`/stats/totalCourses`).set(total_courses+1);
            })            
            
            
        })
           
    })
    
    exports.newMaterialAdded = functions.database.ref(`/courseMaterial/{courseId}/{id}/`).onCreate((snapshot,context) => {
        const newItem = snapshot.val();
        const course = context.params.courseId;
        return admin.database().ref(`/courses/${course}/`).once('value').then((snapshot) =>{
            var courseName=snapshot.val().name;

            return admin.database().ref(`/stats/`).once('value').then((snapshot,context)=>{
                var total_uploads=snapshot.val().totalUploads;
                
                return admin.database().ref(`/users/${newItem.addedById}/`).once('value').then((snapshot,context) =>{
                    var creator_photo=snapshot.val().photoUrl;
                    var current_score=snapshot.val().score;    
                    var number_uploads=snapshot.val().uploads;
                    
                    admin.database().ref(`/recents/${newItem.id}/`).set({
                        addedById:newItem.addedById,
                        addedByPhoto:creator_photo,
                        addedBy:newItem.addedBy,
                        fileName:newItem.fileName,
                        fileId:newItem.id,
                        courseId:course,
                        courseName:courseName,
                        timeStamp:admin.database.ServerValue.TIMESTAMP,
                        message:`${newItem.addedBy} added a new file ${newItem.fileName} to the course ${courseName}`,
                        type:`recent_material`
                    });
                    
                    admin.database().ref(`/users/${newItem.addedById}/score`).set(current_score+10);
                    admin.database().ref(`/stats/totalUploads`).set(total_uploads+1);
                    return admin.database().ref(`/users/${newItem.addedById}/uploads`).set(number_uploads+1);
                })            
            
            
            })
           
        });
    })    
    
    exports.newUserAdded = functions.auth.user().onCreate((user) => {

        admin.database().ref(`/users/${user.uid}`).set({
            name:user.displayName,
            email:user.email,
            id:user.uid,
            photoUrl:user.photoURL,
            score:0,
            uploads:0,
            authority:"User"
        })

        return admin.database().ref(`/stats/`).once('value').then((snapshot)=>{
            var total_users = snapshot.val().totalUsers;
            return admin.database().ref(`/stats/totalUsers`).set(total_users+1);
        })

    });
