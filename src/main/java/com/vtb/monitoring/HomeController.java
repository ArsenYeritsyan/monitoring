package com.vtb.monitoring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final ServerMonitoringService monitoringService;
    private final SSHConnectionService sshConnectionService;
    private final ServerInfoService serverInfoService;


    public HomeController(ServerMonitoringService monitoringService, SSHConnectionService sshConnectionService, ServerInfoService serverInfoService) {
        this.monitoringService = monitoringService;
        this.sshConnectionService = sshConnectionService;
        this.serverInfoService = serverInfoService;
    }

//    @GetMapping("/")
//    public String home(Model model) {
//        List<ServerInfo> serverInfoList = monitoringService.getAllServerInfo();
////        List<ServerInfo> serverInfoList = monitoringService.updateAllServerInfo();
//        model.addAttribute("serverInfoList", serverInfoList);
//        return "home";
//    }

    @GetMapping("/system-info")
    public String getSystemInfo(Model model) {
//        List<ServerInfo> serverInfoList = new ArrayList<>();
//
//        List<ServerInfoConfig.Server> serverConfigs = loadServerConfigs();
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
//            serverInfoList.add(serverInfo);
//        }

        model.addAttribute("servers", monitoringService.getAllServerInfo());
        return "system-info";
    }


    private List<ServerInfoConfig.Server> loadServerConfigs() {
        return monitoringService.loadServerConfigs();
    }
}


