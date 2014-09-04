/***********************************************************************************************
 * @(#)Membership.java
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

import java.util.Date;

import jedi.db.models.CharField;
import jedi.db.models.DateField;
import jedi.db.models.ForeignKeyField;
import jedi.db.models.Manager;
import jedi.db.models.Model;

/**
 * <h2>
 * Classe que modela a adesão de uma pessoa a um grupo.
 * </h2>
 * <h3>Código:</h3>
 * <pre>
 * <code class="java">
 * package app.models;
 * 
 * import java.util.Date;
 * 
 * import jedi.db.models.CharField;
 * import jedi.db.models.DateField;
 * import jedi.db.models.ForeignKeyField;
 * import jedi.db.models.Manager;
 * import jedi.db.models.Model;
 * 
 * public class Membership extends Model {
 * 
 *     // Attributes
 *     private static final long serialVersionUID = -1658988303242454439L;
 *     
 *    {@literal @}ForeignKeyField
 *     private Person person;
 *
 *    {@literal @}ForeignKeyField
 *     private Group group;
 *     
 *    {@literal @}DateField
 *     private Date dateJoined;
 *     
 *    {@literal @}CharField(max_length = 64)
 *     private String inviteReason;
 *     
 *     public static Manager objects = new Manager(Membership.class);
 *     
 *     // Getters
 *     public Person getPerson() {
 *         return person;
 *     }
 *     
 *     public Group getGroup() {
 *         return group;
 *     }
 *     
 *     public Date getDateJoined() {
 *         return dateJoined;
 *     }
 *     
 *     public String getInviteReason() {
 *         return inviteReason;
 *     }
 *     
 *     // Setters
 *     public void setPerson(Person person) {
 *         this.person = person;
 *     }
 *     
 *     public void setGroup(Group group) {
 *         this.group = group;
 *     }
 *     
 *     public void setDateJoined(Date dateJoined) {
 *         this.dateJoined = dateJoined;
 *     }
 *     
 *     public void setInviteReason(String inviteReason) {
 *         this.inviteReason = inviteReason;
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
 * @see java.util.Date
 * 
 * @see jedi.db.models.CharField
 * @see jedi.db.models.DateField
 * @see jedi.db.models.ForeignKeyField
 * @see jedi.db.models.Manager
 * @see jedi.db.models.Model
 * 
 * @see app.models.Person
 * @see app.models.Group
 */
public class Membership extends Model {
	
	// Attributes
	
	/**
	 * Número de versão da classe.
	 * 
	 * @see java.lang.Long
	 */
	private static final long serialVersionUID = -1658988303242454439L;
	
	/**
	 * Pessoa que irá aderir ao grupo.
	 * 
	 * @see jedi.db.models.ForeignKeyField
	 * 
	 * @see app.models.Person
	 */
	@ForeignKeyField
	private Person person;
	
	/**
	 * Grupo a aderir.
	 * 
	 * @see jedi.db.models.ForeignKeyField
	 * 
	 * @see app.models.Group
	 */
	@ForeignKeyField
	private Group group;

	/**
	 * Date da adesão.
	 * 
	 * @see java.util.Date
	 * 
	 * @see jedi.db.models.DateField
	 */
	@DateField
	private Date dateJoined;
	
	/**
	 * Razão do convite.
	 * 
	 * @see java.lang.String
	 * 
	 * @see jedi.db.models.CharField
	 */
	@CharField(max_length = 64)
	private String inviteReason;
	
	/** 
	 * Gerenciador de consultas a objetos dessa 
	 * classe persistidos em banco de dados.
	 * 
	 * @see jedi.db.models.Manager 
	 */
	public static Manager objects = new Manager(Membership.class);

	// Getters
	
	/**
	 * Retorna a pessoa que aderiu ao grupo.
	 * 
	 * @return a pessoa que aderiu ao grupo
	 * 
	 * @see app.models.Person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * Retorna o grupo aderido pela pessoa.
	 * 
	 * @return o grupo aderido pela pessoa
	 * 
	 * @see app.models.Group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Retorna a data de adesão ao grupo.
	 * 
	 * @return a data de adesão ao grupo
	 * 
	 * @see java.util.Date
	 */
	public Date getDateJoined() {
		return dateJoined;
	}

	/**
	 * Retorna a razão do convite.
	 * 
	 * @return a razão do convite
	 * 
	 * @see java.lang.String
	 */
	public String getInviteReason() {
		return inviteReason;
	}
	
	// Setters
	
	/**
	 * Define a pessoa que está aderindo ao grupo.
	 * 
	 * @param person a pessoa que está aderindo ao grupo
	 * 
	 * @see app.models.Person
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
	
	/**
	 * Define o grupo a ser aderido.
	 * 
	 * @param group o grupo a ser aderido
	 * 
	 * @see app.models.Group
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
	
	/**
	 * Define a data de adesão ao grupo.
	 * 
	 * @param dateJoined data de adesão ao grupo
	 * 
	 * @see java.util.Date
	 */
	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	/**
	 * Define a razão do convite.
	 * 
	 * @param inviteReason razão do convite
	 * 
	 * @see java.lang.String
	 */
	public void setInviteReason(String inviteReason) {
		this.inviteReason = inviteReason;
	}	
}