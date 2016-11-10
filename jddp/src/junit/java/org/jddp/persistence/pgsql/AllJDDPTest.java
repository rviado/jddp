package org.jddp.persistence.pgsql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	BooleanExpressionTest.class, 
	NumericeExpressionTest.class, 
	StringExpressionTest.class, 
	ZDTExpressionTest.class,
	ObjectExpressionTest.class,
	UUIDExpressionTest.class,
	LiteralExpressionTest.class,
	DMLTest.class
})
public class AllJDDPTest {

}
