import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FileServer extends Thread {

    private ServerSocket serverSocket;

    private FileServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                System.out.println("...waiting for client");
                Socket clientSock = serverSocket.accept();
                saveFile(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(Socket clientSock) throws IOException {
        System.out.println("saveFile is started");
        DataInputStream inputStream = new DataInputStream(clientSock.getInputStream());
        FileOutputStream outputStream = new FileOutputStream("testfile.jpg");
        byte[] buffer = new byte[4096];

        byte[] key = new byte[8];
        if (inputStream.read(key, 0, key.length) == -1) {
            System.out.println("failed to read key");
        }
        System.out.println("key:" + new String(key));

        byte[] fileSize = new byte[32];
        if (inputStream.read(fileSize, 0, fileSize.length) == -1) {
            System.out.println("failed to read fileSize");
        }
        long remaining = bytesToLong(fileSize);
        System.out.println("remaining:" + remaining);


        int read;
        int totalRead = 0;
        while((read = inputStream.read(buffer, 0, Math.min(buffer.length, (int)remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            outputStream.write(buffer, 0, read);
        }

        outputStream.close();
        inputStream.close();
        System.out.println("saveFile is finished");
    }

    private long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes, 0, Long.BYTES);
        buffer.flip();
        long result = buffer.getLong();
        System.out.println("bytesToLong, " + Arrays.toString(bytes) + " -> " + result);
        return result;
    }

    public static void main(String[] args) {
        FileServer fs = new FileServer(1988);
        fs.start();
    }

}