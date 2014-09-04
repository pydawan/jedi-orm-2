/***********************************************************************************************
 * @(#)MembershipTest.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/02/19
 * 
 * Copyright (c) 2014 Thiago Alexandre Martins Monteiro.
 * 
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the GNU Public License v2.0 which accompanies 
 * this distribution, and is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *    Thiago Alexandre Martins Monteiro - initial API and implementation
 ************************************************************************************************/

package jedi.tests.unittests;

import jedi.db.engine.JediORMEngine;
import jedi.types.DateTime;
import jedi.types.JediDate;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import app.models.Group;
import app.models.Membership;
import app.models.Person;

public class MembershipTest {
	
	@BeforeClass
	public static void testSetup() {
		JediORMEngine.FOREIGN_KEY_CHECKS = false;
		JediORMEngine.flush();
	}
	
	@AfterClass
	public static void testCleanup() {
		JediORMEngine.droptables();
	}
	
	@Test
	public void testInsert() {
		Person ringo = Person.objects.create("name='Ringo Starr'");
		Person paul = Person.objects.create("name='Paul McCartney'");
		Person john = Person.objects.create("name='John Lennon'");
		Person george = Person.objects.create("name='George Harrison'");
		Group beatles = Group.objects.create("name='The Beatles'");		
		Membership m1 = new Membership();
		m1.setPerson(ringo);
		m1.setGroup(beatles);
		m1.setDateJoined(new DateTime(1962, 8, 16));
		m1.setInviteReason("Needed a new drummer.");
		m1.insert();
		Membership m2 = new Membership();
		m2.setPerson(paul);
		m2.setGroup(beatles);
		m2.setDateJoined(new DateTime(1960, 8, 1));
		m2.setInviteReason("Wanted to form a band.");
		m2.insert();
		Membership m3 = new Membership();
		m3.setPerson(john);
		m3.setGroup(beatles);
		m3.setDateJoined(new DateTime(1960, 8, 1));
		m3.setInviteReason("Wanted to form a band.");
		m3.insert();
		Membership m4 = new Membership();
		m4.setPerson(george);
		m4.setGroup(beatles);
		m4.setDateJoined(new JediDate(1961, 9, 1));
		m4.setInviteReason("Needed a new guitarrist");
		m4.insert();
		System.out.println();
		System.out.println(ringo.getGroupSet());
		System.out.println(paul.getGroupSet());
		System.out.println(john.getGroupSet());
		System.out.println(george.getGroupSet());
		System.out.println(beatles.getMembers());
	}
}