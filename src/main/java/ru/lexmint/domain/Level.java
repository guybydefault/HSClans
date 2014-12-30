package ru.lexmint.domain;

import ru.lexmint.HSClans;
import ru.lexmint.utils.Config;

import java.util.Iterator;
import java.util.Set;

/**
 * Level describes clan/player's experience and skill on server. Levels are loaded from config.
 * You can easily get clan/player's level using static method which only needs player's rating and
 * level type (clan/player) defined in arguments.
 */
public class Level {
    private double hsRate;
    private String name;

    private Level(double hsRate, String name) {
        this.hsRate = hsRate;
        this.name = name;
    }

    public double getHsRate() {
        return hsRate;
    }

    public String getName() {
        return name;
    }

    private static final Level[] PLAYER_LEVELS = loadLevels("player.levels");
    private static final Level[] CLAN_LEVELS = loadLevels("clan.levels");

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
        return levels;
    }

    public enum LevelType {
        CLAN,
        PLAYER;
    }
}
