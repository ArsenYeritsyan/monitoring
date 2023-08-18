package com.vtb.monitoring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ServerMonitoringService monitoringService;

    public HomeController(ServerMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<ServerInfo> serverInfoList = monitoringService.getAllServerInfo();
//        List<ServerInfo> serverInfoList = monitoringService.updateAllServerInfo();
        model.addAttribute("serverInfoList", serverInfoList);
        return "home";
    }
}


