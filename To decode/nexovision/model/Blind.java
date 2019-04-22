package eu.nexwell.android.nexovision.model;

import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Blind extends Switch {
    public static Integer SW_STATE_LOWERING = Integer.valueOf(1);
    public static Integer SW_STATE_NOT_MOVING = Integer.valueOf(0);
    public static Integer SW_STATE_RISING = Integer.valueOf(2);
    private static String _defaultCategory = NVModel.CATEGORY_BLINDS;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Blind);
    private Integer SW_ACTION_DOWN = Integer.valueOf(1);
    private Integer SW_ACTION_STOP = Integer.valueOf(0);
    private Integer SW_ACTION_UP = Integer.valueOf(2);
    private boolean invertedLogic = false;

    static {
        _states_LIST.add(SW_STATE_RISING);
        _states_LIST.add(SW_STATE_LOWERING);
        _states_LIST.add(SW_STATE_NOT_MOVING);
        _states_MAP.put(SW_STATE_RISING, Integer.valueOf(R.string.Resource_Blind_StateName1));
        _states_MAP.put(SW_STATE_LOWERING, Integer.valueOf(R.string.Resource_Blind_StateName2));
        _states_MAP.put(SW_STATE_NOT_MOVING, Integer.valueOf(R.string.Resource_Blind_StateName3));
    }

    public Blind() {
        setType(NVModel.EL_TYPE_BLIND);
        this._iconsToStatesMap.put(SW_STATE_RISING, Integer.valueOf(0));
        this._iconsToStatesMap.put(SW_STATE_LOWERING, Integer.valueOf(1));
        this._iconsToStatesMap.put(SW_STATE_NOT_MOVING, Integer.valueOf(2));
        this._backgroundsToStatesMap.put(SW_STATE_RISING, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE_LOWERING, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(SW_STATE_NOT_MOVING, Integer.valueOf(2));
        saveState(SW_STATE_NOT_MOVING);
        saveState(SW_STATE_RISING);
        setIconForState(SW_STATE_RISING, "ic_blind_up");
        setIconForState(SW_STATE_LOWERING, "ic_blind_down");
        setIconForState(SW_STATE_NOT_MOVING, "ic_blind_stop");
        setBackgroundForState(SW_STATE_RISING, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
        setBackgroundForState(SW_STATE_LOWERING, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
        setBackgroundForState(SW_STATE_NOT_MOVING, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
    }

    public String up() {
        return super.getActionCommand(autoInvert(this.SW_ACTION_UP).toString());
    }

    public String down() {
        return super.getActionCommand(autoInvert(this.SW_ACTION_DOWN).toString());
    }

    public String stop() {
        return super.getActionCommand(autoInvert(this.SW_ACTION_STOP).toString());
    }

    public boolean parseResp(String resp) {
        if (resp.length() <= 10 || !resp.matches("~[0-9]*:\\Q" + getResource() + "\\E .*")) {
            return false;
        }
        String strval = resp.substring(getResource().length() + 11);
        if (!strval.matches("[0-9]*")) {
            return false;
        }
        Integer STATE = Integer.valueOf(Integer.parseInt(strval) & 255);
        if (!autoInvert(STATE).equals(getState(Integer.valueOf(0)))) {
            saveState(autoInvert(STATE));
        }
        return true;
    }

    public String switchState() {
        if (isRising() || isLowering()) {
            return stop();
        }
        if (wasRising()) {
            return down();
        }
        return up();
    }

    public static ArrayList<Integer> getStatesList() {
        return _states_LIST;
    }

    public static LinkedHashMap<Integer, Integer> getStatesMap() {
        return _states_MAP;
    }

    public boolean isRising() {
        if (getSimpleState(Integer.valueOf(0)) == SW_STATE_RISING) {
            return true;
        }
        return false;
    }

    public boolean isLowering() {
        if (getSimpleState(Integer.valueOf(0)) == SW_STATE_LOWERING) {
            return true;
        }
        return false;
    }

    public boolean isNotMoving() {
        if (getSimpleState(Integer.valueOf(0)) == SW_STATE_NOT_MOVING) {
            return true;
        }
        return false;
    }

    public boolean wasRising() {
        if (getSimpleState(Integer.valueOf(1)) == SW_STATE_RISING) {
            return true;
        }
        return false;
    }

    public boolean wasLowering() {
        if (getSimpleState(Integer.valueOf(1)) == SW_STATE_LOWERING) {
            return true;
        }
        return false;
    }

    public boolean wasNotMoving() {
        if (getSimpleState(Integer.valueOf(1)) == SW_STATE_NOT_MOVING) {
            return true;
        }
        return false;
    }

    public void setInvertedLogic(boolean invert) {
        this.invertedLogic = invert;
    }

    public boolean isLogicInverted() {
        return this.invertedLogic;
    }

    public Integer autoInvert(Integer input) {
        if (!isLogicInverted()) {
            return input;
        }
        if (input.equals(this.SW_ACTION_DOWN)) {
            return this.SW_ACTION_UP;
        }
        if (input.equals(this.SW_ACTION_UP)) {
            return this.SW_ACTION_DOWN;
        }
        return input;
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
    }

    public String toXML(String spec_attrs) {
        StringBuffer sa = new StringBuffer();
        sa.append(" icon_rising=\"" + getBackgroundByState(SW_STATE_RISING) + "\"");
        sa.append(" icon_stopped=\"" + getBackgroundByState(SW_STATE_NOT_MOVING) + "\"");
        sa.append(" icon_lowering=\"" + getBackgroundByState(SW_STATE_LOWERING) + "\"");
        sa.append(" invert_logic=\"" + Boolean.toString(isLogicInverted()) + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
