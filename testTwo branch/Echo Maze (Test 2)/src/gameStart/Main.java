package gameStart;

import javax.swing.JFrame;
// Not used yet, but reserved for future use
// import com.sun.net.httpserver.*;
// import java.awt.image.BufferedImage;
// import java.io.*;
// import java.net.InetSocketAddress;
// import java.nio.file.Files;
// import javax.imageio.ImageIO;

/**
 * Main entry point for the Echo Maze game.
 * 
 * This class creates the main game window, attaches the GamePanel,
 * and starts the game loop. The commented-out section below shows
 * a planned alternative version that would also run a lightweight
 * HTTP server to display live game screenshots in a web browser.
 */
public class Main {
    
    public static void main(String[] args) {
        // Create the main application window
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit when closed
        window.setResizable(true); // Allow resizing
        window.setTitle("Echo Maze"); // Set window title
        
        // Create and attach the main game panel
        GamePanel gamePanel = new GamePanel();
        // gamePanel.setFocusable(true);      // TODO: Uncomment if input doesn't work on launch
        // gamePanel.requestFocusInWindow();  // Ensures focus for keyboard input
        window.add(gamePanel);
        
        // Automatically size the window based on the panel’s preferred size
        window.pack();
        
        // Center the window on the screen
        window.setLocationRelativeTo(null);
        window.setVisible(true); // Show the window
        
        // Start the main game loop thread
        gamePanel.startGameThread();
    }
    
    // =====================================================================
    // TODO: This alternative version of Main may be implemented later (or never)
    // =====================================================================
    // The code below shows a version that would start a small local web server
    // allowing the game to be viewed live through a web browser.
    //
    // It creates an HTTP server running on port 8080, serving an HTML page
    // that displays the game window as a live-updating PNG image refreshed
    // every second. The server automatically stops when the game window closes.
    //
    // public class Main {
    //     public static void main(String[] args) {
    //         HttpServer server;
    //         try {
    //             server = HttpServer.create(new InetSocketAddress(8080), 0);
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //             return;
    //         }
    //
    //         // Create the game window
    //         JFrame window = new JFrame("Echo Maze");
    //         window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //         window.setResizable(true);
    //
    //         GamePanel gamePanel = new GamePanel();
    //         window.add(gamePanel);
    //         window.pack();
    //         window.setLocationRelativeTo(null);
    //         window.setVisible(true);
    //         gamePanel.startGameThread();
    //
    //         // Create HTML landing page
    //         server.createContext("/", exchange -> {
    //             String response = """
    //                     <html>
    //                         <head>
    //                             <title>Echo Maze - Local View</title>
    //                             <script>
    //                                 function reloadImage() {
    //                                     const img = document.getElementById('live');
    //                                     img.src = '/screenshot?' + new Date().getTime(); // bust cache
    //                                 }
    //                                 setInterval(reloadImage, 1000); // refresh every 1 second
    //                             </script>
    //                         </head>
    //                         <body style="background-color:black; color:white; text-align:center;">
    //                             <h1>Echo Maze - Local Server</h1>
    //                             <p>Game is running locally on your computer.</p>
    //                             <p>Live screenshot:</p>
    //                             <img id="live" src="/screenshot" style="width:75%%; border:2px solid white;" />
    //                             <p>Image updates every second without refreshing the page.</p>
    //                         </body>
    //                     </html>
    //                 """;
    //             exchange.getResponseHeaders().set("Content-Type", "text/html");
    //             exchange.sendResponseHeaders(200, response.getBytes().length);
    //             try (OutputStream os = exchange.getResponseBody()) {
    //                 os.write(response.getBytes());
    //             }
    //         });
    //
    //         // Serve the latest screenshot
    //         server.createContext("/screenshot", exchange -> {
    //             try {
    //                 // Capture game frame as image
    //                 BufferedImage image = new BufferedImage(
    //                         gamePanel.getWidth(),
    //                         gamePanel.getHeight(),
    //                         BufferedImage.TYPE_INT_RGB);
    //                 gamePanel.paint(image.getGraphics());
    //
    //                 // Write to temporary file
    //                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //                 ImageIO.write(image, "png", baos);
    //                 
    //                 // Serve the image
    //                 byte[] bytes = baos.toByteArray();
    //                 exchange.getResponseHeaders().add("Content-Type", "image/png");
    //                 exchange.sendResponseHeaders(200, bytes.length);
    //                 try (OutputStream os = exchange.getResponseBody()) {
    //                     os.write(bytes);
    //                 }
    //
    //             } catch (Exception e) {
    //                 String err = "Error generating screenshot: " + e.getMessage();
    //                 exchange.sendResponseHeaders(500, err.length());
    //                 try (OutputStream os = exchange.getResponseBody()) {
    //                     os.write(err.getBytes());
    //                 }
    //             }
    //         });
    //
    //         server.start();
    //         System.out.println("Server running at http://localhost:8080/");
    //         System.out.println("Visit that URL in a browser to see the live screenshot.");
    //
    //         // Stop server cleanly when window closes
    //         window.addWindowListener(new java.awt.event.WindowAdapter() {
    //             @Override
    //             public void windowClosing(java.awt.event.WindowEvent e) {
    //                 System.out.println("Stopping local web server...");
    //                 server.stop(0);
    //             }
    //         });
    //     }
    // }
}
