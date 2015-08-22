package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaValue;

public class GameObject {
    public enum GameObjectState { MOVING, STAYING }

    public final String OwnerLogin;
    public final String Name;
    public final LuaValue GameObjectScript;

    public GameObjectState BasicState = null;

    public GameObject(String ownerLogin, String name, LuaValue script) {
        OwnerLogin = ownerLogin;
        Name = name;
        GameObjectScript = script;

        BasicState = GameObjectState.valueOf(GameObjectScript.get("State").get("GameObjectState").tojstring());
    }
}
