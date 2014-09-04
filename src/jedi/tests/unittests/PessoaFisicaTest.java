/***********************************************************************************************
 * @(#)PessoaFisicaTest.java
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
import app.models.PessoaFisica;

public class PessoaFisicaTest {
	
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
        PessoaFisica pessoaFisicaEsperada = new PessoaFisica();
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Thiago Alexandre Martins Mont");
        pessoa.setIdade(30);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dataNascimento = sdf.parse("19/11/1982");
            pessoa.setDataNascimento(dataNascimento);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pessoaFisicaEsperada.setCpf("003.696.631-28");
        pessoaFisicaEsperada.setPessoa(pessoa);
        pessoaFisicaEsperada.insert();
        PessoaFisica pessoaFisicaObtida = PessoaFisica.objects.get("cpf", "003.696.631-28");
        Assert.assertEquals(pessoaFisicaEsperada.getId(), pessoaFisicaObtida.getId());
    }

    @Test
    public void testUpdate() {
        PessoaFisica pessoaFisicaEsperada = PessoaFisica.objects.get("cpf", "003.696.631-28");
        Pessoa pessoa = pessoaFisicaEsperada.getPessoa();
        pessoa.update("nome='Thiago'");
        PessoaFisica pessoaFisicaObtida = PessoaFisica.objects.get("cpf", "003.696.631-28");
        String nomeEsperado = pessoa.getNome();
        String nomeObtido = pessoaFisicaObtida.getPessoa().getNome();
        Assert.assertTrue(nomeEsperado.equals(nomeObtido));
    }

    @Test
    public void testDelete() {
        int esperado = 0;
        PessoaFisica.objects.all().delete();
        int obtido = PessoaFisica.objects.all().count();
        Assert.assertEquals(esperado, obtido);
    }
    
    @Test
    public void testSaveInsert() {
    	PessoaFisica pessoaFisicaEsperada = new PessoaFisica();
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("James Goslin");
        pessoa.setIdade(30);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dataNascimento = sdf.parse("19/05/1955");
            pessoa.setDataNascimento(dataNascimento);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pessoaFisicaEsperada.setCpf("111.111.111-11");
        pessoaFisicaEsperada.setPessoa(pessoa);
        pessoaFisicaEsperada.save();
        PessoaFisica pessoaFisicaObtida = PessoaFisica.objects.get("cpf", "111.111.111-11");
        Assert.assertEquals(pessoaFisicaEsperada.getId(), pessoaFisicaObtida.getId());
    }
    
    @Test
    public void testSaveUpdate() {
    	PessoaFisica pessoaFisicaEsperada = PessoaFisica.objects.get("cpf", "111.111.111-11");
    	Pessoa pessoaEsperada = pessoaFisicaEsperada.getPessoa();
    	pessoaEsperada.setNome("James Gosling");
    	pessoaFisicaEsperada.setCpf("222.222.222-22");
    	pessoaFisicaEsperada.save();
    	PessoaFisica pessoaFisicaObtida = PessoaFisica.objects.get("cpf", "222.222.222-22");
    	Pessoa pessoaObtida = pessoaFisicaObtida.getPessoa();
    	Assert.assertEquals(pessoaEsperada.getNome(), pessoaObtida.getNome());
    }
}