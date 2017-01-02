import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class gameFunctions {

	private static final Color boardColor = Controller.boardColor;
	private static Ellipse[][] diskHolder = Controller.diskHolder;
	private static Player player1 = Controller.player1;
	private static Player player2 = Controller.player2;
	private static int MORRISSIZE = Controller.MORRISSIZE;


	/**
	 * Checks if a piece can be removed according to 6men rules.
	 * If a piece is in a mill, it is not removable unless there's no other 
	 * removeable pieces. 
	 * Assumes playerColor is the opponent's color.
	 * 
	 * @param square which square piece is in, counted from outside in
	 * @param cell Counts from top left clockwise
	 * @return whether or not piece can be removed
	 */
	public static boolean checkRemove(int square, int cell){
		//check if there are pieces not participating in mills
		boolean onlyMill = true;
		for (int i = 0; i < MORRISSIZE; i++) {
			for (int j = 0; j <8 ; j++) {

				//if this piece is the opponents piece, and it isn't in a mill, onlyMill is false
				if (checkMill(i, j) == 0 && diskHolder[i][j].getColor().equals(Controller.playerColor)) {
					onlyMill = false;
				}
			}
		}

		boolean rightPiece = diskHolder[square][cell].getColor().equals(Controller.playerColor);
		//if opponent's piece, either not in a mill or opponent only has mills 
		if ((checkMill(square, cell) == 0 || onlyMill) && rightPiece) {
			return true;
		} 
		else {
			return false;
		}
		
	}


	
	/**
	 * Get a list of possible moves from a given position. Doesn't know if
	 * there's a piece at the position being checked, so make sure beforehand
	 * Will need updating for 9men etc
	 * 
	 * @param square
	 * @param cell
	 *            goes clockwise around square starting top left
	 * @return List of empty cell co-ordinates {square, cell}. Empty if none
	 *         exist
	 */
	public static ArrayList<int[]> GetEmpty(int square, int cell) {
		// translate turn into that player's color
		Color empty = boardColor;

		ArrayList<int[]> legal = new ArrayList<int[]>();
		Color nextCwise = diskHolder[square][(cell + 1) % 8].getColor();
		if (nextCwise.equals(empty)) { // check next cell clockwise in square
			int[] index = { square, (cell + 1) % 8 };
			legal.add(index);
		}
		Color nextXCwise = diskHolder[square][(cell + 7) % 8].getColor();
		if (nextXCwise.equals(empty)) { // check next cell counterclockwise in
										// square
			int[] index = { square, (cell + 7) % 8 };
			legal.add(index);
		}
		// if cell is odd, check if the piece can move to the other square
		if (cell % 2 == 1) {
			Color nextSquare = diskHolder[(square + 1) % 2][cell].getColor();
			if (nextSquare.equals(empty)) {
				int[] index = { (square + 1) % 2, cell };
				legal.add(index);
			}
		}
		return legal;
	}

	/**
	 * Checks how many mills a play makes. Assumes that the given location has one 
	 * of the player's pieces in it.
	 * @param square
	 * @param cell
	 * @return number of mills made, 0 if none
	 */
	public static int checkMill(int square, int cell) {

		Color color = diskHolder[square][cell].getColor();
		int mills = 0;

		if (cell % 2 == 1) { // If odd (not a corner)
			// if the cells before and after it ==color, that's a score
			if (diskHolder[square][cell - 1].getColor().equals(color)
					&& diskHolder[square][(cell + 1) % 8].getColor().equals(color)) { // mod8
																						// for
																						// 6-7-0
																						// case
				mills++;
			}

		} else {
			// if next two cells == it
			if (diskHolder[square][cell + 1].getColor().equals(color)
					&& diskHolder[square][(cell + 2) % 8].getColor().equals(color)) {
				mills++;
			}
			// if the previous two cells == it
			if (diskHolder[square][(cell + 7) % 8].getColor().equals(color)
					&& diskHolder[square][(cell + 6) % 8].getColor().equals(color)) {
				mills++;
			}
		}
		return mills;
	}
	
	//Overload, for AI use
	private static int checkMill(int square, int cell, Color color) {

		int mills = 0;

		if (cell % 2 == 1) { // If odd (not a corner)
			// if the cells before and after it ==color, that's a score
			if (diskHolder[square][cell - 1].getColor().equals(color)
					&& diskHolder[square][(cell + 1) % 8].getColor().equals(color)) { // mod8
																						// for
																						// 6-7-0
																						// case
				mills++;
			}

		} else {
			// if next two cells == it
			if (diskHolder[square][cell + 1].getColor().equals(color)
					&& diskHolder[square][(cell + 2) % 8].getColor().equals(color)) {
				mills++;
			}
			// if the previous two cells == it
			if (diskHolder[square][(cell + 7) % 8].getColor().equals(color)
					&& diskHolder[square][(cell + 6) % 8].getColor().equals(color)) {
				mills++;
			}
		}
		return mills;
	}

	/**
	 * To be called every time turns are changed. removes a piece from remaining
	 * @param a , this player's color
	 * @return other player's color
	 */
	public static Color switchTurn(Color a) {
		if (Controller.againstComputer)
			Controller.computersTurn = (Controller.computersTurn == false) ? true : false;

		if (a.equals(player1.getColor())) {
			player1.decrementRemaining();
			return player2.getColor();
		}
		else {
			player2.decrementRemaining();
			return player1.getColor();
		}

	}

	/** Randomly selects a player. Used for starting game w/ random first turn 
	 * 
	 * @return the player who plays first
	 */
	public static Player turnRand() {

		double numb = Math.random();

		if (numb < 0.5)
			return player1;
		else
			return player2;
	}

	/** Method for checking whether a given scenario is legal, can arrise from normal gameplay
	 * For setting up scenarios
	 * @return String of errors, empty if legal boardstate.
	 */
	public static String checkBoard() {
		int p1 = player1.getRemaining();
		int p2 = player2.getRemaining();
		String flag = "";

		if (p2 >= 3 * MORRISSIZE - 2) {
			flag += "\nPlayer 2 has an illegally low number of pieces";
		}

		if (p1 >= 3 * MORRISSIZE - 2) {
			flag += "\nPlayer 1 has an illegally low number of pieces";
		}

		for (int i = 0; i < MORRISSIZE; i++) {
			// should check between squares here if 9 man's morris
			for (int j = 0; j < 8; j += 2) { // iterate through corners

				if (diskHolder[i][j].getColor().equals(player1.getColor())
						&& diskHolder[i][j + 1].getColor().equals(player1.getColor())
						&& diskHolder[i][(j + 2) % 8].getColor().equals(player1.getColor())) {
					p2--;
					if (p2 < 0) { // if there are more scores than pieces left,
									// this score is illegal
						diskHolder[i][j].setColor(Color.yellow);
						diskHolder[i][j + 1].setColor(Color.yellow);
						diskHolder[i][(j + 2) % 8].setColor(Color.yellow);
						flag += ("\nThe score starting in in square: " + i + " , slot: " + j
								+ " implies that player2 has " + -p2 + " extra piece(s).");

					}

				}
				if (diskHolder[i][j].getColor().equals(player2.getColor())
						&& diskHolder[i][j + 1].getColor().equals(player2.getColor())
						&& diskHolder[i][(j + 2) % 8].getColor().equals(player2.getColor())) {
					p1--;
					if (p1 < 0) {
						diskHolder[i][j].setColor(Color.yellow);
						diskHolder[i][j + 1].setColor(Color.yellow);
						diskHolder[i][(j + 2) % 8].setColor(Color.yellow);
						flag += (" The score starting in in square: " + i + " , slot: " + j
								+ " implies that player1 has " + -p1 + " extra piece(s).");

					}
				}
			}

		}
		return flag;
	}
	
	/**
	 * method to check if a player has lost
	 * @param x: takes in as parameter for whom the program is to determine if the player has lost
	 * @return: a string that tells if the game is lost by the player
	 */
	private static String checkloss (Player x){

		if (Controller.lastState==0&&x.getRemaining()<=0){			
			int piececount=0;											// initializes the player's pieces as 0
			
			for (int i=0;i<MORRISSIZE;i++){
				for (int j=0;j<8;j++){
					if (x.getColor()==diskHolder[i][j].getColor()){			//goes through all nodes and increases piececount if 
						piececount++;										// the player's piece is found
					}
					
				}
			}
			if (piececount<=2){
				return "Loss";								//game is lost if there are 2 or less pieces left on the board
			}
		}

		return "Game in Progress";
	}
	
	/**
	 * method to check if there was a draw (that is there are no more spots for a player to move)
	 * @param x: takes in a player for who the program is to determine if any moves are left
	 * @return: a string that tells whether there was a draw or not.
	 */
	private static String checkdraw(Player x){
		
		if (Controller.state!=1){							
			ArrayList<int[]> checkempty= new ArrayList<int[]>();	// array list that will hold the possible places the player can move 
			
			// traverses through all nodes on the board in a nested structure
			for (int i=0;i<MORRISSIZE;i++){
				
				for (int j=0;j<8;j++){
				
					if (x.getColor().equals(diskHolder[i][j].getColor())){	// when a piece of the player is found on the board
			
						checkempty=GetEmpty(i,j);					//method call to check if the piece can move anywhere
						if (!checkempty.isEmpty()){					//if any spots are available to move to then the game is not drawn
							return "Game in Progress";
						}
						
					}
					
				}
			}
			
			return "Draw";
		}
		
		return "Game in Progress";
		
	}
	
	
	/**
	 * method calls drawstate and lossstate methods for each player to see if either one has won the game or not.
	 * @return : method returns a String that describes the game state. That is if there is a win, draw or if the game is in progress
	 */
	public String gamestate(){
		String drawstate;		// holds the string that is returned from the check if the match is a draw (if a player can make no moves)
		String lossstate;		// holds the string that is returned from the check if the match is lost.
		String gstate="";		// game sthat is determined depening on the results from the calls made to drawstate and lossstate
		
		
		if (Controller.playerColor.equals(player1.getColor())){			//when  it is blue's turn 
			
			lossstate= this.checkloss(player1);							// calls to check if blue has sufficient pieces
			drawstate=this.checkdraw(player1);							// calls to check if blue can move on the board
			
			//conditions to compare the results of the two calls above and decide the game state that is to be returned.
			
			if (lossstate.equals(drawstate)){				//when both return game in progress
				gstate="Game in Progress";								
			}
			
			else if (lossstate.equals("Loss")){				// when a loss is determined
				gstate="Red Wins";
			}
			
			else if (drawstate.equals("Draw")){				//when a draw is determined
				gstate="Game Drawn";
			}
			
		}
		
		// the same structure is followed from the red player.
		
		else{
			lossstate= this.checkloss(player2);
			drawstate=this.checkdraw(player2);
			
			if (lossstate.equals(drawstate)){
				gstate="Game in Progress";
			}
			
			else if (lossstate.equals("Loss")){
				gstate="Blue Wins";
			}
			
			else if (drawstate.equals("Draw")){
				gstate="Game Drawn";
			}
			
		}
		return gstate;										// the state is returned at the end
	}

	/** Method for loading an old game. Returns false if no file to be run.
	 * Sets the board up all nice for a game to be played
	 * 
	 * @param filename
	 * @return whether file exists
	 */
	public static boolean loadGame(String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			//get turn
			br.readLine();
			String turn = br.readLine();
			if (turn.equals("b")) {
				Controller.playerColor = Color.blue;
			} else if (turn.equals("r")) {
				Controller.playerColor = Color.red;
			}
			//fill board
			br.readLine();
			for (int i = 0; i < MORRISSIZE; i++ ) {
				String square = br.readLine();
				String[] squareArr = square.split(",");
				for (int j = 0; j < squareArr.length; j++) {
					if (squareArr[j].equals("b")) {
						diskHolder[i][j].setColor(Color.blue);
					} else if (squareArr[j].equals("r")) {
						diskHolder[i][j].setColor(Color.red);
					}
				}
			}
			player1.setRemaining(Integer.parseInt(br.readLine())); // next line is player1's remaining
			player2.setRemaining(Integer.parseInt(br.readLine())); // next line is player2's remaining
			
			Controller.state = Integer.parseInt(br.readLine()); // next line is state
			Controller.lastState = Integer.parseInt(br.readLine()); // next line is lastState
			
			Controller.againstComputer = Boolean.parseBoolean(br.readLine());
			
			if (Controller.againstComputer) // if above variable declaration returned true
				Controller.computer = (Controller.playerColor.equals(Color.BLUE)) ? player2 : player1;
			
			br.close();
			return true;
    	} catch(Exception e) {
    		return false;
    	}
	}
	
	/** Saves the current boardstate to a text file, to be loaded by loadGame()
	 * @param filename
	 */
	public static void saveGame(String filename) {
		try {
			FileWriter fw = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("Turn:"+ "\n");
			if (Controller.playerColor.equals(Color.blue)) {
				bw.write("b"+ "\n");
			} else if (Controller.playerColor.equals(Color.red)) {
				bw.write("r"+ "\n");
			}
			
			bw.write("Board: b = blue, r = red, . = empty"+ "\n");
			for (int i = 0; i< MORRISSIZE; i++) {
				String line = "";
				for (Ellipse el : diskHolder[i]) {
					if (el.getColor().equals(Color.blue)) {
						line += "b,";
					} else if (el.getColor().equals(Color.red)) {
						line += "r,";
					} else if (el.getColor().equals(boardColor)) {
						line += ".,";
					}
				}
				line = line.substring(0, line.length()-1);
				bw.write(line + "\n");
			}
			bw.write(player1.getRemaining() + "\n"); // player 1 remaining
			bw.write(player2.getRemaining() + "\n"); // player 1 remaining
			
			bw.write(Controller.state + "\n"); // state 
			bw.write(Controller.lastState + "\n"); // lastState
			
			bw.write(Controller.againstComputer + "\n"); // is the game against AL?
			
			bw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/** Function to start vs computer game. 
	 * 
	 * @return Whether computer goes first
	 */
	public static boolean computerGoesFirst(){
		
		double rand = Math.random();
	
		if (rand < 0.5) return false;
		
		Player player = (Controller.playerColor.equals(player1.getColor())) ? player1 : player2;
		getOptimalMove(player);
		for (int i = 0; i < MORRISSIZE; i++) 
			for (int j = 0; j <= 7; j++) 
				if (diskHolder[i][j].contains(Controller.xCoord - 100, Controller.yCoord)) {
					diskHolder[i][j].setColor(Controller.playerColor);
					player.decrementRemaining();
					return true;
				}
		
		return false;
	}
	
	/** Get computer's move for early game (placing floating pieces)
	 * @param a Player controlled by computer
	 */
	public static void getOptimalMove(Player a) {
		Color otherColor = Color.black;
		if (a.getColor().equals(Color.blue)) {
			otherColor = Color.red;
		} else if(a.getColor().equals(Color.red)) {
			otherColor = Color.blue;
		}
		
		//if you can block or create a mill, do so
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				boolean isEmpty = diskHolder[i][j].getColor().equals(boardColor);
				boolean isMill = (checkMill(i, j, a.getColor())>0 || checkMill(i, j, otherColor)>0);
				if(isMill && isEmpty) {
					Controller.xCoord = (int) diskHolder[i][j].getCenterX() + 100;
					Controller.yCoord = (int) diskHolder[i][j].getCenterY();
					return;	
				}
			}
		}
		//try to extend mills
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j+=2) {
				boolean isEmpty = diskHolder[i][j].getColor().equals(boardColor);
				//check that one of the spaces in this line is owned by the player
				boolean possibleMill = diskHolder[i][(j+1)%8].getColor().equals(a.getColor()) ||
						diskHolder[i][(j+2)%8].getColor().equals(a.getColor());
				//check that the opponent is not blocking this line
				possibleMill = !(diskHolder[i][(j+1)%8].getColor().equals(otherColor) &&
						diskHolder[i][(j+2)%8].getColor().equals(otherColor));
				if (isEmpty && possibleMill) {
					Controller.xCoord = (int) diskHolder[i][j].getCenterX() + 100;
					Controller.yCoord = (int) diskHolder[i][j].getCenterY();
					return;	
				}
			}
		}
		
		//just put something down
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				boolean isEmpty = diskHolder[i][j].getColor().equals(boardColor);
				if(isEmpty) {
					Controller.xCoord = (int) diskHolder[i][j].getCenterX() + 100;
					Controller.yCoord = (int) diskHolder[i][j].getCenterY();
					return;	
				}
			}
		}
		
	}
		
	
	/** Overload
	 *  Gets computer to remove piece
	 * @param a Computer controlled player
	 * @param b Player whose piece is being removed
	 */
	public static void getOptimalMove(Player a, Player b) {
		
		//just find something you can remove
		for (int i = 0; i < 2; i++) 
			for (int j = 0; j < 8; j++)
				if(diskHolder[i][j].getColor().equals(b.getColor()) && checkRemove(i,j)) {
					Controller.xCoord = (int) diskHolder[i][j].getCenterX() + 100;
					Controller.yCoord = (int) diskHolder[i][j].getCenterY();
					return;			
				}
		
	}

	
	/**
	 * Get computer's move for midgame, where player can only move over one piece 
	 * 
	 * @param player Computer's player
	 * @return List of moves
	 */
	public static ArrayList<int[]> getOptimalMove(Color player){
		
		ArrayList<int[]> legal = new ArrayList<int[]>();
		
		int traversedAllPieces = 0; 
		int possibleMoves = 0;
		int RandomPiece = 0;
		
		while(true){
			
			for (int square = 0; square < MORRISSIZE; square++) {
				for (int cell = 0; cell <8 ; cell++) {
				
					if (diskHolder[square][cell].getColor().equals(player)){
						
						if (legal.size()>0) 
							possibleMoves++;
						
						legal = GetEmpty(square, cell);
					
						if (legal.size()>0 && traversedAllPieces == 1 && possibleMoves == RandomPiece){
							Controller.i_old = square;
							Controller.j_old = cell;
							getOptimalMove(legal); // will assign next position 

							return legal;
						}
									
					}
				}
			}
			
			traversedAllPieces = 1; // after one complete iteration of for loop, set to 1
			
			RandomPiece =  new Random().nextInt(possibleMoves+1)+1; // 
	
			possibleMoves = 0;
			
		}

	}	
	
	// picks which location to move to for some piece 
	private static void getOptimalMove(ArrayList<int[]> legalMoves){
			
		int[] somePos = legalMoves.get(new Random().nextInt(legalMoves.size()));
		
		Controller.xCoord = (int) diskHolder[somePos[0]][somePos[1]].getCenterX()+100;
		Controller.yCoord = (int) diskHolder[somePos[0]][somePos[1]].getCenterY();
		
	}	
	
}
