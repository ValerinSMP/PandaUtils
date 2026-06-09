package darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT;

import java.lang.reflect.Constructor;

import darkpanda73.PandaUtils.PandaUtilities.ServerUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class NMSNBTv1 extends NMSNBTAbstract {

	private Class<?> nbtTagCompound;
	private Class<?> nbtTagList;
	private Class<?> nbtTagString;
	private Class<?> nbtBase;
	private Class<?> itemstack;

	private Constructor<?> nbtTagCompoundConstructor;
	private Constructor<?> nbtTagStringConstructor;
	private Constructor<?> nbtTagListConstructor;

	private MethodResult nbtTagCompoundSet;
	private MethodResult nbtTagCompoundGetString;
	private MethodResult itemstackGetTag;
	private MethodResult itemstackHasTag;
	private MethodResult itemstackSetTag;
	private MethodResult craftItemstackAsBukkitCopy;

	public NMSNBTv1() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		super();

		if (ServerUtilities.VERSION == null) {
			throw new NullPointerException("Unable to retrieve NMS version.");
		}

		nbtTagCompound = Class.forName("net.minecraft.server." + ServerUtilities.VERSION + ".NBTTagCompound");
		nbtTagList = Class.forName("net.minecraft.server." + ServerUtilities.VERSION + ".NBTTagList");
		nbtTagString = Class.forName("net.minecraft.server." + ServerUtilities.VERSION + ".NBTTagString");
		nbtBase = Class.forName("net.minecraft.server." + ServerUtilities.VERSION + ".NBTBase");
		itemstack = Class.forName("net.minecraft.server." + ServerUtilities.VERSION + ".ItemStack");

		nbtTagCompoundConstructor = nbtTagCompound.getConstructor();
		nbtTagStringConstructor = nbtTagString.getConstructor(String.class);
		nbtTagListConstructor = nbtTagList.getConstructor();

		nbtTagCompoundSet = MethodFinder.forClass(nbtTagCompound).setName("set").setParams(String.class, nbtBase)
				.find();
		nbtTagCompoundGetString = MethodFinder.forClass(nbtTagCompound).setName("getString").setParams(String.class)
				.setReturnType(String.class).find();
		itemstackGetTag = MethodFinder.forClass(itemstack).setName("getTag").setReturnType(getNbtTagCompound()).find();
		itemstackHasTag = MethodFinder.forClass(itemstack).setName("hasTag").setReturnType(boolean.class).find();
		itemstackSetTag = MethodFinder.forClass(itemstack).setName("setTag").setParams(nbtTagCompound).find();
		craftItemstackAsBukkitCopy = MethodFinder.forClass(craftItemstack).setName("asBukkitCopy").setParams(itemstack)
				.find();
	}

}
