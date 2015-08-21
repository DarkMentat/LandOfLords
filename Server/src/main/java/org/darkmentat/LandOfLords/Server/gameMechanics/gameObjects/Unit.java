package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

public class Unit extends GameObject implements Movable {
    private Vector mPosition;
    private Vector mDirection;

    public Unit(String ownerLogin, Vector position) {
        super(ownerLogin);

        mPosition = position;
    }

    @Override public Vector getDirection() {
        return mDirection;
    }
    @Override public void setDirection(Vector direction) {
        mDirection = direction;
    }
    @Override public Vector getPosition() {
        return mPosition;
    }
}
