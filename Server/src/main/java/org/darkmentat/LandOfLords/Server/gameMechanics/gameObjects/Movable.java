package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.darkmentat.LandOfLords.Server.gameMechanics.GameMap;

public interface Movable extends Positionable {
    default void performMoving(){
        if(getBasicState() != GameObject.GameObjectState.MOVING)
            return;

        getState().invokemethod("move");
        GameMap.Instance.invalidatePositionableCoordinates(this);
    }
}
