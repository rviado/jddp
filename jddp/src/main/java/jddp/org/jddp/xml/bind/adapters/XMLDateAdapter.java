package org.jddp.xml.bind.adapters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XMLDateAdapter extends XmlAdapter<String, LocalDate> {

	@Override
	public String marshal(LocalDate value) {
		return value != null ? value.format(DateTimeFormatter.ISO_DATE) : null;
	}


	@Override
	public LocalDate unmarshal(String value) {
		return value != null ? LocalDate.parse(value.trim(), DateTimeFormatter.ISO_DATE) : null;
	}

}
