package com.example.gpa_calculator;

import com.example.gpa_calculator.model.Course;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.FileWriter;
import java.io.IOException;


public class ResultController {
    public Button exportBtn;
    @FXML
    private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> colName, colCode;
    @FXML private TableColumn<Course, Double> colCredit;
    @FXML private TableColumn<Course, String> colGrade;
    @FXML private Label totalCreditsLabel;
    @FXML private Label totalPointsLabel;
    @FXML private Label gpaLabel;

    public void setCourses(ObservableList<Course> courses) {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        courseTable.setItems(courses);

        double totalPoints = 0;
        double totalCredits = 0;
        for(Course c : courses) {
            double points = c.getCredit() * c.getGradePoint();
            totalPoints += points;
            totalCredits += c.getCredit();
        }
        totalCreditsLabel.setText(String.format("Total Credits: %.2f", totalCredits));
        totalPointsLabel.setText(String.format("Total Points: %.2f", totalPoints));

        double gpa = totalCredits == 0 ? 0 : totalPoints / totalCredits;
        gpaLabel.setText(String.format("Weighted GPA: %.2f", gpa));
    }

    public void exportToText() {
        ObservableList<Course> courses = courseTable.getItems();
        if (courses.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No courses to export!").show();
            return;
        }
        try (FileWriter writer = new FileWriter("GPA_Result.txt")) {
            writer.write("GPA Calculator Results\n\n");
            double totalPoints = 0;
            double totalCredits = 0;
            for (Course c : courses) {
                writer.write(String.format("Course: %s | Code: %s | Credit: %.2f | Grade: %s\n", c.getName(), c.getCode(), c.getCredit(), c.getGrade()));
                totalPoints += c.getCredit() * c.getGradePoint();
                totalCredits += c.getCredit();
            }
            double gpa = totalCredits == 0 ? 0 : totalPoints / totalCredits;
            writer.write(String.format("\nTotal Credits: %.2f\nTotal Points: %.2f\nWeighted GPA: %.2f\n", totalCredits, totalPoints, gpa));
            new Alert(Alert.AlertType.INFORMATION, "Exported to GPA_Result.txt").show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error exporting file!").show();
        }
    }

}
