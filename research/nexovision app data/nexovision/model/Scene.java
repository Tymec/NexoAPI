package eu.nexwell.android.nexovision.model;

import android.graphics.drawable.Drawable;
import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class Scene extends Element {
    private static Integer SCENE_STATE_OFF = Integer.valueOf(0);
    private static Integer SCENE_STATE_ON = Integer.valueOf(1);
    private static ArrayList<Integer> _states_LIST = new ArrayList();
    private static LinkedHashMap<Integer, Integer> _states_MAP = new LinkedHashMap();
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Scene);
    protected LinkedHashMap<Integer, Integer> _backgroundsToStatesMap;
    protected LinkedHashMap<Integer, Integer> _iconsToStatesMap;
    private Integer _state = SCENE_STATE_OFF;
    private HashMap<Integer, Integer> elementsStatesMap;

    static {
        _states_LIST.add(SCENE_STATE_ON);
        _states_LIST.add(SCENE_STATE_OFF);
        _states_MAP.put(SCENE_STATE_ON, Integer.valueOf(R.string.Resource_Scene_StateName1));
        _states_MAP.put(SCENE_STATE_OFF, Integer.valueOf(R.string.Resource_Scene_StateName2));
    }

    public Scene() {
        setType(NVModel.EL_TYPE_SCENE);
        this.elementsStatesMap = new HashMap();
        this._iconsToStatesMap = new LinkedHashMap();
        this._backgroundsToStatesMap = new LinkedHashMap();
        this._iconsToStatesMap.put(SCENE_STATE_ON, Integer.valueOf(1));
        this._iconsToStatesMap.put(SCENE_STATE_OFF, Integer.valueOf(0));
        this._backgroundsToStatesMap.put(SCENE_STATE_ON, Integer.valueOf(1));
        this._backgroundsToStatesMap.put(SCENE_STATE_OFF, Integer.valueOf(0));
        setState(SCENE_STATE_ON);
        setState(SCENE_STATE_ON);
        setIconForState(SCENE_STATE_ON, "ic_scene");
        setIconForState(SCENE_STATE_OFF, "ic_scene");
        setBackgroundForState(SCENE_STATE_ON, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_red));
        setBackgroundForState(SCENE_STATE_OFF, MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_gray));
    }

    public void createFromSet(ISet set) {
        this.elementsStatesMap.clear();
        Iterator<IElement> itre = set.getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (el instanceof Switch) {
                Switch sw = (Switch) el;
                this.elementsStatesMap.put(sw.getId(), sw.getState(Integer.valueOf(0)));
            }
        }
    }

    public void createFromProject(String[] elstates) {
        this.elementsStatesMap.clear();
        for (String split : elstates) {
            String[] elstate = split.split(":");
            if (elstate.length > 1) {
                this.elementsStatesMap.put(Integer.valueOf(Integer.parseInt(elstate[0])), Integer.valueOf(Integer.parseInt(elstate[1])));
            }
        }
    }

    public ArrayList<String> restoreStates() {
        ArrayList<String> cmds = new ArrayList();
        for (Integer elementById : this.elementsStatesMap.keySet()) {
            IElement el = NVModel.getElementById(elementById);
            if (el != null && (el instanceof ISwitch)) {
                cmds.add(((ISwitch) el).restoreState((Integer) this.elementsStatesMap.get(el.getId())));
            }
        }
        return cmds;
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

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public String toXML(String spec_attrs) {
        StringBuffer sa = new StringBuffer();
        sa.append(" elstates=\"");
        int n = 0;
        for (Integer elementById : this.elementsStatesMap.keySet()) {
            IElement el = NVModel.getElementById(elementById);
            if (el != null && (el instanceof ISwitch)) {
                if (n > 0) {
                    sa.append(",");
                }
                sa.append(el.getId() + ":" + this.elementsStatesMap.get(el.getId()));
                n++;
            }
        }
        sa.append("\"");
        return super.toXML(sa.toString());
    }
}
