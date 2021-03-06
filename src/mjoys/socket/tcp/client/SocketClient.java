package mjoys.socket.tcp.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import mjoys.util.Address;
import mjoys.util.Logger;

// socket client reconnect server when disconnected
public class SocketClient {
    private InetSocketAddress serverAddress;
    private Address localAddress;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public boolean connect(Address server) {
        if (socket != null) {
            logger.log("socket client has connected server:%s", serverAddress.toString());
            return true;
        }
        
        this.serverAddress = server.toSocketAddress();
        if (this.serverAddress == null) {
            return false;
        }
        
        return connect();
    }
    
    public boolean reconnect(Address server) {
        disconnect();
        return connect(server);
    }
    
    public void send(byte[] data, int offset, int length) throws IOException {
        if (socket == null) {
            logger.log("socket client disconnected");
            return ;
        }
        synchronized(out) {
        	out.write(data, offset, length);
        }
    }
    
    public void send(byte[] data) throws IOException {
        send(data, 0, data.length);
    }
    
    public int recv(byte[] buffer) throws IOException {
    	return in.read(buffer);
    }
    
    public int recv(byte[] buffer, int offset, int length) throws IOException {
    	synchronized(in) {
    		return in.read(buffer, offset, length);
    	}
    }
    
    private boolean connect() {
        if (socket != null) {
            return true;
        }
        
        try {
            this.socket = new Socket();
            this.socket.connect(serverAddress);
            this.localAddress = Address.fromSocketAddress(this.socket.getLocalSocketAddress());
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
            logger.log("socket client connect server:%s", serverAddress.toString());
            return true;
        } catch (IOException e) {
            this.socket = null;
            logger.log("socket client connect server failed:" + e.getMessage());
            return false;
        }
    }
    
    public void disconnect() {
        if (socket == null) {
            return;
        }
        
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e1) {
            logger.log("close connection exception:", e1);
        } finally {
            socket = null;
        }
    }
    
    public void reconnect() {
        disconnect();
        
        // 重连,3s重连一次
        while (true) {
            if (connect()) {
                break;
            }
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e1) { }
        }
    }
    
    public Address getLocalAddress() {
        return this.localAddress;
    }
}
