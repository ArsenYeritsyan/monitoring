package com.vtb.monitoring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@ConfigurationProperties(prefix = "serverinfo")
public class ServerInfoConfig {

    private final List<Server> servers = new ArrayList<>();

    public List<Server> getServers() {
        return servers;
    }

    public static class Server {
        private String serverName;
        private String ipAddress;
        private String username;
        private String password;

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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public ServerInfo toServerInfo() {
            return new ServerInfo(serverName, ipAddress, username, password);
        }
    }
}

