package ru.geekbrains.ryabov;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(1);
    private Scanner sc;

    public static void main(String[] args) {
        try {
            new Client().start();
        } finally {
            THREAD_POOL.shutdown();
        }

    }

    private void start() {
            THREAD_POOL.execute(() -> {
                System.out.println("New client started on thread: " + Thread.currentThread().getName());
                ByteBuffer buffer = ByteBuffer.allocate(60);
                try {
                    SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost", 9000));
                    sc = new Scanner(System.in);
                    String s = sc.nextLine();
                    while(channel.isOpen()){
                        channel.write(buffer.wrap(String.format(
                                "Message from client: %s ",
                                s
                        ).getBytes(StandardCharsets.UTF_8)));
                      //  buffer.flip();
                      //  buffer.clear();
                        channel.read(buffer);
                        String q = new String(buffer.array());
                        System.out.println("Echo: " + q);
                        break;
                }
                } catch (IOException e){
                    e.printStackTrace();
               }
            });
        }
}
//не смог пока сделать так, что бы программа не прекращала работы после первого ввода слова