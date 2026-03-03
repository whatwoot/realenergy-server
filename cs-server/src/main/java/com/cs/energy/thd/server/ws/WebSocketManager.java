package com.cs.energy.thd.server.ws;

import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * @authro fun
 * @date 2025/11/24 16:33
 */
public class WebSocketManager {
    private OkHttpClient client;
    private WebSocket webSocket;
    private MessageHandler messageHandler;
    private boolean isConnected = false;
    private String wsUrl;

    private static final int NORMAL_CLOSURE = 1000;
    private static final int RECONNECT_DELAY = 5000;

    public WebSocketManager(String url) {
        this.wsUrl = url;
        this.messageHandler = new MessageHandler();
        this.client = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public synchronized void connect() {
        if (isConnected) {
            System.out.println("WebSocket 已经连接");
            return;
        }

        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                isConnected = true;
                messageHandler.setWebSocket(webSocket);
                System.out.println("WebSocket 连接成功");
                onConnectionEstablished();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                messageHandler.handleTextMessage(text);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                isConnected = false;
                System.out.println("连接关闭中: " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                isConnected = false;
                System.out.println("连接已关闭");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                isConnected = false;
                System.err.println("连接失败: " + t.getMessage());
                scheduleReconnect();
            }
        });
    }

    private void onConnectionEstablished() {
        // 连接建立后的初始化操作
        sendMessage("{\"type\": \"register\", \"clientId\": \"java-client\"}");
    }

    private void scheduleReconnect() {
        System.out.println(RECONNECT_DELAY + "ms 后尝试重连...");
        new Thread(() -> {
            try {
                Thread.sleep(RECONNECT_DELAY);
                connect();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void sendMessage(String message) {
        if (isConnected && webSocket != null) {
            webSocket.send(message);
        } else {
            System.err.println("WebSocket 未连接，无法发送消息");
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(NORMAL_CLOSURE, "用户请求关闭");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
