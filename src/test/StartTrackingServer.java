package test;

import server.TrackingServer;
import server.TrackingServerImpl;

import java.rmi.RemoteException;

public class StartTrackingServer {
    public static void main (String[] args) {
        try {
            TrackingServer ts = new TrackingServerImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
