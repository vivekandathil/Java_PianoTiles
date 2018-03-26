package org.kandathil.tilesGame;

//Class to animate Tiles
public class IndividualTile 
{
		public int x, y;
		
		public int animateY;
		public boolean black;
		
		public IndividualTile(int x, int y, boolean black) 
		{
			this.x = x;
			this.y = y;
			this.black = black;
		}

		public boolean inTile(int x, int y)
		{
			int width = Tiles.TILE_WIDTH;
			int height = Tiles.TILE_HEIGHT;
			return x > this.x * width && x < this.x * width + width && y > this.y * height && y < this.y * height + height;
		}


}
