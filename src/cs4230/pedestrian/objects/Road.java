package cs4230.pedestrian.objects;

import java.awt.Color;
import java.awt.Graphics;

import cs4230.pedestrian.math.Statistics;

public class Road extends Cell {

	public Road(int x, int y, double mult) {
		super(x, y, mult);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw(Graphics gfx) {
		int color = (int)(255*Statistics.sigmoid(getMultiplier()));
		Color col = new Color(color, color, color);
		gfx.setColor(col);
		gfx.fillRect(x * TILE_PX + 1, y * TILE_PX + 1, TILE_PX - 1, TILE_PX - 1);
	}

}