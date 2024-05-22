package tanklogic;

import javax.imageio.ImageIO;
import panels.gamepanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

/**
 * The gameObject class represents a basic game object with properties such as position, speed, acceleration, 
 * and collision detection. It serves as a base class for more specific game objects like tanks and bullets.
 */
public class gameObject {
    protected final double ACCEL, MAX_SPEED;
    protected double xSpeed, ySpeed;
    protected double x, y, rotation;
    protected double xAccel, yAccel;
    public int lives;
    protected BufferedImage image;
    private Rectangle2D hitbox;

    /**
     * Constructs a new gameObject with the specified parameters.
     *
     * @param xPos the initial x position of the game object
     * @param yPos the initial y position of the game object
     * @param imgFile the file path to the image representing the game object
     * @param hbMult the hitbox multiplier for the game object
     * @param accel the acceleration of the game object
     * @param maxSpeed the maximum speed of the game object
     * @param maxLives the maximum lives of the game object
     */
    public gameObject(double xPos, double yPos, String imgFile, double hbMult, double accel, double maxSpeed, int maxLives) {
        x = xPos;
        y = yPos;
        ACCEL = accel;
        MAX_SPEED = maxSpeed;
        image = buffer(imgFile);
        lives = maxLives;
        hitbox = new Rectangle2D.Double(getCenterX() - image.getWidth() * hbMult / 2, getCenterY() - image.getHeight() * hbMult / 2, image.getWidth() * hbMult, image.getHeight() * hbMult);
    }

    /**
     * Loads an image from the specified file path.
     *
     * @param filename the file path to the image
     * @return the loaded BufferedImage
     */
    protected BufferedImage buffer(String filename) {
        try {
            return ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Interpolates the position of the game object based on its speed and the given interpolation factor.
     *
     * @param interpolation the interpolation factor
     */
    public void interpolate(double interpolation) {
        if (isAlive()) {
            moveWithSpeed(interpolation);
            updateHitbox();
        }
    }

    /**
     * Moves the game object based on its speed and the given interpolation factor.
     *
     * @param interpolation the interpolation factor
     */
    private void moveWithSpeed(double interpolation) {
        x += xSpeed * interpolation;
        y += ySpeed * interpolation;
    }

    /**
     * Updates the hitbox of the game object to match its current position.
     */
    private void updateHitbox() {
        hitbox.setRect(getCenterX() - hitbox.getWidth() / 2, getCenterY() - hitbox.getHeight() / 2, hitbox.getWidth(), hitbox.getHeight());
    }

    /**
     * Draws the game object on the screen.
     *
     * @param g the Graphics object used for drawing
     */
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(rotation, getCenterX(), getCenterY());
        g2.drawImage(image, (int) x, (int) y, image.getWidth(), image.getHeight(), null);
        g2.rotate(-rotation, getCenterX(), getCenterY());
    }

    /**
     * Updates the speed and position of the game object based on its acceleration.
     *
     * @param temp the gamepanel object representing the game environment
     */
    public void update(gamepanel temp) {
        updateSpeed(xAccel, MAX_SPEED, true);
        updateSpeed(yAccel, MAX_SPEED, false);
    }

    /**
     * Updates the speed of the game object based on the given acceleration and maximum speed.
     *
     * @param acceleration the acceleration to apply
     * @param maxSpeed the maximum speed to enforce
     * @param isX true if updating the x-axis speed, false if updating the y-axis speed
     */
    private void updateSpeed(double acceleration, double maxSpeed, boolean isX) {
        if (isX) {
            xSpeed = clampSpeed(xSpeed + acceleration, maxSpeed);
        } else {
            ySpeed = clampSpeed(ySpeed + acceleration, maxSpeed);
        }
    }

    /**
     * Clamps the speed to ensure it does not exceed the given maximum speed.
     *
     * @param speed the speed to clamp
     * @param maxSpeed the maximum speed to enforce
     * @return the clamped speed
     */
    private double clampSpeed(double speed, double maxSpeed) {
        return Math.min(Math.max(speed, -maxSpeed), maxSpeed);
    }

    /**
     * Checks if this game object collides with another game object.
     *
     * @param other the other game object to check collision with
     * @return true if a collision occurs, false otherwise
     */
    public boolean checkCollision(gameObject other) {
        if (other.isAlive())
            return hitbox.intersects(other.getHitbox());
        else
            return false;
    }

    /**
     * Handles the collision logic for this game object when colliding with another object.
     *
     * @param speed the speed at which the collision occurs
     * @return the new speed after collision
     */
    protected double collide(double speed) {
        return 0;
    }

    /**
     * Checks for future collision with another game object based on current speed.
     *
     * @param other the other game object to check future collision with
     * @return true if a future collision is detected, false otherwise
     */
    protected boolean futureBump(gameObject other) {
        boolean fCollision = false;

        if (xSpeed != 0 || ySpeed != 0) {
            Rectangle2D futureHb = new Rectangle2D.Double(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

            double futureY = hitbox.getY() + ySpeed;
            futureHb.setRect(hitbox.getX(), futureY, hitbox.getWidth(), hitbox.getHeight());
            if (futureHb.intersects(other.getHitbox())) {
                ySpeed = collide(ySpeed);
                fCollision = true;
            }

            double futureX = hitbox.getX() + xSpeed;
            futureHb.setRect(futureX, hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
            if (futureHb.intersects(other.getHitbox())) {
                xSpeed = collide(xSpeed);
                fCollision = true;
            }

            if (!fCollision) {
                futureHb.setRect(futureX, futureY, hitbox.getWidth(), hitbox.getHeight());
                if (futureHb.intersects(other.getHitbox())) {
                    xSpeed = collide(xSpeed);
                    ySpeed = collide(ySpeed);
                    fCollision = true;
                }
            }
        }

        hitbox.setRect(getCenterX() - hitbox.getWidth() / 2 + xSpeed, getCenterY() - hitbox.getHeight() / 2 + ySpeed, hitbox.getWidth(), hitbox.getHeight());
        return fCollision;
    }

    /**
     * Handles the immediate collision logic for this game object with another object.
     *
     * @param other the other game object to handle collision with
     */
    protected void nowBump(gameObject other) {
        if (checkCollision(other)) {
            double tempX = x;
            Rectangle2D overlap = hitbox.createIntersection(other.getHitbox());
            if (xSpeed != 0)
                x -= xSpeed / Math.abs(xSpeed) * overlap.getWidth();
            if (ySpeed != 0)
                y -= ySpeed / Math.abs(ySpeed) * overlap.getHeight();
            else if (x == tempX) {
                if (getCenterX() > overlap.getX() + overlap.getWidth() / 2)
                    x += overlap.getWidth();
                else if (getCenterX() < overlap.getX() + overlap.getWidth() / 2)
                    x -= overlap.getWidth();
                if (getCenterY() > overlap.getY() + overlap.getHeight() / 2)
                    y += overlap.getHeight();
                else if (getCenterY() < overlap.getY() + overlap.getHeight() / 2)
                    y -= overlap.getHeight();
                else if (x == tempX) {
                    x += (int) (Math.random() * 3 - 1) * other.getHitbox().getWidth();
                    y += (int) (Math.random() * 3 - 1) * other.getHitbox().getHeight();
                }
            }
        }
        hitbox.setRect(getCenterX() - hitbox.getWidth() / 2 + xSpeed, getCenterY() - hitbox.getHeight() / 2 + ySpeed, hitbox.getWidth(), hitbox.getHeight());
    }

    /**
     * Handles the bump logic for this game object with another object.
     *
     * @param other the other game object to bump into
     * @return true if a future collision is detected, false otherwise
     */
    public boolean bump(gameObject other) {
        boolean fCollision = futureBump(other);
        nowBump(other);
        return fCollision;
    }

    /**
     * Gets the hitbox of the game object.
     *
     * @return the hitbox of the game object
     */
    public Rectangle2D getHitbox() {
        return hitbox;
    }

    /**
     * Reduces the lives of the game object by one.
     */
    public void hit() {
        lives--;
    }

    /**
     * Checks if the game object is still alive (has lives remaining).
     *
     * @return true if the game object is alive, false otherwise
     */
    public boolean isAlive() {
        return lives > 0;
    }

    /**
     * Gets the x-coordinate of the center of the game object.
     *
     * @return the x-coordinate of the center
     */
    public double getCenterX() {
        return x + image.getWidth() / 2;
    }

    /**
     * Gets the y-coordinate of the center of the game object.
     *
     * @return the y-coordinate of the center
     */
    public double getCenterY() {
        return y + image.getHeight() / 2;
    }

    /**
     * Gets the width of the game object.
     *
     * @return the width of the game object
     */
    protected double getWidth() {
        return image.getWidth();
    }

    /**
     * Gets the height of the game object.
     *
     * @return the height of the game object
     */
    protected double getHeight() {
        return image.getHeight();
    }
}
