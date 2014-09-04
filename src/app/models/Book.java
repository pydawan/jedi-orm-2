/***********************************************************************************************
 * @(#)Book.java
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
import jedi.db.models.ForeignKeyField;
import jedi.db.models.Manager;
import jedi.db.models.ManyToManyField;
import jedi.db.models.Model;
import jedi.db.models.QuerySet;
import jedi.db.models.Table;

/**
 * <h2>Classe que modela um livro.</h2>
 * <h3>Código:</h3>
 * <pre>
 * <code class="java">
 * package app.models;
 * 
 * import jedi.db.models.CharField;
 * import jedi.db.models.ForeignKeyField;
 * import jedi.db.models.Manager;
 * import jedi.db.models.ManyToManyField;
 * import jedi.db.models.Model;
 * import jedi.db.models.QuerySet;
 * import jedi.db.models.Table;
 * 
 *{@literal @}Table(name = "books", engine = "InnoDB", charset = "utf8", comment = "Table of books")
 * public class Book extends Model {
 * 
 *     // Atributes
 *     private static final long serialVersionUID = 9076408430303339094L;
 *     
 *    {@literal @}CharField(max_length = 30, required = true, unique = true, comment = "This field stores the book\\'s title.")
 *     private String title;
 *     
 *    {@literal @}ManyToManyField
 *    {@code private QuerySet<Author> authors;}
 *    
 *    {@literal @}ForeignKeyField
 *     private Publisher publisher;
 *	   
 *    {@literal @}CharField(max_length = 15, required = true)
 *     private String publicationDate;
 *
 *     public static Manager objects = new Manager(Book.class);
 *
 *     // Constructors
 *    {@code public Book() {
 *         authors = new QuerySet<Author>();
 *         publisher = new Publisher();
 *     }}
 *     
 *    {@code public Book(String title, QuerySet<Author> authors, String publicationDate) {    
 *         this();
 *         this.title = title;
 *         this.authors = authors;
 *         this.publicationDate = publicationDate;
 *     }}
 *     
 *     // Getters
 *     public String getTitle() {
 *         return title;
 *     }
 *     
 *    {@code public QuerySet<Author> getAuthors() {
 *         return authors;
 *     }}
 *     
 *     public Publisher getPublisher() {
 *         return publisher;
 *     }
 *     
 *     public String getPublicationDate() {
 *         return publicationDate;
 *     }
 *     
 *     // Setters
 *     public void setTitle(String title) {
 *         this.title = title;
 *     }
 *     
 *    {@code public void setAuthors(QuerySet<Author> authors) {
 *         this.authors = authors;
 *     }}
 *     
 *     public void setPublisher(Publisher publisher) {
 *         this.publisher = publisher;
 *     }
 *     
 *     public void setPublicationDate(String publicationDate) {
 *         this.publicationDate = publicationDate;
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
 * @see jedi.db.models.ForeignKeyField
 * @see jedi.db.models.Manager
 * @see jedi.db.models.ManyToManyField
 * @see jedi.db.models.Model
 * @see jedi.db.models.Table 
 */
@Table(name = "books", engine = "InnoDB", charset = "utf8", comment = "Table of books")
public class Book extends Model {
	
	// Atributes
	
	/** 
	 * Número de versão da classe.
	 * 
	 * @see java.lang.Long
	 */
	private static final long serialVersionUID = 9076408430303339094L;
	
    /** 
     * Título do livro.
     * 
     * @see java.lang.String 
     * 
     * @see jedi.db.models.CharField
     */
	@CharField(max_length = 30, required = true, unique = true, comment = "This field stores the book\\'s title.")
	private String title;

	/**
	 * Autores do livro.
	 * 
	 * @see jedi.db.models.ManyToManyField
	 * @see jedi.db.models.QuerySet 
	 * 
	 * @see app.models.Author
	 */
	@ManyToManyField
	private QuerySet<Author> authors;

	/**
	 * Editora do livro.
	 * 
	 * @see jedi.db.models.ForeignKeyField
	 * 
	 * @see app.models.Publisher
	 */
	@ForeignKeyField
	private Publisher publisher;

	/**
	 * Data de publicação do livro.
	 *
	 * @see java.lang.String
	 * 
	 * @see jedi.db.models.CharField
	 */
	@CharField(max_length = 15, required = true)
	private String publicationDate;

	/** 
	 * Gerenciador de consultas a objetos dessa classe persistidos 
	 * em banco de dados.
	 * 
	 * @see jedi.db.models.Manager
	 * 
	 * @see app.models.Book
	 */
	public static Manager objects = new Manager(Book.class);

	// Constructors
	
	/**
	 * Construtor padrão.
	 */
	public Book() {
		authors = new QuerySet<Author>();
		publisher = new Publisher();
	}

	/**
	 * Construtor que recebe o título, os autores e a 
	 * data de publicação do livro.
	 *  
	 * @param title o título do livro
	 * @param authors os autores do livro
	 * @param publicationDate a data de publicação do livro
	 * 
	 * @see java.lang.String
	 * 
	 * @see jedi.db.models.QuerySet
	 * 
	 * @see app.models.Author
	 */
	public Book(String title, QuerySet<Author> authors, String publicationDate) {
		this();
		this.title = title;
		this.authors = authors;
		this.publicationDate = publicationDate;
	}

	// Getters
	
	/**
	 * Retorna o título do livro.
	 * 
	 * @return o título do livro
	 * 
	 * @see java.lang.String
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Retorna os autores do livro.
	 * 
	 * @return os autores do livro
	 * 
	 * @see jedi.db.models.QuerySet
	 * 
	 * @see app.models.Author
	 */
	public QuerySet<Author> getAuthors() {
		return authors;
	}

	/**
	 * Retorna a editora do livro.
	 * 
	 * @return a editora do livro
	 * 
	 * @see app.models.Publisher
	 */
	public Publisher getPublisher() {
		return publisher;
	}

	/**
	 * Retorna a data de publicação do livro.
	 * 
	 * @return a data de publicação do livro
	 * 
	 * @see java.lang.String
	 */
	public String getPublicationDate() {
		return publicationDate;
	}

	// Setters
	
	/**
	 * Define o título do livro.
	 * 
	 * @param title o título do livro
	 * 
	 * @see java.lang.String
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Define os autores do livro.
	 *  
	 * @param authors os autores do livro
	 * 
	 * @see jedi.db.models.QuerySet
	 * 
	 * @see app.models.Author
	 */
	public void setAuthors(QuerySet<Author> authors) {
		this.authors = authors;
	}

	/**
	 * Define a editora do livro.
	 *  
	 * @param publisher a editora do livro
	 * 
	 * @see app.models.Publisher
	 */
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	/**
	 * Define a data de publicação do livro.
	 * 
	 * @param publicationDate a data de publicação do livro
	 * 
	 * @see java.lang.String
	 */
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
}
