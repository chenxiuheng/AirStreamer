/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.airplay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Device;

/**
 *
 * @author jasper
 */
public class DeviceConnection {

    private static Logger logger = Logger.getLogger(DeviceConnection.class.getName());
    private Device device;
    private Socket socket;

    public DeviceConnection(Device device) {
        this.device = device;
    }

    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // ignore anything here
            }
        }
    }

    public DeviceResponse sendCommand(DeviceCommand command) {
        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket(device.getInetAddress(), device.getPort());
            } catch (IOException e) {
                logger.error("Error creating socket", e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        try {
            BufferedReader deviceInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter deviceOutput = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String commandString = command.getCommandString();
            deviceOutput.write(commandString + "\n");
            deviceOutput.flush();

            StringBuilder fullResponse = new StringBuilder();
            String partialResponse;
            while (!(partialResponse = deviceInput.readLine().trim()).equals("")) {
                fullResponse.append(partialResponse);
                fullResponse.append("\n");
            }

            int contentLength = 0;
            if (fullResponse.indexOf("Content-Length:") != -1) {
                String cls = "Content-Length:";
                int si = fullResponse.indexOf(cls);
                int ei = fullResponse.indexOf("\n", si + cls.length());
                contentLength = Integer.parseInt(fullResponse.substring(si + cls.length(), ei).trim());
            }

            StringBuffer content = null;
            if (contentLength > 0) {
                content = new StringBuffer(contentLength);
                char buffer[] = new char[1024];
                int read, totalRead = 0;
                do {
                    read = deviceInput.read(buffer);
                    totalRead += read;
                    content.append(buffer, 0, read);
                } while (read != -1 && totalRead < contentLength);
            }

            return new DeviceResponse(fullResponse.toString(), content == null ? null : content.toString());
        } catch (IOException e) {
            logger.error("Error while sending command", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
