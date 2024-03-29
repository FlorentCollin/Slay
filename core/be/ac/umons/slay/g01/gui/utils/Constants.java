package ac.umons.slay.g01.gui.utils;

import com.badlogic.gdx.graphics.Color;

/**
 * Classe contenant toutes les constantes utiles
 */
public class Constants {

    public static final Color[] colors = new Color[] {new Color(9/255f, 161/255f, 63/255f, 1) /* Green */,
                                                new Color(255/255f, 120/255f, 0, 1) /* Orange */,
                                                new Color(242/255f, 128/255f, 238/255f, 1) /* Pink */,
                                                new Color(234/255f, 1, 0, 1), /* Yellow */
                                                new Color(138/255f, 7/255f, 7/255f, 1), /* Red */
                                                new Color(7/255f, 77/255f, 94/255f, 1), /* Cyan */
                                                new Color(25/255f, 25/255f, 25/255f, 1) /* Neutral Gray */,
                                                new Color(0,113/255f,183/255f,1) /* Blue */ };
    public static final String SERVER_ADDRESS = "florentcollin.xyz";
    public static final int PORT = 8888;
    /** Résolution maximum **/
    public static final int MAX_RES_X = 1920;
    public static final int MAX_RES_Y = 1080;
    public static final int PAD = 20;

    public static final int N_TILES = 5; //Nombre de tiles présent dans le tileset hex.
    // Ce nombre est utilisé pour sélectionner et désélectionner des cellules

    public static final String USER_SETTINGS_FILE = "settings/userSettings.json";
    public static final String USER_SHORTCUTS_FILE = "settings/userShortcuts.json";

    public static final int MAP_NUMBER = 10;

    public static float getRatioX(float width) {
        return width / MAX_RES_X;
    }

    public static float getRatioY(float height) {
        return height / MAX_RES_Y;
    }
}
