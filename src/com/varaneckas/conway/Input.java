package com.varaneckas.conway;

import java.util.HashSet;
import java.util.Set;

public class Input {
	
	private Set<Touch> unprocessed = new HashSet<Touch>();
	private Set<Touch> processed = new HashSet<Touch>();
	
	public void addTouch(float x, float y, float pressure) {
		synchronized (unprocessed) {
			unprocessed.add(new Touch(x, y, pressure));
		}
	}
	
	public void flush() {
		synchronized (unprocessed) {
			processed = new HashSet<Touch>(unprocessed);
			unprocessed.clear();
		}
	}
	
	public Set<Touch> getUnprocessed() {
		synchronized (unprocessed) {
			Set<Touch> response = new HashSet<Touch>(unprocessed);
			return response;
		}
	}
	
	public Set<Touch> getProcessed() {
		Set<Touch> response = new HashSet<Touch>(processed);
		processed.clear();
		return response;
	}
}
