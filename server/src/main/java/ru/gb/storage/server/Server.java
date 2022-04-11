package ru.gb.storage.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    private void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 8080));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started");

        while(true){
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    System.out.println("New selector acceptable event");
                    register(selector, serverSocketChannel);
                }

                if (selectionKey.isReadable()) {
                    System.out.println("New selector readable event");
                    readMessage(selectionKey);
                }
                iterator.remove();
            }
        }
    }
    private void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);

        client.register(selector, SelectionKey.OP_READ,  new StringBuilder());
        System.out.println("New client is connected");
    }
    private void readMessage(SelectionKey selectionKey) throws IOException {
        SocketChannel client = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        client.read(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        String message = new String(bytes);
        System.out.println("New message: " + message.trim());
        if(selectionKey.attachment() != null) {
            StringBuilder sb = (StringBuilder) selectionKey.attachment();
            sb.append(message.trim());
            if (message.endsWith("\n")) {
                sb.append("\n");
                byte[] sendBytes = sb.toString().getBytes();
                ByteBuffer sendBuffer = ByteBuffer.allocate(sendBytes.length);
                sendBuffer.put(sendBytes);
                sendBuffer.flip();
                client.write(sendBuffer);
                sb.setLength(0);
            }
        } else {
            client.write(ByteBuffer.wrap(message.getBytes()));
        }
    }
}