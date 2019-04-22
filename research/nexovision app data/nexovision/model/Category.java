package eu.nexwell.android.nexovision.model;

import java.util.Iterator;

public class Category extends Set implements ISet {
    private String _use;

    public Category() {
        setType(NVModel.EL_TYPE_CATEGORY);
    }

    public void setUse(String use) {
        this._use = use;
    }

    public String getUse() {
        return this._use;
    }

    public String toXML(String spec_attrs) {
        String order = "";
        Iterator<IElement> itre = getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (el != null) {
                if (itre.hasNext()) {
                    order = order + el.getId() + ",";
                } else {
                    order = order + el.getId() + "";
                }
            }
        }
        String data = "";
        if (getUse().equals(NVModel.CATEGORY_TEMPERATURE)) {
            String t1 = "";
            String t2 = "";
            if (NVModel.getMainOutThermometer() != null) {
                t1 = "" + NVModel.getMainOutThermometer().getId();
            }
            if (NVModel.getMainInThermometer() != null) {
                t2 = "" + NVModel.getMainInThermometer().getId();
            }
            data = " data=\"" + t1 + "," + t2 + "\"";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\t<element id=\"" + getId() + "\" type=\"" + getType() + "\" use=\"" + getUse() + "\" order=\"" + order + "\"" + data + "/>\n");
        return sb.toString();
    }
}
