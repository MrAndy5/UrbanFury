package panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import util.CustomButton;
import util.sounds;
import tanklogic.*;

public class mainmenu extends gamepanel implements ActionListener
{
    private CustomButton play = new CustomButton("PLAY"),
                         help = new CustomButton("HELP"),
                         exit = new CustomButton("EXIT");
    private Image backgroundImage;
    private boolean ShowHelp = false;
    private urbanFury gamePanel;

    public mainmenu()
    {
        setLayout(new GridBagLayout());
        addButtons();

        p = new player(1300, 1000 - 40);
        loadNextLevel();

        try {
            backgroundImage = ImageIO.read(new File("resources/mainmenu.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gamePanel = new urbanFury();
        gamePanel.setMainMenuPanel(this);
    }

    private void addButtons()
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        play.addActionListener(this);
        help.addActionListener(this);
        exit.addActionListener(this);

        add(play, gbc);
        add(help, gbc);
        add(exit, gbc);
    }

    @Override
    public gamepanel moveNextLevel()
    {
        if (isNextReady())
        {
            if (nextPanel != null)
            {
                nextNotReady();
                nextPanel.startLevel();
                sounds.silence();
            }
            return nextPanel;
        }
        return null;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (ShowHelp)
        {
            Font font = new Font("Arial", Font.BOLD, 50);
            g.setFont(font);
            g.setColor(Color.WHITE);   
            g.drawString("USE WASD TO MOVE AND THE MOUSE TO SHOOT", getWidth()/5, 2*getHeight()/3);
        }

        String narrative = "You are the last tank standing. The enemy has taken over the city and you are the only one left to stop them. You must fight your way through the city. Destroy the enemy tanks until the time limit is reached. Good luck!";
        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.setColor(Color.WHITE);
         
        int x = 75;
        int y = 475;
        int lineHeight = g.getFontMetrics().getHeight();
         
        String[] lines = narrative.split("\\.\\s*");
         
        for (String line : lines) {
            g.setColor(Color.GRAY);
            g.drawString(line, x + 2, y + 2);
             
            g.setColor(Color.WHITE);
            g.drawString(line, x, y);
             
            y += lineHeight;
        }

        String autor = "Made by Andrés Sánchez de Ágreda. Music originally composed and produced by Andrés Sánchez de Ágreda. ICAI POO 2024";
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString(autor, getWidth()/5, 1000); 
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == play)
        {
            sounds.selection();
            nextPanel = gamePanel;
            nextIsReady();
        }
        else if (source == help)
        {
            ShowHelp = !ShowHelp;
            repaint();
        }
        else if (source == exit)
        {
            sounds.selection();
            end();
        }
    }

    @Override
    protected void startLevel(){}
}
