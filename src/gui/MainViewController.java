package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable{

    @FXML
    private MenuItem menuItemSeller;

    @FXML
    private MenuItem menuItemDepartament;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private void onMenuItemSellerAction(){
        System.out.println("onMenuItemSellerAction");
    }

    @FXML
    private void onMenuItemDepartmentAction(){
        System.out.println("onMenuItemDepartmentAction");
    }

    @FXML
    private void onMenuItemAboutAction(){
        loadView("/gui/About.fxml");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    private synchronized void loadView(String absoluteName){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVbox = loader.load();

            Scene mainScene = Main.getMainScene();
            VBox mainVbox = (VBox)((ScrollPane)mainScene.getRoot()).getContent();

            MenuBar mainMenuBar = (MenuBar) mainVbox.getChildren().get(0);
            mainVbox.getChildren().clear();
            mainVbox.getChildren().add(mainMenuBar);
            mainVbox.getChildren().addAll(newVbox.getChildren());
        }catch(IOException e){
            Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
        }   
    }
    
}
