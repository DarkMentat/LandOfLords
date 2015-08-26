package org.darkmentat.LandOfLords.Server.gameMechanics;

import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Positionable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
    public static GameMap Instance = new GameMap();

    private Map<String, String> mCellDescriptions = new HashMap<>();
    private Map<String, List<Positionable>> mPositionablesInCells = new HashMap<>();
    private Map<Positionable, String> mPositionablesCoordinates = new HashMap<>();

    private GameMap() {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                String coords = coordToString(x, y);

                mCellDescriptions.put(coords, "Some cell");
                mPositionablesInCells.put(coords, new ArrayList<>());
            }
        }
    }

    public String getCellDescription(Positionable object) {
        return getCellDescription(object.getX(), object.getY());
    }
    public Positionable[] getPositionablesOnCell(Positionable object) {
        return getPositionablesOnCell(object.getX(), object.getY());
    }

    public String getCellDescription(int x, int y) {
        return mCellDescriptions.get(coordToString(x, y));
    }
    public Positionable[] getPositionablesOnCell(int x, int y) {
        List<Positionable> gameObjects = mPositionablesInCells.get(coordToString(x, y));

        return gameObjects.toArray(new Positionable[gameObjects.size()]);
    }

    public void invalidatePositionableCoordinates(Positionable object){
        String oldCoords = mPositionablesCoordinates.getOrDefault(object, "");

        if(!oldCoords.isEmpty()){
            mPositionablesCoordinates.remove(object);
            mPositionablesInCells.get(oldCoords).remove(object);
        }

        String coords = coordToString(object.getX(), object.getY());
        mPositionablesCoordinates.put(object, coords);
        mPositionablesInCells.get(coords).add(object);
    }

    private String coordToString(int x, int y) {
        return String.valueOf(x) + ", " + String.valueOf(y);
    }
    private int xFromCoordString(String coords) {
        String[] xy = coords.split(", ");

        return Integer.parseInt(xy[0]);
    }
    private int yFromCoordString(String coords) {
        String[] xy = coords.split(", ");
        return Integer.parseInt(xy[1]);
    }
}