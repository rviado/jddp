package org.jddp.util.json;

import static com.owlike.genson.reflect.TypeUtil.expandType;
import static com.owlike.genson.reflect.TypeUtil.getRawClass;
import static com.owlike.genson.reflect.TypeUtil.lookupGenericType;
import static com.owlike.genson.reflect.TypeUtil.resolveTypeVariable;
import static com.owlike.genson.reflect.TypeUtil.typeOf;
import static com.owlike.genson.reflect.TypeUtil.wrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jddp.exception.JDDPException;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Factory;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.Operations;
import com.owlike.genson.Trilean;
import com.owlike.genson.annotation.HandleBeanView;
import com.owlike.genson.annotation.HandleClassMetadata;
import com.owlike.genson.convert.ChainedFactory;
import com.owlike.genson.convert.ContextualFactory;
import com.owlike.genson.convert.DefaultConverters.DateConverter;
import com.owlike.genson.convert.DefaultConverters.WrappedRootValueConverter;
import com.owlike.genson.ext.GensonBundle;
import com.owlike.genson.reflect.BeanCreator;
import com.owlike.genson.reflect.BeanMutatorAccessorResolver;
import com.owlike.genson.reflect.BeanProperty;
import com.owlike.genson.reflect.BeanPropertyFactory;
import com.owlike.genson.reflect.PropertyAccessor;
import com.owlike.genson.reflect.PropertyMutator;
import com.owlike.genson.reflect.PropertyNameResolver;
import com.owlike.genson.reflect.TypeUtil;
import com.owlike.genson.reflect.VisibilityFilter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

public class JAXBBundle extends GensonBundle {

	 private final DatatypeFactory dateFactory;
	  private boolean wrapRootValues = false;

	  public JAXBBundle() {
	    try {
	      dateFactory = DatatypeFactory.newInstance();
	    } catch (DatatypeConfigurationException dce) {
	      throw new IllegalStateException("Could not obtain an instance of DatatypeFactory.", dce);
	    }
	  }

	  /**
	   * When enabled allows to use @XmlRootElement annotation on root objects to wrap them inside a object under some key.
	   * The key by default will be the class name with the first letter to lower case.
	   */
	  public JAXBBundle wrapRootValues(boolean enable) {
	    wrapRootValues = enable;
	    return this;
	  }

	  @Override
	  public void configure(GensonBuilder builder) {
	    // forcing here the order of GensonAnnotationsResolver and AnnotationPropertyNameResolver allows
	    // us to give them preference over Jaxb annotations. We can not assume it true for any bundle,
	    // as in some cases a bundle might want to have preference over all std Genson components
	    builder.withConverters(new XMLGregorianCalendarConverter(), new DurationConveter())
	      .with(new BeanMutatorAccessorResolver.GensonAnnotationsResolver(), new JaxbAnnotationsResolver())
	      .with(new PropertyNameResolver.AnnotationPropertyNameResolver(), new JaxbNameResolver())
	      .withConverterFactory(new EnumConverterFactory())
	      .withBeanPropertyFactory(new JaxbBeanPropertyFactory())
	      .withContextualFactory(new XmlTypeAdapterFactory());

	    if (wrapRootValues)
	      builder.withConverterFactory(new ChainedFactory() {
			@Override
	        protected Converter<?> create(Type type, Genson genson, Converter<?> nextConverter) {
	          Class<?> clazz = TypeUtil.getRawClass(type);
	          XmlRootElement ann = clazz.getAnnotation(XmlRootElement.class);

	          if (ann != null) {
	            String name = "##default".equals(ann.name()) ? firstCharToLower(clazz.getSimpleName()) : ann.name();
	            return new WrappedRootValueConverter<Object>(name, name, (Converter<Object>) nextConverter);
	          }
	          return null;
	        }
	      });
	  }

	  private String firstCharToLower(String str) {
	    return Character.toLowerCase(str.charAt(0)) + (str.length() > 0 ? str.substring(1) : "");
	  }

	  private class DurationConveter implements Converter<Duration> {
	    @Override
	    public void serialize(Duration object, ObjectWriter writer, Context ctx) {
	      writer.writeValue(object.toString());
	    }

	    @Override
	    public Duration deserialize(ObjectReader reader, Context ctx) {
	      return dateFactory.newDuration(reader.valueAsString());
	    }
	  }

	  @HandleClassMetadata
	  @HandleBeanView
	  private class XMLGregorianCalendarConverter implements Converter<XMLGregorianCalendar> {
	    private final DateConverter converter = new DateConverter();
	    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD'T'hh:mm:ssZ");

	    @Override
	    public void serialize(XMLGregorianCalendar object, ObjectWriter writer, Context ctx) {
	      converter.serialize(object.toGregorianCalendar().getTime(), writer, ctx);
	    }

	    @Override
	    public synchronized XMLGregorianCalendar deserialize(ObjectReader reader, Context ctx) {
	      GregorianCalendar cal = new GregorianCalendar();
	      try {
	        cal.setTime(dateFormat.parse(reader.valueAsString()));
	      } catch (ParseException e) {
	        throw new JsonBindingException("Could not parse date "
	          + reader.valueAsString(), e);
	      }

	      return dateFactory.newXMLGregorianCalendar(cal);
	    }

	  }

	  private class XmlTypeAdapterFactory implements ContextualFactory<Object> {
	    @Override
	    public Converter<Object> create(BeanProperty property, Genson genson) {
	      XmlJavaTypeAdapter ann = property.getAnnotation(XmlJavaTypeAdapter.class);
	      Converter<Object> converter = null;

	      if (ann != null) {
	        Class<? extends XmlAdapter> adapterClass = ann.value();
	        Type adapterExpandedType = expandType(
	          lookupGenericType(XmlAdapter.class, adapterClass), adapterClass);
	        Type adaptedType = typeOf(0, adapterExpandedType);
	        Type originalType = typeOf(1, adapterExpandedType);
	        Type propertyType = property.getType();

	        if (getRawClass(propertyType).isPrimitive())
	          propertyType = wrap(getRawClass(propertyType));

	        XmlElement el = property.getAnnotation(XmlElement.class);  
	        Type xmlElementType = el != null && el.type() != XmlElement.DEFAULT.class ? el.type() : null;

	        // checking type consistency
	        if (xmlElementType != null) {
	          if (!match(adaptedType, xmlElementType, false))
	            throw new ClassCastException("The BoundType of XMLAdapter " + adapterClass
	              + " is not assignable from property " + property.getName()
	              + " declared in " + property.getDeclaringClass());
	            
	        } 
	        else if (!match(propertyType, originalType, false)) 
	          throw new ClassCastException("The BoundType of XMLAdapter " + adapterClass
	            + " is not assignable from property " + property.getName()
	            + " declared in " + property.getDeclaringClass());

	        try {
	          XmlAdapter adapter = adapterClass.newInstance();
	          // we also need to find a converter for the adapted type
	          Class<?> clazz = getRawClass(propertyType);
	          Class<?> oClazz = getRawClass(originalType);
	          if (Collection.class.isAssignableFrom(clazz) && !oClazz.isAssignableFrom(clazz)) {
	        	  adaptedType = clazz;
	          }
	          Converter<Object> adaptedTypeConverter = genson.provideConverter(
	            xmlElementType != null ? xmlElementType : adaptedType);
	          converter = new AdaptedConverter(adapter, adaptedTypeConverter);
	        } catch (InstantiationException e) {
	          throw new JsonBindingException(
	            "Could not instantiate XmlAdapter of type " + adapterClass, e);
	        } catch (IllegalAccessException e) {
	          throw new JsonBindingException(
	            "Could not instantiate XmlAdapter of type " + adapterClass, e);
	        }
	      }
	      return converter;
	    }

	    private class AdaptedConverter implements Converter<Object> {
	      private final XmlAdapter<Object, Object> adapter;
	      private final Converter<Object> converter;

	      public AdaptedConverter(XmlAdapter<Object, Object> adapter, Converter<Object> converter) {
	        super();
	        this.adapter = adapter;
	        this.converter = converter;
	      }

	      @Override
	      public Object deserialize(ObjectReader reader, Context ctx) throws Exception {
       	   Object value = converter.deserialize(reader, ctx);;
	    	
	        
	        try {
	        	if (value instanceof Collection) {
		        	try {	
		        		adapter.getClass().getDeclaredMethod("unmarshal", value.getClass());
		        		return adapter.unmarshal(value);
		        	} catch (NoSuchMethodException e) {
		        		List<Object> a = new ArrayList<>();
			  	        for (Object b : (Collection<?>)value) {
			  	        	a.add(adapter.unmarshal(b));
			  	        }
			  	        return a;
		        	}
	        	}
	          
	        	return adapter.unmarshal(value);
	        } catch (Exception e) {
	          throw new JsonBindingException("Could not unmarshal object using adapter "
	            + adapter.getClass());
	        }
	      }

	      @Override
	      public void serialize(Object object, ObjectWriter writer, Context ctx) throws Exception {
	        Object adaptedValue = null;
	        try {
	        	if (object instanceof Collection) {
	        		try {	
		        		adapter.getClass().getDeclaredMethod("marshal", object.getClass());
		        		adaptedValue = adapter.marshal(object);
		        	} catch (NoSuchMethodException e) {
		        		List<Object> a = new ArrayList<>();
			  	        for (Object b : (Collection<?>)object) {
			  	          a.add(adapter.marshal(b));
			  	        }
			  	        adaptedValue = a;
		        	}
	        	} else {
	        		adaptedValue = adapter.marshal(object);
	        	}	
	        } catch (Exception e) {
	          throw new JsonBindingException("Could not marshal object using adapter "
	            + adapter.getClass());
	        }
	        converter.serialize(adaptedValue, writer, ctx);
	      }
	    }
	  }

	  private class EnumConverterFactory implements Factory<Converter<Enum<?>>> {

	    @Override
	    public Converter<Enum<?>> create(Type type, Genson genson) {
	      Class<?> rawClass = getRawClass(type);
	      if (rawClass.isEnum() || Enum.class.isAssignableFrom(rawClass)) {
	        @SuppressWarnings({"unchecked"})
	        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) rawClass;

	        try {
	          Map<String, Enum<?>> valueToEnum = new HashMap<String, Enum<?>>();
	          Map<Enum<?>, String> enumToValue = new HashMap<Enum<?>, String>();
	          for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
	            XmlEnumValue ann = rawClass.getField(enumConstant.name()).getAnnotation(
	              XmlEnumValue.class);

	            if (ann != null) {
	              valueToEnum.put(ann.value(), enumConstant);
	              enumToValue.put(enumConstant, ann.value());
	            } else {
	              valueToEnum.put(enumConstant.name(), enumConstant);
	              enumToValue.put(enumConstant, enumConstant.name());
	            }
	          }

	          return new EnumConverter(valueToEnum, enumToValue);
	        } catch (SecurityException e) {
	          throw new JsonBindingException("Unable to introspect enum "
	            + enumClass, e);
	        } catch (NoSuchFieldException e) {
	        }
	      }

	      // otherwise let genson standard converter handle the conversion
	      return null;
	    }

	    @HandleClassMetadata
	    @HandleBeanView
	    private class EnumConverter implements Converter<Enum<?>> {
	      private final Map<String, Enum<?>> valueToEnum;
	      private final Map<Enum<?>, String> enumToValue;

	      public EnumConverter(Map<String, Enum<?>> valueToEnum, Map<Enum<?>, String> enumToValue) {
	        super();
	        this.valueToEnum = valueToEnum;
	        this.enumToValue = enumToValue;
	      }

	      @Override
	      public void serialize(Enum<?> object, ObjectWriter writer, Context ctx) {
	        writer.writeUnsafeValue(enumToValue.get(object));
	      }

	      @Override
	      public Enum<?> deserialize(ObjectReader reader, Context ctx) {
	        return valueToEnum.get(reader.valueAsString());
	      }
	    }
	  }

	  private class JaxbBeanPropertyFactory implements BeanPropertyFactory {

	    @Override
	    public PropertyAccessor createAccessor(String name, Field field, Type ofType, Genson genson) {
	      Type newType = getType(field, field.getGenericType(), ofType);
	      if (newType != null) {
	        return new PropertyAccessor.FieldAccessor(name, field, newType, getRawClass(ofType));
	      }

	      return null;
	    }

	    @Override
	    public PropertyAccessor createAccessor(String name, Method method, Type ofType,
	                                           Genson genson) {
	      Type newType = getType(method, method.getReturnType(), ofType);
	      if (newType != null) {
	        return new PropertyAccessor.MethodAccessor(name, method, newType,
	          getRawClass(ofType));
	      }
	      return null;
	    }

	    @Override
	    public PropertyMutator createMutator(String name, Field field, Type ofType, Genson genson) {
	      Type newType = getType(field, field.getGenericType(), ofType);
	      if (newType != null) {
	        return new PropertyMutator.FieldMutator(name, field, newType, getRawClass(ofType));
	      }

	      return null;
	    }

	    @Override
	    public PropertyMutator createMutator(String name, Method method, Type ofType, Genson genson) {
	      if (method.getParameterTypes().length == 1) {
	        Type newType = getType(method, method.getReturnType(), ofType);
	        if (newType != null) {
	          return new PropertyMutator.MethodMutator(name, method, newType,
	            getRawClass(ofType));
	        }
	      }
	      return null;
	    }

	    @Override
	    public BeanCreator createCreator(Type ofType, Constructor<?> ctr, String[] resolvedNames,
	                                     Genson genson) {
	      return null;
	    }

	    @Override
	    public BeanCreator createCreator(Type ofType, Method method, String[] resolvedNames,
	                                     Genson genson) {
	      return null;
	    }

	    private Type getType(AccessibleObject object, Type objectType, Type contextType) {
	      XmlElement el = object.getAnnotation(XmlElement.class);
	      if (el != null && el.type() != XmlElement.DEFAULT.class) {
	        if (!TypeUtil.getRawClass(objectType).isAssignableFrom(el.type())) {
	          if (objectType instanceof ParameterizedType) {
	        	 ParameterizedType ptype = (ParameterizedType) objectType;
	        	 if (ptype.getTypeName().startsWith(List.class.getName())) {
		        	 Type[] typeArguments = ptype.getActualTypeArguments();
					    for(Type typeArgument : typeArguments){
					        objectType = typeArgument;
					        break;
					    }
					    
					    XmlJavaTypeAdapter ad = object.getAnnotation(XmlJavaTypeAdapter.class);
					    
					    if (!TypeUtil.getRawClass(objectType).isAssignableFrom(el.type()) && ad == null) {
					          throw new ClassCastException("Invalid XmlElement annotation, " + objectType
					          + " is not assignable from " + el.type());
					    } else if (ad == null) {
					    	return null;
					    }
	        	 }	    
	          } else {
	        	  XmlJavaTypeAdapter ad = object.getAnnotation(XmlJavaTypeAdapter.class);
		          if (ad == null) 
		            throw new ClassCastException("Invalid XmlElement annotation, " + objectType
		              + " is not assignable from " + el.type());
	          }
	        }
	        return el.type();
	      } else
	        return null;
	    }
	  }

	  private class JaxbNameResolver implements PropertyNameResolver {
	    private final static String DEFAULT_NAME = "##default";

	    @Override
	    public String resolve(int parameterIdx, Constructor<?> fromConstructor) {
	      return null;
	    }

	    @Override
	    public String resolve(int parameterIdx, Method fromMethod) {
	      return null;
	    }

	    @Override
	    public String resolve(Field fromField) {
	      return extractName(fromField);
	    }

	    @Override
	    public String resolve(Method fromMethod) {
	      return extractName(fromMethod);
	    }

	    private String extractName(AccessibleObject object) {
	      String name = null;
	      XmlAttribute attr = object.getAnnotation(XmlAttribute.class);
	      if (attr != null)
	        name = attr.name();
	      else {
	        XmlElement el = object.getAnnotation(XmlElement.class);
	        if (el != null) name = el.name();
	      }
	      return DEFAULT_NAME.equals(name) ? null : name;
	    }
	  }

	  private class JaxbAnnotationsResolver extends BeanMutatorAccessorResolver.PropertyBaseResolver {
	    @Override
	    public Trilean isAccessor(Field field, Class<?> fromClass) {
	      if (ignore(field, field.getType(), fromClass)) return Trilean.FALSE;
	      if (include(field, field.getType(), fromClass)) return Trilean.TRUE;
	      return shouldResolveField(field, fromClass);
	    }

	    @Override
	    public Trilean isMutator(Field field, Class<?> fromClass) {
	      if (ignore(field, field.getType(), fromClass)) return Trilean.FALSE;
	      if (include(field, field.getType(), fromClass)) return Trilean.TRUE;
	      return shouldResolveField(field, fromClass);
	    }

	    @Override
	    public Trilean isAccessor(Method method, Class<?> fromClass) {
	      if (ignore(method, method.getReturnType(), fromClass)) return Trilean.FALSE;

	      String name = null;
	      if (method.getName().startsWith("get") && method.getName().length() > 3)
	        name = method.getName().substring(3);
	      else if (method.getName().startsWith("is") && method.getName().length() > 2
	        && method.getReturnType() == boolean.class
	        || method.getReturnType() == Boolean.class)
	        name = method.getName().substring(2);

	      if (name != null) {
	        if (include(method, method.getReturnType(), fromClass)) return Trilean.TRUE;
	        if (find(XmlTransient.class, fromClass, "set" + name, method.getReturnType()) != null)
	          return Trilean.FALSE;


	        return shouldResolveMethod(method, fromClass);
	      }

	      return Trilean.FALSE;
	    }

	    @Override
	    public Trilean isMutator(Method method, Class<?> fromClass) {
	      Class<?> paramClass = method.getParameterTypes().length == 1 ? method
	        .getParameterTypes()[0] : Object.class;
	      if (ignore(method, paramClass, fromClass)) return Trilean.FALSE;

	      if (method.getName().startsWith("set") && method.getName().length() > 3) {
	        if (include(method, method.getReturnType(), fromClass)) return Trilean.TRUE;

	        String name = method.getName().substring(3);

	        // Exclude it if there is a corresponding accessor annotated with XmlTransient
	        if (find(XmlTransient.class, fromClass, "get" + name) != null) return Trilean.FALSE;
	        if (paramClass.equals(boolean.class) || paramClass.equals(Boolean.class)) {
	          if (find(XmlTransient.class, fromClass, "is" + name) != null)
	            return Trilean.FALSE;
	        }


	        return shouldResolveMethod(method, fromClass);
	      }

	      return Trilean.FALSE;
	    }

	    private Trilean shouldResolveField(Field field, Class<?> fromClass) {
	      XmlAccessorType ann = find(XmlAccessorType.class, field, fromClass);

	      if (isDefaultVisibilityMember(field, ann) || isValidPublicMember(field, ann) || isValidFieldMember(field, ann)) {
	        return Trilean.TRUE;
	      } else {
	        return Trilean.FALSE;
	      }
	    }

	    private Trilean shouldResolveMethod(Method m, Class<?> fromClass) {
	      XmlAccessorType ann = find(XmlAccessorType.class, m, fromClass);

	      if (isDefaultVisibilityMember(m, ann) || isValidPropertyMember(m, ann) || isValidPublicMember(m, ann)) {
	        return Trilean.TRUE;
	      } else {
	        return Trilean.FALSE;
	      }
	    }

	    private boolean isDefaultVisibilityMember(Member m, XmlAccessorType xmlAccessTypeAnn) {
	      return xmlAccessTypeAnn == null && VisibilityFilter.PACKAGE_PUBLIC.isVisible(m);
	    }

	    private boolean isValidFieldMember(Member m, XmlAccessorType ann) {
	      return ann != null && ann.value() == XmlAccessType.FIELD && VisibilityFilter.PRIVATE.isVisible(m);
	    }

	    private boolean isValidPublicMember(Member m, XmlAccessorType ann) {
	      return ann != null && ann.value() == XmlAccessType.PUBLIC_MEMBER && VisibilityFilter.PACKAGE_PUBLIC.isVisible(m);
	    }

	    private boolean isValidPropertyMember(Member m, XmlAccessorType ann) {
	      return ann != null && ann.value() == XmlAccessType.PROPERTY && VisibilityFilter.PACKAGE_PUBLIC.isVisible(m);
	    }

	    private boolean ignore(AccessibleObject property, Class<?> ofType, Class<?> fromClass) {
	      XmlTransient xmlTransientAnn = find(XmlTransient.class, property, ofType);
	      if (xmlTransientAnn != null) return true;

	      return false;
	    }

	    private boolean include(AccessibleObject property, Class<?> ofType, Class<?> fromClass) {
	      if (find(XmlAttribute.class, property, ofType) != null
	        || find(XmlElement.class, property, ofType) != null) return true;

	      return false;
	    }
	  }

	  private <A extends Annotation> A find(Class<A> annotation, AccessibleObject onObject,
	                                        Class<?> onClass) {
	    A ann = onObject.getAnnotation(annotation);
	    if (ann != null) return ann;
	    return find(annotation, onClass);
	  }

	  private <A extends Annotation> A find(Class<A> annotation, Class<?> onClass) {
	    A ann = onClass.getAnnotation(annotation);
	    if (ann == null && onClass.getPackage() != null)
	      ann = onClass.getPackage().getAnnotation(annotation);
	    return ann;
	  }

	  private <A extends Annotation> A find(Class<A> annotation, Class<?> inClass, String methodName,
	                                        Class<?>... parameterTypes) {
	    A ann = null;
	    for (Class<?> clazz = inClass; clazz != null; clazz = clazz.getSuperclass()) {
	      try {
	        for (Method m : clazz.getDeclaredMethods())
	          if (m.getName().equals(methodName)
	            && Arrays.equals(m.getParameterTypes(), parameterTypes))
	            if (m.isAnnotationPresent(annotation))
	              return m.getAnnotation(annotation);
	            else
	              break;

	      } catch (SecurityException e) {
	        throw new JDDPException(e);
	      }
	    }
	    return ann;
	}
	  
	  public final static boolean match(Type type, Type oType, boolean strictMatch) {
		    if (type == null || oType == null)
		      return type == null && oType == null;
		    Class<?> clazz = getRawClass(type);
		    Class<?> oClazz = getRawClass(oType);
		    boolean match = strictMatch ? oClazz.equals(clazz) : oClazz.isAssignableFrom(clazz);

		    if (Object.class.equals(oClazz) && !strictMatch) {
		      return match;
		    }
		    
		    if (Collection.class.isAssignableFrom(clazz) && !Collection.class.isAssignableFrom(oClazz)) {
		    	Class<?> elementType = Object.class;
		    	Type genericReturnType = type;
				if(genericReturnType instanceof ParameterizedType){
				    ParameterizedType ptype = (ParameterizedType) genericReturnType;
				    Type[] typeArguments = ptype.getActualTypeArguments();
				    for(Type typeArgument : typeArguments){
				        elementType = (Class<?>) typeArgument;
				        break;
				    }
				}
				return oClazz.isAssignableFrom(elementType);
		    }

		    if (clazz.isArray() && !oClazz.isArray())
		      return match;

		    Type[] types = getTypes(type);
		    Type[] oTypes = getTypes(oType);

		    match = match && (types.length == oTypes.length || types.length == 0);

		    for (int i = 0; i < types.length && match; i++)
		      match = match(types[i], oTypes[i], strictMatch);

		    return match;
		}
	  
	  private final static Type[] getTypes(Type type) {
		    if (type instanceof Class) {
		      Class<?> tClass = (Class<?>) type;
		      if (tClass.isArray())
		        return new Type[]{tClass.getComponentType()};
		      else {
		        TypeVariable<?>[] tvs = ((Class<?>) type).getTypeParameters();
		        Type[] types = new Type[tvs.length];
		        int i = 0;
		        for (TypeVariable<?> tv : tvs) {
		          types[i++] = tv.getBounds()[0];
		        }
		        return types;
		      }
		    } else if (type instanceof ParameterizedType) {
		      return ((ParameterizedType) type).getActualTypeArguments();
		    } else if (type instanceof GenericArrayType) {
		      return new Type[]{((GenericArrayType) type).getGenericComponentType()};
		    } else if (type instanceof WildcardType) {
		      return Operations.union(Type[].class, ((WildcardType) type).getUpperBounds(),
		        ((WildcardType) type).getLowerBounds());
		    } else if (type instanceof TypeVariable<?>) {
		      @SuppressWarnings("unchecked")
		      TypeVariable<Class<?>> tvType = (TypeVariable<Class<?>>) type;
		      Type resolvedType = resolveTypeVariable(tvType, tvType.getGenericDeclaration());
		      return tvType.equals(resolvedType) ? tvType.getBounds() : new Type[]{resolvedType};
		    } else
		      return new Type[0];
		}
	
}
