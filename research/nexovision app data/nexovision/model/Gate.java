package eu.nexwell.android.nexovision.model;

import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Gate extends Switch {
    public static Integer SW_STATE_CLOSED = Integer.valueOf(1);
    public static Integer SW_STATE_OPENED = Integer.valueOf(0);
    public static Integer SW_STATE_UNKNOWN = Integer.valueOf(2);
    private static String _defaultCategory = NVModel.CATEGORY_GATES;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Gate);
    private Integer SW_ACTION_CLOSE = Integer.valueOf(1);
    private Integer SW_ACTION_OPEN = Integer.valueOf(0);
    private Integer SW_ACTION_PULSE = Integer.valueOf(2);
    private long update_counter = 0;

    static {
        _states_LIST.add(SW_STATE_OPENED);
        _states_LIST.add(SW_STATE_CLOSED);
        _states_LIST.add(SW_STATE_UNKNOWN);
        _states_MAP.put(SW_STATE_OPENED, Integer.valueOf(R.string.Resource_Gate_StateName1));
        _states_MAP.put(SW_STATE_CLOSED, Integer.valueOf(R.string.Resource_Gate_StateName2));
        _states_MAP.put(SW_STATE_UNKNOWN, Integer.valueOf(R.string.Resource_Gate_StateName3));
    }

    public Gate() {
        setType(NVModel.EL_TYPE_GATE);
        this._iconsToStatesMap.put(SW_STATE_OPENED, Integer.valueOf(0));
        this._iconsToStatesMap.put(SW_STATE_CLOSED, Integer.valueOf(1));
        this._iconsToStatesMap.put(SW_STATE_UNKNOWN, Integer.valueOf(2));
        this._backgroundsToStatesMap.put(SW_STATE_OPENED, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE_CLOSED, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(SW_STATE_UNKNOWN, Integer.valueOf(2));
        saveState(SW_STATE_CLOSED);
        saveState(SW_STATE_CLOSED);
        setIconForState(SW_STATE_OPENED, "ic_opened");
        setIconForState(SW_STATE_CLOSED, "ic_closed");
        setIconForState(SW_STATE_UNKNOWN, "ic_gate_unknown");
        setBackgroundForState(SW_STATE_CLOSED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
        setBackgroundForState(SW_STATE_OPENED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
        setBackgroundForState(SW_STATE_UNKNOWN, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_orange));
    }

    public String open() {
        return super.getActionCommand(this.SW_ACTION_OPEN.toString());
    }

    public String close() {
        return super.getActionCommand(this.SW_ACTION_CLOSE.toString());
    }

    public String pulse() {
        return super.getActionCommand(this.SW_ACTION_PULSE.toString());
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
        saveState(STATE);
        if (STATE != SW_STATE_OPENED && STATE == SW_STATE_CLOSED) {
        }
        return true;
    }

    public String switchState() {
        return pulse();
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
        sa.append(" icon_opened=\"" + getBackgroundByState(SW_STATE_OPENED) + "\"");
        sa.append(" icon_closed=\"" + getBackgroundByState(SW_STATE_CLOSED) + "\"");
        sa.append(" icon_unknown=\"" + getBackgroundByState(SW_STATE_UNKNOWN) + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
