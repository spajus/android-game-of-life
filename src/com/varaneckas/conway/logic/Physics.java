package com.varaneckas.conway.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Looper;

import com.varaneckas.conway.GameContext;
import com.varaneckas.conway.MainLoop;
import com.varaneckas.conway.Input.Touch;
import com.varaneckas.conway.Utils;

public class Physics {

	private GameContext context;
	private Map<Cell, Integer> neighbourMap = new IdentityHashMap<Cell, Integer>();
	private final Collection<Cell> cells = new HashSet<Cell>(3000, 0.2f);
	int cacheHits;

	public Physics(GameContext context) {
		this.context = context;
	}

	public void tick() {
		Set<Touch> touches = context.getInput().getProcessed();
		for (Touch touch : touches) {
			cells.add(new Cell(touch.x, touch.y));
		}
		doLogic();
	}
	
	private void doLogic() {
		long start = System.currentTimeMillis();
		Collection<Cell> toRemove = new HashSet<Cell>();
		Collection<Cell> toAdd = new HashSet<Cell>();
		for (Cell cell : cells) {
			int neighbours = countNeighbours(cell);
			// Rule 1 through 3
			if (neighbours < 2 || neighbours > 3) {
				toRemove.add(cell);
			}
			// Rule 4
			if (neighbours > 0) {
				collectNearbyRessurectionCandidates(cell, toAdd);
			}
			
		}
		cells.removeAll(toRemove);
		cells.addAll(toAdd);
		long end = System.currentTimeMillis();
		long delta = end - start;
		if (delta > MainLoop.MIN_TICK_TIME) {
			Utils.debug(this, "Game logic took: %s", (end - start));
		}
		neighbourMap.clear();
		cacheHits = 0;
	}
	
	private int countNeighbours(Cell cell) {
		if (neighbourMap.containsKey(cell)) {
			cacheHits++;
			return neighbourMap.get(cell);
		}
		int count = 0;
		int x = cell.getX();
		int y = cell.getY();
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (isOutOfBounds(i, y)) {
					continue;
				}
				if (i == x && j == y) {
					continue;
				}
				if (cells.contains(new Cell(i, j))) {
					count++;
				}
			}
		}
		neighbourMap.put(cell, count);
		return count;
	}

	private void collectNearbyRessurectionCandidates(Cell cell, Collection<Cell> candidates) {
		int x = cell.getX();
		int y = cell.getY();
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (isOutOfBounds(i, y)) {
					continue;
				}
				if (i == x && j == y) {
					continue;
				}
				Cell c = new Cell(i, j);
				if (cells.contains(c) || candidates.contains(c)) {
					continue; //Already there
				} else {
					int neighbours = countNeighbours(c);
					if (neighbours == 3) {
						candidates.add(c);
					}
				}
			}
		}
	}
	
	private boolean isOutOfBounds(int x, int y) {
		if (x < 0 || y < 0 || x > context.getVideo().getMatrixWidth() || y > context.getVideo().getMatrixHeight()) {
			return true;
		}
		return false;
	}

	public List<Cell> getCells() {
		return new ArrayList<Cell>(cells);
	}
}