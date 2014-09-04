/***********************************************************************************************
 * @(#)BookTest.java
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

import app.models.Author;
import app.models.Book;
import app.models.Country;
import app.models.Publisher;
import app.models.State;

public class BookTest {
	
	@BeforeClass
	public static void testSetup() {
		JediORMEngine.FOREIGN_KEY_CHECKS = false;
		JediORMEngine.flush();		
	}

    @AfterClass
    public static void testCleanUp() {
    	JediORMEngine.droptables();
    }
    
    @Test
    public void testInsert() {
        Book expectedBook = new Book();
        expectedBook.setTitle("O Alquimistaa");
        expectedBook.setPublicationDate("17/10/2013");
        expectedBook.setPublisher(new Publisher("Editora Abril", new State("São Paulo", "SP", new Country("Brasil", "BR"))));
        QuerySet<Author> authors = new QuerySet<Author>();
        authors.add(new Author("Paulo", "Coelho", "paulocoelho@gmail.com"));
        expectedBook.setAuthors(authors);
        expectedBook.save();
        Book obtainedBook = Book.objects.get("title", "O Alquimistaa");
        Assert.assertEquals(expectedBook.getId(), obtainedBook.getId());
    }
    
    @Test
    public void testUpdate() {
        Book expectedBook = new Book();
        expectedBook.setTitle("O Alquimista");
        Book obtainedBook = Book.objects.get("title", "O Alquimistaa");
        obtainedBook.update("title='O Alquimista'");
        Assert.assertEquals(expectedBook.getTitle(), obtainedBook.getTitle());
    }
    
    @Test
    public void testDelete() {
        int expected = 0;
        int obtained = Book.objects.all().delete().count();
        Assert.assertEquals(expected, obtained);
    }
    
    @Test
    public void testSaveInsert() {
        Book expectedBook = new Book();
        expectedBook.setTitle("Java Cookbook");
        expectedBook.setPublicationDate("10/10/2000 ...");
        // Changes the publisher of this book.
        expectedBook.setPublisher(new Publisher("O'Reilly", new State("New York", "NY", new Country("Unitated States of America", "US"))));
        // Adds a new author for this book.
        QuerySet<Author> authors = new QuerySet<Author>();
        authors.add(new Author("Ian", "F. Darwin", "iandarwin@gmail.com"));
        expectedBook.setAuthors(authors);
        expectedBook.save();
        Book obtainedBook = Book.objects.get("title", "Java Cookbook");
        Assert.assertEquals(expectedBook.getTitle(), obtainedBook.getTitle());
    }
    
    @Test
    public void testSaveUpdate() {
        Book expectedBook = Book.objects.get("publicationDate", "10/10/2000 ...");
        expectedBook.setPublicationDate("10/10/2000");
        expectedBook.getAuthors().add(new Author("Thiago", "Monteiro", "thiagomonteiro@gmail.com"));
        expectedBook.setPublisher(new Publisher("McGraw-Hill", State.objects.get("acronym", "NY").as(State.class)));
        expectedBook.save();
        Book obtainedBook = Book.objects.get("publicationDate", "10/10/2000");        
        Assert.assertEquals(expectedBook, obtainedBook);
    }

    @Test
    public void testFilter() {
    	Book expectedBook = new Book();
        expectedBook.setTitle("Voina i Mir");
        expectedBook.setPublicationDate("10/10/1865");        
        expectedBook.setPublisher(new Publisher("Lenin Publisher", new State("Moscou", "MO", new Country("Russia", "RU"))));
        QuerySet<Author> authors = new QuerySet<Author>();
        authors.add(new Author("Lev", "Tolstoy", "tolstoy@yandex.ru"));
        expectedBook.setAuthors(authors);
        expectedBook.save();
        Book obtainedBook = Book.objects.<Book>filter("title='Voina i Mir'").first();
        Assert.assertEquals(expectedBook.getTitle(), obtainedBook.getTitle());
    }
}