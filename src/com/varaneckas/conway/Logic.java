package com.varaneckas.conway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The most interesting part of the game. Contains the implementation of 
 * Conway's Game of Life. The game mechanics are described here:
 * http://en.wikipedia.org/wiki/Conway's_Game_of_Life
 * 
 * @author Tomas Varaneckas
 */
public class Logic {

	private GameContext context;
	
	/**
	 * A simple cache for cell neighbor count. Cleaned up after each tick.
	 * Gives immense performance benefit compared to uncached version.
	 */
	private Map<Cell, Integer> neighborMap = 
			new HashMap<Cell, Integer>();
	
	/**
	 * Set of all the cells. Initial capacity and load factor should give
	 * better performance for our situation.
	 * 
	 * {@link HashSet} is chosen because of frequent calls to 
	 * {@link Collection#contains(Object)} operation - if you would switch this
	 * implementation to {@link ArrayList}, there would be an immense
	 * performance degrade. This illustrates the importance of the most suitable
	 * implementation - you should study the Collections Framework well:
	 * http://docs.oracle.com/javase/6/docs/technotes/guides/collections/reference.html
	 */
	private volatile Collection<Cell> cells = new HashSet<Cell>(3000, 0.2f);
	
	public Logic(GameContext context) {
		this.context = context;
	}

	/**
	 * Advances to the next generation of cells.
	 * Reads the processed used input and seeds the cells with it before 
	 * running the logic.
	 */
	public void tick() {
		
		// Materialize user touches into cells.
		Set<Touch> touches = context.getInput().getProcessed();
		for (Touch touch : touches) {
			cells.add(new Cell(touch.x, touch.y));
		}
		
		doLogic();
	}
	
	/**
	 * The main logic that implements Conway's Game of Life.
	 * 
	 * So far this is the slowest method in town. Feel free to optimize it.
	 * You are also welcome to contribute the optimizations back to the source.
	 */
	private void doLogic() {
		long start = System.currentTimeMillis();
		
		// Cells that will die during this generation
		Collection<Cell> toRemove = new HashSet<Cell>();
		// Cells that will be born during this generation
		Collection<Cell> toAdd = new HashSet<Cell>();
		
		// New cell generation. Begins with a copy of the old one.
		Collection<Cell> newCells = new HashSet<Cell>(cells);
		
		// Runs through all the cells and applies the game rules on them.
		for (Cell cell : newCells) {
			
			// We will need the neighbor count for every cell
			int neighbors = countNeighbors(cell, newCells);
			
			// Rule 1 through 3
			if (neighbors < 2 || neighbors > 3) {
				toRemove.add(cell);
			}
			
			// Rule 4
			if (neighbors > 0) {
				collectNearbyRessurectionCandidates(cell, newCells, toAdd);
			}
		}
		
		// Kill the death sentenced cells
		newCells.removeAll(toRemove);
		
		// Do some babies
		newCells.addAll(toAdd);
		
		// Cleanup the cache so the calculations will not go wrong during the
		// next tick.
		neighborMap = new HashMap<Cell, Integer>();
		
		// Replace the generation
		cells = newCells;
		
		// Do some profiling. 
		long end = System.currentTimeMillis();
		long delta = end - start;
		if (delta > MainLoop.MIN_TICK_TIME) {
			// Print the time only when it's slower than our target rate.
			Utils.debug(this, "Game logic took: %s", (end - start));
		}
	}
	
	/**
	 * Calculates the count of neighbors for given cell.
	 */
	private int countNeighbors(Cell cell, Collection<Cell> cells) {
		
		// See if there is a cached value
		if (neighborMap.containsKey(cell)) {
			// FIXME For some weird reasons sometimes this map becomes null.
			return neighborMap.get(cell);
		}
		
		// No cache, let's do the hard work.
		
		// Initial neighbor count
		int count = 0;
		
		int x = cell.getX();
		int y = cell.getY();
		
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				
				// We don't want to run calculations on cells we won't be able 
				// to see.
				if (isOutOfBounds(i, y)) {
					continue;
				}
				
				// Skip the current cell (x:y).
				if (i == x && j == y) {
					continue;
				}
				
				// If cell set contains the cell in same location, it's a 
				// valid neighbor.
				if (cells.contains(new Cell(i, j))) {
					count++;
				}
			}
		}
		
		// Put the count to cache so that we don't have to recalculate it again.
		neighborMap.put(cell, count);
		
		return count;
	}

	/**
	 * According to Conway's Game of Life rule #4, if a dead cell has exactly
	 * 3 neighbors, it must resurrect. 
	 * 
	 * This method works around living cells, it calculates if surrounding dead
	 * cells have 3 neighbors and if they do, those cells are registered in the
	 * candidate collection.
	 */
	private void collectNearbyRessurectionCandidates(Cell cell, 
			Collection<Cell> cells,
			Collection<Cell> candidates) {
		
		int x = cell.getX();
		int y = cell.getY();
		
		// Travel around the cell
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				
				// Skip invisible areas
				if (isOutOfBounds(i, y)) {
					continue;
				}
				
				// Skip the cell itself
				if (i == x && j == y) {
					continue;
				}
				
				// Create a virtual cell
				Cell c = new Cell(i, j);
				
				// Check if cell is alive or already scheduled for a 
				// resurrection. If so, move on.
				if (cells.contains(c) || candidates.contains(c)) {
					continue; //Already there
				} else {
					int neighbours = countNeighbors(c, cells);
					if (neighbours == 3) {
						// Schedule virtual cell for resurrection.
						candidates.add(c);
					}
				}
			}
		}
	}
	
	/**
	 * Checks if given coordinates are out of visible screen. 1 cell is reserved
	 * for padding.
	 * @return true if coordinates are invisible, false otherwise.
	 */
	private boolean isOutOfBounds(int x, int y) {
		
		if (x < -1 || y < -1 
				|| x > context.getVideo().getMatrixWidth() + 1 
				|| y > context.getVideo().getMatrixHeight() + 1) {
			return true;
		}
		
		return false;
	}

	public List<Cell> getCells() {
		// Return a copy of the cells we have. We don't want to leak our 
		// internals in modifiable state.
		return new ArrayList<Cell>(cells);
	}
}