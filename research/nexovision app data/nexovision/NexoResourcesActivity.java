package eu.nexwell.android.nexovision;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.media.MediaRouter.GlobalMediaRouter.CallbackHandler;
import android.support.v7.media.SystemMediaRouteProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import eu.nexwell.android.nexovision.CheckListAdapter.CheckListAdapterListener;
import eu.nexwell.android.nexovision.communication.NexoService;
import eu.nexwell.android.nexovision.communication.NexoTalk;
import eu.nexwell.android.nexovision.communication.NexoTalkListener;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Output;
import eu.nexwell.android.nexovision.model.Output.Function;
import eu.nexwell.android.nexovision.model.Partition;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.Thermostat;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import nexovision.android.nexwell.eu.nexovision.R;

public class NexoResourcesActivity extends AppCompatActivity implements NexoTalkListener, CheckListAdapterListener {
    private static ListView ResourcesList;
    private static AlertDialog additionFinishedDialog;
    private static ArrayList<IElement> candidatesForGatesList = new ArrayList();
    private static CheckBox checkBox;
    private static Context context;
    private static FloatingActionButton fab;
    public static Handler handler;
    private static int humanDecisionAlertDialogsCount = 0;
    private static ArrayList<AlertDialog> humanDecisionAlertDialogsList = new ArrayList();
    private static ProgressDialog importDialog;
    private static AlertDialog importFinishedDialog;
    private static Thread importThread;
    private static int list_level;
    private static AlertDialog notConnectedDialog;
    private static CheckListAdapter res_adapter;
    private static int selected_res_to_add;
    private static LinkedHashMap<Integer, ArrayList<String>> switches = new LinkedHashMap();
    private static HashMap<String, ArrayList<String>> switches_data = new HashMap();
    public static Integer type = null;
    public static LinkedHashMap<Integer, String> typeList = new LinkedHashMap();
    private boolean flag_add_all = false;
    private boolean flag_import_after_connect = false;

    /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$1 */
    class C20411 implements OnClickListener {
        C20411() {
        }

        public void onClick(View view) {
            NexoResourcesActivity.humanDecisionAlertDialogsList.clear();
            NexoResourcesActivity.humanDecisionAlertDialogsCount = 0;
            NexoResourcesActivity.candidatesForGatesList.clear();
            int add_count = 0;
            SparseBooleanArray checkedList;
            int c;
            ArrayList<String> res;
            ArrayList<String> data;
            if (NexoResourcesActivity.type == null) {
                if (((CheckListAdapter) NexoResourcesActivity.ResourcesList.getAdapter()).getCheckedItemPositions().size() > 0) {
                    checkedList = ((CheckListAdapter) NexoResourcesActivity.ResourcesList.getAdapter()).getCheckedItemPositions();
                    ArrayList<Integer> keys = new ArrayList(NexoResourcesActivity.typeList.keySet());
                    c = 0;
                    while (c < NexoResourcesActivity.ResourcesList.getAdapter().getCount()) {
                        if (checkedList.get(c)) {
                            res = (ArrayList) NexoResourcesActivity.switches.get(keys.get(c));
                            if (res != null) {
                                for (int r = 0; r < res.size(); r++) {
                                    data = null;
                                    if (((String) NexoResourcesActivity.typeList.get(keys.get(c))).equals(NVModel.EL_TYPE_PARTITION24H) || ((String) NexoResourcesActivity.typeList.get(keys.get(c))).equals(NVModel.EL_TYPE_THERMOSTAT)) {
                                        data = (ArrayList) NexoResourcesActivity.switches_data.get(((String) res.get(r)).toString());
                                    }
                                    NexoResourcesActivity.this.addElement((String) NexoResourcesActivity.typeList.get(keys.get(c)), ((String) res.get(r)).toString(), data);
                                    add_count++;
                                }
                            }
                        }
                        c++;
                    }
                }
            } else if (((CheckListAdapter) NexoResourcesActivity.ResourcesList.getAdapter()).getCheckedItemPositions().size() > 0) {
                checkedList = ((CheckListAdapter) NexoResourcesActivity.ResourcesList.getAdapter()).getCheckedItemPositions();
                ArrayList arrayList = new ArrayList(NexoResourcesActivity.typeList.keySet());
                for (c = 0; c < NexoResourcesActivity.ResourcesList.getAdapter().getCount(); c++) {
                    if (checkedList.get(c)) {
                        Integer t = NexoResourcesActivity.type;
                        res = (ArrayList) NexoResourcesActivity.switches.get(t);
                        if (res != null) {
                            data = null;
                            if (((String) NexoResourcesActivity.typeList.get(t)).equals(NVModel.EL_TYPE_PARTITION24H) || ((String) NexoResourcesActivity.typeList.get(t)).equals(NVModel.EL_TYPE_THERMOSTAT)) {
                                data = (ArrayList) NexoResourcesActivity.switches_data.get(((String) res.get(c)).toString());
                            }
                            NexoResourcesActivity.this.addElement((String) NexoResourcesActivity.typeList.get(t), ((String) res.get(c)).toString(), data);
                            add_count++;
                        }
                    }
                }
            }
            if (add_count > 0) {
                NexoResourcesActivity.fab.setVisibility(4);
                NexoResourcesActivity.this.checkAll(NexoResourcesActivity.ResourcesList, false);
                NexoResourcesActivity.checkBox.setChecked(false);
                if (NexoResourcesActivity.candidatesForGatesList.size() > 0) {
                    ArrayList<String> candidatesNamesList = new ArrayList();
                    Iterator<IElement> itrg = NexoResourcesActivity.candidatesForGatesList.iterator();
                    while (itrg.hasNext()) {
                        candidatesNamesList.add(((IElement) itrg.next()).getName());
                    }
                    CharSequence[] items = (CharSequence[]) candidatesNamesList.toArray(new CharSequence[0]);
                    final boolean[] checks = new boolean[items.length];
                    for (int i = 0; i < checks.length; i++) {
                        checks[i] = false;
                    }
                    Builder builder = new Builder(NexoResourcesActivity.getContext());
                    builder.setTitle("Select " + NexoResourcesActivity.this.getResources().getString(R.string.ResourceCategoryName_Gates) + ":").setCancelable(false).setMultiChoiceItems(items, null, new OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checks[which] = isChecked;
                        }
                    }).setPositiveButton(NexoResourcesActivity.getContext().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int which) {
                            for (int i = 0; i < checks.length; i++) {
                                if (checks[i]) {
                                    IElement el = (IElement) NexoResourcesActivity.candidatesForGatesList.get(i);
                                    ((Output) el).setFunc(Function.GATE);
                                    String cat = NVModel.getElementTypeDefaultCategory(el.getType());
                                    if (cat == null) {
                                        cat = NVModel.CATEGORY_AUTOMATION;
                                    }
                                    Category tmp_cat = NVModel.getCategory(cat);
                                    if (tmp_cat != null) {
                                        tmp_cat.removeElement(el);
                                    }
                                    tmp_cat = NVModel.getCategory(NVModel.CATEGORY_GATES);
                                    if (tmp_cat != null) {
                                        tmp_cat.addElement(el);
                                    }
                                }
                            }
                            MainActivity.handler.postDelayed(new Runnable() {
                                public void run() {
                                    NexoResourcesActivity.humanDecisionAlertDialogsList.remove(dialog);
                                    if (NexoResourcesActivity.humanDecisionAlertDialogsList.size() > 0) {
                                        AlertDialog alertDialog = (AlertDialog) NexoResourcesActivity.humanDecisionAlertDialogsList.get(0);
                                        if (alertDialog != null) {
                                            alertDialog.show();
                                            int titleId = NexoResourcesActivity.this.getResources().getIdentifier("alertTitle", "id", SystemMediaRouteProvider.PACKAGE_NAME);
                                            if (titleId > 0) {
                                                TextView dialogTitle = (TextView) alertDialog.findViewById(titleId);
                                                if (dialogTitle != null) {
                                                    int total = NexoResourcesActivity.humanDecisionAlertDialogsCount;
                                                    alertDialog.setTitle(((total - NexoResourcesActivity.humanDecisionAlertDialogsList.size()) + 1) + "/" + total + " " + dialogTitle.getText().toString());
                                                }
                                            }
                                        }
                                        dialog.dismiss();
                                        return;
                                    }
                                    dialog.dismiss();
                                    if (NexoResourcesActivity.this.flag_add_all) {
                                        NexoResourcesActivity.this.flag_add_all = false;
                                        NexoResourcesActivity.this.finish();
                                    }
                                }
                            }, 400);
                        }
                    });
                    AlertDialog outputFunc_HumanDecisionCheckListDialog = builder.create();
                    outputFunc_HumanDecisionCheckListDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                    NexoResourcesActivity.humanDecisionAlertDialogsList.add(outputFunc_HumanDecisionCheckListDialog);
                }
                NexoResourcesActivity.humanDecisionAlertDialogsCount = NexoResourcesActivity.humanDecisionAlertDialogsList.size();
                NexoResourcesActivity.additionFinishedDialog.setTitle(add_count + " " + NexoResourcesActivity.getContext().getString(R.string.ResourcesActivity_AdditionFinishedDialog_Title));
                if (NexoResourcesActivity.humanDecisionAlertDialogsList.size() > 0) {
                    NexoResourcesActivity.additionFinishedDialog.setMessage(NexoResourcesActivity.getContext().getString(R.string.ResourcesActivity_AdditionFinishedDialog_Message));
                }
                NexoResourcesActivity.additionFinishedDialog.show();
                return;
            }
            Snackbar.make(view, NexoResourcesActivity.getContext().getString(R.string.ResourcesActivity_NoAddMessage), 0).show();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$2 */
    class C20422 implements DialogInterface.OnClickListener {
        C20422() {
        }

        public void onClick(DialogInterface dialog, int which) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(NexoResourcesActivity.getContext());
            String ip = sharedPref.getString("pref_rememberme_login_ip", null);
            String passwd = sharedPref.getString("pref_rememberme_login_passwd", null);
            Boolean encryption = Boolean.valueOf(false);
            if (ip == null || ip.equals("") || passwd == null || passwd.equals("")) {
                Intent intent = new Intent().setClass(NexoResourcesActivity.getContext(), NexoLoginActivity.class);
                intent.addFlags(67108864);
                NexoResourcesActivity.this.startActivityForResult(intent, 0);
                return;
            }
            String port = "1024";
            String[] target = ip.split(":");
            if (!(target.length <= 0 || target[0] == null || target[0].equals(""))) {
                if (target.length <= 1 || target[1] == null || target[1].equals("")) {
                    port = "1024";
                } else {
                    port = target[1];
                }
            }
            NexoResourcesActivity.this.flag_import_after_connect = true;
            NexoService.start((MainActivity) MainActivity.getContext());
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$3 */
    class C20433 implements DialogInterface.OnClickListener {
        C20433() {
        }

        public void onClick(DialogInterface dialog, int which) {
            for (int i = 0; i < NexoResourcesActivity.res_adapter.getCount(); i++) {
                NexoResourcesActivity.res_adapter.setItemChecked(i, true);
            }
            NexoResourcesActivity.res_adapter.refresh();
            NexoResourcesActivity.this.flag_add_all = true;
            NexoResourcesActivity.fab.performClick();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$4 */
    class C20444 implements DialogInterface.OnClickListener {
        C20444() {
        }

        public void onClick(DialogInterface dialog, int id) {
            NexoResourcesActivity.this.flag_add_all = false;
            dialog.dismiss();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$5 */
    class C20455 implements DialogInterface.OnClickListener {
        C20455() {
        }

        public void onClick(DialogInterface dialog, int id) {
            if (NexoResourcesActivity.humanDecisionAlertDialogsList.size() > 0) {
                AlertDialog alertDialog = (AlertDialog) NexoResourcesActivity.humanDecisionAlertDialogsList.get(0);
                if (alertDialog != null) {
                    alertDialog.show();
                    int titleId = NexoResourcesActivity.this.getResources().getIdentifier("alertTitle", "id", SystemMediaRouteProvider.PACKAGE_NAME);
                    if (titleId > 0) {
                        TextView dialogTitle = (TextView) alertDialog.findViewById(titleId);
                        if (dialogTitle != null) {
                            int total = NexoResourcesActivity.humanDecisionAlertDialogsCount;
                            alertDialog.setTitle(((total - NexoResourcesActivity.humanDecisionAlertDialogsList.size()) + 1) + "/" + total + " " + dialogTitle.getText().toString());
                        }
                    }
                }
                dialog.dismiss();
                return;
            }
            dialog.dismiss();
            if (NexoResourcesActivity.this.flag_add_all) {
                NexoResourcesActivity.this.flag_add_all = false;
                NexoResourcesActivity.this.finish();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$6 */
    class C20466 implements OnItemClickListener {
        C20466() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            boolean z = false;
            if (NexoResourcesActivity.list_level == 1) {
                Boolean checked = Boolean.valueOf(NexoResourcesActivity.res_adapter.checkList.get(arg2));
                SparseBooleanArray sparseBooleanArray = NexoResourcesActivity.res_adapter.checkList;
                if (!checked.booleanValue()) {
                    z = true;
                }
                sparseBooleanArray.put(arg2, z);
                NexoResourcesActivity.res_adapter.refresh();
                return;
            }
            Integer t = (Integer) new ArrayList(NexoResourcesActivity.typeList.keySet()).get(arg2);
            if (NexoResourcesActivity.switches.get(t) != null && ((ArrayList) NexoResourcesActivity.switches.get(t)).size() > 0) {
                NexoResourcesActivity.type = t;
                NexoResourcesActivity.res_adapter = new CheckListAdapter(NexoResourcesActivity.getContext(), R.layout.checklist_item, (List) NexoResourcesActivity.switches.get(t), null, false);
                NexoResourcesActivity.res_adapter.setCheckListAdapterListener(NexoResourcesActivity.this);
                NexoResourcesActivity.list_level = 1;
                NexoResourcesActivity.ResourcesList.setAdapter(NexoResourcesActivity.res_adapter);
                NexoResourcesActivity.ResourcesList.invalidate();
                NexoResourcesActivity.fab.setVisibility(4);
            } else if (NexoTalk.isConnected()) {
                ArrayList<Integer> res = new ArrayList();
                res.add(Integer.valueOf(t.intValue()));
                if (res.size() > 0) {
                    NexoResourcesActivity.this.importResources(res);
                } else {
                    Snackbar.make(arg1, NexoResourcesActivity.getContext().getString(R.string.ResourcesActivity_NoSelMessage), 0).show();
                }
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$7 */
    class C20477 implements Runnable {
        C20477() {
        }

        public void run() {
            NexoResourcesActivity.ResourcesList.setVisibility(4);
            for (int i = 0; i < NexoResourcesActivity.res_adapter.getCount(); i++) {
                NexoResourcesActivity.res_adapter.setItemChecked(i, true);
            }
            NexoResourcesActivity.res_adapter.refresh();
            if (NexoTalk.isConnected()) {
                NexoResourcesActivity.this.startImport();
            } else {
                NexoResourcesActivity.notConnectedDialog.show();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$9 */
    class C20519 implements Runnable {

        /* renamed from: eu.nexwell.android.nexovision.NexoResourcesActivity$9$1 */
        class C20501 implements OnCancelListener {
            C20501() {
            }

            public void onCancel(DialogInterface dialog) {
                NexoService.goBackground();
            }
        }

        C20519() {
        }

        public void run() {
            NexoResourcesActivity.importDialog = new ProgressDialog(NexoResourcesActivity.getContext());
            NexoResourcesActivity.importDialog.setTitle(NexoResourcesActivity.getContext().getString(R.string.ResourcesActivity_LoadResDialog_Title));
            NexoResourcesActivity.importDialog.setMessage("?: 0");
            NexoResourcesActivity.importDialog.setCanceledOnTouchOutside(false);
            NexoResourcesActivity.importDialog.setOnCancelListener(new C20501());
            NexoResourcesActivity.importDialog.show();
        }
    }

    class IMGFileFilter implements FileFilter {
        IMGFileFilter() {
        }

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".png") || pathname.getName().endsWith(".jpg") || pathname.getName().endsWith(".bmp") || pathname.getName().endsWith(".gif")) {
                return true;
            }
            return false;
        }
    }

    static {
        typeList.put(Integer.valueOf(1), NVModel.EL_TYPE_SENSOR);
        typeList.put(Integer.valueOf(2), NVModel.EL_TYPE_ANALOGSENSOR);
        typeList.put(Integer.valueOf(3), NVModel.EL_TYPE_PARTITION);
        typeList.put(Integer.valueOf(4), NVModel.EL_TYPE_PARTITION24H);
        typeList.put(Integer.valueOf(5), NVModel.EL_TYPE_OUTPUT);
        typeList.put(Integer.valueOf(6), NVModel.EL_TYPE_OUTPUT_GROUP);
        typeList.put(Integer.valueOf(7), NVModel.EL_TYPE_LIGHT);
        typeList.put(Integer.valueOf(8), NVModel.EL_TYPE_DIMMER);
        typeList.put(Integer.valueOf(9), NVModel.EL_TYPE_LIGHT_GROUP);
        typeList.put(Integer.valueOf(10), NVModel.EL_TYPE_ANALOGOUTPUT);
        typeList.put(Integer.valueOf(11), NVModel.EL_TYPE_ANALOGOUTPUT_GROUP);
        typeList.put(Integer.valueOf(12), NVModel.EL_TYPE_RGBW);
        typeList.put(Integer.valueOf(13), NVModel.EL_TYPE_RGBW_GROUP);
        typeList.put(Integer.valueOf(14), NVModel.EL_TYPE_BLIND);
        typeList.put(Integer.valueOf(15), NVModel.EL_TYPE_BLIND_GROUP);
        typeList.put(Integer.valueOf(16), NVModel.EL_TYPE_THERMOMETER);
        typeList.put(Integer.valueOf(17), NVModel.EL_TYPE_THERMOSTAT);
        typeList.put(Integer.valueOf(18), NVModel.EL_TYPE_THERMOSTAT_GROUP);
        typeList.put(Integer.valueOf(257), NVModel.EL_TYPE_GATE);
        typeList.put(Integer.valueOf(CallbackHandler.MSG_ROUTE_REMOVED), NVModel.EL_TYPE_VENTILATOR);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_nexoresources);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C20411());
        }
        ResourcesList = (ListView) findViewById(R.id.ResourcesList);
        NexoTalk.addNexoTalkListener(this);
        list_level = 0;
        selected_res_to_add = 0;
        Builder builder = new Builder(this);
        builder.setTitle("Cannot import resources. No connection with Nexo.").setMessage("Connect now?").setPositiveButton("Yes", new C20422()).setNegativeButton("No", null);
        notConnectedDialog = builder.create();
        builder = new Builder(this);
        builder.setTitle(getContext().getString(R.string.ResourcesActivity_LoadResFinishedDialog_Title)).setMessage(getContext().getString(R.string.ResourcesActivity_LoadResFinishedDialog_Message)).setCancelable(true).setNeutralButton(getContext().getString(R.string.ResourcesActivity_LoadResFinishedDialog_ButtonSelect), new C20444()).setPositiveButton(getContext().getString(R.string.ResourcesActivity_LoadResFinishedDialog_ButtonAddAll), new C20433());
        importFinishedDialog = builder.create();
        builder = new Builder(this);
        builder.setCancelable(false).setPositiveButton(getContext().getString(R.string.OK), new C20455());
        additionFinishedDialog = builder.create();
        ArrayList<String> labelList = new ArrayList();
        ArrayList<String> valueList = new ArrayList();
        int i = 0;
        for (String t : typeList.values()) {
            if (NVModel.getElementTypeName(getContext(), t) != null) {
                labelList.add(NVModel.getElementTypeName(context, t));
                Log.e("NexoResourcesActivity", "Res1: " + NVModel.getElementTypeName(context, t));
                if (switches.get(Integer.valueOf(i)) == null || ((ArrayList) switches.get(Integer.valueOf(i))).size() <= 0) {
                    valueList.add(null);
                } else {
                    valueList.add("" + ((ArrayList) switches.get(Integer.valueOf(i))).size());
                }
            } else {
                Log.e("NexoResourcesActivity", "Res2: " + t);
                labelList.add(t);
                valueList.add(null);
            }
            i++;
        }
        res_adapter = new CheckListAdapter(this, R.layout.checklist_item, labelList, valueList, false);
        res_adapter.setCheckListAdapterListener(this);
        ResourcesList.setAdapter(res_adapter);
        fab.setVisibility(4);
        ResourcesList.setOnItemClickListener(new C20466());
        handler.post(new C20477());
    }

    public static Context getContext() {
        return context;
    }

    private void addElement(String type, String resource, ArrayList<String> data) {
        final IElement temp_element = NVModel.newElement(type);
        temp_element.setName(resource);
        String cat = NVModel.getElementTypeDefaultCategory(type);
        if (cat == null) {
            cat = NVModel.CATEGORY_AUTOMATION;
        }
        Category tmp_cat = NVModel.getCategory(cat);
        if (tmp_cat == null) {
            tmp_cat = new Category();
            tmp_cat.setName(cat);
            NVModel.addCategory(tmp_cat);
        }
        tmp_cat.addElement(temp_element);
        if (temp_element instanceof ISwitch) {
            ((ISwitch) temp_element).setResource(resource);
        }
        NVModel.addElement(temp_element);
        if (type.equals(NVModel.EL_TYPE_PARTITION24H) && (temp_element instanceof Partition)) {
            if (data != null) {
                Log.d("NexoResourcesActivity", "Res(" + temp_element.getName() + ").data=" + data);
                Integer mode = Integer.valueOf(Integer.parseInt((String) data.get(0)));
                Log.d("NexoResourcesActivity", "Res(" + temp_element.getName() + ").data.mode=" + mode);
                if (mode != null) {
                    ((Partition) temp_element).setFunc((Partition.Function) Partition._funclist.get(mode.intValue() - 1));
                    return;
                }
                return;
            }
            ArrayList<String> useNamesList = new ArrayList();
            Iterator<Partition.Function> itru = Partition._funclist.iterator();
            while (itru.hasNext()) {
                Partition.Function use = (Partition.Function) itru.next();
                if (use != Partition.Function.COMMON) {
                    useNamesList.add(getContext().getString(use.getResLabel()));
                }
            }
            CharSequence[] items = (CharSequence[]) useNamesList.toArray(new CharSequence[0]);
            Builder builder = new Builder(this);
            builder.setTitle(" " + getResources().getString(R.string.ResourceTypeName_Partition) + ": " + temp_element.getName() + "\nSelect function:").setCancelable(false).setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, int which) {
                    ((Partition) temp_element).setFunc((Partition.Function) Partition._funclist.get(which + 1));
                    MainActivity.handler.postDelayed(new Runnable() {
                        public void run() {
                            NexoResourcesActivity.humanDecisionAlertDialogsList.remove(dialog);
                            if (NexoResourcesActivity.humanDecisionAlertDialogsList.size() > 0) {
                                AlertDialog alertDialog = (AlertDialog) NexoResourcesActivity.humanDecisionAlertDialogsList.get(0);
                                if (alertDialog != null) {
                                    alertDialog.show();
                                    int titleId = NexoResourcesActivity.this.getResources().getIdentifier("alertTitle", "id", SystemMediaRouteProvider.PACKAGE_NAME);
                                    if (titleId > 0) {
                                        TextView dialogTitle = (TextView) alertDialog.findViewById(titleId);
                                        if (dialogTitle != null) {
                                            int total = NexoResourcesActivity.humanDecisionAlertDialogsCount;
                                            alertDialog.setTitle(((total - NexoResourcesActivity.humanDecisionAlertDialogsList.size()) + 1) + "/" + total + " " + dialogTitle.getText().toString());
                                        }
                                    }
                                }
                                dialog.dismiss();
                                return;
                            }
                            dialog.dismiss();
                            if (NexoResourcesActivity.this.flag_add_all) {
                                NexoResourcesActivity.this.flag_add_all = false;
                                NexoResourcesActivity.this.finish();
                            }
                        }
                    }, 400);
                }
            });
            AlertDialog partition24hFunc_HumanDecisionCheckListDialog = builder.create();
            partition24hFunc_HumanDecisionCheckListDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            humanDecisionAlertDialogsList.add(partition24hFunc_HumanDecisionCheckListDialog);
        } else if (type.equals(NVModel.EL_TYPE_THERMOSTAT) && (temp_element instanceof Thermostat) && data != null) {
            Log.d("NexoResourcesActivity", "Res(" + temp_element.getName() + ").data=" + data);
            String thermometer_name = (String) data.get(0);
            Integer min = Integer.valueOf(Integer.parseInt((String) data.get(1)));
            Integer max = Integer.valueOf(Integer.parseInt((String) data.get(2)));
            Log.d("NexoResourcesActivity", "Res(" + temp_element.getName() + ").data.thermometer=" + thermometer_name);
            Log.d("NexoResourcesActivity", "Res(" + temp_element.getName() + ").data.min=" + min);
            Log.d("NexoResourcesActivity", "Res(" + temp_element.getName() + ").data.max=" + max);
            if (min != null) {
                ((Thermostat) temp_element).setMin(Float.valueOf(min.floatValue()));
            }
            if (max != null) {
                ((Thermostat) temp_element).setMax(Float.valueOf(max.floatValue()));
            }
            if (thermometer_name != null && !thermometer_name.isEmpty()) {
                Iterator<IElement> itrt = NVModel.getElementsByType(NVModel.EL_TYPE_THERMOMETER).iterator();
                while (itrt.hasNext()) {
                    IElement el = (IElement) itrt.next();
                    if (el != null && (el instanceof Thermometer) && el.getName().equals(thermometer_name)) {
                        ((Thermometer) el).setThermostat((Thermostat) temp_element);
                    }
                }
            }
        }
    }

    private void importResources(ArrayList<Integer> _types) {
        handler.post(new C20519());
        NexoService.doImport(_types);
    }

    public void onImport(final int type, final int iterator) {
        handler.post(new Runnable() {
            public void run() {
                NexoResourcesActivity.importDialog.setMessage(NVModel.getElementTypeName(NexoResourcesActivity.context, (String) NexoResourcesActivity.typeList.get(Integer.valueOf(type))) + ": " + iterator);
            }
        });
    }

    public void onImportEnd(final ArrayList<Integer> _types) {
        if (_types == null) {
            handler.post(new Runnable() {
                public void run() {
                    NexoResourcesActivity.checkBox.setChecked(false);
                    NexoResourcesActivity.importDialog.dismiss();
                    NexoResourcesActivity.this.flag_add_all = false;
                    NexoService.goBackground();
                }
            });
            return;
        }
        TreeMap<Integer, ArrayList<String>> resources = NexoService.getImportedResources();
        if (resources != null) {
            for (int t = 0; t < _types.size(); t++) {
                switches.put(_types.get(t), resources.get(_types.get(t)));
            }
        }
        switches_data = (HashMap) NexoService.getImportedResourcesData().clone();
        handler.post(new Runnable() {
            public void run() {
                NexoResourcesActivity.ResourcesList.setVisibility(0);
                NexoResourcesActivity.checkBox.setChecked(false);
                NexoResourcesActivity.importDialog.dismiss();
                NexoResourcesActivity.this.flag_add_all = false;
                NexoResourcesActivity.importFinishedDialog.show();
                TreeMap<Integer, ArrayList<String>> resources = NexoService.getImportedResources();
                if (resources != null && resources.size() > 0) {
                    if (_types.size() == 1) {
                        NexoResourcesActivity.type = (Integer) _types.get(0);
                        NexoResourcesActivity.res_adapter = new CheckListAdapter(NexoResourcesActivity.getContext(), R.layout.checklist_item, (List) NexoResourcesActivity.switches.get(_types.get(0)), null, false);
                        NexoResourcesActivity.res_adapter.setCheckListAdapterListener(NexoResourcesActivity.this);
                        NexoResourcesActivity.fab.setVisibility(4);
                        NexoResourcesActivity.list_level = 1;
                        NexoResourcesActivity.ResourcesList.setAdapter(NexoResourcesActivity.res_adapter);
                        NexoResourcesActivity.ResourcesList.invalidate();
                    } else {
                        NexoResourcesActivity.type = null;
                        ArrayList<String> labelList = new ArrayList();
                        ArrayList<String> valueList = new ArrayList();
                        ArrayList<Integer> keys = new ArrayList(NexoResourcesActivity.typeList.keySet());
                        int i = 0;
                        for (String t : NexoResourcesActivity.typeList.values()) {
                            if (NVModel.getElementTypeName(NexoResourcesActivity.context, t) == null) {
                                labelList.add(t);
                                valueList.add(null);
                            } else if (NexoResourcesActivity.switches.get(keys.get(i)) == null || ((ArrayList) NexoResourcesActivity.switches.get(keys.get(i))).size() <= 0) {
                                labelList.add(NVModel.getElementTypeName(NexoResourcesActivity.context, t));
                                valueList.add(null);
                            } else {
                                labelList.add(NVModel.getElementTypeName(NexoResourcesActivity.context, t) + " (" + ((ArrayList) NexoResourcesActivity.switches.get(keys.get(i))).size() + ")");
                                valueList.add("" + ((ArrayList) NexoResourcesActivity.switches.get(keys.get(i))).size());
                            }
                            i++;
                        }
                        NexoResourcesActivity.res_adapter = new CheckListAdapter(NexoResourcesActivity.getContext(), R.layout.checklist_item, labelList, valueList, false);
                        NexoResourcesActivity.res_adapter.setCheckListAdapterListener(NexoResourcesActivity.this);
                        NexoResourcesActivity.fab.setVisibility(4);
                        NexoResourcesActivity.list_level = 0;
                        NexoResourcesActivity.ResourcesList.setAdapter(NexoResourcesActivity.res_adapter);
                        NexoResourcesActivity.ResourcesList.invalidate();
                    }
                }
                NexoService.goBackground();
            }
        });
    }

    public void onStatusUpdate(IElement el, boolean finish) {
    }

    public void onPartitionAlarm(IElement el) {
    }

    public void connectionStatus(boolean connected) {
        if (connected && this.flag_import_after_connect) {
            this.flag_import_after_connect = false;
            ResourcesList.setVisibility(4);
            for (int i = 0; i < res_adapter.getCount(); i++) {
                res_adapter.setItemChecked(i, true);
            }
            res_adapter.refresh();
            startImport();
        }
    }

    protected void onDestroy() {
        this.flag_import_after_connect = false;
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getRepeatCount() == 0) {
            if (type != null) {
                type = null;
                ArrayList<String> labelList = new ArrayList();
                ArrayList<String> valueList = new ArrayList();
                ArrayList<Integer> keys = new ArrayList(typeList.keySet());
                int i = 0;
                for (String t : typeList.values()) {
                    if (NVModel.getElementTypeName(context, t) == null) {
                        labelList.add(t);
                        valueList.add(null);
                    } else if (switches.get(keys.get(i)) == null || ((ArrayList) switches.get(keys.get(i))).size() <= 0) {
                        labelList.add(NVModel.getElementTypeName(context, t));
                        valueList.add(null);
                    } else {
                        labelList.add(NVModel.getElementTypeName(context, t) + " (" + ((ArrayList) switches.get(keys.get(i))).size() + ")");
                        valueList.add("" + ((ArrayList) switches.get(keys.get(i))).size());
                    }
                    i++;
                }
                res_adapter = new CheckListAdapter(this, R.layout.checklist_item, labelList, valueList, false);
                res_adapter.setCheckListAdapterListener(this);
                fab.setVisibility(4);
                list_level = 0;
                ResourcesList.setAdapter(res_adapter);
                ResourcesList.invalidate();
                return true;
            }
            NexoService.goBackground();
            NexoTalk.removeNexoTalkListener(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onCheckChanged(boolean checked) {
        if (((CheckListAdapter) ResourcesList.getAdapter()).getCheckedItemPositions().size() <= 0) {
            fab.setVisibility(4);
        } else if (list_level == 0) {
            selected_res_to_add = 0;
            ArrayList<Integer> keys = new ArrayList(typeList.keySet());
            SparseBooleanArray checkedList = ((CheckListAdapter) ResourcesList.getAdapter()).getCheckedItemPositions();
            TreeMap<Integer, ArrayList<String>> resources = NexoService.getImportedResources();
            if (resources != null) {
                int c = 0;
                while (c < ResourcesList.getAdapter().getCount()) {
                    if (!(!checkedList.get(c) || resources == null || resources.get(keys.get(c)) == null)) {
                        selected_res_to_add = ((ArrayList) resources.get(keys.get(c))).size() + selected_res_to_add;
                    }
                    c++;
                }
            }
            if (selected_res_to_add > 0) {
                fab.setVisibility(0);
            } else {
                fab.setVisibility(4);
            }
        } else {
            fab.setVisibility(0);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nexoresources, menu);
        checkBox = (CheckBox) menu.findItem(R.id.action_check).getActionView();
        checkBox.setText(getString(R.string.ResourcesActivity_SelecAllCheckBox));
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < NexoResourcesActivity.res_adapter.getCount(); i++) {
                    NexoResourcesActivity.res_adapter.setItemChecked(i, isChecked);
                }
                NexoResourcesActivity.res_adapter.refresh();
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_import) {
            return super.onOptionsItemSelected(item);
        }
        if (NexoTalk.isConnected()) {
            ResourcesList.setVisibility(4);
            for (int i = 0; i < res_adapter.getCount(); i++) {
                res_adapter.setItemChecked(i, true);
            }
            res_adapter.refresh();
            startImport();
            return true;
        }
        notConnectedDialog.show();
        return true;
    }

    private void checkAll(ViewGroup vg, boolean check) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(check);
            } else if (v instanceof ViewGroup) {
                checkAll((ViewGroup) v, check);
            }
        }
    }

    private void startImport() {
        if (NexoTalk.isConnected()) {
            importThread = new Thread(new Runnable() {
                public void run() {
                    SparseBooleanArray checkedList = ((CheckListAdapter) NexoResourcesActivity.ResourcesList.getAdapter()).getCheckedItemPositions();
                    ArrayList<Integer> res = new ArrayList();
                    for (int c = 0; c < NexoResourcesActivity.ResourcesList.getAdapter().getCount(); c++) {
                        if (checkedList.get(c)) {
                            res.add(new ArrayList(NexoResourcesActivity.typeList.keySet()).get(c));
                        }
                    }
                    if (res.size() > 0) {
                        NexoResourcesActivity.this.importResources(res);
                    } else {
                        Snackbar.make(NexoResourcesActivity.ResourcesList, NexoResourcesActivity.getContext().getString(R.string.ResourcesActivity_NoSelMessage), 0).show();
                    }
                }
            });
            importThread.start();
        }
    }

    public void connectionProcessInfo(String info, String error) {
    }
}
