/***********************************************************************************************
 * @(#)QuerySet.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/03/04
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import jedi.types.Block;
import jedi.types.Function;

/**
 * Classe que representa uma lista de objetos retornados do banco de dados ou
 * armazenados na memória. QuerySet possui uma API praticamente igual para
 * listas
 * de objetos persistentens ou transientes.
 * 
 * @author Thiago Alexandre Martins Monteiro
 * @param <T>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class QuerySet<T extends Model> extends ArrayList<T> {
	
	private static final long serialVersionUID = 1071905522184893192L;
    private Class<T> entity = null;
    private int offset = 0;
    private transient boolean isPersited;

    public QuerySet() {}

    public QuerySet(Class<T> entity) {
        this.entity = entity;
    }

    public QuerySet(Collection<T> collection) {
        super(collection);
    }

    // Getters
    public Class<T> getEntity() {
        return entity;
    }

    public Class<T> entity() {
        return entity;
    }

    public boolean isPersited() {
        return this.isPersited;
    }

    // Setters
    public void setEntity(Class entity) {
        this.entity = entity;
    }

    public void entity(Class entity) {
        this.entity = entity;
    }

    public QuerySet<T> isPersisted(boolean isPersisted) {
        this.isPersited = isPersisted;
        return this;
    }

    // orderBy
    public QuerySet orderBy(String field) {
        QuerySet orderedList = new QuerySet();
        orderedList.setEntity(this.entity);
        if (field != null && !field.equals("") && !this.isEmpty()) {
            Comparator comparator = null;
            try {
                // As variáveis abaixo tem modificador final para serem
                // acessadas nas classes internas.
                final String fld = field.replace("-", "");
                final String fld2 = field;
                Field f = null;
                if (field.equals("id") || field.equals("-id")) {
                    f = this.entity.getSuperclass().getDeclaredField("id");
                } else {
                    f = this.entity.getDeclaredField(fld);
                }
                f.setAccessible(true);
                if (f != null) {
                    if (field.equals("id")) {
                        comparator = new Comparator<Model>() {
                            public int compare(Model m1, Model m2) {
                                if (m1.getId() < m2.getId()) {
                                    return -1;
                                }
                                if (m1.getId() > m2.getId()) {
                                    return 1;
                                }
                                return 0;
                            }
                        };
                    } else if (field.equals("-id")) {
                        comparator = new Comparator<Model>() {
                            public int compare(Model m1, Model m2) {
                                if (m1.getId() < m2.getId()) {
                                    return 1;
                                }
                                if (m1.getId() > m2.getId()) {
                                    return -1;
                                }
                                return 0;
                            }
                        };
                    }
                    if (f.getType().getName().equals("java.lang.String")) {
                        comparator = new Comparator<Model>() {
                            public int compare(Model m1, Model m2) {
                                int result = 0;
                                try {
                                    Field f1 = m1.getClass().getDeclaredField(fld);
                                    f1.setAccessible(true);
                                    Field f2 = m2.getClass().getDeclaredField(fld);
                                    f2.setAccessible(true);
                                    if (fld2.startsWith("-")) {
                                        result = ((String) f2.get(m2)).compareTo((String) f1.get(m1));
                                    } else {
                                        result = ((String) f1.get(m1)).compareTo((String) f2.get(m2));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return result;
                            }
                        };
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Collections.sort(this, comparator);
            orderedList = this;
        }
        return orderedList;
    }

    public QuerySet limit(int... params) {
        QuerySet objs = new QuerySet();
        objs.setEntity(entity);
        if (!this.isEmpty() && params != null) {
            int start = 0;
            int end = 0;
            // Se for um argumento:
            // params[0] - limit
            if (params.length == 1) {
                if (this.offset > 0) {
                    start = this.offset;
                    // Reconfigurando a memória de deslocamento.
                    this.offset = 0;
                } else {
                    start = 0;
                }
                end = start + params[0];
            }
            // Se forem dois argumentos:
            // params[0] - offset
            // params[1] - limit
            if (params.length == 2) {
                start = params[0];
                end = params[0] + params[1];
            }
            if (end > this.size()) {
                end = this.size();
            }
            for (int i = start; i < end; i++) {
                objs.add(this.get(i));
            }
        }
        return objs;
    }

    public QuerySet offset(int offset) {
        QuerySet records = new QuerySet();
        records.setEntity(this.entity);
        this.offset = offset;
        // Verificando se a lista é vazia.
        if (!this.isEmpty()) {
            for (int i = offset; i < this.size(); i++) {
                records.add(this.get(i));
            }
        }
        return records;
    }

    public QuerySet save() {
        if (!this.isEmpty()) {
            boolean autoCloseConnection;
            for (Object o : this) {            	
                Model model = (Model) o;
                autoCloseConnection = model.autoCloseConnection();
                model.autoCloseConnection(false);
                model.save();
                model.autoCloseConnection(autoCloseConnection);
            }
            // Informando que a lista foi persistida.
            this.isPersisted(true);
        }
        return this;
    }

    public QuerySet delete() {
        if (!this.isEmpty()) {
            Model model;
            for (Object o : this) {
                model = (Model) o;
                // Desabilitando o fechamento automático da conexão após
                // cada operação no banco de dados.
                model.autoCloseConnection(false);
                model.delete();
            }
            // Informando que a lista não se encontra persistida no banco de
            // dados.
            this.isPersisted(false);
            this.removeAll(this);
        }    
        return this;
    }

    // Tem que tratar update para poder atualizar atributos que armazenam
    // referências.
    // Por exemplo: ufs.update("pais", paises.filter("nome", "Brasil") );
    public QuerySet update(String... args) {
        if (!this.isEmpty()) {
            boolean autoCloseConnection;
            for (Object o : this) {
                Model model = (Model) o;
                autoCloseConnection = model.autoCloseConnection();
                model.autoCloseConnection(false);
                model.update(args);
                model.autoCloseConnection(autoCloseConnection);
            }
        }
        return this;
    }

    public int count() {
        return this.size();
    }

    public QuerySet all() {
        QuerySet querySet = new QuerySet();
        querySet.setEntity(this.entity);
        for (int i = 0; i < this.size(); i++) {
            querySet.add(this.get(i));
        }
        return querySet;
    }

    private QuerySet<T> in(String query) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        try {
            if (query != null && !query.trim().isEmpty()) {
                query = query.replace("__in", "");
                // query = query.replace("', ", "',");
                query = query.replaceAll("',\\s+", "',");
                query = query.replaceAll("[\\[\\]]", "");
                String[] queryComponents = query.split("=");
                if (queryComponents != null && queryComponents.length > 0) {
                    String fieldName = queryComponents[0].trim();
                    String[] fieldValues = queryComponents[1].split(",");
                    Field field = null;
                    if (fieldName.equalsIgnoreCase("id")) {
                        field = this.entity.getSuperclass().getDeclaredField(fieldName);
                    } else {
                        field = this.entity.getDeclaredField(fieldName);
                    }
                    field.setAccessible(true);
                    for (T model : this) {
                        for (String fieldValue : fieldValues) {
                            if (field.get(model) != null && field.get(model).toString().equals(fieldValue.replaceAll("'(.*)'", "$1"))) {
                                querySet.add(model);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return querySet;
    }

    private QuerySet<T> range(String query) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        try {
            if (query != null && !query.trim().isEmpty()) {
                query = query.replace("__range", "");
                query = query.replace(", ", ",");
                query = query.replaceAll("['\\[\\]]", "");
                String[] queryComponents = query.split("=");
                if (queryComponents != null && queryComponents.length > 0) {
                    String fieldName = queryComponents[0];
                    String[] fieldValues = queryComponents[1].split(",");
                    Field field = null;
                    if (fieldName.trim().equalsIgnoreCase("id")) {
                        field = this.entity.getSuperclass().getDeclaredField(fieldName);
                    } else {
                        field = this.entity.getDeclaredField(fieldName);
                    }
                    field.setAccessible(true);
                    for (T model : this) {
                        for (int fieldValue = Integer.parseInt(fieldValues[0]); fieldValue <= Integer.parseInt(fieldValues[1]); fieldValue++) {
                            if (field.get(model).equals(fieldValue)) {
                                querySet.add(model);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return querySet;
    }

    private QuerySet<T> filterNumericField(String query) {
        QuerySet<T> querySet = new QuerySet();
        querySet.setEntity(this.entity);
        if (query != null && !query.trim().isEmpty()) {
            query = query.trim().toLowerCase();
            // (<|<=|==|!=|>=|>)
            String[] queryComponents = query.split("\\s+");
            String fieldName = queryComponents[0].trim();
            String operator = queryComponents[1].trim();
            String fieldValue = queryComponents[2].trim();
            // System.out.printf("%s%s%s", field_name, operator, field_value);
            Field field = null;
            try {
                if (fieldName.equals("id")) {
                    field = this.entity.getSuperclass().getDeclaredField(fieldName);
                } else {
                    field = this.entity.getDeclaredField(fieldName);
                }
                field.setAccessible(true);
                for (T model : this) {
                    if (operator.equals("<")) {
                        if (field.getDouble(model) < Double.parseDouble(fieldValue)) {
                            querySet.add(model);
                        }
                    } else if (operator.equals("<=")) {
                        if (field.getDouble(model) <= Double.parseDouble(fieldValue)) {
                            querySet.add(model);
                        }
                    } else if (operator.equals("=")) {
                        if (field.getDouble(model) == Double.parseDouble(fieldValue)) {
                            querySet.add(model);
                        }
                    } else if (operator.equals("!=")) {
                        if (field.getDouble(model) != Double.parseDouble(fieldValue)) {
                            querySet.add(model);
                        }
                    } else if (operator.equals(">")) {
                        if (field.getDouble(model) > Double.parseDouble(fieldValue)) {
                            querySet.add(model);
                        }
                    } else if (operator.equals(">=")) {
                        if (field.getDouble(model) >= Double.parseDouble(fieldValue)) {
                            querySet.add(model);
                        }
                    } else {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return querySet;
    }

    private QuerySet<T> exact(String query) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        if (query != null && !query.trim().isEmpty()) {
            query = query.replace("__exact", "");
            String[] queryComponents = query.split("=");
            String fieldName = queryComponents[0];
            String fieldValue = queryComponents[1];
            if (fieldValue.equalsIgnoreCase("null")) {
                querySet.add(this.isNull(String.format("%s__isnull=true", fieldName)));
            } else {
                querySet.add(this.in(String.format("%s__in=[%s]", fieldName, fieldValue)));
            }
        }
        return querySet;
    }

    private QuerySet<T> isNull(String query) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        if (query != null && !query.trim().isEmpty()) {
            query = query.trim().toLowerCase();
            query = query.replace("__isnull", "");
            String[] queryComponents = query.split("=");
            String fieldName = queryComponents[0];
            boolean isNull = Boolean.parseBoolean(queryComponents[1]);
            Field field = null;
            try {
                if (fieldName.equalsIgnoreCase("id")) {
                    field = this.entity.getSuperclass().getDeclaredField(fieldName);
                } else {
                    field = this.entity.getDeclaredField(fieldName);
                }
                field.setAccessible(true);
                for (T model : this) {
                    if (isNull) {
                        if (field.get(model) == null) {
                            querySet.add(model);
                        }
                    } else {
                        if (field.get(model) != null) {
                            querySet.add(model);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return querySet;
    }

    public QuerySet<T> startsWith(String query) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        if (query != null && !query.isEmpty()) {
            query = query.replace("__startswith", "");
            String[] queryComponents = query.split("=");
            String fieldName = queryComponents[0];
            String fieldValue = queryComponents[1];
            Field field = null;
            try {
                if (fieldName.equals("id")) {
                    field = this.entity.getSuperclass().getDeclaredField(fieldName);
                } else {
                    field = this.entity.getDeclaredField(fieldName);
                }
                field.setAccessible(true);
                String pattern = String.format("^%s.*$", fieldValue.replace("'", ""));

                for (T model : this) {
                    if (field.get(model) != null && field.get(model).toString().matches(pattern)) {
                        querySet.add(model);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return querySet;
    }

    public QuerySet<T> endsWith(String query) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        if (query != null && !query.isEmpty()) {
            query = query.replace("__endswith", "");
            String[] queryComponents = query.split("=");
            String fieldName = queryComponents[0];
            String fieldValue = queryComponents[1];
            Field field = null;
            try {
                if (fieldName.equals("id")) {
                    field = this.entity.getSuperclass().getDeclaredField(fieldName);
                } else {
                    field = this.entity.getDeclaredField(fieldName);
                }
                field.setAccessible(true);
                String pattern = String.format("^.*%s$", fieldValue.replace("'", ""));

                for (T model : this) {
                    if (field.get(model) != null && field.get(model).toString().matches(pattern)) {
                        querySet.add(model);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return querySet;
    }

    public QuerySet<T> contains(String query) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        if (query != null && !query.isEmpty()) {
            query = query.replace("__contains", "");
            String[] queryComponents = query.split("=");
            String fieldName = queryComponents[0];
            String fieldValue = queryComponents[1];
            Field field = null;
            try {
                if (fieldName.equals("id")) {
                    field = this.entity.getSuperclass().getDeclaredField(fieldName);
                } else {
                    field = this.entity.getDeclaredField(fieldName);
                }
                field.setAccessible(true);
                String pattern = String.format("^.*%s.*$", fieldValue.replace("'", ""));

                for (T model : this) {
                    if (field.get(model) != null && field.get(model).toString().matches(pattern)) {
                        querySet.add(model);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return querySet;
    }

    public QuerySet<T> filter(String... queries) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        if (queries != null && !queries.toString().trim().isEmpty()) {
            for (String query : queries) {
                query = query.trim();
                // Retirando o excesso de espaços ao redor do operador =
                query = query.replaceAll("\\s*=\\s*", "=");
                // System.out.println(query);
                // Tratamento para consulta field__in=['valor1', 'valor2']
                if (query.matches("^\\w+__in=\\[(\\d+|\\d+((,|, )\\d+)+)\\]$")) {
                    query = query.replaceAll("(\\d+)", "'$1'");
                    querySet.add(this.in(query));
                } else if (query.matches("^\\w+__in=\\[('[^']+'|'[^']+'((,|, )'[^']+')+)\\]$")) {
                    // System.out.println("CORRESPONDE A REGEX DO field__in=[]");
                    querySet.add(this.in(query));
                } else if (query.matches("^\\w+__range=\\[\\d+(,|, )\\d+\\]$")) {
                    // System.out.println("CORRESPONDE A REGEX field__range[]");
                    querySet.add(this.range(query));
                } else if (query.matches("^\\s*\\w+\\s+(<|<=|==|!=|>=|>)\\s+\\d+\\s*$")) {
                    // Regex para string id <= 10 and id > 50 or id == 100 ...
                    // "^\\s*\\w+\\s+(<|<=|==|!=|>=|>)\\s+\\d+(\\s+(and|or|AND|OR)\\s+\\w+\\s+(<|<=|==|!=|>=|>)\\s+\\d+)*\\s*$"
                    // System.out.println("CORRESPONDE A REGEX PARA NÚMEROS");
                    // id > 10 and id < 50
                    // id >= 12 or id <= 50 and id > 100
                    // id >= 12 or,id <= 50 and,id > 100
                    // Retirando os espaços em brancos das extremidades da
                    // string de consulta.
                    querySet.add(this.filterNumericField(query));
                } else if (query.matches("^(\\w+)__isnull\\s*=\\s*(true|false)\\s*$")) {
                    // query.matches("^(\\w+)__isnull\\s*=\\s*([tT][rR][uU][eE])\\s*$"
                    querySet.add(this.isNull(query));
                } else if (query.matches("^(\\w+)__exact\\s*=\\s*('[^']+'|\\d+|null)\\s*$")) {
                    querySet.add(this.exact(query));
                } else if (query.matches("^(\\w+)__(lt|lte|gt|gte)\\s*=\\s*(\\d+)$")) {
                    // "^(\\w+)__(lt|lte|gt|gte|exact)\\s*=\\s*(\\d+)$"
                    // query = query.replaceAll("__exact=", " = ");
                    query = query.replace("__lt=", " < ");
                    query = query.replace("__lte=", " <= ");
                    query = query.replace("__gt=", " > ");
                    query = query.replace("__gte=", " >= ");
                    querySet = this.filterNumericField(query);
                } else if (query.matches("^(\\w+)__startswith\\s*=\\s*('[^']+'|\\d+)$")) {
                    querySet = this.startsWith(query);
                } else if (query.matches("^(\\w+)__endswith\\s*=\\s*('[^']+'|\\d+)$")) {
                    querySet = this.endsWith(query);
                } else if (query.matches("^(\\w+)__contains\\s*=\\s*('[^']+'|\\d+)$")) {
                    querySet = this.contains(query);
                } else {
                }
            }
            querySet.entity(this.entity);
        }
        return querySet;
    }

    // Funciona como o filter negado.
    public QuerySet<T> exclude(String... queries) {
        QuerySet<T> querySet = this.all();
        querySet.entity(this.entity);
        querySet = querySet.remove(querySet.filter(queries));
        return querySet;
    }

    // O código desse método entrou em conflito com o método save no
    // Manager.java
    // uma vez que ao inserir elementos na QuerySet eles tem seu id definido e o
    // método save só insere
    // models com o id igual a 0.
    // Esse conflito foi solucionado através do atributo is_persisted.
    public boolean add(T model) {
        if (model != null && model.id() == 0) {
            model.id(this.size() + 1);
        }
        return super.add(model);
    }

    public QuerySet<T> add(QuerySet<T> querySet) {
        if (querySet != null && !querySet.isEmpty()) {
            querySet.entity(this.entity());
            this.addAll(querySet);
        }
        return this;
    }

    public QuerySet<T> add(QuerySet<T>... querySets) {
        if (querySets != null && querySets.length > 0) {
            for (QuerySet<T> querySet : querySets) {
                querySet.entity(this.entity());
                this.add(querySet);
            }
        }
        return this;
    }

    public QuerySet<T> add(T... models) {
        // Verificando se o array de modelos passada existe e não está vazia.
        if (models != null && models.length > 0) {
            // Percorrendo cada modelo do array.
            for (T model : models) {
                this.add(model);
            }
        }
        return this;
    }

    public QuerySet<T> add(List<T> models) {
        if (models != null && models.size() > 0) {
            for (T model : models) {
                this.add(model);
            }
        }
        return this;
    }

    public QuerySet<T> remove(QuerySet<T> querySet) {
        if (querySet != null && !querySet.isEmpty()) {
            this.removeAll(querySet);
        }
        return this;
    }

    public QuerySet<T> remove(QuerySet<T>... querySets) {
        if (querySets != null && querySets.length > 0) {
            for (QuerySet<T> querySet : querySets) {
                this.removeAll(querySet);
            }
        }
        return this;
    }

    public QuerySet<T> remove(String... queries) {
        QuerySet<T> querySet = this.filter(queries);
        if (querySet != null && !querySet.isEmpty()) {
            this.removeAll(querySet);
        }
        return this;
    }

    public QuerySet<T> remove(String query) {
        QuerySet<T> querySet = this.filter(query);
        if (querySet != null && !querySet.isEmpty()) {
            this.removeAll(querySet);
        }
        return this;
    }

    public QuerySet<T> remove(T model) {
        if (!this.isEmpty() && model != null) {
            // Fazendo cast para Object para evitar StackOverFlowError.
            // Esse erro ocorre porque é feita uma chamada recursiva a esse
            // método e ao fazer o cast o Java
            // chamada o método desejado.
            this.remove((Object) model);
        }
        return this;
    }

    public QuerySet<T> remove(T... models) {
        if (models != null && models.length > 0) {
            for (T model : models) {
                this.remove(model);
            }
        }
        return this;
    }

    public QuerySet<T> distinct() {
        QuerySet<T> querySet = new QuerySet();
        querySet.setEntity(this.entity);
        if (!this.isEmpty()) {
            // Eliminando elementos repetidos da coleção através de HashSet.
            querySet = new QuerySet(new HashSet<T>(this));
        }
        return querySet;
    }

    public T earliest() {
        T model = null;
        if (!this.isEmpty()) {
            model = this.get(0);
        }
        return model;
    }

    public T latest() {
        T model = null;
        if (!this.isEmpty()) {
            model = this.get(this.size() - 1);
        }
        return model;
    }

    public QuerySet<T> get(String field, Object value) {
        QuerySet<T> querySet = null;
        if (!this.isEmpty()) {
            if (value instanceof String) {
                querySet = this.filter(String.format("%s__in=['%s']", field, value));
            } else {
                querySet = this.filter(String.format("%s__in=[%s]", field, value));
            }
        }
        return querySet;
    }

    // Tendo int como o tipo primitivo de value não ocorre mais o
    // StackOverFlowError
    // ocasionado pela chamada recursiva gerada pelo polimorfismo.
    // Integeger -> Object.
    public T get(String id, int value) {
        T model = null;
        QuerySet<T> querySet = null;
        if (!this.isEmpty()) {
            querySet = this.get("id", new Integer(value));
            model = querySet != null && !querySet.isEmpty() ? querySet.get(0) : null;
        }
        return model;
    }

    public boolean exists() {
        if (!this.isEmpty()) {
            return true;
        }
        return false;
    }

    public QuerySet reverse() {
        if (!this.isEmpty()) {
            Collections.reverse(this);
            return this;
        }
        return null;
    }
    
    public String toString() {
        String string = "[";
        if (!this.isEmpty()) {
            string += "\n";
        }
        for (Model model : this) {
            string += String.format("%s,\n", model.toString(1));
        }
        if (!this.isEmpty()) {
            string = string.substring(0, string.length() - 2);
            string += "\n";
        }
        string += "]\n";
        return string;
    }

    public String toJSON() {
        String json = "[";
        if (!this.isEmpty()) {
            json += "\n";
        }
        for (Model model : this) {
            json += String.format("%s,\n", model.toJSON(1));
        }
        if (!this.isEmpty()) {
            json = json.substring(0, json.length() - 2);
            json += "\n";
        }
        json += "]\n";
        return json;
    }

    public String toXML() {
        String xml = "";
        String xmlElementOpenTag = "";
        String xmlElementCloseTag = "";
        Table tableAnnotation = (Table) this.entity.getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().trim().isEmpty()) {
            xmlElementOpenTag += String.format("<%s>", tableAnnotation.name().trim().toLowerCase());
            xmlElementCloseTag += String.format("<%s>", tableAnnotation.name().trim().toLowerCase());
        } else {
            xmlElementOpenTag += String.format("<%ss>", this.entity.getSimpleName().toLowerCase());
            xmlElementCloseTag += String.format("</%ss>", this.entity.getSimpleName().toLowerCase());
        }
        if (!this.isEmpty()) {
            for (Model model : this) {
                xml += String.format("%s\n", model.toXML(1));
            }
            xml = String.format("%s\n%s%s", xmlElementOpenTag, xml, xmlElementCloseTag);
        } else {
            xmlElementOpenTag = xmlElementOpenTag.replace(">", " />");
            xml = String.format("%s", xmlElementOpenTag);
        }
        return xml + "\n";
    }

    public String toExtenseXML() {
        String xml = "";
        String xmlElementOpenTag = "";
        String xmlElementCloseTag = "";
        Table tableAnnotation = (Table) this.entity.getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().trim().isEmpty()) {
            xmlElementOpenTag += String.format("<%s>", tableAnnotation.name().trim().toLowerCase());
            xmlElementCloseTag += String.format("<%s>", tableAnnotation.name().trim().toLowerCase());
        } else {
            xmlElementOpenTag += String.format("<%ss>", this.entity.getSimpleName().toLowerCase());
            xmlElementCloseTag += String.format("</%ss>", this.entity.getSimpleName().toLowerCase());
        }
        if (!this.isEmpty()) {
            for (Model model : this) {
                xml += String.format("%s\n", model.toExtenseXML(1));
            }
            xml = String.format("%s\n%s%s", xmlElementOpenTag, xml, xmlElementCloseTag);
        } else {
            xmlElementOpenTag = xmlElementOpenTag.replace(">", " />");
            xml = String.format("%s", xmlElementOpenTag);
        }
        return xml + "\n";
    }
    
    public String toCSV() {
    	String csv = "";
    	for (Model model : this) {
    		csv += String.format("%s\n", model.toCSV());
    	}
    	return csv + "\n";
    }

    public QuerySet append(T object) {
        if (object != null) {
            this.add(object);
        }
        return this;
    }

    public <E extends Model> QuerySet<E> as(Class<E> c) {
        return (QuerySet<E>) this;
    }

    public void each(Block block) {
        int index = 0;
        if (block != null) {
            for (T object : this) {
                block.index = index++;
                block.value = object;
                block.run();
            }
        }
    }

    public void each(Function function) {
        int index = 0;
        if (function != null) {
            for (T object : this) {
                function.index = index++;
                function.value = object;
                function.run();
            }
        }
    }

    public QuerySet<T> set(String field, Object value) {
        for (T model : this) {
            model.set(field, value);
        }
        return this;
    }

    public List<List<String>> get(String fieldNames) {
        List<List<String>> fieldsValues = null;
        if (fieldNames != null && !fieldNames.trim().isEmpty()) {
            fieldsValues = new ArrayList<List<String>>();
            String[] fields = null;
            // Apenas um atributo.
            if (fieldNames.matches("^(\\w+)$")) {
                fields = new String[]{fieldNames};
            } else if (fieldNames.matches("^[\\w+,\\s+\\w+]+$")) {
                // Mais de um atributo.
                // Criando array de fields utilizando vírgula seguida ou não de
                // espaço como separador.
                fields = fieldNames.split(",\\s+");
            } else {
            	
            }
            for (T model : this) {
                List<String> fieldValue = new ArrayList<String>();
                for (String field : fields) {
                    if (model.get(field) != null) {
                        fieldValue.add((model.get(field)).toString());
                    } else {
                        fieldValue.add((String) model.get(field));
                    }
                }
                fieldsValues.add(fieldValue);
            }
        }
        return fieldsValues;
    }

    /**
     * Método que retorna o primeiro objeto correspondente a consulta ou null.
     * 
     * @return Model
     */
    public T first() {
        T obj = null;
        // Verificando se a lista não é vazia.
        if (!this.isEmpty()) {
            // Ordenando a lista em ordem crescente pela chave primária.
            this.orderBy("id");
            // Referenciando o primeiro item da lista.
            obj = this.get(0);
        }
        return obj;
    }

    /**
     * Método que retorna o último objeto correspondente a consulta ou null.
     * 
     * @return Model
     */
    public T last() {
        T obj = null;
        if (!this.isEmpty()) {
            // Ordenando a lista em ordem decrescente pela chave primária.
            this.orderBy("-id");
            obj = this.get(0);
        }
        return obj;
    }
    
    public QuerySet<T> create(String... list) {
    	Manager objects = new Manager(this.entity);
    	this.add((T)objects.create(list));
    	return this;
    }   
}