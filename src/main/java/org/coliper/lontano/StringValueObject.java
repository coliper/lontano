package org.coliper.lontano;

public abstract class StringValueObject {
	private final String value;

	protected StringValueObject(String value) {
		this.value = value;
	}

	public String valueAsString() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringValueObject other = (StringValueObject) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
