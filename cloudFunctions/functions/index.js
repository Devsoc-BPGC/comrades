const functions = require("firebase-functions");
const admin = require("firebase-admin");
let db;

admin.initializeApp(functions.config().firebase);
db = admin.database();

exports.newDebugCourseAdded = functions.database.ref(`/debug/courses/{courseId}/`).onCreate((snapshot) => {
    const newItem = snapshot.val();

    return admin.database().ref(`/debug/stats/`).once("value").then((snapshot) => {
        var total_courses = snapshot.val().totalCourses;

        return admin.database().ref(`/debug/users/${newItem.addedById}/`).once("value").then((snapshot) => {
            var creator_photo = snapshot.val().photoUrl;
            var user_name = snapshot.val().name;

            admin.database().ref(`/debug/recents/${newItem.id}/`).set({
                addedById: newItem.addedById,
                addedByPhoto: creator_photo,
                addedBy: user_name,
                courseId: newItem.id,
                courseName: newItem.name,
                timeStamp: admin.database.ServerValue.TIMESTAMP,
                message: `${user_name} added a new course ${newItem.name}`,
                type: `recent_course`
            });
            admin.database().ref(`/debug/courses/${newItem.id}/timeStamp`).set(admin.database.ServerValue.TIMESTAMP);
            return admin.database().ref(`/debug/stats/totalCourses`).set(total_courses + 1);
        })
    })

});

exports.newDebugMaterialAdded = functions.database.ref(`/debug/courseMaterial/{courseId}/{id}/`).onCreate((snapshot, context) => {
    const newItem = snapshot.val();
    const course = context.params.courseId;
    return admin.database().ref(`/debug/courses/${course}/`).once("value").then((snapshot) => {
        var courseName = snapshot.val().name;

        return admin.database().ref(`/debug/stats/`).once("value").then((snapshot) => {
            var total_uploads = snapshot.val().totalUploads;

            return admin.database().ref(`/debug/users/${newItem.addedById}/`).once("value").then((snapshot) => {
                var creator_photo = snapshot.val().photoUrl;
                var current_score = snapshot.val().score;
                var number_uploads = snapshot.val().uploads;

                admin.database().ref(`/debug/recents/${newItem.id}/`).set({
                    addedById: newItem.addedById,
                    addedByPhoto: creator_photo,
                    addedBy: newItem.addedBy,
                    fileName: newItem.fileName,
                    fileId: newItem.id,
                    courseId: course,
                    courseName: courseName,
                    timeStamp: admin.database.ServerValue.TIMESTAMP,
                    message: `${newItem.addedBy} added a new file ${newItem.fileName}`,
                    type: `recent_material`
                });

                admin.database().ref(`/debug/users/${newItem.addedById}/score`).set(current_score + 10);
                admin.database().ref(`/debug/stats/totalUploads`).set(total_uploads + 1);
                admin.database().ref(`/debug/users/${newItem.addedById}/uploads`).set(number_uploads + 1);
                admin.database().ref(`/debug/courseMaterial/${course}/${newItem.hashId}/timeStamp`).set(admin.database.ServerValue.TIMESTAMP);

                const msg = {
                    data: {
                        addedById: newItem.addedById,
                        courseId: course,
                        courseName: courseName,
                        type: "material_added",
                        msg: `${newItem.addedBy} added a new file to ${courseName}`
                    },
                    topic: `debug${course}`
                };

                return admin.messaging().send(msg).then((response) => {
                    console.log(`Successfully sent /debug/materials/${newItem.id} update:`, response);
                    return 0;
                }).catch((error) => {
                    console.log(`Error sending /debug/materials/${newItem.id} update:`, error);
                    throw error;
                });
            })
        })

    });
});

exports.newReleaseCourseAdded = functions.database.ref(`/release/courses/{courseId}/`).onCreate((snapshot) => {
    const newItem = snapshot.val();

    return admin.database().ref(`/release/stats/`).once("value").then((snapshot) => {
        var total_courses = snapshot.val().totalCourses;

        return admin.database().ref(`/release/users/${newItem.addedById}/`).once("value").then((snapshot) => {
            var creator_photo = snapshot.val().photoUrl;
            var user_name = snapshot.val().name;

            admin.database().ref(`/release/recents/${newItem.id}/`).set({
                addedById: newItem.addedById,
                addedByPhoto: creator_photo,
                addedBy: user_name,
                courseId: newItem.id,
                courseName: newItem.name,
                timeStamp: admin.database.ServerValue.TIMESTAMP,
                message: `${user_name} added a new course ${newItem.name}`,
                type: `recent_course`
            });
            admin.database().ref(`/release/courses/${newItem.id}/timeStamp`).set(admin.database.ServerValue.TIMESTAMP);
            return admin.database().ref(`/release/stats/totalCourses`).set(total_courses + 1);
        })
    })
});

exports.newReleaseMaterialAdded = functions.database.ref(`/release/courseMaterial/{courseId}/{id}/`).onCreate((snapshot, context) => {
    const newItem = snapshot.val();
    const course = context.params.courseId;
    return admin.database().ref(`/release/courses/${course}/`).once("value").then((snapshot) => {
        var courseName = snapshot.val().name;

        return admin.database().ref(`/release/stats/`).once("value").then((snapshot) => {
            var total_uploads = snapshot.val().totalUploads;

            return admin.database().ref(`/release/users/${newItem.addedById}/`).once("value").then((snapshot, context) => {
                var creator_photo = snapshot.val().photoUrl;
                var current_score = snapshot.val().score;
                var number_uploads = snapshot.val().uploads;

                admin.database().ref(`/release/recents/${newItem.id}/`).set({
                    addedById: newItem.addedById,
                    addedByPhoto: creator_photo,
                    addedBy: newItem.addedBy,
                    fileName: newItem.fileName,
                    fileId: newItem.id,
                    courseId: course,
                    courseName: courseName,
                    timeStamp: admin.database.ServerValue.TIMESTAMP,
                    message: `${newItem.addedBy} added a new file ${newItem.fileName} to the course ${courseName}`,
                    type: `recent_material`
                });

                admin.database().ref(`/release/users/${newItem.addedById}/score`).set(current_score + 10);
                admin.database().ref(`/release/stats/totalUploads`).set(total_uploads + 1);
                admin.database().ref(`/release/users/${newItem.addedById}/uploads`).set(number_uploads + 1);
                admin.database().ref(`/release/courseMaterial/${course}/${newItem.hashId}/timeStamp`).set(admin.database.ServerValue.TIMESTAMP);

                msg = {
                    data: {
                        addedById: newItem.addedById,
                        courseId: course,
                        courseName: courseName,
                        type: "material_added",
                        msg: `${newItem.addedBy} added a new file to ${courseName}`
                    },
                    topic: `release${course}`
                };
                return admin.messaging().send(msg).then((response) => {
                    console.log(`Successfully sent /release/materials/${newItem.id} update:`, response);
                    return 0;
                }).catch((error) => {
                    console.log(`Error sending /release/materials/${newItem.id} update:`, error);
                    throw error;
                });
            })
        })
    });
});


exports.newUserAdded = functions.auth.user().onCreate((user) => {

    return admin.database().ref(`/debug/users/`).once("value").then((snapshot) => {


        admin.database().ref(`/debug/users/${user.uid}`).set({
            name: user.displayName,
            email: user.email,
            id: user.uid,
            photoUrl: user.photoURL,
            score: 0,
            uploads: 0,
            rank: snapshot.numChildren() + 1,
            authority: "User"
        });

        return admin.database().ref(`/release/users/`).once("value").then((snapshot) => {
            admin.database().ref(`/release/users/${user.uid}`).set({
                name: user.displayName,
                email: user.email,
                id: user.uid,
                photoUrl: user.photoURL,
                score: 0,
                uploads: 0,
                rank: snapshot.numChildren() + 1,
                authority: "User"
            });

            return admin.database().ref(`/release/stats/`).once("value").then((snapshot) => {
                var total_users = snapshot.val().totalUsers;
                return admin.database().ref(`/release/stats/totalUsers`).set(total_users + 1);
            });
        })
    })

});

exports.debugRanks = functions.database.ref("/debug/users/{uuid}/score")
    .onUpdate((snapshot, context) => {
        function upgradeRank(score, rank, uuid) {
            return admin.database().ref(`/debug/users/`).once("value").then((snapshot) => {
                var rankToFind = rank - 1;
                var foundChild = snapshot.find((element)=>{
                    return element.val().rank === rankToFind;
                });
                /*snapshot.forEach((child) => {
                    if (child.val().rank === rankToFind) {
                        foundChild = child.val();
                        return true;
                    }
                });*/
                if (foundChild && foundChild.score < score) {
                    admin.database().ref(`/debug/users/${foundChild.id}/rank`).set(rank);
                    admin.database().ref(`/debug/users/${uuid}/rank`).set(foundChild.rank);

                    if (foundChild.rank !== 1) {
                        upgradeRank(score, foundChild.rank, uuid);
                    }
                }
                return 0;
            });
        }

        const uuid = context.params.uuid;
        return admin.database().ref(`/debug/users/${uuid}/`).once("value").then((snapshot) => {
            var data = snapshot.val();
            var userScore = data.score;
            var userRank = data.rank;

            if (userRank !== 1) {
                upgradeRank(userScore, userRank, uuid);
            }

            return 0;
        });
    });

exports.releaseRanks = functions.database.ref("/release/users/{uuid}/score")
    .onUpdate((snapshot, context) => {
        function upgradeRank(score, rank, uuid) {
            return admin.database().ref(`/release/users/`).once("value").then((snapshot) => {
                var rankToFind = rank - 1;
                var foundChild = snapshot.find((element)=>{
                    return element.val().rank === rankToFind;
                });
                if (foundChild && foundChild.score < score) {
                    admin.database().ref(`/release/users/${foundChild.id}/rank`).set(rank);
                    admin.database().ref(`/release/users/${uuid}/rank`).set(foundChild.rank);

                    if (foundChild.rank !== 1) {
                        upgradeRank(score, foundChild.rank, uuid);
                    }
                }
                return 0;
            });
        }

        const uuid = context.params.uuid;
        return admin.database().ref(`/release/users/${uuid}/`).once("value").then((snapshot) => {
            var data = snapshot.val();
            var userScore = data.score;
            var userRank = data.rank;
            if (userRank !== 1) {
                upgradeRank(userScore, userRank, uuid);
            }
            return 0;
        });
    });

exports.debugCsaNotif = functions.database.ref('/debug/adminFeed/{pushId}').onCreate((snapshot, context) => {
    const newNotif = snapshot.val();

    if (newNotif.notify === true) {
        const payload = {
            sender: `${newNotif.name}`,
            timeStamp: `${newNotif.timestamp}`,
            subject: `${newNotif.title}`,
            post: `${newNotif.post}`
            type: 'csa_notifs'
        }

        return admin.messaging().sendToTopic('CsaNotifications', {data: payload}).then((response) => {
            console.log('Successfully sent notification', response);
            return 0;
        }).catch((error) => {
            console.log('Error sending notification', error);
            throw error;
        });
    }
});
