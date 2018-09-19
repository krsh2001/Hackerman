package com.mygdx.game;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Surya on 2017-05-14.
 */
public class Level6Jail extends ApplicationAdapter implements Screen, ContactListener, InputProcessor {

    private CollisionDetector detector;
    private Body EMP;
    private int blackout;

    private BitmapFont font;

    private static final Level[] LEVELLIST = {
            new Level(new Vector2(96, 416), "tutorialmap.tmx"),
            new Level(new Vector2(832f, 766f), "2.tmx"),
            new Level(new Vector2(940f, 560f), "3.tmx"),
            new Level(new Vector2(32, 928), "4.tmx"),
            new Level(new Vector2(992, 224), "5.tmx"),
            new Level(new Vector2(30f, 25f), "jailmap.tmx"),
            new Level(new Vector2(544, 32), "7.tmx")
    };
    private static int levelNum;

    /**
     * Game Map
     */
    private TiledMap tiledMap;
    /**
     * Map renderer
     */
    private TiledMapRenderer tiledMapRenderer;
    /**
     * Player
     */
    private Player player;

    private Texture battery;

    private final Hackerman game;

    private World world;

    private Box2DDebugRenderer debugRenderer;

    private boolean emp;

    private Stage pauseMenu;

    private Minigame mini;

    private Stage gameText;

    private Image[] buttons;

    private boolean[] selected;

    private CutScene scene;

    private boolean isGameText;

    private RayHandler rayHandler;

    private Light light;

    private int loss;

    private boolean finalScene;

    private Texture[] finalScenes;

    private int finalCount;

    Level6Jail(Hackerman g) {
        this(g, LEVELLIST[levelNum].name, LEVELLIST[levelNum].pos);
    }

    private Level6Jail(final Hackerman game, String filename, Vector2 position) {
        boolean[] hacks;
        this.game = game;

        game.getCam().setToOrtho(false, 200, 150);
        game.getCam().update();
        tiledMap = new TmxMapLoader().load(filename);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(this);
        detector = new CollisionDetector(world, filename, game);

        loss = 120;
        hacks = new boolean[]{false, false, false};

        if (levelNum >= 0)
            hacks[0] = true;
        if (levelNum > 2)
            hacks[1] = true;
        if (levelNum > 3)
            hacks[2] = true;

        player = new Player(new Sprite(new Texture("Assets/Player/playermaintexture.png")), world, game, position.x, position.y, this, hacks);

        blackout = -1;

        debugRenderer = new Box2DDebugRenderer();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Assets/Fonts/digital-7.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        font.setColor(Color.RED);

        emp = false;
        battery = new Texture("Assets/Battery/3.png");

        pauseMenu = new Stage();

        pauseMenu.addActor(new Image(new Texture("Assets/Menu/PauseScreen.png")));

        buttons = new Image[]{
                new Image(new Texture("Assets/Menu/easy.png")), new Image(new Texture("Assets/Menu/medium.png")), new Image(new Texture("Assets/Menu/hard.png")),
                new Image(new Texture("Assets/Menu/vaporwave.png")), new Image(new Texture("Assets/Menu/techno.png")),
                new Image(new Texture("Assets/Menu/musicon.png")), new Image(new Texture("Assets/Menu/musicoff.png")),
                new Image(new Texture("Assets/Menu/fullscreen.png")), new Image(new Texture("Assets/Menu/windowed.png")),
                new Image(new Texture("Assets/Menu/exitpause.png"))
        };

        selected = new boolean[buttons.length];

        for (int i = 0; i < 3; i++) {
            buttons[i].setPosition(10, 360);
        }

        for (int i = 4; i < buttons.length; i += 2) {
            buttons[i].setPosition(10, 360 - (i - 2) * 45);
            buttons[i - 1].setPosition(10, 360 - (i - 2) * 45);
        }

        buttons[9].setPosition(10, 0);

        pauseMenu.addActor(buttons[2-Options.getDifficulty()]);

        if (Options.isVaporwave()) {
            pauseMenu.addActor(buttons[3]);
            selected[3] = true;
        } else {
            selected[4] = true;
            pauseMenu.addActor(buttons[4]);
        }

        if (Options.isSound()) {
            selected[5] = true;
            pauseMenu.addActor(buttons[5]);
        } else {
            selected[6] = true;
            pauseMenu.addActor(buttons[6]);
        }

        if (Options.isFullScreen()) {
            pauseMenu.addActor(buttons[7]);
            selected[7] = true;
        } else {
            selected[8] = true;
            pauseMenu.addActor(buttons[8]);
        }

        pauseMenu.addActor(buttons[9]);

        gameText = new Stage();

        String[] scenes;

        if (filename.equals("Assets/Maps/tutorialmap.tmx")) {
            scenes = new String[]{
                    "Assets/Cutscenes/CutScene_1/Scene.png", "Assets/Cutscenes/CutScene_1/Textbubble_1.png",
                    "Assets/Cutscenes/CutScene_1/Textbubble_2.png", "Assets/Cutscenes/CutScene_1/Textbubble_3.png",
                    "Assets/Cutscenes/CutScene_1/Textbubble_4.png", "Assets/Cutscenes/CutScene_1/Textbubble_5.png",
            };
            scene = new CutScene(game, scenes, this);
        } else if (filename.equals("Assets/Maps/2.tmx")) {
            scenes = new String[]{
                    "Assets/Cutscenes/CutScene_2/Scene.png", "Assets/Cutscenes/CutScene_2/Textbubble_1.png"
            };
            scene = new CutScene(game, scenes, this);
            isGameText = true;
            gameText.addActor(new Image(new Texture("Assets/Cutscenes/GameScenes/GameScene_1/Textbubble_1.png")));
        } else if (filename.equals("Assets/Maps/3.tmx")) {
            isGameText = true;
            gameText.addActor(new Image(new Texture("Assets/Cutscenes/GameScenes/GameScene_2/Textbubble_1.png")));
            scene = new CutScene(game, null, this);
        } else if (filename.equals("Assets/Maps/4.tmx")) {
            isGameText = true;
            gameText.addActor(new Image(new Texture("Assets/Cutscenes/GameScenes/GameScene_3/Textbubble_1.png")));
            scene = new CutScene(game, null, this);
        } else if (filename.equals("Assets/Maps/5.tmx")) {
            isGameText = true;
            gameText.addActor(new Image(new Texture("Assets/Cutscenes/GameScenes/GameScene_4/Textbubble_1.png")));
            scene = new CutScene(game, null, this);
        } else if (filename.equals("Assets/Maps/jailmap.tmx")) {
            scenes = new String[]{
                    "Assets/Cutscenes/CutScene_3/Scene.png", "Assets/Cutscenes/CutScene_3/Textbubble_1.png",
                    "Assets/Cutscenes/CutScene_3/Textbubble_2.png", "Assets/Cutscenes/CutScene_3/Textbubble_3.png",
                    "Assets/Cutscenes/CutScene_3/Textbubble_4.png", "Assets/Cutscenes/CutScene_3/Textbubble_5.png",
                    "Assets/Cutscenes/CutScene_3/Textbubble_6.png", "Assets/Cutscenes/CutScene_3/Textbubble_7.png"
            };
            scene = new CutScene(game, scenes, this);
            isGameText = true;
            gameText.addActor(new Image(new Texture("Assets/Cutscenes/GameScenes/GameScene_5/Textbubble_1.png")));
        } else if (filename.equals("Assets/Maps/7.tmx")) {
            scenes = new String[]{
                    "Assets/Cutscenes/CutScene_4/Scene.png", "Assets/Cutscenes/CutScene_4/Textbubble_1.png",
                    "Assets/Cutscenes/CutScene_4/Textbubble_2.png", "Assets/Cutscenes/CutScene_4/Textbubble_3.png",
                    "Assets/Cutscenes/CutScene_4/Textbubble_4.png", "Assets/Cutscenes/CutScene_4/Textbubble_5.png",
                    "Assets/Cutscenes/CutScene_4/Textbubble_6.png", "Assets/Cutscenes/CutScene_4/Textbubble_7.png"
            };
            scene = new CutScene(game, scenes, this);
            finalScenes = new Texture[]{new Texture("Assets/Cutscenes/FinalScene/FinalScene_1.png"), new Texture("Assets/Cutscenes/FinalScene/FinalScene_2.png")};
        } else {
            scene = new CutScene(game, null, this);
        }

        rayHandler = new RayHandler(world);

        light = new PointLight(rayHandler, 1000, Color.BLUE, 12, 0, 0);
        light.attachToBody(player.getBody());
        light.setActive(false);

        rayHandler.setAmbientLight(1f, 1f, 1f, 0.2f);

        finalCount = 100;

        Gdx.input.setInputProcessor(player);
        save();
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (!SoundStation.isPlaying())
            SoundStation.gamePlay();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getCam().setToOrtho(false, 200, 150);
        game.getCam().update();
        if (finalScene && !scene.isScene()) {
            endOut();
        }
        if (!player.isPaused() && !scene.isScene()) {
            Clock.decrement(1);
        }
        if (player.getMini()) {
            mini.output(delta);
        } else if (scene.isScene()) {
            scene.output();
        } else {
            gameOut();
            if (isGameText) {
                gameText.act();
                gameText.draw();
                if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                    gameText.clear();
                    isGameText = false;
                }
            }
            if (player.isLoss()) {
                pauseMenu.clear();
                pauseMenu.addActor(new Image(new Texture("Assets/Menu/LoseScreen.png")));
                pauseMenu.act();
                pauseMenu.draw();
                if (loss == 0)
                    game.setScreen(new Level6Jail(game));
                else
                    loss--;
            }
        }
        if (Clock.getTime() <= 0)
            game.setScreen(new MainMenu(game));
        if (emp) {
            EMP.destroyFixture(EMP.getFixtureList().first());
            emp = !emp;
        }
    }

    private void endOut() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getCam().setToOrtho(false, 800, 600);

        game.getBatch().begin();
        if (finalCount >= 0) {
            finalCount--;
            game.getBatch().draw(finalScenes[0], 0, 0);
        } else {
            if (finalCount >= - 50) {
                saveScore();
                Clock.setTime(0);
            }
            finalCount--;
            game.getBatch().draw(finalScenes[1],0,0);
        }
        game.getBatch().end();
    }

    private void gameOut() {
        Matrix4 debugMatrix;
        int hours, minutes;
        game.getCam().position.set(new Vector3(player.getX(), player.getY(), 0));
        game.getCam().update();
        world.step(1f / 60f, 6, 2);
        debugMatrix = game.getBatch().getProjectionMatrix().cpy().scale(B2DVars.PIXELS_TO_METERS,
                B2DVars.PIXELS_TO_METERS, 0);
        game.getBatch().setProjectionMatrix(game.getCam().combined);
        tiledMapRenderer.setView(game.getCam());
        tiledMapRenderer.render();
        game.getBatch().begin();
        for (Guard g : detector.getGuards())
            g.draw(game.getBatch());
        player.draw(game.getBatch());
        for (int i = 0; i < detector.getRouters().length; i++) {
            detector.getRouters()[i].getSprite().draw(game.getBatch());
            for (HackableObject obj : detector.getRouters()[i].getHacks())
                obj.getSprite().draw(game.getBatch());
        }
        detector.getElevator().draw(game.getBatch());
        if (!player.isPaused()) {
            Gdx.input.setInputProcessor(player);
            if (blackout == 0) {
                for (Router r : detector.getRouters()) {
                    for (HackableObject h : r.getHacks()) {
                        if (h instanceof Camera)
                            ((Camera) h).blackout();
                    }
                }
            }
            Vector3 unprojected = game.getCam().unproject(new Vector3(0, 0, 0));
            hours = (int) (24 * (float) Clock.getTime() / Clock.getMax());
            minutes = (int) (24f * 60 / (Clock.getMax()) * (Clock.getTime() % (Clock.getMax() / 24f)));
            font.setColor(Color.RED);
            font.draw(game.getBatch(), (hours <= 9 ? "0" : "") + hours + ":" + (minutes <= 9 ? "0" : "") + minutes, unprojected.x, unprojected.y);
            unprojected = game.getCam().unproject(new Vector3(0, 600, 0));
            game.getBatch().draw(battery, unprojected.x, unprojected.y);
            if (blackout >= 0) {
                blackout--;
                rayHandler.setCombinedMatrix(game.getCam());
                rayHandler.updateAndRender();
            }
            for (Guard g : detector.getGuards())
                g.step();
        } else {
            for (Guard g : detector.getGuards())
                g.stop();
            Gdx.input.setInputProcessor(this);
            pauseMenu.act();
            pauseMenu.draw();
        }
        game.getBatch().end();

        //debugRenderer.render(world, debugMatrix);
    }


    void miniSet(Minigame mini) {
        this.mini = mini;
    }

    void switchBattery(int bat) {
        battery = new Texture("Assets/Battery/" + bat + ".png");
    }

    private void save() {
        PrintWriter output;
        try {
            output = new PrintWriter(new FileWriter("Assets/Data/level.txt"));
            output.println(levelNum);
            output.println(Clock.getTime());
            output.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveScore() {
        PrintWriter output;
        try {
            output = new PrintWriter(new FileWriter("Assets/Data/highscore.txt", true));
            output.print(24 * (Clock.getTime() / Clock.getMax()));
            output.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        player.getTexture().dispose();
        world.dispose();
        if (!scene.isNull()) {
            Texture[] dispose = scene.getText();
            for (Texture t : dispose)
                t.dispose();
        }
    }

    @Override
    public void beginContact(Contact contact) {
        List<String> prop = new ArrayList<String>();
        prop.add((String) contact.getFixtureA().getBody().getUserData());
        prop.add((String) contact.getFixtureB().getBody().getUserData());

        if (prop.contains("player") && (prop.contains("baddie") || prop.contains("cam"))) {
            saveScore();
            SoundStation.stop();
            player.setLose(true);
        } else if (prop.contains("player") && prop.contains("victory")) {
            if (levelNum == LEVELLIST.length - 1) {
                finalScene = true;
                String[] scenes = new String[]{
                        "Assets/Cutscenes/CutScene_5/Scene.png", "Assets/Cutscenes/CutScene_5/Textbubble_1.png",
                        "Assets/Cutscenes/CutScene_5/Textbubble_2.png", "Assets/Cutscenes/CutScene_5/Textbubble_3.png",
                        "Assets/Cutscenes/CutScene_5/Textbubble_4.png", "Assets/Cutscenes/CutScene_5/Textbubble_5.png",
                        "Assets/Cutscenes/CutScene_5/Textbubble_6.png", "Assets/Cutscenes/CutScene_5/Textbubble_7.png"
                };
                scene = new CutScene(game, scenes, this);
            } else {
                levelNum++;
                game.setScreen(new Level6Jail(game));
            }
        } else if (prop.contains("EMP")) {
            for (Router r : detector.getRouters()) {
                if (r.getBod().getFixtureList().first().equals(contact.getFixtureA()) || r.getBod().getFixtureList().first().equals(contact.getFixtureB())) {
                    r.set();
                    r.getSprite().setTexture(new Texture("Assets/Hackables/routergood.png"));
                    for (HackableObject h : r.getHacks())
                        h.set();
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    void blackOut() {
        for (Router r : detector.getRouters()) {
            for (HackableObject h : r.getHacks()) {
                if (h instanceof Camera) {
                    blackout = 1800;
                    ((Camera) h).blackout();
                }
            }
        }
        for (Guard g : detector.getGuards()) {
            g.blackOut();
        }
    }

    void EMP() {
        BodyDef def = new BodyDef();
        FixtureDef fix = new FixtureDef();
        Shape shape;
        def.position.set(player.getBody().getPosition());
        def.fixedRotation = true;
        def.type = BodyDef.BodyType.DynamicBody;
        EMP = world.createBody(def);
        shape = new CircleShape();
        shape.setRadius(5);
        fix.shape = shape;
        fix.isSensor = true;
        EMP.createFixture(fix);
        EMP.setUserData("EMP");
        emp = true;
    }

    static void resetLevel() {
        levelNum = 0;
    }

    static void loadLevelNum() {
        BufferedReader input;
        try {
            input = new BufferedReader(new FileReader("Assets/Data/level.txt"));
            levelNum = Integer.parseInt(input.readLine());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Options.writeOptions();
            Gdx.input.setInputProcessor(player);
            player.setPaused(false);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screenX < 10 || screenX > 10 + buttons[0].getWidth())
            return false;

        for (int i = 0; i < 5; i++) {
            if (screenY >= 155 + (i * 90) && screenY <= 155 + (i * 90) + buttons[0].getHeight()) {
                if (i == 0) {
                    buttons[2-Options.getDifficulty()].remove();
                    Options.adjustDifficulty();
                    pauseMenu.addActor(buttons[2-Options.getDifficulty()]);
                } else if (i == 1) {
                    SoundStation.stop();
                    Options.switchTrack();
                } else if (i == 2) {
                    SoundStation.stop();
                    Options.toggleSound();
                } else if (i == 3)
                    Options.toggleFullScreen();
                else if (i == 4) {
                    saveScore();
                    Options.writeOptions();
                    game.setScreen(new MainMenu(game));
                    return false;
                }
                if (i > 0 && i < 4) {
                    if (selected[i * 2 + 1]) {
                        selected[i * 2 + 1] = false;
                        selected[i * 2 + 2] = true;
                        buttons[i * 2 + 1].remove();
                        pauseMenu.addActor(buttons[i * 2 + 2]);
                    } else if (selected[i * 2 + 2]) {
                        selected[i * 2 + 2] = false;
                        selected[i * 2 + 1] = true;
                        buttons[i * 2 + 2].remove();
                        pauseMenu.addActor(buttons[i * 2 + 1]);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    Player getPlayer() {
        return player;
    }

    CollisionDetector getDetector() {
        return detector;
    }

    static int getLevelNum() {
        return levelNum;
    }

    boolean isScene()
    {
        return scene.isScene();
    }
}

class Level {
    Vector2 pos;
    String name;

    Level(Vector2 pos, String name) {
        this.pos = pos;
        this.name = "Assets/Maps/" + name;
    }
}
