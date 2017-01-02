
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

public class mainScreen extends JPanel {
	private JLabel Label1;
	private JLabel Credits;
	private static JButton button1;
	private static JButton button2;
	private static JButton button3;
	private static JButton button4;
	private static double HEIGHT = Controller.HEIGHT;
	private static double WIDTH = Controller.WIDTH;
	
	// constructs Assets for the main page
	public mainScreen() {

		Label1 = new JLabel("<html><center><br>SIX MEN's MORRIS</br></bold></html>");
		Label1.setFont(new Font("Arial", Font.BOLD, 30));
		button1 = new JButton("New Game");
		button2 = new JButton("Scenario Setup");
		button3 = new JButton("Load from last save");
		button4 = new JButton("Play against Computer");
		Credits = new JLabel("<html><center>Credits to Hatim R, Louis B, and Sarthak D</center></html>");

	}
	// paint method 
	public JPanel paint(){

		JPanel screen = new JPanel();
		screen.setLayout(new GridLayout(0,1));
		screen.add(Label1);
		screen.add(button1);
		screen.add(button4);
		screen.add(button2);
		screen.add(button3);
		screen.add(Credits);
		return screen;
		
	}
	// getter method
	public static JButton getButton1() {
		return button1;
	}

	// getter method
	public static JButton getButton2() {
		return button2;
	}
	
	// getter method
	public static JButton getButton3() {
		return button3;
	}
	
	// getter method
	public static JButton getButton4() {
		return button4;
	}
	// scales everything to the window
	public Dimension getPreferredSize() {
		return new Dimension((int) (HEIGHT), (int) (WIDTH));
	}


}
