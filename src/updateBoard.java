import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;


import javax.swing.JPanel;

public class updateBoard extends JPanel {
	private int MORRISSIZE = Controller.MORRISSIZE;
	private double SCALE = Controller.SCALE;
	private double HEIGHT = Controller.HEIGHT;
	private double WIDTH = Controller.WIDTH;
	private Player player1 = Controller.player1;
	private Player player2 = Controller.player2;
	private static final Color boardColor = Controller.boardColor;
	private Ellipse[][] diskHolder = Controller.diskHolder;
	private static Rectangle END = Controller.END;	

	//Updates the board view. Called every time something changes.
	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		super.paintComponent(g);

		g2.setColor(Color.black);

		// draw connection lines
		g2.setStroke(new BasicStroke(4));
		g2.drawLine(200, 350, 200, (int) (250 + 50 * (3 - MORRISSIZE)));
		g2.drawLine(200, 50, 200, (int) (150 - 50 * (3 - MORRISSIZE)));
		g2.drawLine(50, 200, (int) (150 - 50 * (3 - MORRISSIZE)), 200);
		g2.drawLine((int) (250 + 50 * (3 - MORRISSIZE)), 200, 350, 200);
		
		
		for (int i = 0; i < MORRISSIZE; i++) {

			// draw rectangle lines for each level on the board
			g2.drawRect((int) ((100 + i * 100) * SCALE), (int) ((100 + i * 100) * SCALE),
					(int) ((600 - (i * 200)) * SCALE), (int) ((600 - i * 200) * SCALE));

			// draw outlines
			g.drawOval(40 + i * 50, 40 + i * 50, 20, 20); // top left
			g.drawOval(190, 40 + i * 50, 20, 20); // top center
			g.drawOval(340 - i * 50, 40 + i * 50, 20, 20); // top right
			g.drawOval(40 + i * 50, 190, 20, 20); // center left
			g.drawOval(340 - i * 50, 190, 20, 20); // center right
			g.drawOval(40 + i * 50, 340 - i * 50, 20, 20); // bottom left
			g.drawOval(190, 340 - i * 50, 20, 20); // bottom center
			g.drawOval(340 - i * 50, 340 - i * 50, 20, 20); // bottom right


			// draw each object with its current color
			g2.setColor(diskHolder[i][0].getColor());
			g2.fill(diskHolder[i][0]);

			g2.setColor(diskHolder[i][1].getColor());
			g2.fill(diskHolder[i][1]);

			g2.setColor(diskHolder[i][2].getColor());
			g2.fill(diskHolder[i][2]);

			g2.setColor(diskHolder[i][3].getColor());
			g2.fill(diskHolder[i][3]);

			g2.setColor(diskHolder[i][4].getColor());
			g2.fill(diskHolder[i][4]);

			g2.setColor(diskHolder[i][5].getColor());
			g2.fill(diskHolder[i][5]);

			g2.setColor(diskHolder[i][6].getColor());
			g2.fill(diskHolder[i][6]);

			g2.setColor(diskHolder[i][7].getColor());
			g2.fill(diskHolder[i][7]);

			g2.setColor(Color.black);

		}

		if (player1.getRemaining() > 0) {
			// change color, draw outline, change color, fill object
			g2.setColor(boardColor);
			g2.drawOval(2, (int) 166, 30, 30);
			g2.setColor(player1.getColor());
			g2.fill(player1);
		}

		if (player2.getRemaining() > 0) {
			// change color, draw outline, change color, fill object
			g2.setColor(boardColor);
			g2.drawOval(2, (int) 214, 30, 30);
			g2.setColor(player2.getColor());
			g2.fill(player2);
		}

		
		g2.setColor(Color.BLACK);
		// draw end game button
		g2.drawRect(360, 280, 30, 30);
		g2.setColor(Color.ORANGE);
		g2.fill(END);
		g2.setColor(Color.BLACK);
		
		g2.setFont(new Font("Arial", Font.BOLD, 20));
		g2.drawString(Controller.displaystate,120,15);
		g2.setFont(new Font("Arial", Font.BOLD, 10));
		
		String color;
		
		if (Controller.sameTurn) // playerColor does not represent who's turn it is in this case
			color= (Controller.playerColor.equals(player1.getColor()))? "Red" : "Blue";
		
		else 
			color = (Controller.playerColor.equals(player1.getColor()))? "Blue" : "Red";
			
		if (Controller.displaystate.equals("Game in Progress")) g2.drawString(color+"'s Turn",170,30);

	}

	//scales everything to the window
	public Dimension getPreferredSize() {
		return new Dimension((int) (HEIGHT), (int) (WIDTH));
	}

}
