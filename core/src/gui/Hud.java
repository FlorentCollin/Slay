package gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import gui.graphics.screens.BasicScreen;
import gui.utils.Constants;

/**
 * Classe représentant l'HUD in game qui contient notamment le shop ainsi que le nom du joueur actif.
 */
public class Hud extends Stage {
    private final DistrictInfo districtInfo;
    protected BasicScreen parent;
    protected Skin uiSkin;
    protected TextureAtlas itemSkin;
    private Shop shop;

    public Hud(BasicScreen parent, TextureAtlas itemSkin) {
        super();
        this.parent = parent;
        this.uiSkin = parent.getUiSkin();
        this.itemSkin = itemSkin;
        shop = new Shop();
        districtInfo = new DistrictInfo();
        districtInfo.addActorsToStage(this);
        shop.addActorsToStage(this);
    }

    public Shop getShop() {
        return shop;
    }

    public DistrictInfo getDistrictInfo() {
        return districtInfo;
    }

    /**
     * Classe interne qui correspond au Shop (où il est possible d'acheter des soldats)
     */
    public class Shop {
        private final Table table;
        public final Image soldierLvl1;
        public final Image soldierLvl2;
        public final Image soldierLvl3;
        public final Image soldierLvl4;
        private float width, height;

        public Shop() { //TODO Refactor for more soldier or others items
            //Définition de la taille du shop
            Image background = new Image(uiSkin.getDrawable("shop-background"));
            width = background.getWidth() * Constants.getRatioX(Gdx.graphics.getWidth());
            height = background.getHeight() * Constants.getRatioY(Gdx.graphics.getHeight());
            //Création des objets accessibles dans le shop
            soldierLvl1 = new Image(new Sprite(itemSkin.createSprite("Soldier_lvl1")));
            soldierLvl2 = new Image(new Sprite(itemSkin.createSprite("Soldier_lvl2")));
            soldierLvl3 = new Image(new Sprite(itemSkin.createSprite("Soldier_lvl3")));
            soldierLvl4 = new Image(new Sprite(itemSkin.createSprite("Soldier_lvl4")));
            Table scrollTable = new Table();
            //Ajout des objets achetables dans le shop
            scrollTable.add(soldierLvl1).maxHeight(height/4);
            scrollTable.row();
            scrollTable.add(soldierLvl2).maxHeight(height/4);
            scrollTable.row();
            scrollTable.add(soldierLvl3).maxHeight(height/4);
            scrollTable.row();
            scrollTable.add(soldierLvl4).maxHeight(height/4);
            //TODO COMMENT
            ScrollPane scroller;
            scroller = new ScrollPane(scrollTable);
            scroller.setScrollingDisabled(true, false);
            table = new Table(uiSkin);
            table.setBackground("shop-background");
            table.add(scroller).fillX().expand().align(Align.topLeft);
            table.setSize(width, height);

        }

        public void addActorsToStage(Hud hud) {
            table.setPosition(hud.getWidth() - width, hud.getHeight() / 2 - height / 2);
            hud.addActor(table);
        }
    }

    public class DistrictInfo {

        private final float width, height;
        private final Table table;
        public Label goldLabel;

        public DistrictInfo() {
            //Définition de la taille du shop
            Image background = new Image(uiSkin.getDrawable("info-background"));
            width = background.getWidth() * Constants.getRatioX(Gdx.graphics.getWidth());
            height = background.getHeight() * Constants.getRatioY(Gdx.graphics.getHeight());
            Image gold = new Image(itemSkin.createSprite("Coin"));
            Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
            labelStyle.font = parent.getTextFont();

            goldLabel = new Label("", labelStyle);
            gold.setScaling(Scaling.fit);
            //TODO COMMENT
            table = new Table(uiSkin);
            table.setSize(width, height);
            table.setBackground("info-background");
            table.add(goldLabel).expandX().pad(10).align(Align.right);
            table.add(gold).maxWidth(50).align(Align.right).pad(10);
        }

        public void addActorsToStage(Hud hud) {
            table.setPosition(hud.getWidth() - width, 0);
            hud.addActor(table);
        }
    }
}
