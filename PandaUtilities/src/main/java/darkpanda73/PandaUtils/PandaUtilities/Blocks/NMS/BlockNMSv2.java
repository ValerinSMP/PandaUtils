package darkpanda73.PandaUtils.PandaUtilities.Blocks.NMS;

import java.lang.reflect.Constructor;
import java.util.UUID;

import org.bukkit.block.Block;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import darkpanda73.PandaUtils.PandaUtilities.ServerUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(value = AccessLevel.PROTECTED)
public class BlockNMSv2 extends BlockNMSAbstract {

	private Class<?> worldClass;
	private Class<?> nmsWorldClass;
	private Class<?> tileEntitySkullClass;
	private Class<?> blockPositionClass;

	private Constructor<?> blockPositionConstructor;

	private MethodResult worldGetHandleMethod;
	private MethodResult nmsWorldGetTileEntityMethod;
	private MethodResult tileEntitySkullSetGameProfileMethod;

	public BlockNMSv2() throws Exception {
		if (ServerUtilities.VERSION == null) {
			throw new NullPointerException("Unable to retrieve NMS version.");
		}

		worldClass = Class.forName("org.bukkit.craftbukkit." + ServerUtilities.VERSION + ".CraftWorld");
		nmsWorldClass = Class.forName("net.minecraft.world.level.World");
		tileEntitySkullClass = Class.forName("net.minecraft.world.level.block.entity.TileEntitySkull");
		blockPositionClass = Class.forName("net.minecraft.core.BlockPosition");

		blockPositionConstructor = blockPositionClass.getConstructor(int.class, int.class, int.class);

		worldGetHandleMethod = MethodFinder.forData(worldClass, "getHandle").find();
		nmsWorldGetTileEntityMethod = MethodFinder.forData(nmsWorldClass, "c_", blockPositionClass).find();
		tileEntitySkullSetGameProfileMethod = MethodFinder.forData(tileEntitySkullClass, "a", GameProfile.class).find();
	}

	@Override
	public void setSkin(Block block, String value) {
		if (value != null && !value.isEmpty()) {
			try {
				GameProfile profile = new GameProfile(UUID.randomUUID(), "0");
				profile.getProperties().put("textures", new Property("textures", value, ""));
				Object world = getWorldGetHandleMethod().invoke(block.getWorld());
				Object blockPosition = getBlockPositionConstructor().newInstance(block.getX(), block.getY(),
						block.getZ());
				Object skullTile = getNmsWorldGetTileEntityMethod().invoke(world, blockPosition);
				getTileEntitySkullSetGameProfileMethod().invoke(skullTile, profile);
				block.getState().update(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Object world = getWorldGetHandleMethod().invoke(block.getWorld());
				Object blockPosition = getBlockPositionConstructor().newInstance(block.getX(), block.getY(),
						block.getZ());
				Object skullTile = getNmsWorldGetTileEntityMethod().invoke(world, blockPosition);
				getTileEntitySkullSetGameProfileMethod().invoke(skullTile, (Object) null);
				block.getState().update(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
