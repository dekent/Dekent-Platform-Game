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

import entities.Hero;
import entities.Hero.direction;

public class GameMain {
	
	Hero hero;
	
	/**
	 * Main game loop, this is where everything happens
	 */
	public void gameLoop()
	{		
		//Initialization
		initGL();
		initEntities();
		loadTextures();
		
		//Main game loop
		while(!Display.isCloseRequested())	//exits when window is closed
		{
			hero.updatePosition();
			detectTerrainCollision();
			
			renderGL();
			
			pollKeyboardInput();
			
			Display.update();
			Display.sync(60);
		}
		
		//Exit
		Display.destroy();
	}
	
	/**
	 * Poll for input on the keyboard
	 */
	public void pollKeyboardInput()
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			hero.runLeft();
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			hero.runRight();
		}
		else
		{
			hero.idle();
		}
	}
	
	/**
	 * Collision detection between the hero and the stage
	 */
	public void detectTerrainCollision()
	{
		if (hero.getX() < 0)
		{
			hero.setX(0);
		}
		else if (hero.getX() > 800 - 64)
		{
			hero.setX(800 - 64);
		}
		
		if (hero.getY() < 0)
		{
			hero.setY(0);
		}
	}
	
	/**
	 * Initialize all entities (currently just the hero)
	 */
	public void initEntities()
	{
		hero = new Hero(0, 0, direction.right);
	}
	
	/**
	 * Initialize the game window and all OpenGL-related setup
	 */
	public void initGL()
	{
		//Create game window
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		//Enable texures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		//Allow transparent colors in textures
		GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	
    	//Setup 800 by 600 window
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		//Disable 3D effects
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
	}
	
	/**
	 * Load the textures for all entities (will eventually include background and stage textures
	 */
	public void loadTextures()
	{
		hero.loadTexture();
	}
	
	/**
	 * Render any graphics (currently just the hero)
	 */
	public void renderGL()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		GL11.glPushMatrix();
			hero.renderSprite();
		GL11.glPopMatrix();
	}
	
	/**
	 * Main class, simply constructs and runs the game
	 * 
	 * @param args the usual...
	 */
	public static void main(String[] args)
	{
		GameMain gameMain = new GameMain();
		gameMain.gameLoop();
	}
}
