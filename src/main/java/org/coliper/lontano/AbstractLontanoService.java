package org.coliper.lontano;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Preconditions;

public abstract class AbstractLontanoService<T> {
    private Map<RemoteInterfaceName, RemoteInterface> interfaceMap = new HashMap<>();
    private Function<Object, Object> returnValueInterceptor = Function.identity();

    protected abstract Object deserializeFromJson(String json, Class<?> expectedType);

    protected ReturnValueWrapper handleRequest(RemoteInterfaceName ifName,
            RemoteOperationName opName, String requestBody) throws Exception {
        final RemoteInterface intf = this.interfaceMap.get(requireNonNull(ifName, "ifName"));
        Preconditions.checkState(intf != null, "unknown interface name %s", ifName);
        Object returnValueBeforeInterception =
                intf.callOperation(opName, JsonUtil.splitJsonArray(requestBody));
        return new ReturnValueWrapper(returnValueInterceptor.apply(returnValueBeforeInterception));
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    public T addInterface(RemoteInterfaceName interfaceName, Object object) {
        requireNonNull(interfaceName, "interfaceName");
        requireNonNull(object, "object");
        this.interfaceMap.put(interfaceName,
                new RemoteInterface(interfaceName, object, this::deserializeFromJson));
        return self();
    }

    public T addInterface(Object object) {
        return this.addInterface(
                this.createInterfaceNameFromObjectClass(requireNonNull(object, "object")), object);
    }

    private RemoteInterfaceName createInterfaceNameFromObjectClass(Object object) {
        return new RemoteInterfaceName(object.getClass().getName());
    }

    public T returnValueInterceptor(Function<Object, Object> returnValueInterceptor) {
        this.returnValueInterceptor =
                requireNonNull(returnValueInterceptor, "returnValueInterceptor");
        return self();
    }

    protected AbstractLontanoService() {
    }

}
