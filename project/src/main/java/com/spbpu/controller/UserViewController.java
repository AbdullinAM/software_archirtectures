/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.controller;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;


public class UserViewController {

    private Facade facade = Main.facade;
    private String user;

    @FXML private Label loginLabel;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;


    public void setup(String user_) {
        user = user_;
        loginLabel.setText(user);
        try {
            nameLabel.setText(facade.getUserName(user));
            emailLabel.setText(facade.getUserEmail(user));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void initialize() {}
}
