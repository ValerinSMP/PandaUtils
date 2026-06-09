package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Enums;

public enum SQLType {
	NONE,
	MYSQL,
	SQLITE;
	public static SQLType getValue(String sqltype) {
		String t = sqltype.toUpperCase();
		for (SQLType type:SQLType.values()) if (type.name().equals(t)) return type;
		return NONE;
	}
}
