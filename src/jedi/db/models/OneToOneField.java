/***********************************************************************************************
 * @(#)OneToOneField.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/09/02
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

package jedi.db.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jedi.db.CascadeType;
import jedi.db.FetchType;
import jedi.db.Models;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOneField {
	public Class<? extends Model> model() default Model.class; 
    public String column_name() default "";
    public String constraint_name() default "";
    public String references() default "";
    public String referenced_column() default "";
    public String comment() default "";
    public String default_value() default "\0";
    public boolean unique() default true;
    public boolean required() default true;
    public Models on_delete() default Models.CASCADE;
    public Models on_update() default Models.CASCADE;
    public CascadeType cascade_type() default CascadeType.ALL;
    public FetchType fetch_type() default FetchType.EAGER;
}