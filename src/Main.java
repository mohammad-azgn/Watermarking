import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    static Alert Alert_Box(String alertType, String title, String header, String content) {

        Alert alert = new Alert(Alert.AlertType.valueOf(alertType));
        if (!title.equals("")) {
            alert.setTitle(title);
        }
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("WaterMarking");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            closeProgram(primaryStage);
        });
        primaryStage.show();
    }

    private void closeProgram(Stage primaryStage) {
        Alert alert = Alert_Box("CONFIRMATION", "Exit Program", "", "Are you sure to exit WaterMark?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                primaryStage.close();
            }
        });
    }
}