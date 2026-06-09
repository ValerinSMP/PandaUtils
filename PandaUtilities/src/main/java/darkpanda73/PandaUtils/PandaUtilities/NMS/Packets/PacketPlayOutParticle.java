package darkpanda73.PandaUtils.PandaUtilities.NMS.Packets;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder;
import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder.ClassResult;
import darkpanda73.PandaUtils.Utilities.Java.Enum.EnumUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Fields.FieldFinder;
import darkpanda73.PandaUtils.Utilities.Java.Fields.FieldFinder.FieldResult;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PacketPlayOutParticle {

	private static PacketPlayOutParticleBuilder NMS_PACKET_CLASS_CONSTRUCTOR;

	static {
		List<Throwable> throwables = new ArrayList<>();

		NMS_PACKET_CLASS_CONSTRUCTOR = Arrays
				.asList((FailableSupplier<PacketPlayOutParticleBuilder>) () -> new PacketPlayOutParticleBuilder1_21_3(),
						(FailableSupplier<PacketPlayOutParticleBuilder>) () -> new PacketPlayOutParticleBuilder1_21(),
						(FailableSupplier<PacketPlayOutParticleBuilder>) () -> new PacketPlayOutParticleBuilder1_16_5())
				.stream().map(supplier -> {
					try {
						return supplier.get();
					} catch (Throwable e) {
						throwables.add(e);
						return null;
					}
				}).filter(Objects::nonNull).findFirst().orElse(null);

		if (NMS_PACKET_CLASS_CONSTRUCTOR == null && !throwables.isEmpty()) {
			System.out.println("Unable to load NMS data to use PacketPlayOutParticle:");
			throwables.forEach(Throwable::printStackTrace);
		}
	}

	public static boolean isAvailable() {
		return NMS_PACKET_CLASS_CONSTRUCTOR != null;
	}

	private double x;
	private double y;
	private double z;

	private float xDist;
	private float yDist;
	private float zDist;

	private float maxSpeed;

	private int count;

	private boolean overrideLimiter;

	private ParticleOptions particleOptions;

	public PacketPlayOutParticle(double x, double y, double z, float xDist, float yDist, float zDist, float maxSpeed,
			int count, boolean overrideLimiter, ParticleOptions particleOptions) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xDist = xDist;
		this.yDist = yDist;
		this.zDist = zDist;
		this.maxSpeed = maxSpeed;
		this.count = count;
		this.overrideLimiter = overrideLimiter;
		this.particleOptions = particleOptions;
	}

	public void send(Player player) throws Exception {
		if (NMS_PACKET_CLASS_CONSTRUCTOR != null) {
			NMS_PACKET_CLASS_CONSTRUCTOR.send(player, this);
		}
	}

	private static interface PacketPlayOutParticleBuilder {

		public void send(Player player, PacketPlayOutParticle packet) throws Exception;

	}

	private static class PacketPlayOutParticleBuilder1_21_3 implements PacketPlayOutParticleBuilder {

		private ClassResult particlePacketClass;
		private ClassResult particleParamClass;

		private Constructor<?> packetClassConstructor;

		private Constructor<?> particleParamRedstoneConstructor;

		private ClassResult craftPlayerClass;
		private ClassResult playerConnectionClass;
		private MethodResult getHandleMethod;
		private FieldResult playerConnectionField;
		private MethodResult sendPacketMethod;

		public PacketPlayOutParticleBuilder1_21_3() throws Exception {
			particlePacketClass = ClassFinder
					.forClass("net.minecraft.network.protocol.game.PacketPlayOutWorldParticles").find();
			particleParamClass = ClassFinder.forClass("net.minecraft.core.particles.ParticleParam").find();

			packetClassConstructor = particlePacketClass.getFoundClass().getConstructor(
					particleParamClass.getFoundClass(), boolean.class, double.class, double.class, double.class,
					float.class, float.class, float.class, float.class, int.class);

			particleParamRedstoneConstructor = ClassFinder
					.forClass("net.minecraft.core.particles.ParticleParamRedstone").find().getFoundClass()
					.getConstructor(int.class, float.class);

			craftPlayerClass = ClassFinder.forClass("org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer").find();
			playerConnectionClass = ClassFinder.forClass("net.minecraft.server.network.PlayerConnection").find();
			getHandleMethod = MethodFinder.forClass(craftPlayerClass.getFoundClass()).setName("getHandle").find();
			playerConnectionField = FieldFinder
					.forClass(ClassFinder.forClass("net.minecraft.server.level.EntityPlayer").find().getFoundClass())
					.setType(playerConnectionClass.getFoundClass()).find();
			sendPacketMethod = MethodFinder.forClass(playerConnectionField.getField().getType())
					.setParams(ClassFinder.forClass("net.minecraft.network.protocol.Packet").find().getFoundClass())
					.setName("sendPacket").find();
			System.out.println(sendPacketMethod.getMethod());
		}

		private Object toNMSParticleOptions(ParticleOptions particleOptions) throws Exception {
			if (particleOptions instanceof DustParticleOptions) {
				DustParticleOptions options = (DustParticleOptions) particleOptions;
				return particleParamRedstoneConstructor.newInstance(options.getColor().asRGB(), options.getScale());
			} else {
				throw new IllegalAccessException("Unimplemented options");
			}
		}

		private Object toNMSPacket(PacketPlayOutParticle packet) throws Exception {
			return packetClassConstructor.newInstance(toNMSParticleOptions(packet.getParticleOptions()),
					packet.isOverrideLimiter(), packet.getX(), packet.getY(), packet.getZ(), packet.getXDist(),
					packet.getYDist(), packet.getZDist(), packet.getMaxSpeed(), packet.getCount());
		}

		public void send(Player player, PacketPlayOutParticle packet) throws Exception {
			Object craftPlayer = getHandleMethod.invoke(player);
			Object playerConnection = playerConnectionField.getValue(craftPlayer);
			this.sendPacketMethod.invoke(playerConnection, toNMSPacket(packet));
		}

	}

	private static class PacketPlayOutParticleBuilder1_21 implements PacketPlayOutParticleBuilder {

		private ClassResult particlePacketClass;
		private ClassResult particleOptionsClass;

		private Constructor<?> packetClassConstructor;

		private ClassResult vector3fClass;
		private ClassResult vec3Class;
		private MethodResult vec3FromRGBMethod;
		private MethodResult vec3ToVector3fMethod;

		private Constructor<?> dustParticleOptionsConstructor;

		private ClassResult craftPlayerClass;
		private MethodResult getHandleMethod;
		private ClassResult serverPlayerClass;
		private FieldResult playerConnectionField;
		private MethodResult sendPacketMethod;

		public PacketPlayOutParticleBuilder1_21() throws Exception {
			particlePacketClass = ClassFinder
					.forClass("net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket").find();
			particleOptionsClass = ClassFinder.forClass("net.minecraft.core.particles.ParticleOptions").find();

			packetClassConstructor = particlePacketClass.getFoundClass().getConstructor(
					particleOptionsClass.getFoundClass(), boolean.class, double.class, double.class, double.class,
					float.class, float.class, float.class, float.class, int.class);

			vector3fClass = ClassFinder.forClass("org.joml.Vector3f").find();
			vec3Class = ClassFinder.forClass("net.minecraft.world.phys.Vec3").find();
			vec3FromRGBMethod = MethodFinder.forClass(vec3Class.getFoundClass()).setVisibility(Visibility.PUBLIC)
					.setStaticAccess(true).setName("fromRGB24").find();
			vec3ToVector3fMethod = MethodFinder.forClass(vec3Class.getFoundClass()).setVisibility(Visibility.PUBLIC)
					.setName("toVector3f").find();

			dustParticleOptionsConstructor = ClassFinder.forClass("net.minecraft.core.particles.DustParticleOptions")
					.find().getFoundClass().getConstructor(vector3fClass.getFoundClass(), float.class);

			craftPlayerClass = ClassFinder.forClass("org.bukkit.craftbukkit.entity.CraftPlayer").find();
			getHandleMethod = MethodFinder.forClass(craftPlayerClass.getFoundClass()).setName("getHandle").find();
			serverPlayerClass = ClassFinder.forClass("net.minecraft.server.level.ServerPlayer").find();
			playerConnectionField = FieldFinder.forClass(serverPlayerClass.getFoundClass()).setName("connection")
					.find();
			sendPacketMethod = MethodFinder.forClass(playerConnectionField.getField().getType()).setName("send")
					.setParams(particlePacketClass.getFoundClass()).find();
		}

		private Object toNMSParticleOptions(ParticleOptions particleOptions) throws Exception {
			if (particleOptions instanceof DustParticleOptions) {
				DustParticleOptions options = (DustParticleOptions) particleOptions;
				return dustParticleOptionsConstructor.newInstance(
						vec3ToVector3fMethod.invoke(vec3FromRGBMethod.invoke(null, options.getColor().asRGB())),
						options.getScale());
			} else {
				throw new IllegalAccessException("Unimplemented options");
			}
		}

		private Object toNMSPacket(PacketPlayOutParticle packet) throws Exception {
			return packetClassConstructor.newInstance(toNMSParticleOptions(packet.getParticleOptions()),
					packet.isOverrideLimiter(), packet.getX(), packet.getY(), packet.getZ(), packet.getXDist(),
					packet.getYDist(), packet.getZDist(), packet.getMaxSpeed(), packet.getCount());
		}

		public void send(Player player, PacketPlayOutParticle packet) throws Exception {
			Object craftPlayer = getHandleMethod.invoke(player);
			Object playerConnection = playerConnectionField.getValue(craftPlayer);
			this.sendPacketMethod.invoke(playerConnection, toNMSPacket(packet));
		}

	}

	private static class PacketPlayOutParticleBuilder1_16_5 implements PacketPlayOutParticleBuilder {

		private ClassResult particlePacketClass;
		private Constructor<?> packetClassConstructor;

		private ClassResult particleParamClass;
		private ClassResult particleParamRedstoneClass;
		private Constructor<?> particleParamRedstoneConstructor;

		private ClassResult craftPlayerClass;
		private MethodResult getHandleMethod;
		private ClassResult entityPlayerClass;
		private FieldResult playerConnectionField;
		private MethodResult sendPacketMethod;

		public PacketPlayOutParticleBuilder1_16_5() throws Exception {
			particlePacketClass = ClassFinder.forClass("net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles")
					.find();
			particleParamClass = ClassFinder.forClass("net.minecraft.server.v1_16_R3.ParticleParam").find();
			particleParamRedstoneClass = ClassFinder.forClass("net.minecraft.server.v1_16_R3.ParticleParamRedstone")
					.find();

			packetClassConstructor = particlePacketClass.getFoundClass().getConstructor(
					particleParamClass.getFoundClass(), boolean.class, double.class, double.class, double.class,
					float.class, float.class, float.class, float.class, int.class);

			particleParamRedstoneConstructor = particleParamRedstoneClass.getFoundClass().getConstructor(float.class,
					float.class, float.class, float.class);

			craftPlayerClass = ClassFinder.forClass("org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer").find();
			getHandleMethod = MethodFinder.forClass(craftPlayerClass.getFoundClass()).setName("getHandle").find();
			entityPlayerClass = ClassFinder.forClass("net.minecraft.server.v1_16_R3.EntityPlayer").find();
			playerConnectionField = FieldFinder.forClass(entityPlayerClass.getFoundClass()).setName("playerConnection")
					.find();
			sendPacketMethod = MethodFinder.forClass(playerConnectionField.getField().getType()).setName("sendPacket")
					.setParams(particlePacketClass.getFoundClass()).find();
		}

		private Object toNMSParticleOptions(ParticleOptions particleOptions) throws Exception {
			if (particleOptions instanceof DustParticleOptions) {
				DustParticleOptions options = (DustParticleOptions) particleOptions;
				return particleParamRedstoneConstructor.newInstance((float) options.getColor().getRed() / 255F,
						(float) options.getColor().getGreen() / 255F, (float) options.getColor().getBlue() / 255F,
						options.getScale());
			} else {
				throw new IllegalAccessException("Unimplemented options");
			}
		}

		private Object toNMSPacket(PacketPlayOutParticle packet) throws Exception {
			return packetClassConstructor.newInstance(toNMSParticleOptions(packet.getParticleOptions()),
					packet.isOverrideLimiter(), packet.getX(), packet.getY(), packet.getZ(), packet.getXDist(),
					packet.getYDist(), packet.getZDist(), packet.getMaxSpeed(), packet.getCount());
		}

		public void send(Player player, PacketPlayOutParticle packet) throws Exception {
			Object craftPlayer = getHandleMethod.invoke(player);
			Object playerConnection = playerConnectionField.getValue(craftPlayer);
			this.sendPacketMethod.invoke(playerConnection, toNMSPacket(packet));
		}

	}

	@AllArgsConstructor
	@Getter
	public static abstract class ParticleOptions {

		private Particle type;

	}

	public static class DustParticleOptions extends ParticleOptions {

		private @Getter Color color;
		private @Getter float scale;

		public DustParticleOptions(Color color, float scale) {
			super(EnumUtilities.findByAny(Particle.class, "DUST", "REDSTONE"));

			this.color = color;
			this.scale = scale;
		}

	}

	private static interface FailableSupplier<T> {

		public abstract T get() throws Throwable;

	}

}
