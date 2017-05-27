/**
 * Created by kivi on 27.05.17.
 */

package com.spbpu.gui;

import com.spbpu.Main;
import com.spbpu.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class UserViewController {

    private Facade facade = Main.facade;
    private String user;

    @FXML private Label loginLabel;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;


    public void setup(String user_) throws Exception {
        user = user_;
        loginLabel.setText(user);
        nameLabel.setText(facade.getUserName(user));
        emailLabel.setText(facade.getUserEmail(user));
    }

    @FXML
    private void initialize() {}
}