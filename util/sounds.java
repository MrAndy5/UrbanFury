package util;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 * The sounds class provides methods for playing various sound effects and background music in a game.
 */
public class sounds {

    private static boolean playing;
    private static Clip backgroundClip;
    private static String[] backgroundTracks = {
        "resources/mainmenu.wav", 
        "resources/play1.wav", 
        "resources/play2.wav", 
        "resources/play3.wav"
    };
    private static int distance = 1000;  // Delay in milliseconds between background tracks

    /**
     * Initializes the sound system by starting background music.
     */
    public static void initialize() {
        play();
        backgroundMusic();
    }

    /**
     * Picks a random background music track and starts playing it.
     */
    public static void pickSong() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
        String track = backgroundTracks[(int) (Math.random() * backgroundTracks.length)];
        playWAV(track, true);
    }

    /**
     * Stops the currently playing background music.
     */
    public static void silence() {
        if (backgroundClip != null) {
            backgroundClip.stop();
        }
        playing = false;
    }

    /**
     * Resumes playing background music.
     */
    public static void play() {
        playing = true;
    }

    /**
     * Sets the delay between background music tracks.
     *
     * @param d The delay in milliseconds.
     */
    public static void setDistance(double d) {
        distance = (int) d;
    }

    /**
     * Starts playing background music in a separate thread.
     */
    public static void backgroundMusic() {
        Thread music = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (playing) {
                        pickSong();
                        try {
                            // Wait for the background music to finish
                            if (backgroundClip != null) {
                                synchronized (backgroundClip) {
                                    backgroundClip.wait();
                                }
                            }
                            // Wait for the specified distance time before playing the next track
                            Thread.sleep(distance);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        music.start();
    }

    /**
     * Plays the explosion sound effect.
     */
    public static void explosion() {
        playWAV("resources/explosion.wav", false);
    }

    /**
     * Plays the shot sound effect.
     */
    public static void shot() {
        playWAV("resources/shot.wav", false);
    }

    /**
     * Plays the bullet impact sound effect.
     */
    public static void damage() {
        playWAV("resources/bulletimpact.wav", false);
    }

    /**
     * Plays the mouse click sound effect.
     */
    public static void selection() {
        playWAV("resources/mouseclick.wav", false);
    }

    /**
     * Plays a WAV audio file.
     *
     * @param filepath    The path to the WAV file.
     * @param isBackground Whether the audio is background music.
     */
    private static void playWAV(String filepath, boolean isBackground) {
        try {
            File soundFile = new File(filepath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            
            // If this is a background music track, keep a reference to it
            if (isBackground) {
                backgroundClip = clip;
                clip.addLineListener(new LineListener() {
                    @Override
                    public void update(LineEvent event) {
                        if (event.getType() == LineEvent.Type.STOP) {
                            synchronized (clip) {
                                clip.notify();
                            }
                        }
                    }
                });
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}


