package test;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class StartRMIRegistry {
    public static void main (String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            while(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
