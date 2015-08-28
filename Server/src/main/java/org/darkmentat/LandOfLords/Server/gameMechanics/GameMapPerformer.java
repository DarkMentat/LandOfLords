package org.darkmentat.LandOfLords.Server.gameMechanics;

import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.GameObjectState;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Movable;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Observational;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Positionable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Observational.*;

public class GameMapPerformer {
    private final GameMap mMap = GameMap.Instance;
    private final Set<Movable> mMovables = new HashSet<>();
    private final Set<Observational> mObservationals = new HashSet<>();

    public GameMapPerformer() { }

    public void registerPositionable(Positionable positionable){
        if (positionable instanceof Movable) {
            mMovables.add((Movable) positionable);
        }

        if (positionable instanceof Observational) {
            mObservationals.add((Observational) positionable);
        }
    }

    public void performActions(){
        mMovables.stream().filter(m -> m.getBasicState() == GameObjectState.MOVING).forEach(Movable::performMoving);
        mMovables.stream().filter(m -> m.getBasicState() == GameObjectState.MOVING).forEach(mMap::invalidatePositionableCoordinates);

        mObservationals.forEach(o -> o.setSurroundingsInfo(getSurroundingsCellInfo(o)));
    }

    private CellInfo[] getSurroundingsCellInfo(Observational observational){

        ArrayList<CellInfo> result = new ArrayList<>();

        int radius = observational.getSurroundingRadius();
        int obsX = observational.getX();
        int obsY = observational.getY();

        for (int x = obsX - radius; x <= obsX + radius; x++) {
            for (int y = obsY - radius; y <= obsY + radius; y++) {
                String desc = mMap.getCellDescription(observational);
                String[] poss = Arrays.stream(mMap.getPositionablesOnCell(observational))
                        .map(Object::toString)
                        .toArray(String[]::new);

                result.add(new CellInfo(x,y,desc, poss));
            }
        }

        return result.toArray(new CellInfo[result.size()]);
    }
}
