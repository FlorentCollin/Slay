package ac.umons.slay.g01.gui.graphics.screens;

import static ac.umons.slay.g01.gui.graphics.screens.animations.Animations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ac.umons.slay.g01.communication.OnlineMessageListener;
import ac.umons.slay.g01.communication.OnlineMessageSender;
import ac.umons.slay.g01.communication.Messages.JoinRoomMessage;
import ac.umons.slay.g01.communication.Messages.TextMessage;
import ac.umons.slay.g01.gui.app.Slay;
import ac.umons.slay.g01.gui.utils.JoinButton;
import ac.umons.slay.g01.gui.utils.Language;

/**
 * Menu des parties en lignes
 */
public class OnlineMenuScreen extends SubMenuScreen{

    private final Label.LabelStyle labelStyle;
    private final Table table;
    private final TextButton.TextButtonStyle textButtonStyle;
    private Table scrollTable;
    private OnlineMessageSender messageSender;
    private OnlineMessageListener messageListener;

    private TextButton createRoom;
    private TextButton refresh;

    public OnlineMenuScreen(Slay parent, Stage stage, String ip) {
        super(parent, stage, Language.bundle.get("onlineRooms"));

        //Récupération du l'image de fond
        Image background = new Image(uiSkin.getDrawable("room-background"));
        float width = background.getWidth();
        float height = background.getHeight();
        labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = textFont;

        //Création des différents boutons
        textButtonStyle = uiSkin.get("checked",TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        createRoom = new TextButton(Language.bundle.get("createRoom"), textButtonStyle);
        createRoom.setY(stage.getHeight() / 10 - 20);
        createRoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.setScreen(new CreateRoomMenuScreen(parent, stage, true, messageSender, messageListener));
            }
        });

        //Le bouton de refresh est un bouton qui permet de rafraîchir la liste des différentes waiting rooms
        refresh = new TextButton(Language.bundle.get("refresh"), textButtonStyle);
        refresh.setY(createRoom.getY() + createRoom.getHeight() + 20);
        refresh.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageSender.send(new TextMessage("getWaitingRooms"));
            }
        });

        stage.addActor(createRoom);
        stage.addActor(refresh);
        scrollTable = new Table();
        ScrollPane scroller = new ScrollPane(scrollTable, uiSkin);
        scroller.setScrollingDisabled(true, false);
        scroller.setScrollbarsVisible(true);
        scroller.setFadeScrollBars(false);

        table = new Table(uiSkin);
        table.setSize(width, height);
        table.setBackground("room-background");
        table.setY(100);
        table.add(scroller).fillX().expand().align(Align.topLeft).padTop(3).padBottom(3);
        stage.addActor(table);
        Gdx.input.setInputProcessor(this.stage);


        //Création du messageListener thread et du message sender
        try {
            this.messageSender = new OnlineMessageSender(parent.getUserSettings().getUsername(), parent.getUserSettings().getNumberOfPlayers(), ip);
            this.messageListener = new OnlineMessageListener(messageSender.getClientChannel(), messageSender.getSelector());
            this.messageListener.start();
            messageSender.send(new TextMessage("getWaitingRooms"));
        } catch (IOException e) { //Cette exception arrive lorsqu'il y a eu un problème lors de la connection du client au serveur
            Window.WindowStyle windowStyle = uiSkin.get(Window.WindowStyle.class);
            windowStyle.titleFont = textFont;
            Dialog dialog = new Dialog("", windowStyle);
            Table table = dialog.getContentTable();
            dialog.hide();
            table.align(Align.topLeft);
            labelStyle.font = textFont;
            textButtonStyle.font = textFont;
            TextButton b1 = new TextButton(Language.bundle.get("returnToMainMenu"), textButtonStyle);
            b1.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    parent.changeScreen(MainMenuScreen.class);
                    dialog.hide();
                }
            });
            table.add(new Label(Language.bundle.get("connectionError"), labelStyle)).pad(50).row();
            table.add(b1).expandY().align(Align.bottom).pad(50);
//            dialog.text(Language.bundle.get("connectionError"));
//            dialog.button(Language.bundle.get("returnToMainMenu"));
//            stage.addActor(window);
            dialog.show(stage);
            parent.changeScreen(MainMenuScreen.class);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (messageListener != null) {
            if (messageListener.needRefresh()) {
                refreshList();
            }
            //Si la taille de la liste de joueurs est > 0 cela signifie qu'il faut passé au waiting screen
            //TODO refactor with boolean
            if (messageListener.getPlayers().size() > 0) {
                parent.setScreen(new WaitingRoomScreen(parent, stage, messageSender, messageListener));
            }
        }
    }

    @Override
    public void show() {
        super.show();
        table.addAction(slideFromRight(table, 50, table.getY(), ANIMATION_DURATION / 4));
        refresh.addAction(slideFromRight(refresh, stage.getWidth() - refresh.getWidth() - 20, refresh.getY(), ANIMATION_DURATION / 4));
        createRoom.addAction(slideFromRight(createRoom, stage.getWidth() - createRoom.getWidth() - 20, createRoom.getY(), ANIMATION_DURATION / 4));
    }

    @Override
    public void hide() {
        super.hide();
        table.addAction(slideToRight(table));
        refresh.addAction(slideToRight(refresh));
        createRoom.addAction(slideToRight(createRoom));
    }

    @Override
    public void dispose() {
        if(messageSender != null)
            messageSender.close();
    }

    /**
     * Méthode qui permet de rafraîchir la liste des waiting rooms
     */
    private void refreshList() {
        scrollTable.reset();
        //Ajout des labels qui sont toujours présent même lorsqu'il n'y a aucune waiting room
        scrollTable.add(new Label(Language.bundle.get("roomName"), labelStyle)).expandX().pad(10).align(Align.topLeft);
        scrollTable.add(new Label(Language.bundle.get("numberOfPlayers"), labelStyle)).pad(10).padRight(50).align(Align.center);
        scrollTable.row();
        addLine(scrollTable); //Ajout de la line blanche
        if(messageListener.getRoomNames() != null) {
            ArrayList<String> roomNames = messageListener.getRoomNames();
            ArrayList<Integer> nPlayer = messageListener.getnPlayer();
            ArrayList<Integer> nPlayerIn = messageListener.getnPlayerIn();
            ArrayList<UUID> ids = messageListener.getIds();
            for (int i = 0; i < roomNames.size(); i++) {
                if (nPlayer.get(i) - nPlayerIn.get(i) >= parent.getUserSettings().getNumberOfPlayers()) { //On vérifie que la partie possède encore assez de place libre
                    //Ajout du nom de la room
                    scrollTable.add(new Label(roomNames.get(i), labelStyle)).pad(10).align(Align.left);
                    //Ajout du nombre de joueur présent dans la room ainsi que la capacité maximale de celle-ci (exemple : 4/6)
                    scrollTable.add(new Label(nPlayerIn.get(i) + "/" + nPlayer.get(i), labelStyle)).pad(10).align(Align.center);
                    //Ajout d'un bouton qui permet au client de rejoindre une room qui n'est pas encore pleine
                    if (!nPlayerIn.get(i).equals(nPlayer.get(i))) {
                        JoinButton join = new JoinButton(Language.bundle.get("join"), textButtonStyle, ids.get(i));
                        join.setChecked(true);
                        join.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                //Envoie d'un message au server qui indique que le client souhaite rejoindre une room
                                messageSender.send(new JoinRoomMessage(join.getId()));
                            }
                        });
                        //Ajout du bouton join
                        scrollTable.add(join).maxWidth(200).pad(10).padRight(50).align(Align.right);
                    }
                    scrollTable.row();
                }
            }
        }
    }
}
