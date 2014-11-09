package ru.lexmint.utils;

import org.bukkit.Bukkit;
import ru.lexmint.HSClans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class which makes debugging easier. It creates separate file (log) where collects
 * all errors and info from plugin.
 */
public class Debug {
    /**
     * Debug log's file on disk.
     */
    final File log;

    /**
     * In constructor of Debug it creates new debugging log file with given logName and then
     * it's ready for work.
     * @param logName Name of debug log file of disk. You can choose any. Adding file
     *                extension (.log) is not needed.
     */
    public Debug(String logName) {
        log = new File(HSClans.instance.getDataFolder() + File.separator + logName + ".log");
        if (!log.exists()) {
            try {
                log.getParentFile().mkdirs();
                log.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("Error while debugging! Can't create log file for HSJail.");
            }
        }
    }

    /**
     * Patter which is used to format dates while writing to log file.
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM");

    /**
     * Writes error message to the log.
     * @param message Error message.
     */
    public void error(String message) {
        write(message, "[ERROR]");
    }

    /**
     * Writes info message to the log.
     * @param message Info message.
     */
    public void info(String message) {
        write(message, "[INFO]");
    }

    /**
     * Method used to cope with output.
     * @param message Message which we want to write.
     * @param prefix Prefix of the message (error/info/etc).
     */
    private void write(String message, String prefix) {
        try (
                FileWriter fileWriter = new FileWriter(log, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            bufferedWriter.append("[").append(dateFormat.format(new Date(System.currentTimeMillis()))).append("] ");
            bufferedWriter.append(prefix).append(" ");
            bufferedWriter.append(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException exc) {
            Bukkit.getLogger().severe("Error while debugging! Can't write to file. " + exc.getMessage());
        }
    }
}
