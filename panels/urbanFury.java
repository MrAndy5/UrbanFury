package panels;

import javax.swing.AbstractAction;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import tanklogic.*;
import util.sounds;

public class urbanFury extends gamepanel {

    private long lastWinTime, startleveltime, pausedlevel;
    private final int WIN_TIME = 10000;
    private boolean won = false;
    public static final int buildings_WIDTH = 40;
    public static final int buildings_BETWEEN = 5;
    private ArrayList<gameObject> buildings = new ArrayList<gameObject>(0);

    private final String PAUSE = "pause";
    private mainmenu mainMenuPanel;

    public urbanFury() {
        mapa = new String[8 * 2 + 1][11 * 2 + 1];
        initialize(mapa);
        setUp(mapa);
        placeObjects(mapa);
    }

    public void setMainMenuPanel(mainmenu mainMenuPanel) {
        this.mainMenuPanel = mainMenuPanel;
    }

    @Override
    protected void setKeyBindings() {
        super.setKeyBindings();
        bindKey("ESCAPE", PAUSE);
        bindAction(PAUSE, new MenuAction(1));
    }

    @Override
    protected void checkCollisions() {
        super.checkCollisions();
        for (gameObject building : buildings) {
            p.bump(building);
            for (enemytank enemy : enemies) {
                enemy.bump(building);
            }
            for (shooting shot : bullets) {
                if (shot.bump(building)) {
                    shot.hit();
                }
            }
        }
    }

    private void checkAims() {
        for (gameObject building : buildings) {
            for (enemytank enemy : enemies) {
                enemy.checkAim(building);
            }
        }
    }

    @Override
    public synchronized void update() {
        super.update();
        if (!won)
            timelimit--;
        checkAims();
    }

    @Override
    public synchronized void render(double ip) {
        super.render(ip);
    }

    private void setUp(String[][] mapa) {
        int r, c;
        List<Integer> buildingRows = new ArrayList<>();
        List<Integer> buildingCols = new ArrayList<>();
        boolean isHorizontal = Math.random() < 0.5;

        if (isHorizontal) {
            r = 1;
            c = (int) (Math.random() * (mapa[0].length - 1) / 2) * 2 + 1;
            mapa[0][c] = "   ";
            mapa[mapa.length - 1][mapa[0].length - 1 - c] = "   ";
        } else {
            r = (int) (Math.random() * (mapa.length - 1) / 2) * 2 + 1;
            c = 1;
            mapa[r][0] = " ";
            mapa[mapa.length - 1 - r][mapa[0].length - 1] = " ";
        }

        mapa[r][c] = " A ";
        addBuildingPositions(mapa, r, c, buildingRows, buildingCols);

        while (!buildingRows.isEmpty()) {
            int index = (int) (Math.random() * buildingRows.size());
            r = buildingRows.remove(index);
            c = buildingCols.remove(index);

            if (isRemovable(mapa, r, c)) {
                updateMap(mapa, r, c);
                addBuildingPositions(mapa, r, c, buildingRows, buildingCols);
                mapa[r][c] = " 1 ";
            }
        }

        cleanUpMap(mapa);
        mapa[getRow(mapa, " A ")][getCol(mapa, " A ")] = " o ";
    }

    private void addBuildingPositions(String[][] mapa, int r, int c, List<Integer> buildingRows, List<Integer> buildingCols) {
        if (r > 1 && mapa[r - 1][c].equals("---")) {
            buildingRows.add(r - 1);
            buildingCols.add(c);
        }
        if (r < mapa.length - 2 && mapa[r + 1][c].equals("---")) {
            buildingRows.add(r + 1);
            buildingCols.add(c);
        }
        if (c > 1 && mapa[r][c - 1].equals("|")) {
            buildingRows.add(r);
            buildingCols.add(c - 1);
        }
        if (c < mapa[0].length - 2 && mapa[r][c + 1].equals("|")) {
            buildingRows.add(r);
            buildingCols.add(c + 1);
        }
    }

    private boolean isRemovable(String[][] mapa, int r, int c) {
        int count = 0;
        if (mapa[r][c].equals("---")) {
            if (mapa[r - 1][c].equals("   ")) count++;
            if (mapa[r + 1][c].equals("   ")) count++;
        } else if (mapa[r][c].equals("|")) {
            if (mapa[r][c - 1].equals("   ")) count++;
            if (mapa[r][c + 1].equals("   ")) count++;
        }
        return count == 1;
    }

    private void updateMap(String[][] mapa, int r, int c) {
        if (mapa[r][c].equals("---")) {
            mapa[r][c] = "   ";
            r = mapa[r - 1][c].equals("   ") ? r - 1 : r + 1;
        } else if (mapa[r][c].equals("|")) {
            mapa[r][c] = " ";
            c = mapa[r][c - 1].equals("   ") ? c - 1 : c + 1;
        }
    }

    private void cleanUpMap(String[][] mapa) {
        for (int row = 0; row < mapa.length; row++) {
            for (int col = 0; col < mapa[0].length; col++) {
                if (mapa[row][col].equals(" 1 ")) {
                    mapa[row][col] = "   ";
                }
            }
        }
    }

    private int getRow(String[][] mapa, String search) {
        for (int r = 0; r < mapa.length; r++)
            for (int c = 0; c < mapa[0].length; c++)
                if (mapa[r][c].equals(search))
                    return r;
        return -1;
    }

    private int getCol(String[][] mapa, String search) {
        for (int r = 0; r < mapa.length; r++)
            for (int c = 0; c < mapa[0].length; c++)
                if (mapa[r][c].equals(search))
                    return c;
        return -1;
    }

    private void initialize(String[][] mapa) {
        for (int r = 0; r < mapa.length; r++) {
            for (int c = 0; c < mapa[0].length; c++) {
                if (r % 2 == 0) {
                    mapa[r][c] = (c % 2 == 0) ? "+" : "---";
                } else {
                    mapa[r][c] = (c % 2 == 0) ? "|" : "   ";
                }
            }
        }
    }

    private void placeObjects(String[][] mapa) {
        for (int r = 0; r < mapa.length; r++) {
            for (int c = 0; c < mapa[0].length; c++) {
                double x = c * buildings_WIDTH * (buildings_BETWEEN + 1) / 2.0;
                double y = r * buildings_WIDTH * (buildings_BETWEEN + 1) / 2.0;

                switch (mapa[r][c]) {
                    case "+":
                        buildings.add(new gameObject(x, y, "resources/wall.png", buildings_WIDTH / 80.0, 0, 0, 1));
                        break;
                    case "---":
                        for (int i = 0; i < buildings_BETWEEN; i++) {
                            double wallX = (c - 1) * x + buildings_WIDTH * (i + 1);
                            buildings.add(new gameObject(wallX, y, "resources/wall.png", buildings_WIDTH / 80.0, 0, 0, 1));
                        }
                        break;
                    case "|":
                        for (int i = 0; i < buildings_BETWEEN; i++) {
                            double wallY = (r - 1) * y + buildings_WIDTH * (i + 1);
                            buildings.add(new gameObject(x, wallY, "resources/wall.png", buildings_WIDTH / 80.0, 0, 0, 1));
                        }
                        break;
                    case " o ":
                        mapa[mapa.length - r - 1][mapa[0].length - c - 1] = "END";
                        p = new player(x, y);
                        break;
                }
            }
        }

        for (int i = 0; i < INITIAL_ENEMIES; i++) {
            enemytank temp;
            do {
                double enemyX = ((int) (Math.random() * (mapa[0].length - 1) / 2) * 2 + 1) * buildings_WIDTH * (buildings_BETWEEN + 1) / 2.0;
                double enemyY = ((int) (Math.random() * (mapa.length - 1) / 2) * 2 + 1) * buildings_WIDTH * (buildings_BETWEEN + 1) / 2.0;
                temp = new enemytank(enemyX, enemyY);
            } while (temp.checkCollision(p));
            enemies.add(temp);
        }
    }

    public gamepanel moveNextLevel() {
        if (isNextReady()) {
            if (nextPanel != null)
                nextNotReady();
            sounds.silence();
            return nextPanel;
        }
        if (nextPanel instanceof mainmenu)
            return nextPanel;
        return null;
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (gameObject wall : buildings)
            wall.draw(g);

        g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 75));

        if (won) {
            g.setColor(Color.BLACK.brighter());
            String text = "YOU SURVIVED";
            g.drawString(text, positionX(g, text, 0.5), 100);

            if (System.currentTimeMillis() - lastWinTime > WIN_TIME) {
                won = false;
                nextIsReady();
            }
        } else {
            if (timelimit <= 0) {
                won = true;
                lastWinTime = System.currentTimeMillis();
                pause();
                loadNextLevel();
            }
            if (p.lives <= 0) {
                g.setColor(Color.RED);
                String text = "GAME OVER";
                g.drawString(text, positionX(g, text, 0.5), 80);

                Thread returnToMenuThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        nextPanel = mainMenuPanel;
                        nextIsReady();
                    }
                });

                returnToMenuThread.start();
            }
        }
        if (isPaused()) {
            g.setColor(Color.WHITE);
            String text = "PAUSE";
            g.drawString(text, positionX(g, text, 0.5), 160);
            for (enemytank enemy : enemies) {
                enemy.unpauseDeathTime();
                enemy.unpauseShotTime();
                enemy.unpauseExplosionUpdate();
            }
            for (shooting shot : bullets)
                shot.unpauseTimer();
            p.unpauseDeathTime();
            p.unpauseShotTime();
            p.unpauseExplosionUpdate();
            unpausestartleveltime();
        }

        drawUI(g);
    }

    private void pausestartleveltime() {
        pausedlevel = System.currentTimeMillis() - startleveltime;
    }

    private void unpausestartleveltime() {
        startleveltime = System.currentTimeMillis() - pausedlevel;
    }

    @Override
    protected void startLevel() {
        startleveltime = System.currentTimeMillis();
    }

    private class MenuAction extends AbstractAction {
        int code;

        private MenuAction(int key) {
            code = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (code == 0) {
                nextPanel = new mainmenu();
            } else if (code == 1) {
                if (!isPaused()) {
                    for (enemytank enemy : enemies) {
                        enemy.pauseDeathTime();
                        enemy.pauseShotTime();
                        enemy.pauseExplosionUpdate();
                    }
                    for (shooting shot : bullets)
                        shot.pauseTimer();
                    p.pauseDeathTime();
                    p.pauseShotTime();
                    p.pauseExplosionUpdate();
                    pausestartleveltime();

                    sounds.silence();
                } else
                    sounds.play();
                pause();
            }
        }
    }
}
