package at.fhv.audioracer.simulator.gui;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.pivot.wtk.Keyboard.KeyCode;

public class ControlView extends JFrame {
	
	private JPanel contentPane;
	private JSlider sliderAccelaration;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlView frame = new ControlView();
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
	public ControlView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		sliderAccelaration = new JSlider();
		sliderAccelaration.setBounds(302, 17, 30, 380);
		sliderAccelaration.setOrientation(SwingConstants.VERTICAL);
		contentPane.add(sliderAccelaration);
		
		JSlider sliderDirection = new JSlider();
		sliderDirection.setBounds(10, 408, 614, 23);
		contentPane.add(sliderDirection);
		
		JLabel lblAccelarationLabel = new JLabel("accelaration");
		lblAccelarationLabel.setBounds(378, 17, 46, 14);
		contentPane.add(lblAccelarationLabel);
		
		JLabel lblDirectionLabel = new JLabel("direction");
		lblDirectionLabel.setBounds(434, 17, 46, 14);
		contentPane.add(lblDirectionLabel);
		
		JLabel lblAccelarationValue = new JLabel("0");
		lblAccelarationValue.setBounds(378, 42, 46, 14);
		contentPane.add(lblAccelarationValue);
		
		JLabel lblDirectionValue = new JLabel("0");
		lblDirectionValue.setBounds(434, 42, 46, 14);
		contentPane.add(lblDirectionValue);
		
		JButton btnChangeSentVelocity = new JButton("Apply Velocity");
		btnChangeSentVelocity.setBounds(490, 38, 89, 23);
		contentPane.add(btnChangeSentVelocity);
		
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent key) {
				// TODO Auto-generated method stub
				if (key.getKeyCode() == KeyCode.UP) {
					sliderAccelaration.setValue(sliderAccelaration.getValue() + 1);
				}
			}
		});
	}
}
