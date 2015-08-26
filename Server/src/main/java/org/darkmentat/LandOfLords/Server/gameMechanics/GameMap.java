package org.darkmentat.LandOfLords.Server.gameMechanics;

import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.GameObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
    public static GameMap Instance = new GameMap();

    private Map<String, String> mCellDescriptions = new HashMap<>();
    private Map<String, List<GameObject>> mGameObjectsInCells = new HashMap<>();

    private GameMap() {}

    public String getCellDescription(int x, int y) {
        return mCellDescriptions.get(coordToString(x,y));
    }
    public GameObject[] getGameObjectsOnCell(int x, int y) {
        List<GameObject> gameObjects = mGameObjectsInCells.get(coordToString(x, y));

        return gameObjects.toArray(new GameObject[gameObjects.size()]);
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