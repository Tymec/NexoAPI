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
import org.apache.http.HttpStatus;

public class RGBW extends Switch {
    public static Integer SW_ACTION_OFF = Integer.valueOf(0);
    public static Integer SW_ACTION_ON = Integer.valueOf(1);
    public static Integer SW_ACTION_SET = Integer.valueOf(7);
    public static Integer SW_STATE_OFF = Integer.valueOf(0);
    public static Integer SW_STATE_ON = Integer.valueOf(1);
    public static Integer SW_WHITE = Integer.valueOf(0);
    private static String _defaultCategory = NVModel.CATEGORY_LIGHT;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_RGBW);

    static {
        _states_LIST.add(SW_STATE_ON);
        _states_LIST.add(SW_STATE_OFF);
        _states_MAP.put(SW_STATE_ON, Integer.valueOf(R.string.Resource_RGBW_StateName1));
        _states_MAP.put(SW_STATE_OFF, Integer.valueOf(R.string.Resource_RGBW_StateName2));
    }

    public RGBW() {
        setType(NVModel.EL_TYPE_RGBW);
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
        Integer lastState = getState(Integer.valueOf(0));
        Integer r = Integer.valueOf(lastState.intValue() & ((SW_STATE_ON.intValue() << 6) + 63));
        return super.getActionCommand(Integer.valueOf((((Integer.valueOf((lastState.intValue() >> 16) & ((SW_STATE_ON.intValue() << 6) + 63)).intValue() << 24) + (Integer.valueOf((lastState.intValue() >> 8) & ((SW_STATE_ON.intValue() << 6) + 63)).intValue() << 16)) + (r.intValue() << 8)) + (SW_ACTION_ON.intValue() << 0)) + " " + SW_WHITE);
    }

    public String off() {
        Integer lastState = getState(Integer.valueOf(0));
        Integer r = Integer.valueOf(lastState.intValue() & ((SW_STATE_OFF.intValue() << 6) + 63));
        return super.getActionCommand(Integer.valueOf((((Integer.valueOf((lastState.intValue() >> 16) & ((SW_STATE_OFF.intValue() << 6) + 63)).intValue() << 24) + (Integer.valueOf((lastState.intValue() >> 8) & ((SW_STATE_OFF.intValue() << 6) + 63)).intValue() << 16)) + (r.intValue() << 8)) + (SW_ACTION_OFF.intValue() << 0)) + " " + SW_WHITE);
    }

    public String set(int R, int G, int B) {
        Integer lastState = getSimpleState(Integer.valueOf(0));
        Integer r = Integer.valueOf((R / 4) + (lastState.intValue() << 6));
        return super.getActionCommand(Integer.valueOf((((Integer.valueOf((B / 4) + (lastState.intValue() << 6)).intValue() << 24) + (Integer.valueOf((G / 4) + (lastState.intValue() << 6)).intValue() << 16)) + (r.intValue() << 8)) + (SW_ACTION_SET.intValue() << 0)) + " " + SW_WHITE);
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
        return true;
    }

    public Integer getSimpleState(Integer s) {
        if ((getState(s).intValue() & 4210752) > 0) {
            return SW_STATE_ON;
        }
        return SW_STATE_OFF;
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

    public Integer[] getValue() {
        Integer r = Integer.valueOf((getState(Integer.valueOf(0)).intValue() & 63) * 4);
        Integer g = Integer.valueOf(((getState(Integer.valueOf(0)).intValue() >> 8) & 63) * 4);
        Integer b = Integer.valueOf(((getState(Integer.valueOf(0)).intValue() >> 16) & 63) * 4);
        return new Integer[]{r, g, b};
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
    }

    public String restoreState(Integer s) {
        Integer on_off = SW_STATE_OFF;
        if ((s.intValue() & 4210752) > 0) {
            on_off = SW_STATE_ON;
        }
        Integer r = Integer.valueOf((s.intValue() & 63) + (on_off.intValue() << 6));
        return super.getActionCommand(Integer.valueOf((((Integer.valueOf(((s.intValue() >> 16) & 63) + (on_off.intValue() << 6)).intValue() << 24) + (Integer.valueOf(((s.intValue() >> 8) & 63) + (on_off.intValue() << 6)).intValue() << 16)) + (r.intValue() << 8)) + (SW_ACTION_SET.intValue() << 0)) + " " + SW_WHITE);
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
        Integer[] color = getValue();
        GradientDrawable gdr = (GradientDrawable) ContextCompat.getDrawable(MainActivity.getContext(), R.drawable.rounded_rectangle_yellow).mutate();
        gdr.setColor(Color.argb(HttpStatus.SC_MULTI_STATUS, color[0].intValue(), color[1].intValue(), color[2].intValue()));
        GradientDrawable gdr_light = (GradientDrawable) ContextCompat.getDrawable(MainActivity.getContext(), R.drawable.rounded_rectangle_lightyellow).mutate();
        gdr_light.setColor(Color.argb(255, color[0].intValue(), color[1].intValue(), color[2].intValue()));
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
