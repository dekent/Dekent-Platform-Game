package entities;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Hero {
	float SPRITE_PIXEL = 1.0f/512.0f;
	
	private Texture texture;
	
	int herox;
	int heroy;
	float heroxSpeed;
	float heroySpeed;
	boolean runningLeft;
	boolean runningRight;
	boolean facingRight;
	int frame;
	
	enum motionState {
		running, jumping, idle, ducking, beginDuck, beginJump, endRun, endDuck, landing, rolling
	}
	
	enum direction {
		right, left
	}
	
	public Hero(int x, int y, boolean faceRight)
	{
		herox = x;
		heroy = y;
		heroxSpeed = 0;
		heroySpeed = 0;
		runningLeft = false;
		runningRight = false;
		facingRight = faceRight;
		frame = 0;
	}
	
	public void updatePosition()
	{
		herox += (int)heroxSpeed;
		heroy += (int)heroySpeed;
	}
	
	public void runLeft()
	{
		facingRight = false;
		
		if (!runningLeft)
		{
			runningLeft = true;
			runningRight = false;
			frame = 0;
			heroxSpeed = -1;
		}
		else
		{
			frame = (frame + 1) % 48;
			if (heroxSpeed > -3)
				heroxSpeed --;
		}
	}
	
	public void runRight()
	{
		facingRight = true;
		
		if (!runningRight)
		{
			runningLeft = false;
			runningRight = true;
			frame = 0;
			heroxSpeed = 1;
		}
		else
		{
			frame = (frame + 1) % 48;
			if (heroxSpeed < 3)
				heroxSpeed ++;
		}
	}
	
	public void idle()
	{
		runningLeft = false;
		runningRight = false;
		if (heroxSpeed > 0)
			heroxSpeed --;
		else if (heroxSpeed < 0)
			heroxSpeed ++;
	}
	
	public int determineFrameX() 
	{
		if (runningLeft || runningRight)
			return (int)frame/4;
		else if (facingRight)
			return 0;
		else
			return 1;
	}
	
	public int determineFrameY()
	{
		if (runningLeft)
			return 2;
		else if (runningRight)
			return 1;
		else
			return 0;
	}
	
	public void renderSprite()
	{
		int x = determineFrameX();
		int y = determineFrameY();
		
		GL11.glTranslatef(herox, heroy, 0);

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
}
