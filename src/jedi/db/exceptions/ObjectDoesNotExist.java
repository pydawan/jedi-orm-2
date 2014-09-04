/*******************************************************************************
 * Copyright (c) 2014 Thiago Alexandre Martins Monteiro.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Thiago Alexandre Martins Monteiro - initial API and implementation
 ******************************************************************************/
package jedi.db.exceptions;

/**
 * 
 * @author Thiago Alexandre Martins Monteiro
 *
 * Exceção que é lançada quando nenhum objeto é retornado por uma consulta no banco de dados.
 */
public class ObjectDoesNotExist extends Exception {

// Atributos
	
	private static final long serialVersionUID = 5830724599642403525L;
 
// Construtores
	
	public ObjectDoesNotExist() { 
		super(); 
	}
	  
	public ObjectDoesNotExist(String message) { 
		super(message); 
	}
  
	public ObjectDoesNotExist(String message, Throwable cause) { 
		super(message, cause); 
	}
	 
	public ObjectDoesNotExist(Throwable cause) { 
		super(cause); 
	}

}
