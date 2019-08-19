package org.coliper.lontano;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public abstract class AbstractLontanoService {
    private Map<RemoteInterfaceName, RemoteInterface> interfaceMap = new HashMap<>();

    protected abstract String serializeToJson(Object objectToSerialize);

    protected abstract Object deserializeFromJson(String json, Class<?> expectedType);

    protected ReturnValueWrapper handleRequest(RemoteInterfaceName ifName,
            RemoteOperationName opName, String requestBody) {
        final RemoteInterface intf = this.interfaceMap.get(requireNonNull(ifName, "ifName"));
        Preconditions.checkState(intf != null, "unknown interface name %s", ifName);
        return null;
    }

    protected void addInterface(RemoteInterfaceName interfaceName, Object object) {
        requireNonNull(interfaceName, "interfaceName");
        requireNonNull(object, "object");
        this.interfaceMap.put(interfaceName,
                new RemoteInterface(interfaceName, object, this::deserializeFromJson));
    }

    protected void addInterface(Object object) {
        this.addInterface(this.createInterfaceNameFromObjectClass(requireNonNull(object, "object")),
                object);
    }

    private RemoteInterfaceName createInterfaceNameFromObjectClass(Object object) {
        return new RemoteInterfaceName(object.getClass().getName());
    }

    protected AbstractLontanoService() {
    }

}
