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
    private Role role;

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


    public void setup(String user_, String project_, Integer report_) throws Exception {
        user = user_;
        project = project_;
        role = facade.getRoleForProject(user, project);
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

        setUpStatusBox();
        setUpCommentTable();

        onClickUpdateButton();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickUpdateButton() throws Exception {
        setUpAssigneeLabel();
        updateCommentTable();
    }

    private void setUpAssigneeLabel() throws Exception {
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
    }

    private void setUpStatusBox() throws Exception {
        List<Object> items = new ArrayList<>();
        items.add(facade.getReportStatus(project, id));
        items.add(new Separator());
        if (!items.get(0).equals("CLOSED")) {
            switch (role) {
                case NONE:
                case MANAGER:
                    break;
                case TESTER:
                    if (!items.contains("OPENED")) items.add("OPENED");
                    if (!items.contains("CLOSED")) items.add("CLOSED");
                    break;
                case DEVELOPER:
                case TEAMLEADER:
                    if (!items.contains("ACCEPTED")) items.add("ACCEPTED");
                    if (!items.contains("FIXED")) items.add("FIXED");
            }
        }

        statusBox.setItems(FXCollections.observableArrayList(items));
        statusBox.getSelectionModel().selectFirst();

        statusBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal.equals("OPENED")) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Enter information");
                dialog.setHeaderText("Enter comment");

                Optional<String> result = dialog.showAndWait();
                if (!result.isPresent()) return;
                boolean isChanged = false;
                try {
                    if (!facade.reopenReport(user, project, id, result.get())) {
                        isChanged = true;
                        onClickUpdateButton();
                    }
                } catch (Exception e) {
                } finally {
                    if (!isChanged) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Error");
                        alert.setHeaderText("Can't change report status");
                        alert.showAndWait();
                    }
                }
            } else if (newVal.equals("CLOSED")) {
                try {
                    facade.closeReport(user, project, id);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Can't change report status");
                    alert.showAndWait();
                }
            } else if (newVal.equals("ACCEPTED")) {
                try {
                    facade.acceptReport(user, project, id);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Can't change report status");
                    alert.showAndWait();
                }
            } else if (newVal.equals("FIXED")) {
                try {
                    facade.fixReport(user, project, id);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Can't change report status");
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
        commentTable.setItems(FXCollections.observableArrayList(facade.getReportComments(project, id)));
    }

}
