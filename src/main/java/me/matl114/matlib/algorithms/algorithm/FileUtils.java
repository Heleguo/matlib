package me.matl114.matlib.algorithms.algorithm;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;

public class FileUtils {
    public static File getOrCreateFile(String path) throws IOException {
        File file = new File(path);
        return getOrCreateFile(file);
    }
    public static File getOrCreateFile(File file) throws IOException{
        if(!file.getParentFile().exists()){
            Files.createDirectories(file.getParentFile().toPath());
        }
        if(!file.exists()){
            if(file.createNewFile()){
                return file;
            }else {
                throw new IOException(file.toPath().toString() + " create failed");
            }
        }else{
            return file;
        }
    }
    public static void ensureParentDir(File file) throws IOException {
        if(!file.getParentFile().exists()){
            Files.createDirectories(file.getParentFile().toPath());
        }
    }
    public static void copyFile(File from ,String to) throws IOException {
        if(from.exists()){
            File toFile = new File(to);
            ensureParentDir(toFile);
            Files.copy(from.toPath(),toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }else {
            throw new IOException(from + " does not exist");
        }
    }
    public static void copyFile(String resource, String to) throws IOException {
        File toFile = new File(to);
        ensureParentDir(toFile);

        Files.copy(FileUtils.class.getResourceAsStream("/"+resource),toFile.toPath(),StandardCopyOption.REPLACE_EXISTING);

    }
//    public static void copyFolderRecursively(File fromPath, String toPath) throws IOException {
//
//    }
    public static void copyFolderRecursively(String from,String toPath) throws IOException {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        URI uri=null;
        try {
            uri = classLoader.getResource(from).toURI();
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        } catch (NullPointerException e){
            throw new IOException(e.getMessage());
        }

        if(uri == null){
            throw new IOException("something is wrong directory or files missing");
        }

        /** jar case */
        URL jar = FileUtils.class.getProtectionDomain().getCodeSource().getLocation();
        //jar.toString() begins with file:
        //i want to trim it out...
        Path jarFile = Paths.get(URLDecoder.decode(jar.toString(), StandardCharsets.UTF_8) .substring("file:".length()));
        FileSystem fs = FileSystems.newFileSystem(jarFile, Map.of());
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(from));
        Path to=new File(toPath).toPath();
        for(Path p: directoryStream){
            InputStream is = FileUtils.class.getResourceAsStream("/"+p.toString()) ;
            Path target=to.resolve(p.toString());

            if(!Files.exists(target)){
                if(!Files.exists(target.getParent())){
                    Files.createDirectories(target.getParent());
                }
                Files.copy(is,target);
            }
        }
    }
    public static boolean deleteDirectory(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Recursive call to delete files and subfolders
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                }
            }
        }

        // Delete the folder itself
        return folder.delete();
    }

    public static InputStream readResource(String resource){
        return FileUtils.class.getResourceAsStream("/" + resource);
    }


    public static InputStream readFile(String path) {
        File file = new File(path);
        return readFile(file);
    }

    public static InputStream readFile(File file) {
        if(!isAFile(file)){
            throw new RuntimeException("File does not exists: "+ file);
        }
        try{
            return new FileInputStream(file);
        }catch (FileNotFoundException fil){
            throw new RuntimeException(fil);
        }
    }


    public static String readResourceString(String resource)  {
        try (var inputStream = readResource(resource)){
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    //BufferedReader
    //InputStreamBuffer

    public static boolean isAFile(File file){
        return file.exists() && file.isFile();
    }

    public static boolean isAFolder(File file){
        return file.exists() && file.isDirectory();
    }

    public static JsonElement readResourceJson(String resource){
        try(InputStream is = readResource(resource)){
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.parseReader(reader);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public static String readFileString(String path){
        return readFileString(new File(path));
    }

    public static String readFileString(File str){
        try(var inputStream = readFile(str)){
            return new String( inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public static JsonElement readFileJson(String str){
        return readFileJson(new File(str));
    }
    public static JsonElement readFileJson(File str){
        try(InputStream is = readFile(str)){
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.parseReader(reader);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

//    private static String readLines(){
//
//    }

}
