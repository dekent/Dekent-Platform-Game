import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class GameMain {
	
	//Temporary texture object holding the prototype sprite sheet
	private Texture texture;
	
	float SPRITE_PIXEL = 1.0f/512.0f;
	
	int herox = 0;
	int heroy = 0;
	float heroxSpeed = 0;
	float heroySpeed = 0;
	boolean runningLeft = false;
	boolean runningRight = false;
	float frame = 0;
	
	public void gameLoop()
	{
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		//Initialization
		initGL();
		loadTextures();
		
		while(!Display.isCloseRequested())
		{
			herox += heroxSpeed;
			heroy += heroySpeed;
			renderGL();
			
			pollKeyboardInput();
			Display.update();
			Display.sync(60);
		}
		
		Display.destroy();
	}
	
	public void pollKeyboardInput()
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			
			if (!runningLeft)
			{
				runningLeft = true;
				runningRight = false;
				frame = 0;
				heroxSpeed = -1;
			}
			else
			{
				frame = (frame + .25f) % 12;
				if (heroxSpeed > -3)
					heroxSpeed --;
			}
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			if (!runningRight)
			{
				runningLeft = false;
				runningRight = true;
				frame = 0;
				heroxSpeed = 1;
			}
			else
			{
				frame = (frame + .25f) % 12;
				if (heroxSpeed < 3)
					heroxSpeed ++;
			}
		}
		else
		{
			runningLeft = false;
			runningRight = false;
			if (heroxSpeed > 0)
				heroxSpeed --;
			else if (heroxSpeed < 0)
				heroxSpeed ++;
		}
	}
	
	public void initGL()
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	
    	

		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
	}
	
	public void loadTextures()
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("sprites/prototype2.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void renderGL()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		texture.bind();
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		GL11.glPushMatrix();
			GL11.glTranslatef(herox, heroy, 0);
			renderSprite(determineFrameX(), determineFrameY());
		GL11.glPopMatrix();
	}
	
	public int determineFrameX() 
	{
		if (runningLeft || runningRight)
			return (int)frame;
		else
			return 0;
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
	
	public void renderSprite(int x, int y)
	{
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
	
	public static void main(String[] args)
	{
		GameMain gameMain = new GameMain();
		gameMain.gameLoop();
	}
}
