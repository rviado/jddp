package org.jddp.util.json;


import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.XmlElement;

import org.jddp.xml.bind.adapters.XMLDateTimeAdapter;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.reflect.PropertyNameResolver;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import com.owlike.genson.stream.ValueType;



public class JSONConverter {
	
	XMLDateTimeAdapter xmlDateTimeAdapter = new XMLDateTimeAdapter();
	
	public static class ZonedDateTimeConverter implements  Converter<ZonedDateTime> {
		
		@Override
		public ZonedDateTime deserialize(ObjectReader reader, Context arg1)
				throws Exception {
			if (reader.getValueType() == ValueType.INTEGER) {
				Long millis = reader.valueAsLong();
				return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
			}
			
			
			String dt = reader.valueAsString();
			return ZonedDateTime.parse(dt, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		}

		@Override
		public void serialize(ZonedDateTime zonedDateTime, ObjectWriter writer, Context context) throws Exception {
			writer.writeValue(zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
		}
		
		
	}
	
	public static class LocalDateTimeConverter implements  Converter<LocalDateTime> {

		@Override
		public LocalDateTime deserialize(ObjectReader reader, Context arg1)
				throws Exception {
			if (reader.getValueType() == ValueType.INTEGER) {
				Long millis = reader.valueAsLong();
				return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
			} 
			String dt = reader.valueAsString();
			return LocalDateTime.parse(dt, DateTimeFormatter.ISO_DATE_TIME);
		}

		@Override
		public void serialize(LocalDateTime localDateTime, ObjectWriter writer, Context context) throws Exception {
			writer.writeValue(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
		}
	}
	
	
	public static class LocalDateConverter implements  Converter<LocalDate> {

		@Override
		public LocalDate deserialize(ObjectReader reader, Context arg1)
				throws Exception {
			
			if (reader.getValueType() == ValueType.INTEGER) {
				Long millis = reader.valueAsLong();
				return LocalDate.ofEpochDay(millis);
			}
			
			String dt = reader.valueAsString();
			return LocalDate.parse(dt, DateTimeFormatter.ISO_DATE);
		}

		@Override
		public void serialize(LocalDate localDate, ObjectWriter writer, Context context) throws Exception {
			writer.writeValue(localDate.format(DateTimeFormatter.ISO_DATE));
		}
	}
	

	public static class LocalTimeConverter implements  Converter<LocalTime> {

		@Override
		public LocalTime deserialize(ObjectReader reader, Context arg1)
				throws Exception {
			
			if (reader.getValueType() == ValueType.INTEGER) {
				Long nanos = reader.valueAsLong();
				return LocalTime.ofNanoOfDay(nanos);
			}
			
			String dt = reader.valueAsString();
			return LocalTime.parse(dt, DateTimeFormatter.ISO_TIME);
		}

		@Override
		public void serialize(LocalTime localTime, ObjectWriter writer, Context context) throws Exception {
			writer.writeValue(localTime.format(DateTimeFormatter.ISO_TIME));
		}
	}

	public static class NumericConverter implements  Converter<BigDecimal> {

		@Override
		public BigDecimal deserialize(ObjectReader reader, Context arg1)
				throws Exception {
			return new BigDecimal(reader.valueAsString());
		}

		@Override
		public void serialize(BigDecimal numeric, ObjectWriter writer, Context context) throws Exception {
			writer.writeValue(numeric.toString());
		}

	}
	
	public static class Resolver implements PropertyNameResolver {

		public String resolve(int parameterIdx, Constructor<?> fromConstructor) {
			return null;
		}

		
		public String resolve(Field fromField) {
			XmlElement ann = fromField.getAnnotation(XmlElement.class);
			if (ann != null && ann.name() != null && !"##default".equals(ann.name())) {
				return ann.name();
			}
			
			String name = fromField.getName();
			String cname = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			try {
				if (Boolean.class.isAssignableFrom(fromField.getType()) || boolean.class.isAssignableFrom(fromField.getType())) {
					fromField.getDeclaringClass().getMethod("is" + cname);
					return Introspector.decapitalize(cname);
				}	
			} catch (Exception e) {
				try {
					fromField.getDeclaringClass().getMethod("get" + cname);
					return Introspector.decapitalize(cname);
				} catch (Exception e2) {
					
				}	
			}
			
			
			return name;
		}

		public String resolve(Method fromMethod) {
			XmlElement ann = fromMethod.getAnnotation(XmlElement.class);
			if (ann != null && ann.name() != null && !"##default".equals(ann.name())) {
				return ann.name();
			}
			
			String name = fromMethod.getName();
			int length = -1;

			if (name.startsWith("get"))
				length = 3;
			else if (name.startsWith("is"))
				length = 2;
			else if (name.startsWith("set"))
				length = 3;

			if (length > -1 && length < name.length()) {
				return Introspector.decapitalize(name.substring(length));
			} else
				return null;
		}

		public String resolve(int parameterIdx, Method fromMethod) {
			return null;
		}
	}

}
