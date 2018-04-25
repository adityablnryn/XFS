package test;

import peer.Peer;
import peer.XFSPeer;

public class TimingTest {
    public static void main(String[] args) {
        try {
            StartTrackingServer.main(null);

            Peer peer0 = new XFSPeer(0);
            PeerDownloaderThread peerDownloaderThread = new PeerDownloaderThread(peer0);

            for(int i=0;i<50;i++) {
                peerDownloaderThread.start(); //50 concurrent downloads
            }

            long start = System.nanoTime();
            peer0.download("hello.txt"); //this download occurs when there are multiple other peers downloading
            long end = System.nanoTime();
            System.out.println("Time taken: "+(end-start)+" ns");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
