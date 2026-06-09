package darkpanda73.PandaUtils.PandaUtilities.Blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import darkpanda73.PandaUtils.PandaUtilities.Blocks.NMS.BlockNMSAbstract;
import darkpanda73.PandaUtils.PandaUtilities.Blocks.NMS.BlockNMSv1;
import darkpanda73.PandaUtils.PandaUtilities.Blocks.NMS.BlockNMSv2;
import darkpanda73.PandaUtils.PandaUtilities.Blocks.NMS.BlockNMSv3;

public class BlockUtilities {

	private static BlockNMSAbstract BLOCK_NMS;

	static {
		List<Exception> exceptions = new ArrayList<>();
		for (Class<? extends BlockNMSAbstract> clazz : Arrays.asList(BlockNMSv1.class, BlockNMSv2.class,
				BlockNMSv3.class)) {
			try {
				BLOCK_NMS = clazz.getConstructor().newInstance();
				break;
			} catch (Exception e) {
				BLOCK_NMS = null;
				exceptions.add(e);
			}
		}

		if (BLOCK_NMS == null && !exceptions.isEmpty()) {
			System.err.println("[BlockUtilities] Unable to load methods for blocks:");
			exceptions.get(exceptions.size() - 1).printStackTrace();
		} else {
			System.out.println("[PandaUtilities] Initialized NBT methods for blocks successfully.");
		}
	}

	public static void setSkin(Block block, String value) {
		BLOCK_NMS.setSkin(block, value);
	}

	public static Entity setLocationGlowing(Location loc) {
		MagmaCube ent = (MagmaCube) loc.getWorld().spawnEntity(loc.clone().add(0.5, 0, 0.5), EntityType.MAGMA_CUBE);
		ent.setSize(2);
		ent.setAI(false);
		ent.setGlowing(true);
		ent.setInvisible(true);
		ent.setSilent(true);
		ent.setInvulnerable(true);
		ent.setGravity(false);
		ent.setCollidable(false);
		ent.setPersistent(false);
		return ent;
	}

	public static Block getBlockOnSight(Player player, long maxDistance) {
		RayTraceResult result = player.rayTraceBlocks(maxDistance);

		return result != null ? result.getHitBlock() : null;
	}
}
