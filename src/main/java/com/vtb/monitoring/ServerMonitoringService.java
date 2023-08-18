package com.vtb.monitoring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServerMonitoringService {

    private final ServerInfoConfig serverInfoConfig;

    public ServerMonitoringService(ServerInfoConfig serverInfoConfig) {
        this.serverInfoConfig = serverInfoConfig;
    }

    private List<ServerInfo> serverInfoList = new ArrayList<>();

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

    @Scheduled(fixedDelay = 5000)
    public void updateServerInfo() {
        serverInfoList.clear();
        for (ServerInfoConfig.Server serverConfig : serverInfoConfig.getServers()) {
            ServerInfo serverInfo = serverConfig.toServerInfo();
            serverInfo.fetchServerInfo();
            serverInfoList.add(serverInfo);
        }
    }

    public List<ServerInfo> getAllServerInfo() {
        return serverInfoList;
    }
//        public List<ServerInfo> getAllServerInfo() {
//            List<ServerInfo> serverInfoList = new ArrayList<>();
//            for (ServerInfoConfig.Server serverConfig : serverInfoConfig.getServers()) {
//                ServerInfo serverInfo = serverConfig.toServerInfo();
//                serverInfo.fetchServerInfo();
//                serverInfoList.add(serverInfo);
//            }
//            return serverInfoList;
//        }
    }


