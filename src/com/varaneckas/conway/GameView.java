package com.varaneckas.conway;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * The {@link View} which will hold our game visuals. {@link SurfaceView} is the
 * best implementation for game development. 
 * 
 * Make sure you read the docs:
 * http://developer.android.com/reference/android/view/SurfaceView.html
 * 
 * @author Tomas Varaneckas
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	/**
	 * @see #setGameContext(GameContext)
	 */
	private GameContext gameContext;
	
	/**
	 * Video will be used 
	 */
	private Video video;
	
	public GameView(Context context) {
		super(context);
		
		// SurfaceView needs a SurfaceHolder.Callback work properly.
		// In this case our GameView is a SurfaceHolder.Callback, 
		// so it registers itself.
		getHolder().addCallback(this);
		
		// Focus will give us the possibility to get focus and catch touch 
		// events.
		setFocusable(true);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// While finger is down on screen, we will gather input
		if (event.getAction() != MotionEvent.ACTION_UP) {
			// Adjust event coordinates according to our scale.
			gameContext.getInput().addTouch(
					event.getX() / Video.SCALE,  
					event.getY() / Video.SCALE, 
					event.getPressure());
		} else {
			// When finger is released, input will be flushed into Logic.
			gameContext.getInput().flush();
		}
		
		return true;
	}
	
	/**
	 * The view will need game context to pass itself to video renderer, 
	 * register new input events and start the main loop when canvas are ready.
	 */
	public void setGameContext(GameContext gameContext) {
		this.gameContext = gameContext;
		video = gameContext.getVideo();
		
		// Video will draw game context on this view via the SurfaceHolder.
		video.setSurfaceHolder(getHolder());
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Utils.debug(this, "Size changed");
		video.setSize(w, h);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Utils.debug(this, "Surface changed");
		
		// Resize our video when surface changes (screen rotates)
		video.setSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Utils.debug(this, "Surface created");
		
		// At this point we can start drawing on our GameView, so let's start
		// the main loop.
		gameContext.getGameLoop().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		Utils.debug(this, "Surface destroyed");
		
		// Stop the game when surface gets destroyed.
		gameContext.setState(State.STOPPED);
	}

}
