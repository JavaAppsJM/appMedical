package be.hvwebsites.medical.services;

import android.content.Context;

import java.net.ContentHandler;

public class FileBaseService {
    private String fileBaseDir;
    private static final String FILE_BASE_INTERNAL = "base_internal";
    private static final String FILE_BASE_EXTERNAL = "base_external";

    public FileBaseService(String deviceModel, Context inContext, String packageNm, String path) {
        // packageNm en path zijn overbodig geworden !!

        // Bepaal filepath
        if (deviceModel.equals("GT-I9100")){
            // Internal files
            this.fileBaseDir = inContext.getFilesDir().getPath();
        }else {
            // External files
            this.fileBaseDir = inContext.getExternalFilesDir(null).getAbsolutePath();
        }

        boolean debug = true;
/*
        //this.fileBaseDir = path + packName + "/files";
        //this.fileBaseDir = path;

        if (deviceModel.equals("GT-I9100")){
            setFileBaseDir(FILE_BASE_INTERNAL, path);
//            fileBase = FILE_BASE_INTERNAL;
//            this.fileBaseDir = "/data/user/0/" + packageNm + "/files";
        } else {
            setFileBaseDir(FILE_BASE_EXTERNAL, path);
//            fileBase = FILE_BASE_EXTERNAL;
//            this.fileBaseDir = "/storage/emulated/0/Android/data/" + packageNm + "/files";
        }
*/
    }

    public String getFileBaseDir() {
        return fileBaseDir;
    }

/*
    private void setFileBaseDir(String fileBase, String path) {
        //String baseIntPathPart1 = "/data/user/0/";
        //String baseExtPathPart1 = "/storage/emulated/0/Android/data/";
        if (fileBase.equals(FILE_BASE_INTERNAL)){
            this.fileBaseDir = path + packName + "/files";
        }else {
            this.fileBaseDir = path + packName + "/files";
        }
    }
*/
}
