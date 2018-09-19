package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Surya Krishnan
 * Date: June 1, 2017
 *
 * The CodeOrder Minigame challenges the user to place code in the correct order.
 *
 * @version 1.8.1
 */
public class CodeOrder extends Minigame implements InputProcessor {

    /**
     * The game the Minigame is in.
     */
    private final Hackerman game;
    /**
     * The level the minigame is associated with.
     */
    private Level6Jail level;
    /**
     * The Windows background screen.
     */
    private Texture screen;
    /**
     * The question being displayed.
     */
    private Texture question;
    /**
     * The answers.
     */
    private Texture[] answers;
    /**
     * The positions the answers are placed at.
     */
    private int[] indices;
    /**
     * The order the code has been selected in.
     */
    private int[] selection;
    /**
     * The font to write with.
     */
    private BitmapFont font;
    /**
     * The current question the user is on.
     */
    private int questionIndex;

    /**
     * The CodeOrder constructor initializes all of the textures, as well as the indices.
     * @param game The game the minigame is in.
     * @param level The level the minigame is in.
     */
    public CodeOrder(final Hackerman game, Level6Jail level) {
        this.game = game;
        this.level = level;
        screen = new Texture("Assets/Minigame/minigamescreen.png");
        question = new Texture("Assets/Minigame/OrderCode/OrderHeader.png");

        answers = new Texture[]{
                new Texture("Assets/Minigame/OrderCode/option1.png"), new Texture("Assets/Minigame/OrderCode/option2.png"),
                new Texture("Assets/Minigame/OrderCode/option3.png")
        };

        selection = new int[answers.length];
        indices = new int[]{1, 2, 3};

        //Font generation
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Assets/Fonts/digital-7.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        font.setColor(Color.WHITE);

        //Shuffling thrice.
        for (int i = 0; i < 3; i++) {
            for (int z = 0; z < answers.length; z++) {
                int r = (int) (Math.random() * answers.length);

                Texture temp = answers[i];
                int temporary = indices[i];

                answers[i] = answers[r];
                indices[i] = indices[r];

                answers[r] = temp;
                indices[r] = temporary;
            }
        }

        questionIndex = 0;
    }

    /**
     * The output method draws all of the questions, highlighted if necessary, as well as the order they were chosen in. It then checks if the user has succeeded or not.
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
        if (questionIndex == 3) {
            if (checkWin()) {
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
        }

        game.getBatch().begin();
        game.getBatch().draw(screen, 0, 0);
        game.getBatch().draw(question, 100, 450);
        for (int y = 75 ; y <= 325 ; y += 125)
            game.getBatch().draw(answers[2-(y-75)/125], 100, y);
        for (int i = 0; i < selection.length; i++) {
            if (selection[i] == 1) {
                font.draw(game.getBatch(), String.valueOf(i+1), 650, 385);
            } else if (selection[i] == 2) {
                font.draw(game.getBatch(), String.valueOf(i+1), 650, 260);
            } else if (selection[i] == 3) {
                font.draw(game.getBatch(), String.valueOf(i+1), 650, 135);
            }
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
     * The touchDown method checks what, if anything, the user has clicked.
     * @param screenX The X-coordinate of the pointer.
     * @param screenY The Y-coordinate of the pointer.
     * @param pointer The pointer.
     * @param button The button the user pressed.
     * @return false.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screenX < 100 || screenX > 100 + answers[0].getWidth())
            return false;
        for (int y = 200 ; y <= 400 ; y+= 100)
        {
            if (screenY >= y && screenY <= y + answers[0].getHeight())
            {
                selection[questionIndex] = (y-200)/100 + 1;
                questionIndex++;
            }
        }
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

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    /**
     * The checkWin method verifies whether or not the user has successfully completed the minigame.
     * @return If the user was successful.
     */
    private boolean checkWin() {
        for (int i = 0; i < selection.length; i++) {
            if (selection[i] != indices[i]){
                return false;
            }
        }
        return true;
    }

}
