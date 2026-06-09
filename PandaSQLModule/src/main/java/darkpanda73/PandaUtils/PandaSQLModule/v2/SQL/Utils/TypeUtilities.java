package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Utils;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class TypeUtilities {

	private static Map<Integer, String> TYPE_NAMES = new HashMap<>();
	private static String OTHER_NAME = "OTHER";

	static {
		TYPE_NAMES.put(Types.VARCHAR, "VARCHAR");
		TYPE_NAMES.put(Types.BIGINT, "BIGINT");
		TYPE_NAMES.put(Types.INTEGER, "INTEGER");
		TYPE_NAMES.put(Types.DOUBLE, "DOUBLE");
		TYPE_NAMES.put(Types.FLOAT, "FLOAT");
		TYPE_NAMES.put(Types.SMALLINT, "SMALLINT");
		TYPE_NAMES.put(Types.TINYINT, "TINYINT");
		TYPE_NAMES.put(Types.BOOLEAN, "BOOLEAN");
		TYPE_NAMES.put(Types.BLOB, "BLOB");
		TYPE_NAMES.put(Types.NULL, "NULL");
	}

	public static int findTypeByObject(Object obj) {
		if (obj instanceof String) {
			return Types.VARCHAR;
		} else if (obj instanceof Long) {
			return Types.BIGINT;
		} else if (obj instanceof Integer) {
			return Types.INTEGER;
		} else if (obj instanceof Double) {
			return Types.DOUBLE;
		} else if (obj instanceof Float) {
			return Types.FLOAT;
		} else if (obj instanceof Short) {
			return Types.SMALLINT;
		} else if (obj instanceof Byte) {
			return Types.TINYINT;
		} else if (obj instanceof Boolean) {
			return Types.BOOLEAN;
		} else if (obj instanceof byte[]) {
			return Types.BLOB;
		} else if (obj == null) {
			return Types.NULL;
		}
		return Types.OTHER;
	}

	public static String typeToString(int type) {
		return TYPE_NAMES.getOrDefault(type, OTHER_NAME);
	}

}
