package eu.nexwell.android.nexovision.model;

import android.content.Context;
import android.util.Log;
import eu.nexwell.android.nexovision.MainActivity;
import eu.nexwell.android.nexovision.misc.XMLProject;
import eu.nexwell.android.nexovision.model.Partition.Function;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class NVModel {
    public static String CATEGORY_ALARM = "alarm";
    public static String CATEGORY_AUTOMATION = "automation";
    public static String CATEGORY_BLINDS = "blinds";
    public static String CATEGORY_CAMERAS = "cameras";
    public static String CATEGORY_GATES = "gates";
    public static String CATEGORY_GEOLOCATION = "geolocation";
    public static String CATEGORY_LIGHT = "light";
    public static String CATEGORY_LOGICS = "logics";
    public static String CATEGORY_MULTIMEDIA = "multimedia";
    public static String CATEGORY_PLACES = "places";
    public static String CATEGORY_POLYGONS = "polygons";
    public static String CATEGORY_SENSORS = "sensors";
    public static String CATEGORY_TEMPERATURE = "temperature";
    public static String CATEGORY_VIDEOPHONES = "videophones";
    public static IElement CURR_ELEMENT;
    public static String EL_TYPE_ANALOGOUTPUT = "analogoutput";
    public static String EL_TYPE_ANALOGOUTPUT_GROUP = "analogoutput_group";
    public static String EL_TYPE_ANALOGSENSOR = "analogsensor";
    public static String EL_TYPE_BLIND = "blind";
    public static String EL_TYPE_BLIND_GROUP = "blind_group";
    public static String EL_TYPE_CAMERA = "camera";
    public static String EL_TYPE_CATEGORY = "category";
    public static String EL_TYPE_DIMMER = "dimmer";
    public static String EL_TYPE_GATE = "gate";
    public static String EL_TYPE_GEOLOCATIONPOINT = "geolocationpoint";
    public static String EL_TYPE_LIGHT = "light";
    public static String EL_TYPE_LIGHT_GROUP = "light_group";
    public static String EL_TYPE_LOGIC = "logic";
    public static String EL_TYPE_OUTPUT = "output";
    public static String EL_TYPE_OUTPUT_GROUP = "output_group";
    public static String EL_TYPE_PARTITION = "partition";
    public static String EL_TYPE_PARTITION24H = "partition24h";
    public static String EL_TYPE_POLYGON = "polygon";
    public static String EL_TYPE_RGBW = "rgbw";
    public static String EL_TYPE_RGBW_GROUP = "rgbw_group";
    public static String EL_TYPE_SCENE = "scene";
    public static String EL_TYPE_SENSOR = "sensor";
    public static String EL_TYPE_SET = "set";
    public static String EL_TYPE_THERMOMETER = "thermometer";
    public static String EL_TYPE_THERMOMETER_GROUP = "thermometer_group";
    public static String EL_TYPE_THERMOSTAT = "thermostat";
    public static String EL_TYPE_THERMOSTAT_GROUP = "thermostat_group";
    public static String EL_TYPE_VENTILATOR = "ventilator";
    public static String EL_TYPE_VIDEOPHONE = "videophone";
    private static ArrayList<Category> _categories = new ArrayList();
    private static LinkedHashMap<String, Class<?>> _elTypes = new LinkedHashMap();
    private static ArrayList<IElement> _elements = new ArrayList();
    public static int _mainInThermometer = -1;
    public static int _mainOutThermometer = -1;
    private static ArrayList<IElement> _topElements = new ArrayList();

    static {
        try {
            _elTypes.put(EL_TYPE_SENSOR, Class.forName("eu.nexwell.android.nexovision.model.Sensor"));
            _elTypes.put(EL_TYPE_ANALOGSENSOR, Class.forName("eu.nexwell.android.nexovision.model.AnalogSensor"));
            _elTypes.put(EL_TYPE_PARTITION, Class.forName("eu.nexwell.android.nexovision.model.Partition"));
            _elTypes.put(EL_TYPE_PARTITION24H, Class.forName("eu.nexwell.android.nexovision.model.Partition"));
            _elTypes.put(EL_TYPE_OUTPUT, Class.forName("eu.nexwell.android.nexovision.model.Output"));
            _elTypes.put(EL_TYPE_OUTPUT_GROUP, Class.forName("eu.nexwell.android.nexovision.model.OutputGroup"));
            _elTypes.put(EL_TYPE_LIGHT, Class.forName("eu.nexwell.android.nexovision.model.Light"));
            _elTypes.put(EL_TYPE_DIMMER, Class.forName("eu.nexwell.android.nexovision.model.Dimmer"));
            _elTypes.put(EL_TYPE_LIGHT_GROUP, Class.forName("eu.nexwell.android.nexovision.model.LightGroup"));
            _elTypes.put(EL_TYPE_ANALOGOUTPUT, Class.forName("eu.nexwell.android.nexovision.model.AnalogOutput"));
            _elTypes.put(EL_TYPE_ANALOGOUTPUT_GROUP, Class.forName("eu.nexwell.android.nexovision.model.AnalogOutputGroup"));
            _elTypes.put(EL_TYPE_RGBW, Class.forName("eu.nexwell.android.nexovision.model.RGBW"));
            _elTypes.put(EL_TYPE_RGBW_GROUP, Class.forName("eu.nexwell.android.nexovision.model.RGBWGroup"));
            _elTypes.put(EL_TYPE_BLIND, Class.forName("eu.nexwell.android.nexovision.model.Blind"));
            _elTypes.put(EL_TYPE_BLIND_GROUP, Class.forName("eu.nexwell.android.nexovision.model.BlindGroup"));
            _elTypes.put(EL_TYPE_THERMOMETER, Class.forName("eu.nexwell.android.nexovision.model.Thermometer"));
            _elTypes.put(EL_TYPE_THERMOSTAT, Class.forName("eu.nexwell.android.nexovision.model.Thermostat"));
            _elTypes.put(EL_TYPE_THERMOSTAT_GROUP, Class.forName("eu.nexwell.android.nexovision.model.ThermostatGroup"));
            _elTypes.put(EL_TYPE_GATE, Class.forName("eu.nexwell.android.nexovision.model.Gate"));
            _elTypes.put(EL_TYPE_VENTILATOR, Class.forName("eu.nexwell.android.nexovision.model.Ventilator"));
            _elTypes.put(EL_TYPE_LOGIC, Class.forName("eu.nexwell.android.nexovision.model.Logic"));
            _elTypes.put(EL_TYPE_CAMERA, Class.forName("eu.nexwell.android.nexovision.model.CameraIP"));
            _elTypes.put(EL_TYPE_VIDEOPHONE, Class.forName("eu.nexwell.android.nexovision.model.VideophoneIP"));
            _elTypes.put(EL_TYPE_SET, Class.forName("eu.nexwell.android.nexovision.model.Set"));
            _elTypes.put(EL_TYPE_SCENE, Class.forName("eu.nexwell.android.nexovision.model.Scene"));
            _elTypes.put(EL_TYPE_GEOLOCATIONPOINT, Class.forName("eu.nexwell.android.nexovision.model.GeolocationPoint"));
            _elTypes.put(EL_TYPE_POLYGON, Class.forName("eu.nexwell.android.nexovision.model.Polygon"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Thermometer getMainOutThermometer() {
        if (_mainOutThermometer < 0) {
            return null;
        }
        IElement el = getElementById(Integer.valueOf(_mainOutThermometer));
        return el instanceof Thermometer ? (Thermometer) el : null;
    }

    public static Thermometer getMainInThermometer() {
        if (_mainInThermometer < 0) {
            return null;
        }
        IElement el = getElementById(Integer.valueOf(_mainInThermometer));
        return el instanceof Thermometer ? (Thermometer) el : null;
    }

    public static void setMainOutThermometer(Thermometer t) {
        if (t == null) {
            _mainOutThermometer = -1;
        } else {
            _mainOutThermometer = t.getId().intValue();
        }
    }

    public static void setMainInThermometer(Thermometer t) {
        if (t == null) {
            _mainInThermometer = -1;
        } else {
            _mainInThermometer = t.getId().intValue();
        }
    }

    public static void setMainOutThermometer(int tid) {
        _mainOutThermometer = tid;
    }

    public static void setMainInThermometer(int tid) {
        _mainInThermometer = tid;
    }

    public static void init(Context context) {
        getCategories().clear();
        clearTopElements();
        clearElements();
        Category cat_light = new Category();
        cat_light.setUse(CATEGORY_LIGHT);
        cat_light.setName(context.getString(R.string.ResourceCategoryName_Light));
        addCategory(cat_light);
        addTopElement(cat_light);
        addElement(cat_light);
        Category cat_blinds = new Category();
        cat_blinds.setUse(CATEGORY_BLINDS);
        cat_blinds.setName(context.getString(R.string.ResourceCategoryName_Blinds));
        addCategory(cat_blinds);
        addTopElement(cat_blinds);
        addElement(cat_blinds);
        Category cat_alarm = new Category();
        cat_alarm.setUse(CATEGORY_ALARM);
        cat_alarm.setName(context.getString(R.string.ResourceCategoryName_Alarm));
        addCategory(cat_alarm);
        addTopElement(cat_alarm);
        addElement(cat_alarm);
        Category cat_sensors = new Category();
        cat_sensors.setUse(CATEGORY_SENSORS);
        cat_sensors.setName(context.getString(R.string.ResourceCategoryName_Sensors));
        addCategory(cat_sensors);
        addTopElement(cat_sensors);
        addElement(cat_sensors);
        Category cat_vidip = new Category();
        cat_vidip.setUse(CATEGORY_VIDEOPHONES);
        cat_vidip.setName(context.getString(R.string.ResourceCategoryName_Videophones));
        addCategory(cat_vidip);
        addTopElement(cat_vidip);
        addElement(cat_vidip);
        Category cat_cam = new Category();
        cat_cam.setUse(CATEGORY_CAMERAS);
        cat_cam.setName(context.getString(R.string.ResourceCategoryName_Cameras));
        addCategory(cat_cam);
        addTopElement(cat_cam);
        addElement(cat_cam);
        Category cat_gates = new Category();
        cat_gates.setUse(CATEGORY_GATES);
        cat_gates.setName(context.getString(R.string.ResourceCategoryName_Gates));
        addCategory(cat_gates);
        addTopElement(cat_gates);
        addElement(cat_gates);
        Category cat_temp = new Category();
        cat_temp.setUse(CATEGORY_TEMPERATURE);
        cat_temp.setName(context.getString(R.string.ResourceCategoryName_Temperature));
        addCategory(cat_temp);
        addTopElement(cat_temp);
        addElement(cat_temp);
        Category cat_auto = new Category();
        cat_auto.setUse(CATEGORY_AUTOMATION);
        cat_auto.setName(context.getString(R.string.ResourceCategoryName_Automation));
        addCategory(cat_auto);
        addTopElement(cat_auto);
        addElement(cat_auto);
        Category cat_places = new Category();
        cat_places.setUse(CATEGORY_PLACES);
        cat_places.setName(context.getString(R.string.ResourceCategoryName_Places));
        addCategory(cat_places);
        addTopElement(cat_places);
        addElement(cat_places);
        Category cat_logics = new Category();
        cat_logics.setUse(CATEGORY_LOGICS);
        cat_logics.setName(context.getString(R.string.ResourceCategoryName_Logics));
        addCategory(cat_logics);
        addTopElement(cat_logics);
        addElement(cat_logics);
        Category cat_geoloc = new Category();
        cat_geoloc.setUse(CATEGORY_GEOLOCATION);
        cat_geoloc.setName(context.getString(R.string.ResourceCategoryName_Geolocation));
        addCategory(cat_geoloc);
        addTopElement(cat_geoloc);
        addElement(cat_geoloc);
    }

    public static ArrayList<String> getElementTypes() {
        return new ArrayList(_elTypes.keySet());
    }

    public static String getElementTypeName(Context context, String type) {
        Object obj;
        Class<?> c = (Class) _elTypes.get(type);
        Object o = null;
        if (c != null) {
            try {
                Method m;
                if (type.equals(EL_TYPE_PARTITION24H)) {
                    m = ((Class) _elTypes.get(EL_TYPE_PARTITION)).getDeclaredMethod("getTypeNameResId24h", null);
                    if (m != null) {
                        o = m.invoke(null, null);
                    }
                } else {
                    m = c.getDeclaredMethod("getTypeNameResId", null);
                    if (m != null) {
                        o = m.invoke(null, null);
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                obj = null;
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
                obj = null;
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
                obj = null;
            }
        }
        obj = o;
        if (obj != null) {
            return context.getString(((Integer) obj).intValue());
        }
        return null;
    }

    public static ArrayList<Integer> getElementTypeStatesList(String type) {
        Object obj;
        Class<?> c = (Class) _elTypes.get(type);
        Object o = null;
        if (c != null) {
            try {
                Method m = c.getDeclaredMethod("getStatesList", null);
                if (m != null) {
                    o = m.invoke(null, null);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                obj = null;
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
                obj = null;
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
                obj = null;
            }
        }
        obj = o;
        if (obj != null) {
            return (ArrayList) obj;
        }
        return null;
    }

    public static LinkedHashMap<Integer, Integer> getElementTypeStatesMap(String type) {
        Object obj;
        Class<?> c = (Class) _elTypes.get(type);
        Object o = null;
        if (c != null) {
            try {
                Method m = c.getDeclaredMethod("getStatesMap", null);
                if (m != null) {
                    o = m.invoke(null, null);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                obj = null;
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
                obj = null;
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
                obj = null;
            }
        }
        obj = o;
        if (obj != null) {
            return (LinkedHashMap) obj;
        }
        return null;
    }

    public static ArrayList<String> getElementTypeNames(Context context) {
        ArrayList<String> names = new ArrayList();
        for (String type : _elTypes.keySet()) {
            String type_name = getElementTypeName(context, type);
            if (type_name != null) {
                names.add(type_name);
            }
        }
        return names;
    }

    public static String getElementTypeDefaultCategory(String type) {
        Object obj;
        Class<?> c = (Class) _elTypes.get(type);
        Object o = null;
        if (c != null) {
            try {
                Method m = c.getDeclaredMethod("getDefaultCategory", null);
                if (m != null) {
                    o = m.invoke(null, null);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                obj = null;
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
                obj = null;
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
                obj = null;
            }
        }
        obj = o;
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static IElement newElement(String type) {
        if (_elTypes.get(type) != null) {
            try {
                if (!type.equals(EL_TYPE_PARTITION24H)) {
                    return (IElement) ((Class) _elTypes.get(type)).getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                return (IElement) ((Class) _elTypes.get(EL_TYPE_PARTITION)).getConstructor(new Class[]{Function.class}).newInstance(new Object[]{Function.FIRE24H});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (InstantiationException e3) {
                e3.printStackTrace();
            } catch (InvocationTargetException e4) {
                e4.printStackTrace();
            }
        }
        return null;
    }

    public static void addElement(IElement el) {
        _elements.add(el);
    }

    public static IElement getElementById(Integer id) {
        Iterator<IElement> itre = getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (el.getId().equals(id)) {
                return el;
            }
        }
        return null;
    }

    public static ArrayList<IElement> getElements() {
        return _elements;
    }

    public static void removeElement(IElement el, boolean completeRemoval) {
        Iterator<IElement> itrc = getElements().iterator();
        while (itrc.hasNext()) {
            IElement c = (IElement) itrc.next();
            if (c instanceof ISet) {
                if (!(c instanceof Category)) {
                    ((ISet) c).removeElement(el);
                } else if (completeRemoval || ((Category) c).getType().equals(CATEGORY_PLACES)) {
                    ((ISet) c).removeElement(el);
                    if (el instanceof Thermometer) {
                        if (el.getId().intValue() == _mainOutThermometer) {
                            _mainOutThermometer = -1;
                        } else if (el.getId().intValue() == _mainInThermometer) {
                            _mainInThermometer = -1;
                        }
                    }
                }
            }
        }
        if (completeRemoval) {
            _elements.remove(el);
        }
    }

    public static void clearElements() {
        _elements.clear();
        _mainOutThermometer = -1;
        _mainInThermometer = -1;
    }

    public static void addTopElement(IElement el) {
        _topElements.add(el);
    }

    public static void removeTopElement(IElement el) {
        _topElements.remove(el);
    }

    public static ArrayList<IElement> getTopElements() {
        return _topElements;
    }

    public static void clearTopElements() {
        _topElements.clear();
    }

    public static ArrayList<IElement> getElementsByType(String type) {
        ArrayList<IElement> els = new ArrayList();
        Iterator<IElement> itre = getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (!(el == null || el.getType() == null || !el.getType().equals(type))) {
                els.add(el);
            }
        }
        return els;
    }

    public static ArrayList<ISet> getSetsByElementId(int id) {
        ArrayList<ISet> sets = new ArrayList();
        Iterator<IElement> itre = getElementsByType(EL_TYPE_SET).iterator();
        while (itre.hasNext()) {
            ISet set = (ISet) itre.next();
            if (!(set == null || set.getElements() == null || !set.getOrder().contains(Integer.valueOf(id)))) {
                sets.add(set);
            }
        }
        return sets;
    }

    public static ArrayList<String> getElementNamesByType(String type) {
        ArrayList<String> els = new ArrayList();
        Iterator<IElement> itre = getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (el.getType() != null && el.getType().equals(type)) {
                els.add(el.getName());
            }
        }
        return els;
    }

    public static ArrayList<IElement> getCurrentElements() {
        IElement el;
        ArrayList<IElement> elements;
        if (MainActivity.CURR_SET_ID > 0) {
            el = getElementById(Integer.valueOf(MainActivity.CURR_SET_ID));
            if (el instanceof ISet) {
                elements = new ArrayList();
                Iterator<IElement> itre = ((ISet) el).getElements().iterator();
                IElement e;
                if ((el instanceof Category) && ((Category) el).getUse().equals(CATEGORY_PLACES)) {
                    while (itre.hasNext()) {
                        e = (IElement) itre.next();
                        if (e instanceof ISet) {
                            if (((ISet) e).getThermometer() != null) {
                                elements.add(((ISet) e).getThermometer());
                            }
                            elements.addAll(((ISet) e).getLights());
                        } else {
                            elements.add(e);
                        }
                        if ((e instanceof Thermometer) && ((Thermometer) e).getThermostat() != null) {
                            elements.add(((Thermometer) e).getThermostat());
                        }
                    }
                    return elements;
                } else if ((el instanceof Category) && ((Category) el).getUse().equals(CATEGORY_ALARM)) {
                    while (itre.hasNext()) {
                        e = (IElement) itre.next();
                        if (e instanceof Partition) {
                            elements.add(e);
                        }
                    }
                    return elements;
                } else {
                    while (itre.hasNext()) {
                        e = (IElement) itre.next();
                        elements.add(e);
                        if ((e instanceof Thermometer) && ((Thermometer) e).getThermostat() != null) {
                            elements.add(((Thermometer) e).getThermostat());
                        }
                    }
                    return elements;
                }
            } else if (!(el instanceof Partition)) {
                return null;
            } else {
                elements = new ArrayList();
                Iterator<IElement> itrs = ((Partition) el).getSensors().iterator();
                while (itrs.hasNext()) {
                    elements.add((IElement) itrs.next());
                }
                return elements;
            }
        }
        elements = getCategory(CATEGORY_LIGHT).getElements();
        Iterator<IElement> itrt = getCategory(CATEGORY_TEMPERATURE).getElements().iterator();
        while (itrt.hasNext()) {
            el = (IElement) itrt.next();
            if (el instanceof Thermometer) {
                elements.add(el);
            }
        }
        return elements;
    }

    public static int findFirstFreeElementId() {
        ArrayList<Integer> usedIDs = new ArrayList();
        Iterator<IElement> itre = _elements.iterator();
        while (itre.hasNext()) {
            usedIDs.add(((IElement) itre.next()).getId());
        }
        Collections.sort(usedIDs);
        int i = 0;
        while (i < usedIDs.size()) {
            if (((Integer) usedIDs.get(i)).intValue() - 1 > i) {
                return i + 1;
            }
            i++;
        }
        return i + 1;
    }

    public static void addCategory(Category cat) {
        _categories.add(cat);
    }

    public static void setCategories(ArrayList<Category> cats) {
        _categories = cats;
    }

    public static Category getCategory(String use) {
        if (_categories == null || _categories.isEmpty() || use == null) {
            return null;
        }
        Iterator<Category> itrc = _categories.iterator();
        while (itrc.hasNext()) {
            Category cat = (Category) itrc.next();
            if (cat != null && cat.getUse() != null && cat.getUse().equals(use)) {
                return cat;
            }
        }
        return null;
    }

    public static ArrayList<Category> getCategories() {
        return _categories;
    }

    public static ArrayList<String> getCategoriesNames() {
        ArrayList<String> cat_names = new ArrayList();
        Iterator<Category> itrc = _categories.iterator();
        while (itrc.hasNext()) {
            cat_names.add(((Category) itrc.next()).getName());
        }
        return cat_names;
    }

    public static ArrayList<Category> getCategoriesByElementId(int id) {
        ArrayList<Category> cats = new ArrayList();
        Iterator<Category> itrc = _categories.iterator();
        while (itrc.hasNext()) {
            Category cat = (Category) itrc.next();
            if (!(cat == null || cat.getElements() == null || !cat.getOrder().contains(Integer.valueOf(id)))) {
                cats.add(cat);
            }
        }
        return cats;
    }

    public static void setAllSwsUpdated(boolean updated) {
        Iterator<IElement> itre = getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (Switch.class.isInstance(el)) {
                ((Switch) el).setUpdated(updated);
            }
        }
    }

    public static void fixModel(Context context) {
        int i;
        for (i = 0; i < getElements().size(); i++) {
            Category cat;
            IElement el = (IElement) getElements().get(i);
            if (el.getId().intValue() < 0) {
                el.setId(findFirstFreeElementId());
            }
            if (!(el instanceof Category)) {
                ArrayList<Category> cats = getCategoriesByElementId(el.getId().intValue());
                if (cats == null || cats.size() < 1) {
                    cat = getCategory(CATEGORY_AUTOMATION);
                    if (cat != null) {
                        cat.addElement(el);
                    }
                }
            }
        }
        for (i = 0; i < getCategories().size(); i++) {
            cat = (Category) getCategories().get(i);
            ArrayList<Integer> new_order = new ArrayList();
            Iterator it = cat.getOrder().iterator();
            while (it.hasNext()) {
                Integer ID = (Integer) it.next();
                if (getElementById(ID) != null) {
                    new_order.add(ID);
                }
            }
            cat.setOrder((ArrayList) new_order);
        }
        for (i = 0; i < getElementsByType(EL_TYPE_SET).size(); i++) {
            ISet set = (ISet) getElementsByType(EL_TYPE_SET).get(i);
            new_order = new ArrayList();
            it = set.getOrder().iterator();
            while (it.hasNext()) {
                ID = (Integer) it.next();
                if (getElementById(ID) != null) {
                    new_order.add(ID);
                }
            }
            set.setOrder((ArrayList) new_order);
            Log.d("NVModel", "Fix: Set:" + ((IElement) set).getName());
            if (!(set.getWallpaper() == null || set.getWallpaper().isEmpty())) {
                Log.d("NVModel", "Fix: Set:" + ((IElement) set).getName() + " 1(" + set.getWallpaper() + ")");
                set.setWallpaper(new File(set.getWallpaper()).getName(), context);
                Log.d("NVModel", "Fix: Set:" + ((IElement) set).getName() + " 2(" + set.getWallpaper() + ")");
            }
        }
        File defaultProjectDir = new File(XMLProject.defaultProject).getParentFile();
        Log.d("NVModel", "Fix: RemoveUnusedImageFiles from " + defaultProjectDir.getPath());
        for (File file : defaultProjectDir.listFiles()) {
            if (file.isFile()) {
                String filename = file.getName();
                Log.d("NVModel", "Fix: RemoveUnusedImageFiles: " + filename);
                if (filename.contains("wallpaper_id")) {
                    String id_str = filename.substring(12, filename.indexOf(46));
                    Log.d("NVModel", "Fix: RemoveUnusedImageFiles: " + filename + " id=" + id_str);
                    if (!(id_str == null || id_str.isEmpty())) {
                        try {
                            el = getElementById(Integer.valueOf(Integer.parseInt(id_str)));
                            if (el == null || !(el instanceof Set) || ((Set) el).getWallpaper() == null || ((Set) el).getWallpaper().isEmpty()) {
                                Log.d("NVModel", "Fix: RemoveUnusedImageFiles: " + filename + " id=" + id_str + " DELETE");
                                XMLProject.deleteRecursive(file);
                            }
                        } catch (NumberFormatException e) {
                            Log.d("NVModel", "Fix: RemoveInvalidImageFiles: " + filename + " id=" + id_str + " DELETE");
                            XMLProject.deleteRecursive(file);
                        }
                    }
                }
            }
        }
        Iterator<IElement> itrp = getElementsByType(EL_TYPE_PARTITION).iterator();
        while (itrp.hasNext()) {
            el = (IElement) itrp.next();
            if (el instanceof Partition) {
                ((Partition) el).updateSensors();
            }
        }
        Iterator<IElement> itrp24 = getElementsByType(EL_TYPE_PARTITION24H).iterator();
        while (itrp24.hasNext()) {
            el = (IElement) itrp24.next();
            if (el instanceof Partition) {
                ((Partition) el).updateSensors();
            }
        }
    }

    public static String toXML() {
        StringBuffer sb = new StringBuffer();
        Iterator<Category> itrc = _categories.iterator();
        while (itrc.hasNext()) {
            Category cat = (Category) itrc.next();
            if (cat != null) {
                sb.append(cat.toXML(null));
            }
        }
        Iterator<IElement> itrs = getElementsByType(EL_TYPE_SET).iterator();
        while (itrs.hasNext()) {
            ISet set = (ISet) itrs.next();
            if (!(set == null || (set instanceof Category))) {
                sb.append(set.toXML(null));
            }
        }
        Iterator<IElement> itre = getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (!(el == null || (el instanceof ISet))) {
                if (getCategoriesByElementId(el.getId().intValue()).size() > 0 || getSetsByElementId(el.getId().intValue()).size() > 0) {
                    sb.append(el.toXML(null));
                }
            }
        }
        return sb.toString();
    }
}
