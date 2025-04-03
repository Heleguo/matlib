package me.matl114.matlib.utils;

import com.google.common.base.Charsets;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import me.matl114.matlib.algorithms.algorithm.FileUtils;
import me.matl114.matlib.core.AutoInit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;

@AutoInit(level = "Util")
public class ConfigLoader {
    public static void init(Plugin plugin) {
        ConfigLoader.plugin=plugin;

    }
    public static Plugin plugin;
    public static void copyFolder(String folder) {
        File folderFile=new File(plugin.getDataFolder(),folder);
        if(!folderFile.exists()) {
            try{
                FileUtils.copyFolderRecursively(folder,plugin.getDataFolder().toString());
                Debug.logger("生成默认文件夹%s中...".formatted(folder));
            }catch (Throwable e){
                Debug.logger(e,"拷贝默认配置文件夹时遇到错误!");
            }
        }else {
            Debug.logger("%s文件夹已经存在.跳过拷贝...".formatted(folder));
        }
    }
    public static void copyFile(File file, String name) {
        if (!file.exists()) {
            try {
                if(!file.toPath().getParent().toFile().exists()) {
                    Files.createDirectories(file.toPath().getParent());
                }
                Files.copy(plugin.getClass().getResourceAsStream("/"+ name + ".yml"),file.toPath());
            } catch (Throwable e) {

                Debug.logger("创建配置文件时找不到相关默认配置文件,即将生成空文件");
                try{
                    Files.createDirectories(file.toPath().getParent());
                    Files.createFile(file.toPath());
                }catch (IOException e1){
                    Debug.logger("创建空配置文件失败!");
                }
            }

        }
    }
    public static Config loadInternalConfig(String name){
        FileConfiguration config = new YamlConfiguration();
        try{
            config.load((Reader)( new InputStreamReader(plugin.getClass().getResourceAsStream("/"+ name + ".yml"), Charsets.UTF_8)));

        }catch (Throwable e){
            Debug.logger("failed to load internal config " + name + ".yml, Error: " + e.getMessage());
            return null;
        }
        return new Config(null,config);
    }
    public static Config loadExternalConfig(String name){
        FileConfiguration config = new YamlConfiguration();
        final File cfgFile = new File(plugin.getDataFolder(), "%s.yml".formatted(name));
        copyFile(cfgFile, name);
        return new Config(plugin, "%s.yml".formatted(name));
    }
}
