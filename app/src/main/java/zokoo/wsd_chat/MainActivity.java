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
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter mBluetoothAdapter;

    //
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);

        System.out.println("Wlaczenie aplikacji.");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            /*
        // sprawdza czy sa sparowane urzadzenia; jesli tak to pobiera nazwe i adres kazdego z nich
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) { // Czy sa sparowane urzadzenia
            System.out.println("Znaleziono saprowane urzadzenia.");
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC
            }
        }

        // zarejestruj jesli urzadzenie zostalo odkryte..
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }
    */

    // wyrejestruj odbiornik przy wylaczeniu apki
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}


