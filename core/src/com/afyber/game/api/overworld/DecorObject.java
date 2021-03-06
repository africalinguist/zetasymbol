package com.afyber.game.api.overworld;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DecorObject extends Collision {

    boolean collidable;

    public DecorObject(int x, int y, TextureRegion sprite, boolean collidable) {
        super(x, y, sprite.getRegionWidth(), sprite.getRegionHeight());
        this.sprite = sprite;
        this.collidable = collidable;
    }

    public void render(SpriteBatch batch, int cameraX, int cameraY) {
        batch.draw(sprite, this.worldPos[0] - cameraX, this.worldPos[1] - cameraY);
    }

    public boolean isColliding(float x, float y, float width, float height) {
        if (collidable) {
            super.isColliding(x, y, width, height);
        }
        return false;
    }
}
