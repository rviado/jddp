package org.jddp.xjc.annotator.plugin;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.istack.NotNull;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;



public class XJCAnnotatorPlugin extends Plugin {

	public static enum JAVA_TYPE {
		BOOLEAN, BYTE, CHAR, DOUBLE, ENUM, FLOAT, INT, LONG,SHORT,STRING;

		public String toString() {
			return name().toLowerCase();
		}
		
	}
	
	public static enum TAG {
		FIELD, ANNOTATION, ARRAY;
		
		public String toString() {
			return name().toLowerCase();
		}
	}
	
	public static enum ATTR_NAME {
		CLASS, NAME, VALUE, TARGET;
		
		public String toString() {
			return name().toLowerCase();
		}
	}
	
	
	static List<String> TARGETS = Arrays.asList("field", "getter", "setter");
	
	public final String NS_URI = "http://dev.persistence.jddp.org";

	ErrorHandler errHandler = null;
	
	@Override
	public String getOptionName() {
		return "Xannotate";
	}

	@Override
	public String getUsage() {
		return "my usage";
	}

	@Override
	public List<String> getCustomizationURIs() {
		return Collections.singletonList(NS_URI);
	}

	@Override
	public boolean isCustomizationTagName(String nsUri, String localName) {
		return NS_URI.equals(nsUri) &&  (isTag(localName) || isJavaType(localName));
	}

	
	@Override
	public boolean run(Outline outline, Options options, ErrorHandler errHandler) throws SAXException {
		try {
			this.errHandler = errHandler;
			processAllClasses(outline, options);
			processSpecificClasses(outline, options);
			processFields(outline, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	public boolean processSpecificClasses(Outline outline, Options options) throws SAXException {
		
		for (ClassOutline classOutline : outline.getClasses()) {
			for (CPluginCustomization customization : classOutline.target.getCustomizations() ) {
				if (!NS_URI.equals(customization.element.getNamespaceURI())) {
					continue;
				}
				try {
					customization.markAsAcknowledged();
					processClassCustomization(customization, classOutline);
				} catch (SAXException e) {
					
				}
			}
		}
		
		return true;
	}
	
	public boolean processAllClasses(Outline outline, Options options) throws SAXException {
		
		for (ClassOutline classOutline : outline.getClasses()) {
			
			for (CPluginCustomization customization : outline.getModel().getCustomizations() ) {
				if (!NS_URI.equals(customization.element.getNamespaceURI())) {
					continue;
				}
				try {
					customization.markAsAcknowledged();
					processClassCustomization(customization, classOutline);
				} catch (Exception e) {
					
				}
			}
		}
		
		return true;
	}
	
	public boolean processFields(Outline outline, Options options) throws SAXException {
		
		for (ClassOutline classOutline : outline.getClasses()) {
			for (FieldOutline fieldOutline : classOutline.getDeclaredFields()) {
				for (CPluginCustomization customization : fieldOutline.getPropertyInfo().getCustomizations()) {
					if (!NS_URI.equals(customization.element.getNamespaceURI())) {
						continue;
					}
					try {
						customization.markAsAcknowledged();
						processFieldCustomization(customization, fieldOutline);
					} catch (Exception e) {
						
					}
					
				}
			}
		}
		
		return true;
	}
	
	
	public void processFieldCustomization(CPluginCustomization customization, FieldOutline fieldOutline) throws SAXException {
		if (customization != null) {
			
			@NotNull Element element = customization.element;
			String annotationTag = element.getLocalName();
			
			if (!isAnnotation(annotationTag)) {
				fatalError("Missing expected 'annotation' tag", customization.locator);
			}
			
			String className = element.getAttributeNS(NS_URI, ATTR_NAME.CLASS.toString());
			String target = element.getAttributeNS(NS_URI, ATTR_NAME.TARGET.toString());
			
			if (className == null || className.isEmpty()) {
				fatalError("Missing expected 'class' attribute in annotation element", customization.locator);
			}
			if (target == null || target.isEmpty()) {
				target = "field";
				warning("'target' attribute is not specified, using 'field' as target ", customization.locator);
			}
			
			
			if (!TARGETS.contains(target)) {
				fatalError(target + " is an invalid 'target' attribute value, it must be one of " + TARGETS, customization.locator);
			}
			
			Class<? extends Annotation> annotationClass = null;
			annotationClass = newAnnotationClass(className, customization.locator);
			
			
			Target atarget = annotationClass.getAnnotation(Target.class);
			if (atarget != null) {
				for (ElementType avalue : atarget.value()) {
					switch (avalue) {
					case TYPE:
						fatalError(annotationClass.getName() + " annotation is not applicable to a field or method declaration", customization.locator);
					case ANNOTATION_TYPE:
						fatalError(annotationClass.getName() + " annotation is not applicable to a field or method declaration", customization.locator);
					case CONSTRUCTOR:
						fatalError(annotationClass.getName() + " annotation is not applicable to a field or method declaration", customization.locator);
					case FIELD:
						break;
					case LOCAL_VARIABLE:
						break;
					case METHOD:
						break;
					case PACKAGE:
						break;
					case PARAMETER:
						break;
					case TYPE_PARAMETER:
						break;
					case TYPE_USE:
						break;
					default:
						break;
						
					}
					
				}
			}
			
			JFieldVar field = JCodeModelUtil.field(fieldOutline);
			JMethod getter = JCodeModelUtil.getter(fieldOutline);
			JMethod setter = JCodeModelUtil.setter(fieldOutline);
			
			
			Map<String, Collection<JAnnotationUse>> annotations = new HashMap<>();
			
			String name = null;
			if (field != null) {
				annotations.put("field", field.annotations());
				name = field.name();
			}
			if (getter != null) {
				annotations.put("getter", getter.annotations());
				name = getter.name();
			}
			if (setter != null) {
				annotations.put("setter", setter.annotations());
				name = setter.name();
			}
			
			if (annotations.get(target) == null) {
				fatalError("There is no '" + target + "' declaration available for annotation. Available targets are " + annotations.keySet(),  customization.locator);
			} 
			
			boolean isRepeatable = annotationClass.getAnnotation(Repeatable.class) != null;
			
			boolean hasAnnotation = false;
			if (!isRepeatable) {
				for  (JAnnotationUse a : annotations.get(target)) {
					if (a.getAnnotationClass().fullName().equals(className)) {
						hasAnnotation = true;
						String msg =  className + " is not reapatable, skipping annotation since '" + name + "' in " + fieldOutline.parent().implClass.fullName() + " is already annotated with " + className;
						msg += "\n" + customization.locator.getSystemId() + " at line " + customization.locator.getLineNumber() + " , column " + customization.locator.getColumnNumber();
						System.out.println(msg);
						errHandler.warning(new SAXParseException(msg, customization.locator));
						break;
					}
				}
			}
			
			if (isRepeatable || !hasAnnotation) {
				JAnnotationUse j = null;
				if (target.equals("field")) {
					j = field.annotate(annotationClass);
				} else if (target.equals("setter")) {
					j = setter.annotate(annotationClass);
				} else {
					j = getter.annotate(annotationClass);
				}
				setAnnotationFields(element,  j, customization.locator);
			}	
		}	
		
	}
	
	
	public void processClassCustomization(CPluginCustomization customization, ClassOutline classOutline) throws SAXException {
		if (customization != null) {
			Element element = customization.element;
			String annotationTag = element.getLocalName();
			
			if (!isAnnotation(annotationTag)) {
				fatalError("Missing expected 'annotation' tag", customization.locator);
			}
			
			String className = element.getAttributeNS(NS_URI, ATTR_NAME.CLASS.toString());
			
			if (className == null || className.isEmpty()) {
				fatalError("Missing expected 'class' attribute in annotation element", customization.locator);
			}
			
			Class<? extends Annotation> annotationClass = null;
			annotationClass = newAnnotationClass(className, customization.locator);
			
			boolean isRepeatable = annotationClass.getAnnotation(Repeatable.class) != null;
			
			boolean hasAnnotation = false;
			if (!isRepeatable) {
				for  (JAnnotationUse a : classOutline.implClass.annotations()) {
					if (a.getAnnotationClass().fullName().equals(className)) {
						hasAnnotation = true;
						String msg =  className + " is not reapatable, skipping annotation since " + classOutline.implClass.fullName() + " is already annotated with " + className;
						msg += "\n" + customization.locator.getSystemId() + " at line " + customization.locator.getLineNumber() + " , column " + customization.locator.getColumnNumber();
						System.out.println(msg);
						errHandler.warning(new SAXParseException(msg, customization.locator));
						break;
					}
				}
			}
			
			if (isRepeatable || !hasAnnotation) {
				JAnnotationUse j = classOutline.implClass.annotate(annotationClass);
				setAnnotationFields(element,  j, customization.locator);
			}	
		}	
		
	}
	
	
	
	private  void setAnnotationFields(Element element, JAnnotationUse ann, Locator locator) throws SAXException {
		
		NodeList childrens = element.getChildNodes();
		
		for (int i=0; i < childrens.getLength(); i++) {
			Node node = childrens.item(i); 
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				 continue;
			}
			
			Element field = (Element) node;
			
			if (NS_URI.equals(field.getNamespaceURI())) {
				
				String fieldTag =  field.getLocalName();
				
				if (!isField(fieldTag)) {
					fatalError("'" + fieldTag + "' is an invalid annotation element, it must be a field", locator);
				}
				
				String fieldNameAttrValue = field.getAttributeNS(NS_URI, ATTR_NAME.NAME.toString());
				 
				if (fieldNameAttrValue == null || fieldNameAttrValue.isEmpty()) {
					fatalError("Missing expected 'name' attribute in field declaration", locator);
				}
				
				JClass jclass = ann.getAnnotationClass();
				
				
				Class<?> aclass = null;
				
				try {
					aclass = Class.forName(jclass.fullName());
				} catch (ClassNotFoundException e) {
					fatalError("Annotation class " + jclass.fullName() + "not found ", locator, e);
				}
				
				Class<?> expectedType = null;
				try {
					Method amethod = aclass.getMethod(fieldNameAttrValue);
					expectedType = amethod.getReturnType();
				} catch (NoSuchMethodException e) {
					fatalError("No such method named '" + fieldNameAttrValue + "' in annotation class " + jclass.fullName() , locator, e);
				} catch (SecurityException e) {
					fatalError("Unable to acquire reference to method named '" + fieldNameAttrValue + "' in annotation class " + jclass.fullName() , locator, e);
				}
						
				Node childNode = field.getFirstChild();
				
				if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element childElement = (Element) childNode;
					
					boolean isInNamespace = NS_URI.equals(childElement.getNamespaceURI());
					
					if (isInNamespace) {
						String fieldChildTag = childElement.getLocalName();
						
						
						boolean isJavaTypeTag = isJavaType(fieldChildTag);
						boolean isAnnotationTag  = isAnnotation(fieldChildTag);
						boolean isArray = isArray(fieldChildTag);

						validateType(fieldNameAttrValue, expectedType, fieldChildTag, locator);
							
						if (isArray) {
							setArrayValues(childElement.getChildNodes(), ann.paramArray(fieldNameAttrValue), locator);
						} else  {
						
							String attrValue = null;
							String attrName = null;
							
							if (isAnnotationTag) {
								attrName = ATTR_NAME.CLASS.toString();
								attrValue = childElement.getAttributeNS(NS_URI, attrName);
							} else if (isJavaTypeTag) {
								attrName = ATTR_NAME.VALUE.toString();
								attrValue = childElement.getAttributeNS(NS_URI, attrName);
							}
							
							if (attrValue == null || attrName == null) {
								fatalError("Expected attribute ' + attrName + ' for " +  fieldChildTag  + " missing ", locator);
							}
									
							if (isAnnotationTag) {
								 Class<? extends Annotation> clazz = newAnnotationClass(attrValue, locator);
								 JAnnotationUse nextAnn = ann.annotationParam(fieldNameAttrValue, clazz);
								 setAnnotationFields((Element) childElement, nextAnn, locator);
								 	 
							} else if (isJavaTypeTag) {
								setAnnotationFieldValue(fieldNameAttrValue, fieldChildTag, attrValue, expectedType, ann, locator);
							}
						}
					}	
				}
			}
		}
	}
	
	
	
	private void setAnnotationFieldValue(String fieldName, String type, String value, Class<?> expectedType, JAnnotationUse ann, Locator locator) throws SAXException  {
		
		switch (JAVA_TYPE.valueOf(type.toUpperCase())) {
		case BOOLEAN:
			ann.param(fieldName, new Boolean(value).booleanValue());
			break;
		case BYTE:
			ann.param(fieldName, new Byte(value).byteValue());
			break;
		case CHAR:
			ann.param(fieldName, value.charAt(0));
			break;
		case DOUBLE:
			ann.param(fieldName, new Double(value).doubleValue());
			break;
		case ENUM:
			Enum<?> e = newEnum(value, expectedType, locator);
			ann.param(fieldName, e);
			break;
		case FLOAT:
			ann.param(fieldName, new Float(value).floatValue());
			break;
		case INT:
			ann.param(fieldName, new Integer(value).intValue());
			break;
		case LONG:
			ann.param(fieldName, new Long(value).longValue());
			break;
		case SHORT:
			ann.param(fieldName, new Short(value).shortValue());
			break;
		case STRING:
			ann.param(fieldName, value);
			break;
		default:
			break;
		}
	}

	private Enum<?> newEnum(String value, Class<?> expectedType, Locator locator) throws SAXException  {
		try {
			String className = value.substring(0, value.lastIndexOf("."));
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Class<Enum> enumClass = (Class<Enum>) Class.forName(className);
			@SuppressWarnings("unchecked")
			Enum<?> e = Enum.valueOf(enumClass, value.substring(value.lastIndexOf(".")+1));
			
			if (expectedType != null && (!expectedType.isAssignableFrom(e.getClass()))) {
				fatalError(expectedType.getName() + " is incompatible with " + value , locator);
			} 
			
			return e;
		} catch (ClassNotFoundException e) {
			fatalError("Annotation class not found " + value, locator);
			return null;
		}
	}
	
	private Class<? extends Annotation> newAnnotationClass(String className, Locator locator) throws SAXException {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Annotation> clazz  = (Class<Annotation>) Class.forName(className);
			return clazz;
		} catch (ClassNotFoundException e) {
			fatalError("Annotation class not found " + className, locator);
			return null;
		}
	}
	
	private void setArrayValues(NodeList elements, JAnnotationArrayMember array, Locator locator) throws SAXException {

		String expectedType = null;
		Class<?> expectedClassType = null;
		
		for (int idx = 0; idx < elements.getLength(); idx++) {
			Node node = elements.item(idx);
			
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			
			Element element = (Element) node;
			
			String typeTag = element.getLocalName();

			if (expectedType == null) {
				expectedType = typeTag;
			}
			
			if (!expectedType.equals(typeTag)) {
				fatalError("Annotation field of type array must have elements of the same type (" + expectedType + " != " + typeTag + ")", locator);
			} 
			
			boolean isAnnotationTag = isAnnotation(typeTag);
			boolean isJavaTypeTag = isJavaType(typeTag);
		
			//for array only annotation and primitive is allowed to be a member
			if (!isAnnotationTag && !isJavaTypeTag) {
				fatalError("Annotation field of type array must have elements of type Annotation or Primitive ("  + typeTag + ")", locator);
			}
			
			if (isAnnotationTag || isJavaTypeTag) {
				
				ATTR_NAME attrName = null;
				
				if (isAnnotationTag) {
					attrName = ATTR_NAME.CLASS;
				} else if (isJavaTypeTag) {
					attrName = ATTR_NAME.VALUE;
				}
				
				String attrValue = element.getAttributeNS(NS_URI, attrName.toString());
							
				if (attrValue == null || attrValue.isEmpty()) {
					fatalError("annotation expects a " + attrName + " attribute", locator);
				} 
					
				if (isAnnotationTag) {
					 Class<? extends Annotation> clazz = newAnnotationClass(attrValue, locator);
					 JAnnotationUse nextAnn = array.annotate(clazz);
					 setAnnotationFields((Element) node, nextAnn, locator);
				} else if (isJavaTypeTag) {
					
					switch (JAVA_TYPE.valueOf(typeTag.toUpperCase())) {
					case BOOLEAN:
						array.param(new Boolean(attrValue).booleanValue());
						break;
					case BYTE:
						array.param(new Byte(attrValue).byteValue());
						break;
					case CHAR:
						array.param(attrValue.charAt(0));
						break;
					case DOUBLE:
						array.param(new Double(attrValue).doubleValue());
						break;
					case ENUM:
						Enum<?> en = newEnum(attrValue, expectedClassType, locator);
						if (expectedClassType == null) {
							expectedClassType = en.getClass();
						}
						array.param(en);
						break;
					case FLOAT:
						array.param(new Float(attrValue).floatValue());
						break;
					case INT:
						array.param(new Integer(attrValue).intValue());
						break;
					case LONG:
						array.param(new Long(attrValue).longValue());
						break;
					case SHORT:
						array.param(new Short(attrValue).shortValue());
						break;
					case STRING:
						array.param(attrValue);
						break;
					default:
						break;
					}
				}
			}				
		}
	}
	
	
	private void validateType(String name, Class<?> expectedType, String declaredType, Locator locator) throws SAXException {
		boolean isValueType = isJavaType(declaredType);
		boolean isAnnotation  = isAnnotation(declaredType);
		boolean isArray = isArray(declaredType);
		boolean isEnum = isEnum(declaredType);
		boolean isString = isString(declaredType);
		
		if (expectedType.isAnnotation() && !isAnnotation) {
			fatalError("Type '" + declaredType + "' mismatch on '" + name + "' it must be an annotation of type " + expectedType.getName(), locator);
		} else
		if (expectedType.isEnum() && !isEnum) {
			fatalError("Type '" + declaredType + "' mismatch on '" + name + "' it must be an enum of type " + expectedType.getName(), locator);
		} else
		if (expectedType == String.class && !isString) {
			fatalError("Type '" + declaredType + "' mismatch on '" + name + "' it must be a string type", locator);
		} else	
		if (expectedType.isPrimitive() && !isValueType) {
			fatalError("Type '" + declaredType + "' mismatch on '" + name + "' it must be a " + expectedType.getSimpleName().toLowerCase() + " type", locator);
		} else 
		if (expectedType.isArray() && !isArray) {
			fatalError("Type '" + declaredType + "' mismatch on '" + name + "' it must be an array", locator);
		} else
		if (!isAnnotation && !isArray &&!isValueType) {
			fatalError("Type  '" + declaredType + "' on '" + name +  "' is invalid", locator);
		}
	}
	
	
	private boolean isJavaType(String localName) {
		for (JAVA_TYPE p : JAVA_TYPE.values()) {
			if (p.toString().equals(localName)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isTag(String localName) {
		for (TAG t : TAG.values()) {
			if (t.toString().equals(localName)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isAnnotation(String localName) {
		return TAG.ANNOTATION.toString().equals(localName);
	}
	
	private boolean isArray(String localName) {
		return TAG.ARRAY.toString().equals(localName);
	}
	
	private boolean isField(String localName) {
		return TAG.FIELD.toString().equals(localName);
	}
	
	private boolean isEnum(String localName) {
		return JAVA_TYPE.ENUM.toString().equals(localName);
	}
	
	private boolean isString(String localName) {
		return JAVA_TYPE.STRING.toString().equals(localName);
	}
	
	private void fatalError(String msg, Locator locator, Exception e) throws SAXException {
		SAXParseException se = new SAXParseException(msg, locator, e);
		errHandler.fatalError(se);
		throw se;
	}
	
	private void fatalError(String msg, Locator locator) throws SAXException {
		SAXParseException se = new SAXParseException(msg, locator);
		errHandler.fatalError(se);
		throw se;
	}
	
	private void warning(String msg, Locator locator) throws SAXException  {
		SAXParseException se = new SAXParseException(msg, locator);
		errHandler.warning(se);
	}
}
