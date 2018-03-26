package org.kandathil.tilesGame;

//To have text of different fonts/colours in "You Lose" screen
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

//Event Listeners (Tile Clicks, Mouse Down, etc.)
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//JFrame
import javax.swing.JFrame;
import javax.swing.Timer;

import java.util.ArrayList; //Like a regular array, but can be resized
import java.util.Collections;
import java.util.Random;

//Playing Piano MIDI notes
import jm.music.data.Note;
import jm.util.Play;

//Multithreading (for a later version, will hopefull cause less lag by using a separate thread to generate audio)
//import java.lang.Runnable;

public class Tiles implements ActionListener, MouseListener //implement listener interfaces
{
	//set tile sizes for game
	public final static int COLUMNS = 3, ROWS = 3, TILE_WIDTH = 250, TILE_HEIGHT = 300;
	public static Tiles game;
	public ArrayList<IndividualTile> tiles; //Array List of Individual Tiles
	public static ArrayList<Integer> scoreList = new ArrayList<Integer>(); //Array List to store scores
	public Random random;
	public Renderer renderer;
	
	//Score is added to with a higher value depending on the timer delay
	public static int score, milSecDelay;
	public boolean gameOver, scoreWritten;
	public String highscore = "";	 

	//constructor for Tiles
	public Tiles()
	{
		//create new instance of JFrame
		JFrame frame = new JFrame("Java Piano Tiles");
		Timer timer = new Timer(20, this); //create a new timer object to tick every 20 seconds
		
		//tiles = new ArrayList<IndividualTile>(); //initialize ArrayList tiles
		renderer = new Renderer();
		random = new Random();
		
		//set dimensions of JFrame based on tile sizes
		frame.setSize(TILE_WIDTH * COLUMNS, TILE_HEIGHT * ROWS);
		frame.add(renderer);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addMouseListener(this);
		frame.setResizable(false);

		start();
		
		timer.start(); //start the timer
	}
	
	public void start()
	{
		score = 0;
		gameOver = false;
		tiles = new ArrayList<IndividualTile>();
		
		for (int x = 0; x < COLUMNS; x++)
		{
			for (int y = 0; y < ROWS; y++)
			{
				boolean canBeBlack = true;
				
				for (IndividualTile tile : tiles) //For each individual tile in Array list tiles....
				{
					//if the tile position of the tile is already black, it cannot be black
					if (tile.y == y && tile.black)
					{
						canBeBlack = false;
					}
				}
				
				if (!canBeBlack) //if it cannot be black
				{
					tiles.add(new IndividualTile(x, y, false));
				}
				else
				{
					tiles.add(new IndividualTile(x, y, random.nextInt(3) == 0 || x == 2)); //if random number is equal to zero or 2, it can be black
				}
				
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		renderer.repaint();
		
		for (int i = 0; i < tiles.size(); i++)
		{
			IndividualTile tile = tiles.get(i);
			
			if (tile.animateY < 0)
			{
				tile.animateY+= TILE_HEIGHT / 5;
			}
		}
		
		milSecDelay++;
		
	}
	
	public void render(Graphics g) 
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, TILE_WIDTH * COLUMNS, TILE_HEIGHT * ROWS);
		g.setFont(new Font("Coffee House", 1, 100));
		
		if (!gameOver)
		{
			for (IndividualTile tile : tiles) //for each loop: "For each individual tile in the array list of tiles, do this"
			{
				//if-statements can be written in the form "boolean statement ? true result : false result;". Basically, this means "if tile colour is black, set it to white, else set it to black"
				g.setColor(tile.black ? Color.BLACK : Color.WHITE);

				//use graphics library to draw outline of black rectangle with defined height and width
				g.fillRect(tile.x * TILE_WIDTH, tile.y * TILE_HEIGHT + tile.animateY, TILE_WIDTH, TILE_HEIGHT);
				g.setColor(tile.black ? Color.WHITE : Color.BLACK);
				g.drawRect(tile.x * TILE_WIDTH, tile.y * TILE_HEIGHT + tile.animateY, TILE_WIDTH, TILE_HEIGHT);

				//use graphics library to fill in the black rectangle with width and height defined by the aforementioned variables
				//use "- tile.animateY" because when the user clicks a tile, the tiles have to animate downward (falling)
			}			
			g.setColor(Color.RED);
			
			g.drawString(String.valueOf(score), TILE_WIDTH + TILE_WIDTH / 2, 100);
		}
		else
		{
			String scoreStatement = "You scored " + Integer.toString(score) + " points";
			g.setColor(Color.BLACK);
			g.drawString("You lose", 200, TILE_HEIGHT);
			
			//colour/font for score statement
			g.setFont(new Font ("Coffee House", 1, 80));
			g.setColor(Color.GREEN);
			g.drawString(scoreStatement, TILE_WIDTH / 2, TILE_HEIGHT + 300);
			
			//colour/font for highscore
			g.setFont(new Font ("Coffee House", 1, 80));
			g.setColor(Color.RED);
			g.drawString(highscore, TILE_WIDTH / 2, TILE_HEIGHT + 400);
			
		}
		
		//Only run once the game has ended and score has not yet been written
		if (gameOver && !scoreWritten)
		{
			game.writeScore();
			System.out.println(scoreList);
			System.out.println("High score is: " + Collections.max(scoreList));
		}
	}
	
	public static void main(String[] args) 
	{
		game = new Tiles();
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
	}
	
	//Event Listener to detect when mouse is pressed down.
	//Make tile disappear, shift tiles downward and have a smooth animation
	@Override
	public void mousePressed(MouseEvent e) 
	{
		boolean clicked = false; //only animate if black tile is clicked
		
		if (!gameOver)
		{
			scoreWritten = false;
			for (int i = 0; i < tiles.size(); i++)
			{
				IndividualTile tile = tiles.get(i);
				
				//***MOVING AND REMOVING TILES***
				if (e.getY() > TILE_HEIGHT * (ROWS - 1))
				{
					if (tile.inTile(e.getX(), e.getY()) && !clicked)
					{
						if (tile.black)
						{
							for (int j = 0; j < tiles.size(); j++)
							{
								if (tiles.get(j).y == ROWS)
								{
									tiles.remove(j);
								}

								tiles.get(j).y++;
								tiles.get(j).animateY -= TILE_HEIGHT;
							}
							
							//Have a delay so that if a user clicks faster, he/she gains more points
							score += Math.max(100 - milSecDelay, 10);
							
							Tiles.randomPianoNote();
							
							//Test score statement in console
							System.out.println("You've scored: " + Math.max(100 - milSecDelay, 10) + " points!"); 

							milSecDelay = 0;

							boolean canBeBlack = true;

							for (int x = 0; x < COLUMNS; x++)
							{
								boolean black = random.nextInt(4) == 0 || x == COLUMNS - 1;

								IndividualTile newTile = null;

								if (canBeBlack && black)
								{
									newTile = new IndividualTile(x, 0, true);
									canBeBlack = false;
								}
								else
								{
									newTile = new IndividualTile(x, 0, false);
								}

								newTile.animateY -= TILE_HEIGHT;

								tiles.add(newTile);	
							}
						}
						else
						{
							gameOver = true;
						}

						clicked = true;
				}

				}				
				else
				{
					gameOver = true;
				}
			}
		}
		else
		{
			start();
		}

	}

	//These Mouse Events are not currently being used
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		
		
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		
	}
	
	public void writeScore() //Keeps track of all scores for current session
	{
		
		scoreList.add(score);
		//Collections.max finds the highest value in the ArrayList of scores
		highscore = "Highscore is " + Integer.toString(Collections.max(scoreList));
		scoreWritten = true;
		
	}
	
	//generate a random MIDI note and play it
	public static void randomPianoNote() 
	{
		Random randomNote = new Random();
		//Generates a random MIDI note within a good range of audible pitches
		int randomFrequency = randomNote.nextInt(90) + 10; 
		
		Note pianoNote = new Note();
		pianoNote.setPitch(randomFrequency);
		pianoNote.setLength(1);
		
		Play.midi(pianoNote);
	}

}
