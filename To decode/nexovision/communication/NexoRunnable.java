package eu.nexwell.android.nexovision.communication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.PreferenceManager;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import eu.nexwell.android.nexovision.MainActivity;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.Logic;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Partition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import nexovision.android.nexwell.eu.nexovision.R;

class NexoRunnable extends Runfinishable {
    private static ArrayList<Integer> alarming = new ArrayList();
    private static final int maxAcceptableConnectionErrors = 10;
    private String action;
    private ProgressDialog arecProgressDialog = null;
    private boolean cipher;
    private int connectionErrorCounter = 0;
    private boolean disconnectRequest = false;
    private boolean importDone = false;
    private String ip;
    private int iterator = 0;
    private ArrayList<IElement> newElements = null;
    private boolean onceDone = false;
    private String password;
    private String port;
    private RunMode runMode = RunMode.IDLE;
    private boolean runModeChangeRequest = false;
    private ISwitch sw;
    public TreeMap<Integer, ArrayList<String>> switches = null;
    public HashMap<String, ArrayList<String>> switches_data = null;
    private int type_count = 0;
    private ArrayList<Integer> types = null;

    /* renamed from: eu.nexwell.android.nexovision.communication.NexoRunnable$1 */
    class C21031 implements Runnable {

        /* renamed from: eu.nexwell.android.nexovision.communication.NexoRunnable$1$1 */
        class C21021 implements OnClickListener {
            C21021() {
            }

            public void onClick(DialogInterface dialog, int which) {
                NexoService.stop((MainActivity) MainActivity.getContext());
                NexoRunnable.this.finish();
            }
        }

        C21031() {
        }

        public void run() {
            try {
                if (NexoRunnable.this.arecProgressDialog != null) {
                    NexoRunnable.this.arecProgressDialog.dismiss();
                }
                NexoRunnable.this.arecProgressDialog = new ProgressDialog(MainActivity.getContext());
                NexoRunnable.this.arecProgressDialog.setButton(-2, MainActivity.getContext().getString(R.string.CANCEL), new C21021());
                NexoRunnable.this.arecProgressDialog.setCancelable(false);
                NexoRunnable.this.arecProgressDialog.setCanceledOnTouchOutside(false);
                NexoRunnable.this.arecProgressDialog.setTitle("Reconnecting...");
                NexoRunnable.this.arecProgressDialog.setMessage("Wait for reconnect...");
                NexoRunnable.this.arecProgressDialog.show();
            } catch (Exception e) {
                if (NexoRunnable.this.arecProgressDialog != null) {
                    NexoRunnable.this.arecProgressDialog.dismiss();
                }
                NexoRunnable.this.arecProgressDialog = null;
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.communication.NexoRunnable$2 */
    class C21042 implements Runnable {
        C21042() {
        }

        public void run() {
            if (NexoRunnable.this.arecProgressDialog != null) {
                NexoRunnable.this.arecProgressDialog.dismiss();
                NexoRunnable.this.arecProgressDialog = null;
            }
        }
    }

    private enum RunMode {
        IDLE,
        FOREGROUND,
        BACKGROUND
    }

    public NexoRunnable(String ip, String port, String password, boolean cipher) {
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.cipher = cipher;
    }

    public void goForeground(ArrayList<IElement> els) {
        this.newElements = els;
        this.runMode = RunMode.FOREGROUND;
        this.runModeChangeRequest = true;
    }

    public void goBackground(ArrayList<IElement> els) {
        this.newElements = els;
        this.runMode = RunMode.BACKGROUND;
        this.runModeChangeRequest = true;
    }

    public void freeze() {
        this.runMode = RunMode.IDLE;
        this.runModeChangeRequest = true;
    }

    public void doImport(ArrayList<Integer> types) {
        if (this.types == null) {
            this.switches = new TreeMap();
            this.switches_data = new HashMap();
            this.types = types;
            this.runModeChangeRequest = true;
        }
    }

    public void queueAction(String action, ISwitch sw) {
        if (this.action == null) {
            this.action = action;
            this.sw = sw;
            this.runModeChangeRequest = true;
        }
    }

    public ArrayList<Integer> getAlarming() {
        return alarming;
    }

    private void updaterRun() {
        ArrayList<IElement> elements = this.newElements;
        while (!isFinishedOrDisconnected()) {
            int a = 0;
            while (a < elements.size()) {
                if (!sleepAndCheckIfIsFinishedOrDisconnected(50)) {
                    IElement el = (IElement) elements.get(a);
                    String resp;
                    if (el instanceof ISwitch) {
                        try {
                            ISwitch sw = (ISwitch) el;
                            Log.d("NexoTalk.NexoRunnable()", "send = @00000000:" + sw.getUpdateCommand());
                            if (NexoTalk.sendAndRead("@00000000:" + sw.getUpdateCommand()) != null) {
                                correctConnectionAcknowledge();
                            }
                            int i = 0;
                            while (i < 8) {
                                int i2;
                                Log.d("NexoTalk: U", "@00000000:get");
                                resp = NexoTalk.sendAndRead("@00000000:get\u0000");
                                if (resp != null) {
                                    correctConnectionAcknowledge();
                                    Log.d("NexoTalk: U", resp);
                                    if (!resp.equals("~00000000:")) {
                                        Integer old_state = sw.getState(Integer.valueOf(0));
                                        if (sw.parseResp(resp)) {
                                            if (!(sw.isUpdated() && this.onceDone)) {
                                                this.onceDone = true;
                                                sw.setUpdated(true);
                                                NexoTalk.getListenersManager();
                                                ListenersManager.notifyListeners(sw, false);
                                            }
                                            if (!sw.getState(Integer.valueOf(0)).equals(old_state)) {
                                                NexoTalk.getListenersManager();
                                                ListenersManager.notifyListeners(sw, false);
                                            }
                                            if (sw instanceof Partition) {
                                                Partition e = (Partition) sw;
                                                if (e.isAlarming()) {
                                                    NexoTalk.getListenersManager();
                                                    ListenersManager.notifyListenersAbtAlarm(e);
                                                    if (!alarming.contains(e.getId())) {
                                                        alarming.add(e.getId());
                                                    }
                                                } else if (alarming.contains(e.getId())) {
                                                    alarming.remove(e.getId());
                                                }
                                                if (alarming.isEmpty()) {
                                                    Log.d("NexoTalk", "NO ALARM!");
                                                    NexoTalk.getListenersManager();
                                                    ListenersManager.notifyListenersAbtAlarm(null);
                                                }
                                            }
                                        } else {
                                            IElement e2;
                                            NVModel nVModel = new NVModel();
                                            Iterator<IElement> itrp = NVModel.getElementsByType(NVModel.EL_TYPE_PARTITION).iterator();
                                            while (itrp.hasNext()) {
                                                e2 = (IElement) itrp.next();
                                                if ((e2 instanceof Partition) && ((Partition) e2).parseResp(resp)) {
                                                    if (((Partition) e2).isAlarming()) {
                                                        Log.d("NexoTalk", "U: " + e2.getName() + " ALARMING");
                                                        NexoTalk.getListenersManager();
                                                        ListenersManager.notifyListenersAbtAlarm(e2);
                                                    } else {
                                                        Log.d("NexoTalk", "U: " + e2.getName() + " NOT ALARMING");
                                                        if (alarming.contains(e2.getId())) {
                                                            alarming.remove(e2.getId());
                                                        }
                                                    }
                                                }
                                            }
                                            nVModel = new NVModel();
                                            Iterator<IElement> itrp24 = NVModel.getElementsByType(NVModel.EL_TYPE_PARTITION24H).iterator();
                                            while (itrp24.hasNext()) {
                                                e2 = (IElement) itrp24.next();
                                                if ((e2 instanceof Partition) && ((Partition) e2).parseResp(resp)) {
                                                    if (((Partition) e2).isAlarming()) {
                                                        Log.d("NexoTalk", "U: " + e2.getName() + " ALARMING");
                                                        NexoTalk.getListenersManager();
                                                        ListenersManager.notifyListenersAbtAlarm(e2);
                                                    } else {
                                                        Log.d("NexoTalk", "U: " + e2.getName() + " NOT ALARMING");
                                                        if (alarming.contains(e2.getId())) {
                                                            alarming.remove(e2.getId());
                                                        }
                                                    }
                                                }
                                            }
                                            if (alarming.isEmpty()) {
                                                Log.d("NexoTalk", "NO ALARM!");
                                                NexoTalk.getListenersManager();
                                                ListenersManager.notifyListenersAbtAlarm(null);
                                            }
                                            int b = 0;
                                            while (b < elements.size()) {
                                                if (!isFinishedOrDisconnected()) {
                                                    IElement el2 = (IElement) elements.get(b);
                                                    if (el2 != null && (el2 instanceof ISwitch) && ((ISwitch) el2).parseResp(resp)) {
                                                        if (!((ISwitch) el2).isUpdated()) {
                                                            ((ISwitch) el2).setUpdated(true);
                                                            NexoTalk.getListenersManager();
                                                            ListenersManager.notifyListeners(el2, false);
                                                        }
                                                        if (!((ISwitch) el2).getState(Integer.valueOf(0)).equals(old_state)) {
                                                            NexoTalk.getListenersManager();
                                                            ListenersManager.notifyListeners(el2, false);
                                                        }
                                                    } else {
                                                        if (el2 != null && (el2 instanceof Logic)) {
                                                            Log.d("NexoTalk", "NexoRunnable - Logic(" + el2.getName() + ") checking...");
                                                            if (((Logic) el2).parseResp(resp)) {
                                                                Log.d("NexoTalk", "NexoRunnable - Logic(" + el2.getName() + ").parseResp(" + resp + ") == true");
                                                                NexoTalk.getListenersManager();
                                                                ListenersManager.notifyListeners(el2, false);
                                                            }
                                                        }
                                                        b++;
                                                    }
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }
                                Log.e("NexoTalk.NexoRunnable()", "null");
                                somethingWentWrongWithConnection();
                                if (i < 4) {
                                    i2 = 10;
                                } else {
                                    i2 = 50;
                                }
                                if (!sleepAndCheckIfIsFinishedOrDisconnected(i2)) {
                                    i++;
                                } else {
                                    return;
                                }
                            }
                            continue;
                        } catch (CommunicationException e3) {
                            Log.e("NexoTalk.NexoRunnable()", "CommunicationException(" + e3.getMessage() + ")");
                            somethingWentWrongWithConnection();
                        }
                    } else if (el instanceof Logic) {
                        resp = null;
                        try {
                            resp = NexoTalk.sendAndRead("@00000000:get\u0000");
                        } catch (CommunicationException e32) {
                            Log.e("NexoTalk.NexoRunnable()", "CommunicationException(" + e32.getMessage() + ")");
                            somethingWentWrongWithConnection();
                        }
                        if (resp != null) {
                            correctConnectionAcknowledge();
                            Log.d("NexoTalk: Logic:", resp);
                            if (!resp.equals("~00000000:")) {
                                Log.d("NexoTalk", "NexoRunnable - Logic(" + el.getName() + ") checking...");
                                if (((Logic) elements.get(a)).parseResp(resp)) {
                                    Log.d("NexoTalk", "NexoRunnable - Logic(" + el.getName() + ").parseResp(" + resp + ") == true");
                                    NexoTalk.getListenersManager();
                                    ListenersManager.notifyListeners(el, false);
                                }
                            }
                        } else {
                            Log.e("NexoTalk.NexoRunnable()", "null");
                            somethingWentWrongWithConnection();
                        }
                    }
                    a++;
                } else {
                    return;
                }
            }
            NexoTalk.getListenersManager();
            ListenersManager.notifyListeners(null, true);
        }
    }

    private void bgrListenRun() {
        while (!isFinishedOrDisconnected()) {
            String resp = null;
            try {
                Log.d("BgrListener", "@00000000:get");
                resp = NexoTalk.sendAndRead("@00000000:get\u0000");
            } catch (CommunicationException e) {
                setDisconnectRequest();
            }
            if (resp != null) {
                Log.d("BgrListener", resp);
                if (!resp.equals("~00000000:")) {
                    IElement e2;
                    NVModel nVModel = new NVModel();
                    Iterator<IElement> itrp = NVModel.getElementsByType(NVModel.EL_TYPE_PARTITION).iterator();
                    while (itrp.hasNext()) {
                        e2 = (IElement) itrp.next();
                        if ((e2 instanceof Partition) && ((Partition) e2).parseResp(resp)) {
                            if (((Partition) e2).isAlarming()) {
                                NexoTalk.getListenersManager();
                                ListenersManager.notifyListenersAbtAlarm(e2);
                                return;
                            } else if (alarming.contains(e2.getId())) {
                                alarming.remove(e2.getId());
                            }
                        }
                    }
                    nVModel = new NVModel();
                    Iterator<IElement> itrp24 = NVModel.getElementsByType(NVModel.EL_TYPE_PARTITION24H).iterator();
                    while (itrp24.hasNext()) {
                        e2 = (IElement) itrp24.next();
                        if ((e2 instanceof Partition) && ((Partition) e2).parseResp(resp)) {
                            if (((Partition) e2).isAlarming()) {
                                NexoTalk.getListenersManager();
                                ListenersManager.notifyListenersAbtAlarm(e2);
                                return;
                            } else if (alarming.contains(e2.getId())) {
                                alarming.remove(e2.getId());
                            }
                        }
                    }
                    if (alarming.isEmpty()) {
                        NexoTalk.getListenersManager();
                        ListenersManager.notifyListenersAbtAlarm(null);
                    }
                } else if (sleepAndCheckIfIsFinishedOrDisconnected(1000)) {
                    return;
                }
            } else {
                setDisconnectRequest();
            }
        }
    }

    private void setDisconnectRequest() {
        this.disconnectRequest = true;
        this.runModeChangeRequest = true;
    }

    private void tryToDisplayDialog() {
        if (this.arecProgressDialog == null) {
            MainActivity.handler.post(new C21031());
        }
    }

    private boolean sleepAndCheckIfIsFinishedButTryToDisplayDialog(int timesec) {
        for (int i = 0; i < timesec * 4; i++) {
            tryToDisplayDialog();
            if (sleepAndCheckIfIsFinished(Callback.DEFAULT_SWIPE_ANIMATION_DURATION)) {
                return true;
            }
        }
        return false;
    }

    private void hideDialog() {
        MainActivity.handler.post(new C21042());
    }

    private void autoReconnectRun() {
        if (NexoService.getContext() != null && !isFinished()) {
            String ip_port = PreferenceManager.getDefaultSharedPreferences(NexoService.getContext()).getString("pref_rememberme_login_ip", "");
            if (ip_port != null && ip_port.length() != 0) {
                String pass = PreferenceManager.getDefaultSharedPreferences(NexoService.getContext()).getString("pref_rememberme_login_passwd", "");
                if (pass != null && pass.length() != 0) {
                    Boolean encryption = Boolean.valueOf(false);
                    NexoTalk.disconnect();
                    String[] ip_port_entries = ip_port.split(":");
                    String ip = ip_port_entries.length > 0 ? ip_port_entries[0] : "";
                    String port = ip_port_entries.length > 1 ? ip_port_entries[1] : "1024";
                    Log.d("NexoTalk", "...ip=" + ip + ", port=" + port);
                    while (!isFinished()) {
                        tryToDisplayDialog();
                        Log.d("NexoTalk", "Trying to connect...");
                        if (!NexoTalk.connect(ip, port, pass, encryption.booleanValue())) {
                            if (sleepAndCheckIfIsFinishedButTryToDisplayDialog(3)) {
                                break;
                            }
                        }
                        break;
                    }
                    hideDialog();
                }
            }
        }
    }

    private void stayAwakeTransmissionRun() {
        if (!isFinished()) {
            while (!isFinishedOrDisconnected()) {
                try {
                    Log.d("StayAwakeTransmission", "send(!)");
                    NexoTalk.send("!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sleepAndCheckIfIsFinishedOrDisconnected(4000)) {
                    return;
                }
            }
        }
    }

    public TreeMap<Integer, ArrayList<String>> getSwitchesList() {
        return this.switches;
    }

    public HashMap<String, ArrayList<String>> getSwitchesDataList() {
        return this.switches_data;
    }

    private void resImporterRun() {
        this.type_count = 0;
        this.iterator = 0;
        this.importDone = false;
        Log.d("NexoTalk.ResImporter()", "types.size()=" + this.types.size());
        this.switches.put(this.types.get(this.type_count), new ArrayList());
        boolean doImport = true;
        while (doImport) {
            if (isFinishedOrDisconnected()) {
                NexoTalk.getListenersManager();
                ListenersManager.notifyListenersAbtImportEnd(this.importDone ? this.types : null);
                this.importDone = false;
                this.types = null;
                Log.e("NexoTalk", "ResImporter - FINISH");
                return;
            }
            try {
                if (isFinishedOrDisconnected()) {
                    NexoTalk.getListenersManager();
                    ListenersManager.notifyListenersAbtImportEnd(this.importDone ? this.types : null);
                    this.importDone = false;
                    this.types = null;
                    Log.e("NexoTalk", "ResImporter - FINISH");
                    return;
                }
                Log.d("NexoTalk.ResImporter()", "@00000000:system T " + this.types.get(this.type_count) + " " + this.iterator + " ?\u0000");
                NexoTalk.sendAndRead("@00000000:system T " + this.types.get(this.type_count) + " " + this.iterator + " ?\u0000");
                correctConnectionAcknowledge();
                for (int i = 0; i < 8; i++) {
                    if (sleepAndCheckIfIsFinishedOrDisconnected(10)) {
                        NexoTalk.getListenersManager();
                        ListenersManager.notifyListenersAbtImportEnd(this.importDone ? this.types : null);
                        this.importDone = false;
                        this.types = null;
                        Log.e("NexoTalk", "ResImporter - FINISH");
                        return;
                    }
                    Log.d("NexoTalk.ResImporter()", "@00000000:get\u0000");
                    String resp = NexoTalk.sendAndRead("@00000000:get\u0000");
                    if (resp == null) {
                        somethingWentWrongWithConnection();
                    } else if (resp.equals("~00000000:")) {
                        continue;
                    } else {
                        correctConnectionAcknowledge();
                        Log.d("NexoTalk.ResImporter()", "resp = " + resp);
                        String[] line = resp.split("\n");
                        if (line[0].matches("~00000000:~T [0-9]+ [0-9]+")) {
                            int type = Integer.parseInt(line[0].split(" ", 3)[1]);
                            int iter = Integer.parseInt(line[0].split(" ", 3)[2]);
                            Log.d("NexoTalk", "ResImporter: " + type + "!=" + this.types.get(this.type_count) + " " + this.iterator + " [" + line[0] + "] END");
                            if (type == ((Integer) this.types.get(this.type_count)).intValue()) {
                                this.type_count++;
                                if (this.type_count >= this.types.size() && this.iterator == iter) {
                                    doImport = false;
                                } else if (this.type_count < this.types.size()) {
                                    this.switches.put(this.types.get(this.type_count), new ArrayList());
                                }
                                this.iterator = 0;
                            }
                        } else if (line[0].matches("~00000000:~T [0-9]+ [0-9]+ .*")) {
                            String[] s = line[0].split(" ", 4);
                            String type2 = s[1];
                            String src = s[3];
                            Log.d("NexoTalk", "ResImporter: " + this.iterator + " [" + line[0] + "][" + src + "]");
                            if (src != null && ((Integer) this.types.get(this.type_count)).intValue() == Integer.parseInt(type2)) {
                                ((ArrayList) this.switches.get(this.types.get(this.type_count))).add(src);
                                if (line.length > 1) {
                                    ArrayList<String> data = new ArrayList();
                                    for (int ll = 1; ll < line.length; ll++) {
                                        data.add(line[ll]);
                                    }
                                    this.switches_data.put(src, data);
                                }
                                NexoTalk.getListenersManager();
                                ListenersManager.notifyListenersAbtImport(((Integer) this.types.get(this.type_count)).intValue(), this.iterator);
                                this.iterator++;
                            }
                        }
                    }
                }
                continue;
            } catch (CommunicationException e) {
                Log.e("NexoTalk", "ResImporter - CommunicationException(" + e.getMessage() + ")");
                somethingWentWrongWithConnection();
            } catch (Exception e2) {
                e2.printStackTrace();
            } catch (Throwable th) {
                Throwable th2 = th;
                NexoTalk.getListenersManager();
                ListenersManager.notifyListenersAbtImportEnd(this.importDone ? this.types : null);
                this.importDone = false;
                this.types = null;
                Log.e("NexoTalk", "ResImporter - FINISH");
            }
        }
        this.importDone = true;
        NexoTalk.getListenersManager();
        ListenersManager.notifyListenersAbtImportEnd(this.importDone ? this.types : null);
        this.importDone = false;
        this.types = null;
        Log.e("NexoTalk", "ResImporter - FINISH");
    }

    private void actionSenderRun() {
        Log.d("NexoTalk", "sendAction(" + this.action + ")");
        NexoTalk.sendAndRead("@00000000:" + this.action);
        if (this.sw != null) {
            int i;
            String resp;
            if (this.sw instanceof Partition) {
                i = 0;
                while (i < 8) {
                    long j;
                    Log.d("NexoTalk", "@00000000:get");
                    resp = NexoTalk.sendAndRead("@00000000:get\u0000");
                    if (resp != null) {
                        Log.d("NexoTalk", "" + resp);
                        if (this.sw.parseResp(resp)) {
                            NexoTalk.getListenersManager();
                            ListenersManager.notifyListeners(this.sw, true);
                            return;
                        }
                    }
                    if (i < 4) {
                        j = 10;
                    } else {
                        j = 50;
                    }
                    try {
                        Thread.sleep(j);
                        i++;
                    } catch (Exception e) {
                        Log.e("NexoTalk.ActionSender", e.getMessage());
                    } finally {
                        this.action = null;
                        this.sw = null;
                    }
                }
            }
            for (int j2 = 0; j2 < 3; j2++) {
                Log.d("NexoTalk", "sendUpdateCommand(@00000000:" + this.sw.getUpdateCommand());
                NexoTalk.sendAndRead("@00000000:" + this.sw.getUpdateCommand());
                i = 0;
                while (i < 8) {
                    Thread.sleep(i < 4 ? 10 : 50);
                    Log.d("NexoTalk", "@00000000:get");
                    resp = NexoTalk.sendAndRead("@00000000:get\u0000");
                    if (resp != null) {
                        Log.d("NexoTalk", "" + resp);
                        if (!resp.equals("~00000000:") && this.sw.parseResp(resp)) {
                            if (!this.sw.isUpdated()) {
                                this.sw.setUpdated(true);
                                NexoTalk.getListenersManager();
                                ListenersManager.notifyListeners(this.sw, true);
                            }
                            if (!this.sw.getState(Integer.valueOf(0)).equals(this.sw.getState(Integer.valueOf(1)))) {
                                Log.d("NexoTalk", "sendActionAndUpdate: " + this.sw.getState(Integer.valueOf(0)) + " ?= " + this.sw.getState(Integer.valueOf(1)));
                                NexoTalk.getListenersManager();
                                ListenersManager.notifyListeners(this.sw, true);
                                this.action = null;
                                this.sw = null;
                                return;
                            }
                            Thread.sleep(10);
                        }
                    }
                    i++;
                }
                Thread.sleep(10);
            }
        }
        this.action = null;
        this.sw = null;
    }

    private boolean isFinishedOrDisconnected() {
        return isFinished() || !NexoTalk.isConnected() || this.runModeChangeRequest;
    }

    private boolean sleepAndCheckIfIsFinishedOrDisconnected(int timems) {
        if (isFinishedOrDisconnected()) {
            return true;
        }
        for (int i = 0; i < timems / 10; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isFinishedOrDisconnected()) {
                return true;
            }
        }
        return false;
    }

    private void correctConnectionAcknowledge() {
        if (this.connectionErrorCounter > 0) {
            this.connectionErrorCounter = 0;
            Log.e("NexoTalk.Runfinable()", "correctConnectionAcknowledge() --> errC = 0");
        }
    }

    private boolean somethingWentWrongWithConnection() {
        this.connectionErrorCounter++;
        Log.e("NexoTalk.NexoRunnable()", "somethingWentWrongWithConnection() --> errC = " + this.connectionErrorCounter + "");
        if (this.connectionErrorCounter > 10) {
            this.connectionErrorCounter = 0;
            Log.e("NexoTalk.Runfinable()", "somethingWentWrongWithConnection() --> disconnectRequest()");
            setDisconnectRequest();
        }
        return sleepAndCheckIfIsFinished(100);
    }

    public void run() {
        Log.d("NexoRunnable", "RUN");
        if (!NexoTalk.isConnected()) {
            this.disconnectRequest = true;
        }
        while (!isFinished()) {
            this.runModeChangeRequest = false;
            if (this.disconnectRequest || !NexoTalk.isConnected() || !NexoTalk.getCommunication().isOnline() || NexoTalk.getCommunication().connTypeChanged()) {
                Log.d("NexoRunnable", "MODE: AUTORECONNECT");
                this.disconnectRequest = false;
                autoReconnectRun();
                NexoTalk.getCommunication().deactivateConnectionType();
            } else if (this.action != null) {
                Log.d("NexoRunnable", "MODE: ACTION");
                actionSenderRun();
            } else if (this.types != null) {
                Log.d("NexoRunnable", "MODE: IMPORT");
                resImporterRun();
            } else if (this.runMode == RunMode.FOREGROUND && this.newElements != null && this.newElements.size() > 0) {
                Log.d("NexoRunnable", "MODE: FOREGROUND");
                updaterRun();
            } else if (this.runMode == RunMode.BACKGROUND) {
                Log.d("NexoRunnable", "MODE: BACKGROUND");
                bgrListenRun();
            } else {
                Log.d("NexoRunnable", "MODE: STAYAWAKE");
                stayAwakeTransmissionRun();
            }
        }
        NexoTalk.disconnect();
    }
}
