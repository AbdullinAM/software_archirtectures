/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.gui;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import com.spbpu.project.Project;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.text.*;
import java.util.Optional;

public class UserViewController {

    private String user;
    private Facade facade = Main.facade;

    @FXML private Label userLabel;
    @FXML private Button updateButton;
    @FXML private Button signOutButton;
    @FXML private Button addProjectButton;
    @FXML private TabPane tabs;
    @FXML private Tab projectTab;
    @FXML private Tab messageTab;
    @FXML private TableView<String> projectTable;
    @FXML private TableColumn<String, String> projectNameColumn;
    @FXML private TableColumn<String, String> projectRoleColumn;
    @FXML private TableView<String> messageTable;
    @FXML private TableColumn<String, String> messageColumn;

    public void setup(String user_) throws Exception {
        user = user_;
        userLabel.setText(user);
        setUpProjectTable();
        setUpMessageTable();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickSignOutButton() throws Exception {
        Main.showSignInView();
    }

    @FXML
    private void onClickUpdateButton() throws Exception {
        setUpProjectTable();
        setUpMessageTable();
    }

    @FXML
    private void onClickAddProjectButton() throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter information");
        dialog.setHeaderText("Enter new project name");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        if (facade.createProject(user, result.get())) {
            onClickUpdateButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Project with name \"" + result.get() + "\" already exists");
            alert.showAndWait();
        }
    }


    private void setUpProjectTable() {
        projectNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()));
        projectRoleColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getRoleForProject(user, cell.getValue()).name());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        ObservableList<String> projects = FXCollections.observableArrayList();
        try {
            facade.getAllProjects(user).stream().forEach(project -> projects.add(project));
        } catch (Exception e) {
            e.printStackTrace();
        }
        projectTable.setItems(projects);
    }

    private void setUpMessageTable() throws Exception {
        messageColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()));
        ObservableList<String> messages = FXCollections.observableArrayList();
        facade.getMessages(user).stream().forEach(message -> messages.add(message));
        messageTable.setItems(messages);
    }
}


