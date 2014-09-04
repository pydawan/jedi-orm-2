/***********************************************************************************************
 * @(#)PessoaJuridica.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/09/03
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

package app.models;

import jedi.db.models.CharField;
import jedi.db.models.Manager;

/**
 * <h2>Classe que modela uma pessoa jurídica.</h2>
 * <h3>Código:</h3>
 * <pre>
 * <code class="java">
 * package app.models;
 * 
 * import jedi.db.models.CharField;
 * import jedi.db.models.Manager;
 * 
 * public class PessoaJuridica extends Pessoa {
 * 
 *     // Attributes
 *     private static final long serialVersionUID = -6952547594451370637L;
 *     
 *    {@literal @}CharField(max_length = 15)
 *     private String cnpj;
 *     
 *     public static Manager objetcs = new Manager(PessoaJuridica.class);
 *     
 *     // Constructors
 *     public PessoaJuridica() {}
 *     
 *     // Getters
 *     public String getCnpj() {
 *         return cnpj;
 *     }
 *     
 *     // Setters
 *     public void setCnpj(String cnpj) {
 *         this.cnpj = cnpj;
 *     }
 * }
 * </code>
 * </pre>
 * 
 * @author Thiago Monteiro
 * @version 1.0
 * @since 1.0
 * 
 * @see java.lang.Long
 * @see java.lang.String
 * 
 * @see jedi.db.models.Manager
 * @see jedi.db.models.Model
 * 
 * @see app.models.Pessoa
 */
public class PessoaJuridica extends Pessoa {

	// Attributes
	
	/**
	 * Número de versão da classe.
	 * 
	 * @see java.lang.Long
	 */
	private static final long serialVersionUID = -6952547594451370637L;

	/**
	 * CNPJ da pessoa jurídica.
	 * 
	 * @see java.lang.String
	 */
	@CharField(max_length = 15)
	private String cnpj;

	/**
	 * Gerenciador de consultas a objetos dessa classe 
     * persistidos em banco de dados.
     * 
     * @see jedi.db.models.Manager
	 */
	public static Manager objetcs = new Manager(PessoaJuridica.class);
	
	// Constructors
	
	/**
	 * Construtor padrão.
	 */
	public PessoaJuridica() {}

	// Getters
	
	/**
	 * Retorna o CNPJ da pessoa jurídica.
	 * 
	 * @return o CNPJ da pessoa jurídica
	 * 
	 * @see java.lang.String
	 */
	public String getCnpj() {
		return cnpj;
	}

	// Setters
	
	/**
	 * Define o CNPJ da pessoa jurídica.
	 * 
	 * @param cnpj o CNPJ da pessoa jurídica
	 * 
	 * @see java.lang.String
	 */
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
}
