package com.example.thegame;

import java.util.ArrayList;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

/**
 * class representing level (platforms)
 */
public class LevelBuilder {
	
	VertexBufferObjectManager vbom; // for drawing
	
	public LevelBuilder(VertexBufferObjectManager vbom)
	{
		this.vbom = vbom;
		this.rectangles = new ArrayList<Rectangle>();
	}
	ArrayList<Rectangle> rectangles;
	
	/**
	 * @return rectangles, transformed for game coordinate system
	 */
	public ArrayList<Rectangle> getRectangles(){
		return rectangles;
	}
	/**
	 * adds a rectangle, default width = 150
	 * @param x top of a rectangle ("how high" it is, i.e. 0 is at "ground level", and 10 is above it)
	 * @param y left of a rectangle
	 */
	public void add(float x, float y){
		Rectangle r = new Rectangle(x,500 - y,150.0f,5.0f, vbom);
		r.setColor(Color.BLUE);
		rectangles.add(r);
	}
	/**
	 * 
	 * @param x top of a rectangle
	 * @param y left of a rectangle
	 * @param len width of a rectangle (default = 150)
	 */
	
	public void add(float x, float y, float len){
		Rectangle r = new Rectangle(x,500 - y,len,5.0f, vbom);
		r.setColor(Color.BLUE);
		rectangles.add(r);

	}
}
