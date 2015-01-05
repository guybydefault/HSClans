package ru.lexmint.integration;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains requests to API of other plugin (WorldBorder, WorldGuard).
 */
public class WorldProtection {
    /**
     * @return WorldBorder plugin instance. May return null if plugin has not been loaded.
     */
    public static WorldBorder getWorldBorder() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldBorder");
        if (plugin == null) {
            return null;
        }
        return (WorldBorder) plugin;
    }

    /**
     * @return WorldGuard plugin instance. May return null if plugin has not been loaded.
     */
    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    /**
     * @param world World for which you need region manager.
     * @return Region manager, object, which deals with regions in given world.
     * May return null if WorldGuard has not been loaded.
     */
    public static RegionManager getRegionManager(World world) {
        WorldGuardPlugin wgp = getWorldGuard();
        if (wgp != null) {
            return wgp.getRegionManager(world);
        } else {
            return null;
        }
    }

    /**
     * @param loc Location which chunk should be checked for regions.
     * @return True if WorldGuard regions within chunk has been founded. False, otherwise.
     */
    public static boolean checkForRegionsInChunk(Location loc) {
        return checkForRegionsInChunk(loc.getWorld().getChunkAt(loc));
    }

    /**
     * @param chunk Chunk which should be checked for regions.
     * @return True if WorldGuard regions within chunk has been founded. False, otherwise.
     */
    public static boolean checkForRegionsInChunk(Chunk chunk) {
        RegionManager regionManager = getRegionManager(chunk.getWorld());
        if (regionManager == null) {
            // WorldGuard has not been found, so, bypass check.
            return false;
        }

        World world = chunk.getWorld();
        int minChunkX = chunk.getX() << 4;
        int minChunkZ = chunk.getZ() << 4;
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        int worldHeight = world.getMaxHeight();

        BlockVector minChunk = new BlockVector(minChunkX, 0, minChunkZ);
        BlockVector maxChunk = new BlockVector(maxChunkX, worldHeight, maxChunkZ);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgoverlapcheckhsc", minChunk, maxChunk);
        Map<String, ProtectedRegion> allRegions = regionManager.getRegions();
        List<ProtectedRegion> allRegionsList = new ArrayList<ProtectedRegion>(allRegions.values());
        List<ProtectedRegion> overlaps;

        boolean overlapping = false;
        try {
            overlaps = region.getIntersectingRegions(allRegionsList);
            if (overlaps != null && !overlaps.isEmpty()) {
                overlapping = true;
            }
        } catch (UnsupportedIntersectionException e) {
            e.printStackTrace();
        }

        return overlapping;
    }
}
