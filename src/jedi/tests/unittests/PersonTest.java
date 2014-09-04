/***********************************************************************************************
 * @(#)PersonTest.java
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import app.models.Person;

public class PersonTest {

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
		Person expectedPerson = new Person();
		expectedPerson.setName("Thiago Alexandre Martins Monteiro");
		expectedPerson.insert();
		Person obtainedPerson = Person.objects.get("name", "Thiago Alexandre Martins Monteiro");
		Assert.assertEquals(expectedPerson, obtainedPerson);
	}

	@Test
	public void testUpdate() {
		Person expectedPerson = Person.objects.get("name", "Thiago Alexandre Martins Monteiro");
		expectedPerson.update("name='Thiago Alexandre'");
		Person obtainedPerson = Person.objects.get("name", "Thiago Alexandre");
		Assert.assertNotNull(obtainedPerson);
	}

	@Test
	public void testDelete() {
		int expectedAmount = 0;
		int obtainedAmount = Person.objects.all().delete().count();
		Assert.assertEquals(expectedAmount, obtainedAmount);
	}
	
	@Test
	public void testInsertSave() {
		Person expectedPerson = new Person();
		expectedPerson.setName("Wolfgang Amadeus Mozart");
		expectedPerson.save();
		Person obtainedPerson = Person.objects.get("name", "Wolfgang Amadeus Mozart");
		Assert.assertEquals(expectedPerson, obtainedPerson);
	}
	
	@Test
	public void testUpdateSave() {
		Person expectedPerson = new Person();
		expectedPerson.setName("Cumpadi Washington");
		expectedPerson.save();
		expectedPerson.setName("Ludwig van Beethoven");
		expectedPerson.save();
		Person obtainedPerson = Person.objects.get("name", "Ludwig van Beethoven");
		Assert.assertEquals(expectedPerson.getName(), obtainedPerson.getName());
	}
}