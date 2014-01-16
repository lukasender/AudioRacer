package at.fhv.audioracer.camera;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class OpenCVExperiments {
	
	static {
		System.loadLibrary("opencv_java247");
	}
	
	public static void main(String[] args) {
		VideoCapture camera = new VideoCapture(1);
		Mat frame = new Mat();
		int maxTime = 3000; // in ms
		long startTime = System.currentTimeMillis();
		long timer = System.currentTimeMillis();
		while (!camera.isOpened() && (timer - startTime) < maxTime) {
			try {
				Thread.sleep(100);
				System.out.println("Webcam not opened after " + (timer - startTime) + "ms");
				timer = System.currentTimeMillis();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!camera.isOpened()) {
			System.out.println("Webcam could not be opened!");
			System.exit(-1);
		}
		
		System.out.println("Webcam needed " + (timer - startTime) + " ms to be found");
		camera.grab();
		if (camera.read(frame)) {
			Highgui.imwrite("test.jpg", frame);
		} else {
			System.out.println("Could not read current frame");
		}
		
	}
	
}
