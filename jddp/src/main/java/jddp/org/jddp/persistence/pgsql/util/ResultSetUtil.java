package org.jddp.persistence.pgsql.util;

import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.jddp.exception.JDDPException;
import org.jddp.expression.Expression;
import org.jddp.expression.UUIDExpression;
import org.jddp.expression.ZonedDateTimeExpression;
import org.jddp.util.json.JSONBuilder;
import org.postgresql.util.PGInterval;
import org.postgresql.util.PGobject;

import com.opencsv.CSVParser;

public class ResultSetUtil {
	
	final Object fetchedObject;
	final Expression<?> e;
	

	public ResultSetUtil(Object fetchedObject, Expression<?> e) {
		this.fetchedObject = fetchedObject;
		this.e = e;
	}
	
	public Object getObject() throws SQLException {
	
		if (e.isJSONObject()) {
			return new ObjectDeserializer(e).deserialize(getAsJSON()); 
		}
		
				
		//TODO use converters
		if (e instanceof ZonedDateTimeExpression && fetchedObject instanceof Timestamp) {
			Timestamp ts = (Timestamp) fetchedObject;
			return ZonedDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
		}
		
		if (e instanceof UUIDExpression && fetchedObject instanceof UUID) {
			//return fetchedObject.toString();
		}
		
		return fetchedObject;
		
	}
	
	
	public Object getAsJSON() throws SQLException {
		if (fetchedObject instanceof String) {
			return fetchedObject;
		} else if (fetchedObject instanceof PGobject) {
			PGobject obj = (PGobject) fetchedObject;
			if ("jsonb".equals(obj.getType()) || "json".equals(obj.getType())) {
				return obj.getValue();
			} else 	if ("record".equals(obj.getType())) {
				if (obj.getValue().length() >= 2) {
					String escapedJson = obj.getValue();
					if (escapedJson.startsWith("(")) { 
						escapedJson =	escapedJson.substring(1, obj.getValue().length() -1 );
						CSVParser csvParser = new CSVParser();
						try {
							return csvParser.parseLine(escapedJson);
						} catch (IOException e) {
							throw new JDDPException(e);
						}
					}
				}
			} else if ("interval".equals(obj.getType())) {
				//fetch as ISO 8601 JSON String
				PGInterval interval = (PGInterval) fetchedObject;
				return JSONBuilder.JSON.serialize(
						"P" + interval.getYears() + "Y" + interval.getMonths() + "M" + interval.getDays() + "DT" 
				            + interval.getHours() + "H" + interval.getMinutes() + "M" + interval.getSeconds() + "S");
			}
		} else if (fetchedObject instanceof Array) {
			Array array = ((Array) fetchedObject);
			String componentType = array.getBaseTypeName();
			if ("jsonb".equals(componentType) || "json".equals(componentType)) {
				return (String[]) array.getArray();
			}
		} 
		
		throw new JDDPException(fetchedObject.getClass().getName() + " cannot be fetched as json");
	}

	
}
