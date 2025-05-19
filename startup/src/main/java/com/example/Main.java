package com.example;

import com.jcraft.jsch.*;
import java.io.*;
import java.util.*;

public class Main {

    private static String sshUser;
    private static String sshPassword;
    private static String serverIP;
    private static String serverPort;
    private static String rmiRegistryPort;

    private static List<String> clientIPs;

    private static String serverCommand;
    private static List<String> clientCommands;

    public static void main(String[] args) {
        try {
            loadProperties("system.properties");

            // Start the server
            System.out.println("Starting server at " + serverIP);
            runRemoteCommand(serverIP, serverCommand);

            // Start all clients
            for (int i=0; i<clientIPs.size(); i++) {
                System.out.println("Starting client at " + clientIPs.get(i));
                runRemoteCommand(clientIPs.get(i), clientCommands.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties(String filePath) throws IOException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(filePath)) {
            props.load(in);
        }
		
		sshUser = props.getProperty("ssh.user");
        sshPassword = props.getProperty("ssh.password");

        serverIP = props.getProperty("GSP.server");
        serverPort = props.getProperty("GSP.server.port");
		rmiRegistryPort = props.getProperty("GSP.rmiregistry.port");
		int clientCount = Integer.parseInt(props.getProperty("GSP.numberOfNodes"));
		
		clientIPs = new ArrayList<>(clientCount);
		serverCommand = "java -jar /home/Server.jar " + serverPort + " " + rmiRegistryPort;
		for(int i=0; i<clientCount; i++){
			clientIPs.add(props.getProperty("GSP.node"+i));
			String cmd = String.format("java -jar /home/Client.jar %d %s %d false -random 20 50 0.7 5000", i, serverIP, rmiRegistryPort);
			clientCommands.add(cmd);
		}
    }

    private static void runRemoteCommand(String host, String command) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(sshUser, host, 22);
        session.setPassword(sshPassword);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();

        byte[] buffer = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) break;
                System.out.print(new String(buffer, 0, i));
            }
            if (channel.isClosed()) {
                System.out.println("Exit status: " + channel.getExitStatus());
                break;
            }
            Thread.sleep(1000);
        }

        channel.disconnect();
        session.disconnect();
    }
}
