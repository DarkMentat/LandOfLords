package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.Arrays;

public interface Observational extends Positionable {
    class CellInfo {
        public int X;
        public int Y;
        public String Description;
        public LuaTable Positionables;

        public CellInfo(int x, int y, String description, LuaTable positionables) {
            X = x;
            Y = y;
            Description = description;
            Positionables = positionables;
        }
        public CellInfo(int x, int y, String description, String[] positionables) {
            this(x,y,description,LuaTable.listOf(Arrays.stream(positionables).map(LuaString::valueOf).toArray(LuaValue[]::new)));
        }

        public String[] getPositionables(){
            int possLen = Positionables.keyCount();
            String[] possJ = new String[possLen];

            for (int j = 1; j <= possLen; j++) {
                possJ[j-1] = Positionables.get(j).tojstring();
            }

            return possJ;
        }
    }

    default void setSurroundingsInfo(CellInfo[] cells){
        LuaValue[] s = Arrays.stream(cells).map(CoerceJavaToLua::coerce).toArray(LuaValue[]::new);
        LuaTable cs = LuaTable.listOf(s);
        getGameObjectScript().invokemethod("setSurroundings", cs);
    }
    default CellInfo[] getSurroundingsInfo(){
        LuaTable s = getGameObjectScript().get("Surroundings").checktable();
        int len = s.keyCount();

        ArrayList<CellInfo> result = new ArrayList<>();

        for (int i = 1; i <= len; i++) {
            int x = s.get(i).get("X").toint();
            int y = s.get(i).get("Y").toint();
            String description = s.get(i).get("Description").tojstring();
            LuaTable possL = s.get(i).get("Positionables").checktable();

            CellInfo cellInfo = new CellInfo(x, y, description, possL);

            result.add(cellInfo);
        }

        return result.toArray(new CellInfo[len]);
    }
    default int getSurroundingRadius(){
        return getGameObjectScript().invokemethod("getSurroundingRadius").arg1().toint();
    }
}
