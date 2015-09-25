package simulator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import actor.Actor;
import actor.ActorFactory;
import army.Army;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Simulator extends Group{
	private Stage primaryStage;
	private Army forcesOfLight;
	private Army forcesOfDarkness;
	private Stage stageListControllerWindow;
	private Stage stageTableControllerWindow;
	
	private double speedControllerValue = 1.0;
	private static final double MAX_SPEEDUP = 50.0;
	private static final double MIN_SPEEDUP = 1.0;

	public Simulator(Stage primaryStage) {
		this.primaryStage = primaryStage;
		forcesOfLight = new Army("Forces of Light",this, Color.ALICEBLUE);
		forcesOfDarkness = new Army("Forces of Darkness",this, Color.AQUA);
		buildListViewWindow();
		buildTableViewWindow();
		forcesOfLight.setOpposingArmy(forcesOfDarkness);
		forcesOfDarkness.setOpposingArmy(forcesOfLight);
	}

	public void populate() {
		forcesOfLight.populate(ActorFactory.Type.HOBBIT, 4);
		forcesOfLight.populate(ActorFactory.Type.WIZARD, 5);
		forcesOfLight.populate(ActorFactory.Type.ELF, 3);
		forcesOfDarkness.populate(ActorFactory.Type.WIZARD, 4);
		forcesOfDarkness.populate(ActorFactory.Type.ORC, 4);
		forcesOfDarkness.populate(ActorFactory.Type.ELF, 3);
	
		
	}

	public void run() {
		forcesOfLight.startMotion();
		forcesOfDarkness.startMotion();
		
	}

	public void suspend() {
		forcesOfLight.suspendMotion();
		forcesOfDarkness.suspendMotion();
	}

	
	private final void buildListViewWindow() { // final because of its use in the constructor
	    VBox vBoxLightArmy = new VBox(5.0, new Text(forcesOfLight.getName()), new ListView<Actor>(forcesOfLight.getObservableListActors()));
	    VBox vBoxDarkArmy = new VBox(5.0, new Text(forcesOfDarkness.getName()), new ListView<Actor>(forcesOfDarkness.getObservableListActors()));
	    HBox hBoxSceneGraphRoot = new HBox(5.0, vBoxLightArmy, vBoxDarkArmy);

	    if (stageListControllerWindow != null) {
	      stageListControllerWindow.close();
	      stageListControllerWindow.setScene(null);
	    }
	    stageListControllerWindow = new Stage(StageStyle.UTILITY);
	    stageListControllerWindow.initOwner(primaryStage);
	    stageListControllerWindow.setScene(new Scene(hBoxSceneGraphRoot));
	  } // end buildListViewWindow()
	
	
	private final void buildTableViewWindow() { // final because of its use in the constructor
	    VBox vBoxLightArmy = new VBox(5.0, new Text(forcesOfLight.getName()), forcesOfLight.getTableViewOfActors());
	    VBox vBoxDarkArmy = new VBox(5.0, new Text(forcesOfDarkness.getName()), forcesOfDarkness.getTableViewOfActors());
	    HBox hBoxSceneGraphRoot = new HBox(5.0, vBoxLightArmy, vBoxDarkArmy);

	    if (stageTableControllerWindow != null) {
	      stageTableControllerWindow.close();
	      stageTableControllerWindow.setScene(null);
	    }
	    stageTableControllerWindow = new Stage(StageStyle.UTILITY);
	    stageTableControllerWindow.initOwner(primaryStage);
	    stageTableControllerWindow.setScene(new Scene(hBoxSceneGraphRoot));
	  } // end buildTableViewWindow()
	
	public void openListViewWindow() {stageListControllerWindow.show();}
	public void closeListViewWindow() {stageListControllerWindow.hide();}
	
	public void openTableViewWindow() {stageTableControllerWindow.show();}
	public void closeTableViewWindow() {stageTableControllerWindow.hide();}
	
	public void save() {
		  // Using a try block in case there is a file I/O error. Open a file that is configured for binary output.
		  try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("battlefield.ser"))) {
		    forcesOfLight.serialize(out);// "normal" method call that I created. Army class NOT serializable. Actor class and ALL its subclasses are serializable.
		    forcesOfDarkness.serialize(out);// same
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
		} // end save()
	
		public void restore() {
		  // Using a try block in case there is a file I/O error. Open a file that is configured for binary input.
		  try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("battlefield.ser"))) {
		    forcesOfLight.deserialize(in);// "normal" method call that I created. Army class NOT serializable. Actor class and ALL its subclasses are serializable.
		    forcesOfDarkness.deserialize(in); // same
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
		} // end restore()

		public void speedUp() {
			//++speedControllerVlaue;
			if(++speedControllerValue > MAX_SPEEDUP )
				speedControllerValue = MAX_SPEEDUP;
		}
		
		
		public void slowDown() {
			//--speedControllerVlaue;
			if(--speedControllerValue < MIN_SPEEDUP )
				speedControllerValue = MIN_SPEEDUP;
		}

		public double getSpeedControllerValue() {
			// TODO Auto-generated method stub
			return speedControllerValue;
		}

}
