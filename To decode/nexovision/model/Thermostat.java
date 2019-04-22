package eu.nexwell.android.nexovision.model;

import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Thermostat extends Switch {
    public static Integer SW_STATE_NOT_ACTIVE = Integer.valueOf(2);
    public static Integer SW_STATE_OFF = Integer.valueOf(0);
    public static Integer SW_STATE_ON = Integer.valueOf(1);
    private static String _defaultCategory = NVModel.CATEGORY_TEMPERATURE;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Thermostat);
    private Integer SW_ACTION_OFF = Integer.valueOf(0);
    private Integer SW_ACTION_ON = Integer.valueOf(1);
    private Integer SW_STATE_ACTIVE = Integer.valueOf(1);
    private Float _max = Float.valueOf(40.0f);
    private Float _min = Float.valueOf(0.0f);
    private Thermometer _thermometer;

    static {
        _states_LIST.add(SW_STATE_ON);
        _states_LIST.add(SW_STATE_OFF);
        _states_LIST.add(SW_STATE_NOT_ACTIVE);
        _states_MAP.put(SW_STATE_ON, Integer.valueOf(R.string.Resource_Thermostat_StateName1));
        _states_MAP.put(SW_STATE_OFF, Integer.valueOf(R.string.Resource_Thermostat_StateName2));
        _states_MAP.put(SW_STATE_NOT_ACTIVE, Integer.valueOf(R.string.Resource_Thermostat_StateName3));
    }

    public Thermostat() {
        setType(NVModel.EL_TYPE_THERMOSTAT);
        this._backgroundsToStatesMap.put(SW_STATE_ON, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE_OFF, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(SW_STATE_NOT_ACTIVE, Integer.valueOf(2));
        saveState(SW_STATE_NOT_ACTIVE);
        saveState(SW_STATE_NOT_ACTIVE);
    }

    public String activate(Float value) {
        if (value == null || value.floatValue() < getMin().floatValue() || value.floatValue() > getMax().floatValue()) {
            return super.getActionCommand(Integer.valueOf(this.SW_ACTION_ON.intValue() + 8388352).toString());
        }
        return super.getActionCommand(Integer.valueOf(this.SW_ACTION_ON.intValue() + (((int) (value.floatValue() * 10.0f)) << 8)).toString());
    }

    public String deactivate() {
        return super.getActionCommand(Integer.valueOf(this.SW_ACTION_OFF.intValue() + 8388352).toString());
    }

    public boolean parseResp(String resp) {
        if (resp.length() <= 10 || !resp.matches("~[0-9]*:\\Q" + getResource() + "\\E .*")) {
            return false;
        }
        String strval = resp.substring(getResource().length() + 11);
        if (!strval.matches("(\\-)?[0-9]*")) {
            return false;
        }
        saveState(Integer.valueOf(Integer.parseInt(strval)));
        setInfo(String.format("%.1f %s", new Object[]{getValue(), MainActivity.getContext().getString(R.string.TempUnit_C)}));
        return true;
    }

    public Integer getSimpleState(Integer s) {
        if (((getState(s).intValue() >> 8) & 255) == this.SW_STATE_ACTIVE.intValue()) {
            return Integer.valueOf(getState(s).intValue() & 255);
        }
        return SW_STATE_NOT_ACTIVE;
    }

    public String switchState() {
        if (getSimpleState(Integer.valueOf(0)) == SW_STATE_ON || getSimpleState(Integer.valueOf(0)) == SW_STATE_OFF) {
            return deactivate();
        }
        return activate(new Float(32767.0f));
    }

    public static ArrayList<Integer> getStatesList() {
        return _states_LIST;
    }

    public static LinkedHashMap<Integer, Integer> getStatesMap() {
        return _states_MAP;
    }

    public void setMin(Float value) {
        this._min = value;
    }

    public Float getMin() {
        return this._min;
    }

    public void setMax(Float value) {
        this._max = value;
    }

    public Float getMax() {
        return this._max;
    }

    public boolean isOn() {
        if (Integer.valueOf(getState(Integer.valueOf(0)).intValue() & 255) == SW_STATE_ON) {
            return true;
        }
        return false;
    }

    public boolean isActive() {
        if (Integer.valueOf((getState(Integer.valueOf(0)).intValue() >> 8) & 255) == this.SW_STATE_ACTIVE) {
            return true;
        }
        return false;
    }

    public Float getValue() {
        return Float.valueOf(((float) ((short) ((int) ((((long) getState(Integer.valueOf(0)).intValue()) >> 16) & 65535)))) / 10.0f);
    }

    public void setThermometer(Thermometer th) {
        this._thermometer = th;
    }

    public Thermometer getThermometer() {
        return this._thermometer;
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
        if (getThermometer() != null) {
            sa.append(" thermometer=\"" + getThermometer().getId() + "\"");
        }
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
