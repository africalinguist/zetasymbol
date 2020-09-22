package com.afyber.game.api.overworld;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Collision extends WorldObject {

    private int[] collisionDimensions;

    public Collision(int x, int y, int width, int height) {
        super(x, y);
        collisionDimensions = new int[2];
        collisionDimensions[0] = width;
        collisionDimensions[1] = height;
    }

    @Override
    public void render(SpriteBatch batch, int cameraX, int cameraY) {
        // none
    }

    public boolean isColliding(int x, int y, int width, int height) {
        // FIXME: you can get 0.5 pixels close to right and up walls but only 1 pixel close to left and down walls
        if (worldPos[0] + collisionDimensions[0] >= x && x + width >= worldPos[0]) {
            if (worldPos[1] + collisionDimensions[1] >= y && y + height >= worldPos[1]) {
                return true;
            }
        }
        return false;
    }
}
