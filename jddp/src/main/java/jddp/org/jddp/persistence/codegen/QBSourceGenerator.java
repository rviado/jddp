package org.jddp.persistence.codegen;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.jddp.expression.BooleanExpression;
import org.jddp.expression.BooleanFieldExpression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.LiteralExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.NumericFieldExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.ObjectFieldExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.StringFieldExpression;
import org.jddp.expression.UUIDFieldExpression;
import org.jddp.expression.ZonedDateTimeFieldExpression;
import org.jddp.persistence.entity.EntityManager;
import org.jddp.persistence.entity.annotation.Entity;
import org.jddp.persistence.entity.annotation.Indeces;
import org.jddp.persistence.entity.annotation.Index;
import org.jddp.persistence.entity.annotation.PrimaryKey;
import org.jddp.persistence.pgsql.util.TypeUtil;
import org.jddp.persistence.sql.DDL;
import org.jddp.persistence.sql.DML;
import org.jddp.persistence.util.DBType;
import org.jddp.util.bean.Bean;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;


public class QBSourceGenerator {
	
	
	static final String ENTITY_MANAGER_IMPL  = "org.jddp.persistence.pgsql.entity.PGEntityManager";
	

	private Set<Class<?>> visited = new HashSet<>();
	
	 /* create our package */
    JPackage jPackage;
    
    /* The Class to generate */
    JDefinedClass jRootClass;
    JFieldVar jPrimaryKey;
    
	String destination;
	String baseDir;
	
	JFieldVar jJSONField;
    
	JFieldVar jEntityManager;
	
	
	public QBSourceGenerator(String baseDir, String destination) {
		this.baseDir = baseDir;
		this.destination = destination;
		
	}

	private Class<?> primaryKeyType;
	private PrimaryKey primaryKey;
	
	
	 /* Creating java code model classes */
     JCodeModel jModel = new JCodeModel();
    
     JClass jFieldExpression = jModel.ref(FieldExpression.class).narrow(jModel.wildcard());
     JClass jStringExpression = jModel.ref(StringExpression.class).narrow(jModel.wildcard());
     JClass jBooleanExpression = jModel.ref(BooleanExpression.class).narrow(jModel.wildcard());
     JClass jNumericExpression = jModel.ref(NumericExpression.class).narrow(jModel.wildcard());
     JClass jObjectExpression = jModel.ref(ObjectExpression.class).narrow(jModel.wildcard());
     
    
     public void parse(Class<?> entity, String rootElement, String package_) throws IntrospectionException, JClassAlreadyExistsException, ClassNotFoundException {
    	 
     	 /* create our package */
    	 if (package_ == null) {
    		 package_ = entity.getPackage().getName();
    	 }
        jPackage = jModel._package(package_);
        
        /* The Root Class to generate */
        jRootClass = jPackage._class(JMod.PUBLIC | JMod.FINAL, "_" + rootElement);
     	
        JDocComment jClassLevelDoc = jRootClass.javadoc();
        jClassLevelDoc.add("Auto generated " + new Date());
        jClassLevelDoc.add("\nDo not modify");
        
        
     	JClass jEntity = jModel.ref(entity);
     	JClass jEntityManagerIntClass = jModel.ref(EntityManager.class);
     	
     	jEntityManager = jRootClass.field(JMod.PRIVATE | JMod.STATIC |JMod.FINAL, jEntityManagerIntClass.narrow(entity) , "em");
     	jEntityManager.init(jEntityManagerIntClass.staticInvoke("newInstance").arg(jEntity.dotclass()).arg(rootElement));
     	
        jJSONField = jRootClass.field(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, getTypeFieldExpressionInterface(Object.class), "$");
        jJSONField.init(getEntityField("$", Object.class));
         
        
     	primaryKey =  entity.getAnnotation(PrimaryKey.class);
     	
 		if (primaryKey != null) {
 			String primaryKeyName = primaryKey.fieldName() == null || primaryKey.fieldName().trim().isEmpty() ? "pkey" : primaryKey.fieldName().trim();
 			
 			primaryKeyType = getFieldType(primaryKey.type());
 			JClass jPKeyExpressionType = jModel.ref(getTypeFieldExpressionInterface(primaryKey.type()));
 			
 			jPrimaryKey = jRootClass.field(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jPKeyExpressionType, "$" + primaryKeyName);
 			jPrimaryKey.init(jEntityManager.invoke("getPrimaryKey").arg(jPKeyExpressionType.dotclass()));
 			
 		} else {
 			throw new IntrospectionException("Entity class has no PrimaryKey defined, must be annotated with " + PrimaryKey.class.getName());
 		}
 		
 		Indeces idx = entity.getAnnotation(Indeces.class);
 		
 		if (idx != null) {
	 		for (Index i : idx.index()) {
	 			JFieldVar jIndexKey = jRootClass.field(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, getTypeFieldExpressionInterface(i.type()), "$" + i.fieldName());
	 			jIndexKey.init(getEntityField(i.fieldName(), i.type()));
	 		}
 		}
 		
		parse(rootElement, entity,  jRootClass);
 		
     }
     
     
     public  void parse(String fieldPrefix, Class<?> clazz, JDefinedClass jEnclosingClass) throws IntrospectionException, JClassAlreadyExistsException, ClassNotFoundException {
		
    	 if (!isEntity(clazz) || visited.contains(clazz)) { 
			return;
		}
		
				
		visited.add(clazz);
		
		int mods;
		if (jEnclosingClass.equals(jRootClass)) {
			mods = JMod.PUBLIC | JMod.STATIC |JMod.FINAL;
		} else {
			mods = JMod.PUBLIC | JMod.FINAL;
		}
		
		boolean isComplexTypeWithSimpleContent = Bean.hasFieldWithAnnotation(clazz, XmlValue.class);
		
		for(PropertyDescriptor pd : Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors()){
			String propertyName = pd.getName();
					
			Method getter = pd.getReadMethod();
			Method setter = pd.getWriteMethod();
			
			if (getter == null && !pd.getPropertyType().isPrimitive() ) {
				String getterMethodName = "is" + StringUtils.capitalize(propertyName);
				getter = MethodUtils.getAccessibleMethod(clazz, getterMethodName, new Class[] {});
			}
			
			if (getter != null && setter != null) {
				
				Class<?> returnType = getter.getReturnType();
				
				boolean isCollection = Collection.class.isAssignableFrom(returnType); 
				
				if (isCollection) {
					returnType = Bean.getMethodReturnComponentType(getter);
				}
				
				boolean isXmlValue = false;
				
				Field field = Bean.getClassField(clazz, propertyName);
				
				if (isComplexTypeWithSimpleContent) {
					boolean isAttribute = Bean.getAnnotation(getter, field, XmlAttribute.class) != null;
					if (!isAttribute) {
						isXmlValue =  Bean.getAnnotation(getter, field, XmlValue.class) != null;
					}	
				}
				
				String fieldName = propertyName;
				XmlElement ann = Bean.getAnnotation(getter, field, XmlElement.class);
				if (ann != null && !"##default".equals(ann.name())) {
					fieldName = ann.name();
				}
				
						
				String xpath;
				
				if (isXmlValue) {
					xpath = fieldPrefix;
				} else {
					xpath = fieldPrefix + "/" + fieldName;
				}
					
				if (!isEntity(returnType) || visited.contains(returnType)) {
					if (!isCollection  ) {
						//access as specific type
						createAccessor(xpath, mods, jEnclosingClass, returnType, propertyName);
					} else {
						
						createElementOfCollectionAccessor(xpath, mods, jEnclosingClass, returnType, propertyName);
						//access collection as an object
						createAccessor(xpath, mods, jEnclosingClass, Object.class, propertyName);
					}
				} else if (!visited.contains(returnType)) {
					String classXPathLastComponent = StringUtils.capitalize(propertyName);
					
					//avoid path collision
					if (classXPathLastComponent.equals(propertyName)) {
						classXPathLastComponent = propertyName + "_";
					}
					
					xpath = fieldPrefix + "/" + classXPathLastComponent;
					
					JClass jObjectFieldInt = jModel.ref(getTypeFieldExpressionInterface(Object.class));
    		    	JDefinedClass newJClass = jEnclosingClass._class(mods, StringUtils.capitalize(propertyName));// ._extends(jObjectFieldImpl);
    		    	JFieldVar j_this = newJClass.field(JMod.PUBLIC | JMod.FINAL, jObjectFieldInt, newJClass.name());
    		    	
					createConstructorAndIntitializeThis(xpath, mods, newJClass, propertyName, jEnclosingClass, j_this, isCollection);
					
					
					parse(fieldPrefix + "/" + fieldName, returnType, newJClass);
					
				} else {
					//access a visited class 
					createAccessor(xpath, mods, jEnclosingClass, returnType, propertyName);
				}
			}
		}
		
		visited.remove(clazz);
    }
    
     
     private void createConstructorAndIntitializeThis(String xpath, int mods, JDefinedClass newJClass, String propertyName,  JDefinedClass jEnclosingClass, JFieldVar j_this, boolean isCollection) throws ClassNotFoundException, JClassAlreadyExistsException {
    	 
    	
    	JClass jObjectFieldInt = jModel.ref(getTypeFieldExpressionInterface(Object.class));
    	
    	boolean isRoot = jEnclosingClass.equals(jRootClass);

    	if (isRoot) {
    		JFieldVar c = jEnclosingClass.field(mods, jObjectFieldInt, newJClass.name());
    		c.init(JExpr._new(newJClass).ref(newJClass.name()));
    	} else {
    		JFieldVar c = jEnclosingClass.field(mods, jObjectFieldInt, newJClass.name());
    		
    		JMethod con = jEnclosingClass.getConstructor(new JType[] {});
    		con.body().assign(c, JExpr._new(newJClass).ref(newJClass.name()));
    		
    		con = jEnclosingClass.getConstructor(new JType[] {jModel.INT});
    		if (con != null) {
    			con.body().assign(c, JExpr._new(newJClass).ref(newJClass.name()));
    		}
    		
    		//c.init(JExpr._new(newJClass).ref(newJClass.name()));
    	}
		JBlock constructorBody = newJClass.constructor(JMod.NONE).body();
		
		JExpression owner = isRoot ? jJSONField : jEnclosingClass.staticRef("this").ref(jEnclosingClass.name());
		
		constructorBody.assign(j_this, newEntityField(xpath, Object.class).arg(owner));
		
		JMethod jMethod = jEnclosingClass.method(mods, newJClass, propertyName);
		jMethod.body()._return(JExpr._new(newJClass));
		
		
		newJClass.method(JMod.PUBLIC | JMod.FINAL, jObjectFieldInt, "_this").body()._return(j_this);
		
		if (isCollection) {
			constructorBody = addIndexedConstructorAndAccessor(xpath, mods, newJClass, propertyName, jEnclosingClass, j_this);
		}
		
     }
     
     
    private JBlock addIndexedConstructorAndAccessor(String xpath, int mods, JDefinedClass newJClass, String methodName, JDefinedClass jEnclosingClass, JFieldVar j_this) throws ClassNotFoundException {
    		
    	 
	    	//create a constructor with an int parameter
			JMethod con = newJClass.constructor(JMod.NONE);
			JVar var = con.param(jModel.INT, "i");
    	
    		
			//code - _this = new ObjField(internal.getField(xpath, Object.class), owner, i) 
			JBlock constructorBody = con.body();
			JExpression owner = jEnclosingClass.equals(jRootClass) ? jJSONField :  jEnclosingClass.staticRef("this").ref(jEnclosingClass.name());
			
			constructorBody.assign(j_this, newEntityField(xpath, Object.class).arg(owner).arg(var));
			
			//code - definedClass methodName(int i) {
			//          return  new definedClass(i)
			//       }  
			JMethod jMethod = jEnclosingClass.method(mods, newJClass, methodName);
			JVar jVar = jMethod.param(jModel.INT, "i");
			jMethod.body()._return(JExpr._new(newJClass).arg(jVar));
			
			return constructorBody;
			
		 
    }
    
    private void createAccessor(String xpath, int mods, JDefinedClass jDefinedClass, Class<?> returnType, String methodName) {
    	
    	boolean isRoot = jDefinedClass.equals(jRootClass);
    	
    	//code : internal.getField(xpath, XXXField.class)
    	JClass jFieldInt = jModel.ref(getTypeFieldExpressionInterface(returnType));

		//code : XXXField field = new XXXField(internal.getField("ItemDataLine/manufacturer/partyName", XXXField.class), owner);
    	JVar jField;
    	if (Bean.isJavaKeyword(methodName)) {
    		jField= jDefinedClass.field(mods , jFieldInt, "_" + methodName);
    	} else {
    		jField= jDefinedClass.field(mods , jFieldInt, methodName);
    	}
		
		
		if (isRoot) {
			jField.init(newEntityField(xpath, returnType).arg(isRoot ? jJSONField : jDefinedClass.staticRef("this").ref(jDefinedClass.name())));
		} else {
			JMethod constructor = jDefinedClass.getConstructor(new JType[]{});
			constructor.body().assign(jField , newEntityField(xpath, returnType).arg(isRoot ? jJSONField : jDefinedClass.staticRef("this").ref(jDefinedClass.name())));
			
			constructor = jDefinedClass.getConstructor(new JType[]{ jModel.INT});
			if (constructor != null) {
				constructor.body().assign(jField , newEntityField(xpath, returnType).arg(isRoot ? jJSONField : jDefinedClass.staticRef("this").ref(jDefinedClass.name())));
			}	
		}
		
    }
    
    private void createElementOfCollectionAccessor(String xpath, int mods, JDefinedClass jDefinedClass, Class<?> returnType, String methodName) throws ClassNotFoundException {
    	
    	boolean isRoot = jDefinedClass.equals(jRootClass);
    	
    	//code : internal.getField(xpath, ObjField.class)
    	JClass jTypeInterface = jModel.ref(getTypeFieldExpressionInterface(returnType));
		
		//code : public final XXXExpression methodName(int i)
		JMethod jMethod = jDefinedClass.method(mods, jTypeInterface, methodName);
		JVar jIVar = jMethod.param(jModel.INT, "i");
		
		
		
		//code : new ObjField(internal.getField("ItemDataLine/manufacturer/partyName", ObjField.class), $, i);
		JExpression owner = isRoot ? jJSONField : jDefinedClass.staticRef("this").ref(jDefinedClass.name());
		JInvocation jNewObjectField = newEntityField(xpath, Object.class).arg(owner).arg(jIVar);
		
		
		
		
		//code : return objectField.castAsXXX()
		String cast = getRequiredCastMethod(returnType);
		if (getRequiredCastMethod(Object.class).equals(cast)) {
			//no need for cast since it is already an ObjectExpression
			jMethod.body()._return(jNewObjectField);
		} else {
			jMethod.body()._return(jNewObjectField.invoke(cast));
			
//			jTypeInterface = jModel.ref(getTypeFieldExpressionInterface(Object.class));
//			jMethod = jDefinedClass.method(mods, jTypeInterface, methodName + "AsObjectField");
//			jMethod.param(jModel.INT, "i");
//			jMethod.body()._return(jNewObjectField);
		}
		
		
    }
    
   

	public void createQueryBuilder(String entityFullClassName, String rootElement, String package_) throws JClassAlreadyExistsException, IOException, ClassNotFoundException, IntrospectionException  {
    	
    	Class<?> entity = Class.forName(entityFullClassName);
    	
    	parse(entity, rootElement, package_);
    	
    	JClass jEntity = jModel.ref(entity);
    	JClass jSet = jModel.ref(Set.class);
    	JClass jList = jModel.ref(List.class);
    	JClass jArrayList = jModel.ref(ArrayList.class);
    	JClass jMap = jModel.ref(Map.class);
    	JClass jHashMap = jModel.ref(HashMap.class);
    	JClass jCollection = jModel.ref(Collection.class);
    	
    	JClass jDBType = jModel.ref(DBType.class);
    	
    	JClass jClass = jModel.ref(Class.class);
    	
    	
    	JClass jPrimaryKeyType = jModel.ref(primaryKeyType);

    	
    	JClass jInterfaceDML = jModel.ref(DML.class);
    	JClass jInterfaceDDL = jModel.ref(DDL.class);
    	
         
        JMethod jMethodGetEntityManager = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jModel.ref(EntityManager.class).narrow(entity), "getEntityManager");
        jMethodGetEntityManager.body()._return(jEntityManager);
        
        JMethod jMethodGetIndeces = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jSet.narrow(jFieldExpression), "getIndeces");
        jMethodGetIndeces.body()._return(jEntityManager.invoke("getIndeces"));
        
        JMethod jMethodGetIndexValue = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jClass, "getIndexValue");
        JTypeVar g = jMethodGetIndexValue.generify("T");
        jMethodGetIndexValue.type(g);
        JVar v1 = jMethodGetIndexValue.param(jFieldExpression, "field");
        JVar v2 = jMethodGetIndexValue.param(jEntity, "entity");
        JVar v3 = jMethodGetIndexValue.param(jClass.narrow(g), "type");
        jMethodGetIndexValue.body()._return(jEntityManager.invoke("getIndexValue").arg(v1).arg(v2).arg(v3));

        JMethod jMethodgetConstraint = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, String.class, "getConstraint");
        v1 = jMethodgetConstraint.param(jFieldExpression, "field");
        jMethodgetConstraint.body()._return(jEntityManager.invoke("getConstraint").arg(v1));
        
        JMethod jMethodIsIndex = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, boolean.class, "isIndexed");
        v1 = jMethodIsIndex.param(jFieldExpression, "field");
        jMethodIsIndex.body()._return(jEntityManager.invoke("getIndeces").invoke("contains").arg(v1));
        
        JMethod jMethodIsPrimaryKey = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, boolean.class, "isPrimaryKey");
        v1 = jMethodIsPrimaryKey.param(jFieldExpression, "field");
        jMethodIsPrimaryKey.body()._return(v1.invoke("equals").arg(jPrimaryKey));
        
        
        JMethod jMethodGetPrimaryKey = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, getTypeFieldExpressionInterface(primaryKey.type()), "getPrimaryKey");
        jMethodGetPrimaryKey.body()._return(jPrimaryKey);
        
        JMethod jMethodGetPrimaryKeyValue = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, primaryKeyType, "getPrimaryKeyValue");
        v1 = jMethodGetPrimaryKeyValue.param(jEntity, "entity");
        jMethodGetPrimaryKeyValue.body()._return(jEntityManager.invoke("getPrimaryKeyValue").arg(v1));
        
        JMethod jMethodGetPrimaryKeyValues = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jList.narrow(primaryKeyType), "getPrimaryKeyValues");
        v1 = jMethodGetPrimaryKeyValues.param(jList.narrow(jEntity), "entities");
        JBlock body = jMethodGetPrimaryKeyValues.body();
        v2 = body.decl(jList.narrow(primaryKeyType), "keys");
        v2.init(JExpr._new(jArrayList.narrow(primaryKeyType)));
        
        JForEach jFor = body.forEach(jEntity, "entity", v1);
        JBlock jForBody = jFor.body();
        JInvocation jInvoc = jRootClass.staticInvoke(jMethodGetPrimaryKeyValue).arg(jFor.var());
        jForBody.add(v2.invoke("add").arg(jInvoc));
        body._return(v2);
        
        JMethod jMethodGetPrimaryKeyToEntityMap = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jMap.narrow(jPrimaryKeyType, jEntity), "getPrimaryKeyToEntityMap");
        v1 = jMethodGetPrimaryKeyToEntityMap.param(jList.narrow(jEntity), "entities");
        body = jMethodGetPrimaryKeyToEntityMap.body();
        v2 = body.decl(jMap.narrow(jPrimaryKeyType, jEntity), "keyToPartyMap");
        v2.init(JExpr._new(jHashMap.narrow(jPrimaryKeyType, jEntity)));
        jFor = body.forEach(jEntity, "entity", v1);
        jForBody = jFor.body();
        jInvoc = jRootClass.staticInvoke(jMethodGetPrimaryKeyValue).arg(jFor.var());
        jForBody.add(v2.invoke("put").arg(jInvoc).arg(jFor.var()));
        body._return(v2);
        
        JMethod jMethodGetXPathAsField = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jFieldExpression, "getXPathAsField");
        v1 = jMethodGetXPathAsField.param(String.class, "xpath");
        jMethodGetXPathAsField.body()._return(jEntityManager.invoke("getFieldExpression").arg(v1));
        
        JMethod jMethodIsValidXPath = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, boolean.class, "isValidXPath");
        v1 = jMethodIsValidXPath.param(String.class, "xpath");
        jMethodIsValidXPath.body()._return(jEntityManager.invoke("getXPaths").invoke("contains").arg(v1));
        
        JMethod jMethodGetXpathsAsString = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, String.class, "getXpathsAsString");
        jMethodGetXpathsAsString.body()._return(jEntityManager.invoke("getXPaths").invoke("toString"));
        
        JMethod jMethodGetXpathsAsCollection = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jCollection.narrow(String.class), "getXpathsAsCollection");
        jMethodGetXpathsAsCollection.body()._return(jEntityManager.invoke("getXPaths"));
        
        JMethod jMethodNewString = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jStringExpression, "newString");
        v1 = jMethodNewString.param(String.class, "string");
        jMethodNewString.body()._return(jEntityManager.invoke("newString").arg(v1));
        
        JMethod jMethodNewNumber = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jNumericExpression, "newNumber");
        v1 = jMethodNewNumber.param(Number.class, "number");
        jMethodNewNumber.body()._return(jEntityManager.invoke("newNumber").arg(v1));
        
        JMethod jMethodNewBoolean = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jBooleanExpression, "newBoolean");
        v1 = jMethodNewBoolean.param(Boolean.class, "bool");
        jMethodNewBoolean.body()._return(jEntityManager.invoke("newBoolean").arg(v1));
        
        JMethod jMethodNewObject = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jObjectExpression, "newObject");
        v1 = jMethodNewObject.param(Object.class, "object");
        jMethodNewObject.body()._return(jEntityManager.invoke("newObject").arg(v1).arg(jDBType.staticRef("JSONB")));
        
        JMethod jMethodNewObject2 = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jObjectExpression, "newObject");
        v1 = jMethodNewObject2.param(jCollection.narrow(jModel.wildcard()), "collection");
        jMethodNewObject2.body()._return(jEntityManager.invoke("newObject").arg(v1).arg(jDBType.staticRef("JSONB")));
        
        JMethod jMethodNewLiteral = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, LiteralExpression.class, "newLiteral");
        v1 = jMethodNewLiteral.param(String.class, "literal");
        jMethodNewLiteral.body()._return(jEntityManager.invoke("newLiteral").arg(v1));
        
//        JMethod jMethodNewNull = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, ObjectExpression.class, "newNull");
//        v1 = jMethodNewNull.param(jModel.ref(Class.class).narrow(jModel.wildcard()), "nullType");
//        jMethodNewNull.body()._return(jEntityManager.invoke("newNull").arg(v1));
        
        JMethod jMethodGetDML = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jInterfaceDML.narrow(jEntity), "getDML");
        jMethodGetDML.body()._return(jEntityManager.invoke("getDML"));

        JMethod jMethodGetDDL = jRootClass.method(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, jInterfaceDDL.narrow(jEntity), "getDDL");
        jMethodGetDDL.body()._return(jEntityManager.invoke("getDDL"));
         
        File destinationFile = new File(baseDir, destination);
        System.out.print("Generated source -> " + destinationFile.getAbsolutePath() + File.separator );
        
        jModel.build(destinationFile);
        
    }
    
    private  boolean isEntity(Class<?> clazz) {
		return clazz.isAnnotationPresent(Entity.class);
	}
    
    
    private Class<?> getTypeFieldExpressionInterface(String type)  {

    	switch (DBType.from(type)) {
		case TIMESTAMP:
		case TIMESTAMPTZ:
			return ZonedDateTimeFieldExpression.class;
		case UUID:
			return UUIDFieldExpression.class;
		case BOOLEAN:
			return BooleanFieldExpression.class;
		case NUMERIC:
			return NumericFieldExpression.class;
		case TEXT:
			return StringFieldExpression.class;
		case JSON: 
		case JSONB:
		default:
			return ObjectFieldExpression.class;
		
		}
	}
    
    private Class<?> getTypeFieldExpressionInterface(Class<?> typeClass) {
	
		if (TypeUtil.isBoolean(typeClass)) {
			return BooleanFieldExpression.class;
		} 
		if (TypeUtil.isNumeric(typeClass)) {
			return NumericFieldExpression.class;
		} 
		if (TypeUtil.isString(typeClass)) {
			return StringFieldExpression.class;
		} 
		
		return ObjectFieldExpression.class;
    }
    
    
    private String getRequiredCastMethod(Class<?> t) {
		if (TypeUtil.isBoolean(t)) {
			return "castAsBoolean";
		} 
		if (TypeUtil.isNumeric(t)) {
			return "castAsNumeric";
		} 
		if (TypeUtil.isString(t)) {
			return "castAsString";
		} 
		return "castAsObject";
		
	}
    
    private Class<?> getFieldType(String type) {
    	switch (DBType.from(type)) {
		case TIMESTAMP:
		case TIMESTAMPTZ:
		case UUID:
		case TEXT:	
			return String.class;
		case BOOLEAN:
			return Boolean.class;
		case NUMERIC:
			return BigDecimal.class;
		case JSON:
		case JSONB:	
		default:
			return Object.class;
		}
 	}
    
    private JInvocation newEntityField(String xpath, Class<?> type) {
    	 return jEntityManager.invoke("new"  + getTypeFieldExpressionInterface(type).getSimpleName()).arg(xpath);
    }
    
    private JInvocation getEntityField(String xpath, Class<?> type)  {
   	 return jEntityManager.invoke("get"  + getTypeFieldExpressionInterface(type).getSimpleName()).arg(xpath);
   }
    
    private JInvocation getEntityField(String xpath, String type)  {
      	 return jEntityManager.invoke("getFieldExpression").arg(xpath).arg(jModel.ref(getTypeFieldExpressionInterface(type)).dotclass());
      }
    
    public static void main(String[] args) throws ClassNotFoundException, JClassAlreadyExistsException, IOException, IntrospectionException {
    	
    	QBSourceGenerator QBSG = new QBSourceGenerator(null, "./src/generated-sources/querybuilder/java");
    	String e = "org.jddp.persistence.sample.SampleExtension";
    	
    	QBSG.createQueryBuilder(e, "Sample", null);

    }
}
