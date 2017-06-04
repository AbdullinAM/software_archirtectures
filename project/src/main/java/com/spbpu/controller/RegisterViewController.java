/**
 * Created by kivi on 29.05.17.
 */

package com.spbpu.controller;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterViewController {

    private Facade facade = Main.facade;

    @FXML private TextField loginField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField password1Field;
    @FXML private PasswordField password2Field;

    @FXML private Button registerButton;
    @FXML private Button cancelButton;


    @FXML
    private void onClickRegisterButton() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");

        String login = loginField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String pass1 = password1Field.getText();
        String pass2 = password2Field.getText();
        if (login.isEmpty()) {
            alert.setHeaderText("Enter login");
            alert.showAndWait();
            return;
        } else if (name.isEmpty()) {
            alert.setHeaderText("Enter name");
            alert.showAndWait();
            return;
        } else if (email.isEmpty()) {
            alert.setHeaderText("Enter email");
            alert.showAndWait();
            return;
        } else if (pass1.isEmpty()) {
            alert.setHeaderText("Enter password");
            alert.showAndWait();
            return;
        } else if (pass2.isEmpty()) {
            alert.setHeaderText("Repeat password");
            alert.showAndWait();
            return;
        } else if (!pass1.equals(pass2)) {
            alert.setHeaderText("Passwords are different");
            alert.showAndWait();
            return;
        }

        try {
            facade.addUser(login, name, email, pass1);
        } catch (Exception e) {
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
            return;
        }
        Main.showSignInView();
    }

    @FXML
    private void onClickCancelButton() {
        Main.showSignInView();
    }
}
