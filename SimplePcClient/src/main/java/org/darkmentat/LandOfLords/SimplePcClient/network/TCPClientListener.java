package org.darkmentat.LandOfLords.SimplePcClient.network;

import static org.darkmentat.LandOfLords.Common.NetMessagesToClient.*;

public interface TCPClientListener {

    void onError(Exception exception);
    default void onClose() {}
    default void onReceive(PingClient ping) {}
    default void onReceive(PlayerUnitState playerUnitState) {}
}
