package ru.geekbrains.ryabov;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Server {
    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException {
        Scanner sc = new Scanner(System.in);
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress("localhost", 9000));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server Started");

        while (true) {
            selector.select();
            System.out.println("New selector event");
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    System.out.println("New selector acceptable event");
                    registr(selector, serverSocket);
                }
                if (selectionKey.isReadable()) {
                    System.out.println("New selector readable event");
                    readMessage(selectionKey);


                }
                iterator.remove();
            }
        }
    }

        private void registr (Selector selector, ServerSocketChannel serverSocked) throws IOException {
            SocketChannel client = serverSocked.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            //   client.register(selector, SelectionKey.OP_WRITE);
            System.out.println("New client is connected");
        }
        private void readMessage (SelectionKey selectionKey) throws IOException {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(60);
            client.read(buffer);
            String message = new String(buffer.array());
            System.out.println("New message: " + message + " Thread name " + Thread.currentThread().getName());
         //   buffer.flip();
         //   buffer.clear();
            client.write(buffer.wrap(String.format("Echo: %s", message).getBytes(StandardCharsets.UTF_8)));
        }


}
