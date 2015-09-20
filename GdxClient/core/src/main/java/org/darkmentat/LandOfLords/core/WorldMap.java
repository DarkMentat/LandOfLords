package org.darkmentat.LandOfLords.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.List;

public class WorldMap {
    public static final int CELL_SIZE = 64;

    static class Cell {
        static final Texture TEXTURE = new Texture(Gdx.files.internal("cell.png"));

        private final Sprite mSprite;

        public Cell(int x, int y) {
            mSprite = new Sprite(TEXTURE);
            mSprite.setSize(CELL_SIZE, CELL_SIZE);
            mSprite.setPosition(x*CELL_SIZE,y*CELL_SIZE);
        }

        public void draw(Batch batch){
            mSprite.draw(batch);
        }
    }


    private List<Cell> mCells = new ArrayList<>();

    public WorldMap(int startX, int startY, int width, int height) {
        for (int x = startX; x < width; x++) {
            for (int y = startY; y < height; y++) {
                mCells.add(new Cell(x,y));
            }
        }
    }

    public void draw(Batch batch){
        mCells.forEach(c -> c.draw(batch));
    }
}
