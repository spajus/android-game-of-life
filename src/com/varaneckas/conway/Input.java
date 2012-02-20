package com.varaneckas.conway;

import java.util.HashSet;
import java.util.Set;

public class Input {
	
	private Set<Touch> unprocessed = new HashSet<Input.Touch>();
	private Set<Touch> processed = new HashSet<Input.Touch>();
	
	public static class Touch {
		
		public int x;
		public int y;
		public float pressure;
		
		public Touch(float x, float y, float pressure) {
			this.x = Math.round(x);
			this.y = Math.round(y);
			this.pressure = pressure;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Touch other = (Touch) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Touch [" + x + ":" + y + " @ " + pressure + "]";
		}
		
	}

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
