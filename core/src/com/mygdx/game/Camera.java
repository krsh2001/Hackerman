package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;


/**
 * @author David
 * Date: 2017-05-29
 * The Camera class <i>is-a</i> HackableObject, and, if it sees the player, will cause them to lose the level.
 *
 * @version 1.8.1
 */
public class Camera extends HackableObject {

    private boolean blackout;
    private boolean hacked;

    /**
     * The Camera constructor takes a Body and MapObject, and creates the Camera in the correct position and orientation.
     * @param b The Camera's body.
     * @param m The MapObject associated with the Camera.
     */
    Camera (Body b, MapObject m)
    {
        super (b,m,"camera.png");
        Vector2 pos;
        //These numbers are stored to prevent excessive method calls
        float dir = (Float)m.getProperties().get("NESW"),
                x = (Float)m.getProperties().get("X"),
                y = (Float)m.getProperties().get("Y");

        b.getFixtureList().first().setSensor(true);
        b.setUserData("cam");

        //These if statements set the camera's position in accordance with its "NESW" property
        if (dir == 0){
            getSprite().setRotation(0);
            pos = new Vector2(x-26,y-64);}
        else if (dir == 1) {
            getSprite().setRotation(90);
            pos = new Vector2(x + 10,y-37);
        }
        else if (dir == 2) {
            getSprite().setRotation(180);
            pos = new Vector2(x - 27,y-1);
        }
        else {
            getSprite().setRotation(270);
            pos = new Vector2(x-54 , y-37);
        }
        blackout = false;
        hacked = false;
        getSprite().setPosition(pos.x,pos.y);
    }

    /**
     * The hack() method destroys the body associated with the camera so it can no longer see the player.
     */
    public void hack()
    {
        Array<Fixture> fixtures = getBod().getFixtureList();
        if (!canBeHacked())
            return;
        super.hack();
        hacked = true;
        getSprite().setTexture(new Texture("Assets/Hackables/camoff.png"));
        if (fixtures.size > 0) getBod().destroyFixture(fixtures.first());
    }

    /**
     * The switchSprite() method highlights the object, informing the user that they can hack it.
     */
    public void switchSprite()
    {
        if (!blackout && !hacked)
            getSprite().setTexture(new Texture("Assets/Hackables/Scamera.png"));
    }

    public void blackout()
    {
        if (hacked)
            return;
        blackout = !blackout;
        if (blackout)
        {
            getSprite().setTexture(new Texture("Assets/Hackables/camoff.png"));
            getBod().setUserData("blacked");
        }
        else
        {
            getBod().setUserData("cam");
            if (hacked)
                switchSprite();
            else
                getSprite().setTexture(new Texture("Assets/Hackables/camera.png"));
        }
    }
}
