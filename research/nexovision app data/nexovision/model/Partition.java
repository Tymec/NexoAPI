package eu.nexwell.android.nexovision.model;

import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Partition extends Switch {
    public static int ACTION_STATUS = 0;
    public static Integer SW_STATE_ALARMING = Integer.valueOf(3);
    public static Integer SW_STATE_ARMED = Integer.valueOf(1);
    public static Integer SW_STATE_DISARMED = Integer.valueOf(0);
    private static String _defaultCategory = NVModel.CATEGORY_ALARM;
    public static ArrayList<Function> _funclist = new ArrayList();
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Partition);
    private static Integer _typeNameResId24h = Integer.valueOf(R.string.ResourceTypeName_Partition24h);
    private Integer SW_ACTION_ARM;
    private Integer SW_ACTION_CLEAR;
    private Integer SW_ACTION_DISARM;
    private Function _func;
    public ArrayList<IElement> _sensorsList;
    public String _sensors_tmp;

    public enum Function {
        COMMON(0, "common", R.string.Resource_Partition_FuncName_Common),
        FIRE24H(1, "fire24h", R.string.Resource_Partition_FuncName_Fire24h),
        FLOOD24H(2, "flood24h", R.string.Resource_Partition_FuncName_Flood24h);
        
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
        _funclist.add(Function.COMMON);
        _funclist.add(Function.FIRE24H);
        _funclist.add(Function.FLOOD24H);
        _states_LIST.add(SW_STATE_ARMED);
        _states_LIST.add(SW_STATE_DISARMED);
        _states_LIST.add(SW_STATE_ALARMING);
        _states_MAP.put(SW_STATE_ARMED, Integer.valueOf(R.string.Resource_Partition_StateName1));
        _states_MAP.put(SW_STATE_DISARMED, Integer.valueOf(R.string.Resource_Partition_StateName2));
        _states_MAP.put(SW_STATE_ALARMING, Integer.valueOf(R.string.Resource_Partition_StateName3));
    }

    public Partition() {
        this(Function.COMMON);
    }

    public Partition(Function func) {
        this.SW_ACTION_CLEAR = Integer.valueOf(2);
        this.SW_ACTION_ARM = Integer.valueOf(1);
        this.SW_ACTION_DISARM = Integer.valueOf(0);
        setType(NVModel.EL_TYPE_PARTITION);
        this._func = func;
        this._sensorsList = new ArrayList();
        this._iconsToStatesMap.put(SW_STATE_ARMED, Integer.valueOf(0));
        if (func == Function.COMMON) {
            this._iconsToStatesMap.put(SW_STATE_DISARMED, Integer.valueOf(1));
        }
        this._iconsToStatesMap.put(SW_STATE_ALARMING, Integer.valueOf(2));
        this._backgroundsToStatesMap.put(SW_STATE_ARMED, Integer.valueOf(0));
        if (func == Function.COMMON) {
            this._backgroundsToStatesMap.put(SW_STATE_DISARMED, Integer.valueOf(1));
        }
        this._backgroundsToStatesMap.put(SW_STATE_ALARMING, Integer.valueOf(2));
        saveState(SW_STATE_ARMED);
        saveState(SW_STATE_ARMED);
        setIconForState(SW_STATE_ARMED, "ic_lock_closed");
        if (func == Function.COMMON) {
            setIconForState(SW_STATE_DISARMED, "ic_lock_opened");
        }
        setIconForState(SW_STATE_ALARMING, "ic_ring");
        if (func == Function.COMMON) {
            setBackgroundForState(SW_STATE_ARMED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_turquoise));
        } else {
            setBackgroundForState(SW_STATE_ARMED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_yellow));
        }
        if (func == Function.COMMON) {
            setBackgroundForState(SW_STATE_DISARMED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
        }
        setBackgroundForState(SW_STATE_ALARMING, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_red));
    }

    public void setFunc(Function func) {
        this._func = func;
        if (this._func == Function.COMMON) {
            setIconForState(SW_STATE_ARMED, "ic_lock_closed");
        } else if (this._func == Function.FIRE24H) {
            setIconForState(SW_STATE_ARMED, "ic_fire");
        } else if (this._func == Function.FLOOD24H) {
            setIconForState(SW_STATE_ARMED, "ic_flood");
        }
        if (func == Function.COMMON) {
            setBackgroundForState(SW_STATE_ARMED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_turquoise));
        } else if (func == Function.FIRE24H) {
            setBackgroundForState(SW_STATE_ARMED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_yellow));
        } else if (func == Function.FLOOD24H) {
            setBackgroundForState(SW_STATE_ARMED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_blue));
        }
        if (func == Function.COMMON) {
            setBackgroundForState(SW_STATE_DISARMED, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
        }
    }

    public void setFunc(String func) {
        if (func != null) {
            if (func.equals("standard")) {
                setFunc(Function.COMMON);
            } else if (func.equals("fire24h")) {
                setFunc(Function.FIRE24H);
            } else if (func.equals("flood24h")) {
                setFunc(Function.FLOOD24H);
            }
        }
    }

    public Function getFunc() {
        return this._func;
    }

    public String clear(String password) {
        return super.getActionCommand(this.SW_ACTION_CLEAR.toString() + " " + password);
    }

    public String arm(String password) {
        if (this._func == Function.COMMON) {
            return super.getActionCommand(this.SW_ACTION_ARM.toString() + " " + password);
        }
        return null;
    }

    public String disarm(String password) {
        if (this._func == Function.COMMON) {
            return super.getActionCommand(this.SW_ACTION_DISARM.toString() + " " + password);
        }
        return null;
    }

    public boolean parseResp(String resp) {
        if (resp.length() <= 10 || !resp.matches("~[0-9]*:\\Q" + getResource() + "\\E .*")) {
            return false;
        }
        String[] strval = resp.substring(getResource().length() + 11).split(" ");
        if (strval.length <= 0 || !strval[0].matches("[0-9]*")) {
            return false;
        }
        saveState(Integer.valueOf(Integer.parseInt(strval[0]) & 3));
        if (isAlarming()) {
            setInfo(String.format("%s", new Object[]{MainActivity.getContext().getResources().getString(R.string.Resource_Partition_StateName3)}));
        } else if (isArmed()) {
            setInfo(String.format("%s", new Object[]{MainActivity.getContext().getResources().getString(R.string.Resource_Partition_StateName1)}));
        } else if (this._func == Function.COMMON) {
            setInfo(String.format("%s", new Object[]{MainActivity.getContext().getResources().getString(R.string.Resource_Partition_StateName2)}));
        }
        if (strval.length <= 1 || !strval[1].matches("[0-9]*")) {
            return true;
        }
        if ((Integer.valueOf(Integer.parseInt(strval[1])).intValue() & 128) == 128) {
            ACTION_STATUS = 1;
            return true;
        }
        ACTION_STATUS = 2;
        return true;
    }

    public String switchState(String password) {
        if (this._func != Function.COMMON) {
            return null;
        }
        if (isArmed()) {
            return disarm(password);
        }
        return arm(password);
    }

    public static ArrayList<Integer> getStatesList() {
        return _states_LIST;
    }

    public static LinkedHashMap<Integer, Integer> getStatesMap() {
        return _states_MAP;
    }

    public boolean isArmed() {
        if ((getSimpleState(Integer.valueOf(0)).intValue() & 1) != 0) {
            return true;
        }
        return false;
    }

    public boolean isAlarming() {
        if ((getSimpleState(Integer.valueOf(0)).intValue() & 2) != 0) {
            return true;
        }
        return false;
    }

    public Integer getSimpleState(Integer s) {
        return Integer.valueOf(getState(s).intValue() & 3);
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static Integer getTypeNameResId24h() {
        return _typeNameResId24h;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
    }

    public void clearSensors() {
        this._sensorsList.clear();
    }

    public void addSensor(Sensor sensor) {
        this._sensorsList.add(sensor);
    }

    public void addSensors(ArrayList<IElement> sensors) {
        this._sensorsList.addAll(sensors);
    }

    public void addSensors(String sensors) {
        this._sensors_tmp = sensors;
    }

    public void updateSensors() {
        if (this._sensors_tmp != null && !this._sensors_tmp.isEmpty()) {
            String[] ids = this._sensors_tmp.split(",");
            if (ids.length > 0) {
                for (String parseInt : ids) {
                    IElement el = NVModel.getElementById(Integer.valueOf(Integer.parseInt(parseInt)));
                    if (el instanceof Sensor) {
                        this._sensorsList.add(el);
                    }
                }
            }
        }
    }

    public ArrayList<IElement> getSensors() {
        return (ArrayList) this._sensorsList.clone();
    }

    public String toXML(String spec_attrs) {
        StringBuffer sa = new StringBuffer();
        sa.append(" icon_armed=\"" + getBackgroundByState(SW_STATE_ARMED) + "\"");
        if (this._func == Function.COMMON) {
            sa.append(" icon_disarmed=\"" + getBackgroundByState(SW_STATE_DISARMED) + "\"");
        }
        sa.append(" icon_alarming=\"" + getBackgroundByState(SW_STATE_ALARMING) + "\"");
        sa.append(" func=\"" + this._func.getXmlName() + "\"");
        String sensors = null;
        Iterator<IElement> itrs = getSensors().iterator();
        while (itrs.hasNext()) {
            Sensor s = (Sensor) itrs.next();
            if (sensors == null || sensors.isEmpty()) {
                sensors = s.getId() + "";
            } else {
                sensors = sensors + "," + s.getId();
            }
        }
        if (!(sensors == null || sensors.isEmpty())) {
            sa.append(" sensors=\"" + sensors + "\"");
        }
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
