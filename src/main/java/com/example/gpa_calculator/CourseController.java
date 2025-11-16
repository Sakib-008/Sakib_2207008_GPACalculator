package com.example.gpa_calculator;


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CourseController {
    public TextField courseName, courseCode, courseCredit, teacher1, teacher2;
    public ComboBox<String> grade;
    public Button calculateBtn, addCouresBtn;

    public void initialize(){
        grade.setItems(FXCollections.observableArrayList(
                "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F"
        ));
    }

    public void addCourse(){
        try {
            String name = courseName.getText();
            String code = courseCode.getText();
            double credits = Double.parseDouble(courseCredit.getText());
            String t1 =  teacher1.getText();
            String t2 = teacher2.getText();
            String g =  grade.getValue();

        }
        catch (Exception e){

        }
    }

    public void showResult(ActionEvent actionEvent) {
    }
}
