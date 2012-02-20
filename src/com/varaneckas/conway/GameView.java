package com.varaneckas.conway;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private GameContext gameContext;
	private Video video;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (event.getAction() != MotionEvent.ACTION_UP) {
			gameContext.getInput().addTouch(
					event.getX() / Video.SCALE, 
					event.getY() / Video.SCALE, 
					event.getPressure());
		} else {
			gameContext.getInput().flush();
		}
		return true;
	}
	
	public GameView(Context context) {
		super(context);
		getHolder().addCallback(this);
		setFocusable(true);
	}
	
	public void setGameContext(GameContext gameContext) {
		this.gameContext = gameContext;
		video = gameContext.getVideo();
		video.setSurfaceHolder(getHolder());
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		video.setSize(w, h);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Utils.debug(this, "Surface changed");
		video.setSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Utils.debug(this, "Surface created");
		gameContext.getGameLoop().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Utils.debug(this, "Surface destroyed");
		gameContext.setState(State.STOPPED);
	}

}
