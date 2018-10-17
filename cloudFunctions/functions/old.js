/*
exports.notifyDebugUserUpdate = functions.database.ref("/debug/users/{id}")
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: "userUpdate",
                value: JSON.stringify(newData)
            },
            topic: `debugUser${newData.id}`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /debug/users/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /debug/users/${newData.id} update:`, error);
            throw error;
        });
    });

exports.notifyDebugCourseUpdate = functions.database.ref("/debug/courses/{id}")
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: "courseUpdate",
                value: JSON.stringify(newData)
            },
            topic: `debugCourseUpdates`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /debug/courses/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /debug/courses/${newData.id} update:`, error);
            throw error;
        });
    });

exports.notifyDebugMaterialUpdate = functions.database.ref("/debug/materials/{id}")
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: "materialUpdate",
                value: JSON.stringify(newData)
            },
            topic: `debugCourse${newData.courseId}`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /debug/materials/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /debug/materials/${newData.id} update:`, error);
            throw error;
        });
    });
*/
/*
exports.notifyReleaseUserUpdate = functions.database.ref("/release/users/{id}")
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: "userUpdate",
                value: JSON.stringify(newData)
            },
            topic: `releaseUser${newData.id}`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /release/users/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /release/users/${newData.id} update:`, error);
            throw error;
        });
    });

exports.notifyReleaseCourseUpdate = functions.database.ref("/release/courses/{id}")
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: "courseUpdate",
                value: JSON.stringify(newData)
            },
            topic: `releaseCourseUpdates`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /release/courses/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /release/courses/${newData.id} update:`, error);
            throw error;
        });
    });

exports.notifyReleaseMaterialUpdate = functions.database.ref("/release/materials/{id}")
    .onUpdate((snapshot) => {
        const newData = snapshot.after.val();
        if (newData === null) return;
        const msg = {
            data: {
                type: "materialUpdate",
                value: JSON.stringify(newData)
            },
            topic: `releaseCourse${newData.courseId}`
        };
        return admin.messaging().send(msg).then((response) => {
            console.log(`Successfully sent /release/materials/${newData.id} update:`, response);
            return 0;
        }).catch((error) => {
            console.log(`Error sending /release/materials/${newData.id} update:`, error);
            throw error;
        });
    });

*/
