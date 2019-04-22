package eu.nexwell.android.nexovision.model;

import android.graphics.drawable.Drawable;
import java.util.LinkedHashMap;

public class Element implements IElement {
    protected LinkedHashMap<Integer, Drawable> _backgrounds;
    protected LinkedHashMap<Integer, String> _icons;
    private Integer _id = Integer.valueOf(-1);
    private String _name;
    private boolean _selected;
    private String _type;

    public Element() {
        setId(NVModel.findFirstFreeElementId());
        this._icons = new LinkedHashMap();
        this._backgrounds = new LinkedHashMap();
        this._selected = false;
    }

    public void setId(int id) {
        this._id = Integer.valueOf(id);
    }

    public Integer getId() {
        return this._id;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public void setType(String type) {
        this._type = type;
    }

    public String getType() {
        return this._type;
    }

    public void setIcon(String img) {
        this._icons.clear();
        this._icons.put(Integer.valueOf(0), img);
    }

    public String getIcon() {
        return (String) this._icons.get(Integer.valueOf(0));
    }

    public void setIcons(LinkedHashMap<Integer, String> icons) {
        this._icons = (LinkedHashMap) icons.clone();
    }

    public LinkedHashMap<Integer, String> getIcons() {
        return this._icons;
    }

    public void setBackground(Drawable d) {
        this._backgrounds.clear();
        this._backgrounds.put(Integer.valueOf(0), d);
    }

    public Drawable getBackground() {
        return (Drawable) this._backgrounds.get(Integer.valueOf(0));
    }

    public void setBackgrounds(LinkedHashMap<Integer, Drawable> images) {
        this._backgrounds = (LinkedHashMap) images.clone();
    }

    public LinkedHashMap<Integer, Drawable> getBackgrounds() {
        return this._backgrounds;
    }

    public String toXML(String spec_attrs) {
        StringBuffer sb = new StringBuffer();
        sb.append("\t<element id=\"" + getId() + "\" type=\"" + getType() + "\" name=\"" + getName() + "\"" + spec_attrs + "/>\n");
        return sb.toString();
    }

    public void setSelected(boolean selected) {
        this._selected = selected;
    }

    public boolean isSelected() {
        return this._selected;
    }
}
