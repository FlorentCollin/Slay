package ac.umons.slay.g01.gui.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.XmlReader;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.item.Capital;
import ac.umons.slay.g01.logic.item.Item;
import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.level.SoldierLevel;
import ac.umons.slay.g01.logic.player.Player;
import ac.umons.slay.g01.logic.shop.Shop;

/** Classe utilisé pour charger et convertir une map TMX en Board **/
//TODO NEED XML VALIDATOR FOR VALIDATE THE XML FILE

public class Map {

    private XmlReader.Element xmlElement;
    private String worldName;
    protected TiledMap map;
    protected HexagonalTiledMapRenderer tiledMapRenderer;
    protected TiledMapTileLayer cells;
    protected TiledMapTileLayer selectedCells;
    protected TiledMapTileLayer disasterCells;
    protected TiledMapTileSet tileSet;
    protected TiledMapTileSet tileSetSelected;
    protected TiledMapTileSet tileSetDisaster;
    protected Board board;
    protected int numberOfPlayers;
    private HashMap<String, Object>[][] onlineCells;


    public Map(String worldName) {
        XmlReader xml = new XmlReader();
        //Création du parseur xml et on récupère le xml_element contenant les informations du monde
        this.worldName = worldName;
        xmlElement = xml.parse(getFileHandle("worlds/" + worldName + ".xml"));
    }
    /**
     * Méthode qui permet de charger un monde du jeu
     * @param naturalDisasters true s'il l'extension natural disasters est activé, false sinon
     * @return Le board initialisé si loadBoard == true, null sinon
     */
    public Board loadBoard(boolean naturalDisasters, ArrayList<String> playersName) {
            loadTmx();
            generateBoard(xmlElement, naturalDisasters, playersName);
            int width = Integer.parseInt(xmlElement.getAttribute("width"));
            int height = Integer.parseInt(xmlElement.getAttribute("height"));
            if(cells != null) {
                generateDistricts(width, height, false);
            } else {
                generateDistricts(width, height, true);
            }
            generateItems(xmlElement);
        return board;
    }

    public void loadTmx() {
        if(Gdx.files == null) {
            generateTmxMap(worldName);
        } else {
            generateTmxMap(xmlElement);
        }
    }

    /**
     * Méthode qui génère la map Tmx ainsi que le TmxRenderer à partir du fichier tmx
     * @param xmlElement l'xmlElement contenant les informations du monde
     */
    private void generateTmxMap(XmlReader.Element xmlElement) {
        String worldTmx = xmlElement.getAttribute("map");
        map = new TmxMapLoader().load("worlds/" + worldTmx + ".tmx");
        tiledMapRenderer = new HexagonalTiledMapRenderer(map);

        cells = (TiledMapTileLayer) map.getLayers().get("background"); //cellules
        selectedCells = (TiledMapTileLayer) map.getLayers().get("selectedTiles");
        disasterCells = (TiledMapTileLayer) map.getLayers().get("disasters");
        tileSet = map.getTileSets().getTileSet("hex"); //le tileset des hexagones
        tileSetSelected = map.getTileSets().getTileSet("hexSelected");
        tileSetDisaster = map.getTileSets().getTileSet("hexDisasters");
        for (int i = 0; i < selectedCells.getWidth(); i++) {
            for (int j = 0; j < selectedCells.getHeight(); j++) {
                selectedCells.setCell(i, j, new TiledMapTileLayer.Cell());
                disasterCells.setCell(i,j, new TiledMapTileLayer.Cell());
            }
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void generateTmxMap(String mapName) {
        ServerTmxMapLoader tmxLoader = new ServerTmxMapLoader();
        onlineCells = tmxLoader.load(mapName);
    }

    /**
     * Méthode qui génère le board
     * @param xmlElement l'xmlElement contenant les informations du monde
     */
    private void generateBoard(XmlReader.Element xmlElement, boolean naturalDisasters, ArrayList<String> playersName) {
        numberOfPlayers = Integer.parseInt(xmlElement.getChildByName("players").getAttribute("number"));
        CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
        Player newPlayer;
        for (int i = 0; i < numberOfPlayers; i++) {
            if(playersName != null)
                newPlayer = new Player(playersName.get(i));
            else
                newPlayer = new Player();
            //Ajout de l'id du player
            newPlayer.setId(i+1); //Car i=0 correspond aux cellules neutres
            players.add(newPlayer);
        }
        int width = Integer.parseInt(xmlElement.getAttribute("width"));
        int height = Integer.parseInt(xmlElement.getAttribute("height"));
        board = new Board(width, height, players, naturalDisasters, new Shop());
    }

    /**
     * Méthode qui va initialiser les districts à partir de la map Tmx.
     */
    private void generateDistricts(int width, int height, boolean online) {
        //TODO Méthode qui génère les district dans board
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                boolean available;
                int nPlayer;
                if (online) {
                    HashMap<String, Object> properties = onlineCells[i][j];
                    available = (boolean) properties.get("available");
                    nPlayer = (int) properties.get("player");
                } else {
                    //Note : le Math.abs(cells.getHeight()-1 - j) est utilisé ici car la map Tmx à son origine centré
                    //En bas à gauche tandis que le board lui à son origine centré en haut à gauche.
                    TiledMapTileLayer.Cell cell = cells.getCell(i, Math.abs(height-1 - j));
                    MapProperties properties = cell.getTile().getProperties();
                    available = (boolean) properties.get("available");
                    nPlayer = (int) properties.get("player");

                }
                if(!available) { //On change la cellule pour une cellule d'eau si la cellule n'est pas accesible
                    board.changeToWaterCell(i, j);
                }
                if (nPlayer != 0) { //Si la cellule appartient à un joueur (car 0 est la valeur pour une cellule neutre
                    District district = new District(board.getPlayers().get(nPlayer - 1));
                    district.addCell(board.getCell(i,j));
                    board.addDistrict(district);
                    board.getCell(i, j).setDistrict(district);
                    board.checkMerge(board.getCell(i, j)); //On regarde si on ne peut pas merge deux districts
                    //Car les districts ne sont pas spécifiés dans le fichier xml du monde
                }
            }
        }
    }

    /**
     * Méthode qui génère les items dans le board à partir des informations du monde
     * @param xmlElement l'xmlElement contenant les informations du monde
     */
    private void generateItems(XmlReader.Element xmlElement) {
        XmlReader.Element items = xmlElement.getChildByName("items"); //Récupération de la section Items
        for (int i = 0; i < items.getChildCount(); i++) { //Itération sur l'ensemble des items
            XmlReader.Element item = items.getChild(i);
            //Ici on récupère la classe associé à l'item en utilisant la réflexion
            Class<?> itemClass = getClassFromString(item.getAttribute("type"));
            //Récupération de la cellule où il faut placer l'item
            Cell cell = board.getCell(Integer.parseInt(item.getAttribute("x")),
                    Integer.parseInt(item.getAttribute("y")));
            try {
                //Cas spécifique le constructeur de base ne suffit pas. Un soldat doit avoir un level
                if(itemClass.equals(Soldier.class)) {
                    Constructor<?> constructor = itemClass.getConstructor(SoldierLevel.class);
                    int soldierLevel = Integer.parseInt(item.getAttribute("level"));
                    Item newItem = null;
                    newItem = (Item) constructor.newInstance(SoldierLevel.values()[soldierLevel-1]);
                    cell.setItem(newItem);
                } else {
                    Constructor<?> constructor = itemClass.getConstructors()[0]; //Constructeur de base
                    cell.setItem((Item) constructor.newInstance());
                }
                if(itemClass.equals(Capital.class)) {
                	cell.getDistrict().removeCapital();
                    cell.getDistrict().setGold(Integer.parseInt(item.getAttribute("golds")));
                    cell.getDistrict().addCapital(cell);
                }
                else if(itemClass.equals(Tree.class)) {
                	board.addTree(Integer.parseInt(item.getAttribute("x")),
                            Integer.parseInt(item.getAttribute("y")));
                }
                //TODO
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode qui récupère la class associé à un String
     * @param str le nom de la classe
     * @return la classe associé au String
     */
    private static Class<?> getClassFromString(String str) {
        /*
         * Ici on va utiliser le principe de réflexion Ce principe va nous permettre de
         * trouver une class à partir d'un String Et donc d'éviter de devoir placer un
         * switch(str) qui aurait du énumérer tous les éléments possible
         *  Source :
         * https://stackoverflow.com/questions/22439436/loading-a-class-from-a-different
         * -package
         */
        try {
            return Class.forName("ac.umons.slay.g01.logic.item." + str); // Comme les Class Item sont dans un autre package on doit
            // indiquer où les trouver
        } catch (ClassNotFoundException e) {
            Gdx.app.log("ERROR","A name of an Item is wrong in the xml file : " + str);
        }
        return null;
    }

    protected Class<?> getStrategy(String strategy){
    	try {
    		return Class.forName("ac.umons.slay.g01.logic.player.ai.strategy."+strategy);
    	}
    	catch(ClassNotFoundException e) {
    	    Gdx.app.log("ERROR", String.format("the strategy %s didn't exist", strategy));
    		return null;
    	}
    }

    private FileHandle getFileHandle(String path) {
        if(Gdx.files == null) {
            String cwd = new File("").getAbsolutePath();
            return new FileHandle(new File(cwd + File.separator + path.replace("/", File.separator)));
        } else {
            return Gdx.files.internal(path);
        }
    }

    public TiledMap getMap() {
        return map;
    }

    public HexagonalTiledMapRenderer getTiledMapRenderer() {
        return tiledMapRenderer;
    }


    public TiledMapTileSet getTileSet() {
        return tileSet;
    }

    public TiledMapTileLayer getCells() {
        return cells;
    }

    public TiledMapTileLayer getSelectedCells() {
        return selectedCells;
    }

    public TiledMapTileSet getTileSetSelected() {
        return tileSetSelected;
    }

    public TiledMapTileSet getTileSetDisaster() {
        return tileSetDisaster;
    }

    public TiledMapTileLayer getDisasterCells() {
        return disasterCells;
    }
}
