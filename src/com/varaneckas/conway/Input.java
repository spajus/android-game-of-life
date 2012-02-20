package com.varaneckas.conway;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles screen {@link Touch} events. Buffers them in {@link #unprocessed} set and provides
 * a {@link #flush()} operation to promote the touches to the {@link #processed}
 * set.
 * 
 * {@link #processed} set gets cleaned up upon retrieval, so that it won't get
 * processed twice.
 * 
 * This class should be thread safe.
 * 
 * @author Tomas Varaneckas
 */
public class Input {
	
	/**
	 * Contains {@link Touch} events that were not yet processed (finger was not
	 * yet released).
	 */
	private Set<Touch> unprocessed = new HashSet<Touch>();
	
	/**
	 * Contains {@link Touch} events that are ready for pickup by the game 
	 * logic.
	 */
	private Set<Touch> processed = new HashSet<Touch>();
	
	/**
	 * Builds and adds a new {@link Touch} event to the {@link #unprocessed}
	 * event buffer.
	 * 
	 * @param x Event coordinate X.
	 * @param y Event coordinate Y.
	 * @param pressure Pressure of the finger.
	 */
	public void addTouch(float x, float y, float pressure) {
		synchronized (unprocessed) {
			unprocessed.add(new Touch(x, y, pressure));
		}
	}
	
	public void flush() {
		synchronized (unprocessed) {
			synchronized (processed) {
				processed = new HashSet<Touch>(unprocessed);
			}
			unprocessed.clear();
		}
	}
	
	public Set<Touch> getUnprocessed() {
		synchronized (unprocessed) {
			return new HashSet<Touch>(unprocessed);
		}
	}
	
	/**
	 * Gets the latest {@link Touch} events that are ready for logic 
	 * manipulation.
	 * 
	 * @return Set of {@link Touch} events.
	 */
	public Set<Touch> getProcessed() {
		synchronized (processed) {
			Set<Touch> response = new HashSet<Touch>(processed);
			processed.clear();
			return response;
		}
	}
}
