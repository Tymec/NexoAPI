package eu.nexwell.android.nexovision.model;

import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Sensor extends Switch {
    public static Integer SW_STATE_AFFECTED = Integer.valueOf(102);
    public static Integer SW_STATE_NOTAFFECTED = Integer.valueOf(101);
    private static String _defaultCategory = NVModel.CATEGORY_SENSORS;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Sensor);

    static {
        _states_LIST.add(SW_STATE_AFFECTED);
        _states_LIST.add(SW_STATE_NOTAFFECTED);
        _states_MAP.put(SW_STATE_AFFECTED, Integer.valueOf(R.string.Resource_Sensor_StateName1));
        _states_MAP.put(SW_STATE_NOTAFFECTED, Integer.valueOf(R.string.Resource_Sensor_StateName2));
    }

    public Sensor() {
        setType(NVModel.EL_TYPE_SENSOR);
        this._iconsToStatesMap.put(SW_STATE_AFFECTED, Integer.valueOf(0));
        this._iconsToStatesMap.put(SW_STATE_NOTAFFECTED, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(SW_STATE_AFFECTED, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE_NOTAFFECTED, Integer.valueOf(1));
        saveState(SW_STATE_NOTAFFECTED);
        saveState(SW_STATE_NOTAFFECTED);
        setIconForState(SW_STATE_AFFECTED, "ic_sensor_affected");
        setIconForState(SW_STATE_NOTAFFECTED, "ic_sensor_notaffected");
        setBackgroundForState(SW_STATE_AFFECTED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_orange));
        setBackgroundForState(SW_STATE_NOTAFFECTED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
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
        sa.append(" icon_affected=\"" + getBackgroundByState(SW_STATE_AFFECTED) + "\"");
        sa.append(" icon_notaffected=\"" + getBackgroundByState(SW_STATE_NOTAFFECTED) + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
