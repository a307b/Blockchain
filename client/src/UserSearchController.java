import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class UserSearchController implements Initializable {

    @FXML
    private JFXTextField usernameField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //initialize
    }

    public void CPRButtonAction(ActionEvent event) {
        System.out.println("Dette er en test");
    }
}
