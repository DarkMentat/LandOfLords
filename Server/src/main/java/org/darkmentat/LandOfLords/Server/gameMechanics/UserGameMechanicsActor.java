package org.darkmentat.LandOfLords.Server.gameMechanics;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.google.protobuf.GeneratedMessage;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.GameObject;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Movable;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.PlayerUnit;
import org.darkmentat.LandOfLords.Server.gameMechanics.gameObjects.Positionable;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.darkmentat.LandOfLords.Common.NetMessagesToClient.PlayerUnitState;
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

    private Set<Movable> mMovingGameObjects = new HashSet<>();

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
        mMovingGameObjects.removeIf(go -> go.getBasicState() != GameObject.GameObjectState.MOVING);
        mMovingGameObjects.forEach(Movable::performMoving);

        mPlayerUnit.ifPresent(unit -> mNetClient.ifPresent(a -> a.tell(makeStateMsg(unit), self())));
    }

    private void onSpawnPlayerUnit(SpawnPlayerUnit msg) {
        String script = "src/main/lua/org.darkmentat.LandOfLords.Server.scripts/PlayerUnit.lua";

        PlayerUnit playerUnit = new PlayerUnit(mLogin, "Player", mLuaGlobals.loadfile(script).call());

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
        PlayerUnitState.Builder stateMsg = PlayerUnitState.newBuilder();

        stateMsg.setGameObjectState(player.getBasicState().name());

        stateMsg.setX(((Positionable) player).getX());
        stateMsg.setY(((Positionable) player).getY());

        PlayerUnitState.CellInfo.Builder cellBuilder = PlayerUnitState.CellInfo.newBuilder()
                .setDescription(GameMap.Instance.getCellDescription((Positionable) player));

        for (Positionable p : GameMap.Instance.getPositionablesOnCell((Positionable) player)) {
            GameObject go = ((GameObject) p);
            cellBuilder.addUnits("[" + go.OwnerLogin + "] " + go.Name);
        }

        stateMsg.addCellsAround(cellBuilder);

        for (Map.Entry<String, String> entry : player.getStateValues().entrySet()) {
            stateMsg.addStateValue(PlayerUnitState.KeyValueTupple.newBuilder()
                    .setKey(entry.getKey())
                    .setValue(entry.getValue())
                    .build());
        }

        return stateMsg.build();
    }
}
