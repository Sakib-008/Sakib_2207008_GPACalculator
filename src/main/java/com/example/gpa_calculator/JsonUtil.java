package com.example.gpa_calculator;

import com.example.gpa_calculator.model.Course;
import org.json.JSONObject;

public class JsonUtil {
    public static String toJson(Course course) {
        JSONObject obj = new JSONObject();
        obj.put("name", course.name());
        obj.put("code", course.code());
        obj.put("credit", course.credit());
        obj.put("teacher1", course.teacher1());
        obj.put("teacher2", course.teacher2());
        obj.put("grade", course.grade());
        return obj.toString();
    }
    public static Course extractCourse(String jsonString) {
        JSONObject obj = new JSONObject(jsonString);
        return new Course(
                obj.getString("name"),
                obj.getString("code"),
                obj.getDouble("credit"),
                obj.getString("teacher1"),
                obj.getString("teacher2"),
                obj.getString("grade")
        );
    }
}
