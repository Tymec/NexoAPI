package eu.nexwell.android.nexovision.model;

import android.support.v4.view.MotionEventCompat;
import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Light extends Switch {
    public static Integer SW_STATE_OFF = Integer.valueOf(0);
    public static Integer SW_STATE_ON = Integer.valueOf(1);
    private static String _defaultCategory = NVModel.CATEGORY_LIGHT;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Light);
    private Integer SW_ACTION_OFF = Integer.valueOf(0);
    private Integer SW_ACTION_ON = Integer.valueOf(1);

    static {
        _states_LIST.add(SW_STATE_ON);
        _states_LIST.add(SW_STATE_OFF);
        _states_MAP.put(SW_STATE_ON, Integer.valueOf(R.string.Resource_Light_StateName1));
        _states_MAP.put(SW_STATE_OFF, Integer.valueOf(R.string.Resource_Light_StateName2));
    }

    public Light() {
        setType(NVModel.EL_TYPE_LIGHT);
        this._iconsToStatesMap.put(SW_STATE_ON, Integer.valueOf(0));
        this._iconsToStatesMap.put(SW_STATE_OFF, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(SW_STATE_ON, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE_OFF, Integer.valueOf(1));
        saveState(SW_STATE_OFF);
        saveState(SW_STATE_OFF);
        setIconForState(SW_STATE_ON, "ic_light_on");
        setIconForState(SW_STATE_OFF, "ic_light_off");
        setBackgroundForState(SW_STATE_ON, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_yellow));
        setBackgroundForState(SW_STATE_OFF, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
    }

    public String on() {
        return super.getActionCommand(Integer.valueOf(this.SW_ACTION_ON.intValue() + MotionEventCompat.ACTION_POINTER_INDEX_MASK).toString());
    }

    public String off() {
        return super.getActionCommand(this.SW_ACTION_OFF.toString());
    }

    public boolean parseResp(String resp) {
        if (resp.length() <= 10 || !resp.matches("~[0-9]*:\\Q" + getResource() + "\\E .*")) {
            return false;
        }
        String strval = resp.substring(getResource().length() + 11);
        if (!strval.matches("[0-9]*")) {
            return false;
        }
        saveState(Integer.valueOf(Integer.parseInt(strval) & 255));
        return true;
    }

    public Integer getSimpleState(Integer s) {
        return Integer.valueOf(getState(s).intValue() & 255);
    }

    public String switchState() {
        if (getSimpleState(Integer.valueOf(0)) == SW_STATE_ON) {
            return off();
        }
        return on();
    }

    public static ArrayList<Integer> getStatesList() {
        return _states_LIST;
    }

    public static LinkedHashMap<Integer, Integer> getStatesMap() {
        return _states_MAP;
    }

    public boolean isOn() {
        if (getSimpleState(Integer.valueOf(0)) == SW_STATE_ON) {
            return true;
        }
        return false;
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
    }

    public String restoreState(Integer s) {
        if ((s.intValue() & 255) == SW_STATE_OFF.intValue()) {
            return off();
        }
        return on();
    }

    public String toXML(String spec_attrs) {
        StringBuffer sa = new StringBuffer();
        sa.append(" icon_on=\"" + getBackgroundByState(SW_STATE_ON) + "\"");
        sa.append(" icon_off=\"" + getBackgroundByState(SW_STATE_OFF) + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
