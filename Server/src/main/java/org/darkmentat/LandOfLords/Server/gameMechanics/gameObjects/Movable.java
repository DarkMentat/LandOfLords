package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

public interface Movable extends WithLuaState {
    default void performMoving(){
        if(getBasicState() != GameObject.GameObjectState.MOVING)
            return;

        getState().invokemethod("move");
    }
}
