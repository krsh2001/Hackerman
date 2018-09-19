package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Surya Krishnan
 * Date: may 21, 2017
 *
 * The MainMenu class contains buttons to start a new game, load an old game, and exit.
 *
 * @version 1.8.1
 */
public class MainMenu implements Screen, InputProcessor {

    /**
     * The game in which the menu is.
     */
    private final Hackerman game;
    /**
     * The textures for the various buttons.
     */
    private Texture textures[];
    /**
     * The Hackerman logo.
     */
    private Texture logo;
    /**
     * Data on whether the buttons are selected or not.
     */
    private boolean buttons[];

    private BitmapFont font;

    /**
     * The MainMenu constructor initializes the list of textures and buttons, sets the clock, and puts the camera in the correct position.
     * @param game The game the main menu is in.
     */
    public MainMenu(final Hackerman game) {
        this.game = game;
        textures = new Texture[] {new Texture("Assets/Menu/newgamebuttonselected.png"),
                new Texture("Assets/Menu/newgamebuttonunselected.png"),
                new Texture("Assets/Menu/loadgameselected.png"),
                new Texture("Assets/Menu/loadgameunselected.png"),
                new Texture("Assets/Menu/manualselected.png"),
                new Texture("Assets/Menu/manualunselected.png"),
                new Texture("Assets/Menu/exitgameselected.png"),
                new Texture("Assets/Menu/exitgameunselected.png")};
        logo = new Texture("Assets/Menu/hackermanlogo.png");
        buttons = new boolean[] {false,false,false, false};

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Assets/Fonts/digital-7.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);

        Gdx.input.setInputProcessor(this);
        Highscore.organizeScores();

        game.getCam().setToOrtho(false, 800, 600);
        SoundStation.stop();
    }

    @Override
    public void show() {}

    /**
     * The render method draws all of the buttons, as well as the logo.
     * @param delta The time between two cycles.
     */
    @Override
    public void render(float delta) {
        Vector3 unprojected;
        if (!(Gdx.input.getInputProcessor().equals(this)))
            Gdx.input.setInputProcessor(this);
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!SoundStation.isPlaying())
            SoundStation.menuPlay();

        game.getCam().update();
        game.getBatch().setProjectionMatrix(game.getCam().combined);
        game.getBatch().begin();

        unprojected = game.getCam().unproject(new Vector3(560,40,0));
        font.setColor(Color.WHITE);
        font.draw(game.getBatch(), "High Scores", unprojected.x,unprojected.y);

        for (int i = 0 ; i < 3 ; i ++)
        {
            if (Highscore.getScores()[i] > 24)
                break;
            unprojected = game.getCam().unproject(new Vector3(560,100 + i * 50, 0));
            font.draw(game.getBatch(), Double.toString(24 - Highscore.getScores()[i]) + " HOURS", unprojected.x,unprojected.y);
        }

        game.getBatch().draw(logo, 0, 450);
        for (int i = 400 ; i >= 130 ; i-= 90) //Goes through the buttons, and draws the unselected or selected texture.
        {
            if (buttons[(i-130)/90])
                game.getBatch().draw(textures[(3-(i-130)/90) * 2], 25,i);
            else
                game.getBatch().draw(textures[(3-(i-130)/90) * 2 + 1],25,i);
        }

        game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}

    @Override
    public boolean keyDown(int keycode) {
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

    /**
     *
     * @param screenX The pointer's X-coordinate.
     * @param screenY The pointer's Y-coordinate.
     * @param pointer The pointer.
     * @param button The button pressed.
     * @return false
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int height = textures[0].getHeight();
        //If it's not over a button
        if ((screenX < 25 || screenX > 25 + textures[0].getWidth()))
            return false;
        //New game button
        if (screenY >= 115 && screenY <= 115 + height) {
            SoundStation.stop();
            Level6Jail.resetLevel();
            Clock.setTime(Options.getDifficulty() * 36000 + 18000);
            game.setScreen(new Level6Jail(game));
        }
        //Load game button.
        else if (screenY >= 205 && screenY <= 205 + height) {
            SoundStation.stop();
            Level6Jail.loadLevelNum();
            Clock.loadTime(Options.getDifficulty() * 36000 + 18000);
            game.setScreen(new Level6Jail(game));
        }
        //Manual button.
        else if (screenY >= 295 && screenY <= 295 + height){
            game.setScreen(new Manual(game));
        }
        //Exit button
        else if (screenY >= 385 && screenY <= 385 + height)
            Gdx.app.exit();
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

    /**
     * The mouseMoved method detects when the pointer has been moved and determines which buttons are selected.
     * @param screenX The X-coordinate of the pointer.
     * @param screenY The Y-coordinate of the pointer.
     * @return false
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //Checks each button
        for (int i = 385 ; i >= 115 ; i-= 90)
        {
                buttons[3-((i-115)/90)] = screenX >= 25 && screenX <= 25+textures[0].getWidth() && screenY >= i && screenY <= i + textures[0].getHeight();
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
