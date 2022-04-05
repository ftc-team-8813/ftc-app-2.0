package org.firstinspires.ftc.teamcode.util.websocket;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SocketIO implements Closeable
{
    protected boolean connected = false;
    private boolean closed = false;

    public abstract void connect() throws IOException;

    public abstract InputStream getInputStream() throws IOException;

    public abstract OutputStream getOutputStream() throws IOException;

    public abstract String getConnectionInfo();

    public boolean connected()
    {
        return connected;
    }

    public boolean isClosed()
    {
        return closed;
    }

    @Override
    public void close() throws IOException
    {
        closed = true;
    }
}
