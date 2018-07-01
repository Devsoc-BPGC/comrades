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
