package darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT;

import java.lang.reflect.Constructor;

import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public class NMSNBTv6 extends NMSNBTAbstract {

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
	private MethodResult nbtTagStringA;
	private MethodResult itemstackGetTag;
	private MethodResult itemstackHasTag;
	private MethodResult itemstackSetTag;
	private MethodResult craftItemstackAsBukkitCopy;

	public NMSNBTv6() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		super();
		nbtTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
		nbtTagList = Class.forName("net.minecraft.nbt.NBTTagList");
		nbtTagString = Class.forName("net.minecraft.nbt.NBTTagString");
		nbtBase = Class.forName("net.minecraft.nbt.NBTBase");
		itemstack = Class.forName("net.minecraft.world.item.ItemStack");

		nbtTagCompoundConstructor = nbtTagCompound.getConstructor();
		nbtTagListConstructor = nbtTagList.getConstructor();

		nbtTagCompoundSet = MethodFinder.forClass(nbtTagCompound).setName("a").setParams(String.class, nbtBase).find();
		nbtTagCompoundGetString = MethodFinder.forClass(nbtTagCompound).setName("l").setParams(String.class)
				.setReturnType(String.class).find();
		nbtTagStringA = MethodFinder.forClass(nbtTagCompound).setName("a").setParams(String.class).find();
		itemstackGetTag = MethodFinder.forClass(itemstack).setName("v").setReturnType(getNbtTagCompound()).find();
		itemstackHasTag = MethodFinder.forClass(itemstack).setName("u").setReturnType(boolean.class).find();
		itemstackSetTag = MethodFinder.forClass(itemstack).setName("c").setParams(nbtTagCompound).find();
		craftItemstackAsBukkitCopy = MethodFinder.forClass(craftItemstack).setName("asBukkitCopy").setParams(itemstack)
				.find();

	}

	@Override
	public Object nbtTagStringNew(String value) throws Exception {
		return nbtTagStringA.invoke(null, value);
	}

}
