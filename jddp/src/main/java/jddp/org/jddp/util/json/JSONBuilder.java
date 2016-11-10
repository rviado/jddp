package org.jddp.util.json;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;


public class JSONBuilder extends GensonBuilder {

	public static final Genson JSON = new JSONBuilder().create();

	GensonBuilder b;
	
	protected JSONBuilder() {
		 withBundle(new JAXBBundle())
		.withConverter(new JSONConverter.NumericConverter(), BigDecimal.class)
		.withConverter(new JSONConverter.ZonedDateTimeConverter(), ZonedDateTime.class)
		.withConverter(new JSONConverter.LocalDateTimeConverter(), LocalDateTime.class)
		.withConverter(new JSONConverter.LocalDateConverter(), LocalDate.class)
		.withConverter(new JSONConverter.LocalTimeConverter(), LocalTime.class)
		.with(new JSONConverter.Resolver())
		//.useMethods(true)
		//.useFields(false)
		.setSkipNull(true);
	}

	
}
