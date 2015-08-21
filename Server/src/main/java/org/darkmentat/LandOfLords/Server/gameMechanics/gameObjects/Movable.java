package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

public interface Movable extends Positionable{
    Vector getDirection();
    void setDirection(Vector direction);

    default void move(){
        Vector position = getPosition();
        Vector direction = getDirection();

        position.X += direction.X;
        position.Y += direction.Y;
    }
}
