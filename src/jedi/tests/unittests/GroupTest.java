/***********************************************************************************************
 * @(#)GroupTest.java
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
import jedi.db.models.QuerySet;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import app.models.Group;
import app.models.Person;

public class GroupTest {

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
		Group expectedGroup = new Group();
		expectedGroup.setName("The Beatles");
		QuerySet<Person> members = new QuerySet<>();
		Person ringo = Person.objects.create("name='Ringo Starr'");
		Person paul = Person.objects.create("name='Paul McCartney'");
		members.add(ringo);
		members.add(paul);
		expectedGroup.setMembers(members);
		expectedGroup.insert();
		Group obtainedGroup = Group.objects.get("name", "The Beatles");		
		Assert.assertEquals(expectedGroup, obtainedGroup);
	}
}