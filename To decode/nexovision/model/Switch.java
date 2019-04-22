package eu.nexwell.android.nexovision.model;

import android.graphics.drawable.Drawable;
import android.util.Log;
import java.util.LinkedHashMap;

public class Switch extends Element implements ISwitch {
    protected LinkedHashMap<Integer, Integer> _backgroundsToStatesMap = new LinkedHashMap();
    private Object _ctl;
    protected LinkedHashMap<Integer, Integer> _iconsToStatesMap = new LinkedHashMap();
    private String _info;
    private String _resource;
    private Integer[] _states = new Integer[]{null, null};
    private boolean isUpdated = false;
    private boolean needUpdate = false;

    public void setInfo(String info) {
        this._info = info;
    }

    public String getInfo() {
        return this._info;
    }

    public void setControl(Object ctl) {
        this._ctl = ctl;
    }

    public Object getControl() {
        return this._ctl;
    }

    public void setResource(String src) {
        this._resource = src;
    }

    public String getResource() {
        return this._resource;
    }

    public String getActionCommand(String action) {
        return "system C '" + getResource() + "' " + action + "\u0000";
    }

    public String getActionOldCommand(String action) {
        return "system command " + action + " '" + getResource() + "'\u0000";
    }

    public String getUpdateCommand() {
        return "system C '" + getResource() + "' ?\u0000";
    }

    public boolean parseResp(String resp) {
        return false;
    }

    public void setNeedUpdate(boolean need) {
        this.needUpdate = need;
    }

    public boolean doNeedUpdate() {
        return this.needUpdate;
    }

    public void setUpdated(boolean updated) {
        this.isUpdated = updated;
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }

    public void clearIcons() {
        this._icons.clear();
    }

    public void setIconForState(Integer s, String img) {
        Integer i = (Integer) this._iconsToStatesMap.get(s);
        if (i != null) {
            this._icons.put(i, img);
        }
    }

    public String getIconByState(Integer s) {
        Integer i = (Integer) this._iconsToStatesMap.get(s);
        if (i != null && this._icons.size() > 0 && i.intValue() < this._icons.size()) {
            return (String) this._icons.get(i);
        }
        return null;
    }

    public String getIcon() {
        return getIconByState(getSimpleState(Integer.valueOf(0)));
    }

    public void clearBackgrounds() {
        this._backgrounds.clear();
    }

    public void setBackgroundForState(Integer s, Drawable d) {
        Integer i = (Integer) this._backgroundsToStatesMap.get(s);
        if (i != null) {
            this._backgrounds.put(i, d);
        }
    }

    public Drawable getBackgroundByState(Integer s) {
        Integer i = (Integer) this._backgroundsToStatesMap.get(s);
        if (i != null && this._backgrounds.size() > 0 && i.intValue() < this._backgrounds.size()) {
            return (Drawable) this._backgrounds.get(i);
        }
        return null;
    }

    public Drawable getBackground() {
        return getBackgroundByState(getSimpleState(Integer.valueOf(0)));
    }

    public void saveState(Integer state) {
        this._states[1] = this._states[0];
        this._states[0] = state;
    }

    public Integer getState(Integer s) {
        return this._states[s.intValue()];
    }

    public Integer getSimpleState(Integer s) {
        return getState(s);
    }

    public String switchState() {
        return null;
    }

    public boolean isOn() {
        return false;
    }

    public String restoreState(Integer s) {
        Log.d("Switch", getName() + ": RESTORE STATE(" + s + ")");
        return null;
    }

    public String toXML(String spec_attrs) {
        StringBuffer sa = new StringBuffer();
        sa.append(" res=\"" + getResource() + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
