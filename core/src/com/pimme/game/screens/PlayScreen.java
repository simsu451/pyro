package com.pimme.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pimme.game.PyroGame;
import com.pimme.game.entities.enemies.Enemy;
import com.pimme.game.entities.Platform;
import com.pimme.game.entities.Player;
import com.pimme.game.tools.B2World;
import com.pimme.game.tools.Controller;
import com.pimme.game.tools.Hud;
import com.pimme.game.tools.Manager;

public class PlayScreen implements Screen
{
    private PyroGame game;
    private Manager manager;
    private OrthographicCamera gameCam;
    private FitViewport viewPort;

    // Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
//    private MapProperties mapProp;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2World worldCreator;

    private Player player;
    private Hud hud;
    private Controller controller;

    private Vector2 spawnPos;
    private boolean gameOver;

//    private int mapWidth, mapHeight, tileSize;

    public PlayScreen(PyroGame game) {
        this.game = game;
        manager = game.getManager();

        // cam to follow character
        gameCam = new OrthographicCamera();
        gameCam.zoom = 2.0f;

        // FitViewPort to maintain virtual aspect ratios despite screen size
        viewPort = new FitViewport(PyroGame.V_WIDTH / PyroGame.PPM, PyroGame.V_HEIGHT / PyroGame.PPM, gameCam);
        // Load our map and setup map renderer
        mapLoader = new TmxMapLoader();
        generateMap();

        renderer = new OrthogonalTiledMapRenderer(map, 1 / PyroGame.PPM);
        gameCam.position.x = PyroGame.V_WIDTH / PyroGame.PPM;
        gameCam.position.y = PyroGame.V_HEIGHT / PyroGame.PPM;


        world = new World(new Vector2(0, -8), true); // 1 parameter gravity, 2 sleep objects at rest
        b2dr = new Box2DDebugRenderer();
        worldCreator = new B2World(this);
        player = new Player(this);
        hud = new Hud(this, game.batch);

        controller = new Controller(this);
//        Gdx.input.setInputProcessor(new GestureDetector(controller));

//        mapProp = map.getProperties();
//        mapWidth = mapProp.get("width", Integer.class);
//        mapHeight = mapProp.get("height", Integer.class);
//        tileSize = mapProp.get("tilewidth", Integer.class);
    }


    private void update(final float dt) {
        world.step(1 / PyroGame.FPS, 10, 8);
        hud.update(dt);
        updatePlatforms(dt);
        updateEnemies(dt);
        controller.update(dt);
        player.update(dt);
        setCameraPos();
        gameCam.update();
        renderer.setView(gameCam);
    }

    private void updatePlatforms(final float dt) {
        for (Platform platform : worldCreator.getPlatforms()) {
            platform.update();
        }
    }

    private void updateEnemies(final float dt) {
        for (Enemy enemy : worldCreator.getEnemies()) {
            enemy.update(dt);
            if (gameCam.position.x + PyroGame.V_WIDTH / PyroGame.PPM > enemy.getX() && !enemy.body.isActive()) { // Set enemies active when in view
                enemy.body.setActive(true);
            }
        }
    }

    public void setSpawnPosition(Vector2 position) {
        spawnPos = position;
    }

    public Vector2 getSpawnPos() {
        return spawnPos;
    }


    private void setCameraPos() {
//        if (player.body.getPosition().x > (PyroGame.V_WIDTH / PyroGame.PPM)) // && player.body.getPosition().x < (mapWidth * tileSize - PyroGame.V_WIDTH) / PyroGame.PPM)
            gameCam.position.x = player.body.getPosition().x;
//        if (player.body.getPosition().y > (PyroGame.V_HEIGHT / PyroGame.PPM))// && player.body.getPosition().y < (mapHeight * tileSize - PyroGame.V_HEIGHT) / PyroGame.PPM)
            gameCam.position.y = player.body.getPosition().y;
    }


    @Override
    public void show() {

    }

    @Override public void render(final float delta) {
        update(delta);
        //CLEAR SCREEN
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //render game map
        renderer.render();  // renders textures to bodies
        //b2dr.render(world, gameCam.combined);


        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Platform platform : worldCreator.getPlatforms())
            platform.draw(game.batch);
        for (Enemy enemy : worldCreator.getEnemies())
            enemy.draw(game.batch);
        game.batch.end();

        hud.render();
        hud.stage.draw();

        if(gameOver) {
            game.setScreen(new GameOverScreen(game, hud.getScore()));
            dispose();
        }

    }

    private void generateMap() {
        switch (manager.getCurrentLevel()) {
            case LEVEL1:
                map = mapLoader.load("platform_map.tmx");
                break;
            case LEVEL2:
                map = mapLoader.load("platform_map2.tmx");
                break;
            case LEVEL3:
                map = mapLoader.load("platform_map3.tmx");
                break;
            case BOUNCE:
                map = mapLoader.load("bounce_map.tmx");
                break;
        }
    }


    public PyroGame getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }
    public Hud getHud() { return hud; }
    public World getWorld() { return world; }
    public TiledMap getMap() { return map; }
    public OrthographicCamera getGameCam() {
        return gameCam;
    }


    public void setGameOver() {
        gameOver = true;
    }

    @Override public void resize(final int width, final int height) {
        viewPort.update(width, height);
    }

    @Override public void pause() {

    }

    @Override public void resume() {

    }

    @Override public void hide() {
    }

    @Override public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
