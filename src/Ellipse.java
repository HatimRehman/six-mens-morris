import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class Ellipse extends Ellipse2D.Double {
	
	private Color color;
	// this class holds the position objects on the board
	public Ellipse(double a, double b, double c, double d) {

		super(a, b, c, d); // call super constructor

		color = Controller.boardColor; // initial board is predetermined

	}

	// setter method
	public void setColor(Color color) {

		this.color = color; 

	}

	// getter method
	public Color getColor() {

		return color;

	}

}
