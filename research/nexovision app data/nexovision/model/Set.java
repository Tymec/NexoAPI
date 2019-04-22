package eu.nexwell.android.nexovision.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import eu.nexwell.android.nexovision.misc.XMLProject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class Set extends Element implements ISet {
    private static String _defaultCategory;
    private static Integer _typeNameResId;
    private Bitmap _bitmap;
    private HashMap<Integer, Point> _coordinates = new HashMap();
    private ArrayList<IElement> _elements = new ArrayList();
    private int _iconssize;
    private ArrayList<Integer> _order = new ArrayList();
    private int _thermometer;
    private String _wallpaper;

    private class ElementPositionCompartor implements Comparator<IElement> {
        private ElementPositionCompartor() {
        }

        public int compare(IElement a, IElement b) {
            if (a == null) {
                return -1;
            }
            if (b == null) {
                return 1;
            }
            return Integer.valueOf(Set.this._order.indexOf(a.getId())).compareTo(Integer.valueOf(Set.this._order.indexOf(b.getId())));
        }
    }

    public Set() {
        setType(NVModel.EL_TYPE_SET);
        _defaultCategory = NVModel.CATEGORY_PLACES;
        _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Set);
        this._iconssize = 2;
    }

    public void setIconsSize(int is) {
        this._iconssize = is;
    }

    public int getIconsSize() {
        return this._iconssize;
    }

    public void setOrder(ArrayList<Integer> order) {
        this._order = order;
    }

    public void setOrder(String order) {
        String[] array = order.split(",");
        if (order != null && array.length > 0) {
            this._order = new ArrayList();
            for (String el : array) {
                this._order.add(Integer.valueOf(Integer.parseInt(el)));
            }
        }
    }

    public ArrayList<Integer> getOrder() {
        return this._order;
    }

    public void setCoordinates(String coordinates) {
        String[] array = coordinates.split(",");
        if (array.length > 0) {
            Log.d("Set", "setCoordinates(size=" + array.length + ")");
            this._coordinates = new HashMap();
            int i = 0;
            for (String coords : array) {
                String[] coord = coords.split(":");
                int x = 0;
                int y = 0;
                if (coord != null && coord.length > 1) {
                    x = Integer.parseInt(coord[0]);
                    y = Integer.parseInt(coord[1]);
                }
                Log.d("Set", "setCoordinates(x=" + x + ", y=" + y + ", [i=" + i + ", order=" + this._order.get(i) + "])");
                Integer id = (Integer) this._order.get(i);
                if (id != null) {
                    Log.d("Set", "setCoordinates(el=" + id + ", x=" + x + ", y=" + y + ")");
                    this._coordinates.put(id, new Point(x, y));
                }
                i++;
            }
        }
    }

    public void setCoordinatesForElement(IElement el, Point p) {
        this._coordinates.put(el.getId(), p);
    }

    public Point getCoordinatesOfElement(IElement el) {
        return (Point) this._coordinates.get(el.getId());
    }

    public void setWallpaper(String wallpaper, Context context) {
        this._wallpaper = wallpaper;
        if (this._wallpaper != null && !this._wallpaper.isEmpty() && new File(new File(XMLProject.defaultProject).getParent() + File.separator + this._wallpaper).exists()) {
            Log.d("Model: Set", "LOAD file: " + new File(XMLProject.defaultProject).getParent() + File.separator + this._wallpaper);
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenwidth = size.x;
            int screenheight = size.y;
            Options options = new Options();
            options.inPreferredConfig = Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(new File(XMLProject.defaultProject).getParent() + File.separator + this._wallpaper, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options, screenwidth, screenheight);
            Bitmap b = BitmapFactory.decodeFile(new File(XMLProject.defaultProject).getParent() + File.separator + this._wallpaper, options);
            if (b != null) {
                Matrix matrix = new Matrix();
                matrix.postRotate((float) getImageAngle(new File(XMLProject.defaultProject).getParent() + File.separator + this._wallpaper));
                this._bitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            }
        }
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = Math.max(options.outHeight, options.outWidth);
        int width = Math.min(options.outHeight, options.outWidth);
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private int getImageAngle(String filePath) {
        int rotationInDegrees = 0;
        try {
            return exifToDegrees(new ExifInterface(filePath).getAttributeInt("Orientation", 1));
        } catch (IOException e) {
            e.printStackTrace();
            return rotationInDegrees;
        } catch (Throwable th) {
            return rotationInDegrees;
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == 6) {
            return 90;
        }
        if (exifOrientation == 3) {
            return 180;
        }
        if (exifOrientation == 8) {
            return 270;
        }
        return 0;
    }

    public String getWallpaper() {
        return this._wallpaper;
    }

    public Bitmap getBitmap() {
        return this._bitmap;
    }

    public void setThermometer(Thermometer t) {
        this._thermometer = t.getId().intValue();
    }

    public void setThermometer(int tid) {
        this._thermometer = tid;
    }

    public int getThermometerId() {
        return this._thermometer;
    }

    public Thermometer getThermometer() {
        IElement t = NVModel.getElementById(Integer.valueOf(this._thermometer));
        if (t instanceof Thermometer) {
            return (Thermometer) t;
        }
        return null;
    }

    public void addElement(IElement el) {
        if (el != null) {
            if ((el instanceof Thermometer) && !(this instanceof Category)) {
                setThermometer((Thermometer) el);
            }
            this._elements.add(el);
            this._order.add(el.getId());
        }
    }

    public void setElements(ArrayList<IElement> els) {
        if (els != null) {
            clear();
            Iterator<IElement> itre = els.iterator();
            while (itre.hasNext()) {
                addElement((IElement) itre.next());
            }
        }
    }

    public IElement getElement(int i) {
        return (IElement) this._elements.get(i);
    }

    public ArrayList<IElement> getElements() {
        Collections.sort(this._elements, new ElementPositionCompartor());
        return (ArrayList) this._elements.clone();
    }

    public boolean removeElement(IElement el) {
        int ind = this._elements.indexOf(el);
        if (ind < 0) {
            return false;
        }
        this._order.remove(ind);
        this._coordinates.remove(el);
        return this._elements.remove(el);
    }

    public IElement removeElement(int i) {
        if (i < 0) {
            return null;
        }
        this._order.remove(i);
        IElement el = (IElement) this._elements.get(i);
        if (el != null) {
            this._coordinates.remove(el);
        }
        return (IElement) this._elements.remove(i);
    }

    public void clear() {
        this._elements.clear();
        this._order.clear();
        this._coordinates.clear();
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
    }

    public ArrayList<IElement> getElementsByType(String type) {
        ArrayList<IElement> els = new ArrayList();
        Iterator<IElement> itre = getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (el.getType() != null && el.getType().equals(type)) {
                els.add(el);
            }
        }
        return els;
    }

    public ArrayList<String> getElementNamesByType(String type) {
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

    public ArrayList<IElement> getLights() {
        ArrayList<IElement> lights = new ArrayList();
        lights.addAll(getElementsByType(NVModel.EL_TYPE_DIMMER));
        lights.addAll(getElementsByType(NVModel.EL_TYPE_LIGHT));
        lights.addAll(getElementsByType(NVModel.EL_TYPE_LIGHT_GROUP));
        lights.addAll(getElementsByType(NVModel.EL_TYPE_RGBW));
        lights.addAll(getElementsByType(NVModel.EL_TYPE_RGBW_GROUP));
        return lights;
    }

    public ArrayList<IElement> getLightsOn() {
        ArrayList<IElement> lightson = new ArrayList();
        Iterator<IElement> itre = getLights().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (el instanceof Dimmer) {
                if (((Dimmer) el).isOn()) {
                    lightson.add(el);
                }
            } else if (el instanceof Light) {
                if (((Light) el).isOn()) {
                    lightson.add(el);
                }
            } else if (el instanceof LightGroup) {
                if (((LightGroup) el).isOn()) {
                    lightson.add(el);
                }
            } else if (el instanceof RGBW) {
                if (((RGBW) el).isOn()) {
                    lightson.add(el);
                }
            } else if ((el instanceof RGBWGroup) && ((RGBWGroup) el).isOn()) {
                lightson.add(el);
            }
        }
        return lightson;
    }

    public String toXML(String spec_attrs) {
        String order = "";
        String coordinates = "";
        Iterator<IElement> itre = this._elements.iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (el != null) {
                int x;
                int y;
                if (itre.hasNext()) {
                    order = order + el.getId() + ",";
                    x = 0;
                    y = 0;
                    if (this._coordinates.get(el.getId()) != null) {
                        x = ((Point) this._coordinates.get(el.getId())).x;
                        y = ((Point) this._coordinates.get(el.getId())).y;
                    }
                    coordinates = coordinates + x + ":" + y + ",";
                } else {
                    order = order + el.getId() + "";
                    x = 0;
                    y = 0;
                    if (this._coordinates.get(el.getId()) != null) {
                        x = ((Point) this._coordinates.get(el.getId())).x;
                        y = ((Point) this._coordinates.get(el.getId())).y;
                    }
                    coordinates = coordinates + x + ":" + y + "";
                }
            }
        }
        StringBuffer sa = new StringBuffer();
        sa.append(" icon=\"" + getBackground() + "\"");
        sa.append(" image=\"" + getWallpaper() + "\"");
        sa.append(" order=\"" + order + "\"");
        sa.append(" coordinates=\"" + coordinates + "\"");
        sa.append(" thermometer=\"" + getThermometerId() + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
