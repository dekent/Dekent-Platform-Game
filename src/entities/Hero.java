package entities;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import terrain.Level;

public class Hero {
	float SPRITE_PIXEL = 1.0f/512.0f;
	
	private Texture texture;
	
	enum motionState {
		running, jumping, idle, ducking, beginDuck, beginJump, endRun, endDuck, landing, rolling
	}
	
	public enum direction {
		right, left
	}
	
	float herox;
	float heroy;
	float heroxSpeed;
	float heroySpeed;
	motionState mState;
	direction facing;
	boolean onGround;
	int frame;
	
	/**
	 * Constructor for a Hero object
	 * 
	 * @param x initial x position
	 * @param y initial y position
	 * @param dir initial direction to face
	 */
	public Hero(int x, int y, direction dir)
	{
		herox = x;
		heroy = y;
		heroxSpeed = 0;
		heroySpeed = 0;
		mState = motionState.idle;
		facing = dir;
		onGround = true;
		frame = 0;
	}
	
	/**
	 * Updates position based on velocities
	 */
	public void updatePosition()
	{
		herox += heroxSpeed;
		
		if (!onGround)
		{
			heroySpeed -= .2;
			if (heroySpeed < -10)
				heroySpeed = -10;
			heroy += heroySpeed;
			System.out.println(heroySpeed);
		}
		else
		{
			heroySpeed = 0;
		}
	}
	
	/**
	 * Adjust animation frames and speeds for the character running left
	 */
	public void runLeft()
	{
		facing = direction.left;
		
		if (mState != motionState.running)
		{
			mState = motionState.running;
			frame = 32;	//closest run frame to the idle position for a more natural start
			heroxSpeed = -.5f;
		}
		else
		{
			frame = (frame + 1) % 48;
			if (heroxSpeed > -3)
				heroxSpeed -= .5f;
		}
	}
	
	/**
	 * Adjust animation frames and speeds for the character running right
	 */
	public void runRight()
	{
		facing = direction.right;
		
		if (mState != motionState.running)
		{
			mState = motionState.running;
			frame = 32;	//closest run frame to the idle position for a more natural start
			heroxSpeed = .5f;
		}
		else
		{
			frame = (frame + 1) % 48;
			if (heroxSpeed < 3)
				heroxSpeed += .5f;
		}
	}
	
	/**
	 * Adjust idle animation frames and speeds, as well as handle any transitions into the idle state
	 */
	public void idle()
	{
		if (mState == motionState.running)
		{
			mState = motionState.endRun;
			if (frame < 8 || frame >= 32)
				frame = 0;
			else if (frame < 16 && frame >= 8)
				frame = 4;
			else
				frame = 8;
		}
		else if (mState == motionState.endRun)
		{
			frame ++;
			if (frame == 8 || frame == 16)
			{
				frame = 0;
				mState = motionState.idle;
			}
		}
		else
		{
			mState = motionState.idle;
		}
		
		if (heroxSpeed > 0)
			heroxSpeed = (int)(heroxSpeed - 1);
		else if (heroxSpeed < 0)
			heroxSpeed = (int)(heroxSpeed + 1);
	}
	
	
	public void onGround(Level terrain)
	{
		onGround = terrain.checkIfStanding(getX() + 20, getX() + 44, getY());
	}
	
	public void checkTerrainCollision(Level terrain)
	{	
		int correctedPos;
		
		//First, detect horizontal collision.  If a horizontal collision is detected, stop there.  Otherwise, go on to vertical collision detection
		
		correctedPos = terrain.detectVerticalCollision(getX() + 20, getX() + 44, getY());
		if (correctedPos != -999)
			heroy = correctedPos;
	}
	
	/**
	 * Mapping of frames to x coordinates on the sprite sheet
	 * 
	 * @return the x coordinate of the sprite on the sprite sheet
	 */
	public int determineFrameX() 
	{
		if (mState == motionState.running || (mState == motionState.endRun && facing == direction.right))
			return (int)frame/4;
		else if (mState == motionState.endRun && facing == direction.left)
			return (int)frame/4 + 4;
		else if (facing == direction.right)
			return 0;
		else
			return 1;
	}
	
	/**
	 * Mapping of frames to y coordinates on the sprite sheet
	 * 
	 * @return the y coordinate of the sprite on the sprite sheet
	 */
	public int determineFrameY()
	{
		if (mState == motionState.running && facing == direction.right)
			return 1;
		else if (mState == motionState.running && facing == direction.left)
			return 2;
		else if (mState == motionState.endRun)
			return 3;
		else
			return 0;
	}
	
	/**
	 * Render the sprite with the correct animation frame and position
	 */
	public void renderSprite()
	{
		int x = determineFrameX();
		int y = determineFrameY();
	
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glTranslatef(getX(), getY(), 0);

		texture.bind();
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    	
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(x*(SPRITE_PIXEL*32) , y*(SPRITE_PIXEL*32) + SPRITE_PIXEL*32);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(x*(SPRITE_PIXEL*32) + SPRITE_PIXEL*32, y*(SPRITE_PIXEL*32) + SPRITE_PIXEL*32);
			GL11.glVertex2f(64, 0);
			GL11.glTexCoord2f(x*(SPRITE_PIXEL*32) + SPRITE_PIXEL*32, y*(SPRITE_PIXEL*32));
			GL11.glVertex2f(64, 64);
			GL11.glTexCoord2f(x*(SPRITE_PIXEL*32) , y*(SPRITE_PIXEL*32) );
			GL11.glVertex2f(0, 64);
		GL11.glEnd();
	}
	
	/**
	 * Load sprite sheet to OpenGL
	 */
	public void loadTexture()
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("sprites/prototype2.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Getter for x coordinate
	 * 
	 * @return hero x coordinate
	 */
	public int getX()
	{
		return (int)herox;
	}
	
	/**
	 * Getter for y coordinate
	 * 
	 * @return hero y coordinate
	 */
	public int getY()
	{
		return (int)heroy;
	}
	
	/**
	 * Setter for x coordinate
	 * 
	 * @param x new hero x coordinate
	 */
	public void setX(int x)
	{
		herox = x;
	}
	
	/**
	 * Setter for y coordinate
	 * 
	 * @param y new hero y coordinate
	 */
	public void setY(int y)
	{
		heroy = y;
	}
}
