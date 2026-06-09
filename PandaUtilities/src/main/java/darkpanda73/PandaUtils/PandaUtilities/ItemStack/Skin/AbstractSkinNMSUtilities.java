package darkpanda73.PandaUtils.PandaUtilities.ItemStack.Skin;

import org.bukkit.inventory.ItemStack;

public abstract class AbstractSkinNMSUtilities {

	public ItemStack setSkinSafe(ItemStack item, String base64) {
		return setSkinSafe(item, base64, null);
	}

	public ItemStack setSkinSafe(ItemStack item, String base64, String url) {
		try {
			return setSkin(item, base64, url);
		} catch (Exception e) {
			return item;
		}
	}

	public ItemStack setSkin(ItemStack item, String base64) throws Exception {
		return setSkin(item, base64, null);
	}

	public ItemStack setSkin(ItemStack item, String base64, String url) throws Exception {
		try {
			if (url != null && !url.isEmpty()) {
				String originalSkinUrl = getSkinAsURL(item);
				if (originalSkinUrl != null && originalSkinUrl.equals(base64)) {
					return item;
				}

				setSkinByUrl(item, url);
				return item;
			}
		} catch (Throwable e) {
		}

		if (base64 != null && !base64.isEmpty()) {
			String originalSkinBase64 = getSkinAsBase64(item);
			if (originalSkinBase64 != null && originalSkinBase64.equals(base64)) {
				return item;
			}

			setSkinByBase64(item, base64);
		}
		return item;
	}

	public abstract void setSkinByBase64(ItemStack item, String base64) throws Exception;

	public abstract void setSkinByUrl(ItemStack item, String url) throws Exception;

	public abstract String getSkinAsBase64(ItemStack item);

	public abstract String getSkinAsURL(ItemStack item);

}
