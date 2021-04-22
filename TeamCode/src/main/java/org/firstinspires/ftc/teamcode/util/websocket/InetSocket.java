package org.firstinspires.ftc.teamcode.util.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InetSocket extends SocketIO
{
    private Socket socket;
    private String ip;
    private int port;
    
    public InetSocket()
    {
        socket = new Socket();
    }
    
    public InetSocket(String ip, int port)
    {
        socket = new Socket();
        this.ip = ip;
        this.port = port;
    }
    
    public InetSocket(Socket sock)
    {
        socket = sock;
        InetAddress addr = sock.getInetAddress();
        if (addr != null)
        {
            connected = true;
            this.ip = addr.getHostAddress();
            this.port = sock.getPort();
        }
    }
    
    public void setAddr(String ip, int port)
    {
        if (connected)
            throw new IllegalStateException("Socket already connected; cannot change address");
        if (isClosed()) throw new IllegalStateException("Socket closed");
        this.ip = ip;
        this.port = port;
    }
    
    @Override
    public void connect() throws IOException
    {
        if (connected) throw new IllegalStateException("Socket already connected");
        if (isClosed()) throw new IllegalStateException("Socket closed");
        socket.connect(new InetSocketAddress(ip, port));
        connected = true;
    }
    
    @Override
    public InputStream getInputStream() throws IOException
    {
        if (isClosed()) throw new IllegalStateException("Socket closed");
        return socket.getInputStream();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException
    {
        if (isClosed()) throw new IllegalStateException("Socket closed");
        return socket.getOutputStream();
    }
    
    @Override
    public String getConnectionInfo()
    {
        return String.format("%s:%d", ip, port);
    }
    
    @Override
    public void close() throws IOException
    {
        socket.close();
        super.close();
    }
}
