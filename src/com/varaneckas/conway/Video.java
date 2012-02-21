package com.varaneckas.conway;

import java.util.ArrayList;
import java.util.Collection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;


/**
 * Responsible for rendering the game state which {@link Logic} holds.
 * 
 * @author Tomas Varaneckas
 */
public class Video {
	
	private final GameContext context;
	
	/**
	 * Defines background color.
	 */
	private final Paint bgPaint;
	
	/**
	 * Defines active cell color.
	 */
	private final Paint cellPaint;
	
	/**
	 * Defines color of cells that were drawn with finger but not yet flushed
	 * into game logic.
	 */
	private final Paint prePaint;
	
	/**
	 * {@link SurfaceHolder} that manages our {@link GameView}. This will be 
	 * used for getting the {@link Canvas} to draw on.
	 */
	private SurfaceHolder surfaceHolder;
	
	/**
	 * Screen dimensions in pixels.
	 */
	private int width, height;
	
	/**
	 * Scale that tells how many screen pixels will represent one game pixel.
	 */
	public static final float SCALE = 15f;
	
	/**
	 * Constructor that initializes internal {@link Paint} objects.
	 * @see #bgPaint
	 * @see #cellPaint
	 * @see #prePaint
	 */
	public Video(GameContext context) {
		this.context = context;
		
		bgPaint = new Paint();
		bgPaint.setColor(Color.WHITE);
		
		cellPaint = new Paint();
		cellPaint.setColor(Color.BLACK);
		
		prePaint = new Paint();
		prePaint.setColor(Color.GREEN);
	}
	

	/**
	 * Updates all the video:
	 * 1. Clears background and fills it with {@link #bgPaint}.
	 * 2. Draws {@link Cell} objects that come from {@link Logic}.
	 * 3. Draws unprocessed cells that come from {@link Input}.
	 * @see #prepareBackground(Canvas)
	 * @see #drawCells(Canvas)
	 * @see #drawUnprocessedInput(Canvas)
	 */
	public void update() {
		Canvas canvas = surfaceHolder.lockCanvas();
		if (canvas != null) {
			prepareBackground(canvas);
			drawCells(canvas);
			drawUnprocessedInput(canvas);
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}
	
	/**
	 * Fills given canvas with background color. 
	 */
	private void prepareBackground(Canvas canvas) {
		canvas.drawRect(new Rect(0, 0, width, height), bgPaint);
	}

	/**
	 * Draws the current generation of cells that {@link Logic} provides. 
	 */
	private void drawCells(Canvas canvas) {
		drawCells(canvas, context.getLogic().getCells(), cellPaint);
	}

	/**
	 * Draws virtual cells to visualize unprocessed user input.
	 */
	private void drawUnprocessedInput(Canvas canvas) {
		Collection<Cell> preview = new ArrayList<Cell>();
		for (Touch touch : context.getInput().getUnprocessed()) {
			preview.add(new Cell(touch.x, touch.y));
		}
		drawCells(canvas, preview, prePaint);
	}

	/**
	 * Draws a collection of cells using given paint and canvas.
	 * Cells are represented as a rectangle.
	 * @see #SCALE
	 * @param canvas Canvas to draw on.
	 * @param cells Cells that should be drawn.
	 * @param paint Paint that defines cell color.
	 */
	private void drawCells(Canvas canvas, Collection<Cell> cells, Paint paint) {
		for (Cell cell : cells) {
			canvas.drawRect(new Rect(
					Math.round(cell.getX() * SCALE), 
					Math.round(cell.getY() * SCALE), 
					Math.round(cell.getX() * SCALE + SCALE), 
					Math.round(cell.getY() * SCALE + SCALE)), 
					paint);
		}
	}

	/**
	 * Changes video size. Will be called externally from our {@link GameView}.
	 */
	public void setSize(int width, int height) {
		Utils.debug(this, "Setting video size: %d x %d", width, height);
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Gets the width of our game matrix using scaled game pixels.
	 * @see #SCALE
	 */
	public int getMatrixWidth() {
		return Math.round(width / SCALE);
	}
	
	/**
	 * Gets the height of our game matrix using scaled game pixels.
	 * @see #SCALE
	 */
	public int getMatrixHeight() {
		return Math.round(height / SCALE);
	}
	
	/**
	 * @see #surfaceHolder
	 */
	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;
	}
}
