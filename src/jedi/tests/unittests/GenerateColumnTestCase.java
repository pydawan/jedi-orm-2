/***********************************************************************************************
 * @(#)GenerateColumnTestCase.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/07/21
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
import jedi.generator.JediCodeGenerator;

import org.junit.BeforeClass;
import org.junit.Test;

public class GenerateColumnTestCase {
	
	@BeforeClass
	public static void testSetup() {
		JediORMEngine.loadJediProperties();
	}

	@Test
	public void testGetSQL() {
//		JediORMEngine.syncdb();
//		JediORMEngine.droptables();
		JediCodeGenerator.generateCode();
		/*
		try {
			System.out.println("\n\n");
			Field field = PessoaFisica.class.getDeclaredField("pessoa");
			field.setAccessible(true);
			System.out.println("One to One Relationship Code");
			System.out.println(JediCodeGenerator.getRelationshipCode(field));
			System.out.println("------------------------------------------------------");
			System.out.println("Foreign key Relationship Code");
			field = Uf.class.getDeclaredField("pais");
			field.setAccessible(true);
			System.out.println(JediCodeGenerator.getRelationshipCode(field));
			System.out.println("------------------------------------------------------");
			field = Book.class.getDeclaredField("authors");
			field.setAccessible(true);
			System.out.println("Many to many Relationship Code");
			System.out.println(JediCodeGenerator.getRelationshipCode(field));
			field = Group.class.getDeclaredField("members");
			field.setAccessible(true);
			System.out.println(JediCodeGenerator.getRelationshipCode(field));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		*/
		
		/*
		System.out.println("-------------------------------------------------");
		System.out.println("Teste - getSQL");
		System.out.println("-------------------------------------------------");
		System.out.println(JediORMEngine.getCreateTableSQL(Autor.class));
		System.out.println(JediORMEngine.getFields(Autor.class));
		System.out.println(JediORMEngine.getSQL(Group.class));
		System.out.println(JediORMEngine.listSQL(Book.class, CharField.class));
		System.out.println(JediORMEngine.mapSQL(Book.class, CharField.class));
		System.out.println(JediORMEngine.listSQL(Book.class));
		System.out.println(JediORMEngine.mapSQL(Book.class));
		*/
		
		/*
		JediORMEngine.execute(JediORMEngine.getSQL(Autor.class));
		JediORMEngine.syncdb();
		JediORMEngine.droptables();
		*/
		System.out.println("-------------------------------------------------");
	}
}