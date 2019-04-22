package eu.nexwell.android.nexovision.communication;

import eu.nexwell.android.nexovision.model.IElement;
import java.util.ArrayList;

public interface NexoTalkListener {
    void connectionProcessInfo(String str, String str2);

    void connectionStatus(boolean z);

    void onImport(int i, int i2);

    void onImportEnd(ArrayList<Integer> arrayList);

    void onPartitionAlarm(IElement iElement);

    void onStatusUpdate(IElement iElement, boolean z);
}
