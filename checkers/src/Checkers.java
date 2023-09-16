// Name: Jacky Men
// Game: Checkers 
// Added feature: option to save old position and start new games

import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.event.*;

import java.util.Scanner;
import java.io.*;

{
	// Global Variables 
	// ----------------------------------------------------------------------------------------------------------------------------
	public static int gameState = 0; 
	// 0 - menu, 1 - in game, 2 - Black win, 3 - White win, 4 - Offer Draw, 5 - Draw Game, 6 - Rules during Game, 7 - Confirm Resignation, 8 - Rules in menu

	public static int posX; // the x coordinate from a mouse press
	public static int posY; // the y coordinate from a mouse press
	public static int forcedX; // the x coordinate the user has to click if a consecutive capture is available
	public static int forcedY; // the y coordinate the user has to click if a consecutive capture is available
	public static boolean consecutiveCapture = false; // updated through the anotherCapture method 
	public static boolean moving = false; 
	public static int selectedPiece = -1; 
	// We  also use selectedPiece as an indicator for who's turn it is
		// If it's negative, it is black's turn, if it's positive, it is white's turn 
	
	public static int pieceX; // stores x, grid coordinate of selected piece you want to move
	public static int pieceY; // stores y, grid coordinate 
	public static int[][] grid = { {0, 1, 0, 1, 0, 1, 0, 1},
					 			      {1, 0, 1, 0, 1, 0, 1, 0},
								      {0, 1, 0, 1, 0, 1, 0, 1},
								      {0, 0, 0, 0, 0, 0, 0, 0},
								      {0, 0, 0, 0, 0, 0, 0, 0},
								      {-1, 0, -1, 0, -1, 0, -1, 0},
								      {0, -1, 0, -1, 0, -1, 0, -1},
								      {-1, 0, -1, 0, -1, 0, -1, 0} };
	// Correlates to the board coordinates, and uses the same x and y coordinate system as the panel 
	// 0 - no pieces occupying space 
	// -1 - black piece is occupying space 
	// -2 - black king is occupying space 
	// 1 - red piece is occupying space 
	// 2 - red king is occupying space 
	// grid[row][column], so to get a coordinate we want, it is grid[y][x]
	
	public static boolean gameSaved; // checks if a game is saved
	// VERY BIG NOTE:
	// The gameSaved variable is stored in a text file because it is predetermined before the program is even started
	// For example, a game could have been saved from previous times when the program was run, 
	// so we needed to somehow remember that a game has been saved before 
	// We get the value of gameSaved in the "main code" by extracting from the text file
	
	public static boolean playingSaved = false; // checks if the current game being played is a saved game 
	
	public static BufferedImage menu;
	public static BufferedImage board;
	public static BufferedImage redPawn;
	public static BufferedImage blackPawn;
	public static BufferedImage blackKing;
	public static BufferedImage redKing;
	public static BufferedImage redSideBar; 
	public static BufferedImage blackSideBar; 
	public static BufferedImage confirmResig;
	public static BufferedImage blackWins;
	public static BufferedImage redWins;
	public static BufferedImage offerDraw;
	public static BufferedImage draw; 
	public static BufferedImage rules;
	
	// Random stuff Ms.Wong told me to do to have the game stop flickering 
	Image offScreenImage;
	Graphics offScreenBuffer; 
	
	// ----------------------------------------------------------------------------------------------------------------------------
	
	public Checkers()
	{
		// JPanel Settings
		setPreferredSize(new Dimension(800, 600)); // the board will be (600, 600), and the side bar is (200, 600) 
		setBackground(new Color(255, 255, 255));
		
		try
		{
			menu = ImageIO.read(new File("menu.png"));
			board = ImageIO.read(new File("board.png"));
			redPawn = ImageIO.read(new File("redPawn.png"));
			blackPawn = ImageIO.read(new File("blackPawn.png"));
			redKing = ImageIO.read(new File("redKing.png"));
			blackKing = ImageIO.read(new File("blackKing.png"));
			blackSideBar = ImageIO.read(new File("blackSideBar.png"));
			redSideBar = ImageIO.read(new File("redSideBar.png"));
			confirmResig = ImageIO.read(new File("confirmResig.png"));
			blackWins = ImageIO.read(new File("blackWins.png"));
			redWins = ImageIO.read(new File("redWins.png"));
			offerDraw = ImageIO.read(new File("offerDraw.png"));
			draw = ImageIO.read(new File("draw.png"));
			rules = ImageIO.read(new File("rules.png"));
		}
		catch(Exception e)
		{
			System.out.println("IMAGE NOT FOUND!");
		}
		this.setFocusable(true);
		addMouseListener(this);
	}
	
	public void paintComponent(Graphics g)
	{
		// Not my code, given by Ms.Wong for offScreenBuffer
		// -------------------------------------------------------------------
		if (offScreenBuffer == null)
		{
			offScreenImage = createImage (this.getWidth(), this.getHeight());
			offScreenBuffer = offScreenImage.getGraphics(); 
		}
		// -------------------------------------------------------------------
		
		if (gameState != 7 && gameState != 4 && gameState != 6 && gameState != 8) // for pop-ups, there is no need to erase the whole screen 
		{
			offScreenBuffer.clearRect(0, 0, this.getWidth(), this.getHeight());
		}
		
		if (gameState == 0) // draw menu
		{
			offScreenBuffer.drawImage(menu, 0, 0, null);
		}
		else if (gameState == 7) // draw confirm resignation pop-up
		{
			offScreenBuffer.drawImage(confirmResig, 90, 150, null);
		}
		else if (gameState == 4) // draw offer draw pop-up
		{
			offScreenBuffer.drawImage(offerDraw, 100, 158, null);
		}
		else if (gameState == 1) // draw board, pieces, and on-going game 
		{
			offScreenBuffer.drawImage(board, 0, 0, null);
			
			if (selectedPiece < 0) // negative selectedPiece indicates black turn
			{
				offScreenBuffer.drawImage(blackSideBar, 600, 0, null);
			}
			else if (selectedPiece > 0) // positive selectedPiece indicates white turn
			{
				offScreenBuffer.drawImage(redSideBar, 600, 0, null);
			}
			
			if (moving == true) // draw's the highlight if piece is supposed to move
			{
				offScreenBuffer.setColor(new Color(15, 122, 15));
				offScreenBuffer.drawRect(pieceX*75, pieceY*75, 75, 75);
				offScreenBuffer.fillRect(pieceX*75, pieceY*75, 75, 75);
			}
		
			for(int i = 0; i < 8; i++) // uses information from grid to create / update the board on screen
			// j is rows, i is columns 
			{
				for(int j = 0; j < 8; j++) 
				{
					if(grid[j][i] == 1) // if coordinates on 2D indicate redPawn
					{
						offScreenBuffer.drawImage(redPawn, i*75, j*75, null); 
					}
					else if(grid[j][i] == -1) // if coordinates on 2D indicate black pawn
					{
						offScreenBuffer.drawImage(blackPawn, i*75, j*75, null);
					}
					else if(grid[j][i] == 2) // if coordinates on 2D indicate red king
					{
						offScreenBuffer.drawImage(redKing, i*75, j*75, null);
					}
					else if(grid[j][i] == -2) // if coordinates on 2D indicate black king
					{
						offScreenBuffer.drawImage(blackKing, i*75, j*75, null);
					}
				}
			}
		}
		else if (gameState == 2) // draw black win screen
		{
			for (int i = 0; i < 11; i++)
			{
				for (int j = 0; j < 8; j++)
				{
					if (i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1)
					{
						offScreenBuffer.drawImage(blackKing, i * 75, j * 75, null);
					}
					else
					{
						offScreenBuffer.drawImage(blackPawn, i * 75, j * 75, null);
					}
				}
			}
			offScreenBuffer.drawImage(blackWins, 225, 200, null);
		}
		else if (gameState == 3) // draw white win screen 
		{
			for (int i = 0; i < 11; i++)
			{
				for (int j = 0; j < 8; j++)
				{
					if (i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1)
					{
						offScreenBuffer.drawImage(redKing, i * 75, j * 75, null);
					}
					else
					{
						offScreenBuffer.drawImage(redPawn, i * 75, j * 75, null);
					}
				}
			}
			offScreenBuffer.drawImage(redWins, 225, 200, null);
		}
		else if (gameState == 5) // draw tie game screen 
		{
			for (int i = 0; i < 11; i++)
			{
				for (int j = 0; j < 8; j++)
				{
					if (i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1)
					{
						offScreenBuffer.drawImage(blackKing, i * 75, j * 75, null);
					}
					else
					{
						offScreenBuffer.drawImage(redKing, i * 75, j * 75, null);
					}
				}
			}
			offScreenBuffer.drawImage(draw, 225, 200, null);
		}
		else if (gameState == 6) // rules pop-up
		{
			offScreenBuffer.drawImage(rules, 74, 75, null);
		}
		else if (gameState == 8) // rules pop-up during menu screen
		{
			offScreenBuffer.drawImage(rules, 175, 75, null);
		}
		
		if (playingSaved == true && (gameState == 2 || gameState == 3 || gameState == 5))
		// if a previously saved game has been completed (win or draw has occurred), then update the gameSaved variable to false since saved game is over 
		{
			playingSaved = false;
			gameSaved = false;
			try 
			{
				inputGameSaved(); 
			} 
			catch (IOException e1) 
			{
				System.out.println("inputGameSaved did not work");
			}
		}
		
		g.drawImage(offScreenImage, 0, 0, this);
	}
	
	// Not my code, given by Ms. Wong
	// ------------------------------
	public void update (Graphics g)
	{
		paint(g);
	}
	// -----------------------------
	
	// The following method updates the board by updating 'grid' based on what move the user made
	// The board will update by matching the following information and their relationship:
	// 1. Who's turn it is, 2. The piece's location, 3. The user's click location
	// This method will check if the following information ended up being a valid move such as 
	// a capture or a simple diagonal move
	public static int[][] boardUpdater(int x, int y, int pieceX, int pieceY, int[][] grid)
	{
		// Local Variables
		int[][] outputArr = grid; 
		
		// boardUpdater Body
		if (Math.abs(x - pieceX) == 2 && Math.abs(y - pieceY) == 2) // check for capture for both pawns and kings
		{
			outputArr = capture(x, y, pieceX, pieceY, grid);
		}	
		else if (Math.abs(selectedPiece) == 1 && consecutiveCapture == false) // check for one square pawn moves
		{
			outputArr = pawn(x, y, pieceX, pieceY, grid);
		}
		else if (Math.abs(selectedPiece) == 2 && consecutiveCapture == false) // check for one square king moves
		{
			outputArr = king(x, y, pieceX, pieceY, grid);
		}
		return outputArr; 
	}
	
	// Helper method for boardUpdater
	// Whenever the selected piece is a pawn, this will be the method checking if the user made
	// a valid move for the pawn's mechanics 
	// In this method, the pawn can also be promoted to a king when necessary 
	public static int[][] pawn(int x, int y, int pieceX, int pieceY, int[][] grid)
	{
		// Local Variables
		int[][] outputArr = grid; 
		
		// pawn Body 
		if (x == pieceX - 1 && y == pieceY + selectedPiece && outputArr[y][x] == 0) 
		// pawn move left diagonal (from the side's pov)
		{
			outputArr[pieceY][pieceX] = 0;
			outputArr[y][x] = promotionCheck(y, selectedPiece);
			selectedPiece *= -1; // guaranteed to switch turns 
 		}
		else if (x == pieceX + 1 && y == pieceY + selectedPiece && outputArr[y][x] == 0) 
		// pawn move right diagonal (from the side's pov) 
		{
			outputArr[pieceY][pieceX] = 0;
			outputArr[y][x] = promotionCheck(y, selectedPiece);
			selectedPiece *= -1; // guaranteed to switch turns 
 		}
		return outputArr; 
	}
	
	// Helper method for boardUpdater
	// Whenever the selected piece is a king, this will be the method checking if the user made 
	// a valid move for the king's mechanics
	public static int[][] king(int x, int y, int pieceX, int pieceY, int[][] grid)
	{
		// Local Variables
		int[][] outputArr = grid; 
		
		// king Body
		if (outputArr[y][x] == 0 && Math.abs(x - pieceX) == 1 && Math.abs(y - pieceY) == 1) 
		{
			outputArr[pieceY][pieceX] = 0;
			outputArr[y][x] = selectedPiece; 
			selectedPiece *= -1; // guaranteed to switch turns 
	 	}
		return outputArr; 
	}
	
	// Helper method for boardUpdater 
	// Will check if the user's click after the selecting piece click classifies as a capture
	// Needs to check: 
	// If it jumps over opposite side's piece
	// If the square it's jumping to is occupied
	// If another capture is available after
		// If so, the selectedPiece variable will not change
		// Otherwise, the selectedPiece will multiply by -1 to indicate it's the other side's turn
	public static int[][] capture(int x, int y, int pieceX, int pieceY, int[][] grid)
	{
		// Local Variables
		int[][] outputArr = grid; 
		
		// capture Body 
		if (grid[y][x] != 0) 
		{
			// when the selected square is not empty
			// make no changes to grid since "capture" is not valid
			return outputArr; 
		}
		
		if (pieceX > x && pieceY > y)
		{
			if (outputArr[pieceY - 1][pieceX - 1] * outputArr[pieceY][pieceX] >= 0 || selectedPiece == 1) 
			// if the piece is jumping over nothing (ie not capturing anything)
			// or if the piece is jumping over a piece of the same colour
			// or if the piece is a pawn capturing backwards 
				// The capture is not valid so we make no changes to grid 
			{
				return outputArr;
			}
			outputArr[pieceY - 1][pieceX - 1] = 0;
		}
		else if (pieceX < x && pieceY > y)
		{
			if (outputArr[pieceY - 1][pieceX + 1] * outputArr[pieceY][pieceX] >= 0 || selectedPiece == 1)
			{
				return outputArr;
			}
			outputArr[pieceY - 1][pieceX + 1] = 0;
		}
		else if (pieceX > x && pieceY < y)
		{
			if (outputArr[pieceY + 1][pieceX - 1] * outputArr[pieceY][pieceX] >= 0 || selectedPiece == -1)
			{
				return outputArr;
			}
			outputArr[pieceY + 1][pieceX - 1] = 0;
		}
		else if (pieceX < x && pieceY < y)
		{
			if (outputArr[pieceY + 1][pieceX + 1] * outputArr[pieceY][pieceX] >= 0 || selectedPiece == -1)
			{
				return outputArr;
			}
			outputArr[pieceY + 1][pieceX + 1] = 0;
		}
		
		if (Math.abs(selectedPiece) == 1) // only pawns have the potential to promote
		{
			outputArr[y][x] = promotionCheck(y, selectedPiece);
		}
		else
		{
			outputArr[y][x] = selectedPiece; 
			// This is so that king pieces that captured something don't go into the promotionCheck method
		}
		outputArr[pieceY][pieceX] = 0; 

		selectedPiece = outputArr[y][x]; // this line covers the case where a pawn promotes to a king, and then a consec capture is available
		selectedPiece = anotherCapture(x, y, outputArr); 
		// selectedPiece stays the same if consecutive capture is available
		
		return outputArr; 
	}
	
	// anotherCapture is a helper method for the method capture
	// It checks if a consecutive capture is possible 
	// If it changes the sign (positive or negative) of selectedPiece, it is the next opponents turn so there is
		// no consecutive capture
	// If selectedPiece does not change, there is another capture available so the user retains his/her turn
	// BUG TO FIX: Make sure the consecutive move allowed is a capture by the same piece 
	public static int anotherCapture(int x, int y, int[][] grid)
	{
		if (y - 2 >= 0 && x - 2 >= 0 && grid[y - 1][x - 1] * selectedPiece < 0 && grid[y - 2][x - 2] == 0 && selectedPiece != 1)
		// make sure potential captures are within the board coordinates
		{
			consecutiveCapture = true; 
			pieceX = x;
			pieceY = y;
			return selectedPiece; 
		}
		
		else if (y + 2 <= 7 && x - 2 >= 0 && grid[y + 1][x - 1] * selectedPiece < 0 && grid[y + 2][x - 2] == 0 && selectedPiece != -1)
		{
			consecutiveCapture = true;
			pieceX = x;
			pieceY = y;
			return selectedPiece;
		}
		else if (y - 2 >= 0 && x + 2 <= 7 && grid[y - 1][x + 1] * selectedPiece < 0 && grid[y - 2][x + 2] == 0 && selectedPiece != 1)
		{
			consecutiveCapture = true; 
			pieceX = x;
			pieceY = y;
			return selectedPiece;
		}
		else if (y + 2 <= 7 && x + 2 <= 7 && grid[y + 1][x + 1] * selectedPiece < 0 && grid[y + 2][x + 2] == 0 && selectedPiece != -1)
		{
			consecutiveCapture = true; 
			pieceX = x;
			pieceY = y;
			return selectedPiece;
		}
		consecutiveCapture = false; 
		System.out.println("Updated consec capture to false");
		return selectedPiece *= -1; 
	}
	
	// The following method will check if a piece has promoted or not
		// It does this by checking if a piece has reached the end of it's respective side (row 0 or row 7) 
	// If the piece has been promoted, then it will update it as a king 
	public static int promotionCheck(int y, int finalPiece)
	{
		if (y == 0 && finalPiece < 0 || y == 7 && finalPiece > 0)
		{
			finalPiece *= 2; 
		}
		return finalPiece; 
	}
	
	// The following method determines if the user has clicked on his/her piece
	// When the variable 'moving' is true, it indicates that the next click the 
	// user makes, is where he/she intends the piece to go to 
	public static boolean pieceClicked(int[][] grid, int x, int y)
	{
		// Local Variables
		boolean moving = false;
		
		// pieceClicked Body 
		if (selectedPiece < 0 && grid[y][x] < 0)
		{
			moving = true; 
		}
		else if (selectedPiece > 0 && grid[y][x] > 0)
		{
			moving = true; 
		}
		return moving; 
	}
	
	// The following method is a helper method for hasMove
	// Checks if a specified piece on the board can capture 
	public static boolean hasCapture(int x, int y, int piece)
	{
		// Method body
		if (y - 2 >= 0 && x - 2 >= 0 && grid[y - 1][x - 1] * piece < 0 && grid[y - 2][x - 2] == 0 && piece != 1)
			// make sure potential captures are within the board coordinates
		{
			return true;
		}
		else if (y + 2 <= 7 && x - 2 >= 0 && grid[y + 1][x - 1] * piece < 0 && grid[y + 2][x - 2] == 0 && piece != -1)
		{
			return true;
		}
		else if (y - 2 >= 0 && x + 2 <= 7 && grid[y - 1][x + 1] * piece < 0 && grid[y - 2][x + 2] == 0 && piece != 1)
		{
			return true;
		}
		else if (y + 2 <= 7 && x + 2 <= 7 && grid[y + 1][x + 1] * piece < 0 && grid[y + 2][x + 2] == 0 && piece != -1)
		{
			return true;
		}
		return false;
	}
	
	// The following method is a helper method for hasMove 
	// Checks if a specified pawn on the board can make a generic pawn move
	public static boolean hasPawnMove(int x, int y, int piece)
	{
		if (piece < 0 && y - 1 >= 0 && x - 1 >= 0 && grid[y - 1][x - 1] == 0)
			// make sure we are checking the forward moves for a pawn
			// make sure we are not checking a coordinate that is out of range of the board coordinates
			// check if there is a vacant square for a certain pawn move 
		{
			return true;
		}
		else if (piece > 0 && y + 1 <= 7 && x - 1 >= 0 && grid[y + 1][x - 1] == 0)
		{
			return true;
		}
		else if (piece < 0 && y - 1 >= 0 && x + 1 <= 7 && grid[y - 1][x + 1] == 0)
		{
			return true;
		}
		else if (piece > 0 && y + 1 <= 7 && x + 1 <= 7 && grid[y + 1][x + 1] == 0)
		{
			return true;
		}
		return false;
	}
	
	// The following method is a helper method for hasMove
	// Checks if a specified king on the board can make a generic king move
	public static boolean hasKingMove(int x, int y, int piece)
	{
		if (y - 1 >= 0 && x - 1 >= 0 && grid[y - 1][x - 1] == 0)
			// make sure we are not checking a coordinate that is out of range of the board coordinates
			// check if there is a vacant square for a certain king move 
		{
			return true;
		}
		else if (y + 1 <= 7 && x - 1 >= 0 && grid[y + 1][x - 1] == 0)
		{
			return true;
		}
		else if (y - 1 >= 0 && x + 1 <= 7 && grid[y - 1][x + 1] == 0)
		{
			return true;
		}
		else if (y + 1 <= 7 && x + 1 <= 7 && grid[y + 1][x + 1] == 0)
		{
			return true;
		}
		return false;
	}
	
	// The following method checks every red or black piece on the board to see if a valid move is available
	// If there are no more available moves, the method returns false, and the user will lose
	public static boolean hasMove(int grid[][], int turn)
	{
		for(int i = 0; i < 8; i++)
			// j is rows, i is columns 
		{
			for(int j = 0; j < 8; j++)
			{
				if(grid[j][i] * turn == 1)
					// same sign results in positive number
					// if the positive number happens to be 1, then it's a pawn 
				{
					if (hasCapture(i, j, grid[j][i]) == true)
					{
						return true;
					}
					else if (hasPawnMove(i, j, grid[j][i]) == true)
					{
						return true;
					}
				}
				else if(grid[j][i] * turn == 2)
				{
					if (hasCapture(i, j, grid[j][i]) == true)
					{
						return true;
					}
					else if (hasKingMove(i, j, grid[j][i]) == true)
					{
						return true;
					}
				}
			}
		}
		return false; 
	}
	
	// The following method inputs information into a text file to store information
	// regarding the middle of a game
	public static void inputSaved() throws IOException
	{
		// Local Variables
		PrintWriter outputFile = new PrintWriter(new FileWriter("savedGame.txt"));
		
		// inputSaved Body
		for (int i = 0; i < 8; i ++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (j == 7)
				{
					outputFile.println(grid[i][j] + " ");
				}
				else
				{
					outputFile.print(grid[i][j] + " ");
				}
			}
		}
		outputFile.print(selectedPiece);
		outputFile.close();
	}
	
	// The following method extracts information from the text file savedGame
	// In order to display the saved game on the game screen 
	public static void outputSaved() throws IOException
	{
		// Local Variables
		Scanner inputFile = new Scanner(new File("savedGame.txt"));
		
		// inputSaved Body
		for (int i = 0; i < 8; i ++) // copies the board information for every single coordinate
		{
			for (int j = 0; j < 8; j++)
			{
//				cur = inputFile.next();
				grid[i][j] = Integer.parseInt(inputFile.next()); 
			}
		}
		selectedPiece = Integer.parseInt(inputFile.next());
		inputFile.close();
	}
	
	// The following method extracts the value for the gameSaved variable 
	public static void extractGameSaved() throws IOException
	{
		// Local Variables
		Scanner inputFile = new Scanner(new File("gameSaved.txt"));		
		
		// extractGameSaved Body
		gameSaved = Boolean.parseBoolean(inputFile.next());
		inputFile.close();
	}
	
	// The following inputs the value for the gameSaved variable onto another text file 
	// for when the program is run again
	public static void inputGameSaved() throws IOException
	{
		// Local Variables
		PrintWriter outputFile = new PrintWriter(new FileWriter("gameSaved.txt"));
		
		// inputGameSaved Body
		outputFile.print(gameSaved);
		outputFile.close();
	}
	
	public static void main(String[] args) 
	{
		JFrame frame = new JFrame("Checkers!");
		Checkers panel = new Checkers();
		frame.add(panel);
		frame.pack(); 
		frame.setVisible(true);
		
		// Extracting the value for savedGame
		try 
		{
			extractGameSaved();
		} 
		catch (IOException e1) 
		{
			System.out.println("extractGameSaved did not work");
		}
	}
	
	public void mousePressed(MouseEvent e) 
	{	
		posX = e.getX(); 
		posY = e.getY(); 
		System.out.println(posX + " " + posY);
		
		if (gameState == 0) // menu screen
		{
			if (posX >= 255 && posX <= 547 && posY >= 186 && posY <= 221) // new game
			{
				gameState = 1;
				playingSaved = false;
				for (int i = 0; i < 8; i++) // re-making the original board set up information to the grid variable because a new game has started 
				{
					for (int j = 0; j < 8; j++)
					{
						if (((i == 0 || i == 2) && j%2 == 1) || (i == 1 && j%2 == 0))
						{
							grid[i][j] = 1; 
						}
						else if (((i == 5 || i == 7) && j%2 == 0) || (i == 6 && j%2 == 1))
						{
							grid[i][j] = -1; 
						}
						else 
						{
							grid[i][j] = 0;
						}
					}
				}
				selectedPiece = -1; // black always starts first
				paintComponent(this.getGraphics());
			}
			else if (posX >= 152 && posX <= 651 && posY >= 299 && posY <= 334 && gameSaved == true) 
				// Continue game, only works if a game has been saved before and has not ended
			{
				System.out.println("Continue Game");
				gameState = 1;
				try 
				{
					playingSaved = true;
					outputSaved();
				} 
				catch (IOException e1) 
				{
					System.out.println("outputSaved did not work");
				}
				paintComponent(this.getGraphics());
			}
			else if (posX >= 309 && posX <= 492 && posY >= 423 && posY <= 459) // Rules pop-up
			{
				System.out.println("Rules");
				gameState = 8; 
				paintComponent(this.getGraphics());
			}
		}
		else if (gameState == 1) // user has clicked one something while playing 
		{
			// ---------------------------------------------------------------------------------------------------------------------
			// When the user clicks on the side bar
			if (posX > 600)
			{
				if (posX >= 650 && posX <= 753 && posY >= 256 && posY <= 322) // offer draw 
				{
					gameState = 4; 
					System.out.println("Offer Draw");
					paintComponent(this.getGraphics());
					return;
				}
				else if (posX >= 638 && posX <= 760 && posY >= 358 && posY <= 389) // resign
				{
					gameState = 7; 
					System.out.println("Resign");
					paintComponent(this.getGraphics());
					return;
				}
				else if (posX >= 643 && posX <= 756 && posY >= 427 && posY <= 462) // rules
				{
					gameState = 6; 
					System.out.println("Rules");
					paintComponent(this.getGraphics());
					return;
				}
				else if (posX >= 651 && posX <= 752 && posY >= 505 && posY <= 577) // save game
				{
					System.out.println("Save Game");
					try 
					{
						gameSaved = true; 
						inputSaved();
					} 
					catch (IOException e1) 
					{
						System.out.println("Went to catch");
					}
					
					try 
					{
						inputGameSaved();
					} 
					catch (IOException e1) 
					{
						System.out.println("inputGameSaved did not work");
					}
					gameState = 0;
					System.out.println("Reached, epic!");
					paintComponent(this.getGraphics());
					return;
				}
				else
				{
					return; 
					// to avoid out of bounds error when the game uses coordinates out of the board thinking the user is "playing" 
				}
			}
			
			// ---------------------------------------------------------------------------------------------------------------------
			// When the user clicks on the board
			if (moving == false)
			{
				moving = pieceClicked(grid, posX/75, posY/75); 
				
//				System.out.println(moving);
				
				if (moving == true)
				{
					selectedPiece = grid[posY/75][posX/75];
					pieceX = posX/75;
					pieceY = posY/75;
					paintComponent(this.getGraphics());
				}
			}
			else if (moving == true)
			{
				grid = boardUpdater(posX/75, posY/75, pieceX, pieceY, grid); 
				if (consecutiveCapture == true)
				{
					moving = true;
				}
				else
				{
					moving = false;
				}
				
				if (hasMove(grid, -1) == false && selectedPiece < 0)
				{
					gameState = 3;
					System.out.println("RED WINS");
				}
				else if (hasMove(grid, 1) == false && selectedPiece > 0)
				{
					gameState = 2;
					System.out.println("BLACK WINS");
				}
				paintComponent(this.getGraphics()); 
				// After the grid variable is updated, how we actually update the board is through paint component
				// When it redraws the whole board based off what is in grid 
			}
			// ---------------------------------------------------------------------------------------------------------------------
		}
		else if (gameState == 2 || gameState == 3) // User is currently on a win screen 
		{
			if (posX >= 232 && posX <= 365 && posY >= 333 && posY <= 377) // wants to go to menu
			{
				gameState = 0; 
				paintComponent(this.getGraphics());
			}
			else if (posX >= 447 && posX <= 560 && posY >= 333 && posY <= 377) // wants to close program
			{
				try 
				{
					inputGameSaved(); // need to update gameSaved variable to text file before closing program
				} 
				catch (IOException e1) 
				{
					System.out.println("inputGameSaved did not work");
				}
				System.exit(0);
			}
		}
		else if (gameState == 4) // User is currently on offer draw pop-up
		{
			if (posX >= 143 && posX <= 263 && posY >= 345 && posY <= 403) // Draw has been agreed by both players
			{
				gameState = 5; 
				paintComponent(this.getGraphics());
			}
			else if (posX >= 354 && posX <= 443 && posY >= 345 && posY <= 407) // Draw rejected, game continues
			{
				gameState = 1; 
				paintComponent(this.getGraphics());
			}
		}
		else if (gameState == 5) // Game has been tied at this point
		{
			if (posX >= 236 && posX <= 384 && posY >= 353 && posY <= 403) // User want to go back to menu
			{
				gameState = 0; 
				paintComponent(this.getGraphics());
			}
			else if (posX >= 474 && posX <= 596 && posY >= 351 && posY <= 401) // User wants to close program 
			{
				try 
				{
					inputGameSaved(); 
				} 
				catch (IOException e1) 
				{
					System.out.println("inputGameSaved did not work");
				}
				System.exit(0);
			}
		}
		else if (gameState == 6) // Currently on rules pop-up
		{
			if (posX >= 258 && posX <= 483 && posY >= 341 && posY <= 513)
			{
				gameState = 1; 
				paintComponent(this.getGraphics());
			}
		}
		else if (gameState == 7) // Currently on resign pop-up
		{
			if (posX >= 134 && posX <= 260 && posY >= 349 && posY <= 413) // will resign
			{
				if (selectedPiece > 0)
				{
					gameState = 2;
				}
				else
				{
					gameState = 3;
				}
				paintComponent(this.getGraphics());
			}
			else if (posX >= 359 && posX <= 453 && posY >= 349 && posY <= 413) // will continue game 
			{
				gameState = 1;
				paintComponent(this.getGraphics());
			}
		}
		else if (gameState == 8) // On resign pop-up from menu
		{
			if (posX >= 360 && posX <= 441 && posY >= 483 && posY <= 513)
			{
				gameState = 0; 
				paintComponent(this.getGraphics());
			}
		}
	}
	
	// Useless Methods 
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
