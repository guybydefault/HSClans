package ru.lexmint.cmd;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;
import ru.lexmint.utils.Integration;

/**
 * Created by lexmint on 22.12.14.
 */
public class RegenCommand extends BaseCommand {
    /**
     * Main constructor for creating a command.
     *
     * @param senderIsPlayer   Is sender required to be a player or not.
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param usage            String which contains information how to use this command.
     */
    public RegenCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    private RegenTask regenTask = null;

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        WorldBorder worldBorder = Integration.getWorldBorder();
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
        if (borderData.getShape() == null || borderData.getShape() == true) {
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
        private World world;

        private int currentChunkX;
        private int currentChunkZ;

        private int chunkEndX;
        private int chunkEndZ;

        private int startChunkZ;

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
                    if (clanManager.getClaim(currentChunkX, currentChunkZ) == null
                            && !Integration.checkForRegionsInChunk(world.getChunkAt(currentChunkX, currentChunkZ))) {
                        if (world.regenerateChunk(currentChunkX, currentChunkZ)) {
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
