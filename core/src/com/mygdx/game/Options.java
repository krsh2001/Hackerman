package com.mygdx.game;

import java.io.*;

/**
 * Created by David on 31/05/2017.
 */
public class Options {
    private static boolean vaporwave;
    private static int difficulty;
    private static boolean sound;
    private static boolean fullScreen;

    static void toggleSound()
    {
        sound = !sound;
    }

    static void adjustDifficulty()
    {
        if (difficulty < 2)
            difficulty++;
        else
            difficulty = 0;
    }

    static void switchTrack ()
    {
        vaporwave = !vaporwave;
    }

    static void toggleFullScreen()
    {
        fullScreen = !fullScreen;
    }

    public static void loadOptions()
    {
        BufferedReader input;
        try
        {
            input = new BufferedReader(new FileReader("Assets/Data/config.txt"));
            fullScreen = Boolean.parseBoolean(input.readLine());
            sound = Boolean.parseBoolean(input.readLine());
            vaporwave = Boolean.parseBoolean(input.readLine());
            difficulty = Integer.parseInt(input.readLine());
            input.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    static void writeOptions()
    {
        PrintWriter output;
        try
        {
            output = new PrintWriter(new FileWriter("Assets/Data/config.txt"));
            output.println(fullScreen);
            output.println(sound);
            output.println(vaporwave);
            output.println(difficulty);
            output.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static boolean isFullScreen()
    {
        return fullScreen;
    }

    static int getDifficulty() {
        return difficulty;
    }

    static boolean isVaporwave() {
        return vaporwave;
    }

    static boolean isSound() {
        return sound;
    }
}
