package com.example.thegame;

import java.util.ArrayList;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * a class representing a sprite (GameObject)
 * @param x top of a rectangle
 * @param y left of a rectangle
 */
public class PhysicsSprite extends GameObject {

ArrayList<Rectangle> myRectangles;
	public PhysicsSprite(final float pX, final float pY, final TiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,
			ArrayList<Rectangle> rectangles
			
			) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		myRectangles = new ArrayList<Rectangle>(rectangles);
	}
	
	public float getVelocityY() // helpful to prevent jumping while jumping
	{
		return this.mPhysicsHandler.getVelocityY();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void move() { // fall, if not on the platform, else stay in place
		
			for(Rectangle r: myRectangles){ // check collision with every platform
				if(
						new Rectangle(this.mX+50,this.mY+this.mHeight-8,this.mWidth-100,5,vbom).
						collidesWith(
						r
						))  // collision with platform
		{
			this.mPhysicsHandler.setVelocityY(0); //don't fall
			break;
			
		}else{
			this.mPhysicsHandler.setVelocityY(140); // no collision -> gravity
		}
		}
	}
}