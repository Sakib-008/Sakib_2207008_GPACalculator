package com.example.gpa_calculator;

import com.example.gpa_calculator.model.Course;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class ResultController {

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
}
