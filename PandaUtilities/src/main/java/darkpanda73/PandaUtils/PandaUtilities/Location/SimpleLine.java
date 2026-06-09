package darkpanda73.PandaUtils.PandaUtilities.Location;

import java.awt.geom.Line2D;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SimpleLine {

	private SimpleLocation from;
	private SimpleLocation to;

	public boolean intersects(SimpleLine line) {
		if (Math.max(from.getY(), to.getY()) >= Math.min(line.getFrom().getY(), line.getTo().getY())
				&& Math.min(from.getY(), to.getY()) <= Math.max(line.getFrom().getY(), line.getTo().getY())) {
			return Line2D.linesIntersect(from.getX(), from.getZ(), to.getX(), to.getZ(), line.getFrom().getX(),
					line.getFrom().getZ(), line.getTo().getX(), line.getTo().getZ());
		}
		return false;
	}

	public static SimpleLine of(SimpleLocation from, SimpleLocation to) {
		return new SimpleLine(from, to);
	}

}
