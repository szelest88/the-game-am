package com.example.thegame;


import java.util.Timer;
import java.util.TimerTask;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;


public class MainActivity extends BaseGameActivity implements IOnSceneTouchListener,
SensorEventListener
{
	
Camera mCamera;
Scene  mScene;
int width, height;
@Override
public EngineOptions onCreateEngineOptions(){
	DisplayMetrics metrics = getResources().getDisplayMetrics();
	
	width  = metrics.widthPixels;
	height = metrics.heightPixels;
	
	mCamera = new Camera(0, 0, width, height);
	
	return new EngineOptions(
			true, ScreenOrientation.PORTRAIT_SENSOR,
			new RatioResolutionPolicy(width, height), mCamera
			);
}

/////////////////////////////////////////
///////////////////////////////////////// SPRITES
/////////////////////////////////////////

// banana's sprite objects
Sprite bananaSprite;
BitmapTextureAtlas bananaAtlas;
TextureRegion bananaTex;

// monkey's sprite objects
int monkeyTop = 0;
BitmapTextureAtlas monkeyAtlas;
TiledTextureRegion monkeyTex;
int monkeyLeft = 0;
PhysicsSprite playerSprite;

// background sprite objects, two for the parallax effect
Sprite  backSprite1, backSprite2;
BitmapTextureAtlas backAtlas1, backAtlas2;
TextureRegion backTex1, backTex2;

//menu button's (for devices with no physical button) objects
Sprite menuButtonSprite;
BitmapTextureAtlas menuButtonAtlas;
TextureRegion menuButtonTex;


boolean inTheAir = false;

@Override
public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {



	mScene = new Scene();

	mScene.setOnSceneTouchListener(this); // we have to pass the listener to a scene
	
	// and now updateHandler ("game loop")
	mScene.registerUpdateHandler(new IUpdateHandler() {
		public void onUpdate(float pSecondsElapsed) {	
			if(State.fingerDown){ // a screen is being touched
				if(State.fingerLeft<monkeyLeft+monkeyTex.getWidth()/2) //move left, if on the left
					monkeyLeft-=3;
				else //move right if on the right
					monkeyLeft+=3;
				}
			
			if(State.jump==true || State.upChange>0){ 
				
										/* jump, a little bit complicated, but I couldn't make
									    AndEngine's physics "subengine" work as it should 
									    for jumping (deceleration).
							    		
							    		what is happening here is that we're changing position 
							    		1 pixel up every frame; during 20 frames after "jump"
									    event was detected.
									    */
				State.jump=false;
				if(State.upChange<20){
						State.upChange++;
				}
				else{
					State.upChange=0;
				}
			}
			
			
			// our main character - change position according to "State.upChange" 
			// (while jumping )
			playerSprite.setPosition(monkeyLeft, playerSprite.getY()-State.upChange);
			
			if(Math.abs(playerSprite.getX()-bananaSprite.getX())<50 &&
					Math.abs(playerSprite.getY()-bananaSprite.getY())<50
						)
				playerSprite.setPosition(0,200);
			
			/*
			parallax background, there are some classes in AndEngine for it, 
			but they're strange.
			it was easier to write my own version (as it's quite simple)
			/2, /8 divisions - move background like a player, only slower
			*/
			backSprite1.setPosition(monkeyLeft/2-4096,monkeyTop/2-4096);
			backSprite2.setPosition(monkeyLeft/8-4096, monkeyTop/8-4096);	
					menuButtonSprite.setPosition(
					monkeyLeft+monkeyTex.getWidth()/2+width/2-32, 
					playerSprite.getY()+monkeyTex.getHeight()/2+height/2-32);
				mCamera.setChaseEntity(playerSprite); //camera following the monkey
			
		}
	
	@Override
	public void reset() {  
	}
	});
	
	
	VertexBufferObjectManager vbom = this.getVertexBufferObjectManager(); // for sprites
	
	// initialize background sprites
	
	backSprite1 = new Sprite(monkeyLeft/2-4096,monkeyTop/2-4096,backTex1, vbom);
	backSprite2 = new Sprite(monkeyLeft/8-4096,monkeyTop/8-4096,backTex2, vbom);
	
	mScene.attachChild(backSprite1);
	mScene.attachChild(backSprite2);
	
	//create a level
	LevelBuilder lb = new LevelBuilder(vbom);
	
	lb.add(0, 0, 500); // ground at (0,0); width = 500
	
	// platforms (out level):
	lb.add(	300,100); //x, y (x <-height)
	lb.add(	150,200);
	lb.add(	300,300);
	lb.add(	450,400);
	lb.add(	300,500);
	lb.add(	150,600);
	lb.add(	0,	700);
	lb.add(	150,800);
	lb.add(	300,900);
	lb.add(	150,1000);
	
	lb.add(	0,	1100); // banana platform
	
	for(Rectangle r: lb.rectangles){ // add created rectangles to our scene
		r.setY(r.getY());
		r.setColor(Color.BLUE);
		mScene.attachChild(r);
	}
	
	// add the banana
	int bananaHeight = 1250;
	bananaSprite = new Sprite(0, 500-bananaHeight, bananaTex, vbom);
	mScene.attachChild(bananaSprite);
	
	

	
	//add the player
	playerSprite = new PhysicsSprite(monkeyLeft, monkeyTop, monkeyTex, 
			vbom, lb.rectangles);
	mScene.attachChild(playerSprite);
	
	//add menu button (hm, should be optional.
	
	menuButtonSprite = new Sprite(0,0,menuButtonTex,vbom);
	mScene.attachChild(menuButtonSprite);

	pOnCreateSceneCallback.onCreateSceneFinished(mScene);
}
SensorManager sensorManager;
@Override
public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)
throws Exception{

    sensorManager = (SensorManager) this
                    .getSystemService(Context.SENSOR_SERVICE);
    sensorManager.registerListener(this, sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);


	  BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

	  // loading banana's resources
	  
	  bananaAtlas = new BitmapTextureAtlas(getTextureManager(), 183, 127);
	  bananaTex = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
			  bananaAtlas, this, "banana.png", 0, 0);
	  bananaAtlas.load();
	  
	// loading menu buttons's resources
		  menuButtonAtlas = new BitmapTextureAtlas(getTextureManager(), 32, 32);
		  menuButtonTex = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				  menuButtonAtlas, this, "menu_button.png", 0, 0);
		  menuButtonAtlas.load();
	
	  // monkey's resources
	  
	  monkeyAtlas = new BitmapTextureAtlas(getTextureManager(), 560, 168);
	  monkeyTex = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
			  monkeyAtlas,this.getAssets(), 
			  "anim.png",
			  0,0,4,1); 
	  monkeyAtlas.load();
	  
	  // resources for our background, two textures for parallax background
	  
	  backAtlas1 = new BitmapTextureAtlas(getTextureManager(), 512, 512,
    		  TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
	  backTex1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
			  backAtlas1, this, "stars1.png",0,0);
	  backTex1.setTextureSize(8192, 8192);
      backAtlas1.load();	

      backAtlas2 = new BitmapTextureAtlas(getTextureManager(), 512, 512,
    		  TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
      backTex2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    		  backAtlas2, this, "stars2.png",0,0);
      backTex2.setTextureSize(8192, 8192);
      backAtlas2.load();	
      
      
	  pOnCreateResourcesCallback.onCreateResourcesFinished();

}
boolean fingerReleased=true; // technically, it's local for onSceneTouchEvent.
							 // but has to be defined here, as the internal method uses it.
@Override
public boolean onSceneTouchEvent(Scene arg0, TouchEvent arg1) {

	
	if(arg1.getAction() == TouchEvent.ACTION_DOWN ||
			arg1.getAction() == TouchEvent.ACTION_UP)	    
	if((int)arg1.getX()-playerSprite.getX()-32>width/2 && //0 -> æw
			(int)arg1.getY()-playerSprite.getY()-32>height/2) // 0-> æw
	{
		Intent i = new Intent(MainActivity.this, MenuActivity.class);
    	startActivityForResult(i, 1);
    	return true;
	}
	
	if(!acc){
    if(arg1.getAction() == TouchEvent.ACTION_DOWN) {
    	
    	fingerReleased=false;
    
    	State.fingerLeft = (int)arg1.getX();
    		
		if(State.fingerLeft<playerSprite.getX()){
			State.goesLeft=true;
		}
		else{
			State.goesLeft=false;
			
		}
		
		if(State.goesLeft)
			playerSprite.animate(new long[]{200, 200},new int[]{0,1});
		else
			playerSprite.animate(new long[]{200, 200},new int[]{2,3});
	
		Timer t = new Timer(); 	// a hack for distinction between short tap and keeping the finger
								// on the screen surface. Probably it's possible to do it in some
						   		// less complicated way.
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				// if fingerReleased==true, 
				// it means an ACTION_UP event occurred
				// max 200ms after ACTION_DOWN, which means "tap"
				// playerSprite.getVelocity()==0 <- cannot jump while jumping (or falling)
				if(fingerReleased && playerSprite.getVelocityY()==0) 
						State.jump=true;		
			}
		}, 200);
    }

    if(arg1.getAction() == TouchEvent.ACTION_UP) {

    	
    		
    	fingerReleased=true;
    	State.fingerDown = false;
    	
    	if(State.goesLeft) // character is not moving, so we set sprite to "standing"
    		playerSprite.setCurrentTileIndex(0);
    	else
    		playerSprite.setCurrentTileIndex(3);
    	playerSprite.stopAnimation();
			    
    }
    if(arg1.getAction() == TouchEvent.ACTION_MOVE)
    {
    	State.fingerDown = true;
    	State.fingerLeft = (int)arg1.getX();
    	
    	if(State.fingerLeft<playerSprite.getX()){ // if left, a flag for set appropriate sprite state
    		if(State.goesLeft == false){
    		playerSprite.animate(new long[]{200, 200},new int[]{0,1});
    			
    		State.goesLeft=true;
    		}
    	}else{
    		if(State.goesLeft == true){
    			playerSprite.animate(new long[]{200, 200},new int[]{2,3});
        		
    			State.goesLeft=false;
    		}
    	}
		
    }
	}
	return fingerReleased;
}


@Override
public void onPopulateScene(Scene arg0, OnPopulateSceneCallback arg1)
		throws Exception {
    arg1.onPopulateSceneFinished();
}

@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// TODO Auto-generated method stub
	
}
static boolean acc = false;
float prevY = 0;
boolean isGoing=false;
@Override
public void onSensorChanged(SensorEvent event) {
	
	// control by accelerometer, 
	//using state variables same as in the case of control by touchscreen
	// TODO Auto-generated method stub
	synchronized (this) {

		if(acc == true){
		State.jump = false;
        switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
        		if(event.values[0]>2){
        			State.fingerDown = true;
        			State.fingerLeft = 10;
        			State.goesLeft = true;
        			if(!isGoing){
        				playerSprite.animate(new long[]{200, 200},new int[]{0,1});
        			}
        			isGoing=true;
        			
        		}
        		else if(event.values[0]<-2){
        			State.fingerDown = true;
        			State.fingerLeft = 700;

        			State.goesLeft = false;
        			
        			if(!isGoing){
        				playerSprite.animate(new long[]{200, 200},new int[]{2,3});
        			}
        			isGoing=true;
        		}
        		else
        		{
        			State.fingerDown  = false;
        			isGoing = false;
        				playerSprite.stopAnimation();
        		}
        		
        		if(event.values[1]<prevY-2 && playerSprite.getVelocityY()==0)
        			State.jump = true;
                prevY = event.values[1];
        		break;
        }
		}
	}
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) 
{  
    if (keyCode == KeyEvent.KEYCODE_MENU)
    {
    	Intent i = new Intent(MainActivity.this, MenuActivity.class);
    	
    	startActivityForResult(i, 1);
    }
    if(keyCode == KeyEvent.KEYCODE_BACK) {
    	finish();
    	return true;
    	}
    return false; 
}

protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	  if (requestCode == 1) { // ok

	     if(resultCode == RESULT_OK){      
	         acc=data.getBooleanExtra("accelerometer",false);
	         
	     }
	     if (resultCode == RESULT_CANCELED) {    
	     }
	  }
	}


}
