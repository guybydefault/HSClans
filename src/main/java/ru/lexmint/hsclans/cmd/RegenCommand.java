package ru.lexmint.hsclans.cmd;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.integration.Border;
import ru.lexmint.hsclans.integration.WorldGuardIntegration;
import ru.lexmint.hscore.cmd.AbstractCommand;

/**
 * Regenerates world except those chunks where are some clan claims or WorldGuard claims.
 */
class RegenCommand extends AbstractCommand {

    public RegenCommand(String[] aliases, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
    }

    private RegenTask regenTask = null;

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        WorldBorder worldBorder = Border.getWorldBorder();
        if (worldBorder == null) {
            HSClans.instance.getMessenger().message("commands.regen.plugin-not-found", sender);
            return;
        }

        World world = Bukkit.getServer().getWorld(subargs[1]);
        if (world == null) {
            HSClans.instance.getMessenger().message("commands.regen.world-not-found", sender, subargs[1]);
            return;
        }

        BorderData borderData = worldBorder.GetWorldBorder(subargs[1]);
        if (borderData == null) {
            HSClans.instance.getMessenger().message("commands.regen.no-border-set", sender, world.getName());
            return;
        }
        // If border is square
        if (borderData.getShape() == null || borderData.getShape()) {
            if (regenTask == null || !regenTask.isActive()) {
                double centerX = borderData.getX();
                double centerZ = borderData.getZ();
                int radiusX = borderData.getRadiusX();
                int radiusZ = borderData.getRadiusZ();

                double xStart = centerX - radiusX;
                double xEnd = centerX + radiusX;

                double zStart = centerZ - radiusX;
                double zEnd = centerZ + radiusZ;

                Location startLocation = new Location(world, xStart, 1, zStart);
                Location endLocation = new Location(world, xEnd, 1, zEnd);

                int chunkStartX = startLocation.getChunk().getX();
                int chunkStartZ = startLocation.getChunk().getZ();
                int chunkEndX = endLocation.getChunk().getX();
                int chunkEndZ = endLocation.getChunk().getZ();


                regenTask = new RegenTask(world, chunkStartX, chunkStartZ, chunkEndX, chunkEndZ);
                int taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(HSClans.instance, regenTask, 5, 1);
                regenTask.setTaskId(taskId);

                HSClans.instance.getMessenger().message("commands.regen.success", sender, world.getName());
            } else {
                HSClans.instance.getMessenger().message("commands.regen.already-running", sender);
            }
        } else {
            HSClans.instance.getMessenger().message("commands.regen.wrong-border-shape", sender, world.getName());
        }


    }

    private class RegenTask implements Runnable {
        private final World world;

        private int currentChunkX;
        private int currentChunkZ;

        private final int chunkEndX;
        private final int chunkEndZ;

        private final int startChunkZ;

        private int taskId;

        private int chunks;
        private int chunksGen;

        private RegenTask(World world, int startChunkX, int startChunkZ, int chunkEndX, int chunkEndZ) {
            this.world = world;
            this.chunkEndX = chunkEndX;
            this.chunkEndZ = chunkEndZ;
            this.startChunkZ = startChunkZ;
            currentChunkX = startChunkX;
            currentChunkZ = startChunkZ;
        }

        private void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            ClanManager clanManager = HSClans.instance.getClanManager();

            while (currentChunkX <= chunkEndX) {
                while (currentChunkZ <= chunkEndZ) {
                    chunks++;
                    if (clanManager.getClaim(currentChunkX, currentChunkZ, world) == null
                            && !WorldGuardIntegration.checkForRegionsInChunk(world.getChunkAt(currentChunkX, currentChunkZ))) {
                        if (world.regenerateChunk(currentChunkX, currentChunkZ)) {
                            // Doesn't make any difference. It doesn't work! TODO
//                            for (BlockPopulator blockPopulator : world.getPopulators()) {
//                                blockPopulator.populate(world, new Random(), world.getChunkAt(currentChunkX, currentChunkZ));
//                            }
                            chunksGen++;
                        }
                    }
                    if (System.currentTimeMillis() - startTime > 25) {
                        return;
                    }
                    currentChunkZ++;
                }
                currentChunkZ = startChunkZ;
                currentChunkX++;
            }
            HSClans.instance.getDebug().info("RegenTask. " + chunks + " chunks checked. " + chunksGen + " chunks regenerated.");
            Bukkit.getScheduler().cancelTask(taskId);
        }

        private boolean isActive() {
            return Bukkit.getScheduler().isCurrentlyRunning(taskId) || Bukkit.getScheduler().isQueued(taskId);
        }
    }
}