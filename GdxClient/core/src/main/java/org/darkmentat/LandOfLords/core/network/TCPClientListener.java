package org.darkmentat.LandOfLords.core.network;

import static org.darkmentat.LandOfLords.Common.NetMessagesToClient.PingClient;
import static org.darkmentat.LandOfLords.Common.NetMessagesToClient.PlayerUnitState;

public interface TCPClientListener {

    void onSocketError(Exception exception);
    default void onSocketClose() {}
    default void onSocketMessageReceive(PingClient ping) {}
    default void onSocketMessageReceive(PlayerUnitState playerUnitState) {}
}
