/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.gui;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import com.spbpu.facade.Pair;
import com.spbpu.project.Project;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;

import javax.swing.text.*;
import java.util.Optional;

public class MainViewController {

    private String user;
    private Facade facade = Main.facade;

    @FXML private Label userLabel;
    @FXML private Button updateButton;
    @FXML private Button signOutButton;
    @FXML private Button addProjectButton;
    @FXML private TabPane tabs;

    @FXML private Tab projectTab;
    @FXML private TableView<String> projectTable;
    @FXML private TableColumn<String, String> projectNameColumn;
    @FXML private TableColumn<String, String> projectRoleColumn;

    @FXML private Tab messageTab;
    @FXML private TableView<String> messageTable;
    @FXML private TableColumn<String, String> messageColumn;

    @FXML private Tab ticketTab;
    @FXML private TableView<Pair<String, Integer>> ticketTable;
    @FXML private TableColumn<Pair<String, Integer>, String> ticketIdColumn;
    @FXML private TableColumn<Pair<String, Integer>, String> ticketProjectColumn;
    @FXML private TableColumn<Pair<String, Integer>, String> ticketStatusColumn;
    @FXML private TableColumn<Pair<String, Integer>, String> ticketDescriptionColumn;
    @FXML private TableColumn<Pair<String, Integer>, String> ticketAuthorColumn;

    @FXML private Tab reportTab;
    @FXML private TableView<Pair<String, Integer>> reportTable;
    @FXML private TableColumn<Pair<String, Integer>, String> reportIdColumn;
    @FXML private TableColumn<Pair<String, Integer>, String> reportProjectColumn;
    @FXML private TableColumn<Pair<String, Integer>, String> reportStatusColumn;
    @FXML private TableColumn<Pair<String, Integer>, String> reportDescriptionColumn;
    @FXML private TableColumn<Pair<String, Integer>, String> reportAuthorColumn;

    public void setup(String user_) throws Exception {
        user = user_;
        userLabel.setText(user);
        userLabel.setOnMouseClicked(mouseEvent -> {
            try {
                Main.showUserView(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        setUpProjectTable();
        setUpMessageTable();
        setUpTicketTable();
        setUpReportTable();
        onClickUpdateButton();
    }

    @FXML
    private void initialize() {}

    @FXML
    private void onClickSignOutButton() throws Exception {
        Main.showSignInView();
    }

    @FXML
    private void onClickUpdateButton() throws Exception {
        updateProjectTable();
        updateMessageTable();
        updateTicketTable();
        updateReportTable();
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
        projectNameColumn.setCellFactory(col -> {
            final TableCell<String, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        Main.showProjectView(user, cell.getItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell ;
        });
    }

    private void updateProjectTable() throws Exception {
        ObservableList<String> projects = FXCollections.observableArrayList(facade.getAllProjects(user));
        projectTable.setItems(projects);
    }

    private void setUpMessageTable() {
        messageColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()));
    }

    private void updateMessageTable() throws Exception {
        ObservableList<String> messages = FXCollections.observableArrayList(facade.getMessages(user));
        messageTable.setItems(messages);
    }

    private void setUpTicketTable() throws Exception {
        ticketIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSecond().toString()));
        ticketProjectColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFirst()));
        ticketStatusColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getTicketStatus(cell.getValue().getFirst(), cell.getValue().getSecond()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        ticketDescriptionColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getTicketTask(cell.getValue().getFirst(), cell.getValue().getSecond()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        ticketAuthorColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getTicketAuthor(cell.getValue().getFirst(), cell.getValue().getSecond()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });

        ticketProjectColumn.setCellFactory(col -> {
            final TableCell<Pair<String, Integer>, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        Main.showProjectView(user, cell.getItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell ;
        });

        // on doule-click to ticket author open view with his info
        ticketAuthorColumn.setCellFactory(col -> {
            final TableCell<Pair<String, Integer>, String> cell = new TableCell<>();
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

    private void updateTicketTable() throws Exception {
        ObservableList<Pair<String, Integer>> tickets = FXCollections.observableArrayList(facade.getAssignedTickets(user));
        ticketTable.setItems(tickets);
    }

    private void setUpReportTable() throws Exception {
        reportIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSecond().toString()));
        reportProjectColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFirst()));
        reportStatusColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getReportStatus(cell.getValue().getFirst(), cell.getValue().getSecond()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        reportDescriptionColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getReportDescription(cell.getValue().getFirst(), cell.getValue().getSecond()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });
        reportAuthorColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(facade.getReportAuthor(cell.getValue().getFirst(), cell.getValue().getSecond()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });


        reportProjectColumn.setCellFactory(col -> {
            final TableCell<Pair<String, Integer>, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        Main.showProjectView(user, cell.getItem());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return cell ;
        });

        // on doule-click to report author open view with his info
        reportAuthorColumn.setCellFactory(col -> {
            final TableCell<Pair<String, Integer>, String> cell = new TableCell<>();
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
        ObservableList<Pair<String, Integer>> tickets = FXCollections.observableArrayList(facade.getAssignedReports(user));
        reportTable.setItems(tickets);
    }
}


