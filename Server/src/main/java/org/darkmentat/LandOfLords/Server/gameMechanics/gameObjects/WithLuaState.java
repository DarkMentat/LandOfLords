package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Map;
import java.util.TreeMap;

public interface WithLuaState {

    LuaValue getGameObjectScript();

    default LuaValue getState(){
        return getGameObjectScript().get("State");
    }
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

    default void eval(String code){
        getGameObjectScript().invokemethod("eval", LuaString.valueOf(code));
    }
}
