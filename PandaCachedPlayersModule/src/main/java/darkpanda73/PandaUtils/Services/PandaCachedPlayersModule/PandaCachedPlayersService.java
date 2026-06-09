package darkpanda73.PandaUtils.Services.PandaCachedPlayersModule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.InsertStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.SelectStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions.EqualsCondition;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table.UniqueConstraint;
import darkpanda73.PandaUtils.PandaSQLModule.v2.Services.PandaSQLService;
import darkpanda73.PandaUtils.PandaUtilities.Player.Skin.PlayerSkinUtilities;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Objects.PandaCachedPlayer;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Utils.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue;
import darkpanda73.PandaUtils.Utilities.Java.SleepingLoop;
import lombok.Getter;

@PandaService(loadOn = LoadStep.ENABLE, priority = -990)
public class PandaCachedPlayersService {

	private static final long TTL = 120000;

	public static final Table CACHED_PLAYERS_TABLE = new Table("CachedPlayers",
			Arrays.asList(new Column("Uuid", Types.CHAR, "CHAR(36)"), new Column("Name", Types.VARCHAR, "VARCHAR(32)"),
					new Column("Skin", Types.LONGVARCHAR, "VARCHAR(1000)")));

	static {
		CACHED_PLAYERS_TABLE
				.addUniqueConstraint(new UniqueConstraint(Arrays.asList(CACHED_PLAYERS_TABLE.getColumn("Uuid"))));
	}

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaSQLService sqlService;

	private HashMap<UUID, PandaCachedPlayer> cachedPlayers = new HashMap<>();
	private SleepingLoop clearLoop;
	private @Getter AsyncQueue<Runnable> asyncQueue;

	@LoadMethod
	private void load() throws Throwable {
		InjectUtils.inject(PandaCachedPlayer.class, this);

		sqlService.getSqlConnection().executeCreateTable(CreateTableStatement.inst(CACHED_PLAYERS_TABLE));

		try {
			sqlService.getSqlConnection().executeAlterTable(AlterTableStatement.AlterTableAddColumnStatement
					.inst(CACHED_PLAYERS_TABLE, new Column("Skin", Types.LONGVARCHAR, "VARCHAR(1000)")));
		} catch (Throwable e) {
			// Ignore cause means it's already implemented
		}

		clearLoop = new SleepingLoop(
				() -> cachedPlayers.entrySet()
						.removeIf(entry -> (entry.getValue().getLastRequest() + TTL) < System.currentTimeMillis()),
				() -> {
					return cachedPlayers.values().stream()
							.filter(op -> (op.getLastRequest() + TTL) < System.currentTimeMillis())
							.map(PandaCachedPlayer::getLastRequest).findFirst().orElse(Long.MAX_VALUE)
							- System.currentTimeMillis();
				});

		clearLoop.start();

		asyncQueue = new AsyncQueue<>();
		asyncQueue.subscribe((runnable) -> runnable.run());
	}

	@UnloadMethod
	private void unload() throws InterruptedException {
		clearLoop.interrupt();
		asyncQueue.join();
		asyncQueue.stop();
	}

	public PandaCachedPlayer getCachedPlayer(OfflinePlayer player) {
		synchronized (this) {
			PandaCachedPlayer cachedPlayer = this.cachedPlayers.get(player.getUniqueId());

			if (cachedPlayer == null) {
				cachedPlayer = player.isOnline()
						? new PandaCachedPlayer(player.getUniqueId(), player.getName(),
								PlayerSkinUtilities.getSkinAsBase64Safely(player.getPlayer()))
						: new PandaCachedPlayer(player.getUniqueId(), player.getName());
				saveCachedPlayer(cachedPlayer);
				this.cachedPlayers.put(player.getUniqueId(), cachedPlayer);

				if (this.cachedPlayers.size() == 1) {
					this.clearLoop.awake();
				}
			}

			cachedPlayer.updateLastRequest();
			return cachedPlayer;
		}
	}

	public PandaCachedPlayer getCachedPlayer(UUID uuid) {
		synchronized (this) {
			PandaCachedPlayer cachedPlayer = this.cachedPlayers.get(uuid);

			if (cachedPlayer == null) {
				cachedPlayer = createCachedPlayer(uuid);

				if (cachedPlayer != null) {
					this.cachedPlayers.put(uuid, cachedPlayer);

					if (this.cachedPlayers.size() == 1) {
						this.clearLoop.awake();
					}
				} else {
					return null;
				}
			}

			cachedPlayer.updateLastRequest();
			return cachedPlayer;
		}
	}

	public PandaCachedPlayer getCachedPlayer(String name) {
		synchronized (this) {
			PandaCachedPlayer cachedPlayer = this.cachedPlayers.values().stream()
					.filter(op -> op.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

			if (cachedPlayer == null) {
				cachedPlayer = createCachedPlayer(name);

				if (cachedPlayer != null) {
					this.cachedPlayers.put(cachedPlayer.getUuid(), cachedPlayer);

					if (this.cachedPlayers.size() == 1) {
						this.clearLoop.awake();
					}
				} else {
					return null;
				}
			}

			cachedPlayer.updateLastRequest();
			return cachedPlayer;
		}
	}

	private PandaCachedPlayer createCachedPlayer(UUID uuid) {
		PandaCachedPlayer cachedPlayer;

		OfflinePlayer offlinePlayer = Bukkit.getPlayer(uuid);
		if (offlinePlayer != null) {
			cachedPlayer = new PandaCachedPlayer(offlinePlayer.getUniqueId(), offlinePlayer.getName(),
					PlayerSkinUtilities.getSkinAsBase64Safely(offlinePlayer.getPlayer()));
			saveCachedPlayer(cachedPlayer);
			return cachedPlayer;
		}

		try (ResultSet set = sqlService.getSqlConnection()
				.executeQuery(SelectStatement.inst().addTable(CACHED_PLAYERS_TABLE)
						.setCondition(EqualsCondition.inst(CACHED_PLAYERS_TABLE.getColumn("Uuid"), uuid.toString())))) {
			if (set.next()) {
				return new PandaCachedPlayer(UUID.fromString(set.getString("Uuid")), set.getString("Name"),
						set.getString("Skin"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		offlinePlayer = OfflinePlayerUtilities.getOfflinePlayer(uuid);
		if (offlinePlayer != null) {
			cachedPlayer = new PandaCachedPlayer(offlinePlayer.getUniqueId(), offlinePlayer.getName());
			saveCachedPlayer(cachedPlayer);
			return cachedPlayer;
		}

		return null;
	}

	private PandaCachedPlayer createCachedPlayer(String name) {
		PandaCachedPlayer cachedPlayer;

		OfflinePlayer offlinePlayer = Bukkit.getPlayer(name);
		if (offlinePlayer != null) {
			cachedPlayer = new PandaCachedPlayer(offlinePlayer.getUniqueId(), offlinePlayer.getName(),
					PlayerSkinUtilities.getSkinAsBase64Safely(offlinePlayer.getPlayer()));
			saveCachedPlayer(cachedPlayer);
			return cachedPlayer;
		}

		try (ResultSet set = sqlService.getSqlConnection()
				.executeQuery(SelectStatement.inst().addTable(CACHED_PLAYERS_TABLE)
						.setCondition(EqualsCondition.inst(CACHED_PLAYERS_TABLE.getColumn("Name"), name)))) {
			if (set.next()) {
				return new PandaCachedPlayer(UUID.fromString(set.getString("Uuid")), set.getString("Name"),
						set.getString("Skin"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		offlinePlayer = OfflinePlayerUtilities.getOfflinePlayer(name);
		if (offlinePlayer != null) {
			cachedPlayer = new PandaCachedPlayer(offlinePlayer.getUniqueId(), offlinePlayer.getName());
			saveCachedPlayer(cachedPlayer);
			return cachedPlayer;
		}

		return null;
	}

	private void saveCachedPlayer(PandaCachedPlayer cachedPlayer) {
		sqlService.getAsyncQueue().add(() -> {
			try {
				sqlService.getSqlConnection()
						.executeInsert(InsertStatement.inst(CACHED_PLAYERS_TABLE)
								.addEntry(cachedPlayer.getUuid().toString(), cachedPlayer.getName(),
										cachedPlayer.getSkin())
								.setConditionIfExists(EqualsCondition.inst(CACHED_PLAYERS_TABLE.getColumn("Uuid"),
										cachedPlayer.getUuid().toString())));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});
	}

}
