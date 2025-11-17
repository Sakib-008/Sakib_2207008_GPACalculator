package com.example.gpa_calculator.model;

public record Course(String name, String code, double credit, String teacher1, String teacher2, String grade) {

    public double getGradePoint() {
        return switch (grade.toUpperCase()) {
            case "A+" -> 4.00;
            case "A" -> 3.75;
            case "A-" -> 3.5;
            case "B+" -> 3.25;
            case "B" -> 3.00;
            case "B-" -> 2.75;
            case "C+" -> 2.50;
            case "C" -> 2.25;
            case "D" -> 2.00;
            case "F" -> 0.00;
            default -> 0;
        };
    }
}
