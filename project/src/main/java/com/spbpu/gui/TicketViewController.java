/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.gui;

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


    public void setup(String user_, String project_, Integer tiket_) throws Exception {
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
    private void onClickUpdateButton() throws Exception {
        updateAssigneeList();
        updateCommentTable();
    }

    @FXML
    private void onClickAddAssigneeButton() throws Exception {
        if (role == Role.MANAGER || role == Role.TEAMLEADER) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter information");
            dialog.setHeaderText("Enter developer name");

            Optional<String> result = dialog.showAndWait();
            if (!result.isPresent()) return;

            if (facade.addTicketAssignee(user, project, id, result.get())) {
                onClickUpdateButton();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Can't add assignee to ticket");
                alert.showAndWait();
            }
        }
    }


    private void setUpStatusBox() throws Exception {
        List<Object> items = new ArrayList<>();
        items.add(facade.getTicketStatus(project, id));
        items.add(new Separator());
        if (!items.get(0).equals("CLOSED")) {
            switch (role) {
                case NONE:
                case TESTER:
                    break;
                case MANAGER:
                case TEAMLEADER:
                    if (!items.contains("NEW")) items.add("NEW");
                    if (!items.contains("CLOSED")) items.add("CLOSED");
                    break;
                case DEVELOPER:
                    if (!items.contains("ACCEPTED")) items.add("ACCEPTED");
                    if (!items.contains("IN_PROGRESS")) items.add("IN_PROGRESS");
                    if (!items.contains("FINISHED")) items.add("FINISHED");
            }
        }

        statusBox.setItems(FXCollections.observableArrayList(items));
        statusBox.getSelectionModel().selectFirst();

        statusBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal.equals("NEW")) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Enter information");
                dialog.setHeaderText("Enter comment");

                Optional<String> result = dialog.showAndWait();
                if (!result.isPresent()) return;
                boolean isChanged = false;
                try {
                    if (!facade.reopenTicket(user, project, id, result.get())) {
                        isChanged = true;
                        onClickUpdateButton();
                    }
                } catch (Exception e) {
                } finally {
                    if (!isChanged) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Error");
                        alert.setHeaderText("Can't change ticket status");
                        alert.showAndWait();
                    }
                }
            } else if (newVal.equals("CLOSED")) {
                try {
                    facade.closeTicket(user, project, id);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Can't change ticket status");
                    alert.showAndWait();
                }
            } else if (newVal.equals("ACCEPTED")) {
                try {
                    facade.acceptTicket(user, project, id);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Can't change ticket status");
                    alert.showAndWait();
                }
            } else if (newVal.equals("IN_PROGRESS")) {
                try {
                    facade.setTicketInProgress(user, project, id);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Can't change ticket status");
                    alert.showAndWait();
                }
            } else if (newVal.equals("FINISHED")) {
                try {
                    facade.finishTicket(user, project, id);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Can't change ticket status");
                    alert.showAndWait();
                }
            }
            try {
                onClickUpdateButton();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateAssigneeList() throws Exception {
        assigneeList.setItems(FXCollections.observableArrayList(facade.getTicketAssignees(project, id)));
    }

    private void setUpCommentTable() throws Exception {
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
                        Main.showUserView(cell.getItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell;
        });
    }

    private void updateCommentTable() throws Exception {
        commentTable.setItems(FXCollections.observableArrayList(facade.getTicketComments(project, id)));
    }

}
