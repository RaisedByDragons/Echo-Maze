package levels;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile {

	public BufferedImage image;
	public boolean collision = false;
	private Tile[] tile = new Tile[10];
	
	public Tile[] buildTileInfo() {
		
    	try {
    		tile[0] = new Tile();
    		tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/BlackBack.png"));
    		tile[0].collision = true;
    		
    		tile[1] = new Tile();
    		tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WhiteBack.png"));

    		tile[2] = new Tile();
    		tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/GrayBack.png"));
    		
    		tile[3] = new Tile();
    		tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/BlueBack.png"));
    		
    		return tile;
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return null;
    }
	
	public BufferedImage getImage() {
		return image;
	}
	
	public Tile getTile(int value) {
		return tile[value];
	}
}
