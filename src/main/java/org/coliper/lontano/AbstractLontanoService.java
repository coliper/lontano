package org.coliper.lontano;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractLontanoService {
	private Map<InterfaceName, LontanoInterface> interfaceMap = new HashMap<>();

	protected Object handleRequest(InterfaceName ifName, OperationName opName, Object[] parameters) {
		return null;
	}

	protected void addInterface(InterfaceName interfaceName, Object object) {
		this.interfaceMap.put(requireNonNull(interfaceName, "interfaceName"),
				new LontanoInterface(interfaceName, requireNonNull(object, "object")));
	}

	protected void addInterface(Object object) {
		this.addInterface(this.createInterfaceNameFromObjectClass(requireNonNull(object, "object")), object);
	}

	private InterfaceName createInterfaceNameFromObjectClass(Object object) {
		return new InterfaceName(object.getClass().getName());
	}

	protected AbstractLontanoService() {
	}

}
