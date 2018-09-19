package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Hackerman;
import com.mygdx.game.Options;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        Options.loadOptions();
        config.title = "Hackerman";
        config.height = 600;
        config.width = 800;
        config.fullscreen = Options.isFullScreen();
        config.resizable = false;
        new LwjglApplication(new Hackerman(), config);
    }
}
