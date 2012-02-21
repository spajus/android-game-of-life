package com.varaneckas.conway;

import android.app.Activity;
import android.os.Bundle;

/**
 * This is the starting point where you should begin to analyze the code. Before
 * 
 * In Androd, {@link Activity} represents a single application screen.
 * This Activity will be used as single screen which will be used for whole
 * play time.
 * 
 * To find out more about {@link Activity} lifecycle, you should read this:
 * http://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
 * 
 * @author Tomas Varaneckas
 */
public class GameActivity extends Activity {
	
	/**
	 * We will need {@link GameContext} in our main {@link Activity} so that
	 * we can control our game state.
	 */
	private GameContext gameContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	// Some debugging here and in other places will give you a brief view
    	// of event sequence. It's helpful while you are not familiar with
    	// Android application lifecycle.
    	Utils.debug(this, "onCreate()");
        
    	super.onCreate(savedInstanceState);
    	
        // Get the instance of GameContext when main activity gets created.
        gameContext = GameContext.getInstance();
        
        // Create the SurfaceView driven GameView where we will be 
        // drawing on
        GameView gameView = new GameView(this);
        
        // View will require our GameContext, as it will start the main loop
        // when the surface is ready to draw on.
        gameView.setGameContext(gameContext);
        
        // Finally, let our Activity know that we will want to see our GameView
        setContentView(gameView);
    }
    
    /* The methods below will handle the game state. */
    
    @Override
    protected void onStop() {
    	Utils.debug(this, "onStop()");
    	super.onStop();
    	gameContext.setState(State.STOPPED);
    }
    
    @Override
    protected void onPause() {
    	Utils.debug(this, "onPause()");
    	super.onPause();
    	gameContext.setState(State.PAUSED);
    }
    
    @Override
    protected void onResume() {
    	Utils.debug(this, "onResume()");
    	super.onResume();
    	gameContext.setState(State.RUNNING);
    }
}