package com.vtb.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ServerMonitoringService {
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoService.class);

    private final ServerInfoConfig serverInfoConfig;
    private final SSHConnectionService sshConnectionService;
    //    @Async
//    public List<ServerInfo> updateAllServerInfo() {
//        List<CompletableFuture<ServerInfo>> futures = new ArrayList<>();
//
//        for (ServerInfoConfig.Server serverConfig : serverInfoConfig.getServers()) {
//            CompletableFuture<ServerInfo> future = CompletableFuture.supplyAsync(() -> {
//                ServerInfo serverInfo = serverConfig.toServerInfo();
//                serverInfo.fetchServerInfo();
//                return serverInfo;
//            });
//            futures.add(future);
//        }
//
//        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//        return allOf.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList())).join();
//    }
    private final List<ServerInfo> serverInfoList = new CopyOnWriteArrayList<>();

//    private List<ServerInfo> serverInfoList = new ArrayList<>();

    public ServerMonitoringService(ServerInfoConfig serverInfoConfig, SSHConnectionService sshConnectionService) {
        this.serverInfoConfig = serverInfoConfig;
        this.sshConnectionService = sshConnectionService;
    }

    //        @Async
//    @Scheduled(fixedDelay = 50000)
//        public void updateAllServerInfo() {
//
//            List<ServerInfoConfig.Server> serverConfigs = serverInfoConfig.getServers();
//
//            for (ServerInfoConfig.Server serverConfig : serverConfigs) {
//                ServerInfo serverInfo = new ServerInfo(
//                        serverConfig.getServerName(),
//                        serverConfig.getIpAddress(),
//                        serverConfig.getUsername(),
//                        serverConfig.getPassword(),
//                        serverConfig.getOS()
//                );
//
//                ServerInfo systemInfo = sshConnectionService.retrieveSystemInfo(serverInfo);
//                serverInfo.setCpuLoad(systemInfo.getCpuLoad());
//                serverInfo.setMemoryUsed(systemInfo.getMemoryUsed());
//                serverInfo.setMemoryFree(systemInfo.getMemoryFree());
//
//                serverInfoList.add(serverInfo);
//            }
//        }
    @Scheduled(fixedDelay = 40000)
    public void updateAllServerInfo() {
        List<ServerInfoConfig.Server> serverConfigs = serverInfoConfig.getServers();

        ExecutorService executor = Executors.newFixedThreadPool(serverConfigs.size());
        List<Future<ServerInfo>> futures = new ArrayList<>();

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
                // Update serverInfoList with the retrieved data
                updateServerInfoList(updatedServerInfo);
            } catch (Exception e) {
                // Handle exceptions
            }
        }
    }

    private void updateServerInfoList(ServerInfo updatedServerInfo) {
        synchronized (serverInfoList) {
            for (ServerInfo existingServerInfo : serverInfoList) {
                if (existingServerInfo.getServerName().equals(updatedServerInfo.getServerName())) {
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
//private List<ServerInfo> serverInfoList = new ArrayList<>();

//    @Async
//    @Scheduled(fixedDelay = 5000)
//    public void updateAllServerInfo() {
//        if (serverInfoConfig != null) {
//            List<CompletableFuture<ServerInfo>> futures = new ArrayList<>();
//
//            for (ServerInfoConfig.Server serverConfig : serverInfoConfig.getServers()) {
//                CompletableFuture<ServerInfo> future = CompletableFuture.supplyAsync(() -> {
//                    ServerInfo serverInfo = serverConfig.toServerInfo();
//                    serverInfo.fetchServerInfo();
//                    return serverInfo;
//                });
//                futures.add(future);
//            }
//
//            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//            allOf.thenRun(() -> futures.forEach(f -> serverInfoList.add(f.join())));
//        }
//    }

    public List<ServerInfo> getAllServerInfo() {
        logger.info("Retrieving all server info...");
        logger.info("Number of server info entries: {}", serverInfoList.size());
        return new ArrayList<>(serverInfoList);
    }

    public List<ServerInfoConfig.Server> loadServerConfigs() {
        return serverInfoConfig.getServers();
    }


//    @Async
//    @Scheduled(fixedDelay = 5000)
//    public void updateAllServerInfo() {
//        List<CompletableFuture<ServerInfo>> futures = new ArrayList<>();
//
//        for (ServerInfoConfig.Server serverConfig : serverInfoConfig.getServers()) {
//            CompletableFuture<ServerInfo> future = CompletableFuture.supplyAsync(() -> {
//                ServerInfo serverInfo = serverConfig.toServerInfo();
//                serverInfo.fetchServerInfo();
//                return serverInfo;
//            });
//            futures.add(future);
//        }
//
//        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//        allOf.thenRun(() -> futures.forEach(f -> serverInfoList.add(f.join())));
//    }

//    @Scheduled(fixedDelay = 5000)
//    public void updateServerInfo() {
//        List<ServerInfo> updatedServerInfoList = new ArrayList<>();
//
//        for (ServerInfoConfig.Server serverConfig : serverInfoConfig.getServers()) {
//            ServerInfo serverInfo = serverConfig.toServerInfo();
//            serverInfo.fetchServerInfo();
//            updatedServerInfoList.add(serverInfo);
//        }
//
//        serverInfoList = updatedServerInfoList;
//    }


//    @Scheduled(fixedDelay = 5000)
//    public List<ServerInfo> getAllServerInfo() {
//            List<ServerInfo> serverInfoList = new ArrayList<>();
//            for (ServerInfoConfig.Server serverConfig : serverInfoConfig.getServers()) {
//                ServerInfo serverInfo = serverConfig.toServerInfo();
//                serverInfo.fetchServerInfo();
//                serverInfoList.add(serverInfo);
//            }
//            return serverInfoList;
//        }
}


