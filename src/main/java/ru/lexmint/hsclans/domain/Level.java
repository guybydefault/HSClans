package ru.lexmint.hsclans.domain;

import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hscore.utils.Config;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

/**
 * Level describes clan/player's experience and skill on server. Levels are loaded from config.
 * You can easily get clan/player's level using static method which only needs player's rating and
 * level type (clan/player) defined in arguments.
 */
public class Level {
    private static final Level[] PLAYER_LEVELS = loadLevels("player.levels");
    private static final Level[] CLAN_LEVELS = loadLevels("clan.levels");

    private double hsRate;
    private String name;
    private int level;

    private Level(double hsRate, String name) {
        this.hsRate = hsRate;
        this.name = name;
    }

    public static Level getLevelByRate(LevelType levelType, double hsRate) {
        Level result = null;
        Level[] levelsArray;
        if (levelType == LevelType.CLAN) {
            levelsArray = CLAN_LEVELS;
        } else {
            levelsArray = PLAYER_LEVELS;
        }
        for (Level level : levelsArray) {
            if (result == null || (level.getHsRate() > result.getHsRate() && level.getHsRate() <= hsRate)) {
                result = level;
            }
        }
        return result;
    }

    public static Level[] loadLevels(String path) {
        Config settings = HSClans.instance.getSettings();
        Set<String> levelsKeys = settings.getConfigurationSection(path).getKeys(false);
        Iterator<String> it = levelsKeys.iterator();
        Level[] levels = new Level[levelsKeys.size()];
        for (int i = 0; i < levelsKeys.size(); i++) {
            String level = it.next();
            levels[i] = new Level(settings.getDouble(path + "." + level + ".rate"),
                    settings.getString(path + "." + level + ".name"));
        }

        Arrays.sort(levels, new Comparator<Level>() {
            @Override
            public int compare(Level o1, Level o2) {
                if (o1.getHsRate() > o2.getHsRate()) {
                    return 1;
                } else if (o1.getHsRate() < o2.getHsRate()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (int i = 0; i < levels.length; i++) {
            levels[i].setLevel(i);
        }
        return levels;
    }

    public double getHsRate() {
        return hsRate;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    private void setLevel(int level) {
        this.level = level;
    }

    public enum LevelType {
        CLAN,
        PLAYER
    }
}
