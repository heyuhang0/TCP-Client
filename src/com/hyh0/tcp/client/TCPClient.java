package com.hyh0.tcp.client;

import com.hyh0.tcp.socket.TCPSocket;

import java.io.IOException;
import java.net.Socket;


public class TCPClient extends TCPSocket{

    private String address;
    private int port;

    public TCPClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void startEventLoop() throws IOException {
        startEventLoop(new Socket(address, port));
    }
}
