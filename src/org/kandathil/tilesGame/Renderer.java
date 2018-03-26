package org.kandathil.tilesGame;
import javax.swing.JPanel;
import java.awt.Graphics;

@SuppressWarnings("serial") //tell compiler not to give serialversionuid warning (suppresses/ignores compiler warning
public class Renderer extends JPanel
{
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g); //create instance of paintComponent for superclass
		
		if (Tiles.game != null)
		{
			Tiles.game.render(g);
		}
		
		
	}
}
