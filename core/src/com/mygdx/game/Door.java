package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * @author David Nash
 * Date: 2017-05-29
 *
 * The Door class <i>is-a</i> HackableObject that can be open or closed, allowing or blocking the player from moving past a certain point.
 *
 * @version 1.8.1
 */
public class Door extends HackableObject {

    /**
     * The Door constructor constructs a closed door with a sprite at the correct position.
     *
     * @param b The body associated with the door.
     * @param m The MapObject associated with the door.
     */
    Door (Body b, MapObject m)
    {
        super (b,m,"doorclosed.png");
        if ((Boolean)m.getProperties().get("up")){
            getSprite().setPosition((Float)m.getProperties().get("X") - 30, (Float)m.getProperties().get("Y") + 30);
            getSprite().setRotation(90);
        }
        else
            getSprite().setPosition((Float) m.getProperties().get("X"), (Float) m.getProperties().get("Y"));
    }

    /**
     * The hack method opens or closes the door, depending on what setting it was at before it was hacked.
     */
    public void hack()
    {
        Fixture fix = getBod().getFixtureList().first();
        //If it can't be hacked, it doesn't do anything.
        if (!canBeHacked())
            return;
        super.hack();
        //If it's a sensor, it's no longer a sensor. If it isn't, it becomes one.
        fix.setSensor(!fix.isSensor());
        //Changes the sprite between closed and open
        if (fix.isSensor())
            getSprite().setTexture(new Texture("Assets/Hackables/SdoorO.png"));
        else
            getSprite().setTexture(new Texture("Assets/Hackables/Sdoor.png"));
    }

    /**
     * Highlights the door, indicating that it can be hacked..
     */
    public void switchSprite()
    {
        getSprite().setTexture(new Texture("Assets/Hackables/Sdoor.png"));
    }
}
