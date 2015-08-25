package org.darkmentat.LandOfLords.Server.network;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.japi.pf.ReceiveBuilder;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class FrontNetworkActor extends AbstractActor {
    public static final String ADDRESS = "akka://LandOfLordsServer/user/networking";

    public FrontNetworkActor() {
        ActorRef network = Tcp.get(getContext().system()).manager();
        network.tell(TcpMessage.bind(self(), new InetSocketAddress("localhost", 8080), 100), self());

        receive(ReceiveBuilder
                .match(Tcp.Connected.class, this::onConnect)
                .build());
    }

    private void onConnect(Tcp.Connected connected){
        ActorRef handler = getContext().actorOf(Props.create(NetworkClientActor.class, sender()));
        sender().tell(TcpMessage.register(handler), self());
    }
}
