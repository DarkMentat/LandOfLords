package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaValue;

public class GameObject implements WithLuaState{
    public enum GameObjectState { MOVING, STAYING }

    public final String OwnerLogin;
    public final String Name;
    public final LuaValue GameObjectScript;

    public GameObject(String ownerLogin, String name, LuaValue script) {
        OwnerLogin = ownerLogin;
        Name = name;
        GameObjectScript = script;
    }

    public LuaValue getGameObjectScript(){
        return GameObjectScript;
    }
}
