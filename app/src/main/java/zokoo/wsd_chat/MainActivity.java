package zokoo.wsd_chat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        System.out.println("Stan: off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        System.out.println("Stan: on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        System.out.println("Stan: w trakcie przelaczenia na OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        System.out.println("Stan: w trakcie przelaczenia na ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        System.out.println("Pozwala sie odkryc urzadzeniom: ON");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        System.out.println("Pozwala sie odkryc urzadzeniomd: OFF. Mozliwosc odbioru polaczen");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        System.out.println("Pozwala sie odkryc urzadzeniom: OFF");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        System.out.println("Pozwala sie odkryc urzadzeniomn: W trakcie laczenia");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        System.out.println("Pozwala sie odkryc urzadzeniom: Polaczono");
                        break;
                }
            }
        }
    };

    // bedzie wyswietlal urzadzenia
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                System.out.println("Znaleziono urzadzenie: " + device.getName() + "; " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // polaczone
                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    System.out.println("BT, bonding: Polaczony");
                }
                // tworzenie polaczenia
                if (device.getBondState() == BluetoothDevice.BOND_BONDING){
                    System.out.println("BT, bonding: W trakcie podlaczania");
                }

                // zerwanie polaczenia
                if (device.getBondState() == BluetoothDevice.BOND_NONE){
                    System.out.println("BT, bonding: Brak");
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
        Button btnDiscoverable = (Button) findViewById(R.id.btnDiscoverable);
        Button btnFindUnpairedDevices = (Button) findViewById(R.id.btnFindUnpairedDevices);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        lvNewDevices.setOnItemClickListener(MainActivity.this);

        System.out.println("Wlaczenie aplikacji.");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Przycisk: Wlacz modul Bluetooth");
                enableDisableBT();
            }
        });
    }

    public void enableDisableBT(){
        if (mBluetoothAdapter == null) {
            System.out.println("Moduł Bluetooth jest niedostepny, zamykam aplikacje.");
            Toast.makeText(this, "Moduł Bluetooth jest niedostepny, zamykam aplikacje.", Toast.LENGTH_LONG).show();
            finish();
        } else
            System.out.println("Moduł Bluetooth jest dostepny.");

        // jesli wylaczony bluetooth adapter
        if (!mBluetoothAdapter.isEnabled()) {
            System.out.println("Pytanie o wlaczenie modułu Bluetooth.");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            System.out.println("Wylacz Bluetooth.");
            mBluetoothAdapter.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, BTIntent);
        }
    }

    public void btnEnableDisable_Discoverable(View view) {
        System.out.println("Przycisk: Odkrywanie urzadzen wcisniety. Urzadzenie pozwala sie odkryc przez 300s");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);
    }

    public void btnDiscover(View view){
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();

            System.out.println("Przycisk: Restartuje wyszukiwanie urzadzen jesli juz szukal");

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()){
            System.out.println("Przycisk: Wyszukuje niesparowanych urzadzen");

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }

        Toast.makeText(MainActivity.this, "Click on device you want to bond", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mBluetoothAdapter.cancelDiscovery();

        System.out.println("Wybrano urzadzenie do sparowania");

        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        System.out.println("Nazwa urzadzenia: " + deviceName);
        System.out.println("MAC adres urzadzenia: " + deviceAddress);

        mBTDevices.get(i).createBond();
        System.out.println("Proba polaczenia");
    }

    // wywal odbiorniki przy wylaczeniu apki
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
    }

}


