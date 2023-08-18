package com.vtb.monitoring;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.InputStream;

public class ServerInfo {
    private final String serverName;
    private final String ipAddress;
    private final String username;
    private final String password;
    private double cpuLoad;
    private long memoryUsed;
    private long memoryFree;


    public ServerInfo(String serverName, String ipAddress, String username, String password) {
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
    }
    public void fetchServerInfo() {
        if (isWindowsOs()) {
            fetchWindowsServerInfo();
        } else if (isUnixOs()) {
            fetchUnixServerInfo();
        }
    }

    private boolean isWindowsOs() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private boolean isUnixOs() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nix") || osName.contains("nux");
    }

    private void fetchWindowsServerInfo() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, ipAddress, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no"); // Disable host key checking
            session.connect();

            String command = "wmic cpu get loadpercentage && wmic OS get FreePhysicalMemory,TotalVisibleMemorySize";
            String output = executeCommand(session, command);

            String[] lines = output.split(System.lineSeparator());
            cpuLoad = Double.parseDouble(lines[1].trim());
            String[] memoryInfo = lines[2].split("\\s+");
            memoryUsed = Long.parseLong(memoryInfo[2]) * 1024; // Convert to bytes
            memoryFree = Long.parseLong(memoryInfo[1]) * 1024;

            session.disconnect();
        } catch (Exception e) {
            System.out.println("Server IP : " + ipAddress + " - Connection Error - " + e.getMessage());
        }
    }

    private void fetchUnixServerInfo() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, ipAddress, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no"); // Disable host key checking
            session.connect();

            String command = "top -bn1 | grep \"Cpu(s)\" && free | grep Mem";
            String output = executeCommand(session, command);

            String[] lines = output.split(System.lineSeparator());
            cpuLoad = Double.parseDouble(lines[0].split(":")[1].trim());
            String[] memoryInfo = lines[1].split("\\s+");
            memoryUsed = Long.parseLong(memoryInfo[1]) * 1024; // Convert to bytes
            memoryFree = Long.parseLong(memoryInfo[3]) * 1024;

            session.disconnect();
        } catch (Exception e) {
            System.out.println("Server IP : " + ipAddress + " - Connection Error - " + e.getMessage());
        }
    }

    private String executeCommand(Session session, String command) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        InputStream in = channel.getInputStream();
        channel.connect();

        StringBuilder output = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) > 0) {
            output.append(new String(buffer, 0, bytesRead));
        }

        channel.disconnect();
        return output.toString();
    }
}