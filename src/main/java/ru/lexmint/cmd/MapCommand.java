package ru.lexmint.cmd;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Shows map with faction claims on it.
 */
public class MapCommand extends HSCCommand {
    /**
     * Characters which are used as symbols of the map's legend.
     */
    private char[] legendCharArray = HSClans.instance.getMessenger().getMessage("commands.map.legend-chars").toCharArray();

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
    public MapCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        for (String row : getMap(HSClans.instance.getClanManager().getPlayer(sender.getName(), true), ((Player) sender).getLocation())) {
            sender.sendMessage(row);
        }
    }

    public List<String> getMap(CPLayer cpLayer, Location location) {
        ClanManager clanManager = HSClans.instance.getClanManager();

        int centerX = location.getChunk().getX();
        int centerZ = location.getChunk().getZ();


        List<String> result = new LinkedList<>();

        /** Header */
        Claim centerClaim = clanManager.getClaim(centerX, centerZ, location.getWorld());
        String centerEntry;
        if (centerClaim == null) {
            centerEntry = HSClans.instance.getMessenger().format("commands.map.full-claim.none", String.valueOf(centerX), String.valueOf(centerZ));
        } else if (centerClaim.getClan() == cpLayer.getClan()) {
            centerEntry = HSClans.instance.getMessenger().format("commands.map.full-claim.own", cpLayer.getClan().getName(), String.valueOf(centerX), String.valueOf(centerZ));
        } else if (centerClaim.getClan().isAlliedWith(cpLayer.getClan())) {
            centerEntry = HSClans.instance.getMessenger().format("commands.map.full-claim.ally", centerClaim.getClan().getName(), String.valueOf(centerX), String.valueOf(centerZ));
        } else {
            centerEntry = HSClans.instance.getMessenger().format("commands.map.full-claim.enemy", centerClaim.getClan().getName(), String.valueOf(centerX), String.valueOf(centerZ));
        }
        String header = HSClans.instance.getMessenger().format("commands.map.header", centerEntry);
        result.add(header);
        /** Header */

        /* Size */
        int height = HSClans.instance.getSettings().getInt("map.height");
        int width = HSClans.instance.getSettings().getInt("map.width");
        int halfHeight = height / 2;
        int halfWidth = width / 2;

        int startX = centerX - halfWidth;
        int startZ = centerZ - halfHeight;
        int endX = centerX + (width % 2 == 0 ? halfWidth - 1 : halfWidth);
        int endZ = centerZ + (height % 2 == 0 ? halfHeight - 1 : halfHeight);
        /* Size */

        Map<Clan, Character> legend = new HashMap<>();
        int chIndex = 0;

        for (int z = startZ; z <= endZ; z++) {
            StringBuilder row = new StringBuilder();
            for (int x = startX; x <= endX; x++) {
                Claim claim = clanManager.getClaim(x, z, location.getWorld());
                String entry;
                if (z == centerZ && x == centerX) {
                    entry = HSClans.instance.getMessenger().format("commands.map.claim.current");
                } else if (claim == null) {
                    entry = HSClans.instance.getMessenger().format("commands.map.claim.none");
                } else {
                    Character entryChar = legend.get(claim.getClan());
                    if (entryChar == null) {
                        entryChar = legendCharArray[chIndex++];
                        legend.put(claim.getClan(), entryChar);
                    }
                    if (claim.getClan() == cpLayer.getClan()) {
                        entry = HSClans.instance.getMessenger().format("commands.map.claim.own", entryChar.toString());
                    } else if (cpLayer.hasClan() && cpLayer.getClan().isAlliedWith(claim.getClan())) {
                        entry = HSClans.instance.getMessenger().format("commands.map.claim.ally", entryChar.toString());
                    } else {
                        entry = HSClans.instance.getMessenger().format("commands.map.claim.enemy", entryChar.toString());
                    }

                }
                row.append(entry);
            }
            result.add(row.toString());
        }

        /* Legend */
        StringBuilder legendRow = new StringBuilder();
        for (Clan clan : legend.keySet()) {
            char ch = legend.get(clan);
            legendRow
                    .append(" ")
                    .append(ch)
                    .append(": ")
                    .append(clan.getName());

        }
        result.add(HSClans.instance.getMessenger().format("commands.map.legend", legendRow.toString()));
        /* Legend */

        /* Compass */
        result.add(HSClans.instance.getMessenger().format("commands.map.direction", Compass.getCompassPointForDirection(location.getYaw()).getName()));
        /* Compass */

        return result;
    }


    /**
     * Compass which can be used with map.
     */
    public static class Compass {
        public static Point getCompassPointForDirection(double yaw) {
            double degrees = (yaw - 180) % 360;
            if (degrees < 0) {
                degrees += 360;
            }

            if (0 <= degrees && degrees < 22.5) {
                return Point.NORTH;
            } else if (22.5 <= degrees && degrees < 67.5) {
                return Point.NORTH_EAST;
            } else if (67.5 <= degrees && degrees < 112.5) {
                return Point.EAST;
            } else if (112.5 <= degrees && degrees < 157.5) {
                return Point.SOUTH_EAST;
            } else if (157.5 <= degrees && degrees < 202.5) {
                return Point.SOUTH;
            } else if (202.5 <= degrees && degrees < 247.5) {
                return Point.SOUTH_WEST;
            } else if (247.5 <= degrees && degrees < 292.5) {
                return Point.WEST;
            } else if (292.5 <= degrees && degrees < 337.5) {
                return Point.NORTH_WEST;
            } else if (337.5 <= degrees && degrees < 360.0) {
                return Point.NORTH;
            } else {
                return null;
            }
        }

        public enum Point {
            NORTH(HSClans.instance.getLangConfig().getString("commands.map.way.north")),
            NORTH_EAST(HSClans.instance.getLangConfig().getString("commands.map.way.north-east")),
            EAST(HSClans.instance.getLangConfig().getString("commands.map.way.east")),
            SOUTH_EAST(HSClans.instance.getLangConfig().getString("commands.map.way.south-east")),
            SOUTH(HSClans.instance.getLangConfig().getString("commands.map.way.south")),
            SOUTH_WEST(HSClans.instance.getLangConfig().getString("commands.map.way.south-west")),
            WEST(HSClans.instance.getLangConfig().getString("commands.map.way.west")),
            NORTH_WEST(HSClans.instance.getLangConfig().getString("commands.map.way.north-west"));

            private final String name;

            private Point(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }
        }
    }
}
