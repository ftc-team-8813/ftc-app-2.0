package org.firstinspires.ftc.teamcode.util.websocket;

import org.firstinspires.ftc.teamcode.util.Logger;

import java.io.File;
import java.io.IOException;

public class UnixSocketServer extends ServerIO
{
    static
    {
        System.loadLibrary("junixsocket");
    }
    
    private int fd;
    private String path;
    private Logger log = new Logger("Unix Socket Server");
    
    public UnixSocketServer(String path) throws IOException
    {
        this.path = path;
        int fd = _create(path);
        if (fd < 0) throw new IOException(String.format("Error creating socket: %d", fd));
        this.fd = fd;
    }
    
    @Override
    public SocketIO accept() throws IOException
    {
        int client_fd = _accept(fd);
        if (client_fd < 0)
            throw new IOException(String.format("Error creating connection: %d", client_fd));
        return new UnixSocket(path, client_fd);
    }
    
    @Override
    public void close() throws IOException
    {
        _close(fd);
        if (!new File(path).delete()) log.w("Failed to delete socket file %s", path);
        super.close();
    }
    
    private native int _create(String path);
    
    private native int _accept(int server_fd);
    
    private native int _close(int fd);
}
