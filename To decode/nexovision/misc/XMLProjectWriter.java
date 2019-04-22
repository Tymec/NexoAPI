package eu.nexwell.android.nexovision.misc;

import android.util.Log;
import eu.nexwell.android.nexovision.model.NVModel;
import java.io.File;
import java.io.FileOutputStream;

public class XMLProjectWriter {
    private static StringBuffer sb;

    public static void startDocument() {
        sb.append("<?xml version=\"1.0\"?>\n");
        sb.append("<Project>\n");
    }

    public static void endDocument() {
        sb.append("</Project>\n");
    }

    public static void performBody() {
        sb.append(NVModel.toXML());
    }

    public static boolean write(String file) {
        sb = new StringBuffer();
        File f = new File(file);
        if (f.exists()) {
            try {
                Log.d("XMLProjectWriter", "File:" + f.getAbsolutePath());
                FileOutputStream fStream = new FileOutputStream(file);
                startDocument();
                performBody();
                endDocument();
                Log.d("XMLProjectWriter", sb.toString());
                fStream.write(sb.toString().getBytes());
                fStream.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void createEmpty(String file) {
        sb = new StringBuffer();
        if (new File(file).exists()) {
            try {
                FileOutputStream fStream = new FileOutputStream(file);
                startDocument();
                endDocument();
                fStream.write(sb.toString().getBytes());
                fStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
