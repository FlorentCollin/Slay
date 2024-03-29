package ac.umons.slay.g01.communication;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;

import ac.umons.slay.g01.communication.Messages.Message;
import ac.umons.slay.g01.gui.utils.GsonInit;

/**
 * Class qui représente un listener, elle s'occupe de récupérer les messages envoyés par le serveur et de les exécuter
 */
public class OnlineMessageListener extends MessageListener{

    private final SocketChannel clientChannel;
    private final Selector selector;
    private Gson gson;

    public OnlineMessageListener(SocketChannel clientChannel, Selector selector) {
        this.clientChannel = clientChannel;
        this.selector = selector;
        gson = GsonInit.initGson();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("OnlineMessageListener");
        running.set(true);
        //Boucle infinie tant que le client est connecté au serveur
        while(running.get() && clientChannel.isConnected()) {
            try {
                readFromServer();
            } catch (IOException e) {
                stopRunning();
            }
        }
    }

    /**
     * Méthode qui va lire les messages du serveur et les exécuter.
     * @throws IOException
     */
    private void readFromServer() throws IOException {
        Iterator<SelectionKey> keyIterator;
        if(selector.select() != 0) {
            keyIterator = selector.selectedKeys().iterator();
            while(keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if(key.isReadable()) { //Si le serveur à envoyé un message
                    ArrayList<Message> messages = Message.readFromKey(key, gson);
                    for(Message message : messages) {
                    }
                    messages.forEach(this::executeMessage);
                }
                keyIterator.remove();
            }
        }
    }
}
