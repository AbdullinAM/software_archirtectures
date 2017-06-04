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

    public void setup(String user_, String project_) {
        try {
            user = user_;
            project = project_;

            projectLabel.setText(project);
            String manager = facade.getProjectManager(project);
            if (manager.equals(user)) managerLabel.setText(manager + " (You)");
            else managerLabel.setText(manager);
            String teamLeader = facade.getProjectTeamLeader(project);
            if (teamLeader.equals(user)) teamLeaderLabel.setText(teamLeader + " (You)");
            else teamLeaderLabel.setText(teamLeader);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }

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

        if (!teamLeaderLabel.getText().isEmpty()) setTeamLeaderButton.setVisible(false);

        setUpMilestoneTale();
        setUpReportTable();

        onClickUpdateButton();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickUpdateButton() {
        try {
            String teamLeader = facade.getProjectTeamLeader(project);
            if (teamLeader.equals(user)) teamLeaderLabel.setText(teamLeader + " (You)");
            else teamLeaderLabel.setText(teamLeader);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }

        updateDeveloperList();
        updateTesterList();
        updateMilestoneTable();
        updateReportTable();
    }

    @FXML
    private void onClickSetTeamLeaderButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter teamleader login");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        try {
            facade.setProjectTeamLeader(user, project, result.get());
            onClickUpdateButton();
            setTeamLeaderButton.setVisible(false);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddDeveloperButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter developer login");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        try {
            facade.addDeveloper(user, project, result.get());
            onClickUpdateButton();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddTesterButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter tester login");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        try {
            facade.addTester(user, project, result.get());
            onClickUpdateButton();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddReportButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter report description");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        try {
            facade.createReport(user, project, result.get());
            onClickUpdateButton();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddMilestoneButton() {
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

        try {
            facade.createMilestone(user, project, startDate, endDate);
            onClickUpdateButton();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateDeveloperList() {
        ObservableList<String> items;
        try {
            items = FXCollections.observableArrayList(facade.getProjectDevelopers(project));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        developerList.setItems(items);
    }

    private void updateTesterList() {
        ObservableList<String> items;
        try {
            items = FXCollections.observableArrayList(facade.getProjectTesters(project));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        testerList.setItems(items);
    }

    private void setUpMilestoneTale() {
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
                        if (!row.isEmpty()) Main.showMilestoneView(user, project, row.getItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });
    }

    private void updateMilestoneTable() {
        try {
            milestoneTable.setItems(FXCollections.observableArrayList(facade.getProjectMilestones(project)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        milestoneTable.refresh();
    }
    private void setUpReportTable() {
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
                        if (cell != null && !cell.getItem().isEmpty()) Main.showReportView(user, project, item);
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
                        if (cell != null && !cell.getItem().isEmpty()) Main.showUserView(cell.getItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell ;
        });
    }

    private void updateReportTable() {
        try {
            reportTable.setItems(FXCollections.observableArrayList(facade.getProjectReports(project)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        reportTable.refresh();
    }
}
