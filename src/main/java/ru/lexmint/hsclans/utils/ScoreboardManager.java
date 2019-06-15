package ru.lexmint.hsclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

    public void onLoad() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HSClans.instance, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

                    CPLayer cPlayer = HSClans.instance.getClanManager().getPlayer(player);
                    registerSidebarObjective(board, cPlayer);
                    registerPowerIndicatorBelowName(board, cPlayer);

                    player.addScoreboardTag("ScoreBoardTag");
                    player.setScoreboard(board);
                    for (Player anotherPlayer : Bukkit.getOnlinePlayers()) {

                        CPLayer anotherCPlayer = HSClans.instance.getClanManager().getPlayer(anotherPlayer);

                        String teamPrefix = ChatColor.RED.toString();
                        if (anotherCPlayer.hasClan()) {
                            if (anotherCPlayer.isAlly(cPlayer)) {
                                teamPrefix = ChatColor.LIGHT_PURPLE + "[" + anotherCPlayer.getClan().getName() + "] ";
                            } else if (anotherCPlayer.isInTheSameClan(cPlayer)) {
                                teamPrefix = ChatColor.GREEN + "[" + anotherCPlayer.getClan().getName() + "] ";
                            } else {
                                teamPrefix += "[" + anotherCPlayer.getClan().getName() + "] ";
                            }
                        } else if (cPlayer.equals(anotherCPlayer)) {
                            teamPrefix = ChatColor.GREEN.toString();
                        }
                        getTeam(board, anotherCPlayer).setPrefix(teamPrefix);
                    }
                }

            }
        }, 20, 20 * 5);
    }

    private void registerSidebarObjective(Scoreboard scoreboard, CPLayer cpLayer) {
        if (cpLayer.hasClan()) {
            Objective sidebarObjective = scoreboard.registerNewObjective("Sidebar", "dummy");
            sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            sidebarObjective.setDisplayName(HSClans.instance.getMessenger().format("scoreboard.sidebar.title", cpLayer.getClan().getName()));
            sidebarObjective.getScore(HSClans.instance.getLangConfig().getString("scoreboard.sidebar.power"))
                    .setScore(cpLayer.getClan().getPowerRounded());
            sidebarObjective.getScore(HSClans.instance.getLangConfig().getString("scoreboard.sidebar.lands"))
                    .setScore(cpLayer.getClan().getClaimsNumber());
            sidebarObjective.getScore(HSClans.instance.getLangConfig().getString("scoreboard.sidebar.members-online"))
                    .setScore(cpLayer.getClan().getNumberOfMembersOnline());
        }
    }

    private void registerPowerIndicatorBelowName(Scoreboard scoreboard, CPLayer cpLayer) {
        final Objective belowNameObjective = scoreboard.registerNewObjective("playerPower", "dummy");
        belowNameObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        belowNameObjective.setDisplayName(HSClans.instance.getLangConfig().getString("scoreboard.below-name.power"));
        belowNameObjective.getScore("power").setScore(cpLayer.getPowerRounded());
    }


    private Team getTeam(Scoreboard scoreboard, CPLayer cpLayer) {
        String teamName = cpLayer.hasClan() ? cpLayer.getClan().getName() : "Player:" + cpLayer.getName();
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        team.addEntry(cpLayer.getName());
        return team;
    }

    public void update(Player player1) {
    }

}
