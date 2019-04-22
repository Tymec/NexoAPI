package eu.nexwell.android.nexovision.model;

import com.google.android.gms.fitness.FitnessActivities;
import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Output extends Switch {
    public static Integer SW_STATE_OFF = Integer.valueOf(0);
    public static Integer SW_STATE_ON = Integer.valueOf(1);
    public static Integer SW_STATE_UNKNOWN = Integer.valueOf(2);
    public static Integer SW_STATE_UNKNOWN2 = Integer.valueOf(3);
    private static String _defaultCategory = NVModel.CATEGORY_AUTOMATION;
    public static ArrayList<Function> _funclist = new ArrayList();
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Output);
    private Integer SW_ACTION_OFF;
    private Integer SW_ACTION_ON;
    private Function _func;
    private long update_counter;

    public enum Function {
        OTHER(0, FitnessActivities.OTHER, R.string.Resource_Output_FuncName_Other),
        GATE(1, "gate", R.string.Resource_Output_FuncName_Gate);
        
        private final int res_label;
        private final int value;
        private final String xml_name;

        private Function(int value, String xml_name, int res_label) {
            this.value = value;
            this.xml_name = xml_name;
            this.res_label = res_label;
        }

        public int getValue() {
            return this.value;
        }

        public String getXmlName() {
            return this.xml_name;
        }

        public int getResLabel() {
            return this.res_label;
        }
    }

    static {
        _funclist.add(Function.OTHER);
        _funclist.add(Function.GATE);
        _states_LIST.add(SW_STATE_ON);
        _states_LIST.add(SW_STATE_OFF);
        _states_MAP.put(SW_STATE_ON, Integer.valueOf(R.string.Resource_Output_StateName1));
        _states_MAP.put(SW_STATE_OFF, Integer.valueOf(R.string.Resource_Output_StateName2));
    }

    public Output() {
        this(Function.OTHER);
    }

    public Output(Function func) {
        this.SW_ACTION_ON = Integer.valueOf(1);
        this.SW_ACTION_OFF = Integer.valueOf(0);
        this.update_counter = 0;
        setType(NVModel.EL_TYPE_OUTPUT);
        this._func = func;
        this._iconsToStatesMap.put(SW_STATE_OFF, Integer.valueOf(0));
        this._iconsToStatesMap.put(SW_STATE_ON, Integer.valueOf(1));
        this._iconsToStatesMap.put(SW_STATE_UNKNOWN, Integer.valueOf(2));
        this._iconsToStatesMap.put(SW_STATE_UNKNOWN2, Integer.valueOf(3));
        this._backgroundsToStatesMap.put(SW_STATE_ON, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SW_STATE_OFF, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(SW_STATE_UNKNOWN, Integer.valueOf(2));
        this._backgroundsToStatesMap.put(SW_STATE_UNKNOWN2, Integer.valueOf(3));
        saveState(SW_STATE_OFF);
        saveState(SW_STATE_OFF);
        if (this._func == Function.OTHER) {
            setIconForState(SW_STATE_ON, "ic_sensor_affected");
            setIconForState(SW_STATE_OFF, "ic_sensor_notaffected");
            setIconForState(SW_STATE_UNKNOWN, "ic_sensor_affected");
            setIconForState(SW_STATE_UNKNOWN2, "ic_sensor_affected");
        } else if (this._func == Function.GATE) {
            setIconForState(SW_STATE_ON, "ic_car");
            setIconForState(SW_STATE_OFF, "ic_closed");
        }
        setBackgroundForState(SW_STATE_ON, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
        setBackgroundForState(SW_STATE_OFF, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
        setBackgroundForState(SW_STATE_UNKNOWN, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
        setBackgroundForState(SW_STATE_UNKNOWN2, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
    }

    public void setFunc(Function func) {
        this._func = func;
        if (this._func == Function.OTHER) {
            setIconForState(SW_STATE_ON, "ic_sensor_affected");
            setIconForState(SW_STATE_OFF, "ic_sensor_notaffected");
            setIconForState(SW_STATE_UNKNOWN, "ic_sensor_affected");
            setIconForState(SW_STATE_UNKNOWN2, "ic_sensor_affected");
        } else if (this._func == Function.GATE) {
            setIconForState(SW_STATE_ON, "ic_car");
            setIconForState(SW_STATE_OFF, "ic_closed");
        }
        if (func == Function.OTHER) {
            setBackgroundForState(SW_STATE_ON, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
            setBackgroundForState(SW_STATE_OFF, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
            setBackgroundForState(SW_STATE_UNKNOWN, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
            setBackgroundForState(SW_STATE_UNKNOWN2, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
        } else if (func == Function.GATE) {
            setBackgroundForState(SW_STATE_ON, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_violet));
            setBackgroundForState(SW_STATE_OFF, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_violet));
        }
    }

    public void setFunc(String func) {
        if (func != null) {
            if (func.equals(Function.OTHER.getXmlName())) {
                setFunc(Function.OTHER);
            } else if (func.equals(Function.GATE.getXmlName())) {
                setFunc(Function.GATE);
            }
        }
    }

    public Function getFunc() {
        return this._func;
    }

    public String on() {
        return super.getActionCommand(this.SW_ACTION_ON.toString());
    }

    public String off() {
        return super.getActionCommand(this.SW_ACTION_OFF.toString());
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
        if (STATE != SW_STATE_ON && STATE == SW_STATE_OFF) {
        }
        return true;
    }

    public String switchState() {
        if (getState(Integer.valueOf(0)) == SW_STATE_ON) {
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
        sa.append(" func=\"" + this._func.getXmlName() + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
