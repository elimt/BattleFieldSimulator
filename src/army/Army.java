package army;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import simulator.Simulator;
import actor.*;

/**
 * The <i>Army</i> class stores all the objects and shows which side they belong to(either the Forces of Darkness or the Forces of Light).
 * It populates the various armies and also enables the user to edit the values of the fields in the various armies.
 * @author Elim Yao Tsiagbey and Rex Woollard
 * @version Lab Assignment 3: <i>The Hobbit Battlefield Simulator</i>
 */
public class Army{
	/**
	 * 
	 */
	
	private String name;
	private Simulator simulator;
	private Color color;
	private DropShadow dropshadow;
	private Army opposingArmy;
	private DropShadow dropShadow;
	public static final String FONT_NAME = "Copperplate Gothic Bold";
	private static final Font NOTIFICATION_FONT_SMALL = new Font(FONT_NAME, 14.0);
	private static final Font NOTIFICATION_FONT_LARGE = new Font(FONT_NAME, 36.0);
	
	/** Creates an Array using ArrayList to store objects*/
	private ObservableList<Actor> collectionActors = FXCollections.observableList(new ArrayList< >());
	
	
	public ObservableList<Actor> getObservableListActors() {return FXCollections.unmodifiableObservableList(collectionActors); }
	
	public Army(String name, Simulator simulator, Color color) {
		this.name = name;
		this.simulator = simulator;
		this.color = color;
		dropshadow = new DropShadow(10.0, color);
	}
	
	
	/**  @param <i>type</i> and <i>numToAdd</i> captures both the 
	 * object type and the number of that object to create respectively  */
	public void populate(ActorFactory.Type type, int numToAdd) {
		for (int i=0; i<numToAdd; ++i) {
			Actor actor = type.create(this);
			Node avatar = actor.getAvatar();
			simulator.getChildren().add(avatar);
			avatar.setTranslateX(avatar.getScene().getWidth()*Math.random());
			avatar.setTranslateY(avatar.getScene().getHeight()*Math.random());
			avatar.setEffect(dropshadow);
			actor.resetAvatarAttribute();
			collectionActors.add(actor); // send "this" so that Actor object can capture its allegiance
		} // end for
		
	}
	
	/** Enables the user to edit the object depending on the select object
	 *  @param <i>indexOfActorToEdit</i>captures a object number in the array list to edit  */
	public void edit(int indexOfActorToEdit){
		Actor myActor = collectionActors.get(indexOfActorToEdit);
		myActor.inputAllFields();
	}
	
	/**Tells the size of the array
	 * @return int value representing size of the array*/
	public int size()
	  {
	    return this.collectionActors.size();
	  }
	
	
	/**Displays the values of the Objects */
	public void display(){
		System.out.println(name);
		for (Actor current : collectionActors){
			System.out.println(current);
		}
		
	}


	public void startMotion() {
		for(Actor actor : collectionActors)
			actor.startMotion(false);
	}


	public void suspendMotion() {
		for(Actor actor : collectionActors)
			actor.pauseMotion();
		
	}

	public String getName() {return name;}

	public Node getTableViewOfActors() {
		TableView<Actor> tableview = Actor.createTable();
		tableview.setItems(collectionActors);
		return tableview;
	}

	public int getSize() { return collectionActors.size(); }

	public Army getOpposingArmy() {return opposingArmy;}
	
	public void setOpposingArmy(Army opposingArmy){this.opposingArmy = opposingArmy;}

	public Actor findNearestOpponent(Actor actorToMove) {
		Actor nearest = null;
		double distanceToClosest = Double.MAX_VALUE;
		for (Actor current : collectionActors){
			double calculatedDistance = actorToMove.distanceTo(current);
			if(calculatedDistance < distanceToClosest){
				distanceToClosest = calculatedDistance;
				
			}
			nearest = current;
		}
		return nearest;
		}
	
	public void serialize(ObjectOutputStream out) throws IOException {
		  out.writeObject(name);
		  out.writeDouble(color.getRed());
		  out.writeDouble(color.getGreen());
		  out.writeDouble(color.getBlue());
		  out.writeDouble(color.getOpacity());
		  out.writeInt(collectionActors.size());
		  for (Actor a : collectionActors)
		    out.writeObject(a);
		  } // end serialize() to support serialization

		public void deserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
		  collectionActors.clear();
		  name = (String) in.readObject();
		  color = new Color(in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
		  dropShadow = new DropShadow(10.0, color);
		  int size = in.readInt();
		  for (int i = 0; i < size; ++i) {
		    Actor actor = (Actor) in.readObject();
		    actor.setArmyAllegiance(this);
		    actor.getAvatar().setEffect(dropShadow);
		    simulator.getChildren().add(actor.getAvatar());
		    collectionActors.add(actor);
		  }
		} // end deserialize() to support serialization

		

	public double getSpeedControllerValue() {
			return simulator.getSpeedControllerValue();
		}
//	

	
	public void removeNowDeadActor(Actor nowDeadActor) {
		 final ObservableList<Node> listJavaFXNodesOnBattlefield = simulator.getChildren(); // creating as a convenience variable, since the removeNowDeadActor() method needs to manage many Node objects in the simulator collection of Node objects
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // START: Create Notification message about the nowDeadActor: Create, then add two Transition Animations, packing in a ParallelTransition
		 { // setup Stack Frame to allow re-use of variable identifiers "tt", "ft" and "pt"
		 Text message = new Text(240.0, 100.0, "Dead: " + nowDeadActor.getName()); message.setFont(NOTIFICATION_FONT_SMALL); message.setStroke(color);
		 final Duration duration = Duration.seconds(3.0);
		 FadeTransition ft = new FadeTransition(duration); ft.setToValue(0.0); // no need to associate with the Text (message) here, that will be done in the ParallelTransition
		 TranslateTransition tt = new TranslateTransition(duration); tt.setByY(200.0);  // no need to associate with the Text (message) here, that will be done in the ParallelTransition
		 ParallelTransition pt = new ParallelTransition(message, ft, tt); pt.setOnFinished(event->listJavaFXNodesOnBattlefield.remove(message)); pt.play(); // couple both Transitions in the ParallelTransition and associate with Text
		 listJavaFXNodesOnBattlefield.add(message); // it will play() and after playing the code in the setOnFinished() method will called to remove the temporay message from the scenegraph.
		 }
		 // END: Create Notification message about the nowDeadActor: Create, then add two Transition Animations, packing in a ParallelTransition
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

		 collectionActors.remove(nowDeadActor); // removes nowDeadActor from the collection of active Actor objects that are part of this army.
		 listJavaFXNodesOnBattlefield.remove(nowDeadActor.getAvatar()); // removes the avatar from the screnegraph (the Node object). The actor will disappear from the screen.
		 
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // START: Create Final Announcement of Winning Army
		 if (collectionActors.size() == 0) { // Army has been wiped out, since no Actor objects remain in the collection. Therefore . . . the opposing Army wins.
		 Text winner = new Text(260.0, 300.0, "Winner: " + opposingArmy.getName()); winner.setFont(NOTIFICATION_FONT_LARGE); winner.setStroke(opposingArmy.color); winner.setEffect(opposingArmy.dropShadow);
		 final Duration duration = Duration.seconds(1.0);
		 FadeTransition ft = new FadeTransition(duration, winner); ft.setToValue(0.2); ft.setCycleCount(10); ft.setAutoReverse(true); ft.setOnFinished(event->listJavaFXNodesOnBattlefield.remove(winner)); ft.play();
		 listJavaFXNodesOnBattlefield.add(winner); // it will play() and after playing the code in the setOnFinished() method will called to remove the temporary winner from the scenegraph.
		 }
		 // END: Create Final Announcement of Winning Army
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 } // end removeNowDeadActor()
		 
	
	
//	public void removeNowDeadActor(Actor actor) {
//		//collectionActors.remove(actor);
//		//simulator.getChildren().remove(actor.getAvatar());
//		
//		
//		Text message= new Text(240.0,100.0,"Dead:"+ actor.getName());
//		message.setStroke(color);
//		
//		FadeTransition ft=  new FadeTransition(Duration.seconds(3.0)); ft.setToValue(0.0); 
//		TranslateTransition tt = new  TranslateTransition(Duration.seconds(3.0));tt.setByX(Math.random()*2.0); tt.setByY(Math.random()*230.0);
//	  
//		
//		ParallelTransition pt = new ParallelTransition(message,ft ,tt); pt.play();
//		 pt.setOnFinished(event->simulator.getChildren().remove(message));
//		
//		 
//		 collectionActors.remove(actor);
//		simulator.getChildren().add(message);
//	  simulator.getChildren().remove(actor.getAvatar());
//	}
	
	public Point2D validateCoordinate(Point2D possibleNewLocation) {
		double maxX = simulator.getScene().getWidth();
		double maxY = simulator.getScene().getHeight();
		double myX = possibleNewLocation.getX();
		double myY = possibleNewLocation.getY();

		final double MEANDERING_RANGE = 20.0;
		if (myX <= 0.0)
			myX = Math.random() * MEANDERING_RANGE;

		else if (myX >= maxX)
			myX = maxX - Math.random() * MEANDERING_RANGE;

		if (myY <= 0.0)
			myY = Math.random() * MEANDERING_RANGE;

		else if (myY >= maxY)
			myY = maxY - Math.random() * MEANDERING_RANGE;

		return new Point2D(myX, myY);
	}

}