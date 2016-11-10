package org.jddp.xml.bind.adapters;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XMLTimeAdapter extends XmlAdapter<String, LocalTime> {

	@Override
	public String marshal(LocalTime value) {
		return value != null ? value.format(DateTimeFormatter.ISO_TIME) : null;
	}


	@Override
	public LocalTime unmarshal(String value) {
		return value != null ? LocalTime.parse(value.trim(), DateTimeFormatter.ISO_TIME) : null;
	}
}
