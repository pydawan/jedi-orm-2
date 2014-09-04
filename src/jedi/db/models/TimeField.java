/***********************************************************************************************
 * @(#)TimeField.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/03/05
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

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeField {
    public boolean auto_now() default false;
    public boolean auto_now_add() default false;
    public String comment() default "";
    public boolean required() default true;
    public boolean unique() default false;
    public String db_column() default "";
    public boolean db_index() default false;
    public String db_tablespace() default "";
    public String default_value() default "";
    public boolean editable() default true;
    public String error_messages() default "";
    public String help_text() default "";
    public String unique_for_date() default "";
    public String unique_for_month() default "";
    public String unique_for_year() default "";
    public String verbose_name() default ""; 
}