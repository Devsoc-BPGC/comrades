const firebase = require("./firebase.js");
const admin = firebase.admin;
const functions = firebase.functions;

const fillCourses = (response) => {
    return admin.database().ref(`/debug/courses/`).once("value").then((snapshot) => {
        let courseList = [];
        snapshot.forEach(rawData => {
            let course = {};
            course.name = rawData.val().name;
            courseList.push(course);
        });
        return response.send(courseList);
    });
};

const coursesHandler = (request, response) => {
    fillCourses(response);
};

exports.courses = functions.https.onRequest(coursesHandler);
