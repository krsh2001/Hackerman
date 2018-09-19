package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author Surya Krishnan
 * Date: 2017-05-29
 *
 * The Guard class is an enemy that continually patrols, causing a game over if they see the player.
 *
 * @version 1.8.1
 */
public class Guard extends Sprite {

    /**
     * The guard's starting position.
     */
    private float startPos;
    /**
     * Whether or not it must change direction.
     */
    private boolean change;
    /**
     * The position at which it will change direction
     */
    private float endPos;
    /**
     * For how many cycles it must stay still
     */
    private int blocked;
    /**
     * The body associated with its field of view.
     */
    private Body FoV;
    /**
     * If it's going toward or away from the start.
     */
    private boolean start;
    /**
     * If it's moving horizontally or vertically.
     */
    private boolean xOrY;

    /**
     * The Guard constructor creates a new guard facing the correct orientation, with a correctly set-up body.
     * @param sprite The Sprite to be used.
     * @param con Its value on the axis along which it is not moving.
     * @param start At what value it starts.
     * @param end At what value it ends.
     * @param xOrY If it's moving horizontally or vertically.
     * @param world The world with which to create the body.
     */
    Guard(Sprite sprite, float con, float start, float end, boolean xOrY, World world) {
        float angle;
        BodyDef def;
        FixtureDef fix;

        set(sprite);
        change = false;
        blocked = 0;
        startPos = start;
        endPos = end;
        this.start = false;
        this.xOrY = xOrY;

        if (xOrY)
            setPosition(con - 14, startPos + 4);
        else
            setPosition(startPos-30, con-15);

        def = new BodyDef();
        if (xOrY)
            def.position.set(new Vector2(con / 20f, startPos / 20f));
        else
            def.position.set(new Vector2(startPos/20f, con/20f));
        def.type = BodyDef.BodyType.DynamicBody;
        FoV =world.createBody(def);

        fix = new FixtureDef();
        fix.isSensor = true;
        fix.density = 1f;
        PolygonShape shape = new PolygonShape();
        //The shape of the field of view
        Vector2[] vertices = new Vector2[]{
                new Vector2(0f, -0.5f),
                new Vector2(-0.5f,-0.5f),
                new Vector2(-0.5f,-1f),
                new Vector2(0f,-1f),
                new Vector2(2, 0.75f),
                new Vector2(2, -2.25f)
        };
        shape.set(vertices);
        fix.shape = shape;
        fix.restitution = 0f;
        FoV.createFixture(fix);
        FoV.setUserData("baddie");

        //Assigns direction accordingly
        if (xOrY) {
            if (startPos > endPos || endPos > startPos && this.start) {
                angle = (float) Math.PI * 1.5f;
                setRotation(180);
            } else {
                angle = (float) Math.PI / 2f;
                setRotation(0);
            }
            FoV.setTransform(FoV.getPosition().x + 0.05f, FoV.getPosition().y + 1.25f, angle);
        }
        else
        {
        if (startPos < endPos || endPos < startPos && this.start) {
            angle = 0;
            setRotation(270);
        }
        else {
            angle = (float) Math.PI;
            setRotation(90);
        }
         FoV.setTransform(FoV.getPosition().x + 0.5f, FoV.getPosition().y, angle);}
    }

    /**
     * The step method updates the movement of the guard. It will move in the correct direction, or not move
     * at all if blocked is above 0.
     */
    void step() {
        float movement;

        if (blocked > 0) {
            FoV.setLinearVelocity(0, 0);
            blocked--;

            //If it's finished, and must change, it will switch directions before continuing.
            if (blocked == 0 && change) {
                float angle, xChange, yChange;
                change = false;
                if (xOrY) {
                    if (start) {
                        setPosition(getX(), getY() - 36);
                        yChange = -0.8f - 1/20f;
                    }
                    else {
                        setPosition(getX(), getY() + 36);
                        yChange = 0.8f + 1/20f;
                    }
                    if (getRotation() == 180) {
                        angle = (float) Math.PI /2f;
                        setRotation(0);
                        xChange = -1.5f;
                    } else {
                        angle = (float) Math.PI *1.5f;
                        setRotation(180);
                        xChange = 1.5f;
                    }
                }
                else {
                    if (start) {
                        setPosition(getX() + 29, getY()-1);
                        xChange = 0.5f;
                    }
                    else {
                        xChange = -0.5f;
                        setPosition(getX() - 29, getY() + 1);
                    }
                    if (FoV.getAngle() == 0) {
                        setRotation(90);
                        angle = (float) Math.PI;
                        yChange = -1.5f;
                    }
                    else {
                        angle = 0;
                        setRotation(270);
                        yChange = 1.5f;
                    }
                }
                FoV.setTransform(FoV.getPosition().x + xChange, FoV.getPosition().y + yChange, angle);
            }
            return;
        }

        if (startPos < endPos)
            movement = 0.8f;
        else
            movement = -0.8f;
        if (start)
            movement *= -1f;

        if (xOrY) {
            FoV.setLinearVelocity(0f, movement * 3f);
            setPosition(getX(), getY() + movement);
        }
        else {
            setPosition(getX() + movement, getY());
            FoV.setLinearVelocity(movement * 3f, 0f);
        }
        if (!xOrY && ((endPos < startPos && (getX() >= startPos || getX() <= endPos))
                || (endPos > startPos && (getX() <= startPos || getX() >= startPos))) ||
                (xOrY && ((endPos < startPos && (getY() >= startPos || getY() <= endPos))
                        || (endPos > startPos && (getY() <= startPos || getY() >= endPos))))) {
            change = true;
            blocked = 100;
            start = !start;
        }
    }

    void blackOut()
    {
        FixtureDef fix = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        Vector2[] vertices = {
                new Vector2(0f, -0.5f),
                new Vector2(-0.5f,-0.5f),
                new Vector2(-0.5f,-1f),
                new Vector2(0f,-1f),
                new Vector2(1, 0.25f),
                new Vector2(1, -1.75f)
        };
        shape.set(vertices);
        fix.isSensor = true;
        fix.shape = shape;
        FoV.destroyFixture(FoV.getFixtureList().first());
        FoV.createFixture(fix);
        setTexture(new Texture("Assets/Guard/guardbout.png"));
    }

    /**
     * The buzz method is for when the player uses their "buzz" hack, rendering the guard immobile.
     */
    void buzz()
    {
        blocked += 150;
    }

    void stop()
    {
        FoV.setLinearVelocity(0,0);
    }
}
