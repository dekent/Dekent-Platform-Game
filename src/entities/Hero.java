package entities;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import terrain.Level;

import static entities.Hero.motionState.*;
import static entities.Hero.direction.*;

public class Hero {
	float SPRITE_PIXEL = 1.0f/512.0f;
	
	private Texture texture;
	
	public enum motionState {
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
	motionState prevMState;
	direction facing;
	boolean onGround;
	int[] hitbox = {20, 46, 1, 52};	//x1, x2, y1, y2
	int frame;
	int jumpCounter = 0;
	boolean finishedJump = true;
	
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
		mState = idle;
		facing = dir;
		onGround = true;
		frame = 0;
	}
	
	public void saveState()
	{
		prevMState = mState;
	}
	
	/**
	 * Updates position based on velocities
	 */
	public void updatePosition(Level terrain)
	{
		//prevMState = mState;
		
		boolean collision = false;
		while (detectHorizontalCollision(terrain) == true)
		{
			collision = true;
			if (heroxSpeed < 0)
			{
				heroxSpeed = (int)(heroxSpeed + 1);
			}
			else if (heroxSpeed > 0)
			{
				heroxSpeed = (int)(heroxSpeed - 1);
			}
		}
		herox += heroxSpeed;
		if (collision)
			heroxSpeed = 0;
		
		collision = false;
		while (detectVerticalCollision(terrain) == true)
		{
			collision = true;
			if (heroySpeed < 0)
			{
				heroySpeed = (int)(heroySpeed + 1);
			}
			else if (heroySpeed > 0)
			{
				heroySpeed = (int)(heroySpeed - 1);
			}
		}
		heroy += heroySpeed;
		if (collision)
			heroySpeed = 0;
	}

	public void up()
	{
		if (onGround)
		{
			finishedJump = false;
			jumpCounter = 0;
			mState = beginJump;
			heroySpeed = 9;
		}
		else if (!finishedJump)
		{
			heroySpeed -= .4;
			if (heroySpeed < -15)
				heroySpeed = -15;
		}
		else
		{
			verticalIdle();
		}
	}
	
	/**
	 * Adjust animation frames and speeds for the character running left
	 */
	public void left()
	{
		if (mState != jumping && mState != beginJump && mState != ducking)
		{
			facing = left;
			mState = running;
			
			if (heroxSpeed > 0)
				heroxSpeed = -.5f;
			else if (heroxSpeed > -4)
				heroxSpeed -= .5f;
		}
		else
		{
			if (heroxSpeed > -4)
				heroxSpeed -= .25f;
		}
	}
	
	/**
	 * Adjust animation frames and speeds for the character running right
	 */
	public void right()
	{
		if (mState != jumping && mState != beginJump && mState != ducking)
		{
			facing = right;
			mState = running;
			
			if (heroxSpeed < 0)
				heroxSpeed = .5f;
			else if (heroxSpeed < 4)
				heroxSpeed += .5f;
		}
		else
		{
			if (heroxSpeed < 4)
				heroxSpeed += .25f;
		}
	}
	
	/**
	 * Adjust idle animation frames and speeds, as well as handle any transitions into the idle state
	 */
	public void horizontalIdle()
	{
		if (mState == jumping || mState == beginJump)
		{
			if (heroxSpeed > 0)
				heroxSpeed -= .05f;
			else if (heroxSpeed < 0)
				heroxSpeed += .05f;
			
			if (heroxSpeed > -.05 && heroxSpeed < .05)
				heroxSpeed = 0;
		}
		else
		{
			if (mState == running)
			{
				mState = endRun;
			}
			else if (mState == endRun)
			{
				if (prevMState != endRun)
				{
					if (frame == 8 || frame == 16)
					{
						mState = motionState.idle;
					}
				}
				else
				{
					if (frame + 1 == 8 || frame + 1 == 16)
					{
						mState = motionState.idle;
					}
				}
			}
			
			if (heroxSpeed > 0)
				heroxSpeed = (int)(heroxSpeed - 1);
			else if (heroxSpeed < 0)
				heroxSpeed = (int)(heroxSpeed + 1);
		}
	}
	
	public void verticalIdle()
	{
		if (!finishedJump)
		{
			finishedJump = true;
			if (heroySpeed > 3)
				heroySpeed = 3;
			finishedJump = true;
		}
		
		if (!onGround)
		{
			heroySpeed -= .4;
			if (heroySpeed < -15)
				heroySpeed = -15;
		}
	}
	
	public boolean onGround(Level terrain)
	{
		onGround = terrain.checkIfStanding(getX() + hitbox[0], getX()-1 + hitbox[1], getY() + hitbox[2] - 1);
		if (onGround && (mState == motionState.jumping || mState == motionState.beginJump))
		{
			mState = motionState.idle;
		}
		return onGround;
	}
	
	public boolean detectVerticalCollision(Level terrain)
	{		
		if (heroySpeed < 0)
			return terrain.detectGroundCollision(getX() + hitbox[0], getX()-1 + hitbox[1], (int)(getY() + hitbox[2] + heroySpeed));			
		else if (heroySpeed > 0)
			return terrain.detectRoofCollision(getX() + hitbox[0], getX()-1 + hitbox[1], (int)(getY() + hitbox[3] + heroySpeed));
		else
			return false;
	}
	
	public boolean detectHorizontalCollision(Level terrain)
	{
		if (heroxSpeed < 0)
			return terrain.detectLeftCollision((int)(getX() + hitbox[0] + heroxSpeed), getY() + hitbox[2], getY() + hitbox[3]);
		else if (heroxSpeed > 0)
			return terrain.detectRightCollision((int)(getX() + hitbox[1] + heroxSpeed), getY() + hitbox[2], getY() + hitbox[3]);
		else
			return false;
	}
	
	public void updateFrame()
	{
		if (mState == running)
		{
			if (prevMState != running)
			{
				frame = 32;
			}
			else
			{
				frame = (frame + 1) % 48;
			}
		}
		else if (mState == endRun)
		{
			if (prevMState != endRun)
			{
				if (frame < 8 || frame >= 32)
					frame = 0;
				else if (frame < 16 && frame >= 8)
					frame = 4;
				else
					frame = 8;
			}
			else
			{
				frame ++;
			}
		}
		else
		{
			frame = 0;
		}
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
		else if (mState == motionState.beginJump)	//TODO: add directions
			return (int)frame/4;
		else if (mState == motionState.jumping)	//TODO: add directions and animation
			return 3;
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
		else if (mState == motionState.beginJump || mState == motionState.jumping)
			return 4;
		else
			return 0;
	}
	
	/**
	 * Render the sprite with the correct animation frame and position
	 */
	public void renderSprite()
	{
		updateFrame();
		
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
