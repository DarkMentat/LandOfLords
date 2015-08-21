package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

public interface Positionable {
    class Vector {
        public int X;
        public int Y;

        public Vector(int x, int y) {
            X = x;
            Y = y;
        }
    }

    Vector getPosition();
}
