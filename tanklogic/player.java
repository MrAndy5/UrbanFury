package tanklogic;

import java.awt.MouseInfo;
import java.awt.Point;
import panels.gamepanel;
import java.awt.Graphics;

/**
 * The player class represents a player's tank in the game.
 * It extends the tank class and includes additional behaviors specific to player control,
 * such as movement based on keyboard input and turret aiming based on mouse position.
 */
public class player extends tank {
    
    /**
     * Constructs a new player object with the specified initial position.
     *
     * @param xPos the initial x position of the player's tank
     * @param yPos the initial y position of the player's tank
     */
    public player(double xPos, double yPos) {
        super(xPos, yPos, "resources/friendlyhull.png", 0.5, "resources/friendlyturret.png", 0.05, 2, 3, 500, 3000);
    }

    /**
     * Moves the player's tank in the specified direction.
     *
     * @param direction the direction to move the tank (0 for up, 1 for right, 2 for down, 3 for left)
     */
    public void move(int direction) {
        switch (direction) {
            case 0:
                yAccel = -ACCEL;
                break;
            case 1:
                xAccel = ACCEL;
                break;
            case 2:
                yAccel = ACCEL;
                break;
            case 3:
                xAccel = -ACCEL;
                break;
        }
    }

    /**
     * Stops the tank's acceleration in the specified direction.
     *
     * @param direction the direction to stop the tank (4 for vertical, 5 for horizontal)
     */
    public void stop(int direction) {
        switch (direction) {
            case 4:
                yAccel = 0;
                break;
            case 5:
                xAccel = 0;
                break;
        }
    }

    /**
     * Updates the player's tank state, including position, collision detection,
     * and turret aiming based on the mouse position.
     *
     * @param temp the gamepanel object representing the game environment
     */
    @Override
    public void update(gamepanel temp) {
        super.update(temp);
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        Point window = temp.getLocationOnScreen();
        double turnAmountX = mouse.getX() - window.getX() - getCenterX();
        double turnAmountY = mouse.getY() - window.getY() - getCenterY();
        turretaiming = Math.atan(turnAmountY / turnAmountX) + Math.PI / 2;
        if (turnAmountX < 0)
            turretaiming += Math.PI;
    }

    /**
     * Draws the player's tank on the screen.
     *
     * @param g the Graphics object used for drawing
     */
    @Override
    public void draw(Graphics g) {
        super.draw(g);
    }
}
