package darkpanda73.PandaUtils.Utilities.Numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtilities {

	public static double truncate(double number, int decimals) {
		return new BigDecimal(number).setScale(decimals, RoundingMode.UNNECESSARY).doubleValue();
	}

}
