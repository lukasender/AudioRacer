package at.fhv.audioracer.simulator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.fhv.audioracer.client.player.PlayerClient;

@SuppressWarnings("serial")
public class ControlView extends JFrame {
	
	private JPanel contentPane;
	private JSlider _sliderAccelaration;
	private JSlider _sliderDirection;
	
	private JLabel _lblAccelarationValue;
	private JLabel _lblDirectionValue;
	
	JButton _btnChangeSentVelocity;
	
	private PlayerClient _playerClient;
	
	public static void main(String[] args) {
		ControlView cv = new ControlView(new PlayerClient());
		cv.setVisible(true);
		
	}
	
	/**
	 * Create the frame.
	 */
	public ControlView(PlayerClient playerClient) {
		
		_playerClient = playerClient;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		_sliderAccelaration = new JSlider();
		_sliderAccelaration.setBounds(302, 17, 30, 380);
		_sliderAccelaration.setOrientation(SwingConstants.VERTICAL);
		contentPane.add(_sliderAccelaration);
		_sliderAccelaration.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				_lblAccelarationValue.setText("" + calcSliderIntToFloat(_sliderAccelaration));
			}
		});
		
		_sliderDirection = new JSlider();
		_sliderDirection.setBounds(10, 408, 614, 23);
		contentPane.add(_sliderDirection);
		_sliderDirection.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				_lblDirectionValue.setText("" + calcSliderIntToFloat(_sliderDirection));
				
			}
		});
		
		JLabel lblAccelarationLabel = new JLabel("accelaration");
		lblAccelarationLabel.setBounds(378, 17, 46, 14);
		contentPane.add(lblAccelarationLabel);
		
		JLabel lblDirectionLabel = new JLabel("direction");
		lblDirectionLabel.setBounds(434, 17, 46, 14);
		contentPane.add(lblDirectionLabel);
		
		_lblAccelarationValue = new JLabel("0");
		_lblAccelarationValue.setBounds(378, 42, 46, 14);
		contentPane.add(_lblAccelarationValue);
		
		_lblDirectionValue = new JLabel("0");
		_lblDirectionValue.setBounds(434, 42, 46, 14);
		contentPane.add(_lblDirectionValue);
		
		_btnChangeSentVelocity = new JButton("Apply Velocity");
		_btnChangeSentVelocity.setBounds(490, 38, 89, 23);
		contentPane.add(_btnChangeSentVelocity);
		
		_btnChangeSentVelocity.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				_playerClient.setDirection(calcSliderIntToFloat(_sliderDirection));
				_playerClient.setSpeed(calcSliderIntToFloat(_sliderAccelaration));
				
				if (_playerClient.getPlayerServer() != null) {
					_playerClient.getPlayerServer().updateVelocity(_playerClient.getSpeed(), _playerClient.getDirection());
				}
			}
		});
		
	}
	
	/**
	 * Calculates a float value between -1 an 1 for a JSlider int Value depending on Range
	 * 
	 * @param controller
	 * @return float between -1 and 1
	 */
	private float calcSliderIntToFloat(JSlider controller) {
		float temp = 0;
		
		float range = controller.getMaximum() - controller.getMinimum();
		float hrange = range / 2;
		
		temp = (float) (controller.getValue() - hrange) / hrange;
		
		return temp;
	}
}
