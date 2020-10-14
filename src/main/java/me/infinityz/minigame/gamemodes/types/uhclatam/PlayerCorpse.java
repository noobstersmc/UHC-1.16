package me.infinityz.minigame.gamemodes.types.uhclatam;



import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

public class PlayerCorpse {

    private int entityID;
    private Location location;
    private GameProfile gameprofile;

    public PlayerCorpse(String username, Location location) {
        entityID = (int) Math.ceil(Math.random() * 1000) + 2000;
        gameprofile = new GameProfile(UUID.randomUUID(), username);
        this.location = location.clone();
    }

    private void addToTablist() throws Exception {
        Class<?> cIChatBaseComponent = getNMSClass("IChatBaseComponent");
        Class<?> cPacketPlayOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
        Class<?> cPlayerInfoData = getNMSClass("PacketPlayOutPlayerInfo$PlayerInfoData");
        Class<?> cEnumGamemode = getNMSClass("WorldSettings$EnumGamemode");
        Object pPacketPlayOutInfo = cPacketPlayOutPlayerInfo.getConstructor().newInstance();
        Field fa = pPacketPlayOutInfo.getClass().getDeclaredField("a");
        fa.setAccessible(true);
        fa.set(pPacketPlayOutInfo, getEnumConstant(getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), "ADD_PLAYER"));
        Object oPlayerInfoData = cPlayerInfoData.getConstructor(cPacketPlayOutPlayerInfo, GameProfile.class, int.class, cEnumGamemode, cIChatBaseComponent)
                .newInstance(pPacketPlayOutInfo, gameprofile, 1, getEnumConstant(getNMSClass("WorldSettings$EnumGamemode"), "NOT_SET"), serializeString(gameprofile.getName()));
        Field b = pPacketPlayOutInfo.getClass().getDeclaredField("b");
        b.setAccessible(true);
        @SuppressWarnings("unchecked")
        ArrayList<Object> array = (ArrayList<Object>) b.get(pPacketPlayOutInfo);
        array.add(oPlayerInfoData);
        b.set(pPacketPlayOutInfo, array);
        sendPacket(pPacketPlayOutInfo);
    }

    private void animation(int var1) throws Exception {
        Class<?> packetClass = getNMSClass("PacketPlayOutAnimation");
        Object packet = packetClass.newInstance();
        setValue(packet, "a", entityID);
        setValue(packet, "b", (byte) var1);
        sendPacket(packet);
    }

    public void destroy() throws Exception {
        Object removeEntity = invokeConstructor(getNMSClass("PacketPlayOutEntityDestroy"), new Class<?>[]{int[].class}, new int[]{entityID});
        removeFromTablist();
        sendPacket(removeEntity);
    }

    private int floor(double var1) {
        int i = (int) var1;
        return var1 < i ? i - 1 : i;
    }

    private static Class<?> getClass(String name, boolean asArray) {
        try {
            if (asArray) return Array.newInstance(Class.forName(name), 0).getClass();
            else return Class.forName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        Object con = conField.get(nmsPlayer);
        return con;
    }

    private Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws Exception {
        Constructor<?> c = clazz.getConstructor(args);
        c.setAccessible(true);
        return c;
    }

    private Class<?> getCraftBukkitClass(String name) {
        return getClass(getCraftBukkitPrefix() + name, false);
    }

    private String getCraftBukkitPrefix() {
        return "org.bukkit.craftbukkit." + getVersion() + ".";
    }

    private Object getEnumConstant(Class<?> enumClass, String name) throws Exception {
        if (!enumClass.isEnum())
            return null;
        for (Object o : enumClass.getEnumConstants())
            if (name.equals(invokeMethod(o, "name", new Class[0])))
                return o;
        return null;
    }

    private int getFixLocation(double var1) {
        return floor(var1 * 32.0D);
    }

    private byte getFixRotation(float var1) {
        return (byte) ((int) (var1 * 256.0F / 360.0F));
    }

    private Location getGround(Location var1) {
        return new Location(var1.getWorld(),
                var1.getX(),
                location.getWorld().getHighestBlockYAt(var1.getBlockX(), var1.getBlockZ()),
                var1.getZ());
    }

    private Method getMethod(Class<?> clazz, String mname) {
        Method m = null;
        try {
            m = clazz.getDeclaredMethod(mname);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname);
            } catch (Exception ex) {
                for (Method me : clazz.getDeclaredMethods()) {
                    if (me.getName().equalsIgnoreCase(mname))
                        m = me;
                    break;
                }
                if (m == null)
                    for (Method me : clazz.getMethods()) {
                        if (me.getName().equalsIgnoreCase(mname))
                            m = me;
                        break;
                    }
            }
        }
        m.setAccessible(true);
        return m;
    }

    private Class<?> getNMSClass(String clazz) throws Exception {
        return Class.forName("net.minecraft.server." + getVersion() + "." + clazz);
    }

    private static String getVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf(".") + 1);
        return version;
    }

    private void headRotation(float var1, float var2) throws Exception {
        Object packet = invokeConstructor(getNMSClass("PacketPlayOutEntity$PacketPlayOutEntityLook"), new Class<?>[]{int.class, byte.class, byte.class, boolean.class}, entityID, getFixRotation(var1), getFixRotation(var2), true);
        Class<?> packetClass = getNMSClass("PacketPlayOutEntityHeadRotation");
        Object packetHead = packetClass.newInstance();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(var1));
        sendPacket(packet);
        sendPacket(packetHead);
    }

    private Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initargs) throws Exception {
        return getConstructor(clazz, args).newInstance(initargs);
    }

    private Object invokeMethod(Object obj, String method, Object[] initargs) throws Exception {
        return getMethod(obj.getClass(), method).invoke(obj, initargs);
    }

    private void removeFromTablist() throws Exception {
        Class<?> cIChatBaseComponent = getNMSClass("IChatBaseComponent");
        Class<?> cPacketPlayOutPlayerInfo = getNMSClass("PacketPlayOutPlayerInfo");
        Class<?> cPlayerInfoData = getNMSClass("PacketPlayOutPlayerInfo$PlayerInfoData");
        Class<?> cEnumGamemode = getNMSClass("WorldSettings$EnumGamemode");
        Object pPacketPlayOutInfo = cPacketPlayOutPlayerInfo.getConstructor().newInstance();
        Field fa = pPacketPlayOutInfo.getClass().getDeclaredField("a");
        fa.setAccessible(true);
        fa.set(pPacketPlayOutInfo, getEnumConstant(getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), "REMOVE_PLAYER"));
        Object oPlayerInfoData = cPlayerInfoData.getConstructor(cPacketPlayOutPlayerInfo, GameProfile.class, int.class, cEnumGamemode, cIChatBaseComponent)
                .newInstance(pPacketPlayOutInfo, gameprofile, 1, getEnumConstant(getNMSClass("WorldSettings$EnumGamemode"), "NOT_SET"), serializeString(gameprofile.getName()));
        Field b = pPacketPlayOutInfo.getClass().getDeclaredField("b");
        b.setAccessible(true);
        @SuppressWarnings("unchecked")
        ArrayList<Object> array = (ArrayList<Object>) b.get(pPacketPlayOutInfo);
        array.add(oPlayerInfoData);
        b.set(pPacketPlayOutInfo, array);
        sendPacket(pPacketPlayOutInfo);
    }

    private void sendPacket(Object packet) throws Exception {
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket.invoke(getConnection(player), packet);
        }
    }

    private void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("deprecation")
    private void sleep(boolean var1) throws Exception {
        if (var1) {
            Location bedLocation = new Location(location.getWorld(), 1, 1, 1);
            Class<?> packetClass = getNMSClass("PacketPlayOutBed");
            Object bedPacket = packetClass.newInstance();
            setValue(bedPacket, "a", entityID);
            if (getVersion().contains("1.7")) {
                setValue(bedPacket, "b", bedLocation.getBlockX());
                setValue(bedPacket, "c", bedLocation.getBlockY());
                setValue(bedPacket, "d", bedLocation.getBlockZ());
            } else {
                setValue(bedPacket, "b", getNMSClass("BlockPosition")
                        .getConstructor
                                (int.class, int.class, int.class)
                        .newInstance
                                (bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ()));
            }
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.sendBlockChange(bedLocation, Material.RED_BED, (byte) 0);
            }
            sendPacket(bedPacket);
            teleport(getGround(location).add(0, 0.125, 0));
        } else {
            animation(2);
            teleport(getGround(location).add(0, 0.125, 0));
        }
    }

    public void spawn() throws Exception {
        Class<?> packetClass = getNMSClass("PacketPlayOutNamedEntitySpawn");
        Object packet = packetClass.newInstance();
        setValue(packet, "a", entityID);
        setValue(packet, "b", gameprofile.getId());
        setValue(packet, "c", getFixLocation(location.getX()));
        setValue(packet, "d", getFixLocation(location.getY()));
        setValue(packet, "e", getFixLocation(location.getZ()));
        setValue(packet, "f", getFixRotation(location.getYaw()));
        setValue(packet, "g", getFixRotation(location.getPitch()));
        setValue(packet, "h", 0);
        Class<?> dw = getNMSClass("DataWatcher");
        Object dataWatcher = dw.getConstructor(getNMSClass("Entity")).newInstance(new Object[]{null});
        Method a = dw.getMethod("a", int.class, Object.class);
        a.invoke(dataWatcher, 6, (float) 20);
        a.invoke(dataWatcher, 10, (byte) 127);
        setValue(packet, "i", dataWatcher);
        addToTablist();
        sendPacket(packet);
        headRotation(location.getYaw(), location.getPitch());
        removeFromTablist();
        sleep(true);
    }

    private Object serializeString(String s) {
        try {
            Class<?> chatSerelizer = getCraftBukkitClass("util.CraftChatMessage");

            Method mSerelize = chatSerelizer.getMethod("fromString", String.class);

            return ((Object[]) mSerelize.invoke(null, s))[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void teleport(Location var1) throws Exception{
        Class<?> packetClass = getNMSClass("PacketPlayOutEntityTeleport");
        Object packet = packetClass.newInstance();
        setValue(packet, "a", entityID);
        setValue(packet, "b", getFixLocation(var1.getX()));
        setValue(packet, "c", getFixLocation(var1.getY()));
        setValue(packet, "d", getFixLocation(var1.getZ()));
        setValue(packet, "e", getFixRotation(var1.getYaw()));
        setValue(packet, "f", getFixRotation(var1.getPitch()));
        sendPacket(packet);
        headRotation(var1.getYaw(), var1.getPitch());
        this.location = var1.clone();
    }
}
