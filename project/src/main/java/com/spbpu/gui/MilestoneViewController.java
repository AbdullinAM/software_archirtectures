/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.gui;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import com.spbpu.facade.Pair;
import com.spbpu.facade.Role;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.text.DateFormat;
import java.util.Date;

public class MilestoneViewController {

    private Facade facade = Main.facade;
    private String user;
    private String project;
    private Role role;
    private Integer id;

    @FXML private Label idLabel;
    @FXML private Label projectLabel;
    @FXML private Label statusLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label activeDateNameLabel;
    @FXML private Label activeDateLabel;
    @FXML private Label closingDateNameLabel;
    @FXML private Label closingDateLabel;

    @FXML private Button activateButton;
    @FXML private Button closeButton;
    @FXML private Button updateButton;

    @FXML private TableView<Integer> ticketTable;
    @FXML private TableColumn<Integer, String> ticketIdColumn;
    @FXML private TableColumn<Integer, String> ticketStatusColumn;
    @FXML private TableColumn<Integer, String> ticketDescriptionColumn;


    public void setup(String user_, String project_, Integer milestone) throws Exception {
        user = user_;
        project = project_;
        id = milestone;
        role = facade.getRoleForProject(user, project);

        idLabel.setText(id.toString());
        projectLabel.setText(project);
        statusLabel.setText(facade.getMilestoneStatus(project, id));
        startDateLabel.setText(facade.getMilestoneStartDate(project,id).toString());
        endDateLabel.setText(facade.getMilestoneEndDate(project, id).toString());

        setUpTicketTable();

        onClickUpdateButton();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickUpdateButton() throws Exception {
        statusLabel.setText(facade.getMilestoneStatus(project, id));
        if (role != Role.MANAGER) {
            activateButton.setVisible(false);
            closeButton.setVisible(false);
        } else {
            String status = statusLabel.getText();
            if (status.equals("OPENED")) {
                closeButton.setDisable(true);
                activateButton.setDisable(false);
            } else if (status.equals("ACTIVE")) {
                closeButton.setDisable(false);
                activateButton.setDisable(true);
            } else {
                closeButton.setDisable(true);
                activateButton.setDisable(true);
            }
        }
        Date activeDate = facade.getMilestoneActiveDate(project, id);
        if (activeDate != null) {
            activeDateLabel.setText(activeDate.toString());
            activeDateLabel.setVisible(true);
            activeDateNameLabel.setVisible(true);
        } else {
            activeDateLabel.setVisible(false);
            activeDateNameLabel.setVisible(false);
        }
        Date closingDate = facade.getMilestoneClosingDate(project, id);
        if (closingDate != null) {
            closingDateLabel.setText(activeDate.toString());
            closingDateLabel.setVisible(true);
            closingDateNameLabel.setVisible(true);
        } else {
            closingDateLabel.setVisible(false);
            closingDateNameLabel.setVisible(false);
        }

        updateTicketTable();
    }

    @FXML
    private void onClickActivateButton() throws Exception {
        if (facade.activateMilestone(user, project, id)) {
            onClickUpdateButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot change status of milestone");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickCloseButton() throws Exception {
        if (facade.closeMilestone(user, project, id)) {
            onClickUpdateButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot change status of milestone");
            alert.showAndWait();
        }
    }


    private void setUpTicketTable() throws Exception {
        ticketIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().toString()));
        ticketStatusColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getTicketStatus(project, cell.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        ticketDescriptionColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getTicketTask(project, cell.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });

        ticketIdColumn.setCellFactory(col -> {
            final TableCell<Integer, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        Integer item = (Integer) cell.getTableRow().getItem();
                        Main.showTicketView(user, project, item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell;
        });
    }

    private void updateTicketTable() throws Exception {
        ObservableList<Integer> tickets = FXCollections.observableArrayList(facade.getMilestoneTickets(project, id));
        ticketTable.setItems(tickets);
    }
}
