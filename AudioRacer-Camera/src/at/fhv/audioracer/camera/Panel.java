package at.fhv.audioracer.camera;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	
	// Create a constructor method
	public Panel() {
		super();
	}
	
	private BufferedImage getimage() {
		return image;
	}
	
	private void setimage(BufferedImage newimage) {
		image = newimage;
		return;
	}
	
	/**
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix
	 *            Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	public static BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
			case 1:
				type = BufferedImage.TYPE_BYTE_GRAY;
				break;
			case 3:
				type = BufferedImage.TYPE_3BYTE_BGR;
				// bgr to rgb
				byte b;
				for (int i = 0; i < data.length; i = i + 3) {
					b = data[i];
					data[i] = data[i + 2];
					data[i + 2] = b;
				}
				break;
			default:
				return null;
		}
		BufferedImage image2 = new BufferedImage(cols, rows, type);
		image2.getRaster().setDataElements(0, 0, cols, rows, data);
		return image2;
	}
	
	public void paintComponent(Graphics g) {
		BufferedImage temp = getimage();
		if (temp == null) {
			System.out.println("Image was null");
			return;
		}
		g.drawImage(temp, 10, 10, temp.getWidth(), temp.getHeight(), this);
	}
	
	public static void main(String arg[]) {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		JFrame frame = new JFrame("BasicPanel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		Panel panel = new Panel();
		frame.setContentPane(panel);
		frame.setVisible(true);
		Mat webcam_image = new Mat();
		
		VideoCapture capture = new VideoCapture(0);
		
		trackingObjectExample(webcam_image, capture, frame, panel);
		findTriangleExample(webcam_image, capture, frame, panel);
		
	}
	
	private static void trackingObjectExample(Mat webcam_image, VideoCapture capture, JFrame frame,
			Panel panel) {
		BufferedImage temp;
		if (capture.isOpened()) {
			while (true) {
				
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				capture.read(webcam_image);
				if (!webcam_image.empty()) {
					imgTracking = new Mat(webcam_image.size(), webcam_image.type());
					imgTracking = Mat.zeros(webcam_image.size(), webcam_image.type());
					
					Imgproc.GaussianBlur(webcam_image, webcam_image, new Size(5, 5), 5, 5);
					
					Mat imgHSV = new Mat(webcam_image.size(), Core.DEPTH_MASK_8U, new Scalar(3));
					Imgproc.cvtColor(webcam_image, imgHSV, Imgproc.COLOR_BGR2HSV);
					
					Mat imgTreshold = new Mat(imgHSV.size(), Core.DEPTH_MASK_8U, new Scalar(1));
					Core.inRange(imgHSV, new Scalar(160, 160, 60), new Scalar(179, 255, 255),
							imgTreshold);
					
					Imgproc.GaussianBlur(imgTreshold, imgTreshold, new Size(5, 5), 5, 5);
					
					trackObject(imgTreshold);
					Core.add(webcam_image, imgTracking, webcam_image);
					
					frame.setSize(webcam_image.width() + 40, imgTreshold.height() + 60);
					temp = matToBufferedImage(webcam_image);
					panel.setimage(temp);
					panel.repaint();
				} else {
					System.out.println(" --(!) No captured frame -- Break!");
				}
			}
		}
	}
	
	private static double lastX = -1;
	private static double lastY = -1;
	private static Mat imgTracking;
	
	private static void trackObject(Mat imgTreshold) {
		Moments moments = Imgproc.moments(imgTreshold);
		double moment01 = moments.get_m01();
		double moment10 = moments.get_m10();
		double area = moments.get_m00();
		
		if (area > 1000) {
			double posX = moment10 / area;
			double posY = moment01 / area;
			if (lastX >= 0 && lastY >= 0 && posX >= 0 && posY >= 0) {
				// Draw a yellow line from the previous point to the current point
				Core.line(imgTracking, new Point(posX, posY), new Point(lastX, lastY), new Scalar(
						255, 0, 0), 4);
			}
			
			lastX = posX;
			lastY = posY;
			
		}
	}
}
