package at.fhv.audioracer.serial;

import jssc.SerialPortException;

public class Main {
	
	public static void main(String[] args) {
		try {
			new SerialInterface("COM1");
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}