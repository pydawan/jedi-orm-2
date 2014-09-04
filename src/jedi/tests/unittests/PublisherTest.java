/***********************************************************************************************
 * @(#)PublisherTest.java
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
import app.models.Publisher;
import app.models.State;

public class PublisherTest {
	
	@BeforeClass
	public static void testSetup() {
		JediORMEngine.FOREIGN_KEY_CHECKS = false;
		JediORMEngine.flush();
	}

    @AfterClass
    public static void testCleanup() {
        //Country.objects.all().delete();
    	JediORMEngine.droptables();
    }

    @Test
    public void testInsert() {
        // Expected.
        Publisher expectedPublisher = new Publisher();
        expectedPublisher.setName("Editora Abril");
        State state = new State();
        state.setName("Goiás");
        state.setAcronym("GO");
        Country country = new Country();
        country.setName("Brasil");
        country.setAcronym("BR");
        state.setCountry(country);
        expectedPublisher.setState(state);
        expectedPublisher.insert();
        // Obtained.
        Publisher obtainedPublisher = Publisher.objects.get("name", "Editora Abril");
        // Assertion.
        Assert.assertEquals(expectedPublisher.getId(), obtainedPublisher.getId());
    }

    @Test
    public void testUpdate() {
        Publisher expectedPublisher = Publisher.objects.get("name", "Editora Abril");
        State state = new State("São Paulo", "SP", Country.objects.get("name", "Brasil").as(Country.class));
        state.save();
        expectedPublisher.update(String.format("state_id=%d", state.getId()));
        Publisher obtainedPublisher = Publisher.objects.get("name", "Editora Abril");
        Assert.assertEquals(expectedPublisher.getState().getId(), obtainedPublisher.getState().getId());
    }

    @Test
    public void testDelete() {
        int expectedAmount = 0;
        int obtainedAmount = Publisher.objects.all().delete().count();
        Assert.assertEquals(expectedAmount, obtainedAmount);
    }

    @Test
    public void testSaveInsert() {
        Publisher expectedPublisher = new Publisher();
        expectedPublisher.setName("McGraw Hill");
        expectedPublisher.setState(new State("New York", "NY", new Country("Unitated Ufs of America", "US")));
        expectedPublisher.save();
        Publisher obtainedPublisher = Publisher.objects.get("name", "McGraw Hill");
        Assert.assertEquals(expectedPublisher.getId(), obtainedPublisher.getId());
    }
}