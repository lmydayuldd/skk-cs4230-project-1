package cs4230.pedestrian.objects;

import java.awt.Color;
import java.awt.Graphics;

import cs4230.pedestrian.math.*;


/**
 * Pedestrian is a Particle that models a moving, real-life Pedestrian.
 * @author Paul
 */
public class Pedestrian extends Particle implements Comparable<Pedestrian> {
	

	public int walkingSteps, totalSteps;
	public double distance;
	
	/**
	 * Constructs a Pedestrian biased with a move probability field
	 * @param moveField - the move probability field
	 */
	public Pedestrian(double[][] moveField) {
		super(moveField);
		move_Increment = new Statistics(random).normalInt(9, 1.5);
		totalSteps = 0;
		walkingSteps = 0;
		distance = 0;
	}
	

	/**
	 * Sets the position AND assuemes it was due to a move
	 */
	public void setPosition(int x, int y) {
		if (this.x!=x || this.y!=y) {
			// faster than square rooting this
			// distance is already in meters
			distance += (this.x!=x && this.y!=y) ? 0.56568542494:0.4;
			walkingSteps++;
		}
		
		super.setPosition(x, y);
	}
		
	/**
	 * Place move request on the cell
	 */
	@Override
	public void requestMove() {
		
		totalSteps++;
		
		moveCount++;
		
		// only update on moves when necessary based on speed
		if(move_Increment <= moveCount) {
			moveCount=0;
		}
		else {
			return;
		}
		
		// Calculate the move direction probabilities
		double[][] tempMove = new double[3][3];
		double average = 0,tempMult;
		int toCount = 0;
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				tempMove[i][j] = moveField[i][j];
				int tempX = x + i - 1;
				int tempY = y + j - 1;
				Cell temp = grid.getCell(tempX, tempY); 
				tempMult = (temp != null) ? temp.getMultiplier():Integer.MIN_VALUE;
				if(tempMult!=Integer.MIN_VALUE) {
					tempMove[i][j] *= tempMult;
					average += tempMove[i][j];
					toCount++;
				}
				else {
					tempMove[i][j] = tempMult;
				}
			}
		}
				
		
		average /= toCount;
		
		double[] chances = new double[9];
		
		// exponentiate movement probabilities about average weight 
		double current = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				tempMove[i][j] = Math.exp(8*(tempMove[i][j]-average));
				current += tempMove[i][j];
				chances[3 * i + j] = current;
			}
		}
		
		
		// normalize cumulative probabilities as we find our move direction
		double move = random.nextDouble() + Double.MIN_VALUE;
		int count = 0;
		while(count < chances.length-1) {
			chances[count] /= chances[8];
			if(move <= chances[count])
				break;
			count++;
		}
		
		// sanity check
		int tempX = x + (count / 3 - 1);
		int tempY = y + (count % 3 - 1);

		if (tempX >= grid.getWidth() || tempY >= grid.getHeight() || tempX < 0 || tempY < 0) {
			return;
		}
		
		// set priority and request move from cell
		priority = chances[count]-((count>0) ? chances[count-1]:0);
		
		Cell toMove = grid.getCell(tempX, tempY);
		
		// reset move counter to move next tick if we desire to move to an occupied space
		if(toMove.isOccupied) {
			moveCount = move_Increment;
		}
		else {
			toMove.enqueuePedestrian(this);
		}
	}

	@Override
	public int compareTo(Pedestrian o) {
		return (int)Math.signum(o.priority - this.priority);
	}

	@Override
	public void draw(Graphics gfx) {
		gfx.setColor(Color.RED);
		gfx.fillOval(x * TILE_PX + 3, y * TILE_PX + 3, TILE_PX - 6, TILE_PX - 6);
	}	
}
