package com.vtb.monitoring;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class SSHConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(ServerInfoService.class);


    public ServerInfo retrieveSystemInfo(ServerInfo systemInfo) {
//        systemInfo.setIpAddress(systemInfo.getIpAddress());

        try {
            Session session = establishSSHSession(systemInfo.getIpAddress(), systemInfo.getUsername(), systemInfo.getPassword());

//            if (isWindows(session)) {
            if (systemInfo.getoS().equals("Windows")) {
                parseWindowsSystemInfo(session, systemInfo);
            } else {
                parseLinuxSystemInfo(session, systemInfo);
            }

            session.disconnect();
        } catch (Exception e) {
            // Set default values for properties when a connection error occurs
            systemInfo.setCpuLoad(0.0);
            systemInfo.setMemoryFree(0L);
            systemInfo.setMemoryUsed(0L);

            // ToDo Handle exceptions and logging
            logger.info(systemInfo + " ::: not connected - " + e.getMessage());
            return systemInfo;
        }


        return systemInfo;
    }


    private Session establishSSHSession(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, 22);
        session.setPassword(password);

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        return session;
    }

//    private boolean isWindows(Session session) throws IOException, JSchException {
//        ChannelExec channel = (ChannelExec) session.openChannel("exec");
//        channel.setCommand("cmd /c ver");
//        channel.connect();
//
//        InputStream in = channel.getInputStream();
//        byte[] buffer = new byte[1024];
//        int len = in.read(buffer);
//        String output = new String(buffer, 0, len);
//
//        channel.disconnect();
//
//        return output.toLowerCase().contains("windows");
//    }

    private void parseWindowsSystemInfo(Session session, ServerInfo serverInfo) throws IOException, JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("wmic cpu get loadpercentage && wmic OS get FreePhysicalMemory,TotalVisibleMemorySize");
        channel.connect();

        InputStream in = channel.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("LoadPercentage")) {
                serverInfo.setCpuLoad(Double.parseDouble(line.split("=")[1].trim()));
            } else if (line.contains("FreePhysicalMemory")) {
                serverInfo.setMemoryFree(Long.parseLong(line.split("=")[1].trim()));
            } else if (line.contains("TotalVisibleMemorySize")) {
                serverInfo.setMemoryUsed(Long.parseLong(line.split("=")[1].trim()));
            }
        }

        reader.close();
        channel.disconnect();
    }

    private void parseLinuxSystemInfo(Session session, ServerInfo serverInfo) throws IOException, JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("top -bn1 | grep 'Cpu(s)' && free | grep Mem");
        channel.connect();

        InputStream in = channel.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("%Cpu(s)")) {
                String[] cpuLoadParts = line.split(",");
                double cpuLoad = Double.parseDouble(cpuLoadParts[0].split(":")[1].trim().split(" ")[0]);
                serverInfo.setCpuLoad(cpuLoad);
            } else if (line.contains("Mem:")) {
                String[] memParts = line.split("\\s+");
                long memoryUsed = Long.parseLong(memParts[2]);
                long memoryFree = Long.parseLong(memParts[6]);
                serverInfo.setMemoryUsed(memoryUsed);
                serverInfo.setMemoryFree(memoryFree);
            }
        }

        reader.close();
        channel.disconnect();
    }
}