import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.objdetect.QRCodeDetector;

import java.awt.Desktop;
import java.net.URI;

public class QRScanner {

    public static void main(String[] args) {

        // Load OpenCV DLL
        System.load(System.getProperty("user.dir") + "/dll/opencv_java490.dll");

        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Cannot open webcam");
            return;
        }

        Mat frame = new Mat();
        QRCodeDetector qrDecoder = new QRCodeDetector();

        String scannedData = "";

        System.out.println("Scanning for QR codes...");

        while (true) {
            if (camera.read(frame)) {

                Mat points = new Mat();
                String data = qrDecoder.detectAndDecode(frame, points);

                if (!data.isEmpty()) {
                    scannedData = data;  // Store latest scanned data

                    System.out.println("QR Code Detected: " + data);

                    if (data.startsWith("http://") || data.startsWith("https://")) {
                        try {
                            Desktop.getDesktop().browse(new URI(data));
                            System.out.println("Opened in browser.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Display message in the camera window
                Imgproc.putText(frame, "Show QR to Camera", new Point(10, 30),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(0, 255, 0), 2);

                if (!scannedData.isEmpty()) {
                    Imgproc.putText(frame, "Scanned: " + scannedData, new Point(10, frame.rows() - 20),
                            Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255), 2);
                }

                HighGui.imshow("QR Code Scanner", frame);

                if (HighGui.waitKey(1) == 27) {  // ESC key to exit
                    break;
                }
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
    }
}
