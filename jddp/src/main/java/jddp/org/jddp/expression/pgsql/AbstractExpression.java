package org.jddp.expression.pgsql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jddp.exception.JDDPException;
import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.pgsql.util.NamedParameter;
import org.jddp.persistence.util.DBType;

public abstract class AbstractExpression<X extends AbstractExpression<?,?>, E extends Expression<?>> implements Expression<E> {
	protected final Set<VariableExpression> variables = new LinkedHashSet<VariableExpression>();
	protected final Set<FieldExpression<?>> fields = new LinkedHashSet<>();
	protected String _toString;
	protected final int modifier;
	protected final DBType dbType;
	protected final Class<?> type;
	protected final Class<?> arrayType;
	protected String alias;
	protected String qualifier;
	
	
	protected AbstractExpression(Expression<?> e) {
		variables.addAll(e.getBoundVariables());
		_toString = e.toString();
		fields.addAll(e.getFields());
		modifier = e.getModifier();
		dbType = e.getDBType();
		type = e.getType();
		arrayType = e.getArrayType();
		alias = e.getAlias();
		qualifier = e.getQualfier();
		
	}
	
	protected AbstractExpression(Expression<?> e, int modifier, DBType dbType) {
		variables.addAll(e.getBoundVariables());
		_toString =  e.toString();
		fields.addAll(e.getFields());
		this.modifier = modifier;
		this.dbType = dbType;
		alias = e.getAlias();
		qualifier = e.getQualfier();
		
		if (isJSONObject()) {
			if ((modifier & (BOOLEAN | NUMERIC | STRING)) > 0) {
				throw new JDDPException("invalid modifer");
			}
		}
		
		type = TypeUtil.getType(this);
		if (isArray()) {
			arrayType = ArrayList.class;
		} else {
			arrayType = null;
		}
	}
	
	protected  AbstractExpression(int modifier, DBType dbType) {
		_toString = "";
		this.modifier = modifier;
		this.dbType = dbType;
		if (isJSONObject() && !isNil()) {
			if ((modifier & (BOOLEAN | NUMERIC | STRING)) > 0) {
				throw new JDDPException("invalid modifer");
			}
		}
		
		type = TypeUtil.getType(this);
		if (isArray()) {
			arrayType = ArrayList.class;
		} else {
			arrayType = null;
		}
	}
	
	@Override
	public final boolean isBoolean() {return (modifier & BOOLEAN) > 0;};
	@Override
	public final boolean isNumeric() {return (modifier & NUMERIC) > 0;};
	@Override
	public final boolean isString()  {return (modifier & STRING)  > 0;};
	@Override
	public final boolean isArray()   {return (modifier & ARRAY)   > 0;};
	@Override
	public final boolean isJSONObject()  {return (modifier & JSONOBJECT)  > 0;};
	@Override
	public final boolean isNil()  {return (modifier & NULL)  > 0;};
	@Override
	public final boolean isPrimitiveArray()  {return (modifier & PRIMITVE_ARRAY)  > 0;};
	@Override
	public final int getModifier()      {return modifier;};

	@Override
	public DBType getDBType() {
		return dbType;
	}
	
	@Override
	public Class<?> getType() {
		return type;
	}
	
	@Override
	public Class<?> getArrayType() {
		return arrayType;
	}

	@Override
	public NumericExpression<?> castAsNumeric() {
		Cast ue = new Cast(this, NUMERIC, DBType.NUMERIC);
		return new Num(ue);
	}
	
	@Override
	public StringExpression<?> castAsString() {
		Cast ue = new Cast(this, STRING, DBType.TEXT);
		return new Str(ue);
	}
	
	@Override
	public BooleanExpression<?> castAsBoolean() {
		Cast ue = new Cast(this, BOOLEAN, DBType.BOOLEAN);
		return new Bool(ue);
	}
	
	@Override
	public ObjectExpression<?> castAsObject() {
		Cast ue = new Cast(this, JSONOBJECT, DBType.JSONB);
		return new Obj(ue);
	}
	
	
	
	@Override
	public <T extends Expression<?>> T castInto(DBType type) {
		
		Cast ue;
		
		switch (type) {
		case TIMESTAMP:
		case TIMESTAMPTZ:
		case UUID:
		case TEXT:
			ue = new Cast(this, STRING, type);
			return (T) new Str(ue);
		case BOOLEAN:
			ue = new Cast(this, BOOLEAN, type);
			return (T) new Bool(ue);
		case NUMERIC:
			ue = new Cast(this, NUMERIC, type);
			return (T) new Num(ue);
		case JSON:
		case JSONB:
		default:
			ue = new Cast(this, JSONOBJECT, type);
			return (T) new Obj(ue);
		}
		
	}
	
	@Override
	public Set<VariableExpression> getBoundVariables() {
		return Collections.unmodifiableSet(variables);
	}

	@Override
	public Set<FieldExpression<?>> getFields() {
		return Collections.unmodifiableSet(fields);
	}
	
	@Override
	public String toString() {
		return _toString;
	}

	@Override
	public NumericExpression<?> count() {
		return new Num(new Func("COUNT", this, NUMERIC, DBType.NUMERIC));
	}


	@Override
	public String getAlias() {
		return alias;
	}


	@Override
	public String getQualfier() {
		return qualifier;
	}
	
	@Override
	public E unBoundVariables() {
		X n = (X) copy();
		for (VariableExpression v : n.getBoundVariables()) {
			n._toString = NamedParameter.setParameterAsLiteral(v.getName(), v.getValueAsLiteral(), n._toString);
		}
		n.variables.clear();
		return  (E) n;
	}
	
	@Override
	public E parenthesize() {
		X b = (X) copy();
		b._toString = "("  + this._toString + ")";
		return (E) b;
	}

	@Override
	public E  alias(String alias) {
		if (this.alias != null) {
			throw new JDDPException("Cannot alias an already aliased Expression");
		}
		X b = (X) copy();
		b._toString = this._toString  + " as " + alias;
		b.alias = alias;
		return (E) b;
	}

	@Override
	public E qualify(String qualifier) {
		if (this.qualifier != null) {
			throw new JDDPException("Cannot qualify an already qualified expression");
		}
		X b = (X) copy();
		
		b._toString = qualifier + "." + this._toString;
		b.qualifier = qualifier;
		return (E) b;
	}

	
}
