package eu.nexwell.android.nexovision.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class ThermostatGroup extends Thermostat {
    public static Integer SW_STATE_NOT_ACTIVE = Integer.valueOf(2);
    public static Integer SW_STATE_OFF = Integer.valueOf(0);
    public static Integer SW_STATE_ON = Integer.valueOf(1);
    private static String _defaultCategory = NVModel.CATEGORY_TEMPERATURE;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Thermostat_Group);

    static {
        _states_LIST.add(SW_STATE_ON);
        _states_LIST.add(SW_STATE_OFF);
        _states_LIST.add(SW_STATE_NOT_ACTIVE);
        _states_MAP.put(SW_STATE_ON, Integer.valueOf(R.string.Resource_Thermostat_StateName1));
        _states_MAP.put(SW_STATE_OFF, Integer.valueOf(R.string.Resource_Thermostat_StateName2));
        _states_MAP.put(SW_STATE_NOT_ACTIVE, Integer.valueOf(R.string.Resource_Thermostat_StateName3));
    }

    public ThermostatGroup() {
        setType(NVModel.EL_TYPE_THERMOSTAT_GROUP);
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
        sa.append(" icon_inactive=\"" + getBackgroundByState(SW_STATE_NOT_ACTIVE) + "\"");
        sa.append(" min=\"" + getMin().toString() + "\"");
        sa.append(" max=\"" + getMax().toString() + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
