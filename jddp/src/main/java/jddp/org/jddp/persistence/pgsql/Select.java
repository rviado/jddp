package org.jddp.persistence.pgsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jddp.exception.JDDPException;
import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.UUIDExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.expression.ZonedDateTimeExpression;
import org.jddp.expression.pgsql.AbstractExpression;
import org.jddp.expression.pgsql.Bool;
import org.jddp.expression.pgsql.LIST;
import org.jddp.expression.pgsql.Num;
import org.jddp.expression.pgsql.Obj;
import org.jddp.expression.pgsql.OrderBy;
import org.jddp.expression.pgsql.Str;
import org.jddp.expression.pgsql.UID;
import org.jddp.expression.pgsql.ZDT;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.pgsql.entity.EntityMeta;
import org.jddp.persistence.pgsql.util.NamedParameter;
import org.jddp.persistence.pgsql.util.ResultSetUtil;
import org.jddp.persistence.sql.ResultSet;
import org.jddp.persistence.sql.select.SelectConditional;
import org.jddp.persistence.sql.select.SelectConnected;
import org.jddp.persistence.sql.select.SelectDetached;
import org.jddp.persistence.sql.select.SelectDistinctOn;
import org.jddp.persistence.sql.select.SelectFrom;
import org.jddp.persistence.sql.select.SelectGrouped;
import org.jddp.persistence.sql.select.SelectLimited;
import org.jddp.persistence.sql.select.SelectOffsetted;
import org.jddp.persistence.sql.select.SelectOrdered;
import org.jddp.persistence.sql.select.SelectStatement;
import org.jddp.persistence.util.DBType;

import com.owlike.genson.JsonBindingException;



public class Select<E, R>  extends AbstractExpression<Select<E, R>, SelectDetached<R>> implements  SelectStatement<R>, SelectDistinctOn<R>, SelectFrom<R>, SelectOrdered<R>,  SelectConnected<R> {
	
	private final EntityMeta<E> meta;
	
	private final List<Expression<?>> selections = new ArrayList<Expression<?>>();
	private final List<OrderBy> orders = new ArrayList<OrderBy>();
	private final List<Expression<?>> groups = new ArrayList<Expression<?>>();
	private final List<Expression<?>> distincts = new ArrayList<Expression<?>>();
	
	private BooleanExpression<?> whereCondition;
	private Expression<?> from;
	
	private Integer limit;
	private Integer offset;
	private final boolean selectAsEnity;
	private String countSelectSQL;
	private Connection connection;
	private boolean isDistinct = false;
	
	protected boolean isLeaf; 
	
	Select(Select<E, R> as, int modifier, DBType dbType) {
		super(as, modifier, dbType);
		meta = new EntityMeta<E>(as.meta.entityClass);
		isLeaf = as.isLeaf;
		selections.addAll(as.selections);
		distincts.addAll(as.distincts);
		whereCondition = as.whereCondition;
		groups.addAll(as.groups);
		orders.addAll(as.orders);
		limit = as.limit;
		offset = as.offset;
		selectAsEnity = as.selectAsEnity;
		countSelectSQL = as.countSelectSQL;
		connection = as.connection;
		isDistinct = as.isDistinct;
		from = as.from;
	}
	
	Select(Select<E, R> as) {
		super(as);
		meta = new EntityMeta<E>(as.meta.entityClass);
		isLeaf = as.isLeaf;
		selections.addAll(as.selections);
		distincts.addAll(as.distincts);
		whereCondition = as.whereCondition;
		groups.addAll(as.groups);
		orders.addAll(as.orders);
		limit = as.limit;
		offset = as.offset;
		selectAsEnity = as.selectAsEnity;
		countSelectSQL = as.countSelectSQL;
		connection = as.connection;
		isDistinct = as.isDistinct;
		from = as.from;
	}
	
	public Select(EntityClass<E> entityClass) {
		super(0,null);
		meta = new EntityMeta<E>(entityClass);
		selectAsEnity = true;
		selections.add(meta.entityField);
		isLeaf = false;
	}
	
	public Select(EntityClass<E> entityClass, Expression<?>... fields) {
		super(0,null);
		meta = new EntityMeta<E>(entityClass);
		selectAsEnity = false;
		selections.addAll(Arrays.asList(fields));
		for (Expression<?> f : selections) {
			variables.addAll(f.getBoundVariables());
		}
		isLeaf = false;
	}
	
	@Override
	public SelectDistinctOn<R> on(Expression<?> firstExpression, Expression<?>... exprs) {
		Select<E, R> as = new Select<>(this);
		LIST o = new LIST();
		o = o.add(firstExpression);
		for (Expression<?> e : exprs) {
			o = o.add(e);
		}
		
		if (o.length() > 1) {
			o = o.parenthesize();
		}
		as.distincts.add(o);
		as.variables.addAll(o.getBoundVariables());
		return as;
	}

	@Override
	public SelectDistinctOn<R> distinct() {
		Select<E, R> as = new Select<>(this);
		as.isDistinct = true;
		return as;
	}
	
	@Override
	public SelectFrom<R> from(Expression<?> tableExpression) {
		Select<E, R> as = new Select<>(this);
		as.from = tableExpression;
		as.variables.addAll(tableExpression.getBoundVariables());
		return as;
	}
	
	@Override
	public SelectConditional<R> where(BooleanExpression<?> whereCondition) {
		Select<E, R> as = new Select<>(this);
		as.whereCondition = whereCondition; 
		as.variables.addAll(whereCondition.getBoundVariables());
		return as;
	}

	@Override
	public SelectGrouped<R> groupBy(Expression<?> firstGroubBy, Expression<?>... thenGroupBy) {
		Select<E, R> as = new Select<>(this);
		
		LIST o = new LIST();

		o = o.add(firstGroubBy);
		for (Expression<?> e : thenGroupBy) {
			o = o.add(e);
		}
		
		if (o.length() > 1) {
			o = o.parenthesize();
		}
		as.groups.add(o);
		as.variables.addAll(o.getBoundVariables());
		return as;
	}

	@Override
	public SelectOrdered<R> orderBy(Expression<?> firstExpression, Expression<?>... thenOrderBy) {
		Select<E, R> as = new Select<>(this);
		LIST o = new LIST();
		
		if (firstExpression instanceof OrderBy) {
			o = o.add(firstExpression);
		} else {
			o = o.add(new OrderBy(firstExpression));
		}
		
		
		for (Expression<?> e : thenOrderBy) {
			if (e instanceof OrderBy) {
				o = o.add(e);
			} else {
				o = o.add(new OrderBy(e));
			}
		}
		
		as.orders.add(new OrderBy(o));
		as.variables.addAll(o.getBoundVariables());
		
		return as;
	}
	
	
	public @Override SelectOrdered<R> asc() {
		Select<E, R> as = new Select<>(this);

		if (!as.orders.isEmpty()) {
			int size = as.orders.size();
			OrderBy ascendingOrder = as.orders.get(size-1).asc();
			as.orders.remove(size-1);
			as.orders.add(ascendingOrder);
		} else {
			throw new JDDPException("asc() called on non-ordered select");
		}
		
		return as;
	}
	
	public @Override SelectOrdered<R> desc() {
		Select<E, R> as = new Select<>(this);

		if (!as.orders.isEmpty()) {
			int size = as.orders.size();
			OrderBy descendingOrder = as.orders.get(size-1).desc();
			as.orders.remove(size-1);
			as.orders.add(descendingOrder);
		} else {
			throw new JDDPException("desc() called on non-ordered select");
		}
		
		return as;
	}
	
	@Override
	public SelectLimited<R> limit(Integer limit) {
		Select<E, R> as = new Select<>(this);
		as.limit = limit;
		return as;
	}

	@Override
	public SelectOffsetted<R> offset(Integer offset) {
		Select<E, R> as = new Select<>(this);
		as.offset = offset;
		return as;
	}
	
	@Override
	public SelectDetached<R> create() {
		Select<E, R> as = new Select<>(this);
		as._toString = as.createSelectSQL(false);
		as.countSelectSQL = as.createCountSQL(as._toString);
		return as;
	}
	
	
	
	@Override
	public SelectConnected<R> create(Connection connection) {
		Select<E, R> as = new Select<>(this);
		as._toString = as.createSelectSQL(false);
		as.countSelectSQL = as.createCountSQL(as._toString);
		as.connection = connection;
		return as;
	}
	
	@Override
	public R execute() {
		return execute(connection);
	}
	@Override
	public Long executeCount() {
		return executeCount(connection);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public R execute(Connection connection) {
		Object rowElement = null;
		
		PreparedStatement ps = null;
		java.sql.ResultSet rs = null;
		
		try {
			ResultSet records = new PGResultSet();
			List<E> results = new ArrayList<>();
			
			if (limit != null && limit == 0) {
				if (selectAsEnity) {
					return (R) results;
				} 
				return (R) records;
			}
			
			NamedParameter np = new NamedParameter(connection, _toString);
			
			setParameters(np);
			
			ps = np.getPreparedStatement(); 
	
			rs = ps.executeQuery();
			
			while (rs.next()) {
				Iterator<Expression<?>> iter = selections.iterator();
				PGRow row = new PGRow();
				int columnIndex = 1;
	
				while (iter.hasNext()) {
					Expression<?> expr = iter.next();
					
					rowElement =  rs.getObject(columnIndex);
										
					if (rowElement != null) {
						rowElement = new ResultSetUtil(rowElement, expr).getObject();
					}	
					
					row.getColumns().add(rowElement);
					columnIndex++;
				}
				
				if (selectAsEnity) {
					results.add((E) row.getColumn(0));
				} else {
					records.getRows().add(row);
				}	
			}
			
			if (selectAsEnity) {
				return (R) results;
			}
			return (R) records;
			
		} catch (SQLException e) {
			throw new JDDPException("\n" + _toString, e);
		} catch (JsonBindingException e) {
			throw new JDDPException("\n" + _toString + "\n JSON String :\n" + rowElement , e);
		} finally {
			NamedParameter.closeQuietly(rs);
		    NamedParameter.closeQuietly(ps);
		}
	}

	@Override
	public Long executeCount(Connection connection) {
		java.sql.ResultSet rs = null;
		PreparedStatement ps = null;
		
		try {
			NamedParameter np = new NamedParameter(connection, countSelectSQL);
			
			setParameters(np);
			
			ps = np.getPreparedStatement();
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			}
			return 0L;
		} catch (SQLException e) {
			throw new JDDPException("\n" + countSelectSQL, e);
		} finally {
			NamedParameter.closeQuietly(rs);
		    NamedParameter.closeQuietly(ps);
		}
	}

	

	@Override
	public String getSQL(boolean varAsLiterals) {
		if (varAsLiterals) {
			return createSelectSQL(varAsLiterals);
		} 
		return _toString;
	}

	@Override
	public String getCountSQL(boolean varAsLiterals) {
		if (varAsLiterals) {
			return createCountSQL(createSelectSQL(varAsLiterals));
		}
		return countSelectSQL;
	}
	
	@Override
	public String getSQL() {
		return _toString;
	}

	@Override
	public String getCountSQL() {
		return countSelectSQL;
	}

	protected String createSelectSQL(boolean varAsLiterals) {

		StringBuilder sql = new StringBuilder();;
		

		Set<String> joins = new LinkedHashSet<String>();
		
		
		LIST distinctOn = null;
		
		if (!distincts.isEmpty()) {
			distinctOn = new LIST();
			for (Expression<?> e : distincts) {
				distinctOn = distinctOn.add(e);
				for (FieldExpression<?> f : e.getFields()) {
					joins.addAll(f.getRequiredJoins());
				}
			}
		}
		
		if (whereCondition != null) {
			for (FieldExpression<?> f : whereCondition.getFields()) {
				joins.addAll(f.getRequiredJoins());
			}
		}

		LIST groupBy = null;
		
		if (!groups.isEmpty()) {
			groupBy = new LIST();
			for (Expression<?> e : groups) {
				groupBy = groupBy.add(e);
				for (FieldExpression<?> f : e.getFields()) {
					joins.addAll(f.getRequiredJoins());
				}
			}
		}
		
		LIST orderBy = null;
		
		if (!orders.isEmpty()) {
			orderBy = new LIST();
			for (OrderBy o : orders) {
				orderBy = orderBy.add(o);
				for (FieldExpression<?> f : o.getFields()) {
					joins.addAll(f.getRequiredJoins());
				}
			}
		}
		
		
		StringBuilder projectedFields = new StringBuilder();
		
		Expression<?> expr;
		for (int i = 0; i < selections.size(); i++) {
			expr = selections.get(i); 
			if (i > 0) {
				projectedFields.append(", ");
			}
			if (varAsLiterals) {
				projectedFields.append(expr.unBoundVariables().toString());
			} else {
				projectedFields.append(expr);	
			}
			for (FieldExpression<?> f : expr.getFields()) {
				joins.addAll(f.getRequiredJoins());
			}	
		}

		StringBuilder distinct = new StringBuilder();
		
		if (this.isDistinct) {
			distinct.append("DISTINCT ");
			if (distinctOn != null) {
				distinct.append("ON (");
				if (varAsLiterals) {
					sql.append(distinctOn.unBoundVariables()).append(") ");
				} else {
					sql.append(distinctOn).append(") ");
				}
			}
		}
		sql.append("SELECT ").append(distinct).append(projectedFields);
		if (from != null) {
			if (varAsLiterals) {
				sql.append(" FROM ").append(from.unBoundVariables().toString()) ;
			} else {
				sql.append(" FROM ").append(from) ;
			}
		} else {
			sql.append(" FROM ").append(meta.tableName) ;
		}	
		
		
		for (String j :  joins) {
			sql.append("\n").append("    ").append(j);
		}

		if (whereCondition != null) {
			sql.append("\nWHERE\n").append("    ");
			if (varAsLiterals) {
				sql.append(whereCondition.unBoundVariables());
			} else	{
				sql.append("    ").append(whereCondition);
			}
		}

		if (groupBy != null) {
			sql.append(" \nGROUP BY\n    ");
			if (varAsLiterals) {
				sql.append(groupBy.unBoundVariables());
			} else {
				sql.append(groupBy);
			}
		}

		if (orderBy != null) {
			sql.append(" \nORDER BY\n    ");
			if (varAsLiterals) {
				sql.append(orderBy.unBoundVariables());
			} else {
				sql.append(orderBy);
			}
		}
		
		if (limit != null) {
			sql.append("\nLIMIT ").append(limit);
		} 
		
		if (offset != null) {
			sql.append("\nOFFSET ").append(offset);
		}
		
		return sql.toString();
	}
	
	
	protected String createCountSQL(String selectSQL) {
		return  "SELECT COUNT(*) FROM (\n" + selectSQL + "\n) as AAAAAZZZZZ";  
	}
	
	private void setParameters(NamedParameter np) throws SQLException {
		for (VariableExpression val : variables) {
			np.setValue(val);
		}
	}

	@Override
	public boolean isLeaf() {
		return isLeaf;
	}

	

	

	@Override
	public StringExpression<?> asString() {
		return new Str(new Select<>(this, Expression.STRING, DBType.TEXT));
	}

	@Override
	public UUIDExpression<?> asUUID() {
		return new UID(new Select<>(this, Expression.STRING, DBType.UUID));
	}

	@Override
	public ZonedDateTimeExpression<?> asZonedDateTime() {
		return new ZDT(new Select<>(this, Expression.STRING, DBType.TIMESTAMPTZ));
	}

	@Override
	public BooleanExpression<?> asBoolean() {
		return new Bool(new Select<>(this, Expression.BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public NumericExpression<?> asNumeric() {
		return new Num(new Select<>(this, Expression.NUMERIC, DBType.NUMERIC));
	}

	@Override
	public ObjectExpression<?> asObject(Class<?> type) {
		return new Obj(new Select<>(this, Expression.JSONOBJECT, DBType.JSONB), type, null);
	}

	@Override
	public ObjectExpression<?> asObjectCollection(Class<?> type) {
		return new Obj(new Select<>(this, Expression.JSONOBJECT | Expression.ARRAY, DBType.JSONB), type, List.class);
	}
	
	@Override
	public SelectDetached<R> unBoundVariables() {
		Select<E, R> as = new Select<>(this);
		as._toString = as.createSelectSQL(true);
		as.countSelectSQL = as.createCountSQL(as._toString);
		as.variables.clear();
		return as;
	}
	
	@Override
	public SelectDetached<R> parenthesize() {
		@SuppressWarnings("unchecked")
		Select<E, R> sd =  (Select<E, R>) super.parenthesize();
		sd.isLeaf = true;
		return sd;
	}

	@Override
	public SelectDetached<R> alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public SelectDetached<R> qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public SelectDetached<R> copy() {
		return new  Select<>(this);
	}


	
}
