package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

public interface Positionable extends WithLuaState {
    default int getX() {
        return getGameObjectScript().get("X").toint();
    }
    default int getY() {
        return getGameObjectScript().get("Y").toint();
    }
}