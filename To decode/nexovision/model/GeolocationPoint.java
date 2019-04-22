package eu.nexwell.android.nexovision.model;

import android.graphics.drawable.Drawable;
import android.location.Location;
import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class GeolocationPoint extends Element {
    public static Integer GEOLOCP_STATE_IN = Integer.valueOf(1);
    public static Integer GEOLOCP_STATE_OUT = Integer.valueOf(0);
    private static String _defaultCategory = NVModel.CATEGORY_GEOLOCATION;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_GeolocationPoint);
    protected LinkedHashMap<Integer, Integer> _backgroundsToStatesMap;
    private float _distance;
    private Integer _enter_logic;
    private String _enter_message;
    private Integer _exit_logic;
    private String _exit_message;
    protected LinkedHashMap<Integer, Integer> _iconsToStatesMap;
    private Location _location;
    private int _mode;
    private int _radius;
    private Integer _state = GEOLOCP_STATE_OUT;

    static {
        _states_LIST.add(GEOLOCP_STATE_IN);
        _states_LIST.add(GEOLOCP_STATE_OUT);
        _states_MAP.put(GEOLOCP_STATE_IN, Integer.valueOf(R.string.Resource_Logic_StateName2));
        _states_MAP.put(GEOLOCP_STATE_OUT, Integer.valueOf(R.string.Resource_Logic_StateName1));
    }

    public GeolocationPoint() {
        setType(NVModel.EL_TYPE_GEOLOCATIONPOINT);
        this._iconsToStatesMap = new LinkedHashMap();
        this._backgroundsToStatesMap = new LinkedHashMap();
        this._iconsToStatesMap.put(GEOLOCP_STATE_IN, Integer.valueOf(1));
        this._iconsToStatesMap.put(GEOLOCP_STATE_OUT, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(GEOLOCP_STATE_IN, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(GEOLOCP_STATE_OUT, Integer.valueOf(0));
        setState(GEOLOCP_STATE_OUT);
        setState(GEOLOCP_STATE_OUT);
        setIconForState(GEOLOCP_STATE_IN, "ic_location_in");
        setIconForState(GEOLOCP_STATE_OUT, "ic_location_out");
        setBackgroundForState(GEOLOCP_STATE_IN, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_green));
        setBackgroundForState(GEOLOCP_STATE_OUT, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
        this._mode = 0;
        this._distance = 0.0f;
    }

    public GeolocationPoint(Location loc) {
        setLocation(loc);
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

    public String getInfo() {
        if (this._distance <= 0.0f) {
            return null;
        }
        return String.format("%.2f m", new Object[]{Float.valueOf(this._distance)});
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

    public static ArrayList<Integer> getStatesList() {
        return _states_LIST;
    }

    public static LinkedHashMap<Integer, Integer> getStatesMap() {
        return _states_MAP;
    }

    public void setLocation(Location loc) {
        this._location = loc;
    }

    public Location getLocation() {
        return this._location;
    }

    public void setRadius(int radius) {
        this._radius = radius;
    }

    public int getRadius() {
        return this._radius;
    }

    public void setOnEnterLogic(Integer id) {
        this._enter_logic = id;
    }

    public void setOnEnterLogic(Logic logic) {
        if (logic != null) {
            this._enter_logic = logic.getId();
        }
    }

    public Integer getOnEnterLogicId() {
        return this._enter_logic;
    }

    public Logic getOnEnterLogic() {
        IElement el = NVModel.getElementById(this._enter_logic);
        if (el == null || !(el instanceof Logic)) {
            return null;
        }
        return (Logic) el;
    }

    public void setOnExitLogic(Integer id) {
        this._exit_logic = id;
    }

    public void setOnExitLogic(Logic logic) {
        if (logic != null) {
            this._exit_logic = logic.getId();
        }
    }

    public Integer getOnExitLogicId() {
        return this._exit_logic;
    }

    public Logic getOnExitLogic() {
        IElement el = NVModel.getElementById(this._exit_logic);
        if (el == null || !(el instanceof Logic)) {
            return null;
        }
        return (Logic) el;
    }

    public void setOnEnterMessage(String message) {
        this._enter_message = message;
    }

    public String getOnEnterMessage() {
        return this._enter_message;
    }

    public void setOnExitMessage(String message) {
        this._exit_message = message;
    }

    public String getOnExitMessage() {
        return this._exit_message;
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
    }

    public void setMode(int mode) {
        this._mode = mode;
    }

    public int getMode() {
        return this._mode;
    }

    public void setDistance(float dist) {
        this._distance = dist;
    }

    public float getDistance() {
        return this._distance;
    }

    public String toXML(String spec_attrs) {
        StringBuffer sa = new StringBuffer();
        if (getOnEnterMessage() != null) {
            sa.append(" enter_message=\"" + getOnEnterMessage() + "\"");
        } else {
            sa.append(" enter_message=\"\"");
        }
        if (getOnExitMessage() != null) {
            sa.append(" exit_message=\"" + getOnExitMessage() + "\"");
        } else {
            sa.append(" exit_message=\"\"");
        }
        if (getOnEnterLogicId() != null) {
            sa.append(" enter_logic_id=\"" + getOnEnterLogicId().intValue() + "\"");
        } else {
            sa.append(" enter_logic_id=\"\"");
        }
        if (getOnExitLogicId() != null) {
            sa.append(" exit_logic_id=\"" + getOnExitLogicId().intValue() + "\"");
        } else {
            sa.append(" exit_logic_id=\"\"");
        }
        if (getRadius() >= 0) {
            sa.append(" radius=\"" + getRadius() + "\"");
        } else {
            sa.append(" radius=\"\"");
        }
        if (getLocation() != null) {
            sa.append(" latitude=\"" + getLocation().getLatitude() + "\"");
            sa.append(" longitude=\"" + getLocation().getLongitude() + "\"");
            sa.append(" accuracy=\"" + getLocation().getAccuracy() + "\"");
        }
        return super.toXML(sa.toString());
    }
}
