/***********************************************************************************************
 * @(#)StateTest.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/02/15
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

import app.models.Country;
import app.models.State;

public class StateTest {
	
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
        State expectedState = new State();
        expectedState.setName("Goiaz");
        expectedState.setAcronym("GO");
        Country country = new Country();
        country.setName("Brasil");
        country.setAcronym("BR");
        expectedState.setCountry(country);
        expectedState.insert();        
        State obtainedState = State.objects.get("acronym", "GO");
        System.out.println(obtainedState.toJSON());
        Assert.assertEquals(expectedState.getId(), obtainedState.getId());        
    }

    @Test
    public void testUpdate() {
        State expectedState = State.objects.get("acronym", "GO");
        expectedState.update("name='Goiás'");
        State obtainedState = State.objects.get("acronym", "GO");
        Assert.assertTrue(expectedState.getName().equals(obtainedState.getName()));
    }

    @Test
    public void testDelete() {
        int expected = 0;
        Country.objects.all().delete();
        State.objects.all().delete();
        int obtained = State.objects.all().count();
        Assert.assertEquals(expected, obtained);
    }

    @Test
    public void testSaveInsert() {
        State expectedState = new State();
        expectedState.setName("Sao Paulo");
        expectedState.setAcronym("SP");
        Country country = new Country("Brasil", "BR");
        expectedState.setCountry(country);
        expectedState.save();
        State obtainedState = State.objects.get("acronym", "SP");
        Assert.assertEquals(expectedState.getId(), obtainedState.getId());
    }

    @Test
    public void testSaveUpdate() {
        State expectedState = State.objects.get("acronym", "SP");
        expectedState.setName("São Paulo");
        expectedState.save();
        State obtainedState = State.objects.get("acronym", "SP");
        Assert.assertTrue(expectedState.getName().equals(obtainedState.getName()));
    }
}