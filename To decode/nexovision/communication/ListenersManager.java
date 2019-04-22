package eu.nexwell.android.nexovision.communication;

import eu.nexwell.android.nexovision.model.IElement;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

class ListenersManager {
    private static Semaphore listenSem = new Semaphore(1);
    private static ArrayList<NexoTalkListener> listeners;

    static {
        listeners = null;
        listeners = new ArrayList();
    }

    public static void addNexoTalkListener(NexoTalkListener l) {
        try {
            listenSem.acquire();
            listeners.add(l);
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    public static void removeNexoTalkListener(NexoTalkListener l) {
        try {
            listenSem.acquire();
            listeners.remove(l);
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    public static void notifyListeners(IElement el, boolean finish) {
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    NexoTalkListener listener = (NexoTalkListener) listeners.get(i);
                    if (listener != null) {
                        listener.onStatusUpdate(el, finish);
                    }
                }
            }
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    public static void notifyListenersAbtAlarm(IElement el) {
        if (!(el == null || NexoService.getAlarming() == null)) {
            if (!NexoService.getAlarming().contains(el.getId())) {
                NexoService.getAlarming().add(el.getId());
            } else {
                return;
            }
        }
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    NexoTalkListener listener = (NexoTalkListener) listeners.get(i);
                    if (listener != null) {
                        listener.onPartitionAlarm(el);
                    }
                }
            }
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    public static void notifyListenersAbtImport(int type, int iterator) {
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    NexoTalkListener listener = (NexoTalkListener) listeners.get(i);
                    if (listener != null) {
                        listener.onImport(type, iterator);
                    }
                }
            }
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    public static void notifyListenersAbtImportEnd(ArrayList<Integer> types) {
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    NexoTalkListener listener = (NexoTalkListener) listeners.get(i);
                    if (listener != null) {
                        listener.onImportEnd(types);
                    }
                }
            }
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    public static void notifyListenersAbtConnectionStatus(boolean isConnected) {
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    NexoTalkListener listener = (NexoTalkListener) listeners.get(i);
                    if (listener != null) {
                        listener.connectionStatus(isConnected);
                    }
                }
            }
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    public static void notifyListenersAbtConnectionProcessInfo(String info, String error) {
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    NexoTalkListener listener = (NexoTalkListener) listeners.get(i);
                    if (listener != null) {
                        listener.connectionProcessInfo(info, error);
                    }
                }
            }
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }
}
