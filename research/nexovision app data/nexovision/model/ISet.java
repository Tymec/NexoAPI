package eu.nexwell.android.nexovision.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import java.util.ArrayList;

public interface ISet {
    void addElement(IElement iElement);

    void clear();

    Bitmap getBitmap();

    Point getCoordinatesOfElement(IElement iElement);

    IElement getElement(int i);

    ArrayList<String> getElementNamesByType(String str);

    ArrayList<IElement> getElements();

    ArrayList<IElement> getElementsByType(String str);

    int getIconsSize();

    ArrayList<IElement> getLights();

    ArrayList<IElement> getLightsOn();

    ArrayList<Integer> getOrder();

    Thermometer getThermometer();

    int getThermometerId();

    String getWallpaper();

    IElement removeElement(int i);

    boolean removeElement(IElement iElement);

    void setCoordinates(String str);

    void setCoordinatesForElement(IElement iElement, Point point);

    void setElements(ArrayList<IElement> arrayList);

    void setIconsSize(int i);

    void setOrder(String str);

    void setOrder(ArrayList<Integer> arrayList);

    void setThermometer(int i);

    void setThermometer(Thermometer thermometer);

    void setWallpaper(String str, Context context);

    String toXML(String str);
}
