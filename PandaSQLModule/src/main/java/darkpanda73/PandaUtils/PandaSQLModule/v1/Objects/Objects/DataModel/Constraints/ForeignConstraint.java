package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Constraints;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Column;

public class ForeignConstraint {
	private List<Column> origin = new ArrayList<>();
	private List<Column> reference = new ArrayList<>();
	public ForeignConstraint(List<Column> origin, List<Column> reference) { this.origin = origin; this.reference = reference; }
	public ForeignConstraint addOrigin(Column column) { origin.add(column); return this; }
	public ForeignConstraint addOrigins(Column...columns) { for (Column column:columns) addOrigin(column); return this;}
	public ForeignConstraint addReference(Column column) { reference.add(column); return this; }
	public ForeignConstraint addReferences(Column...columns) { for (Column column:columns) addReference(column); return this; }
	public List<Column> getOrigins() { return origin; }
	public List<Column> getReferences() { return reference; }
}
