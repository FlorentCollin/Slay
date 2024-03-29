package ac.umons.slay.g01.gui.app;

import static ac.umons.slay.g01.gui.utils.Constants.USER_SETTINGS_FILE;
import static ac.umons.slay.g01.gui.utils.Constants.USER_SHORTCUTS_FILE;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import ac.umons.slay.g01.gui.graphics.screens.BasicScreen;
import ac.umons.slay.g01.gui.graphics.screens.CreateRoomMenuScreen;
import ac.umons.slay.g01.gui.graphics.screens.InGameScreen;
import ac.umons.slay.g01.gui.graphics.screens.MainMenuScreen;
import ac.umons.slay.g01.gui.graphics.screens.OnlineMenuScreen;
import ac.umons.slay.g01.gui.graphics.screens.SettingsMenuScreen;
import ac.umons.slay.g01.gui.graphics.screens.ShortcutsMenuScreen;
import ac.umons.slay.g01.gui.settings.InitSettings;
import ac.umons.slay.g01.gui.settings.UserSettings;
import ac.umons.slay.g01.gui.settings.UserShortcuts;
import ac.umons.slay.g01.gui.utils.Language;

/**
 * Classe principale du jeu, c'est elle qui gère l'ensemble des menus, et la partie du joueur
 */
public class Slay extends Game {
	private MainMenuScreen mainMenuScreen;
	private SettingsMenuScreen settingsMenuScreen;
	private ShortcutsMenuScreen shortcutsMenuScreen;
    private OnlineMenuScreen onlineMenuScreen;
    private CreateRoomMenuScreen createRoomMenuScreen;

    private UserSettings userSettings;
    private UserShortcuts userShortcuts;

    @Override
	public void create () {
		userSettings = InitSettings.init(USER_SETTINGS_FILE, UserSettings.class);
		userSettings.init();
		userShortcuts = InitSettings.init(USER_SHORTCUTS_FILE, UserShortcuts.class);
		Language.setLanguage(userSettings.getLanguage());
		mainMenuScreen = new MainMenuScreen(this);
		this.setScreen(mainMenuScreen);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(16/255,16/255f,16/255f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void dispose () {
    	InitSettings.dispose(USER_SETTINGS_FILE, userSettings);
    	InitSettings.dispose(USER_SHORTCUTS_FILE, userShortcuts);
    	Gdx.app.exit();
	}

	/**
	 * Méthode qui permet de changer entre les différents menus
	 * @param screen Le menu ou l'interface de jeu qui va s'afficher pour l'utilisateur
	 */
	public void changeScreen(Class<?> screen) {
	    BasicScreen nextScreen = null;
		if(screen == MainMenuScreen.class) {
			if(mainMenuScreen == null) {
				mainMenuScreen = new MainMenuScreen(this);
			}
			if(onlineMenuScreen != null) {
				onlineMenuScreen.dispose();
			}
			if(createRoomMenuScreen != null) {
				createRoomMenuScreen.dispose();
			}
			Gdx.graphics.setResizable(true);
			nextScreen = mainMenuScreen;
		} else if(screen == SettingsMenuScreen.class) {
			if(settingsMenuScreen == null) {
				settingsMenuScreen = new SettingsMenuScreen(this, mainMenuScreen.getStage());
			}
			nextScreen = settingsMenuScreen;
		} else if(screen == ShortcutsMenuScreen.class) {
		    if(shortcutsMenuScreen == null) {
		        shortcutsMenuScreen = new ShortcutsMenuScreen(this, mainMenuScreen.getStage());
            }
            nextScreen = shortcutsMenuScreen;
        } else if(screen == CreateRoomMenuScreen.class) {
			createRoomMenuScreen = new CreateRoomMenuScreen(this, mainMenuScreen.getStage(), false);
            nextScreen = createRoomMenuScreen;
        }
		this.setScreen(nextScreen);
	}

	public void changeScreen(InGameScreen gameScreen) {
		clearScreen();
		this.setScreen(gameScreen);
	}

	public void changeScreen(OnlineMenuScreen onlineMenuScreen) {
		this.onlineMenuScreen = onlineMenuScreen;
		this.setScreen(onlineMenuScreen);
	}

	public void clearScreen() {
		this.mainMenuScreen = null;
		this.settingsMenuScreen = null;
		this.shortcutsMenuScreen = null;
		this.onlineMenuScreen = null;
		this.createRoomMenuScreen = null;
	}

	public UserSettings getUserSettings() {
		return userSettings;
	}

	public UserShortcuts getUserShortcuts() {
		return userShortcuts;
	}
}
