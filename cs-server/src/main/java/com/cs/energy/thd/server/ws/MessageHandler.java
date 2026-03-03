package com.cs.energy.thd.server.ws;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.WebSocket;

/**
 * @authro fun
 * @date 2025/11/24 16:34
 */
public class MessageHandler {
    private WebSocket webSocket;

    public void handleTextMessage(String text) {
        try {
            // 解析 JSON 消息
            Message message = JSONObject.parseObject(text, Message.class);

            switch (message.getType()) {
                case "chat":
                    handleChatMessage(message);
                    break;
                case "notification":
                    handleNotification(message);
                    break;
                case "heartbeat":
                    handleHeartbeat();
                    break;
                default:
                    System.out.println("未知消息类型: " + message.getType());
            }
        } catch (Exception e) {
            System.err.println("消息处理错误: " + e.getMessage());
        }
    }

    private void handleChatMessage(Message message) {
        System.out.println("聊天消息 - 来自: " + message.getFrom() +
                ", 内容: " + message.getContent());
    }

    private void handleNotification(Message message) {
        System.out.println("通知: " + message.getContent());
    }

    private void handleHeartbeat() {
        // 回复心跳
        if (webSocket != null) {
            webSocket.send("{\"type\": \"heartbeat\", \"status\": \"alive\"}");
        }
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    // 消息数据类
    public static class Message {
        private String type;
        private String from;
        private String content;
        private long timestamp;

        // getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
