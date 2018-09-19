package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;


/**
 * @author Surya Krishnan
 * Date: 2017-06-02
 *
 * The FindRoots class challenges the user to find the roots (x-intercepts) of a parabola.
 *
 * @version 1.8.1
 */
public class FindRoots extends Minigame {

    /**
     * The game the minigame exists within.
     */
    final Hackerman game;
    /**
     * The question being asked.
     */
    private Texture question;
    /**
     * The graph illustrating the question.
     */
    private Texture graph;
    /**
     * The background screen.
     */
    private Texture screen;
    /**
     * The stage that text is displayed within.
     */
    private Stage stage;
    /**
     * The text field within the stage.
     */
    private TextField textField;
    /**
     * The level the minigame is associated with.
     */
    private Level6Jail level;
    /**
     * How many roots the user has found so far.
     */
    private int count;
    /**
     * What of the two have been correct.
     */
    private boolean[] correct;
    /**
     * r and s, the roots of the parabola.
     */
    private int[] rs;

    private boolean done;

    /**
     * The FindRoots constructor initializes the relevant textures, and choose which parabola to use.
     * @param game The game that the minigame exists within.
     * @param level The level the game is associated with.
     */
    FindRoots(final Hackerman game, Level6Jail level) {
        Skin skin;
        this.game = game;
        this.level = level;

        done = false;
        question = new Texture("Assets/Minigame/Parabola/findRootsQuestion.png");

        count = 0;

        rs = new int[2];

        int x = (int) (Math.random() * 3);
        if (x == 0) {
            graph = new Texture("Assets/Minigame/Parabola/parabolas1.png");
            rs[0] = 1;
            rs[1] = 5;
        } else if (x == 1) {
            graph = new Texture("Assets/Minigame/Parabola/parabolas2.png");
            rs[0] = -4;
            rs[1] = 0;
        } else {
            graph = new Texture("Assets/Minigame/Parabola/parabolas3.png");
            rs[0] = -2;
            rs[1] = 2;
        }

        screen = new Texture("Assets/Minigame/minigamescreen.png");

        skin = new Skin(Gdx.files.internal("Assets/Fonts/uiskin.json"));

        textField = new TextField("What is r? ", skin);

        stage = new Stage();

        stage.addActor(textField);
        stage.setKeyboardFocus(textField);
        textField.getOnscreenKeyboard().show(true);

        textField.setPosition(200, 25);
        textField.setSize(300, 100);

        correct = new boolean[2];
    }

    /**
     * The output method draws the stage, textfield, question, and parabola.
     * @param delta The delta time.
     */
    @Override
    public void output(float delta) {
        if (!(Gdx.input.getInputProcessor().equals(stage)))
            Gdx.input.setInputProcessor(stage);
        if (!(game.getCam().position.equals(new Vector3(400, 300, 0))))
            game.getCam().position.set(new Vector3(0, 0, 0));
        if (game.getCam().viewportHeight != 600 && game.getCam().viewportWidth != 600)
            game.getCam().setToOrtho(false, 800, 600);
        game.getCam().update();
        game.getBatch().setProjectionMatrix(game.getCam().combined);

        if (count == 2) {
            if (correct[0] && correct[1]) {
                FindRoots.super.change();
            }
            else
            {
                Clock.decrement(Clock.getMax()/24);
            }
            Gdx.input.setInputProcessor(level.getPlayer());
            game.getCam().setToOrtho(false, 200, 150);
            level.getPlayer().switchMini();
        }

        game.getBatch().begin();
        game.getBatch().draw(screen, 0, 0);
        game.getBatch().draw(question, 100, 460);
        game.getBatch().draw(graph, 250, 150);
        game.getBatch().end();
        stage.act(delta);
        stage.draw();
        if (textField.getCursorPosition() < 11)
            textField.setCursorPosition(11);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                String text = textField.getText();
                if (!textField.getText().contains("What is r? ") && !textField.getText().contains("What is s? "))
                {
                    if (count == 0)
                        textField.setText("What is r? ");
                    else
                        textField.setText("What is s? ");
                }
                if (keycode == Input.Keys.ENTER) {
                    try {
                        int x = Integer.parseInt(text.substring(text.indexOf("?") + 2));
                        if (x != 400 && (x == rs[0] || x == rs[1])) {
                            if (x == rs[0] ) rs[0] = 400;
                            else rs[1] = 400;
                            correct[count] = true;
                            count++;
                            textField.setText("What is s? ");
                        } else if (!done){
                            done = true;
                            shutDown();
                            Clock.decrement(Clock.getMax() / 24);
                            Gdx.input.setInputProcessor(level.getPlayer());
                            game.getCam().setToOrtho(false,200,150);
                            level.getPlayer().switchMini();
                        }
                    } catch (NumberFormatException e) {
                    }
                }
                return false;
            }
        });
    }
}