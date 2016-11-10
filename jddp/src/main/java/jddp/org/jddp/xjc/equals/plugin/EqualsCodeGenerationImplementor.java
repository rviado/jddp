package org.jddp.xjc.equals.plugin;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.jvnet.jaxb2_commons.plugin.codegenerator.AbstractCodeGenerationImplementor;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;


public class EqualsCodeGenerationImplementor extends
		AbstractCodeGenerationImplementor<EqualsArguments> {
	
	private final Properties strategyConfig;

	public EqualsCodeGenerationImplementor(JCodeModel codeModel) {
		super(codeModel);
		strategyConfig = new Properties();
		try {
			InputStream strategyConfigFile = this.getClass().getClassLoader().getResourceAsStream("META-INF/jaxb2/plugin/notSoSimpleEquals.properties");
			if (strategyConfigFile != null) {
				strategyConfig.load(strategyConfigFile);
			}
		}	
		catch (Exception e) {
			
		}
	}

	private void returnFalseIfNotEqualsCondition(EqualsArguments arguments,
			JBlock block, boolean isAlwaysSet,
			final JExpression notEqualsCondition) {
		arguments.ifHasSetValue(block, isAlwaysSet, true)
				._if(notEqualsCondition)._then()._return(JExpr.FALSE);
	}

	private void returnFalseIfNe(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		returnFalseIfNotEqualsCondition(arguments, block, isAlwaysSet,
				JOp.ne(arguments.leftValue(), arguments.rightValue()));
	}

	@Override
	public void onArray(JBlock block, boolean isAlwaysSet,
			EqualsArguments arguments) {
		returnFalseIfNotEqualsCondition(
				arguments,
				block,
				isAlwaysSet,
				getCodeModel().ref(Arrays.class).staticInvoke("equals")
						.arg(arguments.leftValue()).arg(arguments.rightValue())
						.not());
	}

	@Override
	public void onBoolean(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		returnFalseIfNe(arguments, block, isAlwaysSet);
	}

	@Override
	public void onByte(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		returnFalseIfNe(arguments, block, isAlwaysSet);
	}

	@Override
	public void onChar(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		returnFalseIfNe(arguments, block, isAlwaysSet);
	}

	@Override
	public void onDouble(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		final JClass Double$class = getCodeModel().ref(Double.class);
		final JExpression leftValueLongBits = Double$class.staticInvoke(
				"doubleToLongBits").arg(arguments.leftValue());
		final JExpression rightValueLongBits = Double$class.staticInvoke(
				"doubleToLongBits").arg(arguments.rightValue());
		returnFalseIfNotEqualsCondition(arguments, block, isAlwaysSet,
				JOp.ne(leftValueLongBits, rightValueLongBits));
	}

	@Override
	public void onFloat(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		final JClass Float$class = getCodeModel().ref(Float.class);
		final JExpression leftValueLongBits = Float$class.staticInvoke(
				"floatToIntBits").arg(arguments.leftValue());
		final JExpression rightValueLongBits = Float$class.staticInvoke(
				"floatToIntBits").arg(arguments.rightValue());
		returnFalseIfNotEqualsCondition(arguments, block, isAlwaysSet,
				JOp.ne(leftValueLongBits, rightValueLongBits));
	}

	@Override
	public void onInt(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		returnFalseIfNe(arguments, block, isAlwaysSet);
	}

	@Override
	public void onLong(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		returnFalseIfNe(arguments, block, isAlwaysSet);
	}

	@Override
	public void onShort(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		returnFalseIfNe(arguments, block, isAlwaysSet);
	}

	@Override
	public void onObject(EqualsArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		
		
		String leftFullClassName = arguments.leftValue().type().fullName();
		
		if (leftFullClassName.startsWith("java.util.List<")) {
			
			String targetType = getTargetType(leftFullClassName.substring("java.util.List<".length(), leftFullClassName.length()-1)); 
			
			if (targetType != null) {
			
				JConditional leftNotNull = block._if(arguments.leftValue().ne(JExpr._null()));

				JBlock leftAndRightNotNullBock = leftNotNull._then()._if(arguments.rightValue().ne(JExpr._null()))._then();
				
				leftNotNull._else()._if(arguments.rightValue().ne(JExpr._null()))._then()._return(JExpr.FALSE);
				
				leftAndRightNotNullBock._if(arguments.leftValue().invoke("size").ne(arguments.rightValue().invoke("size")))._then()._return(JExpr.FALSE);
				
				JClass jTargetType = getCodeModel().ref(targetType);
				JClass iterClass = getCodeModel().ref(Iterator.class).narrow(jTargetType);
				
				
				JVar rightIter = leftAndRightNotNullBock.decl(iterClass, "rightIter", arguments.rightValue().invoke("iterator"));
				
				JVar zvar = leftAndRightNotNullBock.decl(jTargetType, "rightElement");
				JForEach forEach = leftAndRightNotNullBock.forEach(jTargetType, "leftElement", arguments.leftValue());
				
				forEach.body().assign(zvar, rightIter.invoke("next"));
				
				EqualsArguments arg = new EqualsArguments(getCodeModel(), forEach.var(), forEach.var().ne(JExpr._null()), zvar , zvar.ne(JExpr._null()));
				
				onObject(arg, forEach.body(), false);
				
				return;
			}
		}
		
		final JInvocation condition = getEqualsCondition(block, arguments);
		
		returnFalseIfNotEqualsCondition(
				arguments,
				block,
				isAlwaysSet,
				condition.not());
	}
	
	private JInvocation getEqualsCondition(JBlock block, EqualsArguments arguments)  {
		String leftArgumentFullClassName = arguments.leftValue().type().fullName();
		JInvocation equals = null;
		String targetType = getTargetType(leftArgumentFullClassName);
		
		JVar leftValue = arguments.leftValue();
		
		if (targetType != null) {
			try {
				String[] strategy = strategyConfig.getProperty(targetType).split(":");
				if (strategy.length > 1) {
					if (strategy[0].equals("method")) {
						for (int i = 1; i < strategy.length - 1; i++) {
							if (equals == null) {
								equals = leftValue.invoke(strategy[i]);
							} else {
								equals = leftValue.invoke(strategy[i]);
							}
						}
						if (equals == null) {
							equals = leftValue.invoke(strategy[strategy.length - 1]).arg(arguments.rightValue());
						} else {
							equals = equals.invoke(strategy[strategy.length - 1]).arg(arguments.rightValue());
						}	
					} else if (strategy[0].equals("adapter")) {
						String adapter = strategy[1];
						Class<?> adapterClass = Class.forName(adapter);
						JClass jAdapterClass = getCodeModel().ref(adapterClass);
						equals = jAdapterClass.staticInvoke("isEqual").arg(arguments.leftValue()).arg(arguments.rightValue());
					} 
				}
			} catch (Exception e) {
			}
		} 
		
		if (equals == null)
		{
			equals = arguments.leftValue().invoke("equals").arg(arguments.rightValue());
		}
		
		
		return equals;
	}
	
	private boolean isAssignableFrom(String leftArgumentFullClassName, String fullClassName) {
		
		Class<?> cls;
		try {
			cls = Class.forName(fullClassName);
			Class<?> leftArgumentType = Class.forName(leftArgumentFullClassName);
			if (leftArgumentType.isAssignableFrom(cls)) {
				System.out.println(leftArgumentFullClassName + " is assignable from " + fullClassName);
				return true;
			}
		} catch (ClassNotFoundException e) {
			
		}
		return false;
	}
	
	private String getTargetType(String leftArgumentFullClassName) {
		String targetType = null;
		
		try {
			for (Object k : strategyConfig.keySet()) {
				if (leftArgumentFullClassName.equals(k)) {
					targetType = (String) k;
					break;
				} 
				if (isAssignableFrom(leftArgumentFullClassName, (String) k)) {
					targetType = (String) k;
					break;
				}
			}
		} catch (Exception e) {
			
		}
		return targetType;
	}
}
