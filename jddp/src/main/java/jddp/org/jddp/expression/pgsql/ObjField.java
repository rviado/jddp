package org.jddp.expression.pgsql;

import java.util.Collection;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.BooleanFieldExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.NumericFieldExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.ObjectFieldExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.StringFieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.util.DBType;

public class ObjField extends Field<ObjectFieldExpression> implements ObjectFieldExpression {
	
	private final Obj thisAsObject;
	
	
	public ObjField(ObjField o, FieldExpression<?> owner, Integer i) {
		super(o, owner, i);
		if (!isJSONObject()) {
			throw new IllegalArgumentException(xpath + "(" + type + ") : is not an object type");
		}
		thisAsObject = new Obj(this);
	}
	
	public ObjField(ObjField o, FieldExpression<?> owner) {
		this(o, owner, null);
		if (!isJSONObject()) {
			throw new IllegalArgumentException(xpath + "(" + type + ") : is not an object type");
		}
	}
	
	
	
	public ObjField(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType, int modifier, EntityClass<?> entityClass,	String[] accessor) {
		super(xpath, prefix, fieldName, arrayType, type, dbType, modifier, entityClass, accessor);
		if (!isJSONObject()) {
			//throw new IllegalArgumentException(xpath + "(" + type + ") : is not an object type");
		}
		_toString = asObjectReference();
		thisAsObject = new Obj(this);
	}

		
	@Override
	public BooleanExpression<?> eq(ObjectExpression<?> e) {
		return thisAsObject.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(ObjectExpression<?> e) {
		return thisAsObject.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(ObjectExpression<?> e) {
		return thisAsObject.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(ObjectExpression<?> e) {
		return thisAsObject.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(ObjectExpression<?> e) {
		return thisAsObject.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(ObjectExpression<?> e) {
		return thisAsObject.gte(e);
	}

	@Override
	public BooleanExpression<?> contains(ObjectExpression<?> e) {
		return thisAsObject.contains(e);
	}
	
	@Override
	public BooleanExpression<?> containedIn(ObjectExpression<?> e) {
		return thisAsObject.containedIn(e);
	}
	
	@Override
	public ObjectExpression<?> concat(ObjectExpression<?> e) {
		return thisAsObject.concat(e);
	}
	
	@Override
	public BooleanExpression<?> eq(Object e) {
		return thisAsObject.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(Object e) {
		return thisAsObject.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(Object e) {
		return thisAsObject.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(Object e) {
		return thisAsObject.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(Object e) {
		return thisAsObject.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(Object e) {
		return thisAsObject.gte(e);
	}

	@Override
	public BooleanExpression<?> contains(Object e) {
		return thisAsObject.contains(e);
	}
	
	@Override
	public BooleanExpression<?> containedIn(Object e) {
		return thisAsObject.containedIn(e);
	}
	
	@Override
	public ObjectExpression<?> concat(Object e) {
		return thisAsObject.concat(e);
	}
	
	@Override
	public BooleanExpression<?> isKeyExist(String key) {
		return thisAsObject.isKeyExist(key);
	}
	
	@Override
	public BooleanExpression<?> isNull() {
		return thisAsObject.isNull();
	}

	@Override
	public BooleanExpression<?> isNotNull() {
		return thisAsObject.isNotNull();
	}

	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		return thisAsObject.in(e);
	}
	
	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		return thisAsObject.in(c);
	}

	@Override
	public BooleanExpression<?> notIn(Expression<?> e) {
		return thisAsObject.in(e);
	}
	
	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		return thisAsObject.notIn(c);
	}

	@Override
	public ObjectExpression<?> element(int i) {
		return thisAsObject.element(i);
	}

	@Override
	public StringExpression<?> elementAsText(int i) {
		return thisAsObject.elementAsText(i);
	}

	@Override
	public ObjectExpression<?> field(String name) {
		return thisAsObject.field(name);
	}

	@Override
	public StringExpression<?> fieldAstext(String name) {
		return thisAsObject.fieldAstext(name);
	}

	@Override
	public ObjectExpression<?> fieldByPath(Collection<String> paths) {
		return thisAsObject.fieldByPath(paths);
	}

	@Override
	public StringExpression<?> fieldByPathAsText(Collection<String> paths) {
		return thisAsObject.fieldByPathAsText(paths);
	}
	
	@Override
	public ObjectExpression<?> unset(String name) {
		return thisAsObject.unset(name);
	}
	
	@Override
	public ObjectExpression<?> remove(int i) {
		return thisAsObject.remove(i);
	}
	
	@Override
	public ObjectExpression<?> unset(Collection<String> paths) {
		return thisAsObject.unset(paths);
	}
	
	@Override
	public ObjectExpression<?> unset(FieldExpression<?> f) {
		return thisAsObject.unset(f);
	}
	
	@Override
	public ObjectExpression<?> set(Collection<String> paths, ObjectExpression<?> value) {
		return thisAsObject.set(paths, value);
	}
	
	@Override
	public ObjectExpression<?> set(FieldExpression<?> f, ObjectExpression<?> value) {
		return thisAsObject.set(f, value);
	}

	@Override
	public ObjectFieldExpression unBoundVariables() {
		return super.unBoundVariables();
	}

	@Override
	public NumericExpression<?> length() {
		return thisAsObject.length();
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override 
	public BooleanFieldExpression castAsBoolean() {
		return new BoolField(this, owner, i);
	}
	
	@Override 
	public NumericFieldExpression castAsNumeric() {
		return new NumField(this, owner, i);
	}
	
	@Override 
	public StringFieldExpression castAsString() {
		return new StrField(this, owner, i);
	}
	
	@Override
	public ObjectFieldExpression parenthesize() {
		return super.parenthesize();
	}

	@Override
	public ObjectFieldExpression alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public ObjectFieldExpression qualify(String qualifier) {
		return super.qualify(qualifier);
	}
	
	@Override
	public ObjField copy() {
		ObjField o;
		if (this.i != null) {
			o = new ObjField(this, this.owner, this.i);
		} else {
			o = new ObjField(this.xpath, this.prefix, this.fieldName, this.arrayType, this.type, this.dbType, this.modifier, this.entityClass, this.accessor);
			o.owner = this.owner;
		}
		
		return o;
	}
	
}
