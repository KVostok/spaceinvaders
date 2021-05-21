package com.javarush.games.spaceinvaders;

import com.javarush.engine.cell.*;
import com.javarush.games.spaceinvaders.gameobjects.Bullet;
import com.javarush.games.spaceinvaders.gameobjects.EnemyFleet;
import com.javarush.games.spaceinvaders.gameobjects.PlayerShip;
import com.javarush.games.spaceinvaders.gameobjects.Star;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpaceInvadersGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int COMPLEXITY = 5;
    private static final int PLAYER_BULLETS_MAX = 10;

    private boolean isGameStopped;
    private int animationsCount;
    private int score;

    private List<Star> stars;
    private EnemyFleet enemyFleet;
    private List<Bullet> enemyBullets;
    private List<Bullet> playerBullets;
    private PlayerShip playerShip;

    @Override
    public void setCellValueEx(int x, int y, Color cellColor, String value) {
        if (x<0 || x>=WIDTH || y<0 || y>=HEIGHT) return;
        super.setCellValueEx(x, y, cellColor, value);
    }

    @Override
    public void onKeyReleased(Key key) {
        if (key == Key.LEFT && playerShip.getDirection() == Direction.LEFT)
            playerShip.setDirection(Direction.UP);
        if (key == Key.RIGHT && playerShip.getDirection() == Direction.RIGHT)
            playerShip.setDirection(Direction.UP);
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.SPACE && isGameStopped) {
            createGame();
            return;
        }
        if (key == Key.LEFT)
            playerShip.setDirection(Direction.LEFT);
        else if (key == Key.RIGHT)
            playerShip.setDirection(Direction.RIGHT);
        else if (key == Key.SPACE) {
            Bullet b = playerShip.fire();
            if ( b != null && playerBullets.size() < PLAYER_BULLETS_MAX)
                playerBullets.add(b);
        }
    }

    private void stopGameWithDelay(){
        ++animationsCount;
        if (animationsCount >= 10) stopGame(playerShip.isAlive);
    }

    private void stopGame(boolean isWin){
        isGameStopped = true;
        stopTurnTimer();
        if (isWin)
            showMessageDialog(Color.YELLOW, "***!!!WIN!!!***", Color.GREEN, 65);
        else
            showMessageDialog(Color.YELLOW, "***!!!GAME OVER!!!***", Color.RED, 45);
    }

    private void check(){
        playerShip.verifyHit(enemyBullets);
        score += enemyFleet.verifyHit(playerBullets);
        enemyFleet.deleteHiddenShips();
        if (enemyFleet.getBottomBorder() >= playerShip.y)
            playerShip.kill();
        if (enemyFleet.getShipsCount() == 0) {
            playerShip.win();
            stopGameWithDelay();
        }
        removeDeadBullets();
        if (!playerShip.isAlive) stopGameWithDelay();
    }

    private void removeDeadBullets(){
        Iterator<Bullet> it = enemyBullets.iterator();
        while(it.hasNext()){
            Bullet b = it.next();
            if ((!b.isAlive) || (b.y >= (HEIGHT-1))) it.remove();
        }

        it = playerBullets.iterator();
        while(it.hasNext()){
            Bullet b = it.next();
            if ((!b.isAlive) || ((b.y+b.height) < 0)) it.remove();
        }
    }

    private void moveSpaceObjects(){
        enemyFleet.move();
        for (Bullet b: enemyBullets) {
            b.move();
        }
        for (Bullet b:playerBullets) {
            b.move();
        }
        playerShip.move();
    }

    @Override
    public void onTurn(int step) {
        Bullet b = enemyFleet.fire(this);
        moveSpaceObjects();
        check();
        if (b != null) enemyBullets.add(b);
        setScore(score);
        drawScene();
    }

    private void createStars(){
        stars = new ArrayList<Star>();
        for (int i = 0; i < 8; i++) {
            stars.add(new Star(getRandomNumber(WIDTH-1), getRandomNumber(HEIGHT-1)));

        }
    }

    private void createGame(){
        score = 0;
        isGameStopped = false;
        animationsCount = 0;
        createStars();
        enemyFleet = new EnemyFleet();
        enemyBullets = new ArrayList<>();
        playerShip = new PlayerShip();
        playerBullets = new ArrayList<>();
        drawScene();
        setTurnTimer(40);
    }

    private void drawScene(){
        drawField();
        enemyFleet.draw(this);
        for (Bullet b:enemyBullets) {
            b.draw(this);
        }
        for (Bullet b:playerBullets) {
            b.draw(this);
        }
        playerShip.draw(this);

    }

    private void drawField(){
        //Отрисовка поля
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                setCellValueEx(x, y, Color.BLACK, "");
            }

        }

        //Отрисовка звезд
        for (Star s:stars) {
            s.draw(this);
        }

    }

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }
}
