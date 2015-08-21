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
    public static class GetClientActor {
        public final String Login;

        public GetClientActor(String login) {
            Login = login;
        }
    }
    public static class LoginClientActor {
        public final String Login;
        public final ActorRef Actor;

        public LoginClientActor(String login, ActorRef actor) {
            Login = login;
            Actor = actor;
        }
    }
    public static class UnLoginClientActor {
        public final String Login;

        public UnLoginClientActor(String login) {
            Login = login;
        }
    }

    private HashMap<String, ActorRef> mClientActors = new HashMap<>();

    public FrontNetworkActor() {
        ActorRef network = Tcp.get(getContext().system()).manager();
        network.tell(TcpMessage.bind(self(), new InetSocketAddress("localhost", 8080), 100), self());

        receive(ReceiveBuilder
                .match(Tcp.Connected.class, this::onConnect)
                .match(GetClientActor.class, this::onGetClientActor)
                .match(LoginClientActor.class, this::onLoginClientActor)
                .match(UnLoginClientActor.class, this::onUnLoginClientActor)
                .build());
    }

    private void onConnect(Tcp.Connected connected){
        ActorRef handler = getContext().actorOf(Props.create(NetworkClientActor.class, sender()));
        sender().tell(TcpMessage.register(handler), self());
    }
    public void onLoginClientActor(LoginClientActor login){
        mClientActors.put(login.Login, login.Actor);
    }
    public void onUnLoginClientActor(UnLoginClientActor login){
        mClientActors.remove(login.Login);
    }
    private void onGetClientActor(GetClientActor client) {
        if(mClientActors.containsKey(client.Login)){
            sender().tell(mClientActors.get(client.Login), self());
        }
    }
}
