// this class holds the Player objects
public class Player extends Ellipse {

	private int remaining = Controller.MORRISSIZE * 3; // initial disks
	
	// constructor method
	public Player(double a, double b, double c, double d) {

		super(a, b, c, d);
	}

	// setter method for loading saved game
	public void setRemaining(int remaining) {

		this.remaining = remaining;
	}

	// getter method
	public int getRemaining() {

		return remaining;
	}

	// mutator method
	public void decrementRemaining() {

	
			remaining--;
		
		

	}

	// mutator method
	public void restoreRemaining() {

		remaining++;

	}

}
