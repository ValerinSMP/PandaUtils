package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class Table {

	private String name;
	private List<Column> columns = new ArrayList<>();
	private List<ForeignConstraint> foreignConstraints = new ArrayList<>();
	private List<UniqueConstraint> uniqueConstraints = new ArrayList<>();

	public Table(String name) {
		this.name = name;
	}

	public Table(String name, List<Column> columns) {
		this(name);
		this.columns = columns;
		this.columns.forEach(column -> column.setTable(this));
	}

	public Table addColumn(Column column) {
		columns.add(column);
		column.setTable(this);
		return this;
	}

	public Table addColumns(Column... columns) {
		for (Column column : columns) {
			addColumn(column);
		}
		return this;
	}

	public Table addUniqueConstraint(UniqueConstraint uniquec) {
		uniqueConstraints.add(uniquec);
		return this;
	}

	public Table addUniqueConstraints(UniqueConstraint... uniquecs) {
		for (UniqueConstraint uniquec : uniquecs) {
			addUniqueConstraint(uniquec);
		}
		return this;
	}

	public Table addForeignConstraint(ForeignConstraint foreignc) {
		foreignConstraints.add(foreignc);
		return this;
	}

	public Table addForeignConstraints(ForeignConstraint... foreigncs) {
		for (ForeignConstraint foreignc : foreigncs) {
			addForeignConstraint(foreignc);
		}
		return this;
	}

	public Column getColumn(String name) {
		return this.columns.stream().filter(column -> column.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class ForeignConstraint {

		private List<Column> columns = new ArrayList<>();
		private List<Column> referenceColumns = new ArrayList<>();

		public ForeignConstraint addColumn(Column column) {
			columns.add(column);
			return this;
		}

		public ForeignConstraint addColumns(Column... columns) {
			for (Column column : columns) {
				addColumn(column);
			}
			return this;
		}

		public ForeignConstraint addReferenceColumn(Column column) {
			referenceColumns.add(column);
			return this;
		}

		public ForeignConstraint addReferenceColumns(Column... columns) {
			for (Column column : columns) {
				addReferenceColumn(column);
			}
			return this;
		}

	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class UniqueConstraint {

		private List<Column> columns = new ArrayList<>();

		public UniqueConstraint addColumn(Column column) {
			columns.add(column);
			return this;
		}

		public UniqueConstraint addColumns(Column... columns) {
			for (Column column : columns) {
				addColumn(column);
			}
			return this;
		}

	}

}
