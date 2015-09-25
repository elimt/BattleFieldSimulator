package actor;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import util.Input;
import util.SingletonRandom;
import army.Army;

/**
 * The <i>Orc</i>is a subclass for <i>Actor</i> super class. The <i>Orc</i> creates new characters with derived attributes from the super class. Additional attributes are added to the subclasses: 
 * <i>visibility</i> and <i>replenish</i>
 * 
 * @author Elim Yao Tsiagbey
 * @version Lab Assignment 3: <i>The Hobbit Battlefield Simulator</i>
 */

public class Orc extends Actor{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** This value captures how easily a Hobbit can move without being seen */
	private double visibility;
	
	/** This value captures how easily a Hobbit can move without being seen */
	private double replenish;
	
	private Rectangle avatar;

	/** Upper boundary on <i>visibility</i> attribute, currently:{@value} */
	private static final double MAX_VISIBILITY = 20.0;

	/** Lower boundary on <i>visibility</i> attribute, currently:{@value} */
	private static final double MIN_VISIBILITY = 5.0;
	
	/** Upper boundary on <i>replenish</i> attribute, currently:{@value} */
	private static final double MAX_REPLENISH = 5.0;
	
	/** Upper boundary on <i>replenish</i> attribute, currently:{@value} */
	private static final double MIN_REPLENISH = 1.0;
	
	
	public Orc(Army armyAllegiance) {
		super(armyAllegiance);
		setVisibility(SingletonRandom.instance.getNormalDistribution(MIN_VISIBILITY, MAX_VISIBILITY, 2.0));
		setReplenish(SingletonRandom.instance.getNormalDistribution(MIN_REPLENISH, MAX_REPLENISH, 2.0));
	}
	
	/** @param <i>changeToValue</i> captures a change in value in the field <i>Health</i>  */
	protected void adjustHealth(double changeToValue) { // concrete implementation of abstract method defined in Actor
	    super.health.set(health.get() + changeToValue);
	    }


	/** @param <i>visibility</i> captures a value (within a guaranteed range) in the field <i>visibility</i>  */
	public void setVisibility(double visibility) { 
		if (visibility<MIN_VISIBILITY) 
			visibility = MIN_VISIBILITY; 
		else if (visibility>MAX_VISIBILITY) 
			visibility= MAX_VISIBILITY; 
		this.visibility = visibility; 
	}
	
	/** @return a copy of the <i>double</i> value stored in <i>visibility</i> */
	public double getVisibility() { return visibility; }

	/** @param <i>replenish</i> captures a value (within a guaranteed range) in the field <i>replenish</i>  */
	public void setReplenish(double replenish) { 
		if (replenish<MIN_REPLENISH) 
			replenish = MIN_REPLENISH; 
		else if (replenish>MAX_REPLENISH) 
			replenish= MAX_REPLENISH; 
		this.replenish = replenish; 
	}
	
	/** @return a copy of the <i>double</i> value stored in <i>replenish</i> */
	public double getReplenish() { return replenish; }


	/** sets all <i>Actor</i> fields, guaranteeing values within the specified range. */
	@Override
	public void inputAllFields() {
		super.inputAllFields();
		setVisibility(Input.instance.getDouble(String.format("Visibility:%.1f",visibility), MIN_VISIBILITY,MAX_VISIBILITY));
		setReplenish(Input.instance.getDouble(String.format("Replenish:%.1f",replenish), MIN_REPLENISH,MAX_REPLENISH));
	}


	/** overrides the superclass (<i>Object</i>) version of <i>toString()</i> and provides a textual representation of the <i>Actor</i> object. */
	@Override
	public String toString() {
		return String.format("%s Visibility:%4.1f Replenish:%4.1f",super.toString(),getVisibility(),getReplenish());

	}

	@Override
	public void createAvatar() {
		avatar = new Rectangle(3.0, 10.0, Color.RED);
		
	}

	@Override
	public Node getAvatar() {return avatar;}

	@Override
	protected Point2D findNewLocation(Actor opponent) {
		final double RANGE_OF_MEANDERING = 40.0;
		if (opponent == null) {
			return new Point2D(avatar.getTranslateX()+Math.random()*RANGE_OF_MEANDERING, avatar.getTranslateY()+Math.random()*RANGE_OF_MEANDERING); // opponent is dead so just wander around
		} else {
			
			final double PROPORTION_TO_MOVE = 0.3; // 30%
			double myX = avatar.getTranslateX();
			double myY = avatar.getTranslateY();
			double opponentX = opponent.getAvatar().getTranslateX();
			double opponentY = opponent.getAvatar().getTranslateY();
			double deltaX = myX - opponentX;
			double deltaY = myY - opponentY;
			
			myX -= deltaX*PROPORTION_TO_MOVE;
			myY -= deltaY*PROPORTION_TO_MOVE;
			return new Point2D(myX, myY);
		}
	}
	
	// Explicit implementation of writeObject, but called implicitly as a result of recursive calls to writeObject() based on Serializable interface
	private void writeObject(ObjectOutputStream out) throws IOException {
	 out.writeDouble(getReplenish());     // SimpleDoubleProperty name is NOT serializable, so I do it manually
	 out.writeDouble(getVisibility());     // SimpleDoubleProperty name is NOT serializable, so I do it manually
	 
	 } // end writeObject() to support serialization

	  // Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	 replenish = in.readDouble();
	 visibility = in.readDouble();
	
	 } // end readObject() to support serialization
}
