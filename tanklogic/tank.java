package tanklogic;

import java.awt.image.BufferedImage;
import panels.gamepanel;
import panels.urbanFury;
import util.sounds;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * The tank class represents a tank object in the game. It extends the gameObject class
 * and includes attributes and behaviors specific to tanks, such as shooting, drawing,
 * handling explosions, and respawning.
 */
public class tank extends gameObject {
    private final int TIME_BETWEEN_SHOTS;
    private final double ORIGINAL_X, ORIGINAL_Y;
    private final int LIVES;
    private long prevDeathTime, pausedDeathTime, prevExplosionUpdate, pausedExplosionUpdate, prevShotTime, pausedShotTime;
    private final int RESPAWN_TIME, EXPLOSION_UPDATE_TIME = 60;
    private BufferedImage turret, armor;
    private BufferedImage[] explosions = new BufferedImage[33];
    private int explosion = 0;
    private boolean shooting;
    protected double turretaiming;
    private double hitX, hitY;

    /**
     * Constructs a new tank object with the specified parameters.
     *
     * @param xPos      the initial x position of the tank
     * @param yPos      the initial y position of the tank
     * @param baseImg   the base image for the tank
     * @param hbMult    the hitbox multiplier for the tank
     * @param turretImg the image for the turret
     * @param accel     the acceleration of the tank
     * @param maxSpeed  the maximum speed of the tank
     * @param maxLives  the maximum number of lives the tank has
     * @param shotTime  the time between shots
     * @param respawnTime the respawn time after death
     */
    public tank(double xPos, double yPos, String baseImg, double hbMult, String turretImg, double accel, double maxSpeed, int maxLives, int shotTime, int respawnTime) {
        super(xPos, yPos, baseImg, hbMult, accel, maxSpeed, maxLives);
        turret = buffer(turretImg);
        explosions[0] = buffer("resources/explosion/img_0.png");
        explosions[1] = buffer("resources/explosion/img_1.png");
        explosions[2] = buffer("resources/explosion/img_2.png");
        explosions[3] = buffer("resources/explosion/img_3.png");
        explosions[4] = buffer("resources/explosion/img_4.png");
        explosions[5] = buffer("resources/explosion/img_5.png");
        explosions[6] = buffer("resources/explosion/img_6.png");
        explosions[7] = buffer("resources/explosion/img_7.png");
        explosions[8] = buffer("resources/explosion/img_8.png");
        explosions[9] = buffer("resources/explosion/img_9.png");
        explosions[10] = buffer("resources/explosion/img_10.png");
        explosions[11] = buffer("resources/explosion/img_11.png");
        explosions[12] = buffer("resources/explosion/img_12.png");
        explosions[13] = buffer("resources/explosion/img_13.png");
        explosions[14] = buffer("resources/explosion/img_14.png");
        explosions[15] = buffer("resources/explosion/img_15.png");
        explosions[16] = buffer("resources/explosion/img_16.png");
        explosions[17] = buffer("resources/explosion/img_17.png");
        explosions[18] = buffer("resources/explosion/img_18.png");
        explosions[19] = buffer("resources/explosion/img_19.png");
        explosions[20] = buffer("resources/explosion/img_20.png");
        explosions[21] = buffer("resources/explosion/img_21.png");
        explosions[22] = buffer("resources/explosion/img_22.png");
        explosions[23] = buffer("resources/explosion/img_23.png");
        explosions[24] = buffer("resources/explosion/img_24.png");
        explosions[25] = buffer("resources/explosion/img_25.png");
        explosions[26] = buffer("resources/explosion/img_26.png");
        explosions[27] = buffer("resources/explosion/img_27.png");
        explosions[28] = buffer("resources/explosion/img_28.png");
        explosions[29] = buffer("resources/explosion/img_29.png");
        explosions[30] = buffer("resources/explosion/img_30.png");
        explosions[31] = buffer("resources/explosion/img_31.png");
        explosions[32] = buffer("resources/explosion/img_32.png");
        TIME_BETWEEN_SHOTS = shotTime;
        ORIGINAL_X = xPos;
        ORIGINAL_Y = yPos;
        LIVES = maxLives;
        RESPAWN_TIME = respawnTime;
        armor = buffer("resources/lives.png");
    }

    /**
     * Draws the tank object. If the tank is alive, it calls drawAlive. If the tank is dead,
     * it calls drawExplosion if the death time is within 1 second.
     *
     * @param g the Graphics object used to draw the tank
     */
    @Override
    public void draw(Graphics g) {
        if (isAlive()) {
            drawAlive(g);
        } else if (System.currentTimeMillis() - prevDeathTime < 1000) {
            drawExplosion(g);
        }
    }

    /**
     * Draws the tank when it is alive, including its turret.
     *
     * @param g the Graphics object used to draw the tank
     */
    private void drawAlive(Graphics g) {
        super.draw(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform originalTransform = g2.getTransform();

        g2.rotate(turretaiming, getCenterX(), getCenterY());

        g2.drawImage(turret, (int) x, (int) y, turret.getWidth(), turret.getHeight(), null);

        g2.setTransform(originalTransform);
    }

    /**
     * Draws the explosion animation for the tank.
     *
     * @param g the Graphics object used to draw the explosion
     */
    private void drawExplosion(Graphics g) {
        BufferedImage temp = explosions[explosion];
        g.drawImage(temp, (int) (hitX - temp.getWidth() / 2), (int) (hitY - temp.getHeight() / 2), temp.getWidth(), temp.getHeight(), null);
    }

    /**
     * Draws the armor (lives) of the tank.
     *
     * @param g the Graphics object used to draw the armor
     */
    public void drawarmors(Graphics g) {
        for (int i = -1; i < lives - 1; i++) {
            int xDraw = (int) x + (i * 40);
            if (this instanceof enemytank)
                xDraw += 20;
            g.drawImage(armor, xDraw, (int) y - 40, armor.getWidth(), armor.getHeight(), null);
        }
    }

    /**
     * Updates the tank's state, including position, speed, and collision detection.
     *
     * @param temp the gamepanel object representing the game environment
     */
    @Override
    public void update(gamepanel temp) {
        super.update(temp);
        if (getHitbox().getX() < 0)
            x = (getHitbox().getWidth() - getWidth()) / 2;
        else if (getHitbox().getX() + getHitbox().getWidth() > temp.getWidth())
            x = temp.getWidth() - getWidth() / 2 - getHitbox().getWidth() / 2;

        if (getHitbox().getY() < 0)
            y = (getHitbox().getHeight() - getHeight()) / 2;
        else if (getHitbox().getY() + getHitbox().getHeight() > temp.getHeight())
            y = temp.getHeight() - getHeight() / 2 - getHitbox().getHeight() / 2;

        if (xSpeed != 0 || ySpeed != 0) {
            rotation = Math.atan(ySpeed / xSpeed) + Math.PI / 2;
            if (xSpeed < 0)
                rotation += Math.PI;
        }

        if (xAccel == 0)
            xSpeed = adjustSpeed(xSpeed, ACCEL);
        if (yAccel == 0)
            ySpeed = adjustSpeed(ySpeed, ACCEL);
    }

    /**
     * Adjusts the speed of the tank based on acceleration.
     *
     * @param speed the current speed
     * @param accel the acceleration
     * @return the adjusted speed
     */
    private double adjustSpeed(double speed, double accel) {
        if (speed > accel)
            return speed - accel / 2;
        else if (speed < -accel)
            return speed + accel / 2;
        else
            return 0;
    }

    /**
     * Checks if the tank collides with another gameObject.
     *
     * @param other the other gameObject to check collision with
     * @return true if there is a collision, false otherwise
     */
    public boolean checkCollision(gameObject other) {
        if (other.isAlive() && isAlive())
            return super.checkCollision(other);
        else
            return false;
    }

    /**
     * Handles bumping into another gameObject.
     *
     * @param other the other gameObject to bump into
     * @return true if the bump is successful, false otherwise
     */
    public boolean bump(gameObject other) {
        if (other.isAlive() && isAlive())
            return super.bump(other);
        else
            return false;
    }

    /**
     * Shoots a bullet from the tank if it is able to.
     *
     * @return a shooting object representing the bullet, or null if the tank cannot shoot
     */
    public shooting shoot() {
        long now = System.currentTimeMillis();
        if (shooting && now > prevShotTime + TIME_BETWEEN_SHOTS && lives > 0) {
            prevShotTime = now;
            shooting temp = new shooting(x, y, "resources/bullet.png", 5, turretaiming, this);
            sounds.shot();
            return temp;
        }
        return null;
    }

    /**
     * Pauses the shot timer.
     */
    public void pauseShotTime() {
        pausedShotTime = System.currentTimeMillis() - prevShotTime;
    }

    /**
     * Unpauses the shot timer.
     */
    public void unpauseShotTime() {
        prevShotTime = System.currentTimeMillis() - pausedShotTime;
    }

    /**
     * Sets the shooting state of the tank.
     *
     * @param state the new shooting state
     */
    public void setShooting(boolean state) {
        shooting = state;
    }

    /**
     * Handles the tank being hit by an enemy.
     */
    @Override
    public void hit() {
        super.hit();
        if (lives == 0) {
            prevDeathTime = System.currentTimeMillis();
            xSpeed = 0;
            ySpeed = 0;
            xAccel = 0;
            yAccel = 0;
            hitX = getCenterX();
            hitY = getCenterY();
            sounds.explosion();
        }
    }

    /**
     * Gets the time of the tank's death.
     *
     * @return the death time in milliseconds
     */
    public long getDeathTime() {
        return prevDeathTime;
    }

    /**
     * Checks if the death timer has expired.
     *
     * @return true if the tank can respawn, false otherwise
     */
    public boolean checkDeathTimer() {
        return System.currentTimeMillis() > prevDeathTime + RESPAWN_TIME;
    }

    /**
     * Respawns the tank to its original position and resets its lives.
     */
    public void respawn() {
        x = ORIGINAL_X;
        y = ORIGINAL_Y;
        lives = LIVES;
    }

    /**
     * Displays the remaining death time before respawning.
     *
     * @return the remaining death time in seconds
     */
    public int dispDeathTime() {
        if (lives <= 0)
            return (int) ((RESPAWN_TIME + prevDeathTime - System.currentTimeMillis()) / 1000) + 1;
        return 0;
    }

    /**
     * Pauses the death timer.
     */
    public void pauseDeathTime() {
        pausedDeathTime = System.currentTimeMillis() - prevDeathTime;
    }

    /**
     * Unpauses the death timer.
     */
    public void unpauseDeathTime() {
        prevDeathTime = System.currentTimeMillis() - pausedDeathTime;
    }

    /**
     * Updates the explosion animation.
     */
    public void updateExplosion() {
        long now = System.currentTimeMillis();
        if (now - prevExplosionUpdate > EXPLOSION_UPDATE_TIME) {
            explosion = (explosion + 1) % 33;
            prevExplosionUpdate = now;
        }
    }

    /**
     * Pauses the explosion update timer.
     */
    public void pauseExplosionUpdate() {
        pausedExplosionUpdate = System.currentTimeMillis() - prevExplosionUpdate;
    }

    /**
     * Unpauses the explosion update timer.
     */
    public void unpauseExplosionUpdate() {
        prevExplosionUpdate = System.currentTimeMillis() - pausedExplosionUpdate;
    }

    /**
     * Converts pixel coordinates to board coordinates.
     *
     * @param pixels the pixel value to convert
     * @return the corresponding board coordinate
     */
    public int toBoardSpace(double pixels) {
        return (int) ((pixels - 35) / (urbanFury.buildings_WIDTH * (urbanFury.buildings_BETWEEN + 1))) * 2 + 1;
    }

    /**
     * Gets the board column of the tank's center position.
     *
     * @return the board column
     */
    public int getBoardC() {
        return toBoardSpace(getCenterX());
    }

    /**
     * Gets the board row of the tank's center position.
     *
     * @return the board row
     */
    public int getBoardR() {
        return toBoardSpace(getCenterY());
    }

    /**
     * Converts board coordinates to pixel coordinates.
     *
     * @param n the board coordinate to convert
     * @return the corresponding pixel coordinate
     */
    protected static int toPixelSpace(int n) {
        return (n - 1) / 2 * urbanFury.buildings_WIDTH * (urbanFury.buildings_BETWEEN + 1) + 60 + 35;
    }
}
