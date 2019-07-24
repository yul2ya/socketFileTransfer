import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FileClient {

    private Socket socket;

    private FileClient(String host, int port, String file) {
        try {
            socket = new Socket(host, port);
            sendFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String file) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];

        byte[] key = "testKey".getBytes();
        outputStream.write(key);
        outputStream.flush();

        long size = new File(file).length();
        outputStream.write(longToBytes(size));
        outputStream.flush();

        int read;
        int writeCount = 0;
        while ((read = inputStream.read(buffer)) > 0) {
            writeCount += read;
            System.out.println("writeCount:" + writeCount);
            outputStream.write(buffer, 0, read);
            outputStream.flush();
        }

        inputStream.close();
        outputStream.close();
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.putLong(x);
        for (int i = buffer.position(); i < buffer.limit(); i++) {
            buffer.put((byte)'p');
        }
        buffer.flip();
        byte[] result = buffer.array();
        System.out.println("longToBytes, " + x + " -> " + Arrays.toString(result));
        return result;
    }

    public static void main(String[] args) {
        FileClient fileClient = new FileClient("localhost", 1988, "testfile.jpg");
    }

}