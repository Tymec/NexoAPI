package eu.nexwell.android.nexovision.model;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Thermometer extends Switch {
    public static Integer SW_STATE_ON = Integer.valueOf(1);
    private static String _defaultCategory = NVModel.CATEGORY_TEMPERATURE;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Termometer);
    private Thermostat _thermostat;

    static {
        _states_LIST.add(SW_STATE_ON);
        _states_MAP.put(SW_STATE_ON, Integer.valueOf(R.string.ResourceTypeName_Termometer));
    }

    public Thermometer() {
        setType(NVModel.EL_TYPE_THERMOMETER);
        this._iconsToStatesMap.put(SW_STATE_ON, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE_ON, Integer.valueOf(0));
        saveState(SW_STATE_ON);
        saveState(SW_STATE_ON);
        setIconForState(SW_STATE_ON, "ic_thermometer");
        setBackgroundForState(SW_STATE_ON, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
    }

    public boolean parseResp(String resp) {
        if (resp.length() <= 10 || !resp.matches("~[0-9]*:\\Q" + getResource() + "\\E .*")) {
            return false;
        }
        String strval = resp.substring(getResource().length() + 11);
        if (!strval.matches("(\\-)?[0-9]*")) {
            return false;
        }
        Integer STATE = Integer.valueOf(Integer.parseInt(strval));
        saveState(STATE);
        Float temp = Float.valueOf(((float) STATE.intValue()) / 10.0f);
        setInfo(String.format("%.1f %s", new Object[]{temp, MainActivity.getContext().getString(R.string.TempUnit_C)}));
        return true;
    }

    public Float getValue() {
        return Float.valueOf(((float) getState(Integer.valueOf(0)).intValue()) / 10.0f);
    }

    public void setThermostat(Thermostat th) {
        this._thermostat = th;
        th.setThermometer(this);
    }

    public Thermostat getThermostat() {
        return this._thermostat;
    }

    public Integer getSimpleState(Integer s) {
        return SW_STATE_ON;
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
        sa.append(" icon=\"" + getBackground() + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }

    public Drawable getBackground() {
        GradientDrawable dr = (GradientDrawable) ResourcesCompat.getDrawable(MainActivity.getContext().getResources(), R.drawable.rounded_rectangle_gray, null).mutate();
        GradientDrawable dr_light = (GradientDrawable) ResourcesCompat.getDrawable(MainActivity.getContext().getResources(), R.drawable.rounded_rectangle_lightgray, null).mutate();
        Float thermometer_temp = getValue();
        Float thermostat_temp = Float.valueOf(thermometer_temp.floatValue());
        if (getThermostat() != null) {
            thermostat_temp = getThermostat().getValue();
        }
        float diff = thermometer_temp.floatValue() - thermostat_temp.floatValue();
        if (diff < -20.0f) {
            diff = -20.0f;
        } else if (diff > 20.0f) {
            diff = 20.0f;
        }
        int color = ContextCompat.getColor(MainActivity.getContext(), R.color.gray);
        int color_light = ContextCompat.getColor(MainActivity.getContext(), R.color.gray_light);
        int cold_color = ContextCompat.getColor(MainActivity.getContext(), R.color.blue);
        int cold_color_light = ContextCompat.getColor(MainActivity.getContext(), R.color.blue_light);
        int warm_color = ContextCompat.getColor(MainActivity.getContext(), R.color.red);
        int warm_color_light = ContextCompat.getColor(MainActivity.getContext(), R.color.red_light);
        if (diff < 0.0f) {
            dr.setColor(interpolateRGB(color, cold_color, (-diff) / 20.0f));
            dr_light.setColor(interpolateRGB(color_light, cold_color_light, (-diff) / 20.0f));
        } else {
            dr.setColor(interpolateRGB(color, warm_color, diff / 20.0f));
            dr_light.setColor(interpolateRGB(color_light, warm_color_light, diff / 20.0f));
        }
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{16842913}, dr.mutate());
        states.addState(new int[]{16842919}, dr.mutate());
        states.addState(new int[]{16842908}, dr.mutate());
        states.addState(new int[0], dr_light.mutate());
        return states;
    }

    public static int interpolateRGB(int a, int b, float proportion) {
        float aAmount = 1.0f - proportion;
        return Color.rgb((int) ((((float) Color.red(a)) * aAmount) + (((float) Color.red(b)) * proportion)), (int) ((((float) Color.green(a)) * aAmount) + (((float) Color.green(b)) * proportion)), (int) ((((float) Color.blue(a)) * aAmount) + (((float) Color.blue(b)) * proportion)));
    }
}
