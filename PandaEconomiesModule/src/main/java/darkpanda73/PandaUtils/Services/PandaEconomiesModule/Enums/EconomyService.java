package darkpanda73.PandaUtils.Services.PandaEconomiesModule.Enums;

import java.util.Optional;

public enum EconomyService {
	VAULT, TOKENMANAGER, PLAYERPOINTS, ESSENTIALSX, COINSENGINE;

	public EconomyService previous() {
		return values()[(values().length + ordinal() - 1) % values().length];
	}

	public EconomyService next() {
		return values()[(values().length + ordinal() + 1) % values().length];
	}

	public static Optional<EconomyService> find(String name) {
		try {
			return Optional.of(EconomyService.valueOf(name.toUpperCase()));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}

}
