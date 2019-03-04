package com.gl1.slay.server;

import communication.Message;
import server.ServerListener;
import server.ServerSender;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe qui démarre les différents threads liés au serveur
 */
public class ServerLauncher {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server...");
        LinkedBlockingQueue<Message> messageToSend = new LinkedBlockingQueue<>();
        ServerListener serverListener = new ServerListener(8888, messageToSend);
        ServerSender serverSender = new ServerSender(messageToSend);
        //Démarrage des Threads
        serverSender.start();
        serverListener.start();
        System.out.println("Server is online");
    }
}