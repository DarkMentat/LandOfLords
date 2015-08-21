package org.darkmentat.LandOfLords.Server.gameMechanics;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.google.protobuf.GeneratedMessage;
import org.darkmentat.LandOfLords.Common.NetMessagesToClient;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.GameObject;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Movable;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Positionable;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.SpawnPlayerUnit;
import static org.darkmentat.LandOfLords.Server.network.NetworkClientActor.LoginClientActor;
import static org.darkmentat.LandOfLords.Server.network.NetworkClientActor.UnLoginClientActor;


public class UserGameMechanicsActor extends AbstractActor {
    public static class HeartbeatTick {
        public enum HeartbeatMission { MOVING, ACTING }

        public final HeartbeatMission Mission;

        public HeartbeatTick(HeartbeatMission mission) {
            Mission = mission;
        }
    }

    public static String getPath(String login){
        return GameMechanicsActor.ADDRESS + "/userGM_" + login;
    }

    private final String mLogin;

    private Optional<ActorRef> mNetClient = Optional.empty();

    private Map<String, GameObject> mActiveGameObjects = new HashMap<>();
    private Optional<Unit> mPlayerUnit = Optional.empty();

    public UserGameMechanicsActor(String login) {
        mLogin = login;

        receive(ReceiveBuilder
                .match(HeartbeatTick.class, this::onHeartbeatTick)
                .match(GameObject[].class, this::onGameObjectsReceived)
                .match(LoginClientActor.class, this::onLoginNetClient)
                .match(UnLoginClientActor.class, this::onUnLoginNetClient)
                .match(SpawnPlayerUnit.class, this::onSpawnPlayerUnit)
                .build());

    }
    private void onHeartbeatTick(HeartbeatTick tick) {
        switch (tick.Mission) {
            case MOVING:
                mActiveGameObjects.values().forEach(go -> {if(go instanceof Movable) ((Movable)go).move();});
                mPlayerUnit.ifPresent(Movable::move);
                break;
            case ACTING:
                mNetClient.ifPresent(net->mPlayerUnit.ifPresent(u -> net.tell(getPlayerUnitState(), self())));
                break;
        }
    }

    private void onSpawnPlayerUnit(SpawnPlayerUnit msg) {
        Unit unit = new Unit(mLogin,new Positionable.Vector(msg.getX(),msg.getY()));
        unit.setDirection(new Positionable.Vector(msg.getDx(), msg.getDy()));

        mPlayerUnit = Optional.of(unit);
    }

    private void onLoginNetClient(LoginClientActor login) {
        mNetClient = Optional.of(login.Actor);
    }
    private void onUnLoginNetClient(UnLoginClientActor unlogin) {
        mNetClient = Optional.empty();
    }

    private void onGameObjectsReceived(GameObject[] gameObjects) {
        for (GameObject go : gameObjects) {
            if (!mActiveGameObjects.containsKey(go.OwnerLogin)) {
                mActiveGameObjects.put(go.OwnerLogin, go);
            }
        }
    }


    private GeneratedMessage getPlayerUnitState(){
        return NetMessagesToClient.PlayerUnitState.newBuilder()
                .setX(mPlayerUnit.get().getPosition().X)
                .setY(mPlayerUnit.get().getPosition().Y)
                .setDx(mPlayerUnit.get().getDirection().X)
                .setDy(mPlayerUnit.get().getDirection().Y)
                .build();
    }
}
