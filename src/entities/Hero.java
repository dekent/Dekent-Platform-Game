package entities;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

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
	int frame;
	
	public Hero(int x, int y, direction dir)
	{
		herox = x;
		heroy = y;
		heroxSpeed = 0;
		heroySpeed = 0;
		mState = motionState.idle;
		facing = dir;
		frame = 0;
	}
	
	public void updatePosition()
	{
		herox += heroxSpeed;
		heroy += heroySpeed;
	}
	
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
	
	public void renderSprite()
	{
		int x = determineFrameX();
		int y = determineFrameY();
		
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
	
	public void loadTexture()
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("sprites/prototype2.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public int getX()
	{
		return (int)herox;
	}
	
	public int getY()
	{
		return (int)heroy;
	}
	
	public void setX(int x)
	{
		herox = x;
	}
	
	public void setY(int y)
	{
		heroy = y;
	}
}
