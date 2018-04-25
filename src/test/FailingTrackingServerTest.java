package test;

import peer.Peer;
import peer.XFSPeer;
import server.TrackingServer;

import java.rmi.Naming;

public class FailingTrackingServerTest {
    public static void main(String [] args) {
        try {
            //Start TS
            StartTrackingServer.main(null);

            //Instantiate 2 peers
            Peer peer0 = new XFSPeer(0);
            Peer peer1 = new XFSPeer(1);

            Naming.unbind("ts"); // tracking server failing
            System.out.println("TRACKING SERVER FAILED");

            StartTrackingServer.main(null); // recover tracking server
            System.out.println("TRACKING SERVER RECOVERED");

            TrackingServer ts = (TrackingServer) Naming.lookup("ts");
            System.out.println("\nFiles available after recovery: ");
            for(String file : ts.getFileList()) {
                System.out.println(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
