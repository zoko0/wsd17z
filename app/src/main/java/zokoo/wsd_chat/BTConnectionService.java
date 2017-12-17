package zokoo.wsd_chat;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.util.UUID;

public class BTConnectionService {
    private static final String appname = "MYAPP";
    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    ProgressDialog mProcessDialog;

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    public BTConnectionService(Context context, BluetoothAdapter mBluetoothAdapter) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
    }

    // watek do nasluchiwania polaczen
    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appname, UUID_INSECURE);
                System.out.println("Nawiazywanie polaczenia.");
            } catch (IOException e) {
                System.out.println("ERROR: IOException");
                e.printStackTrace();
            }
            mmServerSocket = tmp;
        }

        public void run(){
            System.out.println("Nawiazywanie polaczenia.");

            BluetoothSocket socket = null;

            try{
                socket = mmServerSocket.accept();
                System.out.println("Gniazdo zaakceptowalo polaczenie.");
            } catch (IOException e){
                System.out.println("ERROR: IOException");
                e.printStackTrace();
            }

            if (socket != null){
                connected(socket,mmDevice);
            }

            public void cancel() {
                System.out.println("Zamknij polaczenie.");
                try{
                    mmServerSocket.close();
                } catch (IOException e){
                    System.out.println("ERROR: IOException");
                    e.printStackTrace();
                }
            }
        }

        }
    }

    // watek do nawiazywania polaczen
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            System.out.println("Watek polaczenia - start");
            mmDevice = device;
            deviceUUID = uuid;
        }

        // odpala sie autamtycznie
        public void run() {
            BluetoothSocket tmp = null;
            System.out.println("Proba polaczenia");

            try{

            } catch (IOException e) {
                System.out.println("ERROR: IOException");
                e.printStackTrace();
            }


        }
    }
}