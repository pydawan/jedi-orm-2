/***********************************************************************************************
 * @(#)PessoaFisica.java
 *
 * Version: 1.0
 *
 * Date: 2014/09/09
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
import jedi.db.models.Model;
import jedi.db.models.OneToOneField;

/**
 * <h2>Classe que modela uma pessoa física.</h2>
 * <h3>Código:</h3>
 * <pre>
 * <code class="java">
 * package app.models;
 *
 * import jedi.db.models.CharField;
 * import jedi.db.models.Manager;
 * import jedi.db.models.Model;
 * import jedi.db.models.OneToOneField;
 *
 * public class PessoaFisica extends Model {
 *
 *     // Atributes
 *     private static final long serialVersionUID = -2834119019885606438L;
 *
 *    {@literal @}OneToOneField
 *     private Pessoa pessoa;
 *
 *    {@literal @}CharField(max_length = 14, unique = true)
 *     private String cpf; 
 *
 *     public static Manager objects = new Manager(PessoaFisica.class);
 *
 *     // Constructors
 *     public PessoaFisica() {}
 *
 *     // Getters
 *     public Pessoa getPessoa() {
 *         return pessoa;
 *     }
 *
 *     public String getCpf() {
 *         return cpf;
 *     }
 *
 *     // Setters
 *     public void setPessoa(Pessoa pessoa) {
 *         this.pessoa = pessoa;
 *     }
 *
 *     public void setCpf(String cpf) {
 *         this.cpf = cpf;
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
 * @see jedi.db.models.CharField
 * @see jedi.db.models.Manager
 * @see jedi.db.models.Model
 * @see jedi.db.models.OneToOneField
 *
 * @see app.models.Pessoa
 */
public class PessoaFisica extends Model {

    // Attributes

    /**
     * Número de versão da classe.
     *
     * @see java.lang.Long
     */
    private static final long serialVersionUID = -2834119019885606438L;

    /**
     * Pessoa associada a pessoa física.
     *
     * @see jedi.db.models.OneToOneField
     *
     * @see app.models.Pessoa
     */
    @OneToOneField
    private Pessoa pessoa;

    /**
     * CPF da pessoa.
     *
     * @see java.lang.String
     *
     * @see jedi.db.models.CharField
     */
    @CharField(max_length = 14, unique = true)
    private String cpf;

    /**
     * Gerenciador de consultas a objetos dessa classe 
     * persistidos em banco de dados.
     *
     * @see jedi.db.models.Manager
     */
    public static Manager objects = new Manager(PessoaFisica.class);

    // Constructors

    /**
     * Construtor padrão.
     */
    public PessoaFisica() {}

    // Getters

    /**
     * Retorna a pessoa associada a pessoa física.
     *
     * @return a pessoa associada a pessoa física
     *
     * @see app.models.Pessoa
     */
    public Pessoa getPessoa() {
        return pessoa;
    }

    /**
     * Retorna o CPF da pessoa física.
     *
     * @return o CPF da pessoa física
     *
     * @see java.lang.String
     */
    public String getCpf() {
        return cpf;
    }

    // Setters

    /**
     * Define a pessoa a ser associada a pessoa física.
     *
     * @param pessoa a pessoa a ser associada a pessoa física
     *
     * @see app.models.Pessoa
     */
    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    /**
     * Define o CPF da pessoa física.
     *
     * @param cpf o CPF da pessoa física
     *
     * @see java.lang.String
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
