package com.javarush.games.spaceinvaders.gameobjects;

import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ship extends GameObject {
    public boolean isAlive = true;
    private boolean loopAnimation = false;
    private List<int[][]> frames;
    private int frameIndex;

    public void setAnimatedView(boolean isLoopAnimation, int[][]... viewFrames){
        loopAnimation = isLoopAnimation;
        setMatrix(viewFrames[0]);
        frames = Arrays.asList(viewFrames);
        frameIndex = 0;
    }
    /*public void setAnimatedView(int[][]... viewFrames){
        super.setMatrix(viewFrames[0]);
        frames = Arrays.asList(viewFrames);
        frameIndex = 0;
    }*/
    public boolean isVisible(){
        if (!isAlive && frameIndex >= frames.size())
            return false;
        else
            return true;
    }

    @Override
    public void draw(Game game) {
        super.draw(game);
        nextFrame();
    }

    public void nextFrame(){
        ++frameIndex;
        if (frameIndex >= frames.size() && !loopAnimation) return;
        if (frameIndex >= frames.size() && loopAnimation) frameIndex = 0;
        matrix = frames.get(frameIndex);
    }

    public void kill(){
        isAlive = false;
    }

    public Bullet fire(){
        return null;
    }

    public void setStaticView(int[][] viewFrame){
        super.setMatrix(viewFrame);
        frames = new ArrayList<int[][]>();
        frames.add(viewFrame);
        frameIndex = 0;
    }

    public Ship(double x, double y) {
        super(x, y);
    }
}
