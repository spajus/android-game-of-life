package com.varaneckas.conway;


/**
 * This is our game context. It links all game aspects together, different 
 * components that have this context can fetch the required information 
 * without having to talk to other components directly.
 * 
 * You can bring new features to the game and add them to this context, i.e.
 * if you wanted to have sound effects, add Sound class, register it here 
 * and call required methods when certain events happen.
 *  
 * @author Tomas Varaneckas
 */
public class GameContext {
	
	/**
	 * Singleton instance.
	 * @see #getInstance()
	 */
	private static GameContext instance;
	
	/**
	 * The game state. {@link MainLoop} switches it's logic according to it. 
	 */
	private State state;
	
	/**
	 * The main game loop. See it's documentation for more details.
	 */
	private final MainLoop gameLoop;
	
	/**
	 * Video is responsible for drawing the game on device screen. Without it
	 * everything would happen in pure darkness.
	 */
	private final Video video;
	
	/**
	 * Logic does all the calculations and controls where the cells are.
	 */
	private final Logic logic;
	
	/**
	 * Handles screen touches.
	 */
	private final Input input;
	
	/**
	 * Private constructor - use {@link #create()} to get the game context.
	 */
	private GameContext() {
		state = State.RUNNING;
		video = new Video(this);
		gameLoop = new MainLoop(this);
		logic = new Logic(this);
		input = new Input();
	}
	
	/** 
	 * Singleton getter that returns the instance of GameContext.
	 * We need a singleton here because when screen is rotated, Activity and
	 * View gets destroyed and created again, but we have to retain the game
	 * state.
	 */
	public static GameContext getInstance() {
		if (instance == null) {
			instance = new GameContext();
		}
		return instance;
	}

	/**
	 * Changes the game state. Becomes effective in next main loop cycle.
	 * @param state New game {@link State}.
	 */
	public void setState(State state) {
		Utils.debug(this, "Setting game state to %s", state);
		this.state = state;
	}
	
	/* Getters for various game aspects */
	
	public State getState() {
		return state;
	}
	
	public Video getVideo() {
		return video;
	}
	
	public MainLoop getGameLoop() {
		return gameLoop;
	}

	public Logic getLogic() {
		return logic;
	}
	
	public Input getInput() {
		return input;
	}
}
