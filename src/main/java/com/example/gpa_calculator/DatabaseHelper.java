package com.example.gpa_calculator;

import com.example.gpa_calculator.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:sample.db";
    private static Connection connection;

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS courses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "code TEXT," +
                    "credit REAL," +
                    "teacher1 TEXT," +
                    "teacher2 TEXT," +
                    "grade TEXT" +
                    ");";

    public static void initDatabase() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            createTable();
        }
    }

    private static void createTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_SQL)) {
            statement.executeUpdate();
        }
    }

    public static void insertCourse(Course c) throws SQLException {
        String sql = "INSERT INTO courses (name, code, credit, teacher1, teacher2, grade) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.name());
            ps.setString(2, c.code());
            ps.setDouble(3, c.credit());
            ps.setString(4, c.teacher1());
            ps.setString(5, c.teacher2());
            ps.setString(6, c.grade());
            ps.executeUpdate();
        }
    }

    public static void updateCourse(int id, Course c) throws SQLException {
        String sql = "UPDATE courses SET name=?, code=?, credit=?, teacher1=?, teacher2=?, grade=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.name());
            ps.setString(2, c.code());
            ps.setDouble(3, c.credit());
            ps.setString(4, c.teacher1());
            ps.setString(5, c.teacher2());
            ps.setString(6, c.grade());
            ps.setInt(7, id);
            ps.executeUpdate();
        }
    }

    public static void deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY id ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course(
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getDouble("credit"),
                        rs.getString("teacher1"),
                        rs.getString("teacher2"),
                        rs.getString("grade")
                );
                list.add(c);
            }
        }
        return list;
    }
}
