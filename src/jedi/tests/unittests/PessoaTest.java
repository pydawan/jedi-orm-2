/***********************************************************************************************
 * @(#)PessoaTest.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/02/16
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jedi.db.engine.JediORMEngine;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import app.models.Pessoa;

public class PessoaTest {
	
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
        Pessoa pessoaEsperada = new Pessoa();
        pessoaEsperada.setNome("Thiago Alexandre Martins Mont");
        pessoaEsperada.setIdade(30);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dataNascimento = sdf.parse("19/11/1982");
            pessoaEsperada.setDataNascimento(dataNascimento);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pessoaEsperada.insert();
        Pessoa pessoaObtida = Pessoa.objects.get("nome", "Thiago Alexandre Martins Mont");
        Assert.assertEquals(pessoaEsperada.getId(), pessoaObtida.getId());
    }

    @Test
    public void testUpdate() {
        Pessoa pessoaEsperada = Pessoa.objects.get("nome", "Thiago Alexandre Martins Mont");
        pessoaEsperada.update("nome='Thiago Alexandre Martins Monteiro'");
        Pessoa pessoaObtida = Pessoa.objects.get("nome", "Thiago Alexandre Martins Monteiro");
        Assert.assertTrue(pessoaEsperada.getNome().equals(pessoaObtida.getNome()));
    }

    @Test
    public void testDelete() {
        int quantidadePessoasEsperada = 0;
        Pessoa.objects.all().delete();
        int quantidadePessoasObtida = Pessoa.objects.all().count();
        Assert.assertEquals(quantidadePessoasEsperada, quantidadePessoasObtida);
    }

    @Test
    public void testSaveInsert() {
        Pessoa pessoaEsperada = new Pessoa();
        pessoaEsperada.setNome("Guido");
        pessoaEsperada.setIdade(57);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dataNascimento = sdf.parse("31/01/1956");
            pessoaEsperada.setDataNascimento(dataNascimento);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pessoaEsperada.save();        
        Pessoa pessoaObtida = Pessoa.objects.get("nome", "Guido");
        Assert.assertEquals(pessoaEsperada.getId(), pessoaObtida.getId());       
    }

    @Test
    public void testSaveUpdate() {
        Pessoa pessoaEsperada = Pessoa.objects.get("nome", "Guido");
        pessoaEsperada.setNome("Guido van Rossum");
        pessoaEsperada.isAdmin(true);
        pessoaEsperada.save();
        Pessoa pessoaObtida = Pessoa.objects.get("nome", "Guido van Rossum");
        Assert.assertTrue(pessoaEsperada.getNome().equals(pessoaObtida.getNome()));
    }
}