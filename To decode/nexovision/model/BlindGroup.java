package eu.nexwell.android.nexovision.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class BlindGroup extends Blind {
    public static Integer SW_STATE_LOWERING = Integer.valueOf(1);
    public static Integer SW_STATE_NOT_MOVING = Integer.valueOf(0);
    public static Integer SW_STATE_RISING = Integer.valueOf(2);
    private static String _defaultCategory = NVModel.CATEGORY_BLINDS;
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Blind_Group);

    static {
        _states_LIST.add(SW_STATE_RISING);
        _states_LIST.add(SW_STATE_LOWERING);
        _states_LIST.add(SW_STATE_NOT_MOVING);
        _states_MAP.put(SW_STATE_RISING, Integer.valueOf(R.string.Resource_Blind_StateName1));
        _states_MAP.put(SW_STATE_LOWERING, Integer.valueOf(R.string.Resource_Blind_StateName2));
        _states_MAP.put(SW_STATE_NOT_MOVING, Integer.valueOf(R.string.Resource_Blind_StateName3));
    }

    public BlindGroup() {
        setType(NVModel.EL_TYPE_BLIND_GROUP);
        setIconForState(SW_STATE_RISING, "ic_blind_up");
        setIconForState(SW_STATE_LOWERING, "ic_blind_down");
        setIconForState(SW_STATE_NOT_MOVING, "ic_blind_stop");
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
