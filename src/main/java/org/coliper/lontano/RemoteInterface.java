package org.coliper.lontano;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

class RemoteInterface {
    private static List<Method> OBJECT_CLASS_METHODS =
            Collections.unmodifiableList(Arrays.asList(Object.class.getDeclaredMethods()));

    private final RemoteInterfaceName name;
    private final Map<RemoteOperationName, RemoteOperation> operationMap;
    private final BiFunction<String, Class<?>, Object> jsonDeserializer;

    private static Map<RemoteOperationName, RemoteOperation> createOperationMap(
            Object targetObject) {
        final Map<RemoteOperationName, RemoteOperation> map = new HashMap<>();
        Method[] methods = targetObject.getClass().getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue; // skip static methods
            }
            if (isObjectClassMethod(method)) {
                continue; // skip methods of class Object
            }
            map.put(new RemoteOperationName(method.getName()),
                    new RemoteOperation(targetObject, method));
        }
        return Collections.unmodifiableMap(map);
    }

    private static boolean isObjectClassMethod(Method method) {
        return OBJECT_CLASS_METHODS.contains(method);
    }

    RemoteInterface(RemoteInterfaceName name, Object targetObject,
            BiFunction<String, Class<?>, Object> jsonDeserializer) {
        this.name = requireNonNull(name, "name");
        this.operationMap = createOperationMap(requireNonNull(targetObject, "targetObject"));
        this.jsonDeserializer = requireNonNull(jsonDeserializer, "jsonDeserializer");
    }

    RemoteInterfaceName name() {
        return this.name;
    }

    Set<RemoteOperationName> operationNames() {
        return this.operationMap.keySet();
    }

    Object callOperation(RemoteOperationName opName, String[] jsonParameters) throws Exception {
        requireNonNull(jsonParameters, "jsonParameters");
        final RemoteOperation operation = this.operationMap.get(requireNonNull(opName, "opName"));
        checkState(operation != null, "unknown operation %s", opName);
        Object[] parameters = parseJsonParameters(jsonParameters, operation.parameterTypes());
        return operation.call(parameters);
    }

    private Object[] parseJsonParameters(String[] jsonParameters,
            Class<?>[] expectedParameterTypes) {
        Object[] parameters = new Object[jsonParameters.length];
        checkState(jsonParameters.length == expectedParameterTypes.length,
                "unexpected no of parameters");
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] =
                    this.jsonDeserializer.apply(jsonParameters[i], expectedParameterTypes[i]);
        }
        return parameters;
    }
}
