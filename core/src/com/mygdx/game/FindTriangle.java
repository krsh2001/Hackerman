package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * @author Surya Krishnan
 *
 */
public class FindTriangle extends Minigame {

    final Hackerman game;
    Level6Jail level;
    Texture screen, question, triangle;
    Skin skin;
    TextField textField;
    Stage stage;
    BitmapFont font;
    int length;
    private boolean done;

    public FindTriangle(final Hackerman game, Level6Jail level) {
        this.game = game;
        this.level = level;
        done = false;

        screen = new Texture("Assets/Minigame/minigamescreen.png");
        question = new Texture("Assets/Minigame/Triangle/TriHeader.png");
        triangle = new Texture("Assets/Minigame/Triangle/triangle.png");

        skin = new Skin(Gdx.files.internal("Assets/Fonts/uiskin.json"));

        stage = new Stage();

        textField = new TextField("Enter the value of \"c\": ", skin);
        stage.addActor(textField);

        stage.setKeyboardFocus(textField);
        textField.getOnscreenKeyboard().show(true);

        font = skin.getFont("default-font");
        font.setColor(Color.BLACK);

        textField.setPosition(200, 25);
        textField.setSize(300, 100);

        length = (int) (Math.random() * 11);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void output(float delta) {
        if (!(game.getCam().position.equals(new Vector3(400, 300, 0))))
            game.getCam().position.set(new Vector3(0, 0, 0));
        if (game.getCam().viewportHeight != 600 && game.getCam().viewportWidth != 600)
            game.getCam().setToOrtho(false, 800, 600);
        game.getCam().update();
        game.getBatch().setProjectionMatrix(game.getCam().combined);

        game.getBatch().begin();

        game.getBatch().draw(screen, 0, 0);
        game.getBatch().draw(triangle, 250, 150);
        font.setColor(Color.BLACK);
        font.draw(game.getBatch(), String.valueOf(length), 290, 300);
        font.draw(game.getBatch(), String.valueOf(length), 400, 200);
        font.draw(game.getBatch(), "C", 450, 320);
        game.getBatch().draw(question, 100, 460);

        game.getBatch().end();
        stage.act(delta);
        stage.draw();
        if (textField.getCursorPosition() < 24)
            textField.setCursorPosition(24);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                String text = textField.getText();
                if (!text.contains("Enter the value of \"c\": "))
                    textField.setText("Enter the value of \"c\": ");
                else if (keycode == Input.Keys.ENTER) {
                    try {
                        double x = Double.parseDouble(text.substring(text.indexOf(" ", text.indexOf(":")) + 1));
                        if (done)
                            return false;
                        else
                            done = true;
                        if (Math.abs(x - Math.sqrt(Math.pow(length, 2) * 2)) <= 0.5) {
                            FindTriangle.super.change();
                        }
                        else {
                            shutDown();
                            Clock.decrement(Clock.getMax() / 24);
                        }
                        Gdx.input.setInputProcessor(level.getPlayer());
                        game.getCam().setToOrtho(false, 200, 150);
                        level.getPlayer().switchMini();
                    } catch (NumberFormatException e) {
                        textField.setText("Enter the value of \"c\": ");
                    }
                }
                return false;
            }
        });

    }
}

