package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Created by David on 31/05/2017.
 */
public class SoundStation {
    private static Music clip = Gdx.audio.newMusic(Gdx.files.internal("Assets/Sounds/Vapor/1.mp3"));

    static void menuPlay()
    {
        if (Options.isSound()){
        clip = Gdx.audio.newMusic(Gdx.files.internal("Assets/Sounds/menu.mp3"));
        clip.play();}
    }

    static void gamePlay()
    {
        if (Options.isSound()) {
            if (Options.isVaporwave())
                clip = Gdx.audio.newMusic(Gdx.files.internal("Assets/Sounds/Vapor/" + (int) (Math.random() * 12 + 1) + ".mp3"));
            else
                clip = Gdx.audio.newMusic(Gdx.files.internal("Assets/Sounds/Tech/" + (int)(Math.random()* 4 + 1) + ".mp3"));
            clip.play();
        }
    }

    static void stop()
    {
        clip.stop();
    }

    static boolean isPlaying()
    {
        return clip.isPlaying();
    }
}
