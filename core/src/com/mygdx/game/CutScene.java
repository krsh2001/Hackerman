package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Surya on 2017-05-27.
 */
public class CutScene {

    private Texture scene;
    private Texture[] text;
    private int currentText;
    private final Hackerman game;
    Level6Jail level;
    private boolean isScene = true;
    private boolean isNull;

    public CutScene(final Hackerman game, String[] filepaths, Level6Jail level) {

        this.game = game;
        this.level = level;

        if (filepaths != null) {
            text = new Texture[filepaths.length - 1];
            scene = new Texture(filepaths[0]);
            for (int i = 1; i < filepaths.length; i++) {
                text[i - 1] = new Texture(filepaths[i]);
            }
        } else {
            isScene = false;
            isNull = true;
        }

        currentText = 0;

    }

    public void output() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!(game.getCam().position.equals(new Vector3(400, 300, 0))))
            game.getCam().position.set(new Vector3(0, 0, 0));
        if (game.getCam().viewportHeight != 600 && game.getCam().viewportWidth != 600)
            game.getCam().setToOrtho(false, 800, 600);
        game.getCam().update();
        game.getBatch().setProjectionMatrix(game.getCam().combined);

        game.getBatch().begin();
        game.getBatch().draw(scene, 0, 0);
        game.getBatch().draw(text[currentText], 0, 0);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            currentText++;
        }

        if (currentText == text.length) {
            isScene = false;
        }
        game.getBatch().end();

    }

    public boolean isScene() {
        return isScene;
    }

    public Texture[] getText() {
        Texture[] dispose = new Texture[text.length + 1];
        for (int i = 0; i < text.length; i++) {
            dispose[i] = text[i];
        }
        dispose[text.length - 1] = scene;
        return dispose;
    }

    public boolean isNull(){
        return isNull;
    }
}
