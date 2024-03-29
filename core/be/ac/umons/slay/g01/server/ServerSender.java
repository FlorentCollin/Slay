
package ac.umons.slay.g01.server;

import java.io.IOException;
import org.pmw.tinylog.Logger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

import ac.umons.slay.g01.communication.Messages.Message;
import ac.umons.slay.g01.communication.Messages.NetworkMessage;
import ac.umons.slay.g01.communication.Messages.TextMessage;

/**
 * Thread qui se charge d'envoyer les messages du serveur/room aux différents clients
 */
public class ServerSender extends Thread {
    private LinkedBlockingQueue<Message> messageToSend;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private Gson gson;
    private AtomicBoolean running = new AtomicBoolean(true);

    public ServerSender(ServerSocketChannel serverChannel, Selector selector, LinkedBlockingQueue<Message> messageToSend) {
        //Lien vers la pile des messages à envoyer, c'est sur cette pile que les rooms doivent envoyer leurs messages
        this.messageToSend = messageToSend;
        this.serverChannel = serverChannel;
        this.selector = selector;
        gson = new Gson();
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                //Récupération du message dans la file d'attente
                Message message = messageToSend.take(); //Remarque cette méthode est bloquante
                //Si le message est un message qui peut être envoyé à des clients
                if (serverChannel.isOpen()  && selector.isOpen() && message instanceof NetworkMessage) {
                    NetworkMessage networkMessage = (NetworkMessage) message;
                    String messageStr = message.getClass().getSimpleName() + gson.toJson(message) + "+";
                    ArrayList<String> messageParts = new ArrayList<>();
                    int length = messageStr.length();
                    for (int i = 0; i < length; i += 20000) {
                        messageParts.add(messageStr.substring(i, Math.min(length, i + 20000)));
                    }
                    for (String str : messageParts) {
                        for (Client client : networkMessage.getClients()) { //Envoie du message à tous les clients
                            ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
                            SocketChannel clientChannel = client.getSocketChannel();
                            if (clientChannel.isConnected()) {
                                /* Écriture du message dans le buffer du client
                                 * Ici on écrit le nom de la classe du message en plus du message sérialisé
                                 * Pour permettre au client de retrouver le type du message
                                 * Le "+" est le caractère signalisant la fin du message */
                                while (buffer.hasRemaining()) {
                                    if(!clientChannel.isConnected())
                                        break;
                                    if(clientChannel.write(buffer) == 0){
                                        clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                    }
                                }
                            }
                        }
                    }
                } else if(message instanceof TextMessage && ((TextMessage) message).getMessage().equals("close")) {
                    running.set(false);
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace(); //TODO
            }
        }
        Logger.info("ServerSender is close");
    }

    public void stopRunning() {
        try {
            messageToSend.put(new TextMessage("close"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
