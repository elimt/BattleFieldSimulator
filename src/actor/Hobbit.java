package actor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import army.Army;
import util.Input;
import util.SingletonRandom;



/**
 * The <i>Hobbit</i>is a subclass for <i>Actor</i> super class. The <i>Hobbit</i> creates new characters with derived attributes from the super class. An Additional attribute is added to the subclasses: <i>stealth</i>
 * 
 * @author Elim Yao Tsiagbey
 * @version Lab Assignment 3: <i>The Hobbit Battlefield Simulator</i>
 */

public class Hobbit extends Actor{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** This value captures how easily a Hobbit can move without being seen */
	private double stealth;

	/** Upper boundary on <i>stealth</i> attribute, currently:{@value} */
	public static final double MAX_STEALTH = 50.0;

	/** Lower boundary on <i>stealth</i> attribute, currently:{@value} */
	public static final double MIN_STEALTH = 0.0;
	
	private Circle avatar;
	
	
	public Hobbit(Army armyAllegiance) {
		super(armyAllegiance);
		setStealth(SingletonRandom.instance.getNormalDistribution(MIN_STEALTH, MAX_STEALTH, 2.0));
	}
	
	/** @param <i>changeToValue</i> captures a change in value in the field <i>Health</i>  */
	protected void adjustHealth(double changeToValue) { // concrete implementation of abstract method defined in Actor
	    super.health.set(health.get() + changeToValue);
	    }


	/** @param <i>stealth</i> captures a value (within a guaranteed range) in the field <i>stealth</i>  */
	public void setStealth(double stealth) { 
		if (stealth<MIN_STEALTH) 
			stealth = MIN_STEALTH; 
		else if (stealth>MAX_STEALTH) 
			stealth= MAX_STEALTH; 
		this.stealth = stealth; 
	}


	/** @return a copy of the <i>double</i> value stored in <i>stealth</i> */
	public double getStealth() { return stealth; }


	/** sets all <i>Actor</i> fields, guaranteeing values within the specified range. */
	@Override
	public void inputAllFields() {
		super.inputAllFields();
		//setStealth(Input.instance.getDouble("Stealth: ",MIN_STEALTH,MAX_STEALTH));
		setStealth(Input.instance.getDouble(String.format("Stealth:%.1f",stealth), MIN_STEALTH,MAX_STEALTH));
	}


	/** overrides the superclass (<i>Object</i>) version of <i>toString()</i> and provides a textual representation of the <i>Actor</i> object. */
	@Override
	public String toString() {
		return String.format("%s Stealth:%4.1f",super.toString(),getStealth());

	}

	@Override
	public void createAvatar() {
		avatar = new Circle(5.0, Color.AQUAMARINE);
		
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
			
			myX += deltaX*PROPORTION_TO_MOVE;
			myY += deltaY*PROPORTION_TO_MOVE;
			return new Point2D(myX, myY);
		}
	}
	
	
	// Explicit implementation of writeObject, but called implicitly as a result of recursive calls to writeObject() based on Serializable interface
	private void writeObject(ObjectOutputStream out) throws IOException {
		 out.writeDouble(getStealth());     // SimpleDoubleProperty name is NOT serializable, so I do it manually
		 
		 } // end writeObject() to support serialization

		  // Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
		  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		 stealth = in.readDouble();
		
		 } // end readObject() to support serialization
	
	
	
	}

