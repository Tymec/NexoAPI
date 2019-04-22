package eu.nexwell.android.nexovision.model;

import android.graphics.Point;
import eu.nexwell.android.nexovision.MainActivity;
import nexovision.android.nexwell.eu.nexovision.R;

public class VideophoneIP extends Element {
    private static String _defaultCategory = NVModel.CATEGORY_VIDEOPHONES;
    private static Integer _typeNameResId = Integer.valueOf(R.string.ResourceTypeName_Videophone);
    private String _addr;
    private String _sip_proxy;
    private Point _size = new Point(1024, 768);

    public VideophoneIP() {
        setType(NVModel.EL_TYPE_VIDEOPHONE);
        setIcon("ic_videophone");
        setBackground(MainActivity.getContext().getResources().getDrawable(R.drawable.cell_selector_graphite));
    }

    public void setAddress(String addr) {
        this._addr = addr;
    }

    public String getAddress() {
        return this._addr;
    }

    public void setSipProxy(String sip_proxy) {
        this._sip_proxy = sip_proxy;
    }

    public String getSipProxy() {
        return this._sip_proxy;
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
        sa.append(" sip_proxy=\"" + getSipProxy() + "\"");
        if (spec_attrs != null) {
            sa.append(spec_attrs);
        }
        return super.toXML(sa.toString());
    }
}
