package tanklogic;

import panels.gamepanel;

/**
 * The shooting class represents a bullet shot by a tank in the game.
 * It extends the gameObject class and includes attributes and behaviors specific to bullets,
 * such as updating its position, handling collisions, and managing its lifespan.
 */
public class shooting extends gameObject {
    private gamepanel panel;
    private tank owner;
    private long bulletime, paused;
    private final int RELOAD_TIME = 200;

    /**
     * Constructs a new shooting object with the specified parameters.
     *
     * @param xPos     the initial x position of the bullet
     * @param yPos     the initial y position of the bullet
     * @param imgFile  the image file for the bullet
     * @param maxSpeed the maximum speed of the bullet
     * @param rot      the rotation angle of the bullet
     * @param shooter  the tank that shot the bullet
     */
    public shooting(double xPos, double yPos, String imgFile, double maxSpeed, double rot, tank shooter) {
        super(xPos, yPos, imgFile, 0.14, 0, maxSpeed, 2);
        rotation = rot;
        xSpeed = MAX_SPEED * Math.cos(rotation - Math.PI / 2);
        ySpeed = MAX_SPEED * Math.sin(rotation - Math.PI / 2);
        owner = shooter;
        bulletime = System.currentTimeMillis();
    }

    /**
     * Updates the bullet's state, including position and collision detection.
     *
     * @param temp the gamepanel object representing the game environment
     */
    @Override
    public void update(gamepanel temp) {
        super.update(temp);
        panel = temp;
    }

    /**
     * Handles collision by reversing the speed of the bullet.
     *
     * @param speed the current speed
     * @return the reversed speed
     */
    @Override
    protected double collide(double speed) {
        return -speed;
    }

    /**
     * Handles bumping into another gameObject.
     *
     * @param other the other gameObject to bump into
     * @return true if the bump is successful, false otherwise
     */
    @Override
    public boolean bump(gameObject other) {
        nowBump(other);
        return futureBump(other);
    }

    /**
     * Handles the bullet being hit by an enemy.
     */
    @Override
    public void hit() {
        super.hit();
        rotation = Math.atan(ySpeed / xSpeed) + Math.PI / 2;
        if (xSpeed < 0)
            rotation += Math.PI;
    }

    /**
     * Checks if the bullet is still alive and within the game panel.
     *
     * @return true if the bullet is alive and within bounds, false otherwise
     */
    @Override
    public boolean isAlive() {
        try {
            return super.isAlive() && x > -getWidth() && x < panel.getWidth() && y > -getHeight() && y < panel.getHeight();
        } catch (NullPointerException e) {
            return true;
        }
    }

    /**
     * Checks if the bullet collides with another gameObject.
     *
     * @param other the other gameObject to check collision with
     * @return true if there is a collision, false otherwise
     */
    @Override
    public boolean checkCollision(gameObject other) {
        return super.checkCollision(other) && !(other.equals(owner) && System.currentTimeMillis() - bulletime < RELOAD_TIME);
    }

    /**
     * Pauses the bullet's timer.
     */
    public void pauseTimer() {
        paused = System.currentTimeMillis() - bulletime;
    }

    /**
     * Unpauses the bullet's timer.
     */
    public void unpauseTimer() {
        bulletime = System.currentTimeMillis() - paused;
    }
}
