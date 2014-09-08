/***********************************************************************************************
 * @(#)Author.java
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
import jedi.db.models.Model;
import jedi.db.models.QuerySet;

/**
 * <h2>Classe que modela um autor de livro.</h2>
 * <h3>Código:</h3>
 * <pre>
 * <code class="java"> 
 * package app.models;
 * 
 * import jedi.db.models.CharField;
 * import jedi.db.models.Manager;
 * import jedi.db.models.Model;
 * import jedi.db.models.QuerySet;
 * 
 * public class Author extends Model {
 * 
 *     // Attributes
 *     private static final long serialVersionUID = -8520333625577424268L;
 *     
 *    {@literal @}CharField(max_length = 30)
 *     private String firstName;
 *     
 *    {@literal @}CharField(max_length = 30, required = false)
 *     private String lastName;
 *     
 *    {@literal @}CharField(max_length = 30, required = true, unique = true)
 *     private String email;
 *     
 *     public static Manager objects = new Manager(Author.class);
 *     
 *     // Constructors
 *     public Author() {}
 *     
 *     public Author(String firstName, String email) {
 *         this.firstName = firstName;
 *         this.email = email;
 *         
 *     }
 *     
 *     public Author(String firstName, String lastName, String email) {
 *         this(firstName, email);
 *         this.lastName = lastName;
 *     }
 *     
 *     // Getters
 *     public String getFirstName() {
 *         return firstName;
 *     }
 *     
 *     public String getLastName() {
           return lastName;
 *     }
 *     
 *     public String getEmail() {
 *         return email;
 *     }
 *     
 *     // Setters
 *     public void setFirstName(String firstName) {
 *         this.firstName = firstName;
 *     }
 *     
 *     public void setLastName(String lastName) {
 *         this.lastName = lastName;
 *     }
 *     
 *     public void setEmail(String email) {
 *         this.email = email;
 *     }
 *     
 *     // Generated by Jedi ORM
 *     public QuerySet<Book> getBookSet() {
 *         return Book.objects.getSet(Author.class, this.id);
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
 * @see jedi.db.models.QuerySet
 * 
 * @see app.models.Book
 */
public class Author extends Model {	
	
    // Attributes
	
    /**
     * Número de versão da classe.
     * 
     * @see java.lang.Long
     */
    private static final long serialVersionUID = -8520333625577424268L;

    /** 
     * Nome do autor.
  	 *
     * @see java.lang.String
     * 
     * @see jedi.db.models.CharField
     */
    @CharField(max_length = 30)
    private String firstName;

    /** 
     * Sobrenome do autor.
     * 
     * @see java.lang.String
     * 
     * @see jedi.db.models.CharField
     */
    @CharField(max_length = 30, required = false)
    private String lastName;

    /**
     * E-mail do autor.
     *  
     * @see java.lang.String
     * 
     * @see jedi.db.models.CharField
     */
    @CharField(max_length = 30, required = true, unique = true)
    private String email;

    /** 
     * Gerenciador de consultas a objetos dessa classe 
     * persistidos em banco de dados.
     * 
     * @see jedi.db.models.Manager
     * 
     * @see app.models.Author
     */
    public static Manager objects = new Manager(Author.class);
    
    // Constructors
    
    /**
     * Construtor padrão.
     */
    public Author() {}
    
    /**
     * Construtor que recebe o nome e o e-mail do autor.
     * 
     * @param firstName o nome do autor
     * @param email o e-mail do autor
     * 
     * @see java.lang.String
     */
    public Author(String firstName, String email) {
        this.firstName = firstName;
        this.email = email;
    }
    
    /**
     * Construtor que recebe o nome, o sobrenome e o e-mail do autor.
     * 
     * @param firstName o nome do autor
     * @param lastName o sobrenome do autor
     * @param email o e-mail do autor
     * 
     * @see java.lang.String
     */
    public Author(String firstName, String lastName, String email) {
        this(firstName, email);
        this.lastName = lastName;
    }
    
    // Getters
    
    /**
     * Retorna o nome do autor.
     *
     * @return o nome do autor
     * 
     * @see java.lang.String
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Retorna o sobrenome do autor.
     *
     * @return o sobrenome do autor
     * 
     * @see java.lang.String
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Retorna o e-mail do autor.
     * 
     * @return o e-mail do autor
     * 
     * @see java.lang.String
     */
    public String getEmail() {
        return email;
    }
    
    // Setters    
    
    /**
     * Configura o nome do autor.
     * 
     * @param firstName o nome do autor
     * 
     * @see java.lang.String
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Configura o sobrenome do autor.
     * 
     * @param lastName o sobrenome do autor
     * 
     * @see java.lang.String
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Configura o e-mail do autor.
     * 
     * @param email o e-mail do autor
     * 
     * @see java.lang.String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    // Generated by Jedi ORM
    /**
     * Retorna os livros escritos pelo autor.
     * 
     * @return os livros escritos pelo autor
     * 
     * @see app.models.Book
     */
    public QuerySet<Book> getBookSet() {
        return Book.objects.getSet(Author.class, this.id);
    }
}