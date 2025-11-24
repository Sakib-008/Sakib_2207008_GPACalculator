package com.example.gpa_calculator;

import com.example.gpa_calculator.model.Course;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.sql.SQLException;

public class CourseController {

    public TextField courseName, courseCode, courseCredit, teacher1, teacher2;
    public ComboBox<String> grade;
    public Button calculateBtn, addCoursesBtn, editCourseBtn, deleteCourseBtn, resetBtn, exportBtn;
    public TextField requiredCreditField;
    public TableView<Course> courseTable;
    public TableColumn<Course, String> colName, colCode, colGrade;
    public TableColumn<Course, Double> colCredit;

    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private double totalCredits = 0;
    private double requiredCredits = -1;
    private Course courseBeingEdited = null;

    public void initialize() {
        grade.setItems(FXCollections.observableArrayList(
                "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"
        ));

        addCoursesBtn.setDisable(true);
        calculateBtn.setDisable(true);

        colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().name()));
        colCode.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().code()));
        colCredit.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().credit()));
        colGrade.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().grade()));


        courseTable.setItems(courseList);

        AppExecutor.getExecutor().execute(() -> {
            try {
                DatabaseHelper.initDatabase();
                var coursesFromDB = DatabaseHelper.getAllCourses();
                Platform.runLater(() -> {
                    courseList.addAll(coursesFromDB);
                    totalCredits = coursesFromDB.stream().mapToDouble(Course::credit).sum();
                    checkCalculateButton();
                });
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Database initialization failed!").show());
            }
        });
    }

    public void setRequiredCredits() {
        try {
            requiredCredits = Double.parseDouble(requiredCreditField.getText());
            if (requiredCredits <= 0) throw new NumberFormatException();
            addCoursesBtn.setDisable(false);
            checkCalculateButton();
            new Alert(Alert.AlertType.INFORMATION, "Required Credits set to: " + requiredCredits).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Invalid credit value! Enter a positive number.").show();
        }
    }

    public void addCourse() {
        if (requiredCredits <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please set required credits first!").show();
            return;
        }
        if (grade.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a grade!").show();
            return;
        }

        try {
            String name = courseName.getText();
            String code = courseCode.getText();
            double credits = Double.parseDouble(courseCredit.getText());
            String t1 = teacher1.getText();
            String t2 = teacher2.getText();
            String g = grade.getValue();

            Course course = new Course(name, code, credits, t1, t2, g);

            AppExecutor.getExecutor().execute(() -> {
                try {
                    if (courseBeingEdited != null) {
                        course.setId(courseBeingEdited.getId());
                        DatabaseHelper.updateCourse(course);
                        Platform.runLater(() -> {
                            int index = courseList.indexOf(courseBeingEdited);
                            if (index >= 0) courseList.set(index, course);
                            totalCredits = totalCredits - courseBeingEdited.credit() + course.credit();
                            courseBeingEdited = null;
                            clearInputFields();
                            checkCalculateButton();
                            new Alert(Alert.AlertType.INFORMATION, "Course updated successfully!").show();
                        });
                    } else {
                        Course inserted = DatabaseHelper.insertCourse(course);
                        Platform.runLater(() -> {
                            courseList.add(inserted);
                            totalCredits += inserted.credit();
                            clearInputFields();
                            checkCalculateButton();
                            new Alert(Alert.AlertType.INFORMATION, "Course added successfully!").show();
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to save course in database!").show());
                }
            });
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Invalid input!").show();
        }
    }

    public void editCourse() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "No course selected!").show();
            return;
        }
        courseName.setText(selected.name());
        courseCode.setText(selected.code());
        courseCredit.setText(String.valueOf(selected.credit()));
        teacher1.setText(selected.teacher1());
        teacher2.setText(selected.teacher2());
        grade.setValue(selected.grade());
        courseBeingEdited = selected;
    }

    public void deleteCourse() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "No course selected!").show();
            return;
        }
        courseList.remove(selected);
        totalCredits -= selected.credit();
        checkCalculateButton();

        AppExecutor.getExecutor().execute(() -> {
            try {
                DatabaseHelper.deleteCourse(selected.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to delete course from database!").show());
            }
        });
    }

    public void resetAll() {
        courseList.clear();
        totalCredits = 0;
        requiredCredits = -1;
        calculateBtn.setDisable(true);
        addCoursesBtn.setDisable(true);
        courseBeingEdited = null;
        clearInputFields();
        requiredCreditField.clear();

        AppExecutor.getExecutor().execute(() -> {
            try {
                DatabaseHelper.clearAllCourses();
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to clear database!").show());
            }
        });
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
                writer.write(String.format("Course: %s | Code: %s | Credit: %.2f | Grade: %s\n",
                        c.name(), c.code(), c.credit(), c.grade()));
                totalPoints += c.credit() * c.getGradePoint();
                totalCreditsLocal += c.credit();
            }
            double gpa = totalCreditsLocal == 0 ? 0 : totalPoints / totalCreditsLocal;
            writer.write(String.format("\nTotal Credits: %.2f\nTotal Points: %.2f\nWeighted GPA: %.2f\n",
                    totalCreditsLocal, totalPoints, gpa));
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

    private void clearInputFields() {
        courseName.clear();
        courseCode.clear();
        courseCredit.clear();
        teacher1.clear();
        teacher2.clear();
        grade.setValue(null);
    }

    private void checkCalculateButton() {
        calculateBtn.setDisable(totalCredits < requiredCredits);
    }
}
