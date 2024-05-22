package tanklogic;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import panels.gamepanel;
import util.pathfinder;

public class enemytank extends tank
{
   private player player;
   private ArrayList<pathfinder> path=new ArrayList<pathfinder>();
   private final int TARGET_RANGE=150;
   public enemytank(double xPos,double yPos)
   {
      super(xPos,yPos,"resources/enemyhull.png",0.5,"resources/enemyturret.png",0.05,0.5,2,2000,10000);
   }
   
   /** 
    * @param temp
    */
   @Override
   public void update(gamepanel temp)
   {
      super.update(temp);
      double xDiff=player.getCenterX()-getCenterX();
      double yDiff=player.getCenterY()-getCenterY();
      
      
      if(path.isEmpty())
      {
         simpleAI(xDiff,yDiff);
      }
      else
      {
         if(getBoardR()==path.get(0).getR() && getBoardC()==path.get(0).getC() && path.size()>1)
            path.remove(0);
         moveNextBlock();
      }
      
      turretaiming=Math.atan(yDiff/xDiff)+Math.PI/2;
      if(xDiff<0)
         turretaiming+=Math.PI;
   }
   public void setplayer(player p)
   {
      player=p;
   }
   public void checkAim(gameObject other)
   {
      Line2D aim=new Line2D.Double(getCenterX(),getCenterY(),player.getCenterX(),player.getCenterY());
      
      if(aim.intersects(other.getHitbox()))
         setShooting(false);
   }
   @Override
   public shooting shoot()
   {
      shooting temp=super.shoot();
      setShooting(true);
      return temp;
   }
   
   private void simpleAI(double xDiff, double yDiff) {
      updateAcceleration(xDiff, xSpeed, true);
      updateAcceleration(yDiff, ySpeed, false);
  }
  
  private void updateAcceleration(double diff, double speed, boolean isX) {
      if (speed < MAX_SPEED && speed > -MAX_SPEED) {
          if (diff > TARGET_RANGE) {
              setAcceleration(isX, ACCEL);
          } else if (diff < -TARGET_RANGE) {
              setAcceleration(isX, -ACCEL);
          } else {
              setAcceleration(isX, 0);
          }
      } else {
          setAcceleration(isX, 0);
      }
  }
  
  private void setAcceleration(boolean isX, double accel) {
      if (isX) {
          xAccel = accel;
      } else {
          yAccel = accel;
      }
  }
  private void moveNextBlock() {
   double xDiff = toPixelSpace(path.get(0).getC()) - getCenterX();
   double yDiff = toPixelSpace(path.get(0).getR()) - getCenterY();

   xAccel = calculateAcceleration(xDiff, xSpeed);
   yAccel = calculateAcceleration(yDiff, ySpeed);
}

private double calculateAcceleration(double diff, double speed) {
   if (speed < MAX_SPEED && speed > -MAX_SPEED) {
       if (diff > 5) {
           return ACCEL;
       } else if (diff < -5) {
           return -ACCEL;
       }
   }
   return 0;
}

public pathfinder findPath(String[][] board) {
    if (board == null) {
        return null;
    }

    ArrayList<pathfinder> open = new ArrayList<>();
    ArrayList<pathfinder> closed = new ArrayList<>();
    open.add(new pathfinder(getBoardR(), getBoardC()));

    while (!open.isEmpty()) {
        pathfinder current = findNextStep(open);
        open.remove(current);
        closed.add(current);

        for (int i = -1; i <= 1; i += 2) {
            checkAndAddNeighbor(board, open, closed, current, i, 0);  // Vertical movement
            checkAndAddNeighbor(board, open, closed, current, 0, i);  // Horizontal movement
        }
    }
    return null;
}

private void checkAndAddNeighbor(String[][] board, ArrayList<pathfinder> open, ArrayList<pathfinder> closed, pathfinder current, int rowOffset, int colOffset) {
    int currR = current.getR();
    int currC = current.getC();
    int newR = currR + rowOffset * 2;
    int newC = currC + colOffset * 2;

    if (isValidMove(board, currR + rowOffset, currC + colOffset, rowOffset, colOffset)) {
        pathfinder neighbor = new pathfinder(current, newR, newC);
        neighbor.setDistanceToEnd(player.getBoardR(), player.getBoardC());
        neighbor.findF();

        if (neighbor.getR() == player.getBoardR() && neighbor.getC() == player.getBoardC()) {
            open.clear();
            open.add(neighbor);

        }

        if (!betterPathAlreadyExists(open, neighbor) && !betterPathAlreadyExists(closed, neighbor)) {
            open.add(neighbor);
        }
    }
}

private boolean isValidMove(String[][] board, int newR, int newC, int rowOffset, int colOffset) {
    if (newR < 0 || newR >= board.length || newC < 0 || newC >= board[0].length) {
        return false;
    }
    if (rowOffset != 0 && !board[newR][newC].equals("---")) {
        return true;
    }
    if (colOffset != 0 && !board[newR][newC].equals("|")) {
        return true;
    }
    return false;
}

   public void setPath(pathfinder n)
   {
      if(n!=null)
      {
         path.clear();
         while(n.hasParent())
         {
            path.add(0,n);
            n=n.getParent();
         }
         path.add(0,n);
      }
   }

   private pathfinder findNextStep(ArrayList<pathfinder> open)
   {
      pathfinder nextStep=open.get(0);
      for(pathfinder n:open)
         if(n.getF()<nextStep.getF())
            nextStep=n;
      return nextStep;
   }
   private boolean betterPathAlreadyExists(ArrayList<pathfinder> list,pathfinder potential)
   {
      for(pathfinder n:list)
         if(n.getR()==potential.getR() && n.getC()==potential.getC() && n.getF()<potential.getF())
            return true;
      return false;
   }
}

