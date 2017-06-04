/**
 * Created by kivi on 29.05.17.
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

public class BugReportViewController {

    private Facade facade = Main.facade;
    private String user;
    private String project;
    private Integer id;

    @FXML private Label idLabel;
    @FXML private Label projectLabel;
    @FXML private Label creatorLabel;
    @FXML private Label assigneeLabel;
    @FXML private Label dateLabel;
    @FXML private ChoiceBox statusBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button updateButton;

    @FXML private TableView<Triple<Date, String, String>> commentTable;
    @FXML private TableColumn<Triple<Date, String, String>, String> commentDateColumn;
    @FXML private TableColumn<Triple<Date, String, String>, String> commentAuthorColumn;
    @FXML private TableColumn<Triple<Date, String, String>, String> commentCommentColumn;


    public void setup(String user_, String project_, Integer report_) {
        try {
            user = user_;
            project = project_;
            id = report_;

            idLabel.setText(id.toString());
            projectLabel.setText(project);
            creatorLabel.setText(facade.getReportAuthor(project, id));
            creatorLabel.setOnMouseClicked(mouseEvent -> {
                try {
                    Main.showUserView(creatorLabel.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            dateLabel.setText(facade.getReportCreationTime(project, id).toString());
            descriptionArea.setText(facade.getReportDescription(project, id));
            descriptionArea.setEditable(false);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }

        setUpStatusBox();
        setUpCommentTable();

        onClickUpdateButton();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickUpdateButton() {
        setUpAssigneeLabel();
        updateCommentTable();
    }

    private void setUpAssigneeLabel() {
        try {
            String assignee = facade.getReportAssignee(project, id);
            if (assignee != null) {
                assigneeLabel.setText(assignee);
                assigneeLabel.setOnMouseClicked(mouseEvent -> {
                    try {
                        Main.showUserView(assigneeLabel.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else assigneeLabel.setText("None");
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
            items.add(facade.getReportStatus(project, id));
            items.add(new Separator());
            if (!items.contains("OPENED")) items.add("OPENED");
            if (!items.contains("CLOSED")) items.add("CLOSED");
            if (!items.contains("ACCEPTED")) items.add("ACCEPTED");
            if (!items.contains("FIXED")) items.add("FIXED");

            statusBox.setItems(FXCollections.observableArrayList(items));
            statusBox.getSelectionModel().selectFirst();

            statusBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                String message = "";
                if (newVal.equals("OPENED")) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Enter information");
                    dialog.setHeaderText("Enter comment");

                    Optional<String> result = dialog.showAndWait();
                    if (!result.isPresent()) {
                        return;
                    }
                    try {
                        facade.reopenReport(user, project, id, result.get());
                        onClickUpdateButton();
                    } catch (Exception e) {
                        message = e.getMessage();
                    }
                } else if (newVal.equals("CLOSED")) {
                    try {
                        facade.closeReport(user, project, id);
                    } catch (Exception e) {
                        message = e.getMessage();
                    }
                } else if (newVal.equals("ACCEPTED")) {
                    try {
                        facade.acceptReport(user, project, id);
                    } catch (Exception e) {
                        message = e.getMessage();
                    }
                } else if (newVal.equals("FIXED")) {
                    try {
                        facade.fixReport(user, project, id);
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
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
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
            commentTable.setItems(FXCollections.observableArrayList(facade.getReportComments(project, id)));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
        commentTable.refresh();
    }

}
