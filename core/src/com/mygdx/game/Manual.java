package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Surya on 2017-06-04.
 */
public class Manual implements Screen, InputProcessor {

    private final Hackerman game;

    private Texture[]screens, buttons;

    int currentScreen;

    public Manual(final Hackerman game) {
        this.game = game;

        screens = new Texture[] {
                new Texture("Assets/Menu/Manual/ManualScreen.png"), new Texture("Assets/Menu/Manual/ControlsPage.png"),
                new Texture("Assets/Menu/Manual/HardwareQuiz.png"), new Texture("Assets/Menu/Manual/FindRoots.png"),
                new Texture("Assets/Menu/Manual/Triangle.png"), new Texture("Assets/Menu/Manual/CodeOrder.png"),
                new Texture("Assets/Menu/Manual/CodeOutput.png")
        };

        buttons = new Texture[] {
                new Texture("Assets/Menu/Manual/Controls.png"), new Texture("Assets/Menu/Manual/Learn.png"),
                new Texture("Assets/Menu/Manual/Back.png")
        };
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (!(Gdx.input.getInputProcessor().equals(this)))
            Gdx.input.setInputProcessor(this);
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!SoundStation.isPlaying())
            SoundStation.menuPlay();

        game.getCam().update();
        game.getBatch().setProjectionMatrix(game.getCam().combined);
        game.getBatch().begin();
        if (currentScreen == 0) {
            game.getBatch().draw(screens[currentScreen], 0,0);
            for (int i = 0 ; i < buttons.length; i++) {
                game.getBatch().draw(buttons[i], 10, 300 - i*100);
            }
        } else if (currentScreen > 0) {
            game.getBatch().draw(screens[currentScreen],0,0);
        }
        game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {

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

    @Override
    public void dispose() {
        for (Texture t : screens)
            t.dispose();

        for (Texture t: buttons)
            t.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (currentScreen == 1) {
            currentScreen = 0;
        }
        if (currentScreen > 1) {
            if (currentScreen == 6) {
                currentScreen = 0;
            } else {
                currentScreen++;
            }
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
        if ((screenX < 10 || screenX > 10 + buttons[0].getWidth()) && currentScreen == 0)
            return false;

        if (screenY > 200 && screenY < 200 + buttons[0].getHeight() && currentScreen == 0){
            currentScreen = 1;
        } else if (screenY > 300 && screenY < 300 + buttons[0].getHeight() && currentScreen == 0){
            currentScreen = 2;
        } else if (screenY > 400 && screenY < 400 + buttons[0].getHeight() && currentScreen == 0) {
            game.setScreen(new MainMenu(game));
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
}
