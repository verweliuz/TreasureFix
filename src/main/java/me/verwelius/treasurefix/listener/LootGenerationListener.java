package me.verwelius.treasurefix.listener;

import me.verwelius.treasurefix.algorithm.Finder;
import me.verwelius.treasurefix.util.NmsCaster;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

import java.util.Set;
import java.util.function.Predicate;

public class LootGenerationListener implements Listener {

    private static final Set<String> possibleSpawns = Set.of(
            LootTables.SHIPWRECK_MAP.getKey().getKey(),
            LootTables.UNDERWATER_RUIN_BIG.getKey().getKey(),
            LootTables.UNDERWATER_RUIN_SMALL.getKey().getKey()
    );

    private final NmsCaster nmsCaster;

    private net.minecraft.world.item.ItemStack toNms(ItemStack stack) {
        return nmsCaster.toNms(stack);
    }

    private final Predicate<ItemStack> filter = stack -> {
        if(stack == null) return false;
        if(stack.getType() != Material.MAP) return false;

        net.minecraft.world.item.ItemStack map = toNms(stack);
        CompoundTag tag = map.getTagElement("display");
        if(tag == null) return false;

        String name = tag.getString("Name");
        if(name == null) return false;

        return name.equals("{\"translate\":\"filled_map.buried_treasure\"}");
    };


    private final Finder finder;

    public LootGenerationListener(NmsCaster nmsCaster, Finder finder) {
        this.nmsCaster = nmsCaster;
        this.finder = finder;
    }

    @EventHandler
    private void onMapGeneration(LootGenerateEvent event) {
        if(!possibleSpawns.contains(event.getLootTable().getKey().getKey())) return;

        event.getLoot().replaceAll(stack -> {
            if(!filter.test(stack)) return stack;

            BlockInventoryHolder chest = (BlockInventoryHolder) event.getInventoryHolder();
            return finder.getMap(chest.getBlock().getLocation());
        });
    }
}
