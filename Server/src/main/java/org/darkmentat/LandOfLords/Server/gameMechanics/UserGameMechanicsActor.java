package org.darkmentat.LandOfLords.Server.gameMechanics;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.google.protobuf.GeneratedMessage;
import org.darkmentat.LandOfLords.Common.NetMessagesToClient;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.GameObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.*;

import static org.darkmentat.LandOfLords.Common.NetMessagesToServer.SpawnPlayerUnit;
import static org.darkmentat.LandOfLords.Server.network.NetworkClientActor.LoginClientActor;
import static org.darkmentat.LandOfLords.Server.network.NetworkClientActor.UnLoginClientActor;


public class UserGameMechanicsActor extends AbstractActor {
    public static class HeartbeatTick {}

    public static String getPath(String login){
        return GameMechanicsActor.ADDRESS + "/userGM_" + login;
    }

    private final String mLogin;
    private final Globals mLuaGlobals;

    private Optional<ActorRef> mNetClient = Optional.empty();

    private Optional<GameObject> mPlayerUnit = Optional.empty();

    private Set<GameObject> mMovingGameObjects = new HashSet<>();

    public UserGameMechanicsActor(String login) {
        mLogin = login;

        receive(ReceiveBuilder
                .match(HeartbeatTick.class, this::onHeartbeatTick)
                .match(LoginClientActor.class, this::onLoginNetClient)
                .match(UnLoginClientActor.class, this::onUnLoginNetClient)
                .match(SpawnPlayerUnit.class, this::onSpawnPlayerUnit)
                .build());

        mLuaGlobals = JsePlatform.standardGlobals();
    }
    private void onHeartbeatTick(HeartbeatTick tick) {
        mMovingGameObjects.removeIf(go -> go.BasicState != GameObject.GameObjectState.MOVING);
        mMovingGameObjects.forEach(go -> go.GameObjectScript.get("State").invokemethod("move"));

        mPlayerUnit.ifPresent(unit -> mNetClient.ifPresent(a -> a.tell(makeStateMsg(unit), self())));
    }

    private void onSpawnPlayerUnit(SpawnPlayerUnit msg) {
        String script = "src/main/lua/org.darkmentat.LandOfLords.Server.scripts/GameObjectPrototype.lua";

        GameObject playerUnit = new GameObject(mLogin, "Player", mLuaGlobals.loadfile(script).call());

        mMovingGameObjects.add(playerUnit);

        mPlayerUnit = Optional.of(playerUnit);
    }

    private void onLoginNetClient(LoginClientActor login) {
        mNetClient = Optional.of(login.Actor);
    }
    private void onUnLoginNetClient(UnLoginClientActor unlogin) {
        mNetClient = Optional.empty();
    }

    private GeneratedMessage makeStateMsg(GameObject player){
        return NetMessagesToClient.PlayerUnitState.newBuilder()
                .setX(player.GameObjectScript.get("State").get("X").toint())
                .setY(player.GameObjectScript.get("State").get("Y").toint())
                .setDx(player.GameObjectScript.get("State").get("DirectionX").toint())
                .setDy(player.GameObjectScript.get("State").get("DirectionY").toint())
                .build();
    }
}
