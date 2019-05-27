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
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
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
    private ImageView imageView;

    @FXML
    void choose_image1(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "*.png"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            largeImageMat = Imgcodecs.imread(file.getPath());    //convert image to mat
            resizeImage(largeImageMat, 1000, 1000);  //resize image with mat

            Image image = matToImage(largeImageMat);            //convert mat to image
            imageView.setImage(image);
        }
    }

    @FXML
    void choose_image2(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "*.png"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            smallImageMat = Imgcodecs.imread(file.getPath());
            resizeImage(smallImageMat, 350, 350);

            Image image = matToImage(smallImageMat);
            imageView.setImage(image);
        }
    }

    @FXML
    void start(ActionEvent event) {

        int index = 0;
        for (int i = 0; i < smallImageMat.size().width; i++) {
            for (int j = 0; j < smallImageMat.size().height; j++) {

                int[] largePhotoIndex = getLargePhotoIndex(index++);
                int row = largePhotoIndex[0];
                int column = largePhotoIndex[1];

                double[] smallPhotoIndex = smallImageMat.get(i, j);

                hintSecretPhoto(row, column, smallPhotoIndex);
                // new row and column of largePhoto with smallPhotoIndex
            }
        }

        System.out.println("Finished");

        Image image = matToImage(largeImageMat);
        imageView.setImage(image);
    }

    private void hintSecretPhoto(int row, int column, double[] smallPhotoIndex) {
        String[] binaryColor = binaryOfColor(smallPhotoIndex);
        String rSmall = binaryColor[0];
        String gSmall = binaryColor[0];
        String bSmall = binaryColor[0];

        byte newR, newG, newB;

        for (int i = 0; i < 8; i++) {
            double[] targetPixel = largeImageMat.get(row, column);
            byte r = (byte) targetPixel[0];
            byte g = (byte) targetPixel[1];
            byte b = (byte) targetPixel[2];

            if (rSmall.charAt(7 - i) == '0') {
                newR = resetLSB(r);
            } else
                newR = setLSB(r);

            if (gSmall.charAt(7 - i) == '0') {
                newG = resetLSB(g);
            } else
                newG = setLSB(g);

            if (bSmall.charAt(7 - i) == '0') {
                newB = resetLSB(b);
            } else newB = setLSB(b);

            largeImageMat.put(row, column, new byte[]{newR, newG, newB});

            System.out.println("row  " + row);
            System.out.println("column  " + column);

            column++;
            if (column > 999) {
                row++;
                column = 0;
            }
            if (row == 14 && column == 55) {
                System.out.println("yes");
            }
        }
    }

    private byte setLSB(byte b) {
        if (b % 2 == 0)
            return (byte) (b + 1);
        return b;
    }

    private byte resetLSB(byte b) {
        if (b % 2 != 0)
            return (byte) (b - 1);
        return b;
    }

    private String[] binaryOfColor(double[] pixel) {
        String[] binary = new String[3];
        for (int i = 0; i < 3; i++) {
            binary[i] = appendZero(Long.toBinaryString((long) pixel[i]));
        }
        return binary;
    }

    private String appendZero(String str) {
        if (str.length() < 8) {
            StringBuilder sBuilder = new StringBuilder(str);
            for (int i = 0; i < 8 - str.length(); i++) {
                sBuilder.insert(0, "0");
            }
            str = sBuilder.toString();
        }
        return str;
    }

    private int[] getLargePhotoIndex(int index) {
        int newIndex = index * 8;
        return new int[]{newIndex / 1000, newIndex % 1000};
    }


    @FXML
    void save(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "*.png"));

        File file = fileChooser.showSaveDialog(null);

        BufferedImage bufferedImage = matToBufferImage(largeImageMat);

        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private BufferedImage matToBufferImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return image;
    }

    private void resizeImage(Mat mat, int width, int height) {
        Imgproc.resize(mat, mat, new Size(width, height));
    }

}