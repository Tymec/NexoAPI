package eu.nexwell.android.nexovision.model;

import android.graphics.drawable.Drawable;
import java.util.LinkedHashMap;

public interface ISwitch extends IElement {
    void clearBackgrounds();

    void clearIcons();

    boolean doNeedUpdate();

    String getActionCommand(String str);

    String getActionOldCommand(String str);

    Drawable getBackgroundByState(Integer num);

    LinkedHashMap<Integer, Drawable> getBackgrounds();

    Object getControl();

    String getIconByState(Integer num);

    String getInfo();

    String getResource();

    Integer getSimpleState(Integer num);

    Integer getState(Integer num);

    String getUpdateCommand();

    boolean isUpdated();

    boolean parseResp(String str);

    String restoreState(Integer num);

    void saveState(Integer num);

    void setBackgroundForState(Integer num, Drawable drawable);

    void setControl(Object obj);

    void setIconForState(Integer num, String str);

    void setInfo(String str);

    void setNeedUpdate(boolean z);

    void setResource(String str);

    void setUpdated(boolean z);

    String switchState();

    String toXML(String str);
}
