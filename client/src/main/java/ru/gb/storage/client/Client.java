package ru.gb.storage.client;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final static Scanner SC = new Scanner(System.in);
    private static SocketChannel SOCKET;
    static ByteBuffer bf = ByteBuffer.allocate(1024);

    public static void main(String[] args) {

        try {
            ecoClient();
        } finally {
            SC.close();
        }
    }
    public static void ecoClient() {
        try {
            SOCKET = SocketChannel.open(new InetSocketAddress("localhost", 8080));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client started");
        executorService.execute(()->{
            read();
        });
        readServerMassage();
    }
    public static void readServerMassage() {
        while (true) {
            String msg = SC.nextLine();
            try {
                SOCKET.write(ByteBuffer.wrap(msg.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Server Send Massage: " + msg);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void read () {
        try {
            SOCKET.read(bf);

        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = new String(bf.array());
        System.out.println("Message from server: " + message);
    }
}