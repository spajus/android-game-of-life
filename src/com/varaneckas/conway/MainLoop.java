package com.varaneckas.conway;


public class MainLoop implements Runnable {
	
	private final GameContext context;
	
	public final static long MIN_TICK_TIME = 200;
	
	private long lastUpdate;
	
	private Thread mainLoop;
	
	public MainLoop(GameContext context) {
		this.context = context;
	}

	@Override
	public void run() {
		Utils.debug(this, "Starting game loop");
		while (context.getState() != State.STOPPED) {
			while (context.getState() == State.PAUSED) {
				Utils.debug(this, "Sleeping while game is paused");
				Utils.sleep(100);
			}
			update();
		}
		Utils.debug(this, "Stopping game loop");
	}
	
	public void start() {
		if (mainLoop != null) {
			throw new IllegalStateException("Trying to start a second main loop");
		}
		mainLoop = new Thread(this);
		Utils.debug(this, "Starting game loop thread");
		mainLoop.start();
	}
	
	private void update() {
		//state
		
		//input
		//AI
		//physics
		context.getPhysics().tick();
		//animations
		//sound
		//video
		context.getVideo().update();
		long now = System.currentTimeMillis();
		if (lastUpdate > 0) {
			long delta = now - lastUpdate;
			if (delta < MIN_TICK_TIME) {
				Utils.sleep(MIN_TICK_TIME - delta);
			}
		}
		lastUpdate = System.currentTimeMillis();
	}
	
	public GameContext getContext() {
		return context;
	}

}
