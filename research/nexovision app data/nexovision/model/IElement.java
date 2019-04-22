package eu.nexwell.android.nexovision.model;

import android.graphics.drawable.Drawable;
import java.util.LinkedHashMap;

public interface IElement {
    Drawable getBackground();

    LinkedHashMap<Integer, Drawable> getBackgrounds();

    String getIcon();

    LinkedHashMap<Integer, String> getIcons();

    Integer getId();

    String getName();

    String getType();

    boolean isSelected();

    void setBackground(Drawable drawable);

    void setBackgrounds(LinkedHashMap<Integer, Drawable> linkedHashMap);

    void setIcon(String str);

    void setIcons(LinkedHashMap<Integer, String> linkedHashMap);

    void setId(int i);

    void setName(String str);

    void setSelected(boolean z);

    void setType(String str);

    String toXML(String str);
}
