package actor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import army.Army;

/**
 * The <i>Orc</i>is a subclass for <i>Actor</i> super class. The <i>Orc</i> creates new characters with derived attributes from the super class. Additional attributes are added to the subclasses: 
 * <i>hasSword</i> and <i>hasShield</i>
 * 
 * @author Elim Yao Tsiagbey
 * @version Lab Assignment 3: <i>The Hobbit Battlefield Simulator</i>
 */

public class Elf extends Actor{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Tells if the player has a Staff or not which influences movement */
	private boolean hasSword;
	/** Tells if the player has a Horse or not which influences movement */
	private boolean hasShield;
	
	private Rectangle avatar;

	public Elf(Army armyAllegiance) {
		super(armyAllegiance);
		sethasSword(getRandomBoolean());
		sethasShield(getRandomBoolean());
	}
	
	/**Assigns random boolean values for <i>hasSword</i> and <i>hasShield</i>  */
	public static boolean getRandomBoolean() {
		return Math.random() < 0.5;
	       
	   }
	
	/** @param <i>changeToValue</i> captures a change in value in the field <i>Health</i>  */
	protected void adjustHealth(double changeToValue) { // concrete implementation of abstract method defined in Actor
	    super.health.set(health.get() + changeToValue);
	    }

	/** @param <i>hasSword</i> captures a value in the field <i>hasSword</i>  */
	public void sethasSword(boolean hasSword)
	{

		this.hasSword = hasSword;

	}

	/** @return a copy of the <i>boolean</i> value stored in <i>hasSwordf</i> */
	public boolean gethasSword(){
		return hasSword;
	}
	
	/** @param <i>hasShield</i> captures a value in the field <i>hasShielde</i>  */
	public void sethasShield(boolean hasShield)
	{ 

		this.hasShield = hasShield;
	}
	/** @return a copy of the <i>boolean</i> value stored in <i>hasShield</i> */
	public boolean gethasShield(){
		return hasShield;
	}

	/** sets all <i>Elf</i> fields, guaranteeing values within the specified range. It also asks the user for the values of certain fields */
	@Override 
	public void inputAllFields() {
		super.inputAllFields();
		Scanner input = new Scanner(System.in);
		System.out.print("Do you have a sword? True or False");
		String hasSwordResponse = input.next();
		if(hasSwordResponse.equalsIgnoreCase("true"))
			hasSword = true;
		else if((hasSwordResponse.equalsIgnoreCase("false")))
			hasSword = false;
		sethasSword(hasSword);
		


		System.out.print("Do you have a shield? True or False");
		String hasShieldResponse = input.next();
		if(hasShieldResponse.equalsIgnoreCase("true"))
			hasShield = true;
		else if(hasShieldResponse.equalsIgnoreCase("false"))
			hasShield = false;	
		sethasShield(hasShield);
		//keyboard.close();
		input.close();
	}


	/** overrides the superclass (<i>Object</i>) version of <i>toString()</i> and provides a textual representation of the <i>Elf</i> object. */
	@Override
	public String toString() {
		return (String.format("%s Has Sword:  %s  has Shield: %s",super.toString(),gethasSword(),gethasShield()));
}

	@Override
	public void createAvatar() {
		avatar = new Rectangle(6.0, 8.0, Color.GREENYELLOW);
		
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
			if(gethasShield() == true || gethasSword() == true){
			
			myX -= deltaX*(PROPORTION_TO_MOVE-0.2);
			myY -= deltaY*(PROPORTION_TO_MOVE-0.2);
			return new Point2D(myX, myY);
			}
			else
			myX -= deltaX*PROPORTION_TO_MOVE;
			myY -= deltaY*PROPORTION_TO_MOVE;
			return new Point2D(myX, myY);
		}	}
	
	// Explicit implementation of writeObject, but called implicitly as a result of recursive calls to writeObject() based on Serializable interface
	private void writeObject(ObjectOutputStream out) throws IOException {
	 out.writeBoolean(gethasShield());     // SimpleDoubleProperty name is NOT serializable, so I do it manually
	 out.writeBoolean(gethasSword()); 
	
	 } // end writeObject() to support serialization

  // Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
	  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	 hasShield = in.readBoolean();
	 hasSword = in.readBoolean();

	 } // end readObject() to support serialization
}
