package eu.nexwell.android.nexovision.model;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class AnalogOutput extends Switch {
    public static Integer SW_STATE_OFF = Integer.valueOf(0);
    public static Integer SW_STATE_ON = Integer.valueOf(1);
    private static String _defaultCategory = NVModel.CATEGORY_AUTOMATION;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_AnalogOutput);
    private Integer SW_ACTION_OFF = Integer.valueOf(0);
    private Integer SW_ACTION_ON = Integer.valueOf(1);
    private Integer SW_ACTION_ON_WITH_VALUE = Integer.valueOf(3);
    private Integer SW_ACTION_SET = Integer.valueOf(4);

    static {
        _states_LIST.add(SW_STATE_ON);
        _states_LIST.add(SW_STATE_OFF);
        _states_MAP.put(SW_STATE_ON, Integer.valueOf(R.string.Resource_AnalogOutput_StateName1));
        _states_MAP.put(SW_STATE_OFF, Integer.valueOf(R.string.Resource_AnalogOutput_StateName2));
    }

    public AnalogOutput() {
        setType(NVModel.EL_TYPE_ANALOGOUTPUT);
        this._iconsToStatesMap.put(SW_STATE_OFF, Integer.valueOf(0));
        this._iconsToStatesMap.put(SW_STATE_ON, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(SW_STATE_ON, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE_OFF, Integer.valueOf(1));
        saveState(SW_STATE_OFF);
        saveState(SW_STATE_OFF);
        setIconForState(SW_STATE_ON, "ic_light_on");
        setIconForState(SW_STATE_OFF, "ic_light_off");
        setBackgroundForState(SW_STATE_ON, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_blue));
        setBackgroundForState(SW_STATE_OFF, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
    }

    public String on(Integer value) {
        if (value == null || value.intValue() < 0 || value.intValue() > 100) {
            return super.getActionCommand(this.SW_ACTION_ON.toString());
        }
        return super.getActionCommand(Integer.valueOf(this.SW_ACTION_ON_WITH_VALUE.intValue() + (((int) Math.round((((double) ((float) value.intValue())) * 255.0d) / 100.0d)) << 8)).toString());
    }

    public String off() {
        return super.getActionCommand(this.SW_ACTION_OFF.toString());
    }

    public String set(Integer value) {
        if (value == null || value.intValue() < 0 || value.intValue() > 100) {
            return null;
        }
        return super.getActionCommand(Integer.valueOf(this.SW_ACTION_SET.intValue() + (((int) Math.round((((double) ((float) value.intValue())) * 255.0d) / 100.0d)) << 8)).toString());
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
        setInfo(String.format("%d%s", new Object[]{getValue(), "%"}));
        return true;
    }

    public Integer getSimpleState(Integer s) {
        return Integer.valueOf(getState(s).intValue() & 255);
    }

    public String switchState() {
        if (getSimpleState(Integer.valueOf(0)) == SW_STATE_ON) {
            return off();
        }
        return on(null);
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

    public Integer getValue() {
        return Integer.valueOf((int) Math.round(((double) ((getState(Integer.valueOf(0)).intValue() >> 8) * 100)) / 255.0d));
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

    public Drawable getBackground() {
        if (getSimpleState(Integer.valueOf(0)).equals(SW_STATE_OFF)) {
            return getBackgroundByState(SW_STATE_OFF);
        }
        float bri = getValue().floatValue();
        GradientDrawable gdr = (GradientDrawable) ContextCompat.getDrawable(MainActivity.getContext(), R.drawable.rounded_rectangle_blue).mutate();
        gdr.setColor(interpolateRGB(ContextCompat.getColor(MainActivity.getContext(), R.color.gray), ContextCompat.getColor(MainActivity.getContext(), R.color.blue), bri / 100.0f));
        GradientDrawable gdr_light = (GradientDrawable) ContextCompat.getDrawable(MainActivity.getContext(), R.drawable.rounded_rectangle_lightblue).mutate();
        gdr_light.setColor(interpolateRGB(ContextCompat.getColor(MainActivity.getContext(), R.color.gray_light), ContextCompat.getColor(MainActivity.getContext(), R.color.blue_light), bri / 100.0f));
        Drawable states = new StateListDrawable();
        states.addState(new int[]{16842913}, gdr.mutate());
        states.addState(new int[]{16842919}, gdr.mutate());
        states.addState(new int[]{16842908}, gdr.mutate());
        states.addState(new int[0], gdr_light.mutate());
        return states;
    }

    public static int interpolateRGB(int a, int b, float proportion) {
        float aAmount = 1.0f - proportion;
        return Color.rgb((int) ((((float) Color.red(a)) * aAmount) + (((float) Color.red(b)) * proportion)), (int) ((((float) Color.green(a)) * aAmount) + (((float) Color.green(b)) * proportion)), (int) ((((float) Color.blue(a)) * aAmount) + (((float) Color.blue(b)) * proportion)));
    }
}
