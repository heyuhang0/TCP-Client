package com.hyh0.tcp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

@FunctionalInterface
interface MessageHandler {
    boolean handler(String s);
}

public class TCPClient {

    private Queue<String> outputQ = new LinkedList<>();
    // private LinkedList<String> inputQ = new LinkedList<>();
    private Set<MessageHandler> handlers = new HashSet<>();
    private Socket client;
    private PrintStream out;
    private BufferedReader buf;
    private boolean closed;

    private String address;
    private int port;

    public TCPClient(String address, int port) throws IOException {
        this.address = address;
        this.port = port;
        this.closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() throws IOException {
        closed = true;
        out.close();
        buf.close();
        client.close();
    }

    public void sendMessage(String message) {
        outputQ.add(message);
    }

    public void addListener(MessageHandler listener) {
        handlers.add(listener);
    }

    public void removeListener(MessageHandler listener) {
        handlers.remove(listener);
    }

    /*
    public void forEachMessage(MessageHandler listener) {
        inputQ.removeIf(listener::handler);
    }*/

    public void startEventLoop() throws IOException {
        closed = false;
        client = new Socket(address, port);
        out = new PrintStream(client.getOutputStream());
        buf = new BufferedReader(new InputStreamReader(client.getInputStream()));

        new Thread(() -> {
            try {
                while (!closed) {
                    if (!outputQ.isEmpty()) {
                        out.println(outputQ.poll());
                    }
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                try {
                    close();
                } catch (IOException ignored) {
                }
            }
        }).start();

        new Thread(() -> {
            try {
                while (!closed) {
                    try {
                        String msg = buf.readLine();
                        if (msg == null) {
                            close();
                            System.out.println("已断开连接");
                            break;
                        }
                        handlers.forEach(l -> l.handler(msg));
                    } catch (SocketTimeoutException ignored) {}
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    close();
                } catch (IOException ignored) {
                }
            }
        }).start();
    }
}
