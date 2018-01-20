package zokoo.wsd_chat;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BTConnectionService {
    private static final String appname = "MYAPP";
    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    ProgressDialog mProcessDialog;

    private BluetoothAdapter mBluetoothAdapter = null;
    Context mContext;

    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;

    private ConnectedThread mConnectedThread;

    public BTConnectionService(Context context) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        start();
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

        public void run() {
            System.out.println("Nawiazywanie polaczenia.");

            BluetoothSocket socket = null;

            try {
                socket = mmServerSocket.accept();
                System.out.println("Gniazdo zaakceptowalo polaczenie.");
            } catch (IOException e) {
                System.out.println("ERROR: IOException");
                e.printStackTrace();
            }

            if (socket != null) {
                connected(socket, mmDevice);
            }
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
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                System.out.println("ERROR: IOException " + e.getMessage());
                e.printStackTrace();
            }
            mmSocket = tmp;
            mBluetoothAdapter.cancelDiscovery();

            // polacz z BluetoothSocket
            try {
                mmSocket.connect();
                System.out.println("Polaczenie udane.");
            } catch (IOException e) {

                try{// zamknij polaczenie
                    mmSocket.close();
                } catch (IOException e1) {
                    System.out.println("ERROR: IOException " + e1.getMessage());
                    e1.printStackTrace();
                }
                System.out.println("ConnectThread nie moze sie polaczyc z podanym UUID: " + UUID_INSECURE);
            }
            connected(mmSocket, mmDevice);
        }


        public void cancel() {
            System.out.println("Zamknij polaczenie.");
            try{
                mmSocket.close();
            } catch (IOException e){
                System.out.println("ERROR: IOException");
                e.printStackTrace();
            }
        }
    }

    // czeka na polaczenia i rozpoczyna chat
    public synchronized void start(){
        System.out.println("Start chat");

        // cancel watkow probujacych sie polaczyc
        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    //
    public void startClient(BluetoothDevice device, UUID uuid){
        System.out.println("Start client");

        // inicjujue processdialog
        mProcessDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "Please Wait...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            System.out.println("Start ConnectedThread");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream  tmpOut = null;

            try {
                mProcessDialog.dismiss();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                System.out.println("Err message: " + e.getMessage());
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    System.out.println("Input stream: " + incomingMessage);

                    Intent incomingMessageIntent = new Intent("incomingMessage");
                    incomingMessageIntent.putExtra("theMessage", incomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);

                } catch (IOException e) {
                    System.out.println("Err czytanie inputu: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            System.out.println("Output stream: " + text);
            try{
                mmOutStream.write(bytes);
            } catch (IOException e) {
                System.out.println("Err czytanie outputu: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void cancel() {
            System.out.println("Zamknij polaczenie.");
            try{
                mmSocket.close();
            } catch (IOException e){
                System.out.println("ERROR: IOException");
                e.printStackTrace();
            }
        }
    }


    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        System.out.println("Start watku do zarzadzania polaczeniem");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public void write(byte[] out){
        ConnectedThread r;

        //
        System.out.println("Wywolano call");
        mConnectedThread.write(out);
    }
}