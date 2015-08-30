package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

public interface Movable extends Positionable {

    default void performMoving(){
        getState().invokemethod("performMoving");
    }
}
