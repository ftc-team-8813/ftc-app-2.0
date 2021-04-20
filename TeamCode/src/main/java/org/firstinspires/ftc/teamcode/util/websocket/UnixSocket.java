package org.firstinspires.ftc.teamcode.util.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

public class UnixSocket extends SocketIO
{
    private String path;
    private int fd;
    
    static
    {
        System.loadLibrary("junixsocket");
    }
    
    public UnixSocket() throws IOException
    {
        int fd = _create();
        if (fd < 0) throw new IOException("Error creating socket");
        this.fd = fd;
    }
    
    public UnixSocket(String path) throws IOException
    {
        int fd = _create();
        this.path = path;
        if (fd < 0) throw new IOException("Error creating socket");
        this.fd = fd;
    }
    
    public UnixSocket(String path, int fd)
    {
        this.path = path;
        this.fd = fd;
        connected = true;
    }
    
    public void setPath(String path)
    {
        if (connected) throw new IllegalStateException("Already connected");
        if (isClosed()) throw new IllegalStateException("Socket closed");
        this.path = path;
    }
    
    @Override
    public void connect() throws IOException
    {
        if (connected) throw new IllegalStateException("Already connected");
        if (isClosed()) throw new IllegalStateException("Socket closed");
        if (_connect(fd, path) < 0) throw new IOException("Error connecting");
        connected = true;
    }
    
    @Override
    public InputStream getInputStream() throws IOException
    {
        if (isClosed()) throw new IllegalStateException("Socket closed");
        return new SockInputStream();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException
    {
        if (isClosed()) throw new IllegalStateException("Socket closed");
        return new SockOutputStream();
    }
    
    @Override
    public String getConnectionInfo()
    {
        return path;
    }
    
    @Override
    public void close() throws IOException
    {
        _close(fd);
        super.close();
    }
    
    private native int _create();
    
    private native int _connect(int fd, String path);
    
    private native int _close(int fd);
    
    private native int _send(int fd, byte[] buf, int off, int len);
    
    private native int _recv(int fd, byte[] buf, int off, int len);
    
    private class SockInputStream extends InputStream
    {
        
        @Override
        public int read() throws IOException
        {
            byte[] data = new byte[1];
            int n = read(data, 0, 1);
            if (n < 1) return -1;
            return data[0] & 0xFF;
        }
        
        @Override
        public int read(byte[] data, int off, int len)
        {
            return _recv(fd, data, off, len);
        }
    }
    
    private class SockOutputStream extends OutputStream
    {
        @Override
        public void write(int b) throws IOException
        {
            byte[] data = {(byte) (b & 0xFF)};
            write(data, 0, 1);
        }
        
        @Override
        public void write(byte[] data, int off, int len) throws IOException
        {
            
            while (len > 0)
            {
                int sent = _send(fd, data, off, len);
                if (sent == 0) throw new SocketException("Client disconnected");
                if (sent < 0) throw new SocketException("Connection error");
                
                len -= sent;
                off += sent;
            }
        }
    }
}
