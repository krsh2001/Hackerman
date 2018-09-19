package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

/**
 * The Player class controls user input, as well as drawing the player and controlling it.
 */
public class Player extends Sprite implements InputProcessor {

    /**
     * The level the player's in.
     */
    private Level6Jail level;
    /**
     * If 'A' is pressed down
     */
    private boolean isRight = false;
    /**
     * If 'D' is pressed down
     */
    private boolean isLeft = false;
    /**
     * If 'W' is pressed down
     */
    private boolean isUp = false;
    /**
     * If 'S' is pressed down
     */
    private boolean isDown = false;

    /**
     * The player's battery life.
     */
    private int battery;

    /**
     * If the player is sitting on their laptop or standing.
     */
    private boolean isSitting = false;

    /**
     * The sprite associated with the player.
     */
    private Sprite player;

    /**
     * The sprite associated with the player when they're on their laptop.
     */
    private Sprite playerLaptop;

    /**
     * If the player is or isn't in a minigame.
     */
    private boolean minigame;

    /**
     * The body attached to the player.
     */
    private Body body;

    /**
     * The game the player exists in.
     */
    private final Hackerman game;

    /**
     * The special hacks the player is allowed to do.
     */
    private boolean[] hacks;

    /**
     * What angle the player is at.
     */
    private float rotation;

    private boolean paused;

    private boolean loss;

    Player(Sprite sprite, World world, final Hackerman game, float startX, float startY, Level6Jail level, boolean[] hacks) {
        super(sprite);
        this.game = game;
        this.hacks = hacks;
        minigame = false;
        player = sprite;
        playerLaptop = new Sprite(new Texture("Assets/Player/playerlaptoptexture.png"));
        this.level = level;
        battery = 3;
        loss = false;
        paused = false;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set((startX + getWidth() / 2) / B2DVars.PIXELS_TO_METERS, (startY + getHeight() / 2) / B2DVars.PIXELS_TO_METERS);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / 5 / B2DVars.PIXELS_TO_METERS, getHeight() / 5 / B2DVars.PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        body.setUserData("player");
    }

    void draw(SpriteBatch spriteBatch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(spriteBatch);
    }

    private void update(float delta) {

        setPosition((body.getPosition().x * B2DVars.PIXELS_TO_METERS) - getWidth() / 2,
                ((body.getPosition().y * B2DVars.PIXELS_TO_METERS) - getHeight() / 2));

        game.getBatch().draw(this, getX(), getY(), getOriginX(),
                getOriginY(),
                getWidth(), getHeight(), getScaleX(),
                getScaleY(), getRotation());

        if (isSitting) {
            float x = getX(), y = getY();
            set(playerLaptop);
            setPosition(x, y);
            setRotation(rotation);
        }
        else {
            float x = getX(), y = getY();
            set(player);
            setPosition(x, y);
            setRotation(rotation);
            translator();
        }
    }

    private void translator() {
        if (isLeft) {
            rotation = 90;
            setRotation(90);
            body.setLinearVelocity(-4f, body.getLinearVelocity().y);
        }
        if (isRight) {
            rotation = 270;
            setRotation(270);
            body.setLinearVelocity(4f, body.getLinearVelocity().y);
        }
        if (isUp) {
            rotation = 0;
            setRotation(0);
            body.setLinearVelocity(body.getLinearVelocity().x, 4f);
        }
        if (isDown) {
            rotation = 180;
            setRotation(180);
            body.setLinearVelocity(body.getLinearVelocity().x, -4f);
        }
    }

    /**
     * If key is pressed down
     *
     * @param keycode
     * @return
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            isLeft = true;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            isRight = true;
        }
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            isUp = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            isDown = true;
        }
        if (keycode == Input.Keys.ESCAPE && !level.isScene()) {
            if (paused) {
                paused = false;
            } else {
                paused = true;
            }
        }
        if (keycode == Input.Keys.Z && battery > 2 && hacks[2])
        {
            level.blackOut();
            battery-= 3;
            level.switchBattery(battery);
        }
        if (keycode == Input.Keys.X && battery > 1 && hacks[1]) {
            battery -= 2;
            level.EMP();
            level.switchBattery(battery);
        }
        if (keycode == Input.Keys.Q && !isSitting && !isDown && !isUp && !isRight && !isLeft){
            isSitting = true;
            isDown = false;
            isRight = false;
            isUp = false;
            isLeft = false;
        }
        else if (isSitting && (keycode == Input.Keys.Q || isDown || isUp || isRight || isLeft))
        {
            isSitting = false;
        }
        return false;
    }

    /**
     * If key is let go
     *
     * @param keycode
     * @return
     */
    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) {
            isLeft = false;
            body.setLinearVelocity(Math.max(body.getLinearVelocity().x, 0f), body.getLinearVelocity().y);
        }
        if (keycode == Input.Keys.D) {
            isRight = false;
            body.setLinearVelocity(Math.min(body.getLinearVelocity().x, 0f), body.getLinearVelocity().y);
        }
        if (keycode == Input.Keys.W) {
            isUp = false;
            body.setLinearVelocity(body.getLinearVelocity().x, Math.min(body.getLinearVelocity().y, 0f));
        }
        if (keycode == Input.Keys.S) {
            isDown = false;
            body.setLinearVelocity(body.getLinearVelocity().x, Math.max(body.getLinearVelocity().y, 0f));
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Router[] m = level.getDetector().getRouters();
        Guard[] guards = level.getDetector().getGuards();
        Vector3 unprojected = game.getCam().unproject(new Vector3(screenX, screenY, 0));
        screenX = (int) unprojected.x;
        screenY = (int) unprojected.y;
        for (Guard g : guards)
        {
            if (hacks[0] && battery > 0 && screenX >= g.getX()&& screenX <= g.getX() + g.getWidth() && screenY >= g.getY()&& screenY <=g.getY() + g.getHeight())
            {battery--;
                g.buzz();
                level.switchBattery(battery);}
        }

        if (!isSitting)
            return false;

        for (Router r : m) {
            MapProperties p;
            for (HackableObject obj : r.getHacks()) {
                p = obj.getProp();
                if (screenX >= (Float) p.get("X") && screenX <= (Float) p.get("X") + (Float) p.get("Width") && screenY >= (Float) p.get("Y") && screenY <= (Float) p.get("Y") + (Float) p.get("Height")) {
                    obj.hack();
                    break;
                }
            }
            p = r.getProp();
            if (!r.canBeHacked() && screenX >= (Float) p.get("X") && screenX <= (Float) p.get("X") + (Float) p.get("Width") && screenY >= (Float) p.get("Y") && screenY <= (Float) p.get("Y") + (Float) p.get("Height")) {
                r.getSprite().setTexture(new Texture("Assets/Hackables/routerbad.png"));
                minigame = true;
                game.getCam().setToOrtho(false, 800, 600);
                r.hack(level, (int)(Math.random()*(Level6Jail.getLevelNum()<= 4?Level6Jail.getLevelNum()+1:5)));
                break;
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

    boolean getMini()
    {
        return minigame;
    }

    void switchMini ()
    {
        isSitting = false;
        minigame = false;
    }

    public void setLose(boolean b) {
        loss = b;
    }

    public void setPaused(boolean b) {
        paused = b;
    }

    int getBattery() {
        return battery;
    }

    Body getBody()
    {
        return body;
    }

    public boolean isLoss() {
        return loss;
    }

    public boolean isPaused() {
        return paused;
    }
}