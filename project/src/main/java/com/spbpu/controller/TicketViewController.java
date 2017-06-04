/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.controller;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import com.spbpu.facade.Role;
import com.spbpu.facade.Triple;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TicketViewController {

    private Facade facade = Main.facade;
    private String user;
    private String project;
    private Integer milestone;
    private Integer id;
    private Role role;

    @FXML private Label idLabel;
    @FXML private Label milestoneLabel;
    @FXML private Label projectLabel;
    @FXML private Label authorLabel;
    @FXML private Label dateLabel;
    @FXML private ChoiceBox statusBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button updateButton;

    @FXML private ListView<String> assigneeList;
    @FXML private Button addAssigneeButton;

    @FXML private TableView<Triple<Date, String, String>> commentTable;
    @FXML private TableColumn<Triple<Date, String, String>, String> commentDateColumn;
    @FXML private TableColumn<Triple<Date, String, String>, String> commentAuthorColumn;
    @FXML private TableColumn<Triple<Date, String, String>, String> commentCommentColumn;


    public void setup(String user_, String project_, Integer tiket_) {
        try {
            user = user_;
            project = project_;
            role = facade.getRoleForProject(user, project);
            id = tiket_;
            milestone = facade.getTicketMilestone(project, id);

            idLabel.setText(id.toString());
            milestoneLabel.setText(milestone.toString());
            projectLabel.setText(project);
            authorLabel.setText(facade.getTicketAuthor(project, id));
            authorLabel.setOnMouseClicked(mouseEvent -> {
                try {
                    Main.showUserView(authorLabel.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            dateLabel.setText(facade.getTicketCreationTime(project, id).toString());
            descriptionArea.setText(facade.getTicketTask(project, id));
            descriptionArea.setEditable(false);
            if (role != Role.MANAGER && role != Role.TEAMLEADER)
                addAssigneeButton.setDisable(true);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }

        assigneeList.setOnMouseClicked(mouseEvent -> {
            try {
                if (mouseEvent.getClickCount() == 2) Main.showUserView(assigneeList.getSelectionModel().getSelectedItem());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        setUpCommentTable();
        setUpStatusBox();

        onClickUpdateButton();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickUpdateButton() {
        updateAssigneeList();
        updateCommentTable();
    }

    @FXML
    private void onClickAddAssigneeButton() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter developer name");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        try {
            facade.addTicketAssignee(user, project, id, result.get());
            onClickUpdateButton();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }


    private void setUpStatusBox() {
        try {
            List<Object> items = new ArrayList<>();
            items.add(facade.getTicketStatus(project, id));
            items.add(new Separator());
            if (!items.contains("NEW")) items.add("NEW");
            if (!items.contains("CLOSED")) items.add("CLOSED");
            if (!items.contains("ACCEPTED")) items.add("ACCEPTED");
            if (!items.contains("IN_PROGRESS")) items.add("IN_PROGRESS");
            if (!items.contains("FINISHED")) items.add("FINISHED");
            statusBox.setItems(FXCollections.observableArrayList(items));
            statusBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }

        statusBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            String message = "";
            if (newVal.equals("NEW")) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Enter information");
                dialog.setHeaderText("Enter comment");

                Optional<String> result = dialog.showAndWait();
                if (!result.isPresent()) {
                    return;
                }
                try {
                    facade.reopenTicket(user, project, id, result.get());
                    onClickUpdateButton();
                } catch (Exception e) {
                    message = e.getMessage();
                }
            } else if (newVal.equals("CLOSED")) {
                try {
                    facade.closeTicket(user, project, id);
                } catch (Exception e) {
                    message = e.getMessage();
                }
            } else if (newVal.equals("ACCEPTED")) {
                try {
                    facade.acceptTicket(user, project, id);
                } catch (Exception e) {
                    message = e.getMessage();
                }
            } else if (newVal.equals("IN_PROGRESS")) {
                try {
                    facade.setTicketInProgress(user, project, id);
                } catch (Exception e) {
                    message = e.getMessage();
                }
            } else if (newVal.equals("FINISHED")) {
                try {
                    facade.finishTicket(user, project, id);
                } catch (Exception e) {
                    message = e.getMessage();
                }
            }
            if (!message.isEmpty()) {
                alert.setHeaderText(message);
                alert.showAndWait();
            } else {
                onClickUpdateButton();
            }
        });
    }

    private void updateAssigneeList() {
        try {
            assigneeList.setItems(FXCollections.observableArrayList(facade.getTicketAssignees(project, id)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpCommentTable() {
        commentDateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFirst().toString()));
        commentAuthorColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSecond()));
        commentCommentColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getThird()));


        // on doule-click to ticket author open view with his info
        commentAuthorColumn.setCellFactory(col -> {
            final TableCell<Triple<Date, String, String>, String> cell = new TableCell<>();
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
            return cell;
        });
    }

    private void updateCommentTable() {
        try {
            commentTable.setItems(FXCollections.observableArrayList(facade.getTicketComments(project, id)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
