package eu.nexwell.android.nexovision.model;

import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class AnalogSensor extends Switch {
    public static Integer SW_STATE = Integer.valueOf(0);
    private static String _defaultCategory = NVModel.CATEGORY_SENSORS;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_AnalogSensor);

    static {
        _states_LIST.add(SW_STATE);
        _states_MAP.put(SW_STATE, Integer.valueOf(R.string.Resource_AnalogSensor_StateName));
    }

    public AnalogSensor() {
        setType(NVModel.EL_TYPE_ANALOGSENSOR);
        this._iconsToStatesMap.put(SW_STATE, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE, Integer.valueOf(0));
        saveState(SW_STATE);
        setIconForState(SW_STATE, "ic_sensor_affected");
        setBackgroundForState(SW_STATE, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_blue));
    }

    public boolean parseResp(String resp) {
        if (resp.length() <= 10 || !resp.matches("~[0-9]*:\\Q" + getResource() + "\\E .*")) {
            return false;
        }
        String strval = resp.substring(getResource().length() + 11);
        if (!strval.matches("[0-9]*")) {
            return false;
        }
        saveState(Integer.valueOf(Integer.parseInt(strval)));
        setInfo(String.format("%d", new Object[]{STATE}));
        return true;
    }

    public Integer getValue() {
        return Integer.valueOf(Math.round((float) getState(Integer.valueOf(0)).intValue()));
    }

    public Integer getSimpleState(Integer s) {
        return SW_STATE;
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
        sa.append(" icon=\"" + getBackgroundByState(SW_STATE) + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
