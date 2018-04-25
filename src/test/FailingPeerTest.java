package test;

import peer.Peer;
import peer.XFSPeer;

import java.rmi.Naming;

public class FailingPeerTest {
    public static void main(String[] args) {
        try {
            //Start TS
            StartTrackingServer.main(null);

            //Instantiate 1 peers
            Peer peer0 = new XFSPeer(0);

            Naming.unbind("peer0"); //Peer 0 fails
            System.out.println("PEER 0 FAILED");

            Peer recoveredPeer = new XFSPeer(0); //Peer 0 recovers
            System.out.println("PEER 0 RECOVERED");

            if(recoveredPeer.download("hello.txt")!=null) { //assuming peer0 had this file before crashing
                System.out.println("TEST PASS: Download from recovered peer successful");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
