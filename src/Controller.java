import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Controller {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private Mat largeImageMat;
    private Mat smallImageMat;

    @FXML
    private Button image_btn1;

    @FXML
    private Button image_btn2;

    @FXML
    private ImageView ImageView;

    @FXML
    void choose_image1(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "*.PNG"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            largeImageMat = Imgcodecs.imread(file.getPath());    //convert image to mat
            resizeImage(largeImageMat, 900, 600);  //resize image with mat

            Image image = matToImage(largeImageMat);            //convert mat to image
            ImageView.setImage(image);
        }
    }

    @FXML
    void choose_image2(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "*.PNG"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            smallImageMat = Imgcodecs.imread(file.getPath());
            resizeImage(smallImageMat, 600, 400);

            Image image = matToImage(smallImageMat);
            ImageView.setImage(image);
        }
    }

    @FXML
    void start(ActionEvent event) {
    }

    @FXML
    void restore(ActionEvent event) {
    }

    private Image matToImage(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);

        byte[] byteArray = matOfByte.toArray();

        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SwingFXUtils.toFXImage(bufImage, null);
    }

    private void resizeImage(Mat mat, int width, int height) {
        Imgproc.resize(mat, mat, new Size(width, height));
    }

}