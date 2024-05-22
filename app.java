import javax.swing.JFrame;
import panels.gamepanel;
import panels.mainmenu;
import util.sounds;
import java.awt.event.WindowEvent;

/**
 * The app class represents the main application window for the game "Urban Fury".
 */
public class app extends JFrame {
   private gamepanel gamepanel = new mainmenu();

   /**
    * Constructs the main application window.
    */
   public app() {
      super("Urban Fury");

      setExtendedState(JFrame.MAXIMIZED_BOTH); 
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      
      setContentPane(gamepanel);
      setVisible(true);
      gamepanel.requestFocus();
      
      sounds.initialize();
      
      runGameLoop();
   }

   /**
    * Runs the main game loop in a separate thread.
    */
   private void runGameLoop() {
      JFrame temp = this;
      Thread game = new Thread() {
         @Override
         public void run() {
            gameLoop();
            dispose();
            dispatchEvent(new WindowEvent(temp, WindowEvent.WINDOW_CLOSING));
         }
      };
      game.start();
   }

   /**
    * The main game loop which handles game updates and rendering.
    */
   private void gameLoop() {
      final int targetUpdate = 60;
      final long targetUpdateTime = 1000000000 / targetUpdate;
      final int targetFPS = 60;
      final long targetFrametime = 1000000000 / targetFPS;
      long prevFrameTime = System.nanoTime();
      long accumulator = 0;

      int updates = 0;
      long runningUpdateTime = 0;
      long prevUpdateTime = prevFrameTime;

      int frames = 0;
      long runningFrameTime = 0;
      while (gamepanel.isRunning()) {
         gamepanel tempPanel = gamepanel.moveNextLevel();
         if (tempPanel != null) {
            remove(gamepanel);
            gamepanel = tempPanel;
            getContentPane().invalidate();
            setContentPane(gamepanel);
            getContentPane().revalidate();
            gamepanel.requestFocus();
         }

         double interpolation = 0;
         long now = System.nanoTime();
         accumulator += now - prevFrameTime;
         runningFrameTime += now - prevFrameTime;
         frames++;
         while (accumulator > targetUpdateTime) {
            if (!gamepanel.isPaused())
               gamepanel.update();
            accumulator -= targetUpdateTime;
            runningUpdateTime += System.nanoTime() - prevUpdateTime;
            updates++;
            prevUpdateTime = System.nanoTime();
         }
         interpolation = (double) (System.nanoTime() - prevFrameTime) / targetUpdateTime;

         if (gamepanel.isPaused())
            interpolation = 0;
         if (runningUpdateTime >= 1000000000) {
            gamepanel.setgameupdates(updates);
            updates = 0;
            runningUpdateTime = 0;
         }
         if (runningFrameTime >= 1000000000) {
            gamepanel.setFps(frames);
            frames = 0;
            runningFrameTime = 0;
         }
         gamepanel.render(interpolation);
         prevFrameTime = now;
         while (now - prevFrameTime < targetFrametime && now - prevFrameTime < targetUpdateTime) {
            Thread.yield();
            now = System.nanoTime();
         }
      }
   }

   /**
    * The entry point of the application.
    *
    * @param arg Command-line arguments (not used).
    */
   public static void main(String[] arg) {
      new app();
   }
}
