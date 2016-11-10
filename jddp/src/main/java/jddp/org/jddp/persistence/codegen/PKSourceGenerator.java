package org.jddp.persistence.codegen;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

import org.jddp.persistence.entity.Compositor;
import org.jddp.persistence.entity.annotation.CompositeKey;
import org.jddp.persistence.entity.annotation.PKCompositorFor;
import org.jddp.persistence.entity.annotation.PrimaryKey;
import org.jddp.persistence.pgsql.util.NameGenerator;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;



public class PKSourceGenerator {

    
    // Function to generate CodeModel Class
    public static void generatePKCompositor(Class<?> entityClass, String baseDir, String destination, String package_) {
        try {
        	
        	PrimaryKey pkAnnotation = entityClass.getAnnotation(PrimaryKey.class);
        	if (pkAnnotation == null) {
        		System.err.println(entityClass.getName() + " does not have a PrimaryKey Annotation, exiting");
        		return;
        	}
        	
        	String compositor = pkAnnotation.composite().compositor();
    		
        	String cn = NameGenerator.generateCompositorClassName(entityClass, compositor);
        	
        	String pkg = cn.substring(0,cn.lastIndexOf("."));
        	String className = cn.substring(cn.lastIndexOf(".") + 1);
        	
        	if (package_ != null) {
        		pkg = package_;
        	}
        	
        	/* get the accessors used to compose the key */
        	CompositeKey compAnnotation = pkAnnotation.composite();
        	String[] accessors = compAnnotation.accessors();
        	 
        	/* get the method name of the method to be implemented */
        	String methodName = Compositor.class.getDeclaredMethods()[0].getName();
        	
            /* Creating java code model classes */
            JCodeModel jModel = new JCodeModel();
            
            //get reference to java types we will be using
            JClass jString = jModel.ref(String.class);
            JClass jBigDecimal = jModel.ref(BigDecimal.class);
            JClass jBoolean = jModel.ref(Boolean.class);
            JClass jObject = jModel.ref(Object.class);
            JClass jEntity = jModel.ref(entityClass);
            JClass jPrimaryKey = jModel.ref(PrimaryKey.class);

            //type of the primary key
            JClass jPKType = null;
        	if ("numeric".equals(pkAnnotation.type())) {
        		jPKType = jBigDecimal;
        	} else if ("text".equals(pkAnnotation.type())) {
        		jPKType = jString;
        	} else if ("boolean".equals(pkAnnotation.type())) {
        		jPKType = jBoolean;
        	} else {
        		jPKType = jObject;
        	}
        	
            /* create our package */
            JPackage jPackage = jModel._package(pkg);
            
            /* The Class to generate */
            JDefinedClass jPKCompositor = jPackage._class(className);
            
            /* annotate the class to generate */
            jPKCompositor.annotate(PKCompositorFor.class).param("value", jEntity.dotclass());
            
            //Get a reference to the Compositor Interface and replace generics with concrete types;
            JClass jCompositor = jModel.ref(Compositor.class).narrow(jEntity, jPKType);
            //The class to generate implements the Compositor interface
            jPKCompositor._implements(jCompositor);
           
            /* define static field and initialize it*/
            JFieldVar jKEY_PARTS_ACCESSORS = jPKCompositor.field(JMod.PUBLIC | JMod.FINAL | JMod.STATIC, String[].class, "KEY_PARTS_ACCESSORS");
            jKEY_PARTS_ACCESSORS.init(jEntity.dotclass().invoke("getAnnotation").arg(jPrimaryKey.dotclass()).invoke("composite").invoke("accessors"));

            /* Adding class level coment */
            JDocComment jClassLevelDoc = jPKCompositor.javadoc();
            jClassLevelDoc.add("Auto generated " + new Date());
            jClassLevelDoc.add("\nDo not modify");
           
            /* Adding method to the Class which is public and returns the composed key of type PKType */
            JMethod jComposedKeyMethod = jPKCompositor.method(JMod.PUBLIC, jPKType, methodName);
            
            /* define a parameter named 'entity' of type Entity */
            JVar entityParam = jComposedKeyMethod.param(jEntity, "entity");
            /* annotate the method with @Override to signify that implements the interface method*/
            jComposedKeyMethod.annotate(Override.class);
            
            /* java doc for method */
            jComposedKeyMethod.javadoc().add("Method to compose a primary key");
            
            /* place in a block so scoping is just like in actual java method body. */
            /* Meaning anything we declare inside the block is inaccessible outside the block */
            {
            	/* Adding method body */
                JBlock jBody = jComposedKeyMethod.body();
                
	            /* declare a variable inside method body */
                if (!jPKType.equals(jObject)) { 
		            JVar jComposedKey = jBody.decl(jString, "composedKey");
		            jComposedKey.init(JExpr.lit(""));
                                
		            if (accessors.length == 0) {
		            	System.err.println(entityClass.getName() + " -> PrimaryKey Annotation does not have a an accessor defined, exiting");
		        		return;
		            }
		            
		            for (int i= 0; i < accessors.length; i++) {
		            	String[] getters = accessors[i].split("\\.");
		            	JInvocation result = null;
		            	for (String accessor : getters) {
		            		if (result == null) {
		            			result = entityParam.invoke(accessor);
		            		} else {
		            			result = result.invoke(accessor);
		            		}
		            	}
		            	
		            	jBody.assignPlus(jComposedKey,  result);
		            }
	                
		            /* return  */
		            if (jPKType.equals(jString)) {
		            	jBody._return(jComposedKey);
		            } else {
	            		jBody._return(JExpr._new(jPKType).arg(jComposedKey));
		            }
                } else {
                	
                	JVar jComposedKey = jBody.decl(jObject, "composedKey");
                	
                	for (int i= 0; i < accessors.length; i++) {
		            	String[] getters = accessors[i].split("\\.");
		            	JInvocation result = null;
		            	for (String accessor : getters) {
		            		if (result == null) {
		            			result = entityParam.invoke(accessor);
		            		} else {
		            			result = result.invoke(accessor);
		            		}
		            	}
		            	
		            	jBody.assign(jComposedKey,  result);
		            }
                	
                	jBody._return(jComposedKey);
                }
            }
            
            
            /* Building class at given location */
            File destinationFile = new File(baseDir, destination);
            System.out.print("Generated source -> " + destinationFile.getAbsolutePath() + File.separator );
            jModel.build(destinationFile);
            
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }   
    
    
    public static void main(String[] args) throws ClassNotFoundException {
    	
    	//String cName = "gs1.gepir.gepir_party_ext.xsd._4.PartyDataLineExtensionType";
    	//String cName = "gs1.gln_registries.local_gln_registry_search_result.xsd._1.GLNPartyType";
    	
//    	String cName = "gs1.codelist.xsd._1.Code";
//    	Class<?> ENTITY_TYPE  = Class.forName(cName);

    	
    	//generatePKCompositor(ENTITY_TYPE);
    	
    	
        /* example for loop */
//            JClass iType = model.ref(Integer.class);
//            JForLoop _for = body._for();
//            _for.init(iType.unboxify(), "i", JExpr.lit(0));
//            _for.test(JExpr.ref("i").lt(fieldsParam.ref("length")));
//            _for.update(JExpr.ref("i").incr());
//            _for.body().assignPlus(pkVar,  entityParam.invoke("toString"));
//            _for.body().assignPlus(pkVar,  entityParam.invoke("toString"));
//           
    }
}
