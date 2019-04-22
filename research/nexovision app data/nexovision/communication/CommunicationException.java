package eu.nexwell.android.nexovision.communication;

public class CommunicationException extends Exception {
    private Type type;

    public enum Type {
        CONNECT_EXCEPTION,
        UNKNOWN_HOST_EXCEPTION,
        SOCKET_NULL,
        SOCKET_DISCONNECTED,
        OS_ERROR,
        SOCKET_EXCEPTION,
        SOCKET_TIMEOUT,
        IO_EXCEPTION
    }

    public CommunicationException(Type type) {
        super(type.toString());
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
}
