/***********************************************************************************************
 * @(#)IPAddressField.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/01/23
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

import jedi.db.validators.IPAddressFieldValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IPAddressField {
	public int max_length() default 15;
    public String comment() default "";
    public boolean primary_key() default false;
    public boolean required() default true;
    public boolean unique() default false;
    public boolean blank() default false;
    public String[] choices() default "";
    public String db_column() default "";
    public boolean db_index() default false;
    public String db_tablespace() default "";
    public String default_value() default "\\0";
    public boolean editable() default true;
    public String error_messages() default "";
    public String help_text() default "";
    public String unique_for_date() default "";
    public String unique_for_month() default "";
    public String unique_for_year() default "";
    public String verbose_name() default "";
    public Class<? extends IPAddressFieldValidator> validator() default IPAddressFieldValidator.class;
    public String error_message() default "";
}