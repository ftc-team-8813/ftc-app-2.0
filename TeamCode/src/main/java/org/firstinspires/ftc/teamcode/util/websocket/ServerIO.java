package org.firstinspires.ftc.teamcode.util.websocket;

import java.io.Closeable;
import java.io.IOException;

public abstract class ServerIO implements Closeable
{
    private boolean isClosed = false;
    private boolean created = false;
    
    public abstract SocketIO accept() throws IOException;
    
    public boolean isClosed()
    {
        return isClosed;
    }
    
    @Override
    public void close() throws IOException
    {
        isClosed = true;
    }
}
