package com.example.gpa_calculator;


import com.example.gpa_calculator.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CourseController {
    public TextField courseName, courseCode, courseCredit, teacher1, teacher2;
    public ComboBox<String> grade;
    public Button calculateBtn;
    public Button addCoursesBtn;
    public TextField requiredCreditField;

    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    private double totalCredits = 0;
    private double requiredCredits = -1;

    public void initialize(){
        grade.setItems(FXCollections.observableArrayList(
                "A+", "A", "A-", "B+", "B", "B-", "C+", "C",  "D", "F"
        ));

        addCoursesBtn.setDisable(true);
        calculateBtn.setDisable(true);
    }

    public void setRequiredCredits() {
        try {
            requiredCredits = Double.parseDouble(requiredCreditField.getText());

            if (requiredCredits <= 0) {
                throw new NumberFormatException();
            }

            addCoursesBtn.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Required Credits set to: " + requiredCredits);
            alert.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid credit value! Enter a positive number.");
            alert.show();
        }
    }

    public void addCourse(){
        if (requiredCredits <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please set required credits first!");
            alert.show();
            return;
        }
        try {
            String name = courseName.getText();
            String code = courseCode.getText();
            double credits = Double.parseDouble(courseCredit.getText());
            String t1 =  teacher1.getText();
            String t2 = teacher2.getText();
            String g =  grade.getValue();

            Course c = new Course(name, code, credits, t1, t2, g);
            courseList.add(c);
            totalCredits += credits;

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Course added successfully!");
            alert.show();

            if(totalCredits >= requiredCredits) {
                calculateBtn.setDisable(false);
            }

            courseName.clear();
            courseCode.clear();
            courseCredit.clear();
            teacher1.clear();
            teacher2.clear();
            grade.setValue(null);

        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input!");
            alert.show();
        }
    }

    public void showResult(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("result.fxml"));
        Scene scene = new Scene(loader.load());
        ResultController rc = loader.getController();
        rc.setCourses(courseList);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}
