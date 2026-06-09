package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Utils.TypeUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class Column {

	private @Setter Table table;
	private String name;
	private int type;

	private String typeDefinition;
	private String defaultDefinition;

	private @Setter @Getter boolean primary;
	private @Setter @Getter boolean notNull;
	private @Setter @Getter boolean unique;
	private @Setter @Getter boolean indexed;
	private @Setter @Getter boolean unsigned;
	private @Setter @Getter boolean autoIncrement;

	public Column(Table table, String name, int type) {
		this.table = table;
		this.name = name;
		this.type = type;
		this.typeDefinition = TypeUtilities.typeToString(type);
	}

	public Column(Table table, String name, int type, String typeDefinition) {
		this(table, name, type);
		this.typeDefinition = typeDefinition;
	}

	public Column(Table table, String name, int type, String typeDefinition, String defaultDefinition) {
		this(table, name, type, typeDefinition);
		this.defaultDefinition = defaultDefinition;
	}

	public Column(String name, int type) {
		this(null, name, type);
	}

	public Column(String name, int type, String typeDefinition) {
		this(null, name, type, typeDefinition);
	}

	public Column(String name, int type, String typeDefinition, String defaultDefinition) {
		this(null, name, type, typeDefinition, defaultDefinition);
	}

	@Override
	public String toString() {
		StringBuilder columnBuilder = new StringBuilder();

		columnBuilder.append(getName());

		columnBuilder.append(" ");

		columnBuilder.append((getTypeDefinition() != null && !getTypeDefinition().isEmpty()) ? getTypeDefinition()
				: TypeUtilities.typeToString(getType()));

		if (isUnsigned()) {
			columnBuilder.append(" UNSIGNED");
		}
		if (isNotNull()) {
			columnBuilder.append(" NOT NULL");
		}
		if (getDefaultDefinition() != null && !getDefaultDefinition().isEmpty()) {
			columnBuilder.append(" DEFAULT ").append(getDefaultDefinition());
		}

		return columnBuilder.toString();
	}

}