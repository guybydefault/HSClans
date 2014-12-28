package ru.lexmint.utils;

import org.bukkit.Material;
import ru.lexmint.HSClans;
import ru.lexmint.domain.Clan;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Class with some useful features.
 */
public class Utils {
    /**
     * Converts Set of given CPLayers' names to single String where elements of the set are separated
     * by comma.
     *
     * @param set    Set of CPLayers whose names are needed to be converted.
     * @param spaces if it's true, after, there will be spaces.
     * @return Single string with comma separated elements of the set.
     */
    public static String convertToString(Set<String> set, boolean spaces) {
        StringBuilder result = new StringBuilder();
        Iterator<String> it = set.iterator();

        while (it.hasNext()) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(',');
                if (spaces) {
                    result.append(' ');
                }
            }
        }

        return result.toString();
    }

    /**
     * @param clanSet Set of clans for which you need to get set of names.
     * @return Set of clan names.
     */
    public static Set<String> getClanNames(Set<Clan> clanSet) {
        Set<String> clanNames = new HashSet<>();
        for (Clan clan : clanSet) {
            clanNames.add(clan.getName());
        }
        return clanNames;
    }

    /**
     * @param path Path to material list in config.
     * @return Set of materials from material list in config.
     */
    public static Set<Material> getMaterialsSet(String path) {
        List<String> stringMaterials = HSClans.instance.getSettings().getStringList(path);
        Set<Material> materials = new HashSet<>();
        for (String material : stringMaterials) {
            if (material != null) {
                materials.add(Material.getMaterial(material));
            } else {
                HSClans.instance.getDebug().error("Material " + material + " is null. Path in config: " + path);
            }
        }
        return materials;
    }
}
