package com.spbpu;

import com.spbpu.facade.Facade;
import com.spbpu.facade.FacadeImpl;
import com.spbpu.gui.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {

    public static Facade facade = new FacadeImpl();


    private static Stage mainStage;


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

    public static void showSignInView() throws Exception {
        String fxmlFile = "/fxml/SignIn.fxml";
        FXMLLoader loader = new FXMLLoader();
        AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
        Scene scene = new Scene(root, 384, 275);
        mainStage.setScene(scene);
    }

    public static void showMainView(String user) throws Exception {
        String fxmlFile = "/fxml/MainView.fxml";
        FXMLLoader loader = new FXMLLoader();
        AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
        MainViewController uvc = loader.getController();
        uvc.setup(user);
        Scene scene = new Scene(root, 600, 400);
        mainStage.setScene(scene);
    }

    public static void showUserView(String user) throws Exception {
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
    }

    public static void showProjectView(String user, String project) throws Exception {
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
    }

    public static void showMilestoneView(String user, String project, Integer milestone) throws Exception {
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
    }

    public static void showTicketView(String user, String project, Integer tiket) throws Exception {
        Stage stage = new Stage();
        String fxmlFile = "/fxml/TicketView.fxml";
        FXMLLoader loader = new FXMLLoader();
        AnchorPane root = (AnchorPane) loader.load(Main.class.getClass().getResourceAsStream(fxmlFile));
        TicketViewController uvc = loader.getController();
        uvc.setup(user, project, tiket);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Ticket info");
        stage.show();
    }

    public static void showReportView(String user, String project, Integer report) throws Exception {
        System.out.println("report");
    }

}
