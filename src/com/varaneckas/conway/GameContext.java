package com.varaneckas.conway;

import com.varaneckas.conway.logic.Physics;

public class GameContext {
	
	private State state;
	private final Video video;
	private final MainLoop gameLoop;
	private final Physics physics;
	private final Input input;
	
	private GameContext() {
		state = State.RUNNING;
		video = new Video(this);
		gameLoop = new MainLoop(this);
		physics = new Physics(this);
		input = new Input();
	}
	
	public static GameContext create() {
		return new GameContext();
	}

	public void setState(State state) {
		Utils.debug(this, "Setting game state to %s", state);
		this.state = state;
	}
	
	public State getState() {
		return state;
	}
	
	public Video getVideo() {
		return video;
	}
	
	public MainLoop getGameLoop() {
		return gameLoop;
	}

	public Physics getPhysics() {
		return physics;
	}
	
	public Input getInput() {
		return input;
	}
}
