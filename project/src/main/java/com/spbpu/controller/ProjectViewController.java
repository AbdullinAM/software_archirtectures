package com.spbpu.controller;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import com.spbpu.facade.Role;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.text.*;
import java.util.Date;
import java.util.Optional;


/**
 * Created by kivi on 27.05.17.
 */
public class ProjectViewController {

    private Facade facade = Main.facade;
    private String user;
    private String project;
    private Role role;

    @FXML private Label projectLabel;
    @FXML private Label managerLabel;
    @FXML private Label teamLeaderLabel;

    @FXML private Button updateButton;
    @FXML private Button setTeamLeaderButton;

    @FXML private ListView<String> developerList;
    @FXML private Button addDeveloperButton;

    @FXML private ListView<String> testerList;
    @FXML private Button addTesterButton;

    @FXML private Button addMilestoneButton;
    @FXML private TableView<Integer> milestoneTable;
    @FXML private TableColumn<Integer, String> milestoneIdColumn;
    @FXML private TableColumn<Integer, String> milestoneStartDateColumn;
    @FXML private TableColumn<Integer, String> milestoneEndDateColumn;
    @FXML private TableColumn<Integer, String> milestoneStatusColumn;

    @FXML private Button addReportButton;
    @FXML private TableView<Integer> reportTable;
    @FXML private TableColumn<Integer, String> reportIdColumn;
    @FXML private TableColumn<Integer, String> reportStatusColumn;
    @FXML private TableColumn<Integer, String> reportDescriptionColumn;
    @FXML private TableColumn<Integer, String> reportAuthorColumn;

    public void setup(String user_, String project_) throws Exception {
        user = user_;
        project = project_;
        role = facade.getRoleForProject(user, project);

        projectLabel.setText(project);
        String manager = facade.getProjectManager(project);
        if (manager.equals(user)) managerLabel.setText(manager + " (You)");
        else managerLabel.setText(manager);
        String teamLeader = facade.getProjectTeamLeader(project);
        if (teamLeader.equals(user)) teamLeaderLabel.setText(teamLeader + " (You)");
        else teamLeaderLabel.setText(teamLeader);

        managerLabel.setOnMouseClicked(mouseEvent -> {
            try {
                Main.showUserView(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        teamLeaderLabel.setOnMouseClicked(mouseEvent -> {
            try {
                Main.showUserView(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        developerList.setOnMouseClicked(mouseEvent -> {
            try {
                if (mouseEvent.getClickCount() == 2) Main.showUserView(developerList.getSelectionModel().getSelectedItem());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        testerList.setOnMouseClicked(mouseEvent -> {
            try {
                if (mouseEvent.getClickCount() == 2) Main.showUserView(testerList.getSelectionModel().getSelectedItem());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (role != Role.MANAGER) {
            setTeamLeaderButton.setVisible(false);
            addDeveloperButton.setVisible(false);
            addTesterButton.setVisible(false);
            addMilestoneButton.setVisible(false);
        }
        if (role == Role.MANAGER || role == Role.NONE) {
            addReportButton.setVisible(false);
        }
        if (!teamLeaderLabel.getText().isEmpty()) setTeamLeaderButton.setVisible(false);

        setUpMilestoneTale();
        setUpReportTable();

        onClickUpdateButton();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickUpdateButton() throws Exception {
        String teamLeader = facade.getProjectTeamLeader(project);
        if (teamLeader.equals(user)) teamLeaderLabel.setText(teamLeader + " (You)");
        else teamLeaderLabel.setText(teamLeader);

        updateDeveloperList();
        updateTesterList();
        updateMilestoneTable();
        updateReportTable();
    }

    @FXML
    private void onClickSetTeamLeaderButton() throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter teamleader login");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        if (facade.setProjectTeamLeader(user, project, result.get())) {
            onClickUpdateButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to set team leader to project");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddDeveloperButton() throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter developer login");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        if (facade.addDeveloper(user, project, result.get())) {
            onClickUpdateButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to add developer to project");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddTesterButton() throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter tester login");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        if (facade.addTester(user, project, result.get())) {
            onClickUpdateButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to add tester to project");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddReportButton() throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter report description");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        if (facade.createReport(user, project, result.get()) != null) {
            onClickUpdateButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to create report");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddMilestoneButton() throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter milestone start date");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date startDate, endDate;
        try {
            startDate = df.parse(result.get());
        } catch (ParseException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to parse date, try format DD-MM-YYYY");
            alert.showAndWait();
            return;
        }

        dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter milestone start date");

        result = dialog.showAndWait();
        if (!result.isPresent()) return;
        try {
            endDate = df.parse(result.get());
        } catch (ParseException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to parse date, try format DD-MM-YYYY");
            alert.showAndWait();
            return;
        }

        if (facade.createMilestone(user, project, startDate, endDate) != null) {
            onClickUpdateButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to create milestone");
            alert.showAndWait();
        }
    }

    private void updateDeveloperList() throws Exception {
        ObservableList<String> items = FXCollections.observableArrayList(facade.getProjectDevelopers(project));
        developerList.setItems(items);
    }

    private void updateTesterList() throws Exception {
        ObservableList<String> items = FXCollections.observableArrayList(facade.getProjectTesters(project));
        testerList.setItems(items);
    }

    private void setUpMilestoneTale() throws Exception {
        milestoneIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().toString()));
        milestoneStartDateColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getMilestoneStartDate(project, cell.getValue()).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        milestoneEndDateColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getMilestoneEndDate(project, cell.getValue()).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        milestoneStatusColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getMilestoneStatus(project, cell.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });

        milestoneTable.setRowFactory( tv -> {
            TableRow<Integer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    try {
                        Main.showMilestoneView(user, project, row.getItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });
    }

    private void updateMilestoneTable() throws Exception {
        ObservableList<Integer> items = FXCollections.observableArrayList(facade.getProjectMilestones(project));
        milestoneTable.setItems(items);
    }
    private void setUpReportTable() throws Exception {
        reportIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().toString()));
        reportStatusColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getReportStatus(project, cell.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        reportDescriptionColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getReportDescription(project, cell.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        reportAuthorColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getReportAuthor(project, cell.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });

        reportIdColumn.setCellFactory(col -> {
            final TableCell<Integer, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        Integer item = (Integer) cell.getTableRow().getItem();
                        Main.showReportView(user, project, item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell ;
        });

        // on doule-click to report author open view with his info
        reportAuthorColumn.setCellFactory(col -> {
            final TableCell<Integer, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        Main.showUserView(cell.getItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell ;
        });
    }

    private void updateReportTable() throws Exception {
        ObservableList<Integer> tickets = FXCollections.observableArrayList(facade.getProjectReports(project));
        reportTable.setItems(tickets);
    }
}
