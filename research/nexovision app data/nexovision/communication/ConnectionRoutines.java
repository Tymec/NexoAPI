package eu.nexwell.android.nexovision.communication;

import android.util.Log;
import eu.nexwell.android.nexovision.misc.JCrypto;
import java.security.NoSuchAlgorithmException;

class ConnectionRoutines {
    ConnectionRoutines() {
    }

    public static boolean setupUSSL(Communication communication) throws CommunicationException {
        Log.d("NexoTalk", "setupUSSL...");
        Log.d("NexoTalk", "setupUSSL: odebranie powitania od KSL (" + communication.read() + ")");
        int w = 0;
        while (w < 3) {
            communication.send("plain\n\u0000");
            Log.d("NexoTalk", "setupUSSL[" + w + "]: wyslanie zadania ustanowienia polaczenia nieszyfrowanego");
            int i = 0;
            while (i < 5) {
                String resp = communication.read();
                if (resp != null) {
                    Log.d("NexoTalk", "setupUSSL[" + w + "][" + i + "](" + resp + "): odebranie potwierdzenia przejscia w jeden z 2-ch trybow komunikacji");
                    if (!resp.matches(".*uSSL OK.*")) {
                        if (resp.matches(".*NO uSSL.*")) {
                            Log.d("NexoTalk", "setupUSSL[" + w + "][" + i + "]: KSL przeszedl w tryb komunikacji nieszyfrowanej");
                            break;
                        }
                    }
                    Log.d("NexoTalk", "setupUSSL[" + w + "][" + i + "]: KSL przeszedl w tryb konfiguracji SSL");
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
            if (i < 5) {
                break;
            }
            w++;
        }
        if (w > 2) {
            return false;
        }
        return true;
    }

    public static boolean checkConnection(Communication communication) throws CommunicationException {
        for (int j = 0; j < 3; j++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            communication.send("@00000000:ping\u0000");
            int i = 0;
            while (i < 3) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    String resp = communication.read();
                    if (resp != null) {
                        if (resp.length() > 10 && resp.matches("~[0-9]*:pong.*")) {
                            return true;
                        }
                    } else {
                        i++;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean authorizeConnection(Communication communication, String password) throws NoSuchAlgorithmException, CommunicationException {
        if (password == null) {
            return false;
        }
        int i;
        byte[] output = new byte[18];
        byte[] pass_b0 = password.getBytes();
        byte[] pass = new byte[(pass_b0.length + 1)];
        for (i = 0; i < pass_b0.length; i++) {
            pass[i] = pass_b0[i];
        }
        pass[pass.length - 1] = (byte) 0;
        JCrypto.md5(pass, output);
        for (int j = 0; j < 2; j++) {
            communication.send(output);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (i = 0; i < 2; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                String resp = communication.read();
                if (resp != null) {
                    if (resp.matches(".*LOGIN OK.*")) {
                        return true;
                    }
                    if (resp.matches(".*LOGIN FAILED.*")) {
                        return false;
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e12) {
                e12.printStackTrace();
            }
        }
        return false;
    }

    public static boolean open(Communication communication, String dest, String port) throws CommunicationException {
        return communication.open(dest, port);
    }

    public static void close(Communication communication) throws CommunicationException {
        communication.close();
    }

    public static boolean isConnected(Communication communication) {
        return communication.isConnected();
    }
}
