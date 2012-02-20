package com.varaneckas.conway;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {
	
	private GameContext gameContext;
	private GameView gameView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utils.debug(this, "onCreate()");
        super.onCreate(savedInstanceState);
        gameContext = GameContext.create();
        gameView = new GameView(this);
        gameView.setGameContext(gameContext);
        setContentView(gameView);
    }
    
    @Override
    protected void onStart() {
    	Utils.debug(this, "onStart()");
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	Utils.debug(this, "onStop()");
    	super.onStop();
    	gameContext.setState(State.STOPPED);
    }
}