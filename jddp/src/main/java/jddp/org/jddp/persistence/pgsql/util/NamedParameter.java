package org.jddp.persistence.pgsql.util;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jddp.exception.JDDPException;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.util.DBType;
import org.jddp.util.json.JSONBuilder;
import org.postgresql.util.PGobject;

import com.owlike.genson.Genson;


public class NamedParameter {
   
	static final char SINGLE_QUOTE = '\'';
	static final char DOUBLE_QUOTE = '"';
	static final char DOLLAR_QUOTE = '$';
	static final char COLON = ':';
	static final char ESCAPED = 'E';
	
	
	
	private static Genson JSON = JSONBuilder.JSON;
	
	
	
	String namedParameratizedSQL;
	String positionalParameratizedSQL;
	
	final PreparedStatement stmt;
	
	Map<String, List<Integer>> paramIndexMap = new LinkedHashMap<>();
	
	public NamedParameter(Connection connection, String sql) {
		namedParameratizedSQL = sql;
		positionalParameratizedSQL = parse(sql, paramIndexMap);
		try {
			stmt = connection.prepareStatement(positionalParameratizedSQL);
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
	}

	
	public void setValue(VariableExpression value) {
		try {
			setNamedParameter(value, stmt, paramIndexMap);
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
	}
	
		
	public void setFieldValue(FieldExpression<?> f, Object value) {
		setNamedParameter(f, value, stmt, paramIndexMap);
	}
	
	/**
	 * @return the namedParameratizedSQL
	 */
	public String getNamedParameratizedSQL() {
		return namedParameratizedSQL;
	}


	/**
	 * @return the positionalParameratizedSQL
	 */
	public String getPositionalParameratizedSQL() {
		return positionalParameratizedSQL;
	}


	/**
	 * @return the stmt
	 */
	public PreparedStatement getPreparedStatement() {
		return stmt;
	}


	/**
	 * @return the paramIndexMap
	 */
	public Map<String, List<Integer>> getParamIndexMap() {
		return Collections.unmodifiableMap(paramIndexMap);
	}

	
	public static void closeQuietly(PreparedStatement ps) {
		if (ps == null) {
			return;
		}
		try {
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void closeQuietly(ResultSet rs) {
		if (rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setNamedParameter(VariableExpression v, PreparedStatement stmt, Map<String, List<Integer>> paramIndexMap) throws SQLException {
		
		String name = v.getName(); 
		Object value = v.getValue();
		DBType dbt = v.getDBType();
		
		
		Connection connection = stmt.getConnection();

		if (v.isArray()) {
			Array array = null;
			if (v.isBoolean() || v.isNumeric() || v.isString()) {
				array = connection.createArrayOf(dbt.toString(), ((Collection<?>) value).toArray());
			} else	if (v.isJSONObject()) {
				PGobject[] a = new PGobject[((Collection<?>) value).size()];
				int i = 0;
				for (Object o : (Collection<?>) value) {
					if (o instanceof Expression) {
						a[i++] = toPGObject((Expression<?>) o, o, dbt);
					} else {
						a[i++] = toPGObject(v, o, dbt);
					}
				}	
				array = connection.createArrayOf("jsonb", a);
			} else {
				throw new JDDPException(" Illegal value type " + Integer.toBinaryString(v.getModifier()));
			}
			
			for (int i : paramIndexMap.get(name)) {
				stmt.setArray(i, array);
			}
			
		} else if (v.isBoolean()) {
			for (int i : paramIndexMap.get(name)) {
				stmt.setBoolean(i, (Boolean) value);
			}
		} else if (v.isNumeric()) {
			for (Integer i : paramIndexMap.get(name)) {
				stmt.setBigDecimal(i, TypeUtil.toBigDecimal(value));
			}
		} else if (v.isString()) {	
			for (int i : paramIndexMap.get(name)) {
				stmt.setString(i, (String) value);
			}
		} else if (v.isJSONObject()){
			PGobject pgObject = toPGObject(v, value, dbt);
			for (int i : paramIndexMap.get(name)) {
				stmt.setObject(i, pgObject);
			}	
		} else {
			throw new IllegalArgumentException("Invalid modifier type -> " + v.getModifier());
		}
	}
	
	
public static void setNamedParameter(FieldExpression<?> f, Object value, PreparedStatement stmt, Map<String, List<Integer>> paramIndexMap) {
		DBType dbt = f.getDBType();
		String name = f.getFieldName();
		
		try {
			
			switch (dbt) {
			case TEXT:
				if (!(value instanceof String)) {
					value = JSON.serialize(value);
				}
				for (int i : paramIndexMap.get(name)) {
					stmt.setString(i, (String) value);
				}
				break;
			case BOOLEAN:
				for (int i : paramIndexMap.get(name)) {
					stmt.setBoolean(i, (Boolean) value);
				}
				break;
			case NUMERIC:
				for (Integer i : paramIndexMap.get(name)) {
					stmt.setBigDecimal(i, TypeUtil.toBigDecimal(value));
				}
				break;
			case JSON:
			case JSONB:
			case TIMESTAMP:
			case TIMESTAMPTZ:
			case UUID:
			default:
				PGobject pgObject = toPGObject(f, value, dbt); 
				for (int i : paramIndexMap.get(name)) {
					stmt.setObject(i, pgObject);
				}
			}
		} catch (SQLException e) {
			throw new JDDPException("Error setting field " + f.getFieldName(), e);
		}
	}
	

/**
 * Parses a query with named parameters.  The parameter-index mappings are put into the map
 * @param query query to parse
 * @param paramIndexMap the parameter-index mappings
 * @return the query with name parameters replaced with ?
 */
public   static final String parse(String query, Map<String, List<Integer>> paramIndexMap) {
    int length=query.length();
    StringBuilder parsedQuery=new StringBuilder(length);
    
    int index = 1;
    
    char lookAhead;
    
    for(int i=0;i<length;i++) {
        char c=query.charAt(i);
        
        if (c == ESCAPED &&  (i + 1) < length && (query.charAt(i+1)) == SINGLE_QUOTE) {
        	parsedQuery.append(ESCAPED);
        	StringBuilder m = new StringBuilder();
        	if (match(SINGLE_QUOTE, query, i + 2, length, true, m)) {
        		parsedQuery.append(SINGLE_QUOTE).append(m).append(SINGLE_QUOTE);
           		i += m.length() + 2;
        	} else {
        		throw new JDDPException("Malformed SQL: Single Quote at position " + i + 1 + " has no corresponding pair");
        	}
        } else if (c == SINGLE_QUOTE) {
        	parsedQuery.append(SINGLE_QUOTE);
        	StringBuilder m = new StringBuilder();
        	if (match(SINGLE_QUOTE, query, i + 1, length, false, m)) {
        		parsedQuery.append(m).append(SINGLE_QUOTE);
        		i += m.length() + 1;
        	} else {
        		throw new JDDPException("Malformed SQL: Single Quote at position " + i + " has no corresponding pair");
        	}
        } else if (c == DOUBLE_QUOTE) {
        	parsedQuery.append(DOUBLE_QUOTE);
        	StringBuilder m = new StringBuilder();
        	if (match(DOUBLE_QUOTE, query, i + 1, length, true, m)) {
        		parsedQuery.append(m).append(DOUBLE_QUOTE);
        		i += m.length() + 1;
        	} else {
        		throw new JDDPException("Malformed SQL: Double Quote at position " + i + " has no corresponding pair");
        	}
        	
        } else if (c == DOLLAR_QUOTE) {
        	StringBuilder openTag = new StringBuilder();
        	if (match(DOLLAR_QUOTE, query, i + 1, length, false, openTag)) {
        		StringBuilder dquote = new StringBuilder();
        		dquote.append(DOLLAR_QUOTE).append(openTag).append(DOLLAR_QUOTE);
        		parsedQuery.append(dquote);
        		i += openTag.length() + 2;
        		int x = query.indexOf(dquote.toString(), i);
        		if (x >= 0) {
        			parsedQuery.append(query.substring(i, x)).append(dquote);
        			i = x + dquote.length() - 1;
        		} else {
        			throw new JDDPException("Malformed SQL: Dollar quote " + dquote + " at position " + (i - openTag.length() - 2) + " has no corresponding pair");
        		}
        	} else {
           		throw new JDDPException("Malformed SQL: Dollar Quote at position " + i + " has no corresponding pair");
        	}
        }  else if (c == COLON &&  (i + 1) < length && (lookAhead = query.charAt(i+1)) == COLON) {
        	parsedQuery.append(COLON);
        	i++;
        	while (i < length && (c = query.charAt(i)) == COLON) {
        		parsedQuery.append(c);
        		i++;
        	}
        	i--;
        } else if (c == COLON &&  (i + 1) < length && (lookAhead = query.charAt(i+1)) != DOLLAR_QUOTE  && Character.isJavaIdentifierStart(lookAhead) ) {
        	i += 2;
        	StringBuilder name = new StringBuilder();
        	name.append(lookAhead);
        	while (i < length && (c = query.charAt(i)) != DOLLAR_QUOTE && Character.isJavaIdentifierPart(c)) {
        		name.append(c);
        		i++;
        	}
        	i--;
        	
        	List<Integer> indexList= paramIndexMap.get(name.toString());
            if(indexList==null) {
                indexList=new ArrayList<>();
                paramIndexMap.put(name.toString(), indexList);
            }
            indexList.add(index);

            index++;
            
        	parsedQuery.append('?');
        }  else {
        	parsedQuery.append(c);
        }
        
    }  
    return parsedQuery.toString();
    
}

/**
 * Parses a query with named parameters.
 * @param parameterName the name of the parameter in query to set as literal
 * @param literalValue the string literal to replace the name parameter used to replace occurrences of oldName   
 * @param query the query to parse
 * @return the query with parameterName replaced by the supplied literalValue
 */
public   static final String setParameterAsLiteral(String parameterName, String literalValue, String  query) {
	
    int length=query.length();
    StringBuilder parsedQuery=new StringBuilder(length);
    
    char lookAhead;
    
    for(int i=0;i<length;i++) {
        char c=query.charAt(i);
        
        if (c == ESCAPED &&  (i + 1) < length && (query.charAt(i+1)) == SINGLE_QUOTE) {
        	parsedQuery.append(ESCAPED);
        	StringBuilder m = new StringBuilder();
        	if (match(SINGLE_QUOTE, query, i + 2, length, true, m)) {
        		parsedQuery.append(SINGLE_QUOTE).append(m).append(SINGLE_QUOTE);
           		i += m.length() + 2;
        	} else {
        		throw new JDDPException("Malformed SQL: Single Quote at position " + i + 1 + " has no corresponding pair");
        	}
        } else if (c == SINGLE_QUOTE) {
        	parsedQuery.append(SINGLE_QUOTE);
        	StringBuilder m = new StringBuilder();
        	if (match(SINGLE_QUOTE, query, i + 1, length, false, m)) {
        		parsedQuery.append(m).append(SINGLE_QUOTE);
        		i += m.length() + 1;
        	} else {
        		throw new JDDPException("Malformed SQL: Single Quote at position " + i + " has no corresponding pair");
        	}
        } else if (c == DOUBLE_QUOTE) {
        	parsedQuery.append(DOUBLE_QUOTE);
        	StringBuilder m = new StringBuilder();
        	if (match(DOUBLE_QUOTE, query, i + 1, length, true, m)) {
        		parsedQuery.append(m).append(DOUBLE_QUOTE);
        		i += m.length() + 1;
        	} else {
        		throw new JDDPException("Malformed SQL: Double Quote at position " + i + " has no corresponding pair");
        	}
        	
        } else if (c == DOLLAR_QUOTE) {
        	StringBuilder openTag = new StringBuilder();
        	if (match(DOLLAR_QUOTE, query, i + 1, length, false, openTag)) {
        		StringBuilder dquote = new StringBuilder();
        		dquote.append(DOLLAR_QUOTE).append(openTag).append(DOLLAR_QUOTE);
        		parsedQuery.append(dquote);
        		i += openTag.length() + 2;
        		int x = query.indexOf(dquote.toString(), i);
        		if (x >= 0) {
        			parsedQuery.append(query.substring(i, x)).append(dquote);
        			i = x + dquote.length() - 1;
        		} else {
        			throw new JDDPException("Malformed SQL: Dollar quote " + dquote + " at position " + (i - openTag.length() - 2) + " has no corresponding pair");
        		}
        	} else {
           		throw new JDDPException("Malformed SQL: Dollar Quote at position " + i + " has no corresponding pair");
        	}
        } else if (c == COLON &&  (i + 1) < length && (lookAhead = query.charAt(i+1)) == COLON) {
        	parsedQuery.append(COLON);
        	i++;
        	while (i < length && (c = query.charAt(i)) == COLON) {
        		parsedQuery.append(c);
        		i++;
        	}
        	i--;
        } else if (c == COLON &&  (i + 1) < length && (lookAhead = query.charAt(i+1)) != DOLLAR_QUOTE  && Character.isJavaIdentifierStart(lookAhead) ) {
        	i += 2;
        	StringBuilder name = new StringBuilder();
        	name.append(lookAhead);
        	while (i < length && (c = query.charAt(i)) != DOLLAR_QUOTE && Character.isJavaIdentifierPart(c)) {
        		name.append(c);
        		i++;
        	}
        	i--;

        	if (name.toString().equals(parameterName)) {
        		parsedQuery.append(literalValue);
        	} else {
        		parsedQuery.append(':').append(name);
        	}
        }  else {
        	parsedQuery.append(c);
        }
        
    }  
    return parsedQuery.toString();
}



private static boolean match(char m, String query, int start, int end, boolean escaped, StringBuilder result) {
	char c = 0;
	
	while (start < end && (c= query.charAt(start)) != m) {
		if (escaped && c == '\\') {
			//append and advance pointer since this is an escaped literal
			result.append(c);
			start++;
			c = query.charAt(start); 
		}
		result.append(c);
		start++;
	}
	
	return start < end && (c == m);
	
}

private static PGobject toPGObject(Expression<?> v, Object value, DBType dbt) throws SQLException {
	PGobject pgObject = new PGobject();
	pgObject.setType(dbt.toString());
	
	if (value == null) {
		pgObject.setValue("null");
	} else if (v.isNil()) {
		pgObject.setValue(v.toString());
	} else if (dbt == DBType.TIMESTAMP  || dbt == DBType.TIMESTAMPTZ) {
		if (value instanceof ZonedDateTime) {
			ZonedDateTime zdt = (ZonedDateTime) value;
			pgObject.setValue(OffsetDateTime.from(zdt).toString());
		} else if (value instanceof String) {
			ZonedDateTime zdt = ZonedDateTime.parse((String) value);
			pgObject.setValue(OffsetDateTime.from(zdt).toString());
		}
	} else  if (v.isJSONObject() && !(value instanceof Expression)) {
		pgObject.setValue(JSON.serialize(value));
	} else {
		pgObject.setValue(value.toString());
	}
	
	return pgObject;
}


public static void main(String[] args) {
	
	Map<String, List<Integer>> l = new HashMap<>();
	
	
	String us = "E'sfgsfg\\\'\\sdf' :name $rodel$adfa$dfadf$rodel$";
	String q = JSONBuilder.JSON.serialize(us);
	
	System.out.println(parse(us, l));
	
	
	System.out.println(parse(q, l));
	

	System.out.println(setParameterAsLiteral(   "rodel", "xxxx", " ::rodel = :viado :rodel$c$$c$ :thanks"));
	
	
}

}