package eu.nexwell.android.nexovision.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class RGBWGroup extends RGBW {
    public static Integer SW_ACTION_OFF = Integer.valueOf(0);
    public static Integer SW_ACTION_ON = Integer.valueOf(1);
    public static Integer SW_ACTION_SET = Integer.valueOf(7);
    public static Integer SW_STATE_OFF = Integer.valueOf(0);
    public static Integer SW_STATE_ON = Integer.valueOf(1);
    public static Integer SW_WHITE = Integer.valueOf(0);
    private static String _defaultCategory = NVModel.CATEGORY_LIGHT;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_RGBW_Group);

    static {
        _states_LIST.add(SW_STATE_ON);
        _states_LIST.add(SW_STATE_OFF);
        _states_MAP.put(SW_STATE_ON, Integer.valueOf(R.string.Resource_RGBW_StateName1));
        _states_MAP.put(SW_STATE_OFF, Integer.valueOf(R.string.Resource_RGBW_StateName2));
    }

    public RGBWGroup() {
        setType(NVModel.EL_TYPE_RGBW_GROUP);
    }

    public static ArrayList<Integer> getStatesList() {
        return _states_LIST;
    }

    public static LinkedHashMap<Integer, Integer> getStatesMap() {
        return _states_MAP;
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
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
