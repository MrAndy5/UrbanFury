package panels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import tanklogic.*;
import util.sounds;
public abstract class gamepanel extends JPanel
{ 
   private boolean running=true,paused=true,nextLevelReady=false,hitScan=true;
   private int frame=1;
   protected int gameupdates,fps,timelimit=5000,hitAlpha=0, tanksdestroyed=0;
   protected gamepanel nextPanel;
   protected player p;
   protected ArrayList<enemytank> enemies=new ArrayList<enemytank>();
   protected ArrayList<shooting> bullets=new ArrayList<shooting>();
   protected String[][] mapa;
   protected final int INITIAL_ENEMIES=5;
   protected final String MOVE_UP="up"
                        ,MOVE_DOWN="down"
                        ,MOVE_LEFT="left"
                        ,MOVE_RIGHT="right"
                        ,STOP_VERTICAL="stopver"
                        ,STOP_HORIZONTAL="stophor";
    private Image backgroundImage;
   protected gamepanel()
   {
      setBackground(Color.GREEN.darker());
      setKeyBindings();
      addMouseListener(
         new MouseAdapter()
         {
            @Override
            public void mousePressed(MouseEvent e)
            {
               p.setShooting(true);
            }
            @Override
            public void mouseReleased(MouseEvent e)
            {
               p.setShooting(false);
            }
         });
      sounds.pickSong();
      try{
         backgroundImage=ImageIO.read(new File("resources/grass.png"));
      }
      catch(IOException e)
      {
         e.printStackTrace();}
      
   }
   
   /** 
    * @param key
    * @param name
    */
   protected void bindKey(String key, String name) {
      getInputMap().put(KeyStroke.getKeyStroke(key), name);
  }
  
  protected void bindAction(String name, AbstractAction action) {
      getActionMap().put(name, action);
  }
  
  protected void setKeyBindings() {
      bindKey("UP", MOVE_UP);
      bindKey("W", MOVE_UP);
      bindKey("DOWN", MOVE_DOWN);
      bindKey("S", MOVE_DOWN);
      bindKey("LEFT", MOVE_LEFT);
      bindKey("A", MOVE_LEFT);
      bindKey("RIGHT", MOVE_RIGHT);
      bindKey("D", MOVE_RIGHT);
  
      bindKey("released UP", STOP_VERTICAL);
      bindKey("released W", STOP_VERTICAL);
      bindKey("released DOWN", STOP_VERTICAL);
      bindKey("released S", STOP_VERTICAL);
      bindKey("released LEFT", STOP_HORIZONTAL);
      bindKey("released A", STOP_HORIZONTAL);
      bindKey("released RIGHT", STOP_HORIZONTAL);
      bindKey("released D", STOP_HORIZONTAL);
  
      bindAction(MOVE_UP, new MoveAction(0));
      bindAction(MOVE_RIGHT, new MoveAction(1));
      bindAction(MOVE_DOWN, new MoveAction(2));
      bindAction(MOVE_LEFT, new MoveAction(3));
      bindAction(STOP_VERTICAL, new MoveAction(4));
      bindAction(STOP_HORIZONTAL, new MoveAction(5));
  }
  
   protected void loadNextLevel()
   {
      Thread nextLevel=
         new Thread()
         {
            @Override
            public void run()
            {

               
               nextPanel=new urbanFury();

            }
         };
      nextLevel.start();
   }
   public boolean isRunning()
   {
      return running;
   }
   public boolean isPaused()
   {
      return paused;
   }
   protected boolean isNextReady()
   {
      return nextLevelReady;
   }
   protected void nextIsReady()
   {
      nextLevelReady=true;
   }
   protected void nextNotReady()
   {
      nextLevelReady=false;
   }
   protected void pause()
   {
      paused=!paused;
   }

   public void setgameupdates(int updates)
   {
      gameupdates=updates;
   }
   public void setFps(int frames)
   {
      fps=frames;
   }
   protected void checkCollisions() {
      for (int i = bullets.size() - 1; i >= 0; i--) {
          shooting bullet = bullets.get(i);
          for (int j = enemies.size() - 1; j >= 0; j--) {
              enemytank enemy = enemies.get(j);
              if (bullet.checkCollision(enemy)) {
                  bullets.remove(i);
                  enemy.hit();
                  if (!enemy.isAlive()) {
                        tanksdestroyed++;
                  }
                  sounds.damage();
                  break;
              }
          }
          if (bullet.checkCollision(p)) {
              bullets.remove(i);
              p.hit();
              if (!p.isAlive()) {
                  timelimit -= 1000;
                  hitAlpha = 100;
              }
              sounds.damage();
          }
      }

      for (enemytank enemy : enemies) {
          enemy.bump(p);
          p.bump(enemy);
          for (enemytank otherEnemy : enemies) {
              if (enemy != otherEnemy) {
                  enemy.bump(otherEnemy);
                  otherEnemy.bump(enemy);
              }
          }
      }
  }
  
   public synchronized void render(double interpolation)
   {
      p.interpolate(interpolation);
      for(enemytank enemy:enemies)
         enemy.interpolate(interpolation);
      for(shooting shooting:bullets)
         shooting.interpolate(interpolation);
      repaint();
   }
   public synchronized void update() {
      updateHitFlash();
  
      if (p.isAlive()) {
          updatePlayer();
      } else {
          updatePlayerExplosion();
      }
  
      updateEnemies();
  
      updateBullets();
  
      checkCollisions();
  
      sounds.setDistance(findDistance());
  }
  
  private void updateHitFlash() {
      hitAlpha -= 3;
  }
  
  private void updatePlayer() {
      p.update(this);
      shooting temp = p.shoot();
      if (temp != null) {
          bullets.add(temp);
      }
  }
  
  private void updatePlayerExplosion() {
      p.updateExplosion();
      if (p.checkDeathTimer()) {
          respawnPlayerAndEnemies();
          bullets.clear();
          startLevel();
      }
  }
  
  private void updateEnemies() {
      for (enemytank enemy : enemies) {
          if (enemy.isAlive()) {
              enemy.setplayer(p);
              enemy.setPath(enemy.findPath(mapa));
              enemy.update(this);
              shooting temp = enemy.shoot();
              if (temp != null) {
                  bullets.add(temp);
              }
          } else {
              enemy.updateExplosion();
              if (enemy.checkDeathTimer()) {
                  enemy.respawn();
              }
          }
      }
  }
  
  private void updateBullets() {
      for (int i = bullets.size() - 1; i >= 0; i--) {
          shooting bullet = bullets.get(i);
          bullet.update(this);
          if (!bullet.isAlive()) {
              bullets.remove(i);
          }
      }
  }
  
  private void respawnPlayerAndEnemies() {
      p.respawn();
      for (enemytank enemy : enemies) {
          enemy.respawn();
      }
  }
  

   private double findDistance()
   {
      double min=1000;
      for(int i=0;i<enemies.size();i++)
      {
         enemytank e=enemies.get(i);
         if(e.isAlive() && p.isAlive())
         {
            double temp=Math.sqrt(Math.pow(e.getCenterX()-p.getCenterX(),2)+Math.pow(e.getCenterY()-p.getCenterY(),2));
            if(temp<min)
               min=temp;
         }
      }
      return min;
   }
   protected int positionX(Graphics g,String text,double mult)
   {
      FontMetrics fm = g.getFontMetrics();
      return (int)(getWidth()*mult - fm.stringWidth(text)*0.5);
   }
   protected void drawUI(Graphics g) {
        

      for (enemytank enemy : enemies) {
          enemy.drawarmors(g);
      }
      

      p.drawarmors(g);
  

      Font uiFont = new Font(Font.DIALOG_INPUT, Font.BOLD, 75);
      g.setFont(uiFont);
  

      int clampedTimeLimit = Math.max(timelimit, 0);
      String timeText = "Time Limit: " + clampedTimeLimit;
      g.setColor(Color.WHITE);
      g.drawString(timeText, 50, 50);

      String tanksdestroyedText = "Tanks Destroyed: " + tanksdestroyed;
        g.setColor(Color.WHITE);
        g.drawString(tanksdestroyedText, 50, 100);
  

      String instructions = "Esc key to pause, Backspace to return to the main menu";
      g.drawString(instructions, 1200, 50);
  

      if (p.isAlive()) {
          drawAimingLine(g);
      }
  

      if (hitScan) {
          drawHitFlash(g);
      }
  }
  
  private void drawAimingLine(Graphics g) {
      try {
          Graphics2D g2 = (Graphics2D) g.create();
          g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{50}, 50));
          g2.setColor(Color.RED);
          Point mouse = MouseInfo.getPointerInfo().getLocation();
          Point window = getLocationOnScreen();
          g2.drawLine((int) p.getCenterX(), (int) p.getCenterY(), (int) (mouse.getX() - window.getX()), (int) (mouse.getY() - window.getY()));
          g2.dispose();
      } catch (Exception e) {
          e.printStackTrace(); 
      }
  }
  
  private void drawHitFlash(Graphics g) {
      int clampedAlpha = Math.max(hitAlpha, 0); 
      g.setColor(new Color(255, 0, 0, clampedAlpha));
      g.fillRect(0, 0, getWidth(), getHeight());
  }
  
   @Override
   public synchronized void paintComponent(Graphics g) {
       super.paintComponent(g);
       if (backgroundImage != null) {
           g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        
       }
       
       if (frame == 2 && isPaused()) {
           pauseAndPlaySound();
       }
       

       if (frame < 3) {
           frame++;
       }
       

       drawBullets(g);
       drawEnemies(g);
       drawPlayer(g);
   }
   
   private void pauseAndPlaySound() {
       pause();
       sounds.play();
   }
   
   private void drawBullets(Graphics g) {
       for (shooting bullet : bullets) {
           bullet.draw(g);
       }
   }
   
   private void drawEnemies(Graphics g) {
       for (enemytank enemy : enemies) {
           enemy.draw(g);
       }
   }
   
   private void drawPlayer(Graphics g) {
       p.draw(g);
   }
   
   protected void end()
   {
      running=false;
      sounds.silence();
   }
   public abstract gamepanel moveNextLevel();
   protected abstract void startLevel();
   private class MoveAction extends AbstractAction
   {
      private int direction;
      private MoveAction(int dir)
      {
         direction=dir;
      }
      @Override
      public void actionPerformed(ActionEvent e)
      {
         if(direction<4)
            p.move(direction);
         else
            p.stop(direction);
      }
   }
}
