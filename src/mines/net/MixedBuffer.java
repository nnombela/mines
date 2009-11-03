package mines.net;

import mines.model.Player;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;

/**
 * MixedBuffer class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 11-jul-2006
 */
public class MixedBuffer {
    protected ByteBuffer rcvBuffer;
    protected ByteBuffer sndBuffer;

    public MixedBuffer(int capacity) {
        this.rcvBuffer = ByteBuffer.allocateDirect(capacity);
        this.sndBuffer = ByteBuffer.allocateDirect(capacity);
    }

    public ByteBuffer receiver() {
        return this.rcvBuffer;
    }

    public ByteBuffer sender() {
        return this.sndBuffer;
    }

    public byte get() {
        return rcvBuffer.get();
    }

    public void put(byte value) {
        sndBuffer.put(value);
    }

    public boolean getBoolean() {
        return getInt() != 0;
    }

    public void putBoolean(boolean value) {
        putInt(value? 1 : 0);
    }

    public int getInt() {
        return rcvBuffer.getInt();    
    }

    public void putInt(int value) {
        sndBuffer.putInt(value);
    }

    public void putString(String string) {
        sndBuffer.putInt(string.length());
        sndBuffer.put(string.getBytes());
    }

    public String getString() {
        byte[] data = new byte[rcvBuffer.getInt()];
        rcvBuffer.get(data);
        return new String(data);
    }

    public String[] getMessage() {
        int size = getInt();
        String[] message = new String[size];
        for (int i = 0; i < size; ++i) {
            message[i] = getString();
        }
        return message;
    }

    public void putMessage(String[] message) {
        putInt(message.length);
        for (String str : message) {
            putString(str);
        }
    }

    public void putSocketAddress(InetSocketAddress address) {
        putString(address.getHostName());
        sndBuffer.putInt(address.getPort());
    }

    public InetSocketAddress getSocketAddress(boolean resolved) {
        String hostName = getString();
        int port = rcvBuffer.getInt();
        return resolved? new InetSocketAddress(hostName, port) :
                InetSocketAddress.createUnresolved(hostName, port);
    }

    public void putPlayer(Player player) {
        sndBuffer.putInt(player.getId());
        putSocketAddress(player.getAddress());
        putString(player.getUsername());
    }

    public Player getPlayer(boolean resolved) {
        int id = rcvBuffer.getInt();
        InetSocketAddress address = getSocketAddress(resolved);
        String username = getString();
        return new Player(id, address, username);
    }
}
