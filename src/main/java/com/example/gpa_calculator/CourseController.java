package com.example.gpa_calculator;


import com.example.gpa_calculator.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

public class CourseController {
    public TextField courseName, courseCode, courseCredit, teacher1, teacher2;
    public ComboBox<String> grade;
    public Button calculateBtn, addCoursesBtn, editCourseBtn, deleteCourseBtn , resetBtn, exportBtn;
    public TextField requiredCreditField;
    public TableView<Course> courseTable;
    public TableColumn<Course, String> colName, colCode, colGrade;
    public TableColumn<Course, Double> colCredit;

    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    private double totalCredits = 0;
    private double requiredCredits = -1;

    public void initialize(){
        grade.setItems(FXCollections.observableArrayList(
                "A+", "A", "A-", "B+", "B", "B-", "C+", "C",  "D", "F"
        ));

        addCoursesBtn.setDisable(true);
        calculateBtn.setDisable(true);

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        courseTable.setItems(courseList);
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
        if (grade.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a grade!");
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

    public void editCourse() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "No course selected!").show();
            return;
        }
        courseName.setText(selected.getName());
        courseCode.setText(selected.getCode());
        courseCredit.setText(String.valueOf(selected.getCredit()));
        teacher1.setText(selected.getTeacher1());
        teacher2.setText(selected.getTeacher2());
        grade.setValue(selected.getGrade());

        courseList.remove(selected);
        totalCredits -= selected.getCredit();
        if (totalCredits < requiredCredits) calculateBtn.setDisable(true);
    }

    public void deleteCourse() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "No course selected!").show();
            return;
        }
        courseList.remove(selected);
        totalCredits -= selected.getCredit();
        if (totalCredits < requiredCredits) calculateBtn.setDisable(true);
    }

    public void resetAll() {
        courseList.clear();
        totalCredits = 0;
        requiredCredits = -1;
        calculateBtn.setDisable(true);
        addCoursesBtn.setDisable(true);

        courseName.clear();
        courseCode.clear();
        courseCredit.clear();
        teacher1.clear();
        teacher2.clear();
        grade.setValue(null);
        requiredCreditField.clear();
    }

    public void exportToText() {
        if (courseList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No courses to export!").show();
            return;
        }
        try (FileWriter writer = new FileWriter("GPA_Result.txt")) {
            writer.write("GPA Calculator Results\n\n");
            double totalPoints = 0;
            double totalCreditsLocal = 0;
            for (Course c : courseList) {
                writer.write(String.format("Course: %s | Code: %s | Credit: %.2f | Grade: %s\n", c.getName(), c.getCode(), c.getCredit(), c.getGrade()));
                totalPoints += c.getCredit() * c.getGradePoint();
                totalCreditsLocal += c.getCredit();
            }
            double gpa = totalCreditsLocal == 0 ? 0 : totalPoints / totalCreditsLocal;
            writer.write(String.format("\nTotal Credits: %.2f\nTotal Points: %.2f\nWeighted GPA: %.2f\n", totalCreditsLocal, totalPoints, gpa));
            new Alert(Alert.AlertType.INFORMATION, "Exported to GPA_Result.txt").show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error exporting file!").show();
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
