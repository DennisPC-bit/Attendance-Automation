package GUI.CONTROLLER;

import BE.GUIHelper;
import BE.Student;
import BE.INTERFACE.ISessionManager;
import BE.SessionManager;
import GUI.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class AttendanceOverviewController implements Initializable {

    /**
     * The width of the student picture.
     */
    public static final int WIDTH = 150;

    /**
     * The height of the student picture.
     */
    public static final int HEIGHT = 250;

    /**
     * The font to use for the student name.
     */
    public static final Font FONT = new Font("System Bold", 24);

    /**
     * The default style for each student BorderPane.
     */
    public static final String DEFAULT_STYLE = "-fx-background-color: lightgrey; -fx-background-radius: 10px";

    /**
     * The select style for when clicking on a student BorderPane.
     */
    public static final String SELECTED_STYLE = "-fx-background-radius: 15;-fx-background-color: lightblue;-fx-border-style: solid;-fx-border-color: grey;-fx-border-radius: 15;";

    /**
     * The ISessionManager for handling session data.
     */
    protected ISessionManager sessionManager;

    @FXML
    public FlowPane studentListFlowPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSessionManager();
    }

    /**
     * Initialize the ISessionManager.
     */
    private void initializeSessionManager() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
            sessionManager.setMainController(Main.getInstance());
        } else sessionManager = SessionManager.getInstance();

        if (!sessionManager.hasStudents())
            sessionManager.setStudentList(createStudents(3));

        selectStudent();
        System.out.println(String.format("Students count: %d", sessionManager.getStudentList().size()));
    }

    /**
     * Create students mock data.
     *
     * @param amount The amount of students to create.
     * @return The created students.
     */
    public List<Student> createStudents(int amount) {
        var studentList = new ArrayList<Student>();

        for (int i = 0; i < amount; i++) {
            var firstName = String.format("%s %d", "Test First Name", i);
            var lastName = String.format("%s %d", "Test Last Name", i);
            var student = new Student();

            student.setId(i);

            switch (i) {
                case 0:
                    student.setFirstName("Shawn");
                    student.setLastName("Mendes");
                    student.setPicture("Data/Pictures/shawn mendes.png");
                    break;
                case 1:
                    student.setFirstName("Justin");
                    student.setLastName("Bieber");
                    student.setPicture("Data/Pictures/justin bieber.png");
                    break;
                case 2:
                    student.setFirstName("Adam");
                    student.setLastName("Lavine");
                    student.setPicture("Data/Pictures/adam lavine.png");
                    break;
            }

            // Add attendance mock data.

            // Randomize attendance data.
            for (int a = 0; a < 5; a++) {
                Random random = new Random();
                student.attend(LocalDateTime.now().minusDays(random.nextInt(7)));
            }


            student.setStudentPane(addToStudentListFlowPane(student));
            studentList.add(student);
        }

        return studentList;
    }

    /**
     * Add a Student to the Student FlowPane.
     *
     * @param student The student to add.
     * @return The created BorderPane with the student in it.
     */
    private BorderPane addToStudentListFlowPane(Student student) {
        if (student != null) {
            BorderPane pane = student.getStudentPane();
            if (student.getStudentPane() == null)
                pane = GUIHelper.createStudentBorderPane(student, FONT, WIDTH, HEIGHT, DEFAULT_STYLE);

            studentListFlowPane.getChildren().add(pane);
            return pane;
        }
        return null;
    }

    /**
     * Initialize the event handler for when selecting a student on the student overview.
     * Must only be called once! Put it in the initialize method.
     */
    private void selectStudent() {
        studentListFlowPane.setOnMouseClicked(e -> {
            studentListFlowPane.getChildren().forEach(studentNode -> {
                        var selectedNode = e.getPickResult().getIntersectedNode();

                        if (selectedNode != null && studentNode.getAccessibleText().equals(selectedNode.getAccessibleText())
                                ^ studentNode.getAccessibleText().equals(selectedNode.getParent().getAccessibleText())) {
                            studentNode.setStyle(SELECTED_STYLE);

                            // On mouse left click.
                            if (e.getButton() == MouseButton.PRIMARY) {
                                try {
                                    // Assign the configuration's selected student to the selected one though the node.
                                    sessionManager.getStudentList().forEach((student) -> {

                                        // When the student id matches the accessible text (id), assign.
                                        if (Long.toString(student.getId()).equals(selectedNode.getAccessibleText())) {
                                            // Make the student attend on click.
                                            student.attend();
                                            sessionManager.setSelectedStudent(student);
                                            //System.out.println(String.format("Assigned selected student: %s", student.getId()));
                                        }
                                    });

                                    if (sessionManager.getSelectedStudent() != null) {
                                        System.out.println(String.format("Selected student id: %s", sessionManager.getSelectedStudent().getId()));
                                        var dashboardController = (StudentDashboardController) Main.getInstance().changeStage("FXML/StudentDashboard.fxml", "Student Dashboard");
                                        dashboardController.setSessionManager(sessionManager);
                                        dashboardController.updateDashboard();

                                    }

                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        } else studentNode.setStyle(DEFAULT_STYLE);
                    }
            );
        });
    }
}
