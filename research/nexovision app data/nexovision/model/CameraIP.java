package eu.nexwell.android.nexovision.model;

import android.graphics.Point;
import eu.nexwell.android.nexovision.MainActivity;
import nexovision.android.nexwell.eu.nexovision.R;

public class CameraIP extends Element {
    private static String _defaultCategory = NVModel.CATEGORY_CAMERAS;
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Camera);
    private String _addr;
    private String _sip_proxy;
    private Point _size = new Point(640, 480);

    public CameraIP() {
        setType(NVModel.EL_TYPE_CAMERA);
        setIcon("ic_videocam");
        setBackground(MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_orange));
    }

    public void setAddress(String addr) {
        this._addr = addr;
    }

    public String getAddress() {
        return this._addr;
    }

    public void setSize(int width, int height) {
        this._size = new Point(width, height);
    }

    public void setSize(Point size) {
        this._size = size;
    }

    public Point getSize() {
        return this._size;
    }

    public static Integer getTypeNameResId() {
        return _typeNameResId;
    }

    public static String getDefaultCategory() {
        return _defaultCategory;
    }

    public String toXML(String spec_attrs) {
        StringBuffer sa = new StringBuffer();
        sa.append(" addr=\"" + getAddress() + "\"");
        sa.append(" size_x=\"" + getSize().x + "\" size_y=\"" + getSize().y + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
