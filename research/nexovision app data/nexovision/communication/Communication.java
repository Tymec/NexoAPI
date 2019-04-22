package eu.nexwell.android.nexovision.communication;

import android.net.ConnectivityManager;
import eu.nexwell.android.nexovision.MainActivity;
import eu.nexwell.android.nexovision.communication.CommunicationException.Type;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Locale;
import org.apache.http.HttpStatus;

public class Communication {
    private int connType = -1;
    protected Socket sock;
    public boolean ussl = false;

    public boolean isConnected() {
        return this.sock != null && this.sock.isConnected();
    }

    public boolean open(String dest, String port) throws CommunicationException {
        if (dest == null || dest.equals("")) {
            return false;
        }
        if (port == null || port.equals("")) {
            return false;
        }
        try {
            InetAddress destAddr = InetAddress.getByName(dest);
            this.sock = new Socket();
            this.sock.connect(new InetSocketAddress(destAddr, Integer.parseInt(port)), 1000);
            this.sock.setSoTimeout(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            this.sock.setKeepAlive(true);
            this.sock.setTcpNoDelay(true);
            return true;
        } catch (SocketTimeoutException e) {
            throw new CommunicationException(Type.SOCKET_TIMEOUT);
        } catch (ConnectException e2) {
            throw new CommunicationException(Type.CONNECT_EXCEPTION);
        } catch (UnknownHostException e3) {
            throw new CommunicationException(Type.UNKNOWN_HOST_EXCEPTION);
        } catch (IOException e4) {
            throw new CommunicationException(Type.IO_EXCEPTION);
        }
    }

    private void checkIfSocketIsWorking() throws CommunicationException {
        if (this.sock == null) {
            throw new CommunicationException(Type.SOCKET_NULL);
        } else if (!this.sock.isConnected()) {
            throw new CommunicationException(Type.SOCKET_DISCONNECTED);
        }
    }

    public void send(String s) throws CommunicationException {
        checkIfSocketIsWorking();
        try {
            new PrintStream(this.sock.getOutputStream(), true).write(s.getBytes(getLocaleEncoding()));
        } catch (SocketException e) {
            throw new CommunicationException(Type.SOCKET_EXCEPTION);
        } catch (SocketTimeoutException e2) {
            throw new CommunicationException(Type.SOCKET_TIMEOUT);
        } catch (IOException e3) {
            throw new CommunicationException(Type.IO_EXCEPTION);
        }
    }

    public void send(byte[] b) throws CommunicationException {
        checkIfSocketIsWorking();
        try {
            PrintStream os = new PrintStream(this.sock.getOutputStream(), true, getLocaleEncoding());
            os.write(b);
            if (os.checkError()) {
                throw new CommunicationException(Type.OS_ERROR);
            }
        } catch (SocketException e) {
            throw new CommunicationException(Type.SOCKET_EXCEPTION);
        } catch (SocketTimeoutException e2) {
            throw new CommunicationException(Type.SOCKET_TIMEOUT);
        } catch (IOException e3) {
            throw new CommunicationException(Type.IO_EXCEPTION);
        }
    }

    public String read() throws CommunicationException {
        checkIfSocketIsWorking();
        byte[] resp = new byte[255];
        try {
            int j = new DataInputStream(this.sock.getInputStream()).read(resp);
            if (j < 1) {
                return null;
            }
            String ret;
            if (j >= resp.length) {
                j = resp.length - 1;
            }
            resp[j] = (byte) 0;
            try {
                ret = new String(resp, getLocaleEncoding());
            } catch (UnsupportedEncodingException e) {
                ret = "";
            }
            return ret.substring(0, ret.indexOf(0));
        } catch (SocketException e2) {
            throw new CommunicationException(Type.SOCKET_EXCEPTION);
        } catch (SocketTimeoutException e3) {
            throw new CommunicationException(Type.SOCKET_TIMEOUT);
        } catch (IOException e4) {
            throw new CommunicationException(Type.IO_EXCEPTION);
        }
    }

    public int read(byte[] resp) throws CommunicationException {
        checkIfSocketIsWorking();
        try {
            int j = new DataInputStream(this.sock.getInputStream()).read(resp);
            if (j < 1) {
                return -1;
            }
            if (j >= resp.length) {
                j = resp.length - 1;
            }
            resp[j] = (byte) 0;
            return j;
        } catch (SocketTimeoutException e) {
            throw new CommunicationException(Type.SOCKET_TIMEOUT);
        } catch (IOException e2) {
            throw new CommunicationException(Type.IO_EXCEPTION);
        }
    }

    public void close() throws CommunicationException {
        try {
            if (this.sock != null) {
                this.sock.close();
            }
            this.sock = null;
        } catch (IOException e) {
            throw new CommunicationException(Type.IO_EXCEPTION);
        }
    }

    public String getIpAddress() throws CommunicationException {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> enumInetAddress = ((NetworkInterface) enumNetworkInterfaces.nextElement()).getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
            return ip;
        } catch (SocketException e) {
            throw new CommunicationException(Type.SOCKET_EXCEPTION);
        }
    }

    public boolean isOnline() {
        try {
            return ((ConnectivityManager) MainActivity.getContext().getSystemService("connectivity")).getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean connTypeChanged() {
        try {
            ConnectivityManager cm = (ConnectivityManager) MainActivity.getContext().getSystemService("connectivity");
            if (this.connType > -1) {
                if (!cm.getActiveNetworkInfo().isConnected() || this.connType == cm.getActiveNetworkInfo().getType()) {
                    return false;
                }
                this.connType = cm.getActiveNetworkInfo().getType();
                return true;
            } else if (!cm.getActiveNetworkInfo().isConnected()) {
                return false;
            } else {
                this.connType = cm.getActiveNetworkInfo().getType();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void deactivateConnectionType() {
        this.connType = -1;
    }

    private String getLocaleEncoding() {
        if (Locale.getDefault().getLanguage().equalsIgnoreCase("ru")) {
            return "Cp1251";
        }
        return "Cp1250";
    }
}
