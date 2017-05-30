package com.spbpu;

import com.spbpu.facade.Facade;
import com.spbpu.facade.FacadeImpl;
import com.spbpu.controller.*;
import com.spbpu.service.VerifyEmailService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Main extends Application {

    public static Facade facade = new FacadeImpl();


    private static Stage mainStage;
    private static Map<String, Stage> projectViews = new HashMap<>();
    private static Map<String, Stage> userViews = new HashMap<>();
    private static Map<Integer, Stage> milestoneViews = new HashMap<>();
    private static Map<Integer, Stage> ticketViews = new HashMap<>();
    private static Map<Integer, Stage> reportViews = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        mainStage.setTitle("PMS");

        showSignInView();

        mainStage.show();
    }

    public static void showSignInView() {
        try {
            String fxmlFile = "/fxml/SignIn.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = null;
            root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            Scene scene = new Scene(root, 384, 275);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showRegisterView() {
        try {
            String fxmlFile = "/fxml/RegisterView.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            Scene scene = new Scene(root, 341, 277);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMainView(String user) {
        try {
            String fxmlFile = "/fxml/MainView.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            MainViewController uvc = loader.getController();
            uvc.setup(user);
            Scene scene = new Scene(root, 600, 400);
            mainStage.setScene(scene);
            mainStage.setOnCloseRequest(windowEvent -> Platform.exit());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showUserView(String user) {
        if (userViews.containsKey(user)) {
            userViews.get(user).toFront();
            return;
        }
        try {
            Stage stage = new Stage();
            String fxmlFile = "/fxml/UserView.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            UserViewController uvc = loader.getController();
            uvc.setup(user);
            Scene scene = new Scene(root, 225, 130);
            stage.setScene(scene);
            stage.setTitle("User info");
            stage.show();
            stage.setOnCloseRequest(windowEvent -> userViews.remove(user));
            userViews.put(user, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showProjectView(String user, String project) {
        if (projectViews.containsKey(project)) {
            projectViews.get(project).toFront();
            return;
        }
        try {
            Stage stage = new Stage();
            String fxmlFile = "/fxml/ProjectView.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            ProjectViewController uvc = loader.getController();
            uvc.setup(user, project);
            Scene scene = new Scene(root, 600, 600);
            stage.setScene(scene);
            stage.setTitle("Project info");
            stage.show();
            stage.setOnCloseRequest(windowEvent -> projectViews.remove(project));
            projectViews.put(project, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMilestoneView(String user, String project, Integer milestone) {
        if (milestoneViews.containsKey(milestone)) {
            milestoneViews.get(milestone).toFront();
            return;
        }
        try {
            Stage stage = new Stage();
            String fxmlFile = "/fxml/MilestoneView.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            MilestoneViewController uvc = loader.getController();
            uvc.setup(user, project, milestone);
            Scene scene = new Scene(root, 320, 440);
            stage.setScene(scene);
            stage.setTitle("Milestone info");
            stage.show();
            stage.setOnCloseRequest(windowEvent -> milestoneViews.remove(milestone));
            milestoneViews.put(milestone, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showTicketView(String user, String project, Integer ticket) {
        if (ticketViews.containsKey(ticket)) {
            ticketViews.get(ticket).toFront();
            return;
        }
        try {
            Stage stage = new Stage();
            String fxmlFile = "/fxml/TicketView.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            TicketViewController uvc = loader.getController();
            uvc.setup(user, project, ticket);
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.setTitle("Ticket info");
            stage.show();
            stage.setOnCloseRequest(windowEvent -> ticketViews.remove(ticket));
            ticketViews.put(ticket, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showReportView(String user, String project, Integer report) {
        if (reportViews.containsKey(report)) {
            reportViews.get(report).toFront();
            return;
        }
        try {
            Stage stage = new Stage();
            String fxmlFile = "/fxml/BugReportView.fxml";
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
            BugReportViewController uvc = loader.getController();
            uvc.setup(user, project, report);
            Scene scene = new Scene(root, 600, 250);
            stage.setScene(scene);
            stage.setTitle("Report info");
            stage.show();
            stage.setOnCloseRequest(windowEvent -> reportViews.remove(report));
            reportViews.put(report, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
