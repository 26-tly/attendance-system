package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/server")
public class ServerInfoController {

    @GetMapping("/ip")
    public ResponseEntity<Result<Map<String, Object>>> getServerIp() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String localIp = getLocalIpAddress();
            result.put("ip", localIp);
            result.put("port", 80);
            result.put("checkinUrl", "http://" + localIp + ":80");
            
            return ResponseEntity.ok(Result.success(result));
        } catch (Exception e) {
            result.put("ip", "127.0.0.1");
            result.put("port", 80);
            result.put("checkinUrl", "http://127.0.0.1:80");
            return ResponseEntity.ok(Result.success(result));
        }
    }

    private String getLocalIpAddress() {
        String hotspotIp = null;
        String routerIp = null;
        String campusIp = null;
        
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    
                    String ip = addr.getHostAddress();
                    if (ip.contains(":") || ip.equals("127.0.0.1")) {
                        continue;
                    }

                    if (isPrivateIp(ip)) {
                        String[] parts = ip.split("\\.");
                        int first = Integer.parseInt(parts[0]);
                        int second = Integer.parseInt(parts[1]);
                        
                        if (first == 172 && second >= 16 && second <= 31) {
                            hotspotIp = ip;
                        } else if (first == 192 && second == 168) {
                            routerIp = ip;
                        } else if (first == 10) {
                            campusIp = ip;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (hotspotIp != null) {
            return hotspotIp;
        }
        if (routerIp != null) {
            return routerIp;
        }
        if (campusIp != null) {
            return campusIp;
        }
        
        return "127.0.0.1";
    }

    private boolean isPrivateIp(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        int first = Integer.parseInt(parts[0]);
        int second = Integer.parseInt(parts[1]);
        
        if (first == 10) {
            return true;
        }
        if (first == 172 && second >= 16 && second <= 31) {
            return true;
        }
        if (first == 192 && second == 168) {
            return true;
        }
        if (first == 169 && second == 254) {
            return true;
        }
        
        return false;
    }
}
