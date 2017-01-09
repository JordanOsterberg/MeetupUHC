package tech.shadowsystems.meetupuhc.utilities;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.Random;

/**
 * Copyright (c) Shadow Technical Systems, LLC 2017.
 * Any attempt to use these program(s) without permission may result in a penalty of up to $1,000 USD.
 * Current Package: us.paradoxmcs.us.muhc.utilities
 */
public class MapResetUtil {

    /*
    This class is most likely flawed to some extent. The methods created here only allow for a max of 3 maps, and if you don't have 3 maps ready will crash the plugin.

    Possible solution:
    - Allow for configuration of maps
    - Allow for random generation of maps every game
     */

    private static MapResetUtil mapResetUtil;
    public static MapResetUtil getMapResetUtil() {
        if (mapResetUtil==null){
            mapResetUtil = new MapResetUtil();
        }
        return mapResetUtil;
    }

    public void resetMap(String map){
        // reset
        Random random = new Random();
        int id = random.nextInt(3); // max = 2
        File srcFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath(), map + id);
        File destFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath(), map);

        try {
            delete(destFile);
            copyFolder(srcFile, destFile);
        } catch (IOException e){
            System.out.print("Error while copying map file:");
            e.printStackTrace();
        }

    }

    public void delete(File delete){
        if (delete.isDirectory()){
            String[] files = delete.list();

            for (String file : files){
                File toDelete = new File(file);

                delete(toDelete);
            }
        } else {
            System.out.print("Deleted file " + delete.getName());
            delete.delete();
        }
    }

    public void copyFolder(File src, File dest)
            throws IOException {

        if(src.isDirectory()){

            //if directory not exists, create it
            if(!dest.exists()){
                dest.mkdir();
                System.out.println("Directory copied from "
                        + src + "  to " + dest);
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile,destFile);
            }

        }else{
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();

        }
    }

}
