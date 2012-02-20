package com.varaneckas.conway;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;


public class Video {
	
	private final GameContext context;
	private final Paint bgPaint;
	private final Paint cellPaint;
	private final Paint prePaint;
	private SurfaceHolder surfaceHolder;
	private int width;
	private int height;
	
	public static final float SCALE = 15f;
	
	public Video(GameContext context) {
		this.context = context;
		bgPaint = new Paint();
		bgPaint.setColor(Color.WHITE);
		cellPaint = new Paint();
		cellPaint.setColor(Color.BLACK);
		prePaint = new Paint();
		prePaint.setColor(Color.GREEN);
	}
	
	
	public void setSize(int w, int h) {
		Utils.debug(this, "Setting video size: %d x %d", w, h);
		width = w;
		height = h;
	}
	
	public int getMatrixWidth() {
		return Math.round(width / SCALE);
	}
	
	public int getMatrixHeight() {
		return Math.round(height / SCALE);
	}
	
	private void drawBackground(Canvas canvas) {
		canvas.drawRect(new Rect(0, 0, width, height), bgPaint);
	}

	public void update() {
		Canvas canvas = surfaceHolder.lockCanvas();
		if (canvas != null) {
			drawBackground(canvas);
			drawProcessed(canvas);
			drawUnprocessed(canvas);
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}
	
	private void drawProcessed(Canvas canvas) {
		// TODO Auto-generated method stub
		drawCells(canvas, context.getLogic().getCells(), cellPaint);
		
	}

	private void drawUnprocessed(Canvas canvas) {
		// TODO Auto-generated method stub
		List<Cell> preview = new ArrayList<Cell>();
		for (Touch touch : context.getInput().getUnprocessed()) {
			preview.add(new Cell(touch.x, touch.y));
		}
		drawCells(canvas, preview, prePaint);
		
	}

	private void drawCells(Canvas canvas, List<Cell> cells, Paint paint) {
		for (Cell cell : cells) {
			//canvas.drawPoint(cell.getX(), cell.getY(), cellPaint);
			/*
			canvas.drawCircle(
					cell.getX() * SCALE, 
					cell.getY() * SCALE, 
					SCALE / 2, 
					paint);
					*/
			canvas.drawRect(new Rect(
					Math.round(cell.getX() * SCALE), 
					Math.round(cell.getY() * SCALE), 
					Math.round(cell.getX() * SCALE + SCALE), 
					Math.round(cell.getY() * SCALE + SCALE)), 
					paint);
		}
	}

	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;
	}

}
