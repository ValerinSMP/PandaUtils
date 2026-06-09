package darkpanda73.PandaUtils.PandaJSON.Objects;

import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;

public class PandaJSONData extends PandaJSONElement {

	private Object value;
	private JSONDataType type;

	public PandaJSONData(Object value) {
		if (value == null) {
			this.value = null;
			this.type = JSONDataType.NULL;
		} else if (value instanceof Long || value instanceof Integer || value instanceof Short) {
			this.value = ((Number) value).longValue();
			this.type = JSONDataType.LONG;
		} else if (value instanceof Double || value instanceof Float) {
			this.value = ((Number) value).doubleValue();
			this.type = JSONDataType.DOUBLE;
		} else if (value instanceof Boolean) {
			this.value = value;
			this.type = JSONDataType.BOOLEAN;
		} else if (value instanceof String) {
			this.value = value;
			this.type = JSONDataType.STRING;
		} else {
			throw new IllegalArgumentException("Value is not a primitive value");
		}
	}

	public PandaJSONData(String value) {
		if (value == null) {
			this.value = null;
			this.type = JSONDataType.NULL;
		} else if (value.length() == 0) {
			this.value = value;
			this.type = JSONDataType.STRING;
		} else if (value.charAt(0) == '"') {
			this.value = StringUtilities.removeBoundaries(value, '"').replace("\\\"", "\"");
			this.type = JSONDataType.STRING;
		} else {
			switch (value.toLowerCase()) {
			case "null":
				this.value = null;
				this.type = JSONDataType.NULL;
				break;
			case "false":
				this.value = false;
			case "true":
				if (this.value == null) {
					this.value = false;
				}
				this.type = JSONDataType.BOOLEAN;
				break;
			default:
				if (value.matches("[0-9]{0,}\\.{0,1}[0-9]{0,}")) {
					if (value.contains(".")) {
						try {
							this.value = (double) Double.parseDouble(value);
							this.type = JSONDataType.DOUBLE;
							break;
						} catch (NumberFormatException e) {
						}
					} else {
						try {
							this.value = (long) Long.parseLong(value);
							this.type = JSONDataType.LONG;
							break;
						} catch (NumberFormatException e) {
						}
					}

				}
				throw new IllegalArgumentException("Value could not be parsed into a valid entity");
			}
		}
	}

	public PandaJSONData() {
		this.value = null;
		this.type = JSONDataType.NULL;
	}

	@Override
	public boolean isData() {
		return true;
	}

	@Override
	public PandaJSONData asData() {
		return this;
	}

	public boolean isNull() {
		return type == JSONDataType.NULL;
	}

	public boolean isLong() {
		return type == JSONDataType.LONG;
	}

	public boolean isDouble() {
		return type == JSONDataType.DOUBLE;
	}

	public boolean isBoolean() {
		return type == JSONDataType.BOOLEAN;
	}

	public boolean isString() {
		return type == JSONDataType.STRING;
	}

	public JSONDataType dataType() {
		return type;
	}

	public Object value() {
		return value;
	}

	public String valueAsString() {
		if (isNull())
			throw new NullPointerException();
		return value instanceof String ? (String) value : String.valueOf(value);
	}

	public int valueAsInteger() {
		if (isNull())
			throw new NullPointerException();
		if (!isLong())
			throw new IllegalStateException("Getting data with invalid type!");
		return ((Long) value).intValue();
	}

	public long valueAsLong() {
		if (isNull())
			throw new NullPointerException();
		if (!isLong())
			throw new IllegalStateException("Getting data with invalid type!");
		return (long) value;
	}

	public float valueAsFloat() {
		if (isNull())
			throw new NullPointerException();
		if (!isDouble())
			throw new IllegalStateException("Getting data with invalid type!");
		return ((Double) value).floatValue();
	}

	public double valueAsDouble() {
		if (isNull())
			throw new NullPointerException();
		if (!isDouble())
			throw new IllegalStateException("Getting data with invalid type!");
		return (double) value;
	}

	public boolean valueAsBoolean() {
		if (isNull())
			throw new NullPointerException();
		if (!isBoolean())
			throw new IllegalStateException("Getting data with invalid type!");
		return (boolean) value;
	}

	@Override
	public String toString(int level) {
		try {
			switch (dataType()) {
			case BOOLEAN:
				return StringUtilities.addSpaces(level, String.valueOf(valueAsBoolean()));
			case DOUBLE:
				return StringUtilities.addSpaces(level, String.valueOf(valueAsDouble()));
			case LONG:
				return StringUtilities.addSpaces(level, String.valueOf(valueAsLong()));
			case NULL:
				return StringUtilities.addSpaces(level, "null");
			case STRING:
				return StringUtilities.addSpaces(level, "\"" + asData().valueAsString().replace("\"", "\\\"") + "\"");
			default:
				return "\"\"";
			}
		} catch (Exception e) {
			return "\"\"";
		}
	}

}
