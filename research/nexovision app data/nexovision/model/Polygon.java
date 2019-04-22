package eu.nexwell.android.nexovision.model;

import android.graphics.Point;
import eu.nexwell.android.nexovision.MainActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class Polygon extends Element {
    private static String _defaultCategory = NVModel.CATEGORY_POLYGONS;
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Polygon);
    private int _alpha = 50;
    private boolean _editMode = false;
    private ArrayList<Point> _points = new ArrayList();

    public Polygon() {
        setType(NVModel.EL_TYPE_POLYGON);
        setIcon("ic_alarm");
        setBackground(MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_graphite));
    }

    public void setAlpha(int a) {
        this._alpha = a;
    }

    public int getAlpha() {
        return this._alpha;
    }

    public void enableEditMode() {
        this._editMode = true;
    }

    public void disableEditMode() {
        this._editMode = false;
    }

    public boolean isInEditmode() {
        return this._editMode;
    }

    public void move(int x, int y) {
        Iterator<Point> itrp = this._points.iterator();
        while (itrp.hasNext()) {
            Point point = (Point) itrp.next();
            if (point != null) {
                point.x += x;
                point.y += y;
            }
        }
    }

    public Point addPoint(int x, int y) {
        Point p = new Point(x, y);
        this._points.add(p);
        return p;
    }

    public Point addPointBefore(int x, int y, Point before) {
        Point p = new Point(x, y);
        int index = this._points.indexOf(before);
        if (index >= 0) {
            this._points.add(index, p);
        } else {
            this._points.add(p);
        }
        return p;
    }

    public void clearPoints() {
        this._points.clear();
    }

    public void setPoints(ArrayList<Point> points) {
        this._points = points;
    }

    public void setPoints(String points) {
        this._points.clear();
        for (String point_str : Arrays.asList(points.split("\\s*;\\s*"))) {
            String[] point_arr = point_str.split("\\s*,\\s*");
            if (point_arr != null && point_arr.length > 1) {
                this._points.add(new Point(Integer.parseInt(point_arr[0]), Integer.parseInt(point_arr[1])));
            }
        }
    }

    public ArrayList<Point> getPointsAsArray() {
        return this._points;
    }

    public String getPointsAsString() {
        String points = "";
        Iterator<Point> itrp = this._points.iterator();
        while (itrp.hasNext()) {
            Point point = (Point) itrp.next();
            if (!points.isEmpty()) {
                points = points + ";";
            }
            points = points + point.x + "," + point.y;
        }
        return points;
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
    }

    public String toXML(String spec_attrs) {
        StringBuffer sa = new StringBuffer();
        sa.append(" points=\"" + getPointsAsString() + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
