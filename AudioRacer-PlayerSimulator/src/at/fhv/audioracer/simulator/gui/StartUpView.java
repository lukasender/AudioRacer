package at.fhv.audioracer.simulator.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.simulator.player.Simulator;

public class StartUpView extends JFrame {
	
	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartUpView frame = new StartUpView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public StartUpView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblFreeCarsLabel = new JLabel("Free cars");
		lblFreeCarsLabel.setBounds(10, 11, 80, 14);
		contentPane.add(lblFreeCarsLabel);
		
		JList<Car> listFreeCars = new JList<>();
		listFreeCars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listFreeCars.setBounds(10, 24, 80, 227);
		contentPane.add(listFreeCars);
		
		JButton btnReady = new JButton("Ready");
		btnReady.setBounds(100, 228, 89, 23);
		contentPane.add(btnReady);
		
		final JButton btnConnect = new JButton("Connect");
		btnConnect.setBounds(199, 228, 89, 23);
		contentPane.add(btnConnect);
		btnConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Simulator.startClient("Max MusterMann");
					btnConnect.setText("Disconnect");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
}
