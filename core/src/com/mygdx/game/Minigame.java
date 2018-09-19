package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Surya on 2017-05-16.
 */
public abstract class Minigame {

    private Router router;
    private static Sound sound = Gdx.audio.newSound(Gdx.files.internal("Assets/Sounds/startup.mp3"));

    Minigame()
    {
        startUp();
    }

    void change(){
        shutDown();
        HackableObject[]objs = router.getHacks();
        for (HackableObject h : objs)
            h.set();
        router.set();
        router.getSprite().setTexture(new Texture("Assets/Hackables/routergood.png"));
    }

    private static void startUp()
    {
        sound.dispose();
        sound = Gdx.audio.newSound(Gdx.files.internal("Assets/Sounds/startup.mp3"));
        sound.play();
    }

    static void shutDown()
    {
        sound.dispose();
        sound = Gdx.audio.newSound(Gdx.files.internal("Assets/Sounds/shutdown.mp3"));
        sound.play();
    }

    void setRouter(Router r){
        router = r;
    }

    public abstract void output(float delta);
}
