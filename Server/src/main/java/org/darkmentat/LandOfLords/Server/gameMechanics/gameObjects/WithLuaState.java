package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Map;
import java.util.TreeMap;

import static org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.GameObject.*;

public interface WithLuaState {
    LuaValue getState();

    default GameObjectState getBasicState(){
        return GameObjectState.valueOf(getState().get("GameObjectState").tojstring());
    }
    default Map<String, String> getStateValues(){
        Map<String, String> result = new TreeMap<>();
        LuaTable mapValues = getState().invokemethod("getStateValues").arg1().checktable();

        for (LuaValue key : mapValues.keys()) {
            LuaValue value = mapValues.get(key);

            result.put(key.tojstring(), value.tojstring());
        }

        return result;
    }
}
