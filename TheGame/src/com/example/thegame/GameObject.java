package com.example.thegame;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

// a class for an animated sprite, which can be controlled (a top class for PhysicsSprite,
// where most of interesting thing happen).
// based on:
// http://perle-development.com/tutorials/andengine-tutorial-03-player-movement-01/

public abstract class GameObject extends AnimatedSprite {

	public PhysicsHandler mPhysicsHandler;

	VertexBufferObjectManager vbom;
	public GameObject(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.mPhysicsHandler = new PhysicsHandler(this);
		this.registerUpdateHandler(this.mPhysicsHandler);
		vbom = pVertexBufferObjectManager;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
			move();
			
		super.onManagedUpdate(pSecondsElapsed);
	}

	public abstract void move();
}
