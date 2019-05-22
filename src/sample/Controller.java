package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {

    @FXML
    private ImageView ImageView;

    @FXML
    void choose(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "*.PNG"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            ImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    void start(ActionEvent event) {

    }

}