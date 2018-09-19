package com.mygdx.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author David Nash
 * Date: May 31, 2017
 *
 * The Clock class stores the time that the user has left, as well as how much time they were initially allotted.
 *
 * @version 1.8.1
 */
public class Clock {
    /**
     * The amount of time the user has left.
     */
    private static int time;
    /**
     * The amount of time the user was given at the start.
     */
    private static int maxTime;

    /**
     * The setTime method resets the clock, and adjusts the maximum accordingly.
     * @param time How much time the user is being given.
     */
    static void setTime(int time)
    {
        maxTime = time;
        Clock.time = time;
    }

    /**
     * Decreases the amount of time left by an amount.
     * @param amt The amount of time to decrease by.
     */
    static void decrement(int amt)
    {
        time-= amt;
    }


    /**
     * The loadTime method loads what time the user has left from a file.
     *
     * @param max The maximum amount of time, so the clock has perspective on how much it "really" is.
     */
    static void loadTime(int max)
    {
        BufferedReader input;
        maxTime = max;
        try
        {
            input = new BufferedReader(new FileReader("Assets/Data/level.txt"));
            input.readLine();
            time = Integer.parseInt(input.readLine());
            input.close();
        }
        catch (IOException e)
        {
        }
        if (time > maxTime)
            time = maxTime;
    }

    /**
     * Returns how much time the user has left.
     * @return The amount of time the user has left.
     */
    static int getTime()
    {
        return time;
    }

    /**
     * Returns how much time the user was given initially.
     * @return The amount of time the user was given initially.
     */
    static int getMax()
    {
        return maxTime;
    }
}
