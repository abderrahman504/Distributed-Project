package com.example;

// import com.jcraft.jsch.*;
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
			Thread.sleep(5000);
            // Start all clients
            for (int i=0; i<clientCommands.size(); i++) {
                System.out.println("Starting client");
                runRemoteCommand("", clientCommands.get(i));
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
		
        clientCommands = new ArrayList<>(clientCount);
		clientIPs = new ArrayList<>(clientCount);
		serverCommand = "java -jar Server.jar " + rmiRegistryPort + " " + serverPort;
		for(int i=0; i<clientCount; i++){
			// clientIPs.add(props.getProperty("GSP.node"+i));
			String cmd = String.format("java -jar Client.jar %d %s %d false -random 20 50 0.7 5000", i, serverIP, Integer.parseInt(rmiRegistryPort));
			clientCommands.add(cmd);
		}
    }

    private static void runRemoteCommand(String host, String command) throws Exception {
        new ProcessBuilder(command.split(" ")).inheritIO().start();
    }
}

