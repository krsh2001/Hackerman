package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by David on 15/05/2017.
 */
public abstract class HackableObject {
    private boolean hacked;
    private boolean hasBeenHacked;
    private Body bod;
    private Sprite sprite;
    private MapObject obj;

    HackableObject(Body s, MapObject m, String texture)
    {
        sprite = new Sprite(new Texture("Assets/Hackables/"+texture));
        hacked = false;
        hasBeenHacked = false;
        bod = s;
        bod.setUserData("Hackable");
        obj = m;
    }

    public void hack(){hasBeenHacked = true;}

    void set()
    {hacked = true;
    switchSprite();}

    MapProperties getProp()
    {
        return obj.getProperties();
    }

    boolean canBeHacked()
    {
        return hacked;
    }

    abstract void switchSprite();

    Sprite getSprite()
    {
        return sprite;
    }

    Body getBod()
    {
        return bod;
    }
}
