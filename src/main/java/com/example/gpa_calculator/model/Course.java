package com.example.gpa_calculator.model;

public class Course {

    private int id;
    private final String name;
    private final String code;
    private final double credit;
    private final String teacher1;
    private final String teacher2;
    private final String grade;

    // Constructor for new course (no id yet)
    public Course(String name, String code, double credit, String teacher1, String teacher2, String grade) {
        this(-1, name, code, credit, teacher1, teacher2, grade);
    }

    // Constructor with id (from DB)
    public Course(int id, String name, String code, double credit, String teacher1, String teacher2, String grade) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.credit = credit;
        this.teacher1 = teacher1;
        this.teacher2 = teacher2;
        this.grade = grade;
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String name() { return name; }
    public String code() { return code; }
    public double credit() { return credit; }
    public String teacher1() { return teacher1; }
    public String teacher2() { return teacher2; }
    public String grade() { return grade; }

    // Grade point calculation
    public double getGradePoint() {
        return switch (grade.toUpperCase()) {
            case "A+" -> 4.0;
            case "A" -> 3.75;
            case "A-" -> 3.5;
            case "B+" -> 3.25;
            case "B" -> 3.0;
            case "B-" -> 2.75;
            case "C+" -> 2.5;
            case "C" -> 2.25;
            case "D" -> 2.0;
            case "F" -> 0.0;
            default -> 0;
        };
    }
}
