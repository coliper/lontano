package org.coliper.lontano;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class LontanoInterface {
	private static List<Method> OBJECT_CLASS_METHODS = Collections
			.unmodifiableList(Arrays.asList(Object.class.getDeclaredMethods()));

	private final InterfaceName name;
	private final Object targetObject;
	private final Map<OperationName, Method> operationMap;

	private static Map<OperationName, Method> createOperationMap(Object targetObject) {
		final Map<OperationName, Method> map = new HashMap<>();
		Method[] methods = targetObject.getClass().getMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue; // skip static methods
			}
			if (isObjectClassMethod(method)) {
				continue; // skip methods of class Object
			}
			map.put(new OperationName(method.getName()), method);
		}
		return Collections.unmodifiableMap(map);
	}

	private static boolean isObjectClassMethod(Method method) {
		return OBJECT_CLASS_METHODS.contains(method);
	}

	LontanoInterface(InterfaceName name, Object targetObject) {
		this.name = requireNonNull(name, "name");
		this.targetObject = requireNonNull(targetObject, "targetObject");
		this.operationMap = createOperationMap(targetObject);
	}

	InterfaceName name() {
		return this.name;
	}

	Set<OperationName> operationNames() {
		return this.operationMap.keySet();
	}

	Object callOperation(OperationName opName, Object[] parameters) throws Throwable {
		final Method method = this.operationMap.get(requireNonNull(opName, "opName"));
		checkArgument(method != null, "unknown operation %s", opName);
		try {
			return method.invoke(requireNonNull(this.targetObject, "targetObject"), parameters);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException("unexpected exception " + e, e);
		}
	}
}
