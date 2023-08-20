package com.vtb.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ServerInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoService.class);
    private final SSHConnectionService sshConnectionService;
    private final ServerInfoConfig serverInfoConfig;
    private final List<ServerInfo> serverInfoList;

    @Autowired
    public ServerInfoService(ServerInfoConfig serverInfoConfig, SSHConnectionService sshConnectionService) {
        this.serverInfoList = new ArrayList<>();
        this.sshConnectionService = sshConnectionService;
        this.serverInfoConfig = serverInfoConfig;
    }

    public List<ServerInfo> getAllServerInfo() {
        logger.info("Retrieving all server info...");
        logger.info("Number of server info entries: {}", serverInfoList.size());
        return new ArrayList<>(serverInfoList);
    }


    @Scheduled(fixedDelay = 5000)
    public void updateAllServerInfo() {
        List<ServerInfoConfig.Server> serverConfigs = serverInfoConfig.getServers();

        ExecutorService executor = Executors.newFixedThreadPool(serverConfigs.size());
        List<Future<ServerInfo>> futures = new CopyOnWriteArrayList<>();

        for (ServerInfoConfig.Server serverConfig : serverConfigs) {
            Future<ServerInfo> future = executor.submit(() -> {
                ServerInfo serverInfo = new ServerInfo(
                        serverConfig.getServerName(),
                        serverConfig.getIpAddress(),
                        serverConfig.getUsername(),
                        serverConfig.getPassword(),
                        serverConfig.getOS()
                );

                return sshConnectionService.retrieveSystemInfo(serverInfo);
            });

            futures.add(future);
        }

        executor.shutdown();

        for (Future<ServerInfo> future : futures) {
            try {
                ServerInfo updatedServerInfo = future.get();
                updateServerInfoList(updatedServerInfo);
            } catch (Exception e) {
                logger.info("catching in updateAllServerInfo");
            }
        }
    }

    private void updateServerInfoList(ServerInfo updatedServerInfo) {
        synchronized (serverInfoList) {
            for (ServerInfo existingServerInfo : serverInfoList) {
                if (existingServerInfo.getIpAddress().equals(updatedServerInfo.getIpAddress())) {
                    // Update properties if they have changed
                    if (existingServerInfo.getCpuLoad() != updatedServerInfo.getCpuLoad()) {
                        existingServerInfo.setCpuLoad(updatedServerInfo.getCpuLoad());
                    }
                    if (existingServerInfo.getMemoryFree() != updatedServerInfo.getMemoryFree()) {
                        existingServerInfo.setMemoryFree(updatedServerInfo.getMemoryFree());
                    }
                    if (existingServerInfo.getMemoryUsed() != updatedServerInfo.getMemoryUsed()) {
                        existingServerInfo.setMemoryUsed(updatedServerInfo.getMemoryUsed());
                    }
                    break;
                }
            }
        }
    }
//    @Scheduled(fixedDelay = 5000)
//    public void updateAllServerInfo() {
//        serverInfoList.clear();
//        List<ServerInfoConfig.Server> serverConfigs = serverInfoConfig.getServers();
//
//        for (ServerInfoConfig.Server serverConfig : serverConfigs) {
//            ServerInfo serverInfo = new ServerInfo(
//                    serverConfig.getServerName(),
//                    serverConfig.getIpAddress(),
//                    serverConfig.getUsername(),
//                    serverConfig.getPassword(),
//                    serverConfig.getOS()
//            );
//
//            ServerInfo systemInfo = sshConnectionService.retrieveSystemInfo(serverInfo);
//            serverInfo.setCpuLoad(systemInfo.getCpuLoad());
//            serverInfo.setMemoryUsed(systemInfo.getMemoryUsed());
//            serverInfo.setMemoryFree(systemInfo.getMemoryFree());
//
//            logger.info(String.valueOf(serverInfo));
//            serverInfoList.add(serverInfo);
//        }
//    }
}
