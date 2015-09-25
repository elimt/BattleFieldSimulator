package actor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import army.Army;
import util.Input;
import util.SingletonRandom;

/**
 * The <i>Actor</i> class is the super class for all different types of <i>Actor</i> subclasses. The <i>Actor</i> class tracks state information for individual actors in the simulation: <i>name</i>,
 * <i>health</i>, <i>strength</i>, <i>speed</i>, etc (and later, a screen avatar with coordinates). Additional attributes are tracked in the subclasses. The behaviours (moving and battling) are
 * defined in the subclasses. <i>Actor</i> class is currently a concrete class. Later, the <i>Actor</i> class will become an <i>abstract</i> class where no <i>Actor</i> objects will ever be created --
 * only subclass objects.
 * 
 * @author Elim Yao Tsiagbey and Rex Woollard
 * @version Lab Assignment 3: <i>The Hobbit Battlefield Simulator</i>
 */
public abstract class Actor implements Serializable 
{ 	
	// Could also be written as " public class Actor extends Object { " but no one ever does . . . why waste time typing something that isn't																							// required.
	// Series of constants which are common to all objects. No instances of these values reside in any Actor objects.
	// The keyword "static" makes a single item (such as MAX_STRENGTH) common to all Actor objects. The keyword "final" makes an item constant.

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * static variable used to embed sequence number in <i>Actor</i> names; because it is static, there is one-and-only-one instance of this variable regardless of the number of Actor objects in
	 * existence (from none to infinity).
	 */
	private static int actorSerialNumber = 0;

	// INSTANCE FIELDS: Each Actor object will have its own independent set of instance fields
	/** stores the actual Actor's name, for example, "<i>Gandalf the Gray</i>" */
	private SimpleStringProperty name = new SimpleStringProperty();

	/** captures a reference-to-String value stored in local, stack-oriented local variable <i>name</i>, stores in heap-oriented instance variable <i>this.name</i> */
	public void setName(String name) 
	{
		this.name.set(name);
	}

	/** @return a copy of the reference-to-String value stored in <i>name</i>; safe to return because String is immutable */
	public String getName() 
	{
		return name.get();
	}

	protected Army armyAllegiance;
	private TranslateTransition tt;
	private Tooltip tooltip;

	/** Upper boundary on <i>strength</i> attribute, currently:{@value} */
	public static final double MAX_STRENGTH = 100.0; // the use of the JavaDoc tag {@value} causes the constant value to be included in the documentation
	/** Lower boundary on <i>strength</i> attribute, currently:{@value} */
	public static final double MIN_STRENGTH = 10.0; // the use of the JavaDoc tag {@value} causes the constant value to be included in the documentation
	/** influences degree of damage inflicted in skirmish with other players */
	private SimpleDoubleProperty strength = new SimpleDoubleProperty();

	/**
	 * @param <i>strength</i> captures a value (within a guaranteed range) in the field <i>strength</i>
	 * 
	 * */

	public void setStrength(double strength) 
	{
		if (strength < MIN_STRENGTH)
			strength = MIN_STRENGTH;
		else if (strength > MAX_STRENGTH)
			strength = MAX_STRENGTH;
		this.strength.set(strength);
	}

	/** @return a copy of the <i>double</i> value stored in <i>strength</i> */
	public double getStrength() 
	{
		return strength.get();
	}

	/** Upper boundary on <i>health</i> attribute, currently:{@value} */
	public static final double MAX_HEALTH = 100.0;
	/** Lower boundary on <i>health</i> attribute, currently:{@value} */
	public static final double MIN_HEALTH = 1.0;
	/** Defines threshold for ability to move; value is 0.0 to 1.0, currently:{@value} */
	private static final double THRESHOLD_OF_ADEQUATE_HEALTH = 0.3; // effectively 30%
	/** influences ability to survive damage inflicted in skirmish with other players; can also influence mobility (along with speed) */
	protected SimpleDoubleProperty health = new SimpleDoubleProperty();

	/** @param <i>health</i> captures a value (within a guaranteed range) in the field <i>health</i> */
	public void setHealth(double health) 
	{
		if (health < MIN_HEALTH)
			health = MIN_HEALTH;
		else if (health > MAX_HEALTH)
			health = MAX_HEALTH;
		this.health.set(health);
	}

	/** applies <i>changeToValue</i> to the <i>health</i> attribute. */
	// public void adjustHealth(double changeToValue) { stealth.set(stealth.get() + changeToValue); }
	protected abstract void adjustHealth(double changeToValue);

	/** @return a copy of the <i>double</i> value stored in <i>health</i> */
	public double getHealth() 
	{
		return health.get();
	}

	/** Upper boundary on <i>speed</i> attribute, currently:{@value} */
	public static final double MAX_SPEED = 100.0;
	/** Lower boundary on <i>speed</i> attribute, currently:{@value} */
	public static final double MIN_SPEED = 10.0;
	/** influences speed of movement */
	private SimpleDoubleProperty speed = new SimpleDoubleProperty();

	/** @param <i>speed</i>captures a value (within a guaranteed range) in the field <i>speed</i> */
	public void setSpeed(double speed) 
	{
		if (speed < MIN_SPEED)
			speed = MIN_SPEED;
		else if (speed > MAX_SPEED)
			speed = MAX_SPEED;
		this.speed.set(speed);
	} // end setSpeed()

	/** @return a copy of the <i>double</i> value stored in <i>speed</i> */
	public double getSpeed()
	{
		return speed.get();
	}

	/**
	 * <i>Actor</i> constructor is used when building <i>Actor</i> objects automatically: <i>strength</i>, <i>health</i>, <i>speed</i> fields are given randomly generated values within their range;
	 * <i>name</i> is given a sequentially numbered name: <i>Auto:<b>n</b></i> where <i><b>n</b></i> is the sequence number. The <i>name</i> can be edited to create an unique <i>Actor</i>.
	 */
	public Actor() 
	{
		++actorSerialNumber; // static class-oriented variable. There is one-and-only-one instance of this variable regardless of the number of Actor objects in existence (from none to infinity).
		// setName("Actor:" + actorSerialNumber);
		setName(String.format("%d:%s", actorSerialNumber, getClass().getSimpleName())); // An alternate way to assemble a String to use as a name. Because of polymorphism "getClass().getName()" will
																																										// return the subclass name when they exist.
		setStrength(SingletonRandom.instance.getNormalDistribution(MIN_STRENGTH, MAX_STRENGTH, 2.0));
		setHealth(SingletonRandom.instance.getNormalDistribution(MIN_HEALTH, MAX_HEALTH, 2.0));
		setSpeed(SingletonRandom.instance.getNormalDistribution(MIN_SPEED, MAX_SPEED, 2.0));
		createAvatar();
		tt = new TranslateTransition();
		tt.setNode(getAvatar());

	} // end Actor constructor

	public Actor(Army armyAllegiance)
	{
		this();
		this.armyAllegiance = armyAllegiance;
	}

	public void setArmyAllegiance(Army army)
	{
		armyAllegiance = army;

	}

	public Army getAllegiance() 
	{
		return armyAllegiance;
	}

	/**
	 * sets all <i>Actor</i> fields, guaranteeing values within the specified range. Later, it will be treated as a virtual method, and subclasses will call this (the superclass method) to perform its
	 * work.
	 */
	public void inputAllFields() 
	{
		setName(Input.instance.getString(getClass().getSimpleName() + ":Current Name:" + name + " New Name:"));
		setStrength(Input.instance.getDouble(String.format("Strength:%.1f", strength), MIN_STRENGTH, MAX_STRENGTH));
		setHealth(Input.instance.getDouble(String.format("Health:%.1f", health), MIN_HEALTH, MAX_HEALTH));
		setSpeed(Input.instance.getDouble(String.format("Speed:%.1f", speed), MIN_SPEED, MAX_SPEED));
	} // end void inputAllFields()

	/** <i>Actor</i> regain health on each cycle of the simulation (and loose health in battles handled by other code). */
	public void gameCycleHealthGain() 
	{
		final double MAX_CYCLE_HEALTH_GAIN = 2.0;
		adjustHealth(Math.random() * MAX_CYCLE_HEALTH_GAIN);
	}

	public double getHitPoints()
	{
		return getStrength() + getHealth() * .5 * Math.random();
	}

	/**
	 * processes a single round of combat between two Actor objects: the <b>attacker</b> is this object; the <b>defender</b> is received as an argument. This method is called by the <b>attacker</b>
	 * <i>Actor</i> object. This <b>attacker</b> <i>Actor</i> object chooses another <i>Actor</i> object as the <b>defender</b> by sending a reference-to the second <i>Actor</i> object. When program
	 * execution arrives in <i>combatRound</i>, the method will have access to 2 sets of <i>Actor</i> attributes (a.k.a. instance fields). In particular, this method will need to use <i>health</i> and
	 * <i>strength</i> to process a single round of combat. As an outcome of the single round, both <i>Actor</i> objects: the <b>attacker</b> and the <b>defender</b> are likely to loose some
	 * <i>health</i> value, but the <i>Actor</i> object with less <i>strength</i> will likely incur more damage to their <i>health</i>. You access the <b>attacker</b> instance fields (such as
	 * <i>health</i> using <i>this.health</i> and the <b>defender</b> instance fields using <i>defender.health</i>. Of course, <i>defender</i> is the name of the stack-oriented reference-to variable
	 * that is sent to the method. A portion of the code might look like this:
	 * 
	 * <pre>
	 * {@code
	 * final double LOSS_OF_HEALTH_FOR_WINNER = 1.0;
	 * final double LOSS_OF_HEALTH_FOR_LOOSER = 4.0;
	 * final double RANDOM_VARIATION_RANGE = 10.0;
	 * if (this.strength+Math.random()*RANDOM_VARIATION_RANGE > defender.strength+Math.random()*RANDOM_VARIATION_RANGE) {
	 *   this.health -= LOSS_OF_HEALTH_FOR_WINNER;
	 *   defender.health -= LOSS_OF_HEALTH_FOR_LOOSER;
	 * } else {
	 *   this.health -= LOSS_OF_HEALTH_FOR_LOOSER;
	 *   defender.health -= LOSS_OF_HEALTH_FOR_WINNER;
	 * }
	 * </pre>
	 * 
	 * Of course, the preceding code snippet is only a simple limited fragment. There are other issues you might want to explore in deciding how to process a single round of combat.
	 * 
	 * @param <i>defender</i> a reference to a different <i>Actor</i> object that will engage in combat with this <i>Actor</i> object.
	 * @return <i>health</i> of the <i>Actor</i> following the combat round.
	 * */
	public double combatRound(Actor defender) 
	{
		final double MAX_COMBAT_HEALTH_REDUCTION_OF_LOOSER = 10.0; // health ranges 0.0 to 100.0, thus could loose 0.0 to 10.0
		final double MAX_COMBAT_HEALTH_REDUCTION_OF_WINNER = 3.0; // could loose 0.0 to 3.0
		double healthAdjustmentOfLooser = -(Math.random() * MAX_COMBAT_HEALTH_REDUCTION_OF_LOOSER) - 1.0; // looser looses at least 1.0
		double healthAdjustmentOfWinner = -(Math.random() * MAX_COMBAT_HEALTH_REDUCTION_OF_WINNER) + 1.0; // winner gains at least 1.0

		double proportionHitPoints = getHitPoints() / (getHitPoints() + defender.getHitPoints()); // between 0.0 and 1.0
		if (Math.random() > proportionHitPoints) {
			adjustHealth(healthAdjustmentOfLooser);
			defender.adjustHealth(healthAdjustmentOfWinner);
		} 
		else 
		{
			defender.adjustHealth(healthAdjustmentOfLooser);
			adjustHealth(healthAdjustmentOfWinner);
		}
		return getHealth();
	} // end combatRound()

	/**
	 * based on <i>health</i> determines if the <i>Actor</i> is healthy enough to move; returns a <i>true</i> or <i>false</i> value
	 * 
	 * @return boolean value representing whether the <i>Actor</i> can move.
	 * */
	public boolean isHealthyEnoughToMove()
	{
		return (getHealth() > MAX_HEALTH * THRESHOLD_OF_ADEQUATE_HEALTH);
	} // expression results in a boolean value

	/** overrides the superclass (<i>Object</i>) version of <i>toString()</i> and provides a textual representation of the <i>Actor</i> object. */
	@Override
	public String toString() 
	{
		return String.format("Name:%-10s Health:%4.1f Strength:%4.1f Speed:%4.1f", getName(), getHealth(), getStrength(), getSpeed());
		// return name + " Strength:" + strength + " Speed:"+ speed + " Health:" + health; // it works but its BAD FORM. Many extra String objects are created and thrown away. No field width control.
	} // end String toString()

	public abstract void createAvatar();
	public abstract Node getAvatar();

	/**
	 * Defines the characteristics of a <i>TranslateTransition</i>. Each call results in ONE segment of motion. When that segment is finished, it "chains" another call to <i>startMotion()</i> (which is
	 * NOT recursion)! The initial call is made by the managing <i>Army</i> object; subsequent calls are made through the "chaining" process described here.
	 * 
	 * @param engageInCombat
	 *          TODO
	 */
	public void startMotion(boolean engageInCombat) 
	{
		Army opposingArmy = armyAllegiance.getOpposingArmy();
		Actor opponent = opposingArmy.findNearestOpponent(this);
		final double DISTANCE_WHERE_WE_ENGAGE_IN_COMBAT = 50.0;
		if (opponent != null) 
		{
			if (engageInCombat && distanceTo(opponent) < DISTANCE_WHERE_WE_ENGAGE_IN_COMBAT) 
			{
				combatRound(opponent);
				if (getHealth() <= 0.0)
				{
					armyAllegiance.removeNowDeadActor(this);
					return; // I am now dead, thus, I cannot move . . . having been removed
				}
	
				if (opponent.getHealth() <= 0.0)
				{
					opponent.armyAllegiance.removeNowDeadActor(opponent);
					opponent = null;
				}
			}

			Point2D newLocation = armyAllegiance.validateCoordinate(findNewLocation(opponent)); // opponent MIGHT be null
			// *************************** End Your work for findNewLocation() *******************************
			if (tt.getStatus() != Animation.Status.RUNNING)
			{
				// *************************** Start Your work for to use newLocation *******************************
				tt.setToX(newLocation.getX());
				tt.setToY(newLocation.getY());
				// *************************** End Your work for to use newLocation *******************************
				// tt.setDuration(Duration.seconds(Math.random()*5.0+1.0));
				tt.setDuration(Duration.seconds((MAX_SPEED + MIN_SPEED) * 1.0 / (getSpeed() * armyAllegiance.getSpeedControllerValue())));
				// tt.setDuration(Duration.seconds(MAX_SPEED/getSpeed()*5.0*armyAllegiance.getSpeedControllerValue()));
				// tt.setDelay(Duration.seconds(100.0/getHealth()));
				tt.setOnFinished(event -> startMotion(true)); // NOT RECURSION!!!!
				tt.play(); // give assembled object to the render engine.
				// Of course, play() is an object-oriented method which has access to "this" inside,

			}// and it can use "this" to give to the render engine.
		} // end if (RUNNING)
	} // end startMotion()


	protected abstract Point2D findNewLocation(Actor opponent);

	public void pauseMotion() 
	{
		if (tt != null && tt.getStatus() == Animation.Status.RUNNING)
			tt.pause();
	}

	/** createTable is static to allow Army to define a table without having any Actor objects present. */
	public static TableView<Actor> createTable() 
	{
		TableView<Actor> table = new TableView<Actor>();
		final double PREF_WIDTH_DOUBLE = 50.0;
		table.setPrefWidth(PREF_WIDTH_DOUBLE * 7.5); // 7.0 because there are 6 individual columns, but one of those is DOUBLE-WIDTH, and there is some inter-column spacing
		table.setEditable(true);

		TableColumn<Actor, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<Actor, String>("name"));
		nameCol.setPrefWidth(PREF_WIDTH_DOUBLE * 2.0);
		TableColumn<Actor, Number> healthCol = new TableColumn<>("Health");
		healthCol.setCellValueFactory(cell -> cell.getValue().health);
		healthCol.setPrefWidth(PREF_WIDTH_DOUBLE);
		TableColumn<Actor, Number> strengthCol = new TableColumn<>("Strength");
		strengthCol.setCellValueFactory(cell -> cell.getValue().strength);
		strengthCol.setPrefWidth(PREF_WIDTH_DOUBLE);
		TableColumn<Actor, Number> speedCol = new TableColumn<>("Speed");
		speedCol.setCellValueFactory(cell -> cell.getValue().speed);
		speedCol.setPrefWidth(PREF_WIDTH_DOUBLE);
		TableColumn<Actor, Number> locationXCol = new TableColumn<>("X");
		locationXCol.setCellValueFactory(cell -> cell.getValue().getAvatar().translateXProperty());
		locationXCol.setPrefWidth(PREF_WIDTH_DOUBLE);
		TableColumn<Actor, Number> locationYCol = new TableColumn<>("Y");
		locationYCol.setCellValueFactory(cell -> cell.getValue().getAvatar().translateYProperty());
		locationYCol.setPrefWidth(PREF_WIDTH_DOUBLE);
		ObservableList<TableColumn<Actor, ?>> c = table.getColumns();
		c.add(nameCol);
		c.add(healthCol);
		c.add(strengthCol);
		c.add(speedCol);
		c.add(locationXCol);
		c.add(locationYCol);
		// Compare line ABOVE with line BELOW: The BELOW line looks cleaner and does actually work . . . but the compiler spits out a warning. The ABOVE line accomplishes the same thing, less elegantly, but without warnings.
		// table.getColumns().addAll(nameCol, healthCol, strengthCol, speedCol, locationXCol, locationYCol);

		// The following code makes each cell in the selected columns editable (Name, Health, Strength, Speed)
		// We CANNOT implement edit capabilities on the X/Y columns since they are READ-ONLY.
		nameCol.setCellFactory(TextFieldTableCell.<Actor> forTableColumn());
		nameCol.setOnEditCommit(event ->
		{
			Actor a = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
			a.setName(event.getNewValue());
			a.resetAvatarAttribute();
		});

		healthCol.setCellFactory(TextFieldTableCell.<Actor, Number> forTableColumn(new NumberStringConverter()));
		healthCol.setOnEditCommit(event -> 
		{
			Actor a = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
			a.setHealth((Double) event.getNewValue());
			a.resetAvatarAttribute();
		});

		strengthCol.setCellFactory(TextFieldTableCell.<Actor, Number> forTableColumn(new NumberStringConverter()));
		strengthCol.setOnEditCommit(event -> 
		{
			Actor a = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
			a.setStrength((Double) event.getNewValue());
			a.resetAvatarAttribute();
		});

		speedCol.setCellFactory(TextFieldTableCell.<Actor, Number> forTableColumn(new NumberStringConverter()));
		speedCol.setOnEditCommit(event ->
		{
			Actor a = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
			a.setSpeed((Double) event.getNewValue());
			a.resetAvatarAttribute();
		});

		return table;
	} // end createTable()

	public void resetAvatarAttribute() 
	{
		Tooltip.install(getAvatar(), new Tooltip(toString()));
		Tooltip tooltip = new Tooltip();
		Node avatar = getAvatar();
		avatar.setScaleX(getHealth() / ((MAX_HEALTH + MIN_HEALTH) / 2.0));
		avatar.setScaleY(getHealth() / ((MAX_HEALTH + MIN_HEALTH) / 2.0));
		tooltip.setText(toString());// Note: This updates the text in the Tooltip that was installed earlier. We re-use the originally installed Tooltip.

	}

	// Explicit implementation of writeObject, but called implicitly as a result of recursive calls to writeObject() based on Serializable interface
	private void writeObject(ObjectOutputStream out) throws IOException 
	{
		out.writeObject(getName()); // SimpleDoubleProperty name is NOT serializable, so I do it manually
		out.writeDouble(getStrength()); // SimpleDoubleProperty strength is NOT serializable, so I do it manually
		out.writeDouble(getHealth()); // SimpleDoubleProperty health is NOT serializable, so I do it manually
		out.writeDouble(getSpeed()); // SimpleDoubleProperty speed is NOT serializable, so I do it manually
		out.writeDouble(getAvatar().getTranslateX()); // Node battlefieldAvatar is NOT serializable. It's TOO BIG anyway, so I extract the elements that I need (here, translateX property) to retain
																									// manually.
		out.writeDouble(getAvatar().getTranslateY()); // Node battlefieldAvatar
		// is NOT serializable.
		// It's TOO BIG anyway,
		// so I extract the
		// elements that I need
		// (here, translateY
		// property) to retain
		// manually.
	} // end writeObject() to support serialization

	// Explicit implementation of readObject, but called implicitly as a result
	// of recursive calls to readObject() based on Serializable interface
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		name = new SimpleStringProperty((String) in.readObject());
		strength = new SimpleDoubleProperty(in.readDouble());
		health = new SimpleDoubleProperty(in.readDouble());
		speed = new SimpleDoubleProperty(in.readDouble());
		createAvatar();
		getAvatar().setTranslateX(in.readDouble());
		getAvatar().setTranslateY(in.readDouble());
		tooltip = new Tooltip(toString());
		Tooltip.install(getAvatar(), tooltip);
		resetAvatarAttribute();

		tt = new TranslateTransition();
		tt.setNode(getAvatar());
	} // end readObject() to support serialization

	public double distanceTo(Actor current) 
	{
		double myX = getAvatar().getTranslateX();
		double myY = getAvatar().getTranslateY();

		double currentX = current.getAvatar().getTranslateX();
		double currentY = current.getAvatar().getTranslateY();

		double deltaX = myX - currentX;
		double deltaY = myY - currentY;

		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

} // end class Actor
