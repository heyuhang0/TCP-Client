package com.hyh0.tcp.socket;

@FunctionalInterface
public interface MessageHandler {
    boolean handler(TCPSocket socket, String msg);
}

