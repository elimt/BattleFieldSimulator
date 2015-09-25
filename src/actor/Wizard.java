package actor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import army.Army;

/**
 * The <i>Hobbit</i>is a subclass for <i>Actor</i> super class. The <i>Hobbit</i> creates new characters with derived attributes from the super class.
 * 
 * @author Elim Yao Tsiagbey
 * @version Lab Assignment 3: <i>The Hobbit Battlefield Simulator</i>
 */
public class Wizard extends Actor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Tells if the player has a Staff or not which influences movement */
	private boolean hasStaff;
	/** Tells if the player has a Horse or not which influences movement */
	private boolean hasHorse;

	private ImageView avatar;

	public Wizard(Army armyAllegiance) {
		super(armyAllegiance);
		sethasHorse(getRandomBoolean());
		sethasStaff(getRandomBoolean());
	}

	public static boolean getRandomBoolean() {
		return Math.random() < 0.5;

	}

	/** @param <i>changeToValue</i> captures a change in value in the field <i>Health</i> */
	protected void adjustHealth(double changeToValue) { // concrete implementation of abstract method defined in Actor
		super.health.set(health.get() + changeToValue);
	}

	/** @param <i>hasStaff</i> captures a value in the field <i>hasStaff</i> */
	public void sethasStaff(boolean staff) {

		this.hasStaff = staff;

	}

	/** @return a copy of the <i>boolean</i> value stored in <i>hasStaff</i> */
	public boolean gethasStaff() {
		return hasStaff;
	}

	/** @param <i>hasHorse</i> captures a value in the field <i>hasHorse</i> */
	public void sethasHorse(boolean horse) {

		this.hasHorse = horse;
	}

	/** @return a copy of the <i>boolean</i> value stored in <i>hasHorse</i> */
	public boolean gethasHorse() {
		return hasHorse;
	}

	/** sets all <i>Wizard</i> fields, guaranteeing values within the specified range. It also asks the user for the values of certain fields */
	@Override
	public void inputAllFields() {
		super.inputAllFields();
		Scanner input = new Scanner(System.in);
		System.out.print("Do you have a horse? True or False");
		String hasHorseResponse = input.next();
		if (hasHorseResponse.equalsIgnoreCase("true"))
			hasHorse = true;
		else if ((hasHorseResponse.equalsIgnoreCase("false")))
			hasHorse = false;
		sethasHorse(hasHorse);

		System.out.print("Do you have a staff? True or False");
		String hasStaffResponse = input.next();
		if (hasStaffResponse.equalsIgnoreCase("true"))
			hasStaff = true;
		else if (hasStaffResponse.equalsIgnoreCase("false"))
			hasStaff = false;
		sethasStaff(hasStaff);
		input.close();

	}

	/** overrides the superclass (<i>Object</i>) version of <i>toString()</i> and provides a textual representation of the <i>Wizard</i> object. */
	// @Override
	public String toString() {
		return (String.format("%s Has Staff: %s has Horse: %s", super.toString(), gethasStaff(), gethasHorse()));

	}

	@Override
	public void createAvatar() {
		try {
			avatar = new ImageView(new Image(new FileInputStream("AnimatedWizard-1.gif")));
			avatar.setFitWidth(30.0);
			avatar.setPreserveRatio(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}

	}

	@Override
	public Node getAvatar() {
		return avatar;
	}

	@Override
	protected Point2D findNewLocation(Actor opponent) {
		final double RANGE_OF_MEANDERING = 40.0;
		if (opponent == null) {
			return new Point2D(avatar.getTranslateX() + Math.random() * RANGE_OF_MEANDERING, avatar.getTranslateY() + Math.random() * RANGE_OF_MEANDERING); // opponent is dead so just wander around
		} else {

			final double PROPORTION_TO_MOVE = 0.3; // 30%
			double myX = avatar.getTranslateX();
			double myY = avatar.getTranslateY();
			double opponentX = opponent.getAvatar().getTranslateX();
			double opponentY = opponent.getAvatar().getTranslateY();
			double deltaX = myX - opponentX;
			double deltaY = myY - opponentY;
			if (gethasHorse() == true || gethasStaff() == true) {

				myX -= deltaX * (PROPORTION_TO_MOVE + 0.3);
				myY -= deltaY * (PROPORTION_TO_MOVE + 0.3);
				return new Point2D(myX, myY);
			} else
				myX -= deltaX * PROPORTION_TO_MOVE;
			myY -= deltaY * PROPORTION_TO_MOVE;
			return new Point2D(myX, myY);
		}
		// return new Point2D(opponent.getAvatar().getTranslateX(), opponent.getAvatar().getTranslateY());
	}

	// Explicit implementation of writeObject, but called implicitly as a result of recursive calls to writeObject() based on Serializable interface
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeBoolean(gethasStaff()); // SimpleDoubleProperty name is NOT serializable, so I do it manually
		out.writeBoolean(gethasHorse());

	} // end writeObject() to support serialization

	// Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		hasStaff = in.readBoolean();
		hasHorse = in.readBoolean();

	} // end readObject() to support serialization
}
