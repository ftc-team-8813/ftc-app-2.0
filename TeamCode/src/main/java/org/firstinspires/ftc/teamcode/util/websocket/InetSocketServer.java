package org.firstinspires.ftc.teamcode.util.websocket;

import java.io.IOException;
import java.net.ServerSocket;

public class InetSocketServer extends ServerIO
{
    private ServerSocket sock;
    
    public InetSocketServer(int port) throws IOException
    {
        sock = new ServerSocket(port);
    }
    
    @Override
    public SocketIO accept() throws IOException
    {
        return new InetSocket(sock.accept());
    }
    
    @Override
    public void close() throws IOException
    {
        sock.close();
        super.close();
    }
}
