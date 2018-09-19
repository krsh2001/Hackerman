package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * @author Surya Krishnan
 * Date: 2017-05-29
 *
 * The Hackerman class controls the game, containing the camera and batch.
 *
 * @version 1.8.1
 */
public class Hackerman extends Game {

    /**
     * The SpriteBatch used for drawing Sprites and Textures.
     */
    private SpriteBatch batch;
    /**
     * The orthographic camera used for viewing the game.
     */
     private OrthographicCamera cam;

    @Override
    /**
     * The create method initializes the game, setting up the screen, SpriteBatch, and Camera.
     */
    public void create() {
        batch = new SpriteBatch();
        cam = new OrthographicCamera();
        this.setScreen(new MainMenu(this));
    }

    @Override
    /**
     * The dispose method disposes of the SpriteBatch.
     */
    public void dispose() {
        batch.dispose();
    }

    /**
     * The getBatch method is an accessor for the SpriteBatch.
     * @return The game's SpriteBatch.
     */
    SpriteBatch getBatch()
    {
        return batch;
    }

    /**
     * The getCam method is an accessor for the game's camera.
     * @return The game's camera.
     */
    OrthographicCamera getCam()
    {
        return cam;
    }
}

