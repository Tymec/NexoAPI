package eu.nexwell.android.nexovision;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import eu.nexwell.android.nexovision.GeoService.GeoListener;
import eu.nexwell.android.nexovision.communication.NexoService;
import eu.nexwell.android.nexovision.communication.NexoTalk;
import eu.nexwell.android.nexovision.communication.NexoTalkListener;
import eu.nexwell.android.nexovision.model.AnalogOutput;
import eu.nexwell.android.nexovision.model.AnalogOutputGroup;
import eu.nexwell.android.nexovision.model.CameraIP;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.Dimmer;
import eu.nexwell.android.nexovision.model.Gate;
import eu.nexwell.android.nexovision.model.GeolocationPoint;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.Logic;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Output;
import eu.nexwell.android.nexovision.model.OutputGroup;
import eu.nexwell.android.nexovision.model.Partition;
import eu.nexwell.android.nexovision.model.Partition.Function;
import eu.nexwell.android.nexovision.model.RGBW;
import eu.nexwell.android.nexovision.model.Scene;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.Thermostat;
import eu.nexwell.android.nexovision.model.Ventilator;
import eu.nexwell.android.nexovision.model.VideophoneIP;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;
import org.askerov.dynamicgrid.DynamicGridView;

public class GridFragment extends Fragment implements NexoTalkListener, GeoListener {
    static boolean longPress = false;
    static Runnable longPressTimer = null;
    private GridListCellAdapter adapter;
    private AlertDialog delDialog;
    private ArrayList<IElement> elements = null;
    private ImageView fragmentBackground;
    private int fragmentId = 0;
    private DynamicGridView gridView;
    private AlertDialog longPressMenuDialog;

    /* renamed from: eu.nexwell.android.nexovision.GridFragment$1 */
    class C20091 implements OnClickListener {
        C20091() {
        }

        public void onClick(DialogInterface dialog, int item) {
            switch (item) {
                case 0:
                    Intent intent;
                    if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_VIDEOPHONE)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorVideophoneActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_CAMERA)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorCameraActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_THERMOSTAT)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorThermometerActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_SET)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorSetActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_PARTITION)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorPartitionActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_LOGIC)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorLogicActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_GEOLOCATIONPOINT)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorGeolocationPointActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_SCENE)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorSceneActivity.class);
                    } else if (NVModel.CURR_ELEMENT.getType().equals(NVModel.EL_TYPE_POLYGON)) {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorPolygonActivity.class);
                    } else {
                        intent = new Intent().setClass(GridFragment.this.getContext(), EditorBasicActivity.class);
                    }
                    if (intent != null) {
                        intent.addFlags(67108864);
                        GridFragment.this.startActivityForResult(intent, 0);
                        return;
                    }
                    return;
                case 1:
                    if (GridFragment.this.delDialog != null) {
                        GridFragment.this.delDialog.show();
                        return;
                    }
                    return;
                case 2:
                    GridFragment.this.gridView.startEditMode();
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GridFragment$2 */
    class C20102 implements OnClickListener {
        C20102() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GridFragment$3 */
    class C20113 implements OnClickListener {
        C20113() {
        }

        public void onClick(DialogInterface dialog, int id) {
            Log.d("GridFragment", "Remove from everywhere: " + NVModel.CURR_ELEMENT.getName());
            NVModel.removeElement(NVModel.CURR_ELEMENT, true);
            MainActivity.refreshFragment();
            dialog.dismiss();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GridFragment$4 */
    class C20124 implements OnClickListener {
        C20124() {
        }

        public void onClick(DialogInterface dialog, int id) {
            NVModel.removeElement(NVModel.CURR_ELEMENT, false);
            MainActivity.refreshFragment();
            dialog.dismiss();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GridFragment$6 */
    class C20146 implements Runnable {
        C20146() {
        }

        public void run() {
            ((MainActivity) MainActivity.getContext()).getSupportActionBar().setTitle(GridFragment.this.getContext().getString(R.string.APP_NAME));
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GridFragment$7 */
    class C20157 implements OnItemClickListener {
        C20157() {
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            IElement el = (IElement) GridFragment.this.elements.get(position);
            if (el != null) {
                NVModel.CURR_ELEMENT = el;
                ((IElement) ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).getItem(position)).setSelected(false);
                if (el instanceof ISet) {
                    if (MainActivity.isLandscapeOrientation()) {
                        MainActivity.setFragment(RoomFragment.newInstance(el.getId().intValue()), "" + el.getId());
                    } else {
                        MainActivity.setFragment(GridFragment.newInstance(el.getId().intValue()), "" + el.getId());
                    }
                } else if (el instanceof ISwitch) {
                    if (NexoTalk.isConnected()) {
                        if ((el instanceof Partition) && ((Partition) el).getFunc() == Function.COMMON) {
                            NexoService.freeze();
                            intent = new Intent().setClass(GridFragment.this.getContext(), ElementControl_PartitionActivity.class);
                            intent.addFlags(67108864);
                            GridFragment.this.startActivityForResult(intent, 0);
                        } else if ((el instanceof Partition) && ((((Partition) el).getFunc() == Function.FIRE24H || ((Partition) el).getFunc() == Function.FLOOD24H) && ((Partition) el).isAlarming())) {
                            NexoService.freeze();
                            intent = new Intent().setClass(GridFragment.this.getContext(), ElementControl_PartitionActivity.class);
                            intent.addFlags(67108864);
                            GridFragment.this.startActivityForResult(intent, 0);
                        } else {
                            NexoService.queueActionAndUpdate((ISwitch) el, ((ISwitch) el).switchState());
                        }
                    } else if (GridFragment.this.fragmentId > 0) {
                        if (el instanceof Thermometer) {
                            intent = new Intent().setClass(GridFragment.this.getContext(), EditorThermometerActivity.class);
                        } else if (el instanceof Partition) {
                            intent = new Intent().setClass(GridFragment.this.getContext(), EditorPartitionActivity.class);
                        } else {
                            intent = new Intent().setClass(GridFragment.this.getContext(), EditorBasicActivity.class);
                        }
                        intent.addFlags(67108864);
                        GridFragment.this.startActivityForResult(intent, 0);
                    }
                } else if (el instanceof VideophoneIP) {
                    intent = new Intent().setClass(GridFragment.this.getContext(), ElementControl_VideophoneActivity.class);
                    intent.addFlags(67108864);
                    GridFragment.this.startActivityForResult(intent, 0);
                } else if (el instanceof CameraIP) {
                    intent = new Intent().setClass(GridFragment.this.getContext(), ElementControl_CameraActivity.class);
                    intent.addFlags(67108864);
                    GridFragment.this.startActivityForResult(intent, 0);
                } else if (el instanceof Logic) {
                    if (NexoTalk.isConnected()) {
                        NexoService.queueAction(((Logic) el).action());
                        return;
                    }
                    intent = new Intent().setClass(GridFragment.this.getContext(), EditorLogicActivity.class);
                    intent.addFlags(67108864);
                    GridFragment.this.startActivityForResult(intent, 0);
                } else if (el instanceof GeolocationPoint) {
                    intent = new Intent().setClass(GridFragment.this.getContext(), EditorGeolocationPointActivity.class);
                    intent.addFlags(67108864);
                    GridFragment.this.startActivityForResult(intent, 0);
                } else if (!(el instanceof Scene)) {
                } else {
                    if (NexoTalk.isConnected()) {
                        Iterator<String> itra = ((Scene) el).restoreStates().iterator();
                        while (itra.hasNext()) {
                            String action = (String) itra.next();
                            if (!(action == null || action.isEmpty())) {
                                NexoService.queueAction(action);
                            }
                        }
                        return;
                    }
                    intent = new Intent().setClass(GridFragment.this.getContext(), EditorSceneActivity.class);
                    intent.addFlags(67108864);
                    GridFragment.this.startActivityForResult(intent, 0);
                }
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GridFragment$9 */
    class C20219 implements OnItemLongClickListener {
        C20219() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            IElement el = (IElement) GridFragment.this.elements.get(position);
            if (el == null) {
                return false;
            }
            NVModel.CURR_ELEMENT = el;
            if (NexoTalk.isConnected()) {
                if ((el instanceof Partition) && ((Partition) el).getSensors() != null && ((Partition) el).getSensors().size() > 0) {
                    MainActivity.setFragment(GridFragment.newInstance(el.getId().intValue()), "" + el.getId());
                    return true;
                } else if (el instanceof Dimmer) {
                    return false;
                } else {
                    Intent intent;
                    if (el instanceof RGBW) {
                        NexoService.freeze();
                        intent = new Intent().setClass(GridFragment.this.getContext(), ElementControl_RGBWActivity.class);
                        intent.addFlags(67108864);
                        GridFragment.this.startActivityForResult(intent, 0);
                        return true;
                    } else if (el instanceof Thermostat) {
                        return false;
                    } else {
                        if (el instanceof AnalogOutput) {
                            return false;
                        }
                        if (el instanceof VideophoneIP) {
                            intent = new Intent().setClass(GridFragment.this.getContext(), ElementControl_VideophoneActivity.class);
                            intent.addFlags(67108864);
                            GridFragment.this.startActivityForResult(intent, 0);
                        }
                    }
                }
            } else if (!(el instanceof Category)) {
                GridFragment.this.longPressMenuDialog.show();
            } else if (((Category) el).getUse().equals(NVModel.CATEGORY_VIDEOPHONES)) {
            }
            return true;
        }
    }

    public static GridFragment newInstance(int id) {
        GridFragment f = new GridFragment();
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
        Log.d("GridFragment", "onCreate(fid=" + this.fragmentId + ")");
        NexoTalk.addNexoTalkListener(this);
        GeoService.addGeoListener(this);
        CharSequence[] lpa_items = new CharSequence[]{getString(R.string.RoomFragment_SWLongPressDialog_EditItem), getString(R.string.RoomFragment_SWLongPressDialog_RemoveItem), getString(R.string.RoomFragment_SWLongPressDialog_ReorderItems)};
        Builder builder = new Builder(getContext());
        builder.setTitle(getString(R.string.RoomFragment_SWLongPressDialog_Title));
        builder.setItems(lpa_items, new C20091());
        this.longPressMenuDialog = builder.create();
        builder = new Builder(getContext());
        builder.setMessage(R.string.GridFragment_RemoveDialog_Question).setCancelable(false).setPositiveButton(R.string.GridFragment_RemoveDialog_OnlyFromPlacesItem, new C20124()).setNeutralButton(R.string.GridFragment_RemoveDialog_FromEverywhereItem, new C20113()).setNegativeButton(R.string.NO, new C20102());
        this.delDialog = builder.create();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.grid_fragment, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int layout = R.layout.cell;
        this.fragmentBackground = (ImageView) getView().findViewById(R.id.fragmentBackground);
        this.gridView = (DynamicGridView) getView().findViewById(R.id.gridView);
        if (this.elements == null) {
            this.elements = new ArrayList();
        }
        this.elements.clear();
        Log.d("GridFragment", "onActivityCreated(fid=" + this.fragmentId + ")");
        if (this.fragmentId > 0) {
            final IElement el = NVModel.getElementById(Integer.valueOf(this.fragmentId));
            if (el != null) {
                MainActivity.handler.post(new Runnable() {
                    public void run() {
                        ((MainActivity) MainActivity.getContext()).getSupportActionBar().setTitle(el.getName());
                    }
                });
                if (el instanceof ISet) {
                    if (((ISet) el).getBitmap() != null) {
                        Display display = ((MainActivity) MainActivity.getContext()).getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        this.fragmentBackground.setImageBitmap(BlurBuilder.blur(getContext(), Bitmap.createScaledBitmap(((ISet) el).getBitmap(), size.x / 2, size.y / 2, true)));
                    }
                    if (((ISet) el).getElements().size() <= 0) {
                        this.adapter = null;
                    } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_TEMPERATURE)) {
                        if (MainActivity.isLandscapeOrientation()) {
                            this.gridView.setNumColumns(2);
                        } else {
                            this.gridView.setNumColumns(1);
                        }
                        Iterator<IElement> itrv = ((ISet) el).getElements().iterator();
                        while (itrv.hasNext()) {
                            IElement v = (IElement) itrv.next();
                            if (v instanceof Ventilator) {
                                this.elements.add(v);
                            }
                        }
                        Iterator<IElement> itrt = ((ISet) el).getElements().iterator();
                        while (itrt.hasNext()) {
                            IElement t = (IElement) itrt.next();
                            if (t instanceof Thermometer) {
                                this.elements.add(t);
                            }
                        }
                        this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell_long, R.layout.cell_long2}, this.elements);
                    } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_ALARM)) {
                        if (MainActivity.isLandscapeOrientation()) {
                            this.gridView.setNumColumns(2);
                        } else {
                            this.gridView.setNumColumns(1);
                        }
                        this.elements.clear();
                        Iterator<IElement> itrp = ((ISet) el).getElements().iterator();
                        while (itrp.hasNext()) {
                            IElement p = (IElement) itrp.next();
                            if (p instanceof Partition) {
                                this.elements.add(p);
                            }
                        }
                        this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell_long2}, this.elements);
                    } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_GATES)) {
                        if (MainActivity.isLandscapeOrientation()) {
                            this.gridView.setNumColumns(4);
                        } else {
                            this.gridView.setNumColumns(2);
                        }
                        this.elements.clear();
                        itro = ((ISet) el).getElements().iterator();
                        while (itro.hasNext()) {
                            o = (IElement) itro.next();
                            if (((o instanceof Output) && ((Output) o).getFunc().equals(Output.Function.GATE)) || (o instanceof Gate)) {
                                this.elements.add(o);
                            }
                        }
                        this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell}, this.elements);
                    } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_AUTOMATION)) {
                        if (MainActivity.isLandscapeOrientation()) {
                            this.gridView.setNumColumns(5);
                        } else {
                            this.gridView.setNumColumns(3);
                        }
                        this.elements.clear();
                        itro = ((ISet) el).getElements().iterator();
                        while (itro.hasNext()) {
                            o = (IElement) itro.next();
                            if (((o instanceof Output) && ((Output) o).getFunc().equals(Output.Function.OTHER)) || (o instanceof OutputGroup) || (o instanceof AnalogOutput) || (o instanceof AnalogOutputGroup)) {
                                this.elements.add(o);
                            }
                        }
                        this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell}, this.elements);
                    } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_LOGICS)) {
                        if (MainActivity.isLandscapeOrientation()) {
                            this.gridView.setNumColumns(2);
                        } else {
                            this.gridView.setNumColumns(1);
                        }
                        this.elements.clear();
                        itro = ((ISet) el).getElements().iterator();
                        while (itro.hasNext()) {
                            o = (IElement) itro.next();
                            if (o instanceof Logic) {
                                this.elements.add(o);
                            }
                        }
                        this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell_long3}, this.elements);
                    } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_GEOLOCATION)) {
                        if (MainActivity.isLandscapeOrientation()) {
                            this.gridView.setNumColumns(2);
                        } else {
                            this.gridView.setNumColumns(1);
                        }
                        this.elements.clear();
                        itro = ((ISet) el).getElements().iterator();
                        while (itro.hasNext()) {
                            o = (IElement) itro.next();
                            if (o instanceof GeolocationPoint) {
                                this.elements.add(o);
                            }
                        }
                        this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell_long3}, this.elements);
                    } else {
                        if (MainActivity.isLandscapeOrientation()) {
                            this.gridView.setNumColumns(5);
                        } else {
                            this.gridView.setNumColumns(3);
                        }
                        this.elements.clear();
                        if (NVModel.getCategory(NVModel.CATEGORY_PLACES).getId().intValue() == this.fragmentId) {
                            if (MainActivity.isLandscapeOrientation()) {
                                this.gridView.setNumColumns(4);
                            } else {
                                this.gridView.setNumColumns(2);
                            }
                            layout = R.layout.cell_big;
                        }
                        this.elements = ((ISet) el).getElements();
                        this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{layout}, this.elements);
                    }
                } else if (el instanceof Partition) {
                    this.elements = ((Partition) el).getSensors();
                    if (this.elements != null && this.elements.size() > 0) {
                        this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell}, this.elements);
                    }
                }
            }
        } else {
            MainActivity.handler.post(new C20146());
            if (NVModel.getTopElements().size() > 0) {
                this.elements = NVModel.getTopElements();
                this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell}, this.elements);
            }
        }
        if (NexoTalk.isConnected()) {
            Log.d("GridFragment", "onActivityCreated(fid=" + this.fragmentId + "):NexoTalk.update()");
            NexoService.goForeground(NVModel.getCurrentElements());
        }
        if (this.adapter != null && this.adapter.getCount() > 0) {
            this.gridView.setAdapter((ListAdapter) this.adapter);
            this.gridView.setOnItemClickListener(new C20157());
            SliderRectView sliderRectView = (SliderRectView) getView().findViewById(R.id.sliderRectView);
            sliderRectView.init(5);
            final SliderRectView sliderRectView2 = sliderRectView;
            this.gridView.setOnTouchListener(new OnTouchListener() {
                int first_position = -1;
                int position = -1;
                float value = 0.0f;
                int visible_position = -1;

                /* renamed from: eu.nexwell.android.nexovision.GridFragment$8$2 */
                class C20182 implements Runnable {
                    C20182() {
                    }

                    public void run() {
                        ((IElement) ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).getItem(C20208.this.position)).setSelected(true);
                        ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).notifyDataSetChanged();
                    }
                }

                /* renamed from: eu.nexwell.android.nexovision.GridFragment$8$3 */
                class C20193 implements Runnable {
                    C20193() {
                    }

                    public void run() {
                        if (C20208.this.position >= 0) {
                            ((IElement) ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).getItem(C20208.this.position)).setSelected(false);
                            ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).notifyDataSetChanged();
                        }
                    }
                }

                public boolean onTouch(View v, MotionEvent event) {
                    float currentXPosition = event.getX();
                    float currentYPosition = event.getY();
                    IElement el;
                    if (event.getAction() == 0) {
                        this.first_position = GridFragment.this.gridView.getFirstVisiblePosition();
                        this.position = GridFragment.this.gridView.pointToPosition((int) currentXPosition, (int) currentYPosition);
                        this.visible_position = this.position - this.first_position;
                        Log.e("GridFragment", "POS=" + this.visible_position + " FVPOS=" + this.first_position);
                        if (this.position < 0) {
                            return false;
                        }
                        el = (IElement) GridFragment.this.elements.get(this.position);
                        if (el == null) {
                            return false;
                        }
                        NVModel.CURR_ELEMENT = el;
                        if (NexoTalk.isConnected() && ((el instanceof Dimmer) || (((el instanceof Thermometer) && ((Thermometer) el).getThermostat() != null) || (el instanceof AnalogOutput) || (el instanceof AnalogOutputGroup) || (el instanceof Ventilator)))) {
                            final float finalCurrentXPosition = currentXPosition;
                            Handler handler = MainActivity.handler;
                            Runnable c20171 = new Runnable() {

                                /* renamed from: eu.nexwell.android.nexovision.GridFragment$8$1$1 */
                                class C20161 implements Runnable {
                                    C20161() {
                                    }

                                    public void run() {
                                        ((IElement) ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).getItem(C20208.this.position)).setSelected(false);
                                        ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).notifyDataSetChanged();
                                    }
                                }

                                public void run() {
                                    int[] grid_location = new int[2];
                                    GridFragment.this.gridView.getLocationOnScreen(grid_location);
                                    int[] item_location = new int[2];
                                    GridFragment.this.gridView.getChildAt(C20208.this.visible_position).getLocationOnScreen(item_location);
                                    Log.d("GridFragment", "CHILD[" + C20208.this.visible_position + "].location(x=" + item_location[0] + ", y=" + item_location[1] + ")");
                                    C20208.this.value = sliderRectView2.show(item_location[1] - grid_location[1], GridFragment.this.gridView.getChildAt(C20208.this.visible_position).getHeight(), (int) finalCurrentXPosition, C20208.this.value);
                                    GridFragment.longPress = true;
                                    MainActivity.handler.post(new C20161());
                                }
                            };
                            GridFragment.longPressTimer = c20171;
                            handler.postDelayed(c20171, 500);
                            MainActivity.handler.post(new C20182());
                        }
                        return false;
                    } else if (event.getAction() == 2) {
                        this.value = sliderRectView2.move((int) currentXPosition, this.value);
                        return GridFragment.longPress;
                    } else if (event.getAction() != 1) {
                        return false;
                    } else {
                        if (this.position < 0) {
                            return false;
                        }
                        MainActivity.handler.post(new C20193());
                        sliderRectView2.hide();
                        if (GridFragment.longPress) {
                            GridFragment.longPress = false;
                            el = (IElement) GridFragment.this.elements.get(this.position);
                            if (el == null) {
                                return true;
                            }
                            NVModel.CURR_ELEMENT = el;
                            if (el instanceof Dimmer) {
                                NexoService.queueActionAndUpdate((ISwitch) el, ((Dimmer) el).on(Integer.valueOf((int) this.value)));
                            } else if (el instanceof AnalogOutput) {
                                NexoService.queueActionAndUpdate((ISwitch) el, ((AnalogOutput) el).on(Integer.valueOf((int) this.value)));
                            } else if ((el instanceof Thermometer) && ((Thermometer) el).getThermostat() != null) {
                                NexoService.queueActionAndUpdate(((Thermometer) el).getThermostat(), ((Thermometer) el).getThermostat().activate(new Float(this.value)));
                            } else if (el instanceof Ventilator) {
                                NexoService.queueActionAndUpdate((ISwitch) el, ((Ventilator) el).on(Integer.valueOf((int) this.value)));
                            }
                            return true;
                        }
                        if (GridFragment.longPressTimer != null) {
                            MainActivity.handler.removeCallbacks(GridFragment.longPressTimer);
                        }
                        return false;
                    }
                }
            });
            this.gridView.setOnItemLongClickListener(new C20219());
            this.gridView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (!GridFragment.longPress && GridFragment.longPressTimer != null) {
                        MainActivity.handler.removeCallbacks(GridFragment.longPressTimer);
                    }
                }

                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
        }
    }

    public void refresh() {
        MainActivity.handler.post(new Runnable() {
            public void run() {
                int layout = R.layout.cell;
                if (GridFragment.this.gridView.getAdapter() != null) {
                    if (GridFragment.this.fragmentId > 0) {
                        if (NVModel.getCategory(NVModel.CATEGORY_TEMPERATURE).getId().intValue() == GridFragment.this.fragmentId) {
                            GridFragment.this.gridView.setNumColumns(1);
                            layout = R.layout.cell_long;
                        }
                        IElement el = NVModel.getElementById(Integer.valueOf(GridFragment.this.fragmentId));
                        if (el instanceof ISet) {
                            if (((ISet) el).getElements().size() > 0) {
                                GridFragment.this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{layout}, ((ISet) el).getElements());
                            }
                        } else if (el instanceof Partition) {
                            GridFragment.this.elements = ((Partition) el).getSensors();
                            GridFragment.this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{layout}, GridFragment.this.elements);
                        } else if (NVModel.getTopElements().size() > 0) {
                            GridFragment.this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{layout}, NVModel.getTopElements());
                        }
                    } else if (NVModel.getTopElements().size() > 0) {
                        GridFragment.this.adapter = new GridListCellAdapter(MainActivity.getContext(), new int[]{R.layout.cell}, NVModel.getTopElements());
                    }
                    ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).notifyDataSetChanged();
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        this.elements.clear();
        this.elements = null;
        GeoService.removeGeoListener(this);
        NexoTalk.removeNexoTalkListener(this);
    }

    public void onImport(int type, int iterator) {
    }

    public void onImportEnd(ArrayList<Integer> arrayList) {
    }

    public void onStatusUpdate(IElement el, boolean finish) {
        MainActivity.handler.post(new Runnable() {
            public void run() {
                if (GridFragment.this.gridView.getAdapter() != null) {
                    ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).notifyDataSetChanged();
                }
            }
        });
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
        if (this.fragmentId > 0) {
            IElement el = NVModel.getElementById(Integer.valueOf(this.fragmentId));
            if ((el instanceof ISet) && ((ISet) el).getElements().size() > 0 && (el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_GEOLOCATION)) {
                MainActivity.handler.post(new Runnable() {
                    public void run() {
                        if (GridFragment.this.gridView.getAdapter() != null) {
                            ((GridListCellAdapter) GridFragment.this.gridView.getAdapter()).notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    public void connectionProcessInfo(String info, String error) {
    }

    public boolean isEditMode() {
        return this.gridView.isEditMode();
    }

    public void setEditMode(boolean em) {
        if (em) {
            this.gridView.startEditMode();
            return;
        }
        this.gridView.stopEditMode();
        if (this.gridView.getAdapter() != null) {
            ArrayList<IElement> elementsOrdered = new ArrayList();
            for (IElement obj : ((GridListCellAdapter) this.gridView.getAdapter()).getItems()) {
                if (obj instanceof IElement) {
                    elementsOrdered.add(obj);
                }
            }
            IElement el = NVModel.getElementById(Integer.valueOf(this.fragmentId));
            if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_TEMPERATURE)) {
                Iterator<IElement> itre = ((ISet) el).getElements().iterator();
                while (itre.hasNext()) {
                    IElement e = (IElement) itre.next();
                    if ((e instanceof Ventilator) || (e instanceof Thermometer)) {
                        this.elements.remove(e);
                    }
                }
                this.elements.addAll(elementsOrdered);
            } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_ALARM)) {
                Iterator<IElement> itrp = ((ISet) el).getElements().iterator();
                while (itrp.hasNext()) {
                    IElement p = (IElement) itrp.next();
                    if (p instanceof Partition) {
                        this.elements.remove(p);
                    }
                }
                this.elements.addAll(elementsOrdered);
            } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_GATES)) {
                itro = ((ISet) el).getElements().iterator();
                while (itro.hasNext()) {
                    o = (IElement) itro.next();
                    if (((o instanceof Output) && ((Output) o).getFunc().equals(Output.Function.GATE)) || (o instanceof Gate)) {
                        this.elements.remove(o);
                    }
                }
                this.elements.addAll(elementsOrdered);
            } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_AUTOMATION)) {
                itro = ((ISet) el).getElements().iterator();
                while (itro.hasNext()) {
                    o = (IElement) itro.next();
                    if ((o instanceof Output) && ((Output) o).getFunc().equals(Output.Function.OTHER)) {
                        this.elements.remove(o);
                    }
                }
                this.elements.addAll(elementsOrdered);
            } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_LOGICS)) {
                itro = ((ISet) el).getElements().iterator();
                while (itro.hasNext()) {
                    o = (IElement) itro.next();
                    if (o instanceof Logic) {
                        this.elements.remove(o);
                    }
                }
                this.elements.addAll(elementsOrdered);
            } else if ((el instanceof Category) && ((Category) el).getUse().equals(NVModel.CATEGORY_GEOLOCATION)) {
                itro = ((ISet) el).getElements().iterator();
                while (itro.hasNext()) {
                    o = (IElement) itro.next();
                    if (o instanceof GeolocationPoint) {
                        this.elements.remove(o);
                    }
                }
                this.elements.addAll(elementsOrdered);
            } else {
                this.elements.clear();
                this.elements.addAll(elementsOrdered);
                Log.d("GridFragment", "ELEMENTS: " + this.elements);
            }
            ((ISet) el).setElements(this.elements);
        }
    }
}
