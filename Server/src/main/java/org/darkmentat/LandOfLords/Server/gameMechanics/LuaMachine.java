package org.darkmentat.LandOfLords.Server.gameMechanics;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaMachine {
    private final Globals mLuaGlobals;

    public LuaMachine() {

        mLuaGlobals = JsePlatform.standardGlobals();
    }

    public LuaValue loadPlayerUnit(){
        String script = "src/main/lua/org.darkmentat.LandOfLords.Server.scripts/PlayerUnit.lua";

        return mLuaGlobals.loadfile(script).call();
    }
}
