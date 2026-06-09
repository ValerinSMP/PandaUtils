package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Constraints;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Column;

public class UniqueConstraint {
	private List<Column> uniques = new ArrayList<>();
	public UniqueConstraint(Column...columns) { for (Column column:columns) uniques.add(column); }
	public List<Column> getUniques() { return uniques; }
}
