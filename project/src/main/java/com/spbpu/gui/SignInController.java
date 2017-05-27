/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.gui;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.soap.Text;

public class SignInController {

    private Facade facade = Main.facade;

    @FXML private Label loginLabel;
    @FXML private Label passwordLabel;
    @FXML private Label errorLabel;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Button signInButton;

    @FXML
    private void initialize() {}

    @FXML
    private void onClickSignInButton() {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Enter login and password");
            return;
        }
        try {
            if (facade.authenticate(login, password)) {
                Main.showMainView(login);
            } else {
                errorLabel.setText("Incorrect login or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error connecting to database");
        }
    }
}
