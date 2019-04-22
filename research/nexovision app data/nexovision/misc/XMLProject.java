package eu.nexwell.android.nexovision.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import eu.nexwell.android.nexovision.model.AnalogOutput;
import eu.nexwell.android.nexovision.model.AnalogOutputGroup;
import eu.nexwell.android.nexovision.model.AnalogSensor;
import eu.nexwell.android.nexovision.model.Blind;
import eu.nexwell.android.nexovision.model.CameraIP;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.GeolocationPoint;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.Logic;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Output;
import eu.nexwell.android.nexovision.model.OutputGroup;
import eu.nexwell.android.nexovision.model.Partition;
import eu.nexwell.android.nexovision.model.Polygon;
import eu.nexwell.android.nexovision.model.Scene;
import eu.nexwell.android.nexovision.model.Sensor;
import eu.nexwell.android.nexovision.model.Switch;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.Thermostat;
import eu.nexwell.android.nexovision.model.ThermostatGroup;
import eu.nexwell.android.nexovision.model.VideophoneIP;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLProject {
    public static String defaultCamshotsPath = null;
    public static String defaultImagesPath = null;
    public static String defaultProject = null;
    public static String defaultProjectsPath = null;
    public static boolean initDone = false;
    private static Context lastContext = null;
    private static SharedPreferences sharedPrefs;

    public static Context getLastContext() {
        return lastContext;
    }

    public static void initModel(Context context) {
        initModel(context, false);
    }

    public static void initModel(Context context, boolean forceReload) {
        lastContext = context;
        if (forceReload) {
            initDone = false;
        }
        if (!initDone) {
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String projects_path = null;
            String camshots_path = null;
            String images_path = null;
            if (context.getExternalFilesDir(null).exists()) {
                defaultProjectsPath = context.getExternalFilesDir(null).getPath() + File.separator + "projects";
                defaultCamshotsPath = context.getExternalFilesDir(null).getPath() + File.separator + "camshots";
                defaultImagesPath = context.getExternalFilesDir(null).getPath() + File.separator + "images";
                if (context.getFilesDir().exists()) {
                    String old_defaultProjectsPath = context.getFilesDir().getPath() + File.separator + "projects";
                    String old_defaultCamshotsPath = context.getFilesDir().getPath() + File.separator + "camshots";
                    String old_defaultImagesPath = context.getFilesDir().getPath() + File.separator + "images";
                    if (new File(old_defaultProjectsPath).isDirectory()) {
                        Log.d("MainActivity", "File(old_defaultProjectsPath).exists()");
                        copyFileOrDirectory(old_defaultProjectsPath, context.getExternalFilesDir(null).getPath());
                        deleteRecursive(new File(old_defaultProjectsPath));
                        if (new File(old_defaultProjectsPath).isDirectory()) {
                            Log.e("MainActivity", "ERROR: File(old_defaultProjectsPath).exists()");
                        } else {
                            Log.d("MainActivity", "OK: !File(old_defaultProjectsPath).exists()");
                        }
                    }
                    if (new File(old_defaultImagesPath).isDirectory()) {
                        Log.d("MainActivity", "File(old_defaultImagesPath).exists()");
                        copyFileOrDirectory(old_defaultImagesPath, context.getExternalFilesDir(null).getPath());
                        deleteRecursive(new File(old_defaultImagesPath));
                    }
                    if (new File(old_defaultCamshotsPath).isDirectory()) {
                        Log.d("MainActivity", "File(old_defaultCamshotsPath).exists()");
                        copyFileOrDirectory(old_defaultCamshotsPath, context.getExternalFilesDir(null).getPath());
                        deleteRecursive(new File(old_defaultCamshotsPath));
                    }
                    projects_path = defaultProjectsPath;
                    camshots_path = defaultCamshotsPath;
                    images_path = defaultImagesPath;
                } else {
                    projects_path = sharedPrefs.getString("pref_systemprojectspath", defaultProjectsPath);
                    camshots_path = sharedPrefs.getString("pref_systemcamshotspath", defaultCamshotsPath);
                    images_path = sharedPrefs.getString("pref_systemimagespath", defaultImagesPath);
                }
            } else if (context.getFilesDir().exists()) {
                defaultProjectsPath = context.getFilesDir().getPath() + File.separator + "projects";
                defaultCamshotsPath = context.getFilesDir().getPath() + File.separator + "camshots";
                defaultImagesPath = context.getFilesDir().getPath() + File.separator + "images";
                projects_path = sharedPrefs.getString("pref_systemprojectspath", defaultProjectsPath);
                camshots_path = sharedPrefs.getString("pref_systemcamshotspath", defaultCamshotsPath);
                images_path = sharedPrefs.getString("pref_systemimagespath", defaultImagesPath);
            }
            if (!(projects_path == null || new File(projects_path).exists())) {
                new File(projects_path).mkdirs();
            }
            sharedPrefs.edit().putString("pref_systemprojectspath", projects_path).commit();
            if (!(camshots_path == null || new File(camshots_path).exists())) {
                new File(camshots_path).mkdirs();
            }
            sharedPrefs.edit().putString("pref_systemcamshotspath", camshots_path).commit();
            if (!(images_path == null || new File(images_path).exists())) {
                new File(images_path).mkdirs();
            }
            sharedPrefs.edit().putString("pref_systemimagespath", images_path).commit();
            for (File file : new File(projects_path).listFiles()) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    int pos = fileName.lastIndexOf(".");
                    if (pos > 0) {
                        fileName = fileName.substring(0, pos);
                    }
                    new File(projects_path + File.separator + fileName).mkdirs();
                    copyFileOrDirectory(file.getPath(), projects_path + File.separator + fileName);
                    new File(projects_path + File.separator + fileName + File.separator + file.getName()).renameTo(new File(projects_path + File.separator + fileName + File.separator + "nexoproject.xml"));
                    deleteRecursive(file);
                }
            }
            File imagesDir = new File(images_path);
            if (imagesDir.isDirectory()) {
                for (File dir : new File(projects_path).listFiles()) {
                    if (dir.isDirectory()) {
                        for (File file2 : imagesDir.listFiles()) {
                            if (file2.isFile()) {
                                copyFileOrDirectory(file2.getPath(), dir.getPath());
                            }
                        }
                    }
                }
                for (File file22 : imagesDir.listFiles()) {
                    if (file22.isFile()) {
                        deleteRecursive(file22);
                    }
                }
            }
            defaultProject = sharedPrefs.getString("pref_systemlastproject", sharedPrefs.getString("pref_systemprojectspath", defaultProjectsPath) + File.separator + "default" + File.separator + "nexoproject.xml");
            sharedPrefs.edit().putString("pref_systemlastproject", defaultProject).commit();
            if (defaultProject == null || defaultProject.isEmpty() || !new File(defaultProject).exists()) {
                defaultProject = sharedPrefs.getString("pref_systemprojectspath", defaultProjectsPath) + File.separator + "default" + File.separator + "nexoproject.xml";
            }
            parse(defaultProject, context);
            initDone = true;
        }
    }

    public static void copyFileOrDirectory(String srcDir, String dstDir) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());
            if (src.isDirectory()) {
                for (String file : src.list()) {
                    copyFileOrDirectory(new File(src, file).getPath(), dst.getPath());
                }
                return;
            }
            copyFile(src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        return fileOrDirectory.delete();
    }

    public static void parse(String file, Context context) {
        File f = new File(file);
        if (f.exists()) {
            try {
                NVModel.init(context);
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
                doc.getDocumentElement().normalize();
                NodeList nodes = doc.getDocumentElement().getChildNodes();
                for (int n = 0; n < nodes.getLength(); n++) {
                    if (nodes.item(n).getNodeType() == (short) 1) {
                        Element el = (Element) nodes.item(n);
                        if (el.getNodeName().equals("element")) {
                            Log.d("XMLProject.parse", "node=" + el.getAttribute("id"));
                            if (el.getAttribute("type") != null && (NVModel.getElementTypes().contains(el.getAttribute("type")) || el.getAttribute("type").equals(NVModel.EL_TYPE_CATEGORY))) {
                                Log.d("XMLProject.parse", "type=" + el.getAttribute("id"));
                                IElement element = null;
                                if (el.getAttribute("type").equals(NVModel.EL_TYPE_CATEGORY)) {
                                    Log.d("XMLProject.parse", "category=" + el.getAttribute("id"));
                                    if (!(el.getAttribute("use") == null || el.getAttribute("use").isEmpty())) {
                                        Category cat = NVModel.getCategory(el.getAttribute("use"));
                                        if (cat != null) {
                                            if (!(el.getAttribute("order") == null || el.getAttribute("order").isEmpty())) {
                                                cat.setOrder(el.getAttribute("order"));
                                                Log.d("XMLProject.parse", "set order for cat=" + cat.getUse() + ", name=" + cat.getName() + " order=" + el.getAttribute("order"));
                                            }
                                            if (!(!cat.getUse().equals(NVModel.CATEGORY_TEMPERATURE) || el.getAttribute("data") == null || el.getAttribute("data").isEmpty())) {
                                                String[] data = el.getAttribute("data").split(",");
                                                if (data.length <= 0 || data[0] == null || data[0].isEmpty()) {
                                                    NVModel.setMainOutThermometer(-1);
                                                } else {
                                                    NVModel.setMainOutThermometer(Integer.parseInt(data[0]));
                                                }
                                                if (data.length <= 1 || data[1] == null || data[1].isEmpty()) {
                                                    NVModel.setMainInThermometer(-1);
                                                } else {
                                                    NVModel.setMainInThermometer(Integer.parseInt(data[1]));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    element = NVModel.newElement(el.getAttribute("type"));
                                }
                                if (!(element == null || el.getAttribute("name") == null || el.getAttribute("name").isEmpty())) {
                                    element.setName(el.getAttribute("name"));
                                    NVModel.addElement(element);
                                    if (!(el.getAttribute("id") == null || el.getAttribute("id").isEmpty())) {
                                        element.setId(Integer.parseInt(el.getAttribute("id")));
                                    }
                                    ArrayList<Category> cats_list = NVModel.getCategoriesByElementId(element.getId().intValue());
                                    Iterator<Category> itrc = cats_list.iterator();
                                    while (itrc.hasNext()) {
                                        ((Category) itrc.next()).addElement(element);
                                    }
                                    Log.d("XMLProject.parse", "<element type=\"" + element.getType() + "\" id=\"" + element.getId() + "\" name=\"" + element.getName() + "\" cats=\"" + cats_list.size() + "\"...>");
                                    Iterator<ISet> itrg = NVModel.getSetsByElementId(element.getId().intValue()).iterator();
                                    while (itrg.hasNext()) {
                                        ((ISet) itrg.next()).addElement(element);
                                    }
                                    if (element instanceof ISet) {
                                        if (el.getAttribute("icon") == null || el.getAttribute("icon").isEmpty()) {
                                        }
                                        if (!(el.getAttribute("image") == null || el.getAttribute("image").isEmpty())) {
                                            ((ISet) element).setWallpaper(el.getAttribute("image"), context);
                                        }
                                        if (!(el.getAttribute("order") == null || el.getAttribute("order").isEmpty())) {
                                            ((ISet) element).setOrder(el.getAttribute("order"));
                                        }
                                        if (!(el.getAttribute("coordinates") == null || el.getAttribute("coordinates").isEmpty())) {
                                            ((ISet) element).setCoordinates(el.getAttribute("coordinates"));
                                        }
                                        if (!(el.getAttribute("thermometer") == null || el.getAttribute("thermometer").isEmpty())) {
                                            ((ISet) element).setThermometer(Integer.parseInt(el.getAttribute("thermometer")));
                                        }
                                    } else if (element instanceof CameraIP) {
                                        if (el.getAttribute("addr") != null) {
                                            ((CameraIP) element).setAddress(el.getAttribute("addr"));
                                        }
                                        if (el.getAttribute("size_x") != null && el.getAttribute("size_x").length() > 0 && el.getAttribute("size_y") != null && el.getAttribute("size_y").length() > 0) {
                                            ((CameraIP) element).setSize(new Point(Integer.parseInt(el.getAttribute("size_x")), Integer.parseInt(el.getAttribute("size_y"))));
                                        }
                                    } else if (element instanceof VideophoneIP) {
                                        if (el.getAttribute("icon") == null || el.getAttribute("icon").isEmpty()) {
                                        }
                                        if (el.getAttribute("addr") != null) {
                                            ((VideophoneIP) element).setAddress(el.getAttribute("addr"));
                                        }
                                        if (el.getAttribute("sip_proxy") != null) {
                                            ((VideophoneIP) element).setSipProxy(el.getAttribute("sip_proxy"));
                                        }
                                    } else if ((element instanceof ISwitch) && el.getAttribute("res") != null && !el.getAttribute("res").isEmpty()) {
                                        ((Switch) element).setResource(el.getAttribute("res"));
                                        if (element instanceof Output) {
                                            if (el.getAttribute("icon_on") == null || el.getAttribute("icon_on").isEmpty()) {
                                            }
                                            if (el.getAttribute("icon_off") == null || !el.getAttribute("icon_off").isEmpty()) {
                                            }
                                            if (!(el.getAttribute("func") == null || el.getAttribute("func").isEmpty())) {
                                                ((Output) element).setFunc(el.getAttribute("func"));
                                            }
                                        } else if (element instanceof OutputGroup) {
                                            if (el.getAttribute("icon_on") == null || !el.getAttribute("icon_on").isEmpty()) {
                                            }
                                            if (!(el.getAttribute("icon_off") == null || el.getAttribute("icon_off").isEmpty())) {
                                            }
                                        } else if (element instanceof AnalogOutput) {
                                            if (el.getAttribute("icon_on") == null || el.getAttribute("icon_on").isEmpty()) {
                                            }
                                            if (!(el.getAttribute("icon_off") == null || el.getAttribute("icon_off").isEmpty())) {
                                            }
                                        } else if (element instanceof AnalogOutputGroup) {
                                            if (el.getAttribute("icon_on") == null || !el.getAttribute("icon_on").isEmpty()) {
                                            }
                                            if (!(el.getAttribute("icon_off") == null || el.getAttribute("icon_off").isEmpty())) {
                                            }
                                        } else if (element instanceof Sensor) {
                                            if (el.getAttribute("icon_affected") == null || el.getAttribute("icon_affected").isEmpty()) {
                                            }
                                            if (!(el.getAttribute("icon_notaffected") == null || el.getAttribute("icon_notaffected").isEmpty())) {
                                            }
                                        } else if (element instanceof Blind) {
                                            if (!(el.getAttribute("invert_logic") == null || el.getAttribute("invert_logic").isEmpty())) {
                                                ((Blind) element).setInvertedLogic(Boolean.parseBoolean(el.getAttribute("invert_logic").toLowerCase()));
                                            }
                                        } else if (element instanceof AnalogSensor) {
                                            if (!(el.getAttribute("icon") == null || el.getAttribute("icon").isEmpty())) {
                                            }
                                        } else if (element instanceof Partition) {
                                            if (el.getAttribute("icon_armed") == null || !el.getAttribute("icon_armed").isEmpty()) {
                                            }
                                            if (el.getAttribute("icon_disarmed") == null || !el.getAttribute("icon_disarmed").isEmpty()) {
                                            }
                                            if (el.getAttribute("icon_alarming") == null || !el.getAttribute("icon_alarming").isEmpty()) {
                                            }
                                            if (!(el.getAttribute("func") == null || el.getAttribute("func").isEmpty())) {
                                                ((Partition) element).setFunc(el.getAttribute("func"));
                                            }
                                            if (!(el.getAttribute("sensors") == null || el.getAttribute("sensors").isEmpty())) {
                                                ((Partition) element).clearSensors();
                                                ((Partition) element).addSensors(el.getAttribute("sensors"));
                                            }
                                        } else if (element instanceof Thermometer) {
                                            if (!(el.getAttribute("icon") == null || el.getAttribute("icon").isEmpty())) {
                                            }
                                        } else if (element instanceof Thermostat) {
                                            if (el.getAttribute("icon_on") == null || !el.getAttribute("icon_on").isEmpty()) {
                                            }
                                            if (el.getAttribute("icon_off") == null || !el.getAttribute("icon_off").isEmpty()) {
                                            }
                                            if (el.getAttribute("icon_inactive") == null || !el.getAttribute("icon_inactive").isEmpty()) {
                                            }
                                            if (!(el.getAttribute("min") == null || el.getAttribute("min").isEmpty())) {
                                                ((Thermostat) element).setMin(Float.valueOf(Float.parseFloat(el.getAttribute("min"))));
                                            }
                                            if (!(el.getAttribute("max") == null || el.getAttribute("max").isEmpty())) {
                                                ((Thermostat) element).setMax(Float.valueOf(Float.parseFloat(el.getAttribute("max"))));
                                            }
                                            if (!(el.getAttribute("thermometer") == null || el.getAttribute("thermometer").isEmpty())) {
                                                IElement e = NVModel.getElementById(Integer.valueOf(Integer.parseInt(el.getAttribute("thermometer"))));
                                                if (e instanceof Thermometer) {
                                                    ((Thermometer) e).setThermostat((Thermostat) element);
                                                }
                                            }
                                        } else if (element instanceof ThermostatGroup) {
                                            if (el.getAttribute("icon_on") == null || el.getAttribute("icon_on").isEmpty()) {
                                            }
                                            if (el.getAttribute("icon_off") == null || !el.getAttribute("icon_off").isEmpty()) {
                                            }
                                            if (el.getAttribute("icon_inactive") == null || !el.getAttribute("icon_inactive").isEmpty()) {
                                            }
                                            if (el.getAttribute("min") != null) {
                                                ((ThermostatGroup) element).setMin(Float.valueOf(Float.parseFloat(el.getAttribute("min"))));
                                            }
                                            if (el.getAttribute("max") != null) {
                                                ((ThermostatGroup) element).setMax(Float.valueOf(Float.parseFloat(el.getAttribute("max"))));
                                            }
                                        }
                                    } else if (element instanceof Logic) {
                                        if (el.getAttribute("event1") != null) {
                                            ((Logic) element).setEvent1(el.getAttribute("event1"));
                                        }
                                        if (el.getAttribute("action1") != null) {
                                            ((Logic) element).setAction1(el.getAttribute("action1"));
                                        }
                                        if (el.getAttribute("state1_label") != null) {
                                            ((Logic) element).setState1Label(el.getAttribute("state1_label"));
                                        }
                                        if (el.getAttribute("event2") != null) {
                                            ((Logic) element).setEvent2(el.getAttribute("event2"));
                                        }
                                        if (el.getAttribute("action2") != null) {
                                            ((Logic) element).setAction2(el.getAttribute("action2"));
                                        }
                                        if (el.getAttribute("state2_label") != null) {
                                            ((Logic) element).setState2Label(el.getAttribute("state2_label"));
                                        }
                                    } else if (element instanceof GeolocationPoint) {
                                        if (el.getAttribute("enter_message") != null) {
                                            ((GeolocationPoint) element).setOnEnterMessage(el.getAttribute("enter_message"));
                                        }
                                        if (el.getAttribute("exit_message") != null) {
                                            ((GeolocationPoint) element).setOnExitMessage(el.getAttribute("exit_message"));
                                        }
                                        if (!(el.getAttribute("enter_logic_id") == null || el.getAttribute("enter_logic_id").isEmpty())) {
                                            ((GeolocationPoint) element).setOnEnterLogic(Integer.valueOf(Integer.parseInt(el.getAttribute("enter_logic_id"))));
                                        }
                                        if (!(el.getAttribute("exit_logic_id") == null || el.getAttribute("exit_logic_id").isEmpty())) {
                                            ((GeolocationPoint) element).setOnExitLogic(Integer.valueOf(Integer.parseInt(el.getAttribute("exit_logic_id"))));
                                        }
                                        if (!(el.getAttribute("radius") == null || el.getAttribute("radius").isEmpty())) {
                                            ((GeolocationPoint) element).setRadius(Integer.parseInt(el.getAttribute("radius")));
                                        }
                                        if (!(el.getAttribute("latitude") == null || el.getAttribute("latitude").isEmpty() || el.getAttribute("longitude") == null || el.getAttribute("longitude").isEmpty() || el.getAttribute("accuracy") == null || el.getAttribute("accuracy").isEmpty())) {
                                            Location location = new Location("L");
                                            location.setLatitude(Double.parseDouble(el.getAttribute("latitude")));
                                            location.setLongitude(Double.parseDouble(el.getAttribute("longitude")));
                                            location.setAccuracy(Float.parseFloat(el.getAttribute("accuracy")));
                                            ((GeolocationPoint) element).setLocation(location);
                                        }
                                    } else if (element instanceof Scene) {
                                        if (!(el.getAttribute("elstates") == null || el.getAttribute("elstates").isEmpty())) {
                                            ((Scene) element).createFromProject(el.getAttribute("elstates").split(","));
                                        }
                                    } else if (!(!(element instanceof Polygon) || el.getAttribute("points") == null || el.getAttribute("points").isEmpty())) {
                                        ((Polygon) element).setPoints(el.getAttribute("points"));
                                    }
                                    if (NVModel.getCategoriesByElementId(element.getId().intValue()).size() < 0 && NVModel.getSetsByElementId(element.getId().intValue()).size() < 0) {
                                        NVModel.removeElement(element, true);
                                    }
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                }
                NVModel.fixModel(context);
                return;
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
                return;
            } catch (SAXException e2) {
                e2.printStackTrace();
                return;
            } catch (IOException e3) {
                e3.printStackTrace();
                return;
            }
        }
        newProject(file);
        parse(file, context);
    }

    public static boolean write(String file) {
        File f = new File(file);
        if (f.exists()) {
            return XMLProjectWriter.write(file);
        }
        try {
            f.createNewFile();
            return XMLProjectWriter.write(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean newProject(String file) {
        File f = new File(file);
        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
                XMLProjectWriter.createEmpty(file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static ArrayList<String> getProjectsList(Context context, boolean exclude_loaded_project) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<String> FL = new ArrayList();
        if (sharedPreferences != null) {
            File file = new File(sharedPreferences.getString("pref_systemprojectspath", defaultProjectsPath) + File.separator);
            if (file.exists()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File name : files) {
                        String tmpstr = name.getName();
                        if (tmpstr != null) {
                            if (!exclude_loaded_project) {
                                FL.add(tmpstr);
                            } else if (defaultProject == null || !defaultProject.matches(sharedPreferences.getString("pref_systemprojectspath", defaultProjectsPath) + File.separator + tmpstr + File.separator + tmpstr + ".xml")) {
                                FL.add(tmpstr);
                            }
                        }
                    }
                }
            }
        }
        return FL;
    }
}
