package eu.nexwell.android.nexovision.model;

import android.graphics.drawable.Drawable;
import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Logic extends Element {
    private static Integer LOGIC_STATE_1 = Integer.valueOf(0);
    private static Integer LOGIC_STATE_2 = Integer.valueOf(1);
    private static String _defaultCategory = NVModel.CATEGORY_LOGICS;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Logic);
    private ArrayList<String> LAST_EVENTS;
    private String[] _action = new String[2];
    protected LinkedHashMap<Integer, Integer> _backgroundsToStatesMap;
    private String[] _event = new String[2];
    protected LinkedHashMap<Integer, Integer> _iconsToStatesMap;
    private Integer _state = LOGIC_STATE_1;
    private String[] _state_label = new String[2];

    static {
        _states_LIST.add(LOGIC_STATE_2);
        _states_LIST.add(LOGIC_STATE_1);
        _states_MAP.put(LOGIC_STATE_2, Integer.valueOf(R.string.Resource_Logic_StateName2));
        _states_MAP.put(LOGIC_STATE_1, Integer.valueOf(R.string.Resource_Logic_StateName1));
    }

    public Logic() {
        setType(NVModel.EL_TYPE_LOGIC);
        this._iconsToStatesMap = new LinkedHashMap();
        this._backgroundsToStatesMap = new LinkedHashMap();
        this._iconsToStatesMap.put(LOGIC_STATE_2, Integer.valueOf(1));
        this._iconsToStatesMap.put(LOGIC_STATE_1, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(LOGIC_STATE_2, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(LOGIC_STATE_1, Integer.valueOf(0));
        setState(LOGIC_STATE_2);
        setState(LOGIC_STATE_2);
        setIconForState(LOGIC_STATE_2, "ic_logic4");
        setIconForState(LOGIC_STATE_1, "ic_logic");
        setBackgroundForState(LOGIC_STATE_2, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
        setBackgroundForState(LOGIC_STATE_1, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
        this.LAST_EVENTS = new ArrayList();
    }

    public void setIconForState(Integer s, String img) {
        Integer i = (Integer) this._iconsToStatesMap.get(s);
        if (i != null) {
            this._icons.put(i, img);
        }
    }

    public void setBackgroundForState(Integer s, Drawable d) {
        Integer i = (Integer) this._backgroundsToStatesMap.get(s);
        if (i != null) {
            this._backgrounds.put(i, d);
        }
    }

    public void setState(Integer state) {
        this._state = state;
    }

    public Integer getState() {
        return this._state;
    }

    public String getActionCommand(String action) {
        return "system L " + action + "\u0000";
    }

    public String getActionOldCommand(String action) {
        return "system logic " + action + "\u0000";
    }

    public void setAction1(String action) {
        this._action[0] = action;
    }

    public String getAction1() {
        return this._action[0];
    }

    public void setAction2(String action) {
        this._action[1] = action;
    }

    public String getAction2() {
        return this._action[1];
    }

    public void setEvent1(String event) {
        this._event[0] = event;
    }

    public String getEvent1() {
        return this._event[0];
    }

    public void setEvent2(String event) {
        this._event[1] = event;
    }

    public String getEvent2() {
        return this._event[1];
    }

    public void setState1Label(String label) {
        this._state_label[0] = label;
    }

    public void setState2Label(String label) {
        this._state_label[1] = label;
    }

    public String getState1Label() {
        return this._state_label[0];
    }

    public String getState2Label() {
        return this._state_label[1];
    }

    public String getStateLabel() {
        if (this._state == LOGIC_STATE_1) {
            return getState1Label();
        }
        return getState2Label();
    }

    public String action() {
        if (this._state == LOGIC_STATE_1) {
            return action2();
        }
        return action1();
    }

    public String action1() {
        return getActionCommand(getAction1());
    }

    public String action2() {
        return getActionCommand(getAction2());
    }

    public String getIconByState(Integer s) {
        Integer i = (Integer) this._iconsToStatesMap.get(s);
        if (this._icons.size() <= 0 || i.intValue() >= this._icons.size()) {
            return null;
        }
        return (String) this._icons.get(i);
    }

    public Drawable getBackgroundByState(Integer s) {
        Integer i = (Integer) this._backgroundsToStatesMap.get(s);
        if (this._backgrounds.size() <= 0 || i.intValue() >= this._backgrounds.size()) {
            return null;
        }
        return (Drawable) this._backgrounds.get(i);
    }

    public Drawable getBackground() {
        return getBackgroundByState(getState());
    }

    public String getIcon() {
        return getIconByState(getState());
    }

    public boolean parseResp(String resp) {
        if (resp.length() > 10 && resp.matches("~[0-9]*:\\Q" + getEvent1() + "\\E")) {
            setState(LOGIC_STATE_1);
        } else if (resp.length() <= 10 || !resp.matches("~[0-9]*:\\Q" + getEvent2() + "\\E")) {
            return false;
        } else {
            setState(LOGIC_STATE_2);
        }
        if (this.LAST_EVENTS.size() >= 100) {
            this.LAST_EVENTS.remove(0);
        }
        this.LAST_EVENTS.add(new Date().toString());
        return true;
    }

    public ArrayList<String> getLastEvents() {
        return this.LAST_EVENTS;
    }

    public void clearLastEvents() {
        this.LAST_EVENTS.clear();
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
        if (getEvent1() != null) {
            sa.append(" event1=\"" + getEvent1() + "\"");
        } else {
            sa.append(" event1=\"\"");
        }
        if (getAction1() != null) {
            sa.append(" action1=\"" + getAction1() + "\"");
        } else {
            sa.append(" action1=\"\"");
        }
        if (getState1Label() != null) {
            sa.append(" state1_label=\"" + getState1Label() + "\"");
        } else {
            sa.append(" state1_label=\"\"");
        }
        if (getEvent2() != null) {
            sa.append(" event2=\"" + getEvent2() + "\"");
        } else {
            sa.append(" event2=\"\"");
        }
        if (getAction2() != null) {
            sa.append(" action2=\"" + getAction2() + "\"");
        } else {
            sa.append(" action2=\"\"");
        }
        if (getState2Label() != null) {
            sa.append(" state2_label=\"" + getState2Label() + "\"");
        } else {
            sa.append(" state2_label=\"\"");
        }
        return super.toXML(sa.toString());
    }
}
