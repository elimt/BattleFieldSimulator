package test;

import static org.junit.Assert.*;
import javafx.scene.paint.Color;
import org.junit.Test;
import actor.*;
import army.*;
/**
 * Simple jUnit class to test behaviour of <i>Actor</i> and its subtypes.
 * 
 * @author Rex Woollard
 * @see Actor
 * @version Lab 4 Assignment
 */
public class JUnitTestSubclassing 
{

	@Test
	public void testIndependentActors() 
	{
		// Create two standalone Actor objects and manage them with subclass-specific reference-to variables to access subclass-specific methods
		Army armyLight = new Army("Forces of Light", null, Color.RED); // required for armyAlligience
		Hobbit a1 = new Hobbit(armyLight); // Reference variable a1 captures location information of a newly created Actor object.
		assertTrue("Health not within boundary values", a1.getHealth() >= Hobbit.MIN_HEALTH && a1.getHealth()<=Hobbit.MAX_HEALTH);
		assertTrue("Speed not within boundary values", a1.getSpeed() >= Hobbit.MIN_SPEED && a1.getSpeed()<=Hobbit.MAX_SPEED);
		assertTrue("Strength not within boundary values", a1.getStrength() >= Hobbit.MIN_STRENGTH && a1.getStrength()<=Hobbit.MAX_STRENGTH);
		assertTrue("Stealth not within boundary values", a1.getStealth() >= Hobbit.MIN_STEALTH && a1.getStealth()<=Hobbit.MAX_STEALTH);
		System.out.print("\nActor object (a1) after default constructor: ");
		System.out.println(a1);
		
		a1.setHealth(-10.0);		assertTrue("Health not within boundary values", a1.getHealth() >= Hobbit.MIN_HEALTH && a1.getHealth()<=Hobbit.MAX_HEALTH);
		a1.setHealth(200.0);		assertTrue("Health not within boundary values", a1.getHealth() >= Hobbit.MIN_HEALTH && a1.getHealth()<=Hobbit.MAX_HEALTH);
		a1.setSpeed(-10.0);			assertTrue("Speed not within boundary values", a1.getSpeed() >= Hobbit.MIN_SPEED && a1.getSpeed()<=Hobbit.MAX_SPEED);
		a1.setSpeed(200.0);			assertTrue("Speed not within boundary values", a1.getSpeed() >= Hobbit.MIN_SPEED && a1.getSpeed()<=Hobbit.MAX_SPEED);
		a1.setStrength(-10.0);	assertTrue("Strength not within boundary values", a1.getStrength() >= Hobbit.MIN_STRENGTH && a1.getStrength()<=Hobbit.MAX_STRENGTH);
		a1.setStrength(200.0);	assertTrue("Strength not within boundary values", a1.getStrength() >= Hobbit.MIN_STRENGTH && a1.getStrength()<=Hobbit.MAX_STRENGTH);
		a1.setStealth(-10.0);		assertTrue("Stealth not within boundary values", a1.getStealth() >= Hobbit.MIN_STEALTH && a1.getStealth()<=Hobbit.MAX_STEALTH);
		a1.setStealth(200.0);		assertTrue("Stealth not within boundary values", a1.getStealth() >= Hobbit.MIN_STEALTH && a1.getStealth()<=Hobbit.MAX_STEALTH);
		
		Wizard a2 = new Wizard(armyLight); // Reference variable a2 captures location information of a newly created Actor object.
		assertTrue("Health not within boundary values", a2.getHealth() >= Hobbit.MIN_HEALTH && a2.getHealth()<=Hobbit.MAX_HEALTH);
		assertTrue("Speed not within boundary values", a2.getSpeed() >= Hobbit.MIN_SPEED && a2.getSpeed()<=Hobbit.MAX_SPEED);
		assertTrue("Strength not within boundary values", a2.getStrength() >= Hobbit.MIN_STRENGTH && a2.getStrength()<=Hobbit.MAX_STRENGTH);
		// no need to test boolean, since it can only ever be true or false.
		System.out.print("\nActor object (a2) after default constructor: ");
		System.out.println(a2);
	} // end main()
	
	/**
	 * During the simulation, we expect some Actor objects to die during combat 
	 */
	@Test
	public void testCombatBetweenPairsOfActors() 
	{
		Army forcesOfDarkness = new Army("Forces of Darkness", null, Color.RED);
		final int NUM_CYCLES = 10;
		final int NUM_SKIRMISHES_BEFORE_DEATH = 10;
		final double MAX_ROUNDS = 20.0;
		System.out.println("TESTING Actor-to-Actor SKIRMISHES\nCreate pairs of Actor objects that will engage in combat. Cycle through several rounds of fighting.");
		System.out.printf("NUM_CYCLES:%d  NUM_SKIRMISHES per cycle:%d  MAX_ROUNDS per skirmish:%.0f\n", NUM_CYCLES, NUM_SKIRMISHES_BEFORE_DEATH, MAX_ROUNDS);
		for (int cycle = 1; cycle <= NUM_CYCLES; ++cycle) 
		{
			System.out.printf("\nCYCLE: %d TWO NEW Actor OBJECTS CREATED\n", cycle);
			Actor attacker = new Hobbit(forcesOfDarkness);
			Actor defender = new Wizard(forcesOfDarkness);
			int skirmishCountDown = NUM_SKIRMISHES_BEFORE_DEATH; 
			while (skirmishCountDown > 0)
			{
				attacker.gameCycleHealthGain();
				defender.gameCycleHealthGain();
				int round = (int) (Math.random() * MAX_ROUNDS) + 5;
				System.out.printf("\nBegin Skirmish: Total Rounds: %d\n", round);
				while (round > 0 && attacker.getHealth() > 0.0 && defender.getHealth() > 0.0)
				{
					attacker.combatRound(defender);
					System.out.printf("%2d: Attacker:%s ===> Defender:%s\n", round--, attacker, defender);
				} // end while() for number of rounds in a single skirmish

				// Actor objects have broken off the skirmish. Determine the outcome of the skirmish.
				// Skirmish could finish with no clear winner, or because one of the Actor objects died.
				if (attacker.getHealth() <= 0.0 || defender.getHealth() <= 0.0) 
				{
					// unlikely that both Actor objects died, but it is possible, thus no use of "else" clause.
					if (attacker.getHealth() <= 0.0)
						System.out.printf("Attacker DIES: Attacker Health:%3.1f    Defender Health:%3.1f\n", attacker.getHealth(), defender.getHealth());
					if (defender.getHealth() <= 0.0)
						System.out.printf("Defender DIES: Attacker Health:%3.1f    Defender Health:%3.1f\n", attacker.getHealth(), defender.getHealth());
					System.out.println("SKIRMISH FINISHED. BOTH Actor objects garbage collected.");
					break; // terminate for (skirmishes)
				} 
				else 
				{
					System.out.println("No one died in this cycle.");
				} // end if-else-if-else for messaging
				--skirmishCountDown;
			} // end while() NUM_SKIRMISHES_BEFORE_DEATH
			assertTrue("No death occured following maximum pattern of engagement.", skirmishCountDown > 0);
		} // end for() cycle
	} // testCombatBetweenPairsOfActors
} // end class JUnitTestSubclassing
