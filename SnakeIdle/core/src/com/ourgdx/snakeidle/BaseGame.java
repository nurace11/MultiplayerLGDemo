package com.ourgdx.snakeidle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ourgdx.snakeidle.screens.BaseScreen;

public abstract class BaseGame extends Game {
    private static BaseGame game;

    public static Skin skin;

    public BaseGame(){
        game = this;
    }

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        /*fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("futura-normal.ttf"));

        FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = Gdx.graphics.getWidth() / 46;//28
        fontParameters.color = Color.WHITE;
        fontParameters.borderWidth = 2;
        fontParameters.borderColor = Color.BLACK;
        fontParameters.borderStraight = true;
        fontParameters.minFilter = TextureFilter.Linear;
        fontParameters.magFilter = TextureFilter.Linear;

        customFont = fontGenerator.generateFont(fontParameters);
        labelStyle.font = customFont;

        FreeTypeFontParameter infoTextParameters = new FreeTypeFontParameter();
        infoTextParameters.size = Gdx.graphics.getWidth() / 80;//16
        infoTextParameters.color = Color.WHITE;*/

        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor( im );
    }

    public static void setActiveScreen(BaseScreen screen){
        game.setScreen(screen);
    };

    @Override
    public void dispose() {
        super.dispose();
    }
}
