package org.darkmentat.LandOfLords.SimplePcClient.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TCPClient {
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private final String mHost;
    private final int mPort;

    private Socket mSocket;
    private Scanner mInputStream;
    private DataOutputStream mOutputStream;

    private Optional<Consumer<String>> mOnReceiveData = Optional.empty();
    private Optional<Consumer<Exception>> mOnError = Optional.empty();

    public TCPClient(String host, int port) {
        mHost = host;
        mPort = port;
    }

    public void setOnReceiveData(Consumer<String> onReceiveData) {
        mOnReceiveData = Optional.of(onReceiveData);
    }
    public void setOnError(Consumer<Exception> onError) {
        mOnError = Optional.of(onError);
    }

    public void connect(){
        try {
            mSocket = new Socket(mHost, mPort);
            mInputStream = new Scanner(mSocket.getInputStream());
            mOutputStream = new DataOutputStream(mSocket.getOutputStream());

            mExecutor.submit((Runnable) this::receive);
        }
        catch (IOException e) {
            mOnError.ifPresent(c -> c.accept(e));
        }
    }
    public void close(){
        if (mSocket == null) return;

        try {
            mSocket.getInputStream().close();
            mSocket.close();
            mExecutor.shutdownNow();
        }
        catch (Exception e) {
            mOnError.ifPresent(c -> c.accept(e));
        }
    }

    public void send(String message){
        try {
            mOutputStream.writeUTF(message);
        } catch (IOException e) {
            mOnError.ifPresent(c -> c.accept(e));
        }
    }
    public void receive(){
        while(mInputStream.hasNext()) {
            mOnReceiveData.ifPresent(c -> c.accept(mInputStream.nextLine()));
        }
    }
}


