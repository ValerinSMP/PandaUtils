package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Joins;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Column;

public class InnerJoin extends Join {
	public InnerJoin(Column append, Column origin) { super(append, origin); }
	@Override
	public String toString() { return new StringBuilder().append("INNER ").append(super.toString()).toString(); }
}
