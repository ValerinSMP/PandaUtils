package darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaUtilities.ServerUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;

public abstract class NMSNBTAbstract {

	protected Class<?> craftItemstack;
	protected Method craftItemstackAsNMSCopy;

	public NMSNBTAbstract() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		try {
			craftItemstack = Class
					.forName("org.bukkit.craftbukkit." + ServerUtilities.VERSION + ".inventory.CraftItemStack");
		} catch (Throwable e) {
			craftItemstack = Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack");
		}
		craftItemstackAsNMSCopy = craftItemstack.getMethod("asNMSCopy", ItemStack.class);
	}

	protected abstract Class<?> getNbtTagCompound();

	protected abstract Class<?> getNbtTagList();

	protected abstract Class<?> getNbtTagString();

	protected abstract Class<?> getNbtBase();

	protected abstract Class<?> getItemstack();

	protected abstract Constructor<?> getNbtTagCompoundConstructor();

	protected abstract Constructor<?> getNbtTagStringConstructor();

	protected abstract Constructor<?> getNbtTagListConstructor();

	protected abstract MethodResult getNbtTagCompoundSet();

	protected abstract MethodResult getNbtTagCompoundGetString();

	protected abstract MethodResult getItemstackGetTag();

	protected abstract MethodResult getItemstackHasTag();

	protected abstract MethodResult getItemstackSetTag();

	protected abstract MethodResult getCraftItemstackAsBukkitCopy();

	public Object craftItemStackAsNMSCopy(Object itemstack) throws Exception {
		return craftItemstackAsNMSCopy.invoke(null, itemstack);
	}

	public Object craftItemStackAsBukkitCopy(Object itemstack) throws Exception {
		return getCraftItemstackAsBukkitCopy().invoke(null, itemstack);
	}

	public Object itemStackGetTag(Object itemstack) throws Exception {
		return getItemstackGetTag().invoke(itemstack);
	}

	public boolean itemStackHasTag(Object itemstack) throws Exception {
		return ((Boolean) getItemstackHasTag().invoke(itemstack)).booleanValue();
	}

	public void itemStackSetTag(Object itemstack, Object nbttag) throws Exception {
		getItemstackSetTag().invoke(itemstack, nbttag);
	}

	public Object nbtTagCompoundNew() throws Exception {
		return getNbtTagCompoundConstructor().newInstance();
	}

	public void nbtTagCompoundSetString(Object nbttagcompound, String id, String value) throws Exception {
		getNbtTagCompoundSet().invoke(nbttagcompound, id, nbtTagStringNew(value));
	}

	public void nbtTagCompoundSetNbtBase(Object nbttagcompound, String id, Object value) throws Exception {
		getNbtTagCompoundSet().invoke(nbttagcompound, id, value);
	}

	public Object nbtTagCompoundGetString(Object nbttagcompound, String id) throws Exception {
		return getNbtTagCompoundGetString().invoke(nbttagcompound, id);
	}

	public Object nbtTagStringNew(String value) throws Exception {
		return getNbtTagStringConstructor().newInstance(value);
	}

	public Object nbtTagListNew() throws Exception {
		return getNbtTagListConstructor().newInstance();
	}

}
