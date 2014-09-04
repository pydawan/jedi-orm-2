package jedi.tests.unittests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jedi.db.engine.JediORMEngine;

import org.junit.Test;

import app.models.PessoaJuridica;

public class PessoaJuridicaTestCase {

	@Test
	public void testInsert() {
		JediORMEngine.FOREIGN_KEY_CHECKS = false;
		JediORMEngine.flush();
		PessoaJuridica pessoaJuridicaEsperada = new PessoaJuridica();
        pessoaJuridicaEsperada.setNome("Thiago Alexandre Martins Mont");
        pessoaJuridicaEsperada.setIdade(30);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dataNascimento = sdf.parse("19/11/1982");
            pessoaJuridicaEsperada.setDataNascimento(dataNascimento);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pessoaJuridicaEsperada.setCnpj("445442525254254");
        pessoaJuridicaEsperada.insert();
        System.out.println(PessoaJuridica.objetcs.all().toString());
        PessoaJuridica.objetcs.get("id", 1).delete();
        System.out.println(PessoaJuridica.objetcs.all());
        JediORMEngine.droptables();
	}
}
