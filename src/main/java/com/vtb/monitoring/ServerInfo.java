package com.vtb.monitoring;

import java.util.Objects;

public class ServerInfo {
    private String serverName;
    private String ipAddress;
    private String username;
    private String password;
    private String oS;
    private double cpuLoad;
    private long memoryUsed;
    private long memoryFree;


    public ServerInfo(String serverName, String ipAddress, String username, String password, String oS) {
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.oS = oS;
    }
//
//    public void fetchServerInfo() {
//        if (isWindowsOs()) {
//            fetchWindowsServerInfo();
//        } else if (isUnixOs()) {
//            fetchUnixServerInfo();
//        }
//    }
//
//    private boolean isWindowsOs() {
//        return System.getProperty("os.name").toLowerCase().contains("win");
//    }
//
//    private boolean isUnixOs() {
//        String osName = System.getProperty("os.name").toLowerCase();
//        return osName.contains("nix") || osName.contains("nux");
//    }
//
//    private void fetchWindowsServerInfo() {
//        try {
//            JSch jsch = new JSch();
//            Session session = jsch.getSession(username, ipAddress, 22);
//            session.setPassword(password);
//            session.setConfig("StrictHostKeyChecking", "no"); // Disable host key checking
//            session.connect();
//
//            String command = "wmic cpu get loadpercentage && wmic OS get FreePhysicalMemory,TotalVisibleMemorySize";
//            String output = executeCommand(session, command);
//
//            String[] lines = output.split(System.lineSeparator());
//            cpuLoad = Double.parseDouble(lines[1].trim());
//            String[] memoryInfo = lines[2].split("\\s+");
//            memoryUsed = Long.parseLong(memoryInfo[2]) * 1024; // Convert to bytes
//            memoryFree = Long.parseLong(memoryInfo[1]) * 1024;
//
//            session.disconnect();
//        } catch (Exception e) {
//            System.out.println("Server IP : " + ipAddress + " - Connecting - " + e.getMessage());
//        }
//    }
//
//    private void fetchUnixServerInfo() {
//        try {
//            JSch jsch = new JSch();
//            Session session = jsch.getSession(username, ipAddress, 22);
//            session.setPassword(password);
//            session.setConfig("StrictHostKeyChecking", "no"); // Disable host key checking
//            session.connect();
//
//            String command = "top -bn1 | grep \"Cpu(s)\" && free | grep Mem";
//            String output = executeCommand(session, command);
//
//            String[] lines = output.split(System.lineSeparator());
//            cpuLoad = Double.parseDouble(lines[0].split(":")[1].trim());
//            String[] memoryInfo = lines[1].split("\\s+");
//            memoryUsed = Long.parseLong(memoryInfo[1]) * 1024; // Convert to bytes
//            memoryFree = Long.parseLong(memoryInfo[3]) * 1024;
//
//            session.disconnect();
//        } catch (Exception e) {
//            System.out.println("Server IP : " + ipAddress + " - Connecting - " + e.getMessage());
//        }
//    }
//
//    private String executeCommand(Session session, String command) throws Exception {
//        ChannelExec channel = (ChannelExec) session.openChannel("exec");
//        channel.setCommand(command);
//
//        InputStream in = channel.getInputStream();
//        channel.connect();
//
//        StringBuilder output = new StringBuilder();
//        byte[] buffer = new byte[1024];
//        int bytesRead;
//        while ((bytesRead = in.read(buffer)) > 0) {
//            output.append(new String(buffer, 0, bytesRead));
//        }
//
//        channel.disconnect();
//        return output.toString();
//    }


    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverName='" + serverName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", oS='" + oS + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfo that = (ServerInfo) o;
        return Objects.equals(ipAddress, that.ipAddress) && Objects.equals(oS, that.oS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress, oS);
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public long getMemoryFree() {
        return memoryFree;
    }

    public void setMemoryFree(long memoryFree) {
        this.memoryFree = memoryFree;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getoS() {
        return oS;
    }
}