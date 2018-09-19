package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.maps.MapObject;

import java.util.Arrays;

/**
 * @author David Nash
 * Date: May 31, 2017
 *
 * The Router class <i>is-a</i> HackableObject that controls several othe HackableObjects.
 *
 * @version 1.8.1
 */
public class Router extends HackableObject {

    /**
     * The objects that it controls.
     */
    private HackableObject[] hacks;
    /**
     * The game with which it is associated
     */
    private final Hackerman game;

    /**
     * The Router constructor creates uses the properties of a Map Object to put itself in the correct position.
     * @param object The MapObject it is associated with.
     * @param bod The Body it is associated with.
     * @param game The game in which it is.
     */
    public Router(MapObject object, Body bod, Hackerman game) {
        super(bod, object, "routerbad.png");
        hacks = new HackableObject[0];
        this.game = game;
        getSprite().setPosition((Float) object.getProperties().get("X"), (Float) object.getProperties().get("Y"));
    }

    /**
     * Returns the list of hackable objects it controls.
     * @return The list of hackable objects it controls.
     */
    HackableObject[] getHacks() {
        return hacks;
    }

    /**
     * Adds one hackable object to the hacks array.
     * @param obj The object to be added.
     */
    void add(HackableObject obj) {
        HackableObject[] temp;
        temp = Arrays.copyOf(hacks, hacks.length + 1);
        temp[hacks.length] = obj;
        hacks = temp;
    }

    /**
     * Creates a minigame that, if successfully completed, will hack every hackable object connected to the router.
     * @param level The level that it exists in.
     * @param choice Which minigame has been selected.
     */
    void hack(Level6Jail level, int choice) {
         Minigame[] minigames = new Minigame[] {
                 new HardwareQuiz(game,level), new FindRoots(game, level), new FindTriangle(game, level), new CodeOutput(game, level), new CodeOrder(game, level)
         };
        minigames[choice].setRouter(this);
        level.miniSet(minigames[choice]);
    }

    /**
     * The switchSprite method highlights the router in green if it can be hacked.
     */
    void switchSprite()
    {}
}
