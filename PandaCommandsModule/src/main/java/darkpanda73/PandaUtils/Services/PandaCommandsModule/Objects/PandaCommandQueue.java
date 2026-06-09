package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PandaCommandQueue {

	public static enum Status {
		NO_COOLDOWN, COOLDOWN, PENDING
	}

	private @NonNull PandaSubCommand command;
	protected @Getter(lombok.AccessLevel.NONE) HashMap<UUID, CommandResponse> cooldowns = new HashMap<>();
	protected @Getter(lombok.AccessLevel.NONE) CommandResponse consoleCooldown = null;

	public void addCooldown(CommandSender sender, CommandResponse commandResponse) {
		if (commandResponse.getCooldown() == null) {
			commandResponse.setCooldown(command.getCurrentCooldown());
		}

		if (sender instanceof Player) {
			this.cooldowns.put(((Player) sender).getUniqueId(), commandResponse);
		} else {
			this.consoleCooldown = commandResponse;
		}

		commandResponse.execute();
	}

	public Status getCooldownStatus(CommandSender sender) {
		CommandResponse commandResponse;

		if (sender instanceof Player) {
			commandResponse = this.cooldowns.get(((Player) sender).getUniqueId());
		} else {
			commandResponse = this.consoleCooldown;
		}

		if (commandResponse != null) {
			if (commandResponse.getStatus() == CommandResponse.Status.COMPLETED) {
				if ((commandResponse.getCooldown() * 1000) >= (System.currentTimeMillis()
						- commandResponse.getStart())) {
					return Status.COOLDOWN;
				} else {
					if (sender instanceof Player) {
						this.cooldowns.remove(((Player) sender).getUniqueId());
					} else {
						this.consoleCooldown = null;
					}
				}
			} else if (commandResponse.getStatus() == CommandResponse.Status.RUNNING) {
				return Status.PENDING;
			}
		}

		return Status.NO_COOLDOWN;
	}

	public long getCooldownRemaining(CommandSender sender) {
		CommandResponse commandResponse;

		if (sender instanceof Player) {
			commandResponse = this.cooldowns.get(((Player) sender).getUniqueId());
		} else {
			commandResponse = this.consoleCooldown;
		}

		return commandResponse != null ? Math.max(
				(commandResponse.getStart() + (commandResponse.getCooldown() * 1000)) - System.currentTimeMillis(), 0)
				: 0;
	}

	public void clearExpired() {
		this.cooldowns.entrySet().removeIf(entry -> entry.getValue().getStatus() == CommandResponse.Status.COMPLETED
				&& command.getCurrentCooldown() < (System.currentTimeMillis() - entry.getValue().getStart()));
	}

}
