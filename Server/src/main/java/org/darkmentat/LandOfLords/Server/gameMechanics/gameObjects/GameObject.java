package org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects;

public abstract class GameObject {
    public final String OwnerLogin;

    public GameObject(String ownerLogin) {
        OwnerLogin = ownerLogin;
    }
}
