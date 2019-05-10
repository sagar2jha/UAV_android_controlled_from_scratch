package aztux.quadcontrol;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class PIDActivity extends Activity {
    public static String _kd = "0";
    public static String _ki = "0";
    public static String _kp = "0";
    public static String ip = "192.168.0.254";
    int curpid = 0;
    EditText ipaddr;
    EditText kd;
    EditText ki;
    EditText kp;
    boolean pidlistloaded = false;
    SharedPreferences prefs;

    /* renamed from: aztux.quadcontrol.PIDActivity$1 */
    class C00001 implements OnItemSelectedListener {
        C00001() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
            if (PIDActivity.this.pidlistloaded) {
                PIDActivity.this.saveBoxes(PIDActivity.this.curpid);
            }
            PIDActivity.this.updateBoxes(pos);
            PIDActivity.this.pidlistloaded = true;
            PIDActivity.this.curpid = pos;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: aztux.quadcontrol.PIDActivity$2 */
    class C00022 implements OnClickListener {

        /* renamed from: aztux.quadcontrol.PIDActivity$2$1 */
        class C00011 implements Runnable {
            C00011() {
            }

            public void run() {
                for (int i = 0; i < 4; i++) {
                    PIDActivity.this.sendMsg("{ \"type\": \"pid\", \"pid\": \"pr_rate\",\"kp\": " + PIDActivity.this.prefs.getString("pid:0:kp", "1.0") + ", \"ki\": " + PIDActivity.this.prefs.getString("pid:0:ki", "1.0") + ", \"kd\": " + PIDActivity.this.prefs.getString("pid:0:kd", "1.0") + "}\n");
                    PIDActivity.this.sendMsg("{ \"type\": \"pid\", \"pid\": \"pr_stab\",\"kp\": " + PIDActivity.this.prefs.getString("pid:1:kp", "1.0") + ", \"ki\": " + PIDActivity.this.prefs.getString("pid:1:ki", "1.0") + ", \"kd\": " + PIDActivity.this.prefs.getString("pid:1:kd", "1.0") + "}\n");
                    PIDActivity.this.sendMsg("{ \"type\": \"pid\", \"pid\": \"yaw_rate\",\"kp\": " + PIDActivity.this.prefs.getString("pid:2:kp", "1.0") + ", \"ki\": " + PIDActivity.this.prefs.getString("pid:2:ki", "1.0") + ", \"kd\": " + PIDActivity.this.prefs.getString("pid:2:kd", "1.0") + "}\n");
                    PIDActivity.this.sendMsg("{ \"type\": \"pid\", \"pid\": \"yaw_stab\",\"kp\": " + PIDActivity.this.prefs.getString("pid:3:kp", "1.0") + ", \"ki\": " + PIDActivity.this.prefs.getString("pid:3:ki", "1.0") + ", \"kd\": " + PIDActivity.this.prefs.getString("pid:3:kd", "1.0") + "}\n");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        C00022() {
        }

        public void onClick(View arg0) {
            PIDActivity._kp = PIDActivity.this.kp.getText().toString();
            PIDActivity._ki = PIDActivity.this.ki.getText().toString();
            PIDActivity._kd = PIDActivity.this.kd.getText().toString();
            PIDActivity.ip = PIDActivity.this.ipaddr.getText().toString();
            PIDActivity.this.saveBoxes(PIDActivity.this.curpid);
            new Thread(new C00011()).start();
            PIDActivity.this.finish();
        }
    }

    void updateBoxes(int pid) {
        this.kp.setText(this.prefs.getString("pid:" + pid + ":kp", "1.0"));
        this.ki.setText(this.prefs.getString("pid:" + pid + ":ki", "1.0"));
        this.kd.setText(this.prefs.getString("pid:" + pid + ":kd", "1.0"));
    }

    void saveBoxes(int pid) {
        this.prefs.edit().putString("pid:" + pid + ":kp", this.kp.getText().toString()).commit();
        this.prefs.edit().putString("pid:" + pid + ":ki", this.ki.getText().toString()).commit();
        this.prefs.edit().putString("pid:" + pid + ":kd", this.kd.getText().toString()).commit();
    }

    void sendMsg(String str) {
        SocketException e1;
        UnknownHostException e;
        DatagramSocket s = null;
        InetAddress local = null;
        try {
            DatagramSocket s2 = new DatagramSocket();
            try {
                local = InetAddress.getByName(ip);
                s = s2;
            } catch (SocketException e2) {
                e1 = e2;
                s = s2;
                e1.printStackTrace();
                s.send(new DatagramPacket(str.getBytes(), str.length(), local, 5000));
            } catch (UnknownHostException e3) {
                e = e3;
                s = s2;
                e.printStackTrace();
                s.send(new DatagramPacket(str.getBytes(), str.length(), local, 5000));
            }
        } catch (SocketException e4) {
            e1 = e4;
            e1.printStackTrace();
            s.send(new DatagramPacket(str.getBytes(), str.length(), local, 5000));
        } catch (UnknownHostException e5) {
            e = e5;
            e.printStackTrace();
            s.send(new DatagramPacket(str.getBytes(), str.length(), local, 5000));
        }
        try {
            s.send(new DatagramPacket(str.getBytes(), str.length(), local, 5000));
        } catch (UnknownHostException e6) {
            e6.printStackTrace();
        } catch (IOException e7) {
            e7.printStackTrace();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0009R.layout.pid);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.kp = (EditText) findViewById(C0009R.id.kp);
        this.ki = (EditText) findViewById(C0009R.id.ki);
        this.kd = (EditText) findViewById(C0009R.id.kd);
        this.ipaddr = (EditText) findViewById(C0009R.id.ipaddr);
        this.ipaddr.setText(ip);
        updateBoxes(0);
        Spinner pidSelect = (Spinner) findViewById(C0009R.id.pidSelect);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, C0009R.array.pid_array, 17367048);
        adapter.setDropDownViewResource(17367049);
        pidSelect.setAdapter(adapter);
        pidSelect.setOnItemSelectedListener(new C00001());
        ((Button) findViewById(C0009R.id.doupdate)).setOnClickListener(new C00022());
    }
}
