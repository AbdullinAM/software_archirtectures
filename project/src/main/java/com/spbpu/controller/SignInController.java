/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.controller;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Optional;

public class SignInController {

    private Facade facade = Main.facade;

    @FXML private Label loginLabel;
    @FXML private Label passwordLabel;
    @FXML private Label errorLabel;
    @FXML private Label registerLabel;
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
            facade.authenticate(login, password);
            Main.showMainView(login);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onCLickRegister() throws IOException {
        Main.showRegisterView();
    }
}
