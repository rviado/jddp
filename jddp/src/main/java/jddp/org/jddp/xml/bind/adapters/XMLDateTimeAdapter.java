package org.jddp.xml.bind.adapters;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XMLDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

	@Override
	public String marshal(ZonedDateTime value) {
		return value != null ? value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) : null;
	}


	@Override
	public ZonedDateTime unmarshal(String value) {
		if (value == null) {
			return null;
		}
		
		try {
			Long millis = Long.valueOf(value);
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
		} catch (NumberFormatException e) {
		}	
		
		
		return ZonedDateTime.parse(value.trim(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

}
