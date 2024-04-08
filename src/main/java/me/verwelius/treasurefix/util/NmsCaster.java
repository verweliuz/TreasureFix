package me.verwelius.treasurefix.util;

import net.minecraft.server.level.ServerLevel;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NmsCaster {

    private final Class craftItemStackClass;
    private final Class craftWorldClass;

    private final Method asNmsCopy;
    private final Method asBukkitCopy;

    public NmsCaster(String craftPackage) {
        try {
            craftItemStackClass = Class.forName(craftPackage + ".inventory.CraftItemStack");
            craftWorldClass = Class.forName(craftPackage + ".CraftWorld");
            
            asNmsCopy = craftItemStackClass.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
            asBukkitCopy = craftItemStackClass.getMethod("asBukkitCopy", net.minecraft.world.item.ItemStack.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerLevel toServerLevel(World world) {
        Object casted = craftWorldClass.cast(world);
        try {
            Object object = casted.getClass().getMethod("getHandle").invoke(casted);
            return ((ServerLevel) object);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public net.minecraft.world.item.ItemStack toNms(org.bukkit.inventory.ItemStack itemStack) {
        try {
            Object object = asNmsCopy.invoke(null, itemStack);
            return ((net.minecraft.world.item.ItemStack) object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public org.bukkit.inventory.ItemStack toBukkit(net.minecraft.world.item.ItemStack itemStack) {
        try {
            Object object = asBukkitCopy.invoke(null, itemStack);
            return ((org.bukkit.inventory.ItemStack) object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
