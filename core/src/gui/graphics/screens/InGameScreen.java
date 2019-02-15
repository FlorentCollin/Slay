package gui.graphics.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import gui.app.Slay;
import gui.utils.Map;
import logic.Coords.OffsetCoords;
import logic.Coords.TransformCoords;
import logic.board.Board;
import logic.board.cell.Cell;

import java.util.ArrayList;

public class InGameScreen extends BasicScreen {

    private Map map;
    private Vector3 mouseLoc = new Vector3();
    private float worldWith;
    private float worldHeight;
    private TiledMapTileLayer cells;
    private Board board;



    public InGameScreen(Slay parent, String mapName) {
        super(parent);
        map = new Map();
        board = map.load("testMap.tmx");
        cells = map.getCells();

        camera.setToOrtho(true);
        worldWith = (cells.getWidth()/2) * cells.getTileWidth() + (cells.getWidth() / 2) * (cells.getTileWidth() / 2) + cells.getTileWidth()/4;
        worldHeight = cells.getHeight() * cells.getTileHeight() + cells.getTileHeight() / 2;
        viewport = new FillViewport(worldWith, worldHeight, camera);
        camera.update();

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        camera.update();
        map.getTiledMapRenderer().setView(camera);
        map.getTiledMapRenderer().render();
        mouseLoc.x = Gdx.input.getX();
        mouseLoc.y = Gdx.input.getY();
        camera.unproject(mouseLoc);
        if(Gdx.input.isButtonPressed(102)) {
            OffsetCoords coords = getCoordsFromMousePosition(mouseLoc);
            if(cells.getCell(coords.col,coords.row) != null) {
                cells.getCell(coords.col, coords.row).setTile(map.getTileSet().getTile(1));
            }
        }
        if(Gdx.input.isKeyPressed(19)) {
            camera.zoom -= 0.05;
        }
        if(Gdx.input.isKeyPressed(20)) {
            camera.zoom += 0.05;
        }

        if(Gdx.input.isKeyPressed(51)) {
            camera.translate(0,-10);
        }
        if(Gdx.input.isKeyPressed(47)) {
            camera.translate(0,10);
        }
        if(Gdx.input.isKeyPressed(29)) {
            camera.translate(-10,0);
        }
        if(Gdx.input.isKeyPressed(32)) {
            camera.translate(+10,0);
        }

    }

    protected void generateStage() {
        camera = new OrthographicCamera();
        viewport = new FillViewport(1920, 1080, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Retourne les coordonnées de la cellule qui se trouve à la position de la souris
     * @param mouseLoc position de la souris
     * @return les coordonnées de la cellule qui est à la position de la souris
     */
    private OffsetCoords getCoordsFromMousePosition(Vector3 mouseLoc) {
        //- cells.getTileWidth() / 2 et - cells.getTileHeight() / 2 sont là pour créer le décalage de l'origine.
        // Ce qui permet de retrouver les bonnes coordonnés
        // le (int)cells.getTileWidth() /2 correspond à la taille de l'hexagone (ie la longueur de la droite qui va du
        // centre vers une des pointes de l'hexagone
        return TransformCoords.pixelToOffset((int)(mouseLoc.x - cells.getTileWidth() / 2),
                (int)(mouseLoc.y - cells.getTileHeight() / 2), (int)cells.getTileWidth() /2);
    }


    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

}
