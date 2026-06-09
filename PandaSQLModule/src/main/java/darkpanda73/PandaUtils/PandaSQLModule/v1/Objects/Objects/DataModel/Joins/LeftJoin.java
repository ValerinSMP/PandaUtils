package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Joins;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Column;

public class LeftJoin extends Join {
	public LeftJoin(Column append, Column origin) { super(append, origin); }
	@Override
	public String toString() { return new StringBuilder().append("LEFT OUTER ").append(super.toString()).toString(); }
}
