package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import java.io.*;
import java.util.Arrays;

/**
 * Created by David on 04/06/2017.
 */
public class Highscore{
    private static double scores[];

    static void organizeScores()
    {
        double temp;
        PrintWriter output;
        BufferedReader input;
        scores = new double[3];
        try
        {
            input = new BufferedReader(new FileReader("Assets/Data/highscore.txt"));
            for (int i = 0 ; i < 3 ; i++)
            {
                scores[i] = Double.parseDouble(input.readLine());
            }
            input.close();
            Arrays.sort(scores);
            temp = scores[0];
            scores[0] = scores[2];
            scores[2] = temp;
            output = new PrintWriter(new FileWriter("Assets/Data/highscore.txt"));
            for (int i = 0 ; i < 3 ; i++)
            {
                output.println(scores[i]);
            }
            output.close();
        }
        catch (IOException e){}
    }

    static double[] getScores()
    {
        return scores;
    }
}
