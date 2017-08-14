package ru.lexmint.integration;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import ru.lexmint.HSClans;

/**
 * Deals with world protection plugin - worldguard.
 */
public class WorldGuard {
    private static WorldGuardPlugin worldGuard;

    public static void setup() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            worldGuard = null;
            HSClans.instance.getLogger().severe("WorldGuard not found! Integration has failed.");
        } else {
            worldGuard = (WorldGuardPlugin) plugin;
        }
    }

    public static WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    /**
     * @param chunk Chunk which should be checked for regions.
     * @return True if WorldGuard regions within chunk has been founded. False, otherwise.
     */
    public static boolean checkForRegionsInChunk(Chunk chunk) {
        if (worldGuard == null) {
            // WorldGuard has not been found, so, bypass check.
            return false;
        }

        RegionManager regionManager = worldGuard.getRegionManager(chunk.getWorld());

        World world = chunk.getWorld();
        int minChunkX = chunk.getX() << 4;
        int minChunkZ = chunk.getZ() << 4;
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        int worldHeight = world.getMaxHeight();

        BlockVector minChunk = new BlockVector(minChunkX, 0, minChunkZ);
        BlockVector maxChunk = new BlockVector(maxChunkX, worldHeight, maxChunkZ);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgoverlapcheckhsc", minChunk, maxChunk);
        ApplicableRegionSet set = regionManager.getApplicableRegions(region);

        return set.size() >= 1;
    }

    /**
     * @param loc Location which chunk should be checked for regions.
     * @return True if WorldGuard regions within chunk has been founded. False, otherwise.
     */
    public static boolean checkForRegionsInChunk(Location loc) {
        return checkForRegionsInChunk(loc.getWorld().getChunkAt(loc));
    }
}
