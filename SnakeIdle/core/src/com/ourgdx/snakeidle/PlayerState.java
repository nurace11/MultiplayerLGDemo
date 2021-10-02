package com.ourgdx.snakeidle;

public class PlayerState {
    private String nickname;

    public PlayerState(){
        nickname = SnakeIdle.preferences.getString("nickname");
    }

    public void savePlayerState(){
        SnakeIdle.preferences.putString("nickname", nickname);
    }

}
