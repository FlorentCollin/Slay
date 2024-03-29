package ac.umons.slay.g01.communication;

import static ac.umons.slay.g01.gui.utils.Constants.PORT;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.google.gson.Gson;

import ac.umons.slay.g01.communication.Messages.Message;
import ac.umons.slay.g01.communication.Messages.UsernameMessage;

/**
 * Classe qui permet l'envoit de message de l'interface graphique au serveur durant une partie en ligne
 */
public class OnlineMessageSender implements MessageSender {
    private SocketChannel clientChannel;
    private Selector selector;
    private Gson gson;

    public OnlineMessageSender(String username, int numberOfPlayer, String serverAddress) throws IOException {
        gson = new Gson();
        //Ouverture de la connection au serveur
        clientChannel = SocketChannel.open(new InetSocketAddress(InetAddress.getByName(serverAddress), PORT));
        clientChannel.configureBlocking(false);
        selector = Selector.open();
        //On associe le channel à un selector
        clientChannel.register(selector, SelectionKey.OP_READ);

        send(new UsernameMessage(username, numberOfPlayer)); //Envoie de l'username du Client
    }

    @Override
    public void send(Message message) {
        try {
            //Écriture du message dans le buffer du client pour que le serveur puisse le récupérer
            clientChannel.write(ByteBuffer.wrap((message.getClass().getSimpleName() + gson.toJson(message) + "+").getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            clientChannel.close();
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public SocketChannel getClientChannel() {
        return clientChannel;
    }

    public Selector getSelector() {
        return selector;
    }
}
