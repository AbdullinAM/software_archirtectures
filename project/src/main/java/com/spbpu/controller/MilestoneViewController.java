/**
 * Created by kivi on 27.05.17.
 */

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

import java.util.Date;
import java.util.Optional;

public class MilestoneViewController {

    private Facade facade = Main.facade;
    private String user;
    private String project;
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
    @FXML private Button addTicketButton;

    @FXML private TableView<Integer> ticketTable;
    @FXML private TableColumn<Integer, String> ticketIdColumn;
    @FXML private TableColumn<Integer, String> ticketStatusColumn;
    @FXML private TableColumn<Integer, String> ticketDescriptionColumn;


    public void setup(String user_, String project_, Integer milestone) {
        try {
            user = user_;
            project = project_;
            id = milestone;

            idLabel.setText(id.toString());
            projectLabel.setText(project);
            statusLabel.setText(facade.getMilestoneStatus(project, id));
            startDateLabel.setText(facade.getMilestoneStartDate(project,id).toString());
            endDateLabel.setText(facade.getMilestoneEndDate(project, id).toString());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }

        setUpTicketTable();

        onClickUpdateButton();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickUpdateButton() {
        try {
        statusLabel.setText(facade.getMilestoneStatus(project, id));
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
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
        updateTicketTable();
    }

    @FXML
    private void onClickActivateButton() {
        try {
            facade.activateMilestone(user, project, id);
            onClickUpdateButton();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickCloseButton() {
        try {
            facade.closeMilestone(user, project, id);
            onClickUpdateButton();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickAddTicketButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter new ticket description");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        try {
            facade.createTicket(user, project, id, result.get());
            onClickUpdateButton();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }


    private void setUpTicketTable() {
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
                        if (cell != null && !cell.getItem().isEmpty()) Main.showTicketView(user, project, item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell;
        });

    }

    private void updateTicketTable() {
        try {
            ticketTable.setItems(FXCollections.observableArrayList(facade.getMilestoneTickets(project, id)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ticketTable.refresh();
    }
}
