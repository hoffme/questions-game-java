package com.questions.red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Connection {

    private final Socket socket;

    private final DataInputStream input;
    private final DataOutputStream output;

    private final int sizeLengthData;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;

        this.output = new DataOutputStream(socket.getOutputStream());
        this.input = new DataInputStream(socket.getInputStream());

        this.sizeLengthData = 4;
    }

    public void send(byte[] data) throws IOException {
        byte[] lengthData = ByteBuffer.allocate(this.sizeLengthData).putInt(data.length).array();

        byte[] msg = new byte[lengthData.length + data.length];
        System.arraycopy(lengthData, 0, msg, 0, lengthData.length);
        System.arraycopy(data, 0, msg, lengthData.length, data.length);

        this.output.write(msg);
        this.output.flush();
    }

    public byte[] receive() throws IOException {
        int length = ByteBuffer.wrap(this.input.readNBytes(this.sizeLengthData)).getInt();
        return this.input.readNBytes(length);
    }

    public void close() throws IOException { this.socket.close(); }
}
