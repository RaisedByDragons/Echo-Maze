package levels;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Tile {

    // The image that represents this tile
    public BufferedImage image;

    // Whether this tile blocks movement (true = solid)
    public boolean collision = false;

    // Array to hold multiple types of tiles (e.g., floor, wall, etc.)
    private Tile[] tile = new Tile[10];

    // Builds and returns an array of Tile objects with their images and collision settings
    public Tile[] buildTileInfo() {
        try {
            // Tile 0: Black background (solid wall)
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/BlackBack.png"));
            tile[0].collision = true;

            // Tile 1: White background (walkable floor)
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WhiteBack.png"));

            // Tile 2: Gray background (possibly used for variation)
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/GrayBack.png"));
            
            // Tile 3: Blue background (could represent a special zone or visual cue)
            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/BlueBack.png"));
            
            // Return the completed tile array
            return tile;

        } catch (IOException e) {
            // Print an error if image loading fails
            e.printStackTrace();
        }
        // Return null if tile loading fails
        return null;
    }

    // Returns this tile's image
    public BufferedImage getImage() {
        return image;
    }

    // Returns the tile from the array at the specified index
    public Tile getTile(int value) {
        return tile[value];
    }
}
