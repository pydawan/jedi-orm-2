/***********************************************************************************************
 * @(#)ManagerTest.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/09/08
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

import jedi.db.models.QuerySet;
import org.junit.Test;
import app.models.Country;

public class ManagerTest {
    @Test
    public void testWhere() {
    	QuerySet<Country> qs1 = Country.objects.where("name = {0} or id = 15", "Brazil");
    	QuerySet<Country> qs2 = Country.objects.where("name = 'Brazil' or id = 15 or id = 30");
    	System.out.println(qs1.toJSON());
    	System.out.println(qs2.toXML());
    }
}