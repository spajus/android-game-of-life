package com.varaneckas.conway;

import android.util.Log;


public class MainLoop implements Runnable {
	
	private final GameContext context;
	
	public final static long MIN_TICK_TIME = 200;
	
	private long lastUpdate;
	
	private Thread mainLoop;
	
	public MainLoop(GameContext context) {
		this.context = context;
	}

	/**
	 * The main loop happens here.
	 * @see #start()
	 * @see #update()
	 */
	@Override
	public void run() {
		Utils.debug(this, "Starting game loop");
		
		// The main loop will run until the game gets stopped.
		while (context.getState() != State.STOPPED) {
			
			// Handle game pause. Just sleep and wait till we're in a different
			// state.
			while (context.getState() == State.PAUSED) {
				Utils.sleep(100);
			}
			
			// Main update - see the method for details.
			update();
		}
		
		Utils.debug(this, "Stopping game loop");
	}
	
	/**
	 * Gives the spark to the main loop. 
	 * @see #run()
	 */
	public void start() {
		
		if (mainLoop != null) {
			// Don't start a second game loop - the game was most likely paused 
			// or stopped, so just resume.
			context.setState(State.RUNNING);
		}
		
		Utils.debug(this, "Starting game loop thread");
		mainLoop = new Thread(this);
		mainLoop.start();
	}
	
	/**
	 * Game loop should process game aspects in following order:
	 * 1. state
	 * 2. input
	 * 3. AI
	 * 4. physics
	 * 5. animations
	 * 6. sound
	 * 7. video
	 * 
	 * State.
	 * In our example user has no direct control over states, it is switched
	 * when certain system events happen - view surface is ready, app is paused
	 * or closed. state is handled in {@link #run()} method.
	 * 
	 * Input.
	 * All touch events are handled asynchronously when {@link GameView} is 
	 * touched. Everything goes to {@link Input} and it is used every time when 
	 * {@link Logic} ticks.
	 * 
	 * AI physics.
	 * All the AI and physics, if you can call it that, happens in 
	 * {@link Logic#tick()}.
	 * 
	 * Animations & sound.
	 * There are no animations in between the game states, and there is no 
	 * sound, so we have nothing here for that.
	 * 
	 * Video.
	 * Finally, video update happens in {@link Video#update()}.
	 * 
	 * After everything runs, {@link #limitFPS()} ensures that the game does not
	 * run too fast. Although if you won't make any optimizations, your game 
	 * update will not be faster than {@link #MIN_TICK_TIME}.
	 * 
	 * I recommend reading this great article:
	 * http://www.rbgrn.net/content/54-getting-started-android-game-development
	 */
	private void update() {
		try {
			// Process input and recalculate cells.
			context.getLogic().tick();
			
			// Draw new cell matrix on our game view.
			context.getVideo().update();
			
			// Limits game speed on faster devices.
			limitFPS();
			
		// Let's protect ourselves from a disaster when device draws the 
		// exception popup.
		} catch (Exception e) {
			Log.e(MainLoop.class.getSimpleName(), 
					"Unexpected exception in main loop", e);
		}
	}

	/**
	 * Counts time that passed since last game update and sleeps for a while if
	 * this time was shorter than target frame time.
	 */
	private void limitFPS() {
		long now = System.currentTimeMillis();
		if (lastUpdate > 0) {
			long delta = now - lastUpdate;
			if (delta < MIN_TICK_TIME) {
				Utils.sleep(MIN_TICK_TIME - delta);
			}
		}
		lastUpdate = System.currentTimeMillis();
	}

}
