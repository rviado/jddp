package org.jddp.persistence.pgsql.entity;

import java.util.Collection;
import java.util.Set;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.BooleanFieldExpression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.LiteralExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.NumericFieldExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.ObjectFieldExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.StringFieldExpression;
import org.jddp.expression.pgsql.Bool;
import org.jddp.expression.pgsql.BoolField;
import org.jddp.expression.pgsql.BoolNull;
import org.jddp.expression.pgsql.Literal;
import org.jddp.expression.pgsql.Num;
import org.jddp.expression.pgsql.NumField;
import org.jddp.expression.pgsql.NumNull;
import org.jddp.expression.pgsql.Obj;
import org.jddp.expression.pgsql.ObjField;
import org.jddp.expression.pgsql.ObjNull;
import org.jddp.expression.pgsql.Str;
import org.jddp.expression.pgsql.StrField;
import org.jddp.expression.pgsql.StrNull;
import org.jddp.persistence.entity.EntityManager;
import org.jddp.persistence.pgsql.PGDDL;
import org.jddp.persistence.pgsql.PGDML;
import org.jddp.persistence.sql.DDL;
import org.jddp.persistence.sql.DML;
import org.jddp.persistence.util.DBType;

public class PGEntityManager<E> implements EntityManager<E> {

	public static final ObjectExpression<?> EMPTY_OBJECT_ARRAY = new Obj(new Object[]{}, DBType.JSONB).unBoundVariables();
	public static final ObjectExpression<?> EMPTY_BOOLEAN_ARRAY = new Obj(new Boolean[]{}, DBType.JSONB).unBoundVariables();
	public static final ObjectExpression<?> EMPTY_NUMERIC_ARRAY = new Obj(new Number[]{}, DBType.JSONB).unBoundVariables();
	public static final ObjectExpression<?> EMPTY_STRING_ARRAY = new Obj(new String[]{}, DBType.JSONB).unBoundVariables();
	
	EntityClass<E> entityClass;
	DML<E> dml;
	DDL<E> ddl;
	
	public PGEntityManager(Class<E> entity, String rootElement) {
		entityClass = new EntityClass<>(entity, rootElement);
		dml = new PGDML<>(entityClass);
		ddl = new PGDDL<>(entityClass);
	}
	

	@Override
	public ObjectFieldExpression getObjectFieldExpression(String xpath) {
		return (ObjectFieldExpression) entityClass.getField(xpath);
	}
	
	
	@Override
	public ObjectFieldExpression newObjectFieldExpression(FieldExpression<?> f, FieldExpression<?> owner) {
		return  new ObjField((ObjField) f, owner);
	}
	
	@Override
	public ObjectFieldExpression newObjectFieldExpression(String xpath, FieldExpression<?> owner) {
		return  new ObjField((ObjField) getObjectFieldExpression(xpath), owner);
	}


	@Override
	public ObjectFieldExpression newObjectFieldExpression(FieldExpression<?> f, FieldExpression<?> owner, int i) {
		return  new ObjField((ObjField) f, owner, i);
	}

	@Override
	public ObjectFieldExpression newObjectFieldExpression(String xpath, FieldExpression<?> owner, int i) {
		return  new ObjField((ObjField) getObjectFieldExpression(xpath), owner, i);
	}
	

	@Override
	public StringFieldExpression getStringFieldExpression(String xpath) {
		return (StringFieldExpression) entityClass.getField(xpath);
	}


	@Override
	public StringFieldExpression newStringFieldExpression(FieldExpression<?> f, FieldExpression<?> owner) {
		return new StrField((StrField) f, owner);
	}
	
	@Override
	public StringFieldExpression newStringFieldExpression(String xpath, FieldExpression<?> owner) {
		return new StrField((StrField) getStringFieldExpression(xpath), owner);
	}


	@Override
	public BooleanFieldExpression getBooleanFieldExpression(String xpath) {
		return (BooleanFieldExpression) entityClass.getField(xpath);
	}

	@Override
	public BooleanFieldExpression newBooleanFieldExpression(FieldExpression<?> f, FieldExpression<?> owner) {
		return new BoolField((BoolField) f, owner);
	}

	@Override
	public BooleanFieldExpression newBooleanFieldExpression(String xpath, FieldExpression<?> owner) {
		return new BoolField((BoolField) getBooleanFieldExpression(xpath), owner);
	}
	

	@Override
	public NumericFieldExpression getNumericFieldExpression(String xpath) {
		return (NumericFieldExpression) entityClass.getField(xpath);
	}


	@Override
	public NumericFieldExpression newNumericFieldExpression(FieldExpression<?> f, FieldExpression<?> owner) {
		return new NumField((NumField) f, owner);
	}
	
	@Override
	public NumericFieldExpression newNumericFieldExpression(String xpath, FieldExpression<?> owner) {
		return new NumField((NumField) getNumericFieldExpression(xpath), owner);
	}

	@Override
	public FieldExpression<?> getFieldExpression(String xpath) {
		return entityClass.getField(xpath);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends FieldExpression<?>> T getFieldExpression(String xpath, Class<T> type) {
		return (T) entityClass.getField(xpath);
	}
	
	
	@Override
	public LiteralExpression newLiteral(String literal) {
		return new Literal(literal);
	}


	@Override
	public FieldExpression<?> getPrimaryKey() {
		return entityClass.getPrimaryKey();
	}
	
	@Override
	public <T extends FieldExpression<?>> T getPrimaryKey(Class<T> pkeyType) {
		return entityClass.getPrimaryKey(pkeyType);
	}
	
	@Override
	public <T> T getPrimaryKeyValue(E obj) {
		return entityClass.getPrimaryKeyValue(obj);
	}
	
	@Override
	public <T> T getIndexValue(FieldExpression<?> f, E e, Class<T> type) {
		return entityClass.getIndexValue(f, e, type);
	}
	
	@Override
	public DML<E> getDML() {
		return dml;
	}


	@Override
	public DDL<E> getDDL() {
		return ddl;
	}

	@Override
	public  Set<FieldExpression<?>> getIndeces() {
		return entityClass.getIndeces();
	}
	
	@Override
	public String getConstraint(FieldExpression<?> field) {
		return entityClass.getConstraint(field);
	}
	
	@Override
	public boolean isIndexed(FieldExpression<?> field) {
		return entityClass.isIndex(field);
	}
	
	@Override
    public  Collection<String> getXPaths() {
        return entityClass.getXPaths();
    }
	
	@Override
    public FieldExpression<?> getXPathAsField(String xpath) {
        return entityClass.getField(xpath);
    }

	@Override
    public boolean isValidXPath(String xpath) {
        return entityClass.getXPaths().contains(xpath);
    }

	@Override
    public String getXpathsAsString() {
        return entityClass.getXPaths().toString();
    }
	
	@Override
	public NumericExpression<?> newNumber(Number n) {
		if (n == null) {
			return NumNull.NUMERIC_NULL;
		}
		return new Num(n);
	}

	@Override
	public BooleanExpression<?> newBoolean(Boolean b) {
		if (b == null) {
			return BoolNull.BOOLEAN_NULL;
		}
		return new Bool(b);
	}

	@Override
	public StringExpression<?> newString(String s) {
		if (s == null) {
			return StrNull.STRING_NULL;
		}
		return new Str(s);
	}

	@Override
	public ObjectExpression<?> newObject(Object o, DBType dbType) {
		if (o == null) {
			return ObjNull.OBJECT_NULL;
		}
		return new Obj(o, dbType);
	}
	
	
	@Override
	public ObjectExpression<?> newObject(Collection<?> c, DBType dbType) {
		if (c == null) {
			return ObjNull.OBJECT_NULL;
		}
		return new Obj(c, dbType);
	}


	
	
	 
}
