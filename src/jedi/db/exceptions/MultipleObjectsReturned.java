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
 * Exceção lançada quando mais de um objeto é retornado por uma consulta no banco de dados.
 *
 */

public class MultipleObjectsReturned extends Exception {

// 	Atributos
	
	private static final long serialVersionUID = 271849121410861140L;
	
//	Construtores
	
	public MultipleObjectsReturned() {
		super();
	}

	public MultipleObjectsReturned(String message) {
		super(message);
	}
	
	public MultipleObjectsReturned(Throwable cause) {
		super(cause);
	}
	
	public MultipleObjectsReturned(String message, Throwable cause) {
		super(message, cause);
	}
}
