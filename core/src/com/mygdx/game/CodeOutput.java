package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Surya Krishnan
 * Date: 2017-06-02
 *
 * The CodeOutput method challenges the user to select the correct output for a given line of code.
 *
 * @version 1.8.1
 */
public class CodeOutput extends Minigame implements InputProcessor {

    /**
     * The game the minigame exists in.
     */
    private final Hackerman game;
    /**
     * The background screen.
     */
    private Texture screen;
    /**
     * The question header.
     */
    private Texture header;
    /**
     * The code to be analyzed.
     */
    private Texture question;
    /**
     * The potential answers.
     */
    private Texture[] answers;
    /**
     * The level the minigame is associated with.
     */
    private Level6Jail level;
    /**
     * If the buttons are selected or not.
     */
    private boolean[] buttons;
    /**
     * The question chosen by the program.
     */
    private int questionChoice;

    /**
     * The CodeOutput constructor initializes the screen and textures, selects which question to ask the user, and initializes the buttons array.
     * @param game The game the minigame exists in.
     * @param level The level the minigame exists in.
     */
    CodeOutput(final Hackerman game, Level6Jail level) {
        this.game = game;
        this.level = level;

        screen = new Texture("Assets/Minigame/minigamescreen.png");
        header = new Texture("Assets/Minigame/CodeOutput/CodeOutHeader.png");

        questionChoice = (int) (Math.random() * 3);


        if (questionChoice == 0) {
            question = new Texture("Assets/Minigame/CodeOutput/Quiz1/question.png");
            answers = new Texture[]{
                    new Texture("Assets/Minigame/CodeOutput/Quiz1/option1unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz1/option1selected.png"),
                    new Texture("Assets/Minigame/CodeOutput/Quiz1/option2unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz1/option2selected.png"),
                    new Texture("Assets/Minigame/CodeOutput/Quiz1/option3unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz1/option3selected.png"),
            };
        }
        //123
        else if (questionChoice == 1) {
            question = new Texture("Assets/Minigame/CodeOutput/Quiz2/question.png");
            answers = new Texture[]{
                    new Texture("Assets/Minigame/CodeOutput/Quiz2/option1unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz2/option1selected.png"),
                    new Texture("Assets/Minigame/CodeOutput/Quiz2/option2unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz2/option2selected.png"),
                    new Texture("Assets/Minigame/CodeOutput/Quiz2/option3unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz2/option3selected.png"),
            };
        }
        //No output
        else if (questionChoice == 2) {
            question = new Texture("Assets/Minigame/CodeOutput/Quiz3/question.png");
            answers = new Texture[]{
                    new Texture("Assets/Minigame/CodeOutput/Quiz3/option1unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz3/option1selected.png"),
                    new Texture("Assets/Minigame/CodeOutput/Quiz3/option2unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz3/option2selected.png"),
                    new Texture("Assets/Minigame/CodeOutput/Quiz3/option3unselected.png"), new Texture("Assets/Minigame/CodeOutput/Quiz3/option3selected.png"),
            };
        }

        buttons = new boolean[answers.length];
    }

    /**
     * The output method draws the screen, the question, and the buttons, drawing the selected textures if necessary.
     * @param delta The delta time.
     */
    @Override
    public void output(float delta) {
        if (!Gdx.input.getInputProcessor().equals(this))
            Gdx.input.setInputProcessor(this);
        if (!(game.getCam().position.equals(new Vector3(400, 300, 0))))
            game.getCam().position.set(new Vector3(0, 0, 0));
        if (game.getCam().viewportHeight != 600 && game.getCam().viewportWidth != 600)
            game.getCam().setToOrtho(false, 800, 600);
        game.getCam().update();
        game.getBatch().setProjectionMatrix(game.getCam().combined);
        game.getBatch().begin();
        game.getBatch().draw(screen, 0, 0);
        game.getBatch().draw(header, 100, 460);
        game.getBatch().draw(question, 100, 350);

        for (int i = 0 ; i < 3 ; i++)
        {
            if (buttons[i])
                game.getBatch().draw(answers[i * 2], 100, i * 100 + 40);
            else
                game.getBatch().draw(answers[i*2 + 1],100,i*100 + 40);
        }

        game.getBatch().end();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * The touchDown method is activated when the user clicks, and verifies which button they've clicked on, assigning variables accordingly.
     * @param screenX The X-coordinate of the pointer.
     * @param screenY The Y-coordinate of the pointer.
     * @param pointer The pointer.
     * @param button The button pressed.
     * @return False.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int desiredY;
        if (questionChoice == 1)
            desiredY = 460;
        else
            desiredY = 360;
        if (screenX > 100 && screenX < 100 + answers[0].getWidth() && screenY > desiredY && screenY < desiredY + answers[0].getHeight()) {
            super.change();
        }
        else
        {
            Clock.decrement(Clock.getMax()/24);
            shutDown();
        }
        Gdx.input.setInputProcessor(level.getPlayer());
        game.getCam().setToOrtho(false, 200, 150);
        level.getPlayer().switchMini();
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    /**
     * The mouseMoved method detects whether or not the user has scrolled over a button.
     * @param screenX The x-coordinate of the pointer.
     * @param screenY The y-coordinate of the pointer.
     * @return False.
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (screenX < 100 || screenX > 100 + answers[0].getWidth())
            return false;
        for (int i = 0 ; i < 3 ; i++)
        {
                buttons[2-i] = screenY > i * 100 +260 && screenY < i * 100 +260 + answers[0].getHeight();
        }

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
