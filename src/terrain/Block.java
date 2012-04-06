package terrain;

import org.lwjgl.opengl.GL11;

public class Block {
	Coordinate[] bounds = new Coordinate[4];	//[bottomLeft, bottomRight, topRight, topLeft]
	
	public Block(Coordinate a, Coordinate b, Coordinate c, Coordinate d)
	{
		bounds[0] = a;
		bounds[1] = b;
		bounds[2] = c;
		bounds[3] = d;
	}
	
	public void renderBlock()
	{
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(bounds[0].x, bounds[0].y);
			GL11.glVertex2f(bounds[1].x, bounds[1].y);
			GL11.glVertex2f(bounds[2].x, bounds[2].y);
			GL11.glVertex2f(bounds[3].x, bounds[3].y);
		GL11.glEnd();
	}
}
