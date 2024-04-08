package me.verwelius.treasurefix.algorithm;

import me.verwelius.treasurefix.config.Config;
import me.verwelius.treasurefix.util.NmsCaster;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.util.Random;
import java.util.logging.Logger;

public class Finder {

    private final static long X_MUL = 341873128712L;
    private final static long Z_MUL = 132897987541L;
    private final static long SALT = 10387320;

    private final NmsCaster nmsCaster;

    private final Logger logger;
    private final Config config;

    private World world;

    public Finder(NmsCaster nmsCaster, Logger logger, Config config) {
        this.nmsCaster = nmsCaster;
        this.logger = logger;
        this.config = config;
    }

    public ItemStack getMap(Location loc) {
        world = loc.getWorld();
        BlockPos pos = findNearest(loc.getBlockX() / 16, loc.getBlockZ() / 16);

        long start = System.currentTimeMillis();

        ServerLevel level = nmsCaster.toServerLevel(world);

        net.minecraft.world.item.ItemStack map = MapItem.create(level, pos.getX(), pos.getZ(),
                MapView.Scale.CLOSE.getValue(), true, true);

        map.getOrCreateTagElement("display")
                .putString("Name", "{\"translate\":\"filled_map.buried_treasure\"}");

        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, pos, "+", MapDecoration.Type.RED_X);

        long end = System.currentTimeMillis();

        int elapsed = (int) (end - start);

        if(config.logging) {
            logger.info(applyTime(config.mapCreated, elapsed));
        }

        return nmsCaster.toBukkit(map);
    }

    private BlockPos findNearest(int x, int z) {
        long start = System.currentTimeMillis();
        ChunkPos chunk = null;
        for(int r = 1; chunk == null; r++) {
            for(int i = 0; i < r * 2; i++) {
                if(hasTreasure(x - r + i, z - r)) chunk = new ChunkPos(i - r, -r);
                if(hasTreasure(x + r, z - r + i)) chunk = new ChunkPos(r, i - r);
                if(hasTreasure(x + r - i, z + r)) chunk = new ChunkPos(r - i, r);
                if(hasTreasure(x - r, z + r - i)) chunk = new ChunkPos(-r, r - i);
                if(chunk != null) break;
            }
        }

        int range = (int) Math.ceil(Math.sqrt(chunk.x * chunk.x + chunk.z * chunk.z));
        for(int i = -range; i <= range; i++) {
            for(int j = -range; j <= range; j++) {
                if(chunk.x * chunk.x + chunk.z * chunk.z < i * i + j * j) continue;
                if(hasTreasure(x + i, z + j)) chunk = new ChunkPos(i, j);
            }
        }

        if(config.logging) {
            logger.info(String.format(config.treasureFound, System.currentTimeMillis() - start));
        }
        return new BlockPos((chunk.x + x) * 16 + 9, 79, (chunk.z + z) * 16 + 9);
    }

    private String applyTime(String input, int time) {
        return input.replaceAll("%time%", String.valueOf(time));
    }

    private boolean hasTreasure(int x, int z) {
        long seed = x * X_MUL + z * Z_MUL + world.getSeed() + SALT;
        if(new Random(seed).nextFloat() >= 0.01) return false;
        Biome biome = world.getBiome((x << 4) + 9, 79, (z << 4) + 9);
        return biome == Biome.BEACH || biome == Biome.SNOWY_BEACH;
    }

}
