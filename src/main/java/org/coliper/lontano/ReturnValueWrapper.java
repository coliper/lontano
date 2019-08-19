package org.coliper.lontano;

public class ReturnValueWrapper {
	private final Object returnValue;

	public ReturnValueWrapper(Object returnValue) {
		this.returnValue = returnValue;
	}

	public Object getReturnValue() {
		return returnValue;
	}
}
