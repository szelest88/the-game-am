package com.example.thegame;

/**
 * Class containing information about character state and interaction
 * state (is finger down, finger position etc.)
 * Everything public and static, which is ugly, but it gives quite easy way of
 * passing data between the game loop and event handlers, while representing the state.
 * In such a simple app, should suffice.
 */
public class State {
	/** is the monkey turned to the left or to the right */
	public static boolean goesLeft = false; 
	/** is finger on the screen */
	public static boolean fingerDown = false;
	/** true while jump */
	public static boolean jump = false;
	/** how high is the monkey (during a jump) */
	public static float upChange=0;
	/** finger position (left) */
	public static int fingerLeft = 0;
}
