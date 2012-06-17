package terrain;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

public class Level {
	ArrayList<Block> terrainList;
	
	public Level(ArrayList<Block> a)
	{
		terrainList = a;
	}
	
	public void renderTerrain()
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.0f, 0.0f, 0.0f);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(100, 0);
			GL11.glVertex2f(100, 100);
			GL11.glVertex2f(0, 100);
		GL11.glEnd();
		
		for (int i = 0; i < terrainList.size(); i++)
		{
			terrainList.get(i).renderBlock();
		}
	}
	
	public boolean checkIfStanding(int x1, int x2, int y)
	{
		for (int i = 0; i < terrainList.size(); i++)
		{
			if (y <= terrainList.get(i).bounds[2].y && y >= terrainList.get(i).bounds[0].y)
			{
				for (int j = x1; j <= x2; j++)
				{
					if (j >= terrainList.get(i).bounds[3].x && j <= terrainList.get(i).bounds[2].x)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean detectGroundCollision(int x1, int x2, int y)
	{
		for (int i = 0; i < terrainList.size(); i++)
		{
			if (y <= terrainList.get(i).bounds[2].y && y >= terrainList.get(i).bounds[0].y)
			{
				for (int j = x1; j <= x2; j++)
				{
					if (j >= terrainList.get(i).bounds[3].x && j <= terrainList.get(i).bounds[2].x)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean detectRoofCollision(int x1, int x2, int y)
	{
		for (int i = 0; i < terrainList.size(); i++)
		{
			if (y <= terrainList.get(i).bounds[2].y && y >= terrainList.get(i).bounds[0].y)
			{
				for (int j = x1; j <= x2; j++)
				{
					if (j >= terrainList.get(i).bounds[3].x && j <= terrainList.get(i).bounds[2].x)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean detectLeftCollision(int x, int y1, int y2)
	{
		for (int i = 0; i < terrainList.size(); i++)
		{
			if (x <= terrainList.get(i).bounds[1].x && x >= terrainList.get(i).bounds[0].x)
			{
				for (int j = y1; j <= y2; j++)
				{
					if (j >= terrainList.get(i).bounds[1].y && j <= terrainList.get(i).bounds[2].y)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean detectRightCollision(int x, int y1, int y2)
	{
		for (int i = 0; i < terrainList.size(); i++)
		{
			if (x <= terrainList.get(i).bounds[1].x && x >= terrainList.get(i).bounds[0].x)
			{
				for (int j = y1; j <= y2; j++)
				{
					if (j >= terrainList.get(i).bounds[1].y && j <= terrainList.get(i).bounds[2].y)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
