package org.jddp.expression.pgsql;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.pgsql.util.NameGenerator;
import org.jddp.persistence.util.DBType;
import org.jddp.util.json.JSONBuilder;

import com.owlike.genson.Genson;

public class Var extends AbstractExpression<Var, VariableExpression> implements VariableExpression  {
	
	static Genson JSON = JSONBuilder.JSON;
	
	final protected Object value;
	final String name;
	boolean asLiteral = false;
	
	Var(Var v) {
		super(v.modifier, v.getDBType());
		value = v.value;
		name = NameGenerator.newUniqueParameterName(v.value.getClass());
		
		if (v.isArray()) {
			_toString = new StringBuilder("(:").append(name).append(")").toString();
		} else {
			_toString = ":" + name;
		}
	}
	
	public Var(Boolean bool) {
		super(BOOLEAN, DBType.BOOLEAN);
		value = bool;
		name = NameGenerator.newUniqueParameterName(value.getClass());
		_toString = ":" + name;
	}
	
	public Var(String string) {
		super(STRING, DBType.TEXT);
		value = string;
		name = NameGenerator.newUniqueParameterName(value.getClass());
		_toString = ":" + name;
	}
	
	public Var(Number number) {
		super(NUMERIC, DBType.NUMERIC);
		value = number;
		name = NameGenerator.newUniqueParameterName(value.getClass());
		_toString = ":" + name;
	}
	
	
	public Var(Object obj, DBType dbType) {
		super(JSONOBJECT | (obj.getClass().isArray() ?  PRIMITVE_ARRAY : 0), dbType);
		value = obj;
		name = NameGenerator.newUniqueParameterName(value.getClass());
		_toString = ":" + name;
	}
	
	
	public Var(Collection<?> c, DBType dbType) {
		super(ARRAY | TypeUtil.getElementTypeModifier(c), dbType);
		value = Collections.unmodifiableCollection(c);
		name = NameGenerator.newUniqueParameterName(Collection.class);
		_toString = ":" + name;
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public String getName() {
		return name;
	}
	

	@Override
	public String getValueAsLiteral() {
		if (isArray()) {
			StringBuilder str = new StringBuilder("$${");
			boolean first = true;
			for  (Object o : (Collection<?>) value) {
				if (first) {
					str.append(toLiteral(o, true));
					first = false;
				} else {
					str.append(", ").append(toLiteral(o, true));
				}
			}
			return str.append("}$$").toString();
		}
		
		return toLiteral(value, false);
	}
	
	
	
	
	
	@Override
	public Set<VariableExpression> getBoundVariables() {
		if (asLiteral) {
			return Collections.emptySet();
		}
		return Collections.singleton(this);
	}
	
	@Override
	public Set<FieldExpression<?>> getFields() {
		return Collections.emptySet();
	}
	
	private String toLiteral(Object o, boolean noQuotes) {
		
		if (o == null) {
			return "null";
		}
		
		if (o instanceof Expression) {
			return ((Expression<?>) o).unBoundVariables().toString();
		}
		
		if (o.getClass().getComponentType() == Object.class) {
			int len = Array.getLength(o);
			String s = "[";
			for (int i = 0; i<len; i++){
				if (i > 0) {
					s += ", " + toLiteral(Array.get(o, i), false);
				} else {
					s  += toLiteral(Array.get(o, i), false);
				}
			}
			return s + "]";
		}
		
		
		if (o instanceof String) {
			if (noQuotes) {
				return o.toString();
			} else {
				return new StringBuilder("$$").append(o).append("$$").toString();
			}
		}
		
		if (TypeUtil.isBoolean(o.getClass()) || TypeUtil.isNumeric(o.getClass())) {
			return o.toString();
		}
		
		if (noQuotes) {
			return JSON.serialize(o);
		}
		
		return new StringBuilder("CAST('").append(JSON.serialize(o)).append("' as jsonb)").toString();
		
	}
	
	
	@Override
	public boolean isLeaf() {
		return true;
	}	
	
	@Override
	public VariableExpression unBoundVariables() {
		Var val = (Var) super.unBoundVariables();
		val.asLiteral = true;
		return val;
	}
	
	@Override
	public VariableExpression parenthesize() {
		return super.parenthesize();
	}

	@Override
	public VariableExpression alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public VariableExpression qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public VariableExpression copy() {
		return  new Var(this);
	}
		
}
