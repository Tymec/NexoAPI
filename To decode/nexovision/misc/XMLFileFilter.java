package eu.nexwell.android.nexovision.misc;

import java.io.File;
import java.io.FileFilter;

/* compiled from: XMLProject */
class XMLFileFilter implements FileFilter {
    XMLFileFilter() {
    }

    public boolean accept(File pathname) {
        if (pathname.getName().endsWith(".xml")) {
            return true;
        }
        return false;
    }
}
