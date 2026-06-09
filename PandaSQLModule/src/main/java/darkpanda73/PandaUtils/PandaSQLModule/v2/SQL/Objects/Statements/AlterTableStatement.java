package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import lombok.Getter;

public abstract class AlterTableStatement extends Statement {

	private @Getter String tableName;

	public AlterTableStatement(Table table) {
		this(table.getName());
	}

	public AlterTableStatement(String tableName) {
		this.tableName = tableName;
	}

	public AlterTableStatement setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public abstract PreparedStatement build(SQLConnection sqlConnection);

	public static class AlterTableAddColumnStatement extends AlterTableStatement {

		private @Getter Column column;

		public AlterTableAddColumnStatement(Table table, Column column) {
			super(table);
			this.column = column;
		}

		public AlterTableAddColumnStatement(String tableName, Column column) {
			super(tableName);
			this.column = column;
		}

		public static AlterTableAddColumnStatement inst(Table table, Column column) {
			return new AlterTableAddColumnStatement(table, column);
		}

		public static AlterTableAddColumnStatement inst(String tableName, Column column) {
			return new AlterTableAddColumnStatement(tableName, column);
		}

		@Override
		public PreparedStatement build(SQLConnection sqlConnection) {
			return buildStatement(sqlConnection, this);
		}

		public static PreparedStatement buildStatement(SQLConnection sqlConnection,
				AlterTableAddColumnStatement statement) {
			PreparedStatement preparedStatement = new PreparedStatement();

			StringBuilder builder = new StringBuilder();

			builder.append("ALTER TABLE ");

			builder.append(sqlConnection.getPrefix() + statement.getTableName());

			builder.append(" ADD ");

			builder.append(statement.column.toString());

			preparedStatement.setStatement(builder.toString());

			return preparedStatement;
		}

	}

	public static class AlterTableRenameColumnStatement extends AlterTableStatement {

		private @Getter String oldColumnName;
		private @Getter Column column;

		public AlterTableRenameColumnStatement(Table table, String oldColumnName, Column column) {
			super(table);
			this.oldColumnName = oldColumnName;
			this.column = column;
		}

		public AlterTableRenameColumnStatement(String tableName, String oldColumnName, Column column) {
			super(tableName);
			this.oldColumnName = oldColumnName;
			this.column = column;
		}

		public static AlterTableRenameColumnStatement inst(Table table, String oldColumnName, Column column) {
			return new AlterTableRenameColumnStatement(table, oldColumnName, column);
		}

		public static AlterTableRenameColumnStatement inst(String tableName, String oldColumnName, Column column) {
			return new AlterTableRenameColumnStatement(tableName, oldColumnName, column);
		}

		@Override
		public PreparedStatement build(SQLConnection sqlConnection) {
			return buildStatement(sqlConnection, this);
		}

		public static PreparedStatement buildStatement(SQLConnection sqlConnection,
				AlterTableRenameColumnStatement statement) {
			PreparedStatement preparedStatement = new PreparedStatement();

			StringBuilder builder = new StringBuilder();

			builder.append("ALTER TABLE ");

			builder.append(sqlConnection.getPrefix() + statement.getTableName());

			if (sqlConnection.isMySQLConnection()) {
				builder.append(" CHANGE ");

				builder.append(statement.oldColumnName);

				builder.append(" ");

				builder.append(statement.column.toString());

				preparedStatement.setStatement(builder.toString());
			} else {
				builder.append(" RENAME COLUMN ");

				builder.append(statement.oldColumnName);

				builder.append(" TO ");

				builder.append(statement.column.getName());

				preparedStatement.setStatement(builder.toString());
			}

			return preparedStatement;
		}

	}

	public static class AlterTableDropColumnStatement extends AlterTableStatement {

		private @Getter String columnName;

		public AlterTableDropColumnStatement(Table table, String columnName) {
			super(table);
			this.columnName = columnName;
		}

		public AlterTableDropColumnStatement(String tableName, String columnName) {
			super(tableName);
			this.columnName = columnName;
		}

		public static AlterTableDropColumnStatement inst(Table table, String columnName) {
			return new AlterTableDropColumnStatement(table, columnName);
		}

		public static AlterTableDropColumnStatement inst(String tableName, String columnName) {
			return new AlterTableDropColumnStatement(tableName, columnName);
		}

		@Override
		public PreparedStatement build(SQLConnection sqlConnection) {
			return buildStatement(sqlConnection, this);
		}

		public static PreparedStatement buildStatement(SQLConnection sqlConnection,
				AlterTableDropColumnStatement statement) {
			PreparedStatement preparedStatement = new PreparedStatement();

			StringBuilder builder = new StringBuilder();

			builder.append("ALTER TABLE ");

			builder.append(sqlConnection.getPrefix() + statement.getTableName());

			builder.append(" DROP COLUMN ");

			builder.append(statement.columnName);

			preparedStatement.setStatement(builder.toString());

			return preparedStatement;
		}

	}

	public static class AlterTableDropConstraintStatement extends AlterTableStatement {

		private @Getter String constraintId;

		public AlterTableDropConstraintStatement(Table table, String constraintId) {
			super(table);
			this.constraintId = constraintId;
		}

		public AlterTableDropConstraintStatement(String tableName, String constraintId) {
			super(tableName);
			this.constraintId = constraintId;
		}

		public static AlterTableDropConstraintStatement inst(Table table, String constraintId) {
			return new AlterTableDropConstraintStatement(table, constraintId);
		}

		public static AlterTableDropConstraintStatement inst(String tableName, String constraintId) {
			return new AlterTableDropConstraintStatement(tableName, constraintId);
		}

		@Override
		public PreparedStatement build(SQLConnection sqlConnection) {
			return buildStatement(sqlConnection, this);
		}

		public static PreparedStatement buildStatement(SQLConnection sqlConnection,
				AlterTableDropConstraintStatement statement) {
			PreparedStatement preparedStatement = new PreparedStatement();

			StringBuilder builder = new StringBuilder();

			builder.append("ALTER TABLE ");

			builder.append(sqlConnection.getPrefix() + statement.getTableName());

			builder.append(" DROP CONSTRAINT ");

			builder.append(statement.getConstraintId());

			preparedStatement.setStatement(builder.toString());

			return preparedStatement;
		}

	}

	public static class AlterTableRenameToStatement extends AlterTableStatement {

		private @Getter String newName;

		public AlterTableRenameToStatement(Table table, String newName) {
			super(table);
			this.newName = newName;
		}

		public AlterTableRenameToStatement(String tableName, String newName) {
			super(tableName);
			this.newName = newName;
		}

		public static AlterTableRenameToStatement inst(Table table, String newName) {
			return new AlterTableRenameToStatement(table, newName);
		}

		public static AlterTableRenameToStatement inst(String tableName, String newName) {
			return new AlterTableRenameToStatement(tableName, newName);
		}

		@Override
		public PreparedStatement build(SQLConnection sqlConnection) {
			return buildStatement(sqlConnection, this);
		}

		public static PreparedStatement buildStatement(SQLConnection sqlConnection,
				AlterTableRenameToStatement statement) {
			PreparedStatement preparedStatement = new PreparedStatement();

			StringBuilder builder = new StringBuilder();

			builder.append("ALTER TABLE ");

			builder.append(sqlConnection.getPrefix() + statement.getTableName());

			builder.append(" RENAME TO ");

			builder.append(sqlConnection.getPrefix() + statement.getNewName());

			preparedStatement.setStatement(builder.toString());

			return preparedStatement;
		}

	}

}