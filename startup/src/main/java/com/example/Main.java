package com.example;

// import com.jcraft.jsch.*;
import java.io.*;
import java.util.*;

public class Main {

	

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
		
		
		int clientCount = Integer.parseInt(props.getProperty("GSP.numberOfNodes"));
		
        clientCommands = new ArrayList<>(clientCount);
		clientIPs = new ArrayList<>(clientCount);
		serverCommand = "java -jar Server.jar";
		for(int i=0; i<clientCount; i++){
			// clientIPs.add(props.getProperty("GSP.node"+i));
			String cmd = String.format("java -jar Client.jar %d false -random 20 50 0.7 5000", i);
			clientCommands.add(cmd);
		}
    }

    private static void runRemoteCommand(String host, String command) throws Exception {
        new ProcessBuilder(command.split(" ")).inheritIO().start();
    }
}

