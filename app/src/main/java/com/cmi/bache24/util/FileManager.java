package com.cmi.bache24.util;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by omar on 4/3/16.
 */
public class FileManager {

    public static void writeLog(String report, String value) {

        try {
            //This will get the SD Card directory and create a folder named MyFiles in it.
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File (sdCard.getAbsolutePath() + "/Bache24Log");
            directory.mkdirs();

            //Now create the file in the above directory and write the contents into it
            File file = new File(directory, "reporte_" + report + "_log.txt");
            FileOutputStream fOut = null;

            StringBuilder outputText = new StringBuilder();
            outputText.append("------------ NUEVA ENTRADA ------------\n");
            outputText.append(value);

            fOut = new FileOutputStream(file);

            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(outputText.toString());
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
