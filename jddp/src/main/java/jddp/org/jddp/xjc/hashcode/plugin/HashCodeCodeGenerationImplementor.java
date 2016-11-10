package org.jddp.xjc.hashcode.plugin;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.jvnet.jaxb2_commons.plugin.codegenerator.AbstractCodeGenerationImplementor;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;

public class HashCodeCodeGenerationImplementor extends
		AbstractCodeGenerationImplementor<HashCodeArguments> {

	private final Properties strategyConfig;

	public HashCodeCodeGenerationImplementor(JCodeModel codeModel) {
		super(codeModel);
		strategyConfig = new Properties();
		try {
			InputStream strategyConfigFile = this.getClass().getClassLoader().getResourceAsStream("META-INF/jaxb2/plugin/notSoSimpleHashCode.properties");
			if (strategyConfigFile != null) {
				strategyConfig.load(strategyConfigFile);
			}
		}	
		catch (Exception e) {
			
		}
	}

	private void ifHasSetValueAssignPlusValueHashCode(
			HashCodeArguments arguments, JBlock block,
			JExpression valueHashCode, boolean isAlwaysSet,
			boolean checkForNullRequired) {
		arguments.ifHasSetValue(block, isAlwaysSet, checkForNullRequired)
				.assignPlus(arguments.currentHashCode(), valueHashCode);
	}

	private void ifHasSetValueAssignPlusValueCastedToInt(
			HashCodeArguments arguments, JBlock block, boolean isAlwaysSet) {
		ifHasSetValueAssignPlusValueHashCode(arguments, block,
				JExpr.cast(getCodeModel().INT, arguments.value()), isAlwaysSet,
				true);
	}

	@Override
	public void onArray(JBlock block, boolean isAlwaysSet,
			HashCodeArguments arguments) {
		ifHasSetValueAssignPlusValueHashCode(
				arguments,
				block,
				getCodeModel().ref(Arrays.class).staticInvoke("hashCode")
						.arg(arguments.value()), isAlwaysSet, false);

	}

	@Override
	public void onBoolean(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		ifHasSetValueAssignPlusValueHashCode(arguments, block,
				JOp.cond(arguments.value(), JExpr.lit(1231), JExpr.lit(1237)),
				isAlwaysSet, true);
	}

	@Override
	public void onByte(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		ifHasSetValueAssignPlusValueCastedToInt(arguments, block, isAlwaysSet);
	}

	@Override
	public void onChar(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		ifHasSetValueAssignPlusValueCastedToInt(arguments, block, isAlwaysSet);
	}

	@Override
	public void onDouble(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		// long bits = doubleToLongBits(value);
		final JVar bits = block.decl(JMod.FINAL, getCodeModel().LONG, arguments
				.value().name() + "Bits", getCodeModel().ref(Double.class)
				.staticInvoke("doubleToLongBits").arg(arguments.value()));
		// return (int)(bits ^ (bits >>> 32));
		final JExpression valueHashCode = JExpr.cast(getCodeModel().INT,
				JOp.xor(bits, JOp.shrz(bits, JExpr.lit(32))));
		ifHasSetValueAssignPlusValueHashCode(arguments, block, valueHashCode,
				isAlwaysSet, true);
	}

	@Override
	public void onFloat(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		ifHasSetValueAssignPlusValueHashCode(arguments, block,
				getCodeModel().ref(Float.class).staticInvoke("floatToIntBits")
						.arg(arguments.value()), isAlwaysSet, true);
	}

	@Override
	public void onInt(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		ifHasSetValueAssignPlusValueHashCode(arguments, block,
				arguments.value(), isAlwaysSet, true);
	}

	@Override
	public void onLong(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		ifHasSetValueAssignPlusValueHashCode(
				arguments,
				block,
				JExpr.cast(
						getCodeModel().INT,
						arguments.value().xor(
								arguments.value().shrz(JExpr.lit(32)))),
				isAlwaysSet, true);
	}

	@Override
	public void onShort(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		ifHasSetValueAssignPlusValueCastedToInt(arguments, block, isAlwaysSet);
	}

	@Override
	public void onObject(HashCodeArguments arguments, JBlock block,
			boolean isAlwaysSet) {
		
		String leftFullClassName = arguments.value().type().fullName();
		if (leftFullClassName.startsWith("java.util.List<")) {
			String targetType = getTargetType(leftFullClassName.substring("java.util.List<".length(), leftFullClassName.length()-1)); 
			if (targetType != null) {
				JBlock valueNotNullBlock = block._if(arguments.value().ne(JExpr._null()))._then();
				
				JClass jTargetType = getCodeModel().ref(targetType);
				JForEach forEach = valueNotNullBlock.forEach(jTargetType, "element", arguments.value());
				JVar elementVar = forEach.var();
				
				HashCodeArguments arg = new HashCodeArguments(getCodeModel(), arguments.currentHashCode(), arguments.multiplier(), elementVar, elementVar.ne(JExpr._null()));
				onObject(arg, forEach.body(), isAlwaysSet);
				return;
			}
		}
		
		JInvocation hashCode = getHashCode(arguments);
		
		ifHasSetValueAssignPlusValueHashCode(arguments, block, hashCode, isAlwaysSet, true);
	}
	
	private JInvocation getHashCode(HashCodeArguments arguments)  {
		String valueFullClassName = arguments.value().type().fullName();
		JInvocation hashCode = null;
		String targetType = getTargetType(valueFullClassName);
		
		JVar value = arguments.value();
		
		if (targetType != null) {
			try {
				String[] strategy = strategyConfig.getProperty(targetType).split(":");
				if (strategy.length > 1) {
					if (strategy[0].equals("method")) {
						for (int i = 1; i < strategy.length; i++) {
							if (hashCode == null) {
								hashCode = value.invoke(strategy[i]);
							} else {
								hashCode = hashCode.invoke(strategy[i]);
							}
						}
					} else if (strategy[0].equals("adapter")) {
						String adapter = strategy[1];
						Class<?> adapterClass = Class.forName(adapter);
						JClass jAdapterClass = getCodeModel().ref(adapterClass);
						hashCode= jAdapterClass.staticInvoke("hashCode").arg(value);
					} 
				}
			} catch (Exception e) {
			}
		} 
		
		if (hashCode == null)
		{
			hashCode = value.invoke("hashCode");
		}
		
		return hashCode;
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
