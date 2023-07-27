package ca.mta.iottestbed.arbiter;

import java.io.IOException;
import java.util.HashSet;

import ca.mta.iottestbed.logger.BufferedLogger;
import ca.mta.iottestbed.network.Connection;
import ca.mta.iottestbed.network.Listener;

/**
 * A smart meter that reads data from sensors over the network.
 * 
 * @author Hayden Walker
 * @version 2023-06-15
 */
public class NetworkedArbiter {

    /**
     * The port that the arbiter will listen on for access checks.
     */
    private static final int LISTENING_PORT = 5008;

    /**
     * The port that the arbiter will use to send results of access checks.
     */
    private static final int SENDING_PORT = 5007;

    /**
     * Set of active connections.
     */
    private HashSet<Connection> connections;

    /**
     * Arbiter's name.
     */
    private String name;

    /**
     * Log of network activity.
     */
    private BufferedLogger networkLog;

    /**
     * Permission table.
     */
    private TableArbiter arbiter;

    /**
     * Create a new Arbiter object.
     * 
     * @param name Name of Arbiter.
     */
    public NetworkedArbiter(String name) {
        this.connections = new HashSet<Connection>();
        this.name = name;
        this.networkLog = new BufferedLogger();
        this.networkLog.timestampEnabled(true);
        this.arbiter = new TableArbiter();
    }
       
    /**
     * Establish a connection with a sensor at a certain IP address.
     * 
     * @param ip IP address.
     */
    private void addDevice(String ip) throws IOException {
        Connection connection = new Connection(ip, SENDING_PORT);
        connection.addLogger(networkLog);
        connection.send("addmeter");
        connections.add(connection);
    }
    
    /**
     * Listen for new connections.
     * 
     * New connections will be listened to on a new thread.
     * 
     * @throws IOException If an IOException is encountered when opening or closing a socket.
     */
    private void listen() throws IOException {
        // ServerSocket to listen for incoming connections
        Listener listener = new Listener(LISTENING_PORT);
        listener.addLogger(networkLog);
        boolean active = true;

        while(active) {
            // read new socket
            Connection connection = listener.accept();
            connection.addLogger(networkLog);

            // create new thread to listen to the socket
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        monitor(connection);
                    } catch (IOException e) {

                    }
                }
            }).start();
        }

        listener.close();
    }

    /**
     * Listen to a connection.
     * 
     * Monitors a connection, and handles incoming messages. Stops listening
     * if a read fails.
     * 
     * @param socket Socket to listen to.
     * @throws IOException
     */
    private void monitor(Connection connection) throws IOException {
        // listen while connection is active
        boolean active = true;
    
        while(active) {
            // read data from socket
            String[] data = connection.receive();        

            // check for failure
            if(data == null) {
                active = false;
            }

            // log readings
            else if(data[1].equals("report")) {

            }

            // respond to ping
            else if(data[1].equals("ping")) {
                connection.send(name, "pong");
            }
        }

        // close and remove connection
        connections.remove(connection);
        connection.close();
    }

    public void start(String[] ips) throws IOException, InterruptedException {
        // add all ips
        // TODO: make this look for sensors
        for(String ip : ips) {
            addDevice(ip);
        }

        // listen for readings on a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { listen(); } catch(IOException e) {}
            }
        }).start();

        System.out.println("Meter " + name + " started.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // display readings periodically
                while(true) {
                    //System.out.println(name);
                    //displayReadings();
                    //networkLog.printFlush();
                    networkLog.printFlush();

                    // System.out.println("Active connections:");

                    // for(Connection connection : connections) {
                    //     System.out.println("\t" + connection.getIP());
                    // }

                    //TimeUnit.SECONDS.sleep(5);
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
    }

    /**
     * Start the meter. Usage: java -jar Meter.jar [name] [sensor IP addresses ...]
     * 
     * @param args Usage: java -jar Meter.jar [name] [sensor IP addresses ...]
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // // attempt to start
        // try { 
        //     Meter meter1 = new Meter(args[0]);
        
        //     String[] ips = new String[args.length - 1];
            
        //     for(int i = 1; i < args.length; i++) {
        //         ips[i - 1] = args[i];
        //     }
            
        //     meter1.start(ips);
        // }

        // // display error message
        // catch(Exception e) {
        //     System.err.println("Usage: java -jar Meter.jar [name] [valid IPs ...]");
        // }

        NetworkedArbiter a1 = new NetworkedArbiter("A1");
        a1.start(new String[]{"127.0.0.1"});
    }
}
