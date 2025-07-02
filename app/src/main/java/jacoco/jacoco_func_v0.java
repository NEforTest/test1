package jacoco;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class jacoco_func_v0 {
    private static final String TAG = "JacocoHelper";
    private static String serverAddress;
    private static int serverPort;

    private static String DEFAULT_COVERAGE_FILE_PATH;

    public static void FileSenderClient() {
        serverAddress = "10.0.2.2";
        serverPort = 8080;
    }
    //ec文件的路径
    public void sendFilesFromDirectory(String directoryPath) throws IOException {
        File file = new File(directoryPath);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory");
        }

        if (file.isFile()) {
            sendFile(file);
        }
    }

    private static void sendFile(File file) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             FileInputStream fis = new FileInputStream(file)) {

            // Send file metadata
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            // Send file content
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, read);
            }

            // Get response from server
            String response = dis.readUTF();
            System.out.println("Server response for " + file.getName() + ": " + response);

        } catch (IOException e) {
            System.err.println("Error sending file " + file.getName() + ": " + e.getMessage());
        }
    }

    /**
     * 生成ec文件
     *
     */
    public static File generateEcFile() {
        OutputStream out = null;
        File mCoverageFilePath = new File(DEFAULT_COVERAGE_FILE_PATH);
        try {
            Log.d(TAG, DEFAULT_COVERAGE_FILE_PATH);
            if (mCoverageFilePath.exists()) {
                Log.d(TAG, "清除旧的ec文件");
                mCoverageFilePath.delete();
            }
            mCoverageFilePath.createNewFile();
            out = new FileOutputStream(mCoverageFilePath.getPath(), true);
            Object agent = Class.forName("org.jacoco.agent.rt.RT")
                    .getMethod("getAgent")
                    .invoke(null);
            if (agent != null) {
                out.write((byte[]) agent.getClass().getMethod("getExecutionData", boolean.class).invoke(agent, false));
                Log.d(TAG, "write");
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mCoverageFilePath;
    }

    public static void save_and_send(String COVERAGE_FILE_PATH) {
        try {
            DEFAULT_COVERAGE_FILE_PATH=COVERAGE_FILE_PATH;
            FileSenderClient();
            File file=generateEcFile();
            sendFile(file);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
}
