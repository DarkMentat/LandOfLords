package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaValue;

public class PlayerUnit implements UserGameObject, Movable, Observational {
    private final String mOwnerLogin;
    private final String mName;
    private final LuaValue mScript;

    public PlayerUnit(String ownerLogin, LuaValue script) {
        mName = "PlayerUnit";
        mOwnerLogin = ownerLogin;
        mScript = script;
    }

    @Override public String getOwnerLogin() {
        return mOwnerLogin;
    }
    @Override public String getName() {
        return mName;
    }
    @Override public LuaValue getGameObjectScript() {
        return mScript;
    }

    @Override public String toString() {
        return "[" + mOwnerLogin + "] " + mName;
    }
}