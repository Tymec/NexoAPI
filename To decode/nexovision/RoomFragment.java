package eu.nexwell.android.nexovision;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import eu.nexwell.android.nexovision.GeolocationService.GeolocationListener;
import eu.nexwell.android.nexovision.communication.NexoService;
import eu.nexwell.android.nexovision.communication.NexoTalk;
import eu.nexwell.android.nexovision.communication.NexoTalkListener;
import eu.nexwell.android.nexovision.model.AnalogOutput;
import eu.nexwell.android.nexovision.model.AnalogOutputGroup;
import eu.nexwell.android.nexovision.model.CameraIP;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.Dimmer;
import eu.nexwell.android.nexovision.model.GeolocationPoint;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.Logic;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Partition;
import eu.nexwell.android.nexovision.model.Partition.Function;
import eu.nexwell.android.nexovision.model.Polygon;
import eu.nexwell.android.nexovision.model.Scene;
import eu.nexwell.android.nexovision.model.Sensor;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.Ventilator;
import eu.nexwell.android.nexovision.model.VideophoneIP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class RoomFragment extends Fragment implements NexoTalkListener, GeolocationListener {
    private static String LOG_TAG = "RoomFragment";
    public static AlertDialog delDialog;
    static boolean longPress = false;
    public static AlertDialog longPressMenuDialog;
    static Runnable longPressTimer = null;
    public static boolean moveMode = false;
    public static Polygon polygonInEditMode = null;
    private int _xDelta;
    private int _yDelta;
    private HashMap<IElement, View> elementToViewMap;
    private ArrayList<IElement> elements = null;
    private ImageView fragmentBackground;
    private int fragmentId = 0;
    private FrameLayout polygonsContainer;
    private RelativeLayout relativeLayout;

    /* renamed from: eu.nexwell.android.nexovision.RoomFragment$1 */
    class C20731 implements OnClickListener {
        C20731() {
        }

        public void onClick(DialogInterface dialog, int item) {
            switch (item) {
                case 0:
                    Intent intent;
                    if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_VIDEOPHONE)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorVideophoneActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_CAMERA)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorCameraActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_THERMOSTAT)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorThermometerActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_SET)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorSetActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_PARTITION)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorPartitionActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_LOGIC)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorLogicActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_GEOLOCATIONPOINT)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorGeolocationPointActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_SCENE)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorSceneActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_POLYGON)) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorPolygonActivity.class);
                    } else {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorBasicActivity.class);
                    }
                    if (intent != null) {
                        intent.addFlags(67108864);
                        RoomFragment.this.startActivityForResult(intent, 0);
                        return;
                    }
                    return;
                case 1:
                    RoomFragment.moveMode = true;
                    MainActivity.refreshFragment();
                    return;
                case 2:
                    if (RoomFragment.delDialog != null) {
                        RoomFragment.delDialog.show();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.RoomFragment$2 */
    class C20742 implements OnClickListener {
        C20742() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.RoomFragment$3 */
    class C20753 implements OnClickListener {
        C20753() {
        }

        public void onClick(DialogInterface dialog, int id) {
            Log.d("LOG_TAG", "Remove from everywhere: " + NVModel.CURR_ELEMENT.getName());
            NVModel.removeElement(NVModel.CURR_ELEMENT, true);
            MainActivity.refreshFragment();
            dialog.dismiss();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.RoomFragment$4 */
    class C20764 implements OnClickListener {
        C20764() {
        }

        public void onClick(DialogInterface dialog, int id) {
            NVModel.removeElement(NVModel.CURR_ELEMENT, false);
            MainActivity.refreshFragment();
            dialog.dismiss();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.RoomFragment$7 */
    class C20817 implements OnLongClickListener {
        C20817() {
        }

        public boolean onLongClick(View v) {
            return false;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.RoomFragment$8 */
    class C20828 implements View.OnClickListener {
        C20828() {
        }

        public void onClick(View v) {
            IElement obj = v.getTag();
            if (obj != null && (obj instanceof IElement)) {
                IElement el = obj;
                if (el != null) {
                    NVModel.CURR_ELEMENT = el;
                    el.setSelected(false);
                    Intent intent;
                    if (el instanceof ISwitch) {
                        if (NexoTalk.isConnected()) {
                            if ((el instanceof Partition) && ((Partition) el).getFunc() == Function.COMMON) {
                                NexoService.freeze();
                                intent = new Intent().setClass(RoomFragment.this.getContext(), ElementControl_PartitionActivity.class);
                                intent.addFlags(67108864);
                                RoomFragment.this.startActivityForResult(intent, 0);
                            } else if ((el instanceof Partition) && ((((Partition) el).getFunc() == Function.FIRE24H || ((Partition) el).getFunc() == Function.FLOOD24H) && ((Partition) el).isAlarming())) {
                                NexoService.freeze();
                                intent = new Intent().setClass(RoomFragment.this.getContext(), ElementControl_PartitionActivity.class);
                                intent.addFlags(67108864);
                                RoomFragment.this.startActivityForResult(intent, 0);
                            } else {
                                NexoService.queueActionAndUpdate((ISwitch) el, ((ISwitch) el).switchState());
                            }
                        } else if (RoomFragment.this.fragmentId > 0) {
                            if (el instanceof Thermometer) {
                                intent = new Intent().setClass(RoomFragment.this.getContext(), EditorThermometerActivity.class);
                            } else if (el instanceof Partition) {
                                intent = new Intent().setClass(RoomFragment.this.getContext(), EditorPartitionActivity.class);
                            } else {
                                intent = new Intent().setClass(RoomFragment.this.getContext(), EditorBasicActivity.class);
                            }
                            intent.addFlags(67108864);
                            RoomFragment.this.startActivityForResult(intent, 0);
                        }
                    } else if (el instanceof VideophoneIP) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), ElementControl_VideophoneActivity.class);
                        intent.addFlags(67108864);
                        RoomFragment.this.startActivityForResult(intent, 0);
                    } else if (el instanceof CameraIP) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), ElementControl_CameraActivity.class);
                        intent.addFlags(67108864);
                        RoomFragment.this.startActivityForResult(intent, 0);
                    } else if (el instanceof Logic) {
                        if (NexoTalk.isConnected()) {
                            NexoService.queueAction(((Logic) el).action());
                            return;
                        }
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorLogicActivity.class);
                        intent.addFlags(67108864);
                        RoomFragment.this.startActivityForResult(intent, 0);
                    } else if (el instanceof GeolocationPoint) {
                        intent = new Intent().setClass(RoomFragment.this.getContext(), EditorGeolocationPointActivity.class);
                        intent.addFlags(67108864);
                        RoomFragment.this.startActivityForResult(intent, 0);
                    } else if ((el instanceof Scene) && NexoTalk.isConnected()) {
                        Log.d("GridFragment", "Scene: SET");
                        Iterator<String> itra = ((Scene) el).restoreStates().iterator();
                        while (itra.hasNext()) {
                            String action = (String) itra.next();
                            Log.d("GridFragment", "Scene: action=" + action);
                            if (!(action == null || action.isEmpty())) {
                                NexoService.queueAction(action);
                            }
                        }
                    }
                }
            }
        }
    }

    public static RoomFragment newInstance(int id) {
        RoomFragment f = new RoomFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    public int getFragmentId() {
        return this.fragmentId;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        int i;
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            i = getArguments().getInt("id");
        } else {
            i = 0;
        }
        this.fragmentId = i;
        moveMode = false;
        Log.d("LOG_TAG", "onCreate(fid=" + this.fragmentId + ")");
        NexoTalk.addNexoTalkListener(this);
        GeolocationService.addGeolocationListener(this);
        CharSequence[] lpa_items = new CharSequence[]{getString(R.string.RoomFragment_SWLongPressDialog_EditItem), "Move", getString(R.string.RoomFragment_SWLongPressDialog_RemoveItem)};
        Builder builder = new Builder(getContext());
        builder.setTitle(getString(R.string.RoomFragment_SWLongPressDialog_Title));
        builder.setItems(lpa_items, new C20731());
        longPressMenuDialog = builder.create();
        builder = new Builder(getContext());
        builder.setMessage(R.string.GridFragment_RemoveDialog_Question).setCancelable(false).setPositiveButton(R.string.GridFragment_RemoveDialog_OnlyFromPlacesItem, new C20764()).setNeutralButton(R.string.GridFragment_RemoveDialog_FromEverywhereItem, new C20753()).setNegativeButton(R.string.NO, new C20742());
        delDialog = builder.create();
        if (this.elementToViewMap == null) {
            this.elementToViewMap = new HashMap();
        } else {
            this.elementToViewMap.clear();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.room_fragment, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.polygonsContainer = (FrameLayout) getView().findViewById(R.id.polygonsContainer);
        this.polygonsContainer.removeAllViews();
        this.fragmentBackground = (ImageView) getView().findViewById(R.id.fragmentBackground);
        this.relativeLayout = (RelativeLayout) getView().findViewById(R.id.relativeLayout);
        if (this.elements == null) {
            this.elements = new ArrayList();
        }
        this.elements.clear();
        this.relativeLayout.removeAllViews();
        Log.d("LOG_TAG", "onActivityCreated(fid=" + this.fragmentId + ")");
        if (this.fragmentId > 0) {
            final IElement el = NVModel.getElementById(Integer.valueOf(this.fragmentId));
            MainActivity.handler.post(new Runnable() {
                public void run() {
                    ((MainActivity) MainActivity.getContext()).getSupportActionBar().setTitle(el.getName());
                }
            });
            if ((el instanceof ISet) && !(el instanceof Category)) {
                Display display = ((MainActivity) MainActivity.getContext()).getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (((ISet) el).getBitmap() != null) {
                    this.fragmentBackground.setImageBitmap(Bitmap.createScaledBitmap(((ISet) el).getBitmap(), size.x / 2, size.y / 2, true));
                }
                if (((ISet) el).getElements().size() > 0) {
                    this.elements.clear();
                    this.elements = ((ISet) el).getElements();
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    SliderRoundView sliderRoundView = (SliderRoundView) getView().findViewById(R.id.sliderRoundView);
                    sliderRoundView.init(5);
                    int x = 0;
                    Iterator<IElement> itre = this.elements.iterator();
                    while (itre.hasNext()) {
                        IElement e = (IElement) itre.next();
                        if (e instanceof Polygon) {
                            this.polygonsContainer.addView(new PolygonView(getContext(), (Polygon) e));
                        } else {
                            View view;
                            if (moveMode) {
                                view = inflater.inflate(R.layout.cell, this.relativeLayout, false);
                            } else {
                                view = inflater.inflate(R.layout.cell_nolabel, this.relativeLayout, false);
                            }
                            IconLabel label = (IconLabel) view.findViewById(R.id.label);
                            ImageView icon = (ImageView) view.findViewById(R.id.icon);
                            TextView text1 = (TextView) view.findViewById(R.id.text1);
                            LinearLayout box = (LinearLayout) view.findViewById(R.id.box);
                            if (e != null) {
                                if (label != null) {
                                    label.setSelected(true);
                                    label.setEnabled(true);
                                    label.setText(e.getName());
                                }
                                if (box != null) {
                                    Drawable d = e.getBackground();
                                    if (d != null) {
                                        box.setBackground(d);
                                        if (e.isSelected()) {
                                            d.setState(new int[]{16842913});
                                        } else {
                                            d.setState(new int[0]);
                                            d.invalidateSelf();
                                        }
                                    }
                                    if (e instanceof Sensor) {
                                        box.getBackground().setAlpha(0);
                                        box.setBackgroundColor(0);
                                    }
                                }
                                if (icon != null) {
                                    int resId = getContext().getResources().getIdentifier("drawable/" + e.getIcon(), null, getContext().getPackageName());
                                    if (resId <= 0) {
                                        resId = getContext().getResources().getIdentifier("drawable/i_1_3d_unknown", null, getContext().getPackageName());
                                    }
                                    icon.setImageResource(resId);
                                }
                                if (text1 != null) {
                                    if ((e instanceof Dimmer) && ((Dimmer) e).getInfo() != null) {
                                        text1.setText(((Dimmer) e).getValue().intValue() + "%");
                                        text1.setVisibility(0);
                                    } else if (!(e instanceof Thermometer) || ((Thermometer) e).getValue() == null) {
                                        text1.setVisibility(8);
                                    } else {
                                        if (((Thermometer) e).getThermostat() != null) {
                                            text1.setText(((Thermometer) e).getValue().intValue() + "," + ((int) ((((Thermometer) e).getValue().floatValue() - ((float) ((Thermometer) e).getValue().intValue())) * 10.0f)) + "/" + ((Thermometer) e).getThermostat().getValue() + "℃");
                                        } else {
                                            text1.setText(((Thermometer) e).getValue().intValue() + "℃");
                                        }
                                        text1.setVisibility(0);
                                    }
                                }
                            }
                            int tenscreenwidth = size.x / (((int) Math.pow(2.0d, (double) ((ISet) el).getIconsSize())) * 4);
                            int tenscreenheight = size.y / (((int) Math.pow(2.0d, (double) ((ISet) el).getIconsSize())) * 4);
                            view.setTag(e);
                            final SliderRoundView sliderRoundView2 = sliderRoundView;
                            view.setOnTouchListener(new OnTouchListener() {
                                int[] location = new int[2];
                                float value = 0.0f;

                                /* renamed from: eu.nexwell.android.nexovision.RoomFragment$6$2 */
                                class C20792 implements Runnable {
                                    C20792() {
                                    }

                                    public void run() {
                                        RoomFragment.longPressMenuDialog.show();
                                    }
                                }

                                public boolean onTouch(View v, MotionEvent event) {
                                    final int X = (int) event.getRawX();
                                    int Y = (int) event.getRawY();
                                    final int bw = RoomFragment.this.relativeLayout.getMeasuredWidth();
                                    final int bh = RoomFragment.this.relativeLayout.getMeasuredHeight();
                                    IElement elem = (IElement) v.getTag();
                                    if (!NexoTalk.isConnected()) {
                                        switch (event.getAction() & 255) {
                                            case 0:
                                                LayoutParams lParams = (LayoutParams) v.getLayoutParams();
                                                RoomFragment.this._xDelta = X - lParams.leftMargin;
                                                RoomFragment.this._yDelta = Y - lParams.topMargin;
                                                if (!RoomFragment.moveMode) {
                                                    Handler handler = MainActivity.handler;
                                                    Runnable c20792 = new C20792();
                                                    RoomFragment.longPressTimer = c20792;
                                                    handler.postDelayed(c20792, 500);
                                                    break;
                                                }
                                                break;
                                            case 1:
                                                if (RoomFragment.longPressTimer != null) {
                                                    MainActivity.handler.removeCallbacks(RoomFragment.longPressTimer);
                                                }
                                                LayoutParams lParams2 = (LayoutParams) v.getLayoutParams();
                                                int x = lParams2.leftMargin;
                                                int y = lParams2.topMargin;
                                                Log.d(RoomFragment.LOG_TAG, "x=" + x + ",y=" + y);
                                                ((ISet) el).setCoordinatesForElement((IElement) v.getTag(), new Point(x, y));
                                                break;
                                            case 2:
                                                if (RoomFragment.moveMode) {
                                                    LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
                                                    int px = ((X - RoomFragment.this._xDelta) * 100) / bw;
                                                    int py = ((Y - RoomFragment.this._yDelta) * 100) / bh;
                                                    Log.d(RoomFragment.LOG_TAG, "X[%]=" + px + ", Y[%]=" + py);
                                                    layoutParams.leftMargin = (px * bw) / 100;
                                                    layoutParams.topMargin = (py * bh) / 100;
                                                    layoutParams.rightMargin = -250;
                                                    layoutParams.bottomMargin = -250;
                                                    v.setLayoutParams(layoutParams);
                                                    break;
                                                }
                                                break;
                                        }
                                        RoomFragment.this.relativeLayout.invalidate();
                                        return true;
                                    } else if (!(elem instanceof Dimmer) && ((!(elem instanceof Thermometer) || ((Thermometer) elem).getThermostat() == null) && !(elem instanceof AnalogOutput) && !(elem instanceof AnalogOutputGroup) && !(elem instanceof Ventilator))) {
                                        return false;
                                    } else {
                                        switch (event.getAction() & 255) {
                                            case 0:
                                                Handler handler2 = MainActivity.handler;
                                                final View view = v;
                                                Runnable c20781 = new Runnable() {
                                                    public void run() {
                                                        int offset_x = 0;
                                                        int offset_y = 0;
                                                        view.getLocationInWindow(C20806.this.location);
                                                        int top_m = view.getTop() - (view.getHeight() * 2);
                                                        if (top_m < 0) {
                                                            offset_y = -top_m;
                                                            top_m = 0;
                                                        } else if ((view.getHeight() * 5) + top_m > bh) {
                                                            offset_y = bh - ((view.getHeight() * 5) + top_m);
                                                            top_m = bh - (view.getHeight() * 5);
                                                        }
                                                        int left_m = view.getLeft() - (view.getWidth() * 2);
                                                        if (left_m < 0) {
                                                            offset_x = -left_m;
                                                            left_m = 0;
                                                        } else if ((view.getWidth() * 5) + left_m > bw) {
                                                            offset_x = bw - ((view.getWidth() * 5) + left_m);
                                                            left_m = bw - (view.getWidth() * 5);
                                                        }
                                                        int height = view.getHeight() * 5;
                                                        int xpos = X;
                                                        int center_x = (C20806.this.location[0] + (view.getWidth() / 2)) + offset_x;
                                                        int center_y = (C20806.this.location[1] + (view.getHeight() / 2)) + offset_y;
                                                        C20806.this.value = sliderRoundView2.show(top_m, left_m, height, xpos, new Point(center_x, center_y), C20806.this.value);
                                                        RoomFragment.longPress = true;
                                                    }
                                                };
                                                RoomFragment.longPressTimer = c20781;
                                                handler2.postDelayed(c20781, 500);
                                                break;
                                            case 1:
                                                sliderRoundView2.hide();
                                                if (RoomFragment.longPress) {
                                                    RoomFragment.longPress = false;
                                                    if (!(elem instanceof Dimmer)) {
                                                        if (!(elem instanceof AnalogOutput)) {
                                                            if (!(elem instanceof Thermometer) || ((Thermometer) elem).getThermostat() == null) {
                                                                if (elem instanceof Ventilator) {
                                                                    NexoService.queueActionAndUpdate((ISwitch) elem, ((Ventilator) elem).on(Integer.valueOf((int) this.value)));
                                                                    break;
                                                                }
                                                            }
                                                            NexoService.queueActionAndUpdate(((Thermometer) elem).getThermostat(), ((Thermometer) elem).getThermostat().activate(new Float(this.value)));
                                                            break;
                                                        }
                                                        NexoService.queueActionAndUpdate((ISwitch) elem, ((AnalogOutput) elem).on(Integer.valueOf((int) this.value)));
                                                        break;
                                                    }
                                                    NexoService.queueActionAndUpdate((ISwitch) elem, ((Dimmer) elem).on(Integer.valueOf((int) this.value)));
                                                    break;
                                                }
                                                if (RoomFragment.longPressTimer != null) {
                                                    MainActivity.handler.removeCallbacks(RoomFragment.longPressTimer);
                                                }
                                                return false;
                                                break;
                                            case 2:
                                                this.value = sliderRoundView2.move(X, Y, this.value);
                                                if (RoomFragment.longPress) {
                                                    return true;
                                                }
                                                return false;
                                        }
                                        return true;
                                    }
                                }
                            });
                            view.setOnLongClickListener(new C20817());
                            view.setOnClickListener(new C20828());
                            ViewGroup.LayoutParams layoutParams = new LayoutParams(tenscreenwidth, tenscreenwidth);
                            Point p = ((ISet) el).getCoordinatesOfElement(e);
                            if (p != null) {
                                layoutParams.leftMargin = p.x;
                                layoutParams.topMargin = p.y;
                            } else {
                                layoutParams.leftMargin = 0;
                                layoutParams.topMargin = 0;
                            }
                            this.relativeLayout.addView(view, layoutParams);
                            this.elementToViewMap.put(e, view);
                            x++;
                        }
                    }
                }
            }
        }
        if (NexoTalk.isConnected()) {
            Log.d("GridFragment", "onActivityCreated(fid=" + this.fragmentId + "):NexoTalk.update()");
            NexoService.goForeground(NVModel.getCurrentElements());
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void refresh() {
    }

    public void onDestroy() {
        super.onDestroy();
        Iterator<IElement> itrp = NVModel.getElementsByType(NVModel.EL_TYPE_POLYGON).iterator();
        while (itrp.hasNext()) {
            IElement el = (IElement) itrp.next();
            if (el instanceof Polygon) {
                ((Polygon) el).disableEditMode();
            }
        }
        this.elements.clear();
        this.elements = null;
        this.elementToViewMap.clear();
        this.elementToViewMap = null;
        this.relativeLayout.removeAllViews();
        GeolocationService.removeGeolocationListener(this);
        NexoTalk.removeNexoTalkListener(this);
    }

    public void onImport(int type, int iterator) {
    }

    public void onImportEnd(ArrayList<Integer> arrayList) {
    }

    public void onStatusUpdate(final IElement el, boolean finish) {
        final View view = (View) this.elementToViewMap.get(el);
        if (view != null) {
            MainActivity.handler.post(new Runnable() {
                public void run() {
                    IconLabel label = (IconLabel) view.findViewById(R.id.label);
                    ImageView icon = (ImageView) view.findViewById(R.id.icon);
                    LinearLayout box = (LinearLayout) view.findViewById(R.id.box);
                    TextView text1 = (TextView) view.findViewById(R.id.text1);
                    if (el != null) {
                        if (label != null) {
                            label.setSelected(true);
                            label.setEnabled(true);
                            label.setText(el.getName());
                        }
                        if (box != null) {
                            Drawable d = el.getBackground();
                            if (d != null) {
                                box.setBackground(d);
                                if (el.isSelected()) {
                                    d.setState(new int[]{16842913});
                                } else {
                                    d.setState(new int[0]);
                                    d.invalidateSelf();
                                }
                            }
                            if (el instanceof Sensor) {
                                box.getBackground().setAlpha(0);
                                box.setBackgroundColor(0);
                            }
                        }
                        if (icon != null) {
                            int resId = RoomFragment.this.getContext().getResources().getIdentifier("drawable/" + el.getIcon(), null, RoomFragment.this.getContext().getPackageName());
                            if (resId <= 0) {
                                resId = RoomFragment.this.getContext().getResources().getIdentifier("drawable/i_1_3d_unknown", null, RoomFragment.this.getContext().getPackageName());
                            }
                            icon.setImageResource(resId);
                        }
                        if (text1 != null) {
                            if ((el instanceof Dimmer) && ((Dimmer) el).getInfo() != null) {
                                text1.setText(((Dimmer) el).getValue().intValue() + "%");
                                text1.setVisibility(0);
                            } else if (!(el instanceof Thermometer) || ((Thermometer) el).getValue() == null) {
                                text1.setVisibility(8);
                            } else {
                                if (((Thermometer) el).getThermostat() != null) {
                                    text1.setText(((Thermometer) el).getValue().intValue() + "," + ((int) ((((Thermometer) el).getValue().floatValue() - ((float) ((Thermometer) el).getValue().intValue())) * 10.0f)) + "/" + ((Thermometer) el).getThermostat().getValue() + "℃");
                                } else {
                                    text1.setText(((Thermometer) el).getValue().intValue() + "℃");
                                }
                                text1.setVisibility(0);
                            }
                        }
                    }
                    view.invalidate();
                }
            });
        }
    }

    public void onPartitionAlarm(IElement el) {
    }

    public void connectionStatus(boolean connected) {
    }

    public void onLocationUpdate(Location loc) {
    }

    public void onStateChange(GeolocationPoint glp, float distance) {
    }

    public void onStateUpdate(GeolocationPoint glp, float distance) {
    }

    public void connectionProcessInfo(String info, String error) {
    }

    public Bitmap BITMAP_RESIZER(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);
        float ratioX = ((float) newWidth) / ((float) bitmap.getWidth());
        float ratioY = ((float) newHeight) / ((float) bitmap.getHeight());
        float middleX = ((float) newWidth) / 2.0f;
        float middleY = ((float) newHeight) / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - ((float) (bitmap.getWidth() / 2)), middleY - ((float) (bitmap.getHeight() / 2)), new Paint(1));
        return scaledBitmap;
    }
}
