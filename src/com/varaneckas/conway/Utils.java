package com.varaneckas.conway;

import android.util.Log;

public class Utils {
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException("Sleep interrupted", e);
		}
	}

	public static void debug(Object source, String message, Object ... args) {
		Log.i(source.getClass().getSimpleName(), String.format(message, args));
	}

}
