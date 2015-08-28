package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaInteger;

public interface Movable extends Positionable {
    default void move(int x, int y){
        getGameObjectScript().invokemethod("move", new LuaInteger[]{LuaInteger.valueOf(x), LuaInteger.valueOf(y)});
    }
    default void stay(){
        getGameObjectScript().invokemethod("stay");
    }
    default void performMoving(){
        getState().invokemethod("performMoving");
    }
}
