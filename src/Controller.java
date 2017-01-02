import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class Controller {

	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;
	public static final int MORRISSIZE = 2;
	public static final double SCALE = 0.5; // DO NOT CHANGE
	public static Ellipse[][] diskHolder = new Ellipse[MORRISSIZE][8];
	public static Player player1;
	public static Player player2;
	public static Player computer;
	public static Rectangle END;
	public static Color boardColor = Color.black;
	public static int lastState = 1;
	public static int state = 1;
	public static String displaystate="Game in Progress";
	public static boolean againstComputer = false;
	public static boolean computersTurn = false;
	public static int xCoord; // x coordinates of the piece clicked
	public static int yCoord; // y coordinates of the piece clicked
	public static int i_old, j_old;
	public static boolean sameTurn; // for updateBoard to not display wrong player
	// if a mill was formed by other player
	
	private static boolean forceEntry = false; // if computers turn, jumps straight
											// into case statements by setting to true	
	private static int mills;
	private static ArrayList<int[]> legal;
	private static JFrame frame;
	private static JPanel board;
	
	
	public static Color playerColor = boardColor; // initially black

	public Controller() {

		player1 = new Player(2, 166, 30, 30);
		player2 = new Player(2, 214, 30, 30);

		player1.setColor(Color.blue);
		player2.setColor(Color.red);

		for (int i = 0; i < MORRISSIZE; i++) {

			Ellipse one = new Ellipse(40 + i * 50, 40 + i * 50, 20, 20); // top left
			Ellipse two = new Ellipse(190, 40 + i * 50, 20, 20); //top center
			Ellipse three = new Ellipse(340 - i * 50, 40 + i * 50, 20, 20); //top right
			Ellipse four = new Ellipse(340 - i * 50, 190, 20, 20); //center right
			Ellipse five = new Ellipse(340 - i * 50, 340 - i * 50, 20, 20); //bottom right
			Ellipse six = new Ellipse(190, 340 - i * 50, 20, 20); //bottom center
			Ellipse seven = new Ellipse(40 + i * 50, 340 - i * 50, 20, 20); //bottom left
			Ellipse eight = new Ellipse(40 + i * 50, 190, 20, 20); //center left

			END = new Rectangle(360, 280, 30, 30);

			// place each object in data structure
			diskHolder[i][0] = one;
			diskHolder[i][1] = two;
			diskHolder[i][2] = three;
			diskHolder[i][3] = four;
			diskHolder[i][4] = five;
			diskHolder[i][5] = six;
			diskHolder[i][6] = seven;
			diskHolder[i][7] = eight;
   
		}

	}

	private static void createFrame() {

		frame = new JFrame(MORRISSIZE * 3 + " Men's Morris");
		frame.setSize((int) (WIDTH), (int) (HEIGHT));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.add(board);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	/** Method for starting the actual game.
	 * 
	 * @param startCondition Whether starting from save, from scenario, or new game
	 */
	public static void startGame(String startCondition) {
		
		gameFunctions GameFunctions = new gameFunctions();
		
		if (startCondition.equals("From Save")) {
			
			if(!GameFunctions.loadGame("save.txt")){
				JOptionPane.showMessageDialog(null, "Save file not found. New game will be started.", "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		// assumption: scenario set up assumes players don't have any more pieces left
		// or they would have been placed.
		else if (startCondition.equals("From Scenario")) {
			state = 2; // start from piece shuffling state
			player1.setRemaining(0);
			player2.setRemaining(0);
		}
		
		// assumption: scenario set up assumes players don't have any more pieces left
		// or they would have been placed.
		else if (startCondition.equals("With Computer")) {		
			
			againstComputer = true;
			
			playerColor = GameFunctions.turnRand().getColor();
		
			//determines if computer randomly goes first and makes move
			
			if (gameFunctions.computerGoesFirst()){
				computer = (playerColor.equals(player1.getColor())) ? player1 : player2;
				playerColor = (playerColor.equals(Color.red)) ? Color.blue : Color.red;
				
				JOptionPane.showMessageDialog(null, "Computer" + " plays first.", "",
						JOptionPane.INFORMATION_MESSAGE);
			}
			
			else {
				JOptionPane.showMessageDialog(null, "You" + " play first.", "",
						JOptionPane.INFORMATION_MESSAGE);
				computer = (playerColor.equals(player1.getColor())) ? player2 : player1;
			}
		}
		
		updateBoard layout = new updateBoard();
		board.setSize((int) (WIDTH), (int) (HEIGHT));
		board.add(layout);
		board.repaint();
		board.revalidate();
		
		if (playerColor.equals(boardColor)) playerColor = GameFunctions.turnRand().getColor(); // condition against loaded game
		
		String color = (playerColor.equals(Color.red)) ? "Red" : "Blue";
		
		if (!againstComputer)
		JOptionPane.showMessageDialog(null, color + " plays first.", "",
				JOptionPane.INFORMATION_MESSAGE);
		
		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				
				// "save" button, you can only save if you aren't in the middle of a move
				// (ie havent moved into a mill or selected a piece to move yet)
				if (END.contains(e.getX() - 100, e.getY())) {
					
					if (state == 1 || state == 2){
						GameFunctions.saveGame("save.txt");
						JOptionPane.showMessageDialog(null, "Game Progress Saved!", "Board Status",
								JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(null, "Can't save while turn in progress!", "Board Status",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}

				else {
					
					xCoord = e.getX();
					yCoord = e.getY();

					// nested for loop finds correct disk
					for (int i = 0; i < MORRISSIZE; i++) {

						for (int j = 0; j <= 7; j++) {
							
							// continues until
							if (diskHolder[i][j].contains(xCoord - 100, yCoord) || forceEntry) {
								
								//Debugging helper
								System.out.println(forceEntry);
								System.out.println(state);
								System.out.println("");
								
								switch (state) {
								case 0: // remove mills
									
									if (!GameFunctions.checkRemove(i, j)){ 					
										
										break;
									}
								
									else {
										diskHolder[i][j].setColor(boardColor);
										// call the checks
										updateBoard update = new updateBoard();
										board.add(update);
										board.repaint();
										board.revalidate();
										mills--;
							
										if (mills != 0) break; // breaks if mills = 2
									
										else{
											
											state = (lastState == 3) ? 2 : lastState;
											
											lastState=0;
											sameTurn = false;
											
											
										}
										
										if (againstComputer && !computersTurn)
											forceEntry = false;
										
										displaystate=GameFunctions.gamestate();
										if (!displaystate.equals("Game in Progress"))
											break;
										
										if (computersTurn){
											i = 0;
											j = -1;
										
											if (state == 1) 
												GameFunctions.getOptimalMove(computer);
											
											else if (state == 2) forceEntry = true;
											
											continue;
										}
									}
									break;
										
								case 1: // players have not placed all pieces
									
									if (diskHolder[i][j].getColor().equals(boardColor)) {

										diskHolder[i][j].setColor(playerColor);				

											updateBoard update = new updateBoard();
											board.add(update);
											board.repaint();
											board.revalidate();
										
										playerColor = GameFunctions.switchTurn(playerColor);
										
										if ((player1.getRemaining() + player2.getRemaining()) <= 0)
											state = 2; // go to next state

										mills = GameFunctions.checkMill(i,j);
										
						 				if (mills != 0) {
											state = 0;
											lastState = 1;
											sameTurn = true;
											
											if (computersTurn)
												break;
										}
									}
									
									if (againstComputer && (computersTurn || (mills != 0 && !computersTurn))){
										forceEntry = false;
										if (mills == 0)
											GameFunctions.getOptimalMove(computer);
										
										
										else{
											Player otherPlayer = (computer.getColor().equals(player1.getColor())) ? player2 : player1;				
											GameFunctions.getOptimalMove(computer, otherPlayer);			
										
										}
										
										
										i = 0; // restart for loop
										j = -1; // restart for loop
																				
										continue; // restart Loop without waiting for mouse click
										
									}
										
									break; // break from case}

									
								case 2: // players can only shuffle pieces (pick a piece)
									
									if (computersTurn){	

										legal = GameFunctions.getOptimalMove(computer.getColor()); // will get non empty legal
										state = 3;				
										i = 0;
										j = -1;
										forceEntry = !forceEntry;
										continue;
									}
									
									else if (diskHolder[i][j].getColor().equals(playerColor)){
									
										legal = GameFunctions.GetEmpty(i, j);
										if (!legal.isEmpty()){
											state = 3;
											i_old = i;
											j_old = j;
										}
										
									}
									 
									break; // break from case
									
								case 3: // players can only shuffle pieces (move the piece)
									
									if (diskHolder[i][j].getColor().equals(playerColor)) state = 2;
									
									else {
										for (int[] k : legal){
										
										if (k[0] == i && k[1] == j){
					
											diskHolder[i][j].setColor(playerColor);
											diskHolder[i_old][j_old].setColor(boardColor);
											
											updateBoard update = new updateBoard();
											
											board.add(update);
											board.repaint();
											board.revalidate();
											
											state = 2;
											
											playerColor = GameFunctions.switchTurn(playerColor);
												
											mills = GameFunctions.checkMill(i,j);
											
											if (mills != 0) {
												state = 0;
												lastState = 3;
												sameTurn = true;
												if (computersTurn)
													break;
											}
											
											if (againstComputer && (computersTurn || (mills != 0 && !computersTurn))){
												
												if (mills!=0){
													Player otherPlayer = (computer.getColor().equals(player1.getColor())) ? player2 : player1;				
													GameFunctions.getOptimalMove(computer, otherPlayer);
													forceEntry = false;
													}
												
												else
													forceEntry = true;
												
												i = 0;
												j = -1;
												
												
											}
											
											
											break;
											}
										
										}
										if (againstComputer && (computersTurn || (mills != 0 && !computersTurn)))
											continue;
										
										break;	
									}
									
								}
								//calls the gamestate function to determine the current state of the game
								//i.e. If a player won, the game was tied, or if the game is still in progress
								
								displaystate=GameFunctions.gamestate();
								if (!displaystate.equals("Game in Progress")) {; state = 9; // non existent state = end
								
								JOptionPane.showMessageDialog(null, "GAME OVER\n"+ displaystate, "Board Status",
										JOptionPane.INFORMATION_MESSAGE);
								
								frame.dispose();
								}
								
								break; // break from loop
								
							}
							
							
						}

					}

				}
		
			}

		});

	}

	/**
	 * Runs when create scenario option is chosen
	 */
	public static void scenarioSetup(){
		
		gameFunctions GameFunctions = new gameFunctions();
		
		updateBoard layout = new updateBoard();
	
		board.add(layout);
		board.repaint();
		board.revalidate();
	
		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
	
				if (END.contains(e.getX() - 100, e.getY())) {
					
					String boardState = GameFunctions.checkBoard();
					if (boardState.equals("")){
						
						board.removeMouseListener(this);
						
						startGame("From Scenario");
					}
					
					else {	
						JOptionPane.showMessageDialog(null, "Illegal Board State! " + boardState, "Board Status",
							JOptionPane.INFORMATION_MESSAGE);
						}
				}
	
				// if player1 is clicked
				else if (player1.contains(e.getX() - 100, e.getY()))
					playerColor = player1.getColor();
	
				// if player2 is clicked
				else if (player2.contains(e.getX() - 100, e.getY()))
					playerColor = player2.getColor();
	
				else {
					// nested for loop finds correct disk
					for (int i = 0; i < MORRISSIZE; i++) {
	
						for (int j = 0; j <= 7; j++) {
	
							if (diskHolder[i][j].contains(e.getX() - 100, e.getY())) {
	
								// if a black/red tile is placed on
								// blue, restore blue count by 1
								if (((playerColor.equals(boardColor) || playerColor.equals(player2.getColor()))
										&& diskHolder[i][j].getColor().equals(Color.blue))) {
									player1.restoreRemaining();
	
								}
	
								// if a black/blue tile is placed on
								// red, restore red count by 1
								else if (((playerColor.equals(boardColor)
										|| playerColor.equals(player1.getColor()))
										&& diskHolder[i][j].getColor().equals(Color.red))) {
									player2.restoreRemaining();
								}
	
								Color oldColor = diskHolder[i][j].getColor();
								diskHolder[i][j].setColor(playerColor); // place
																		// tile
	
								// refresh pane
								updateBoard update = new updateBoard();
								board.add(update);
								board.repaint();
								board.revalidate();
	
								// decrement count of player1's
								// remaining tiles if current disk is
								// not already blue
								if (playerColor == player1.getColor() && !oldColor.equals(player1.getColor())) {
									player1.decrementRemaining();
									if (player1.getRemaining() == 0)
										playerColor = boardColor;
								}
	
								// decrement count of player2's
								// remaining tiles if current disk is
								// not already red
								else if (playerColor == player2.getColor()
										&& !oldColor.equals(player2.getColor()))
									player2.decrementRemaining();
								if (player2.getRemaining() == 0)
									playerColor = boardColor;
	
								break;
							}
	
						}
	
					}
				}
			
			}
		});
	
	}

	public static void main(String args[]) {

		Controller game = new Controller();
		mainScreen mains = new mainScreen();
		board = new JPanel();
		board.add(mains.paint());
		createFrame();

		mains.getButton1().addActionListener(new ActionListener() { /// new game
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
					mains.getButton1().removeActionListener(this);
					mains.getButton2().removeActionListener(this);
					mains.getButton3().removeActionListener(this);
					mains.getButton4().removeActionListener(this);
					
					board.remove(mains.paint());
					game.startGame("New game");
				}
			});
		
	
		mains.getButton2().addActionListener(new ActionListener() { /// scenario
			
			@Override
			public void actionPerformed(ActionEvent e) {
					
					mains.getButton1().removeActionListener(this);
					mains.getButton2().removeActionListener(this);
					mains.getButton3().removeActionListener(this);
					mains.getButton4().removeActionListener(this);
					
					board.remove(mains.paint());	
					game.scenarioSetup();
				}
			});
		

		mains.getButton3().addActionListener(new ActionListener() { /// scenario
			
			@Override
			public void actionPerformed(ActionEvent e) {
						
					mains.getButton1().removeActionListener(this);
					mains.getButton2().removeActionListener(this);
					mains.getButton3().removeActionListener(this);
					mains.getButton4().removeActionListener(this);
					
					board.remove(mains.paint());	
					game.startGame("From Save");
				}
			});
		
		mains.getButton4().addActionListener(new ActionListener() { /// scenario
			
			@Override
			public void actionPerformed(ActionEvent e) {
						
					mains.getButton1().removeActionListener(this);
					mains.getButton2().removeActionListener(this);
					mains.getButton3().removeActionListener(this);
					mains.getButton4().removeActionListener(this);
					
					board.remove(mains.paint());	
					game.startGame("With Computer");
				}
			});
		
		
	

		}
	}
	