package com.example;

// import com.jcraft.jsch.*;
import java.io.*;
import java.util.*;

public class Main {

	

    private static String serverCommand;
    private static List<String> clientCommands;
	private static int graphSize, batchSize, sleep;
	private static float writeRatio;


    public static void main(String[] args) {
        try {
            loadProperties("system.properties");

			List<Process> processes = new ArrayList<>();
            // Start the server
            System.out.println("Starting server");
            processes.add(runCommand(serverCommand));
			Thread.sleep(5000);
            // Start all clients
            for (int i=0; i<clientCommands.size(); i++) {
                System.out.println("Starting client " + i);
                processes.add(runCommand(clientCommands.get(i)));
            }

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					for (Process p : processes){
						p.destroy();
						try{
							p.waitFor();
						} catch (Exception e){
							p.destroyForcibly();
						}
					}	
				}
			}));

			while(true) {}
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
		graphSize = Integer.parseInt(props.getProperty("client.graphSize"));
		batchSize = Integer.parseInt(props.getProperty("client.batchSize"));
		writeRatio = Float.parseFloat(props.getProperty("client.writeRatio"));
		sleep = Integer.parseInt(props.getProperty("client.sleepInterval"));
		
        clientCommands = new ArrayList<>(clientCount);
		serverCommand = "java -jar Server.jar";
		for(int i=0; i<clientCount; i++){
			String cmd = String.format("java -jar Client.jar %d false -random %d %d %f %d", i, graphSize, batchSize, writeRatio, sleep);
			clientCommands.add(cmd);
		}
    }

    private static Process runCommand(String command) throws Exception {
        return new ProcessBuilder(command.split(" ")).inheritIO().start();
    }
}

