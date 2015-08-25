package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaValue;

public class PlayerUnit extends GameObject implements Movable {
    public PlayerUnit(String ownerLogin, String name, LuaValue script) {
        super(ownerLogin, name, script);
    }
}
