package darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PandaPermission {

	private @Getter String permission;
	private @Getter Insertion insertion;

	public PandaPermission(String permission) {
		this(permission, Insertion.NONE);
	}

	public PandaPermission(String permission, Insertion insertion) {
		this.permission = permission;
		this.insertion = insertion;
	}

	public boolean hasPermission(Player pl) {
		return pl.hasPermission(getPermission());
	}

	@AllArgsConstructor
	@Getter
	public static enum Insertion {
		NONE(null), ALL('+'), OP('!');

		private Character symbol;

		public static Insertion findBySymbol(char symbol) {
			for (Insertion insertion : Insertion.values()) {
				if (insertion.getSymbol() != null && insertion.getSymbol() == symbol) {
					return insertion;
				}
			}
			return NONE;
		}
	}

}
