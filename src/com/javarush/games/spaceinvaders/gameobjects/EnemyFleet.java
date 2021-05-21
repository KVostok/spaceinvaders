package com.javarush.games.spaceinvaders.gameobjects;

import com.javarush.engine.cell.Game;
import com.javarush.games.spaceinvaders.Direction;
import com.javarush.games.spaceinvaders.ShapeMatrix;
import com.javarush.games.spaceinvaders.SpaceInvadersGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyFleet {
    private static final int ROWS_COUNT =  3;
    private static final int COLUMNS_COUNT = 10;
    private static final int STEP = ShapeMatrix.ENEMY.length+1;
    private List<EnemyShip> ships;
    private Direction direction = Direction.RIGHT;

    public int getShipsCount(){
        return ships.size();
    }

    public double getBottomBorder(){
        double max = 0;
        if (!ships.isEmpty())
            max=ships.get(0).y+ships.get(0).height;
        for (EnemyShip es:ships) {
            if ((es.y+ es.height)>max) max = es.y+ es.height;
        }
        return max;
    }

    public void deleteHiddenShips(){
        Iterator<EnemyShip> it = ships.iterator();
        while (it.hasNext()){
            EnemyShip es=it.next();
            if (!es.isVisible()) it.remove();
        }
    }

    public int verifyHit(List<Bullet> bullets){
        if (bullets.isEmpty()) return 0;
        int sum = 0;
        for (EnemyShip es:ships) {
            for (Bullet b:bullets) {
                if (es.isCollision(b) && es.isAlive && b.isAlive) {
                    es.kill();
                    b.kill();
                    sum += es.score;
                }
            }
        }
        return sum;
    }
    public Bullet fire(Game game){
        if (ships.isEmpty()) return null;
        if (game.getRandomNumber(100 / SpaceInvadersGame.COMPLEXITY ) > 0)
            return null;

        return ships.get(game.getRandomNumber(ships.size())).fire();
    }

    public void move(){
        if (ships.isEmpty()) return;

        if (direction == Direction.LEFT && getLeftBorder() < 0) {
            direction = Direction.RIGHT;
            for (EnemyShip es:ships) {
                es.move(Direction.DOWN, getSpeed());
            }
        }else
            if (direction == Direction.RIGHT && getRightBorder() > SpaceInvadersGame.WIDTH){
                direction = Direction.LEFT;
                for (EnemyShip es:ships) {
                    es.move(Direction.DOWN, getSpeed());
                }
            }else
                for (EnemyShip es:ships) {
                    es.move(direction, getSpeed());
                }

        getSpeed();
    }

    private double getSpeed(){
        double min = 2.0;
        double num = 3.0/ships.size();
        if ( num < min) min = num;
        return min;
    }

    private double getLeftBorder(){
        double minX = ships.get(0).x;
        for (int i = 1; i < ships.size(); i++) {
            if (ships.get(i).x < minX)
               minX = ships.get(i).x;
        }
        return minX;
    }

    private double getRightBorder(){
        double maxX = ships.get(0).x+ships.get(0).width;
        for (int i = 1; i < ships.size(); i++) {
            if ((ships.get(i).x+ships.get(i).width) > maxX)
                maxX = ships.get(i).x+ships.get(i).width;
        }
        return maxX;
    }

    public void draw(Game game){
        for (EnemyShip es:ships) {
            es.draw(game);
        }
    }

    public EnemyFleet() {
        createShips();
    }

    private void createShips(){
        ships = new ArrayList<EnemyShip>();
        for (int y = 0; y < ROWS_COUNT; y++) {
            for (int x = 0; x < COLUMNS_COUNT; x++) {
                ships.add(new EnemyShip(x * STEP, y * STEP + 12));
            }
        }
        ships.add(new Boss(STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));
    }
}
