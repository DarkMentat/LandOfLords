package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaValue;

public class GameObject {
    public enum GameObjectState { MOVING, STAYING, WORKING, WAITING }

    public final String OwnerLogin;
    public final String Name;

    public GameObjectState BasicState = GameObjectState.STAYING;

    public LuaValue Parameters;
    public LuaValue Skills;
    public LuaValue Items;
    public LuaValue FullState;
    public LuaValue Behaviour;
    public LuaValue Stimuli;

    // public String Memory;

    public GameObject(String ownerLogin, String name) {
        OwnerLogin = ownerLogin;
        Name = name;
    }
}
