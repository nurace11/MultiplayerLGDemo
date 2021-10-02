package com.ourgdx.snakeidle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.ourgdx.snakeidle.screens.LevelScreen;

public class SnakeIdle extends BaseGame {
	public static int screenWidth = 1280, screenHeight = 720;

	public static Preferences preferences;

	public static PlayerState playerState;

	@Override
	public void create() {
		super.create();

		preferences = Gdx.app.getPreferences("save23");

		playerState = new PlayerState();

		setActiveScreen(new LevelScreen());
	}
}
