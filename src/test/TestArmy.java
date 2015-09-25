package test;

import static org.junit.Assert.assertTrue;
import javafx.scene.paint.Color;
import org.junit.Test;
import util.Input;
import army.Army;
import actor.*;

/**
 * The <i>TestArmy</i>is a JUnit Test <i>Army</i> class. The <i>TestArmy</i> tests various test cases for the Army Class and other Classes.
 * 
 * @author Elim Yao Tsiagbey
 * @version Lab Assignment 3: <i>The Hobbit Battlefield Simulator</i>
 */

public class TestArmy 
{
	
	/** Creates <i>Army</i> object and tests armyAllegiance
	 * Creates <i>Elf</i> and <i>Orc</i> objects and tests the hasSword, hasShield, Visibility and Replenish fields**/
	@Test
	public void testPhase1()
	{
		Army army = new Army("forcesOfDarkness", null, Color.RED);

		Actor hobbit = new Hobbit(army);
		assertTrue(hobbit.getAllegiance() == army);

		Actor a1 = new Elf(army); // Reusing the reference variable a1 to store the location information for another new Actor object. The old Actor object is no longer referenced by the program and is eligible for garbage collection (that is, permanent removal from heap memory).
		System.out.print("\nActor object (re-use a2) after default constructor: ");
		System.out.println((Elf)a1);
		((Elf)a1).sethasSword(true);// Setting the sethasStaff value to true
		assertTrue(((Elf)a1).gethasSword()==true);
		System.out.print("Actor object (re-use a2) after inputAllFields() method: ");
		System.out.println(((Elf)a1));

		Actor a2 = new Elf(army); // Reusing the reference variable a1 to store the location information for another new Actor object. The old Actor object is no longer referenced by the program and is eligible for garbage collection (that is, permanent removal from heap memory).
		System.out.print("\nActor object (re-use a2) after default constructor: ");
		System.out.println((Elf)a2);
		((Elf)a2).sethasShield(true);// Setting the sethasStaff value to true
		assertTrue(((Elf)a2).gethasShield()==true);
		System.out.print("Actor object (re-use a2) after inputAllFields() method: ");
		System.out.println(((Elf)a2));

		Actor a3 = new Orc(army); // Reference variable a1 captures location information of a newly created Actor object.
		System.out.print("\nActor object (a3) after default constructor: ");
		System.out.println(((Orc)a3));

		((Orc)a3).setVisibility(14);
		System.out.print("Actor object (a3) after field-specific set() methods: ");
		System.out.println(((Orc)a3));

		((Orc)a3).setReplenish(3);
		System.out.print("Actor object (a3) after field-specific set() methods: ");
		System.out.println(((Orc)a3));
		//test elf and orc
	}
	
	
	/** Creates <i>Elf</i> object and tests the boundaries for the Health, Strength and Speed 
	 * and also testing randomly set variables **/
	@Test
	public void testPhase2()
	{
		
		Army army = new Army("forcesOfDarkness", null, null);
		Actor elf = new Elf(army);
		
		//Testing for randomly set variables and testing to make sure they are within the valid boundary
		assertTrue(elf.getHealth() >= Actor.MIN_HEALTH );
		assertTrue(elf.getHealth() <=Actor.MAX_HEALTH );
		
		//Testing for randomly set variables and testing to make sure they are within the valid boundary
		assertTrue(elf.getStrength()  >=Actor.MIN_STRENGTH );
		assertTrue(elf.getStrength()  <=Actor.MAX_STRENGTH);

		//Testing for randomly set variables and testing to make sure they are within the valid boundary
		assertTrue(elf.getSpeed()  >=Actor.MIN_SPEED);
		assertTrue(elf.getSpeed()  <=Actor.MAX_SPEED);
	}
	
	/** Creates <i>Army/i> object and tests the various functions in the Army class **/
	@Test
	public void testPhase3()
	{
		//Testing the populate function
		Army forcesOfLight = new Army("Forces of Light", null, null);
		forcesOfLight.populate(ActorFactory.Type.HOBBIT, 4);
		forcesOfLight.populate(ActorFactory.Type.ELF, 3);
		forcesOfLight.populate(ActorFactory.Type.WIZARD, 2);
		forcesOfLight.display(); // display after automatic generation with random values
		
		//Testing the edit function
		//System.out.println("Please input 3, newHobbit, 35, 4, 75, 16, 45");
		int indexToEdit = Input.getInt("Forces of Light", 0, forcesOfLight.size()-1);
		forcesOfLight.edit(indexToEdit);
		forcesOfLight.display(); // display after editing a selected Actor object

		
		//Testing display function for Stealth
		Actor wiz = new Wizard(forcesOfLight);
		String s = wiz.toString();
		assert(s.contains("Stealth"));
		
		//Testing display function for hasHorse and hasStaff
		Actor hob = new Hobbit(forcesOfLight);
		String t = hob.toString();
		assert(t.contains("hasHorse"));
		assert(t.contains("hasStaff"));
		
		//Testing display function for hasSword and hasShield
		Actor elf = new Elf(forcesOfLight);
		String u = elf.toString();
		assert(u.contains("hasSword"));
		assert(u.contains("hasShield"));
		
		//Testing display function for Visibility and Replenish
		Actor orc = new Orc(forcesOfLight);
		String v = orc.toString();
		assert(v.contains("Visibility"));
		assert(v.contains("Replenish"));
		
		//Testing the size function
		assertTrue(forcesOfLight.size() == forcesOfLight.getSize());
	}
}
