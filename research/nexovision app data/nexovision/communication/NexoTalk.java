package eu.nexwell.android.nexovision.communication;

import eu.nexwell.android.nexovision.MainActivity;
import nexovision.android.nexwell.eu.nexovision.R;

public class NexoTalk {
    private static Communication communication = new Communication();
    private static ListenersManager listenersManager = new ListenersManager();

    public static void addNexoTalkListener(NexoTalkListener l) {
        ListenersManager listenersManager = listenersManager;
        ListenersManager.addNexoTalkListener(l);
    }

    public static void removeNexoTalkListener(NexoTalkListener l) {
        ListenersManager listenersManager = listenersManager;
        ListenersManager.removeNexoTalkListener(l);
    }

    static ListenersManager getListenersManager() {
        return listenersManager;
    }

    static Communication getCommunication() {
        return communication;
    }

    private static String getConnectionProgressMessage(int curr_phase, boolean cipher, String ip) {
        String[] phase = new String[]{MainActivity.getContext().getString(R.string.ConnectionActivity_ConnectionProgressDialog_ConnectionPhase), MainActivity.getContext().getString(R.string.ConnectionActivity_ConnectionProgressDialog_KeyExchangePhase), MainActivity.getContext().getString(R.string.ConnectionActivity_ConnectionProgressDialog_AuthPhase), MainActivity.getContext().getString(R.string.ConnectionActivity_ConnectionProgressDialog_TestPhase)};
        String mess = "";
        int i = 0;
        while (i < phase.length) {
            if (cipher || i != 1) {
                if (i == curr_phase) {
                    mess = mess + "> ";
                } else {
                    mess = mess + "  ";
                }
                mess = mess + phase[i];
                if (i == curr_phase) {
                    mess = mess + "...\n";
                } else {
                    mess = mess + "\n";
                }
            }
            i++;
        }
        return mess + "\n" + MainActivity.getContext().getString(R.string.ConnectionActivity_ConnectionProgressDialog_IPLabel) + ": " + ip;
    }

    private static String getConnectionProgressMessage(int messageId) {
        return MainActivity.getContext().getString(messageId);
    }

    public static boolean connect(String ip, String port, String password, boolean cipher) {
        Exception e;
        boolean z = true;
        ListenersManager listenersManager;
        if (ConnectionRoutines.isConnected(communication)) {
            listenersManager = listenersManager;
            ListenersManager.notifyListenersAbtConnectionProcessInfo(null, "");
        } else {
            listenersManager = listenersManager;
            ListenersManager.notifyListenersAbtConnectionProcessInfo(getConnectionProgressMessage(0, cipher, ip), null);
            z = false;
            try {
                if (ConnectionRoutines.open(communication, ip, port)) {
                    if (cipher) {
                        listenersManager = listenersManager;
                        ListenersManager.notifyListenersAbtConnectionProcessInfo(getConnectionProgressMessage(1, cipher, ip), null);
                    }
                    if (ConnectionRoutines.setupUSSL(communication)) {
                        listenersManager = listenersManager;
                        ListenersManager.notifyListenersAbtConnectionProcessInfo(getConnectionProgressMessage(2, cipher, ip), null);
                        if (ConnectionRoutines.authorizeConnection(communication, password)) {
                            listenersManager = listenersManager;
                            ListenersManager.notifyListenersAbtConnectionProcessInfo(getConnectionProgressMessage(3, cipher, ip), null);
                            if (ConnectionRoutines.checkConnection(communication)) {
                                listenersManager = listenersManager;
                                ListenersManager.notifyListenersAbtConnectionProcessInfo(null, "");
                                z = true;
                            } else {
                                listenersManager = listenersManager;
                                ListenersManager.notifyListenersAbtConnectionProcessInfo(null, getConnectionProgressMessage(R.string.ConnectionActivity_ConnectionProgressDialog_TestPhaseErr));
                            }
                        } else {
                            listenersManager = listenersManager;
                            ListenersManager.notifyListenersAbtConnectionProcessInfo(null, getConnectionProgressMessage(R.string.ConnectionActivity_ConnectionProgressDialog_AuthPhaseErr));
                        }
                    } else {
                        listenersManager = listenersManager;
                        ListenersManager.notifyListenersAbtConnectionProcessInfo(null, getConnectionProgressMessage(R.string.ConnectionActivity_ConnectionProgressDialog_AuthPhaseErr));
                    }
                } else {
                    listenersManager = listenersManager;
                    ListenersManager.notifyListenersAbtConnectionProcessInfo(null, getConnectionProgressMessage(R.string.ConnectionActivity_ConnectionProgressDialog_ConnectionPhaseErr));
                }
                if (!z) {
                    try {
                        ConnectionRoutines.close(communication);
                    } catch (CommunicationException e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    e.printStackTrace();
                    listenersManager = listenersManager;
                    ListenersManager.notifyListenersAbtConnectionProcessInfo(null, getConnectionProgressMessage(R.string.ConnectionActivity_ConnectionProgressDialog_ConnectionPhaseErr));
                    if (null == null) {
                        try {
                            ConnectionRoutines.close(communication);
                        } catch (CommunicationException e22) {
                            e22.printStackTrace();
                        }
                    }
                    listenersManager = listenersManager;
                    ListenersManager.notifyListenersAbtConnectionStatus(z);
                    return z;
                } catch (Throwable th) {
                    if (null == null) {
                        try {
                            ConnectionRoutines.close(communication);
                        } catch (CommunicationException e222) {
                            e222.printStackTrace();
                        }
                    }
                }
            } catch (Exception e32) {
                e = e32;
                e.printStackTrace();
                listenersManager = listenersManager;
                ListenersManager.notifyListenersAbtConnectionProcessInfo(null, getConnectionProgressMessage(R.string.ConnectionActivity_ConnectionProgressDialog_ConnectionPhaseErr));
                if (null == null) {
                    ConnectionRoutines.close(communication);
                }
                listenersManager = listenersManager;
                ListenersManager.notifyListenersAbtConnectionStatus(z);
                return z;
            }
            listenersManager = listenersManager;
            ListenersManager.notifyListenersAbtConnectionStatus(z);
        }
        return z;
    }

    public static boolean isConnected() {
        return ConnectionRoutines.isConnected(communication);
    }

    public static void disconnect() {
        try {
            ConnectionRoutines.close(communication);
        } catch (CommunicationException e) {
        }
        ListenersManager listenersManager = listenersManager;
        ListenersManager.notifyListenersAbtConnectionStatus(ConnectionRoutines.isConnected(communication));
    }

    public static void send(String s) throws CommunicationException {
        communication.send(s);
    }

    public static String sendAndRead(String s) throws CommunicationException {
        communication.send(s);
        return communication.read();
    }
}
