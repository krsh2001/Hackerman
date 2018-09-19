package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Surya Krishnan
 * Date: 2017-05-29
 *
 * @version 1.8.1
 */
public class HardwareQuiz extends Minigame implements InputProcessor {

    private Texture[] textures;
    private final Hackerman game;
    private int[] questions, indices;
    private Texture screen, question;
    private Texture[] answers;
    private int questionIndex;
    private boolean[] correct;
    private boolean buttons[];
    private Level6Jail level;

    HardwareQuiz(final Hackerman game, Level6Jail level) {
        this.game = game;
        this.level = level;

        buttons = new boolean[] {false,false,false};
        correct = new boolean[] {false,false,false};

        screen = new Texture("Assets/Minigame/minigamescreen.png");
        question = new Texture("Assets/Minigame/HardwareQuiz/QuizHeader.png");
        textures = new Texture[7];
        textures[0] = new Texture("Assets/Minigame/HardwareQuiz/cpufantexture.png");
        textures[1] = new Texture("Assets/Minigame/HardwareQuiz/CPUTexture.png");
        textures[2] = new Texture("Assets/Minigame/HardwareQuiz/GraphicsCardTexture.png");
        textures[3] = new Texture("Assets/Minigame/HardwareQuiz/motherboardtexture.png");
        textures[4] = new Texture("Assets/Minigame/HardwareQuiz/powersupplytexture.png");
        textures[5] = new Texture("Assets/Minigame/HardwareQuiz/RAMTexture.png");
        textures[6] = new Texture("Assets/Minigame/HardwareQuiz/romtexture.png");
        answers = new Texture[14];
        answers[0] = new Texture("Assets/Minigame/HardwareQuiz/cpufanselected.png");
        answers[1] = new Texture("Assets/Minigame/HardwareQuiz/cpufanunselected.png");
        answers[2] = new Texture("Assets/Minigame/HardwareQuiz/cpuunselected.png");
        answers[3] = new Texture("Assets/Minigame/HardwareQuiz/cpuselected.png");
        answers[4] = new Texture("Assets/Minigame/HardwareQuiz/gpuunselected.png");
        answers[5] = new Texture("Assets/Minigame/HardwareQuiz/gpuselected.png");
        answers[6] = new Texture("Assets/Minigame/HardwareQuiz/motherboardunselected.png");
        answers[7] = new Texture("Assets/Minigame/HardwareQuiz/motherboardselected.png");
        answers[8] = new Texture("Assets/Minigame/HardwareQuiz/powersupplyunselected.png");
        answers[9] = new Texture("Assets/Minigame/HardwareQuiz/powersupplyselected.png");
        answers[10] = new Texture("Assets/Minigame/HardwareQuiz/RAMunselected.png");
        answers[11] = new Texture("Assets/Minigame/HardwareQuiz/RAMselected.png");
        answers[12] = new Texture("Assets/Minigame/HardwareQuiz/ROMunselected.png");
        answers[13] = new Texture("Assets/Minigame/HardwareQuiz/ROMselected.png");

        questionIndex = 0;

        questions = new int[3];
        for (int i = 0; i < questions.length; i++) {
            if (i == 0) {
                questions[i] = (int) (Math.random() * 7);
            } else {
                while (true) {
                    int question = (int) (Math.random() * 7);
                    if (i == 1) {
                        if (questions[i - 1] != question) {
                            questions[i] = question;
                            break;
                        }
                    } else {
                        if (questions[i - 1] != question && questions[i - 2] != question) {
                            questions[i] = question;
                            break;
                        }
                    }
                }
            }
        }
        indices = new int[] {0,1,2};
        for (int i = 0 ; i < 3 ; i++)
        {
            for (int z = 0 ; z < indices.length ; z++)
            {
                int r = (int) (Math.random() * indices.length);
                int temp = indices[i];
                indices[i] = indices[r];
                indices[r] = temp;
            }
        }
    }

    @Override
    public void output(float delta) {
        if (!Gdx.input.getInputProcessor().equals(this))
            Gdx.input.setInputProcessor(this);
        if (!(game.getCam().position.equals(new Vector3(400,300,0))))
            game.getCam().position.set(new Vector3(0, 0, 0));
        if (game.getCam().viewportHeight != 600 && game.getCam().viewportWidth != 600)
            game.getCam().setToOrtho(false,800,600);
        game.getCam().update();
        game.getBatch().setProjectionMatrix(game.getCam().combined);
        game.getBatch().begin();
        game.getBatch().draw(screen, 0,0);
        game.getBatch().draw(question, 100, 450);

        if (questionIndex == 3) {
            if (correct[0] && correct[1] && correct[2]){
                super.change();
            }
            Gdx.input.setInputProcessor(level.getPlayer());
            game.getCam().setToOrtho(false,200,150);
            level.getPlayer().switchMini();
        }

        if (questionIndex < 3) {
            game.getBatch().draw(textures[questions[questionIndex]], 300, 270);
        }

        for (int i = 0 ; i < 3; i ++)
        {
            if (buttons[i])
                game.getBatch().draw(answers[questions[i] * 2],275*indices[i] + 50,50);
            else
                game.getBatch().draw(answers[questions[i]*2 + 1],275* indices[i]+ 50,50);
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (int i = 50; i <= 600 ; i += 275)
        {
            if (screenX > i && screenX < i + 150 && screenY > 450 && screenY < 550) {
                if ((i - 50) / 275 == indices[questionIndex]) {
                    correct[questionIndex] = true;
                    questionIndex++;
                }
                else {
                    lose();
                }
            }
        }
        return false;
    }

    private void lose()
    {
        shutDown();
        Clock.decrement(Clock.getMax() / 24);
        Gdx.input.setInputProcessor(level.getPlayer());
        game.getCam().setToOrtho(false,200,150);
        level.getPlayer().switchMini();
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
        for (int i = 0 ; i < 3; i++) {
            buttons[i]= screenX > 275*indices[i]+50 && screenX < 275*indices[i] + 200 && screenY > 450 && screenY < 550;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}