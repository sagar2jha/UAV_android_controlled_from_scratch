package aztux.quadcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class QuadcontrolActivity extends Activity {
    Context ctx = null;
    Handler hand = new Handler();
    int height;
    boolean listening = true;
    InetAddress local;
    int pitCenterX;
    int pitCenterY;
    public int pitch = 0;
    public int roll = 0;
    /* renamed from: s */
    DatagramSocket f0s;
    String serverMsgs = "";
    /* renamed from: t */
    Timer f1t = new Timer();
    int thrCenterX;
    int thrCenterY;
    public int throttle = 0;
    TimerTask udptask;
    /* renamed from: v */
    View f2v = null;
    int width;
    public int yaw = 0;

    /* renamed from: aztux.quadcontrol.QuadcontrolActivity$1 */
    class C00041 implements Runnable {

        /* renamed from: aztux.quadcontrol.QuadcontrolActivity$1$1 */
        class C00031 implements Runnable {
            C00031() {
            }

            public void run() {
                QuadcontrolActivity.this.f2v.invalidate();
            }
        }

        C00041() {
        }

        public void run() {
            try {
                DatagramSocket sock = new DatagramSocket(7001);
                while (QuadcontrolActivity.this.listening) {
                    byte[] message = new byte[1500];
                    DatagramPacket p = new DatagramPacket(message, message.length);
                    sock.receive(p);
                    QuadcontrolActivity.this.serverMsgs = new String(message, 0, p.getLength());
                    QuadcontrolActivity.this.hand.post(new C00031());
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* renamed from: aztux.quadcontrol.QuadcontrolActivity$2 */
    class C00052 extends TimerTask {
        C00052() {
        }

        public void run() {
            try {
                QuadcontrolActivity.this.local = InetAddress.getByName(PIDActivity.ip);
                String msg = "{ \"type\": \"rcinput\", \"thr\": " + QuadcontrolActivity.this.throttle + ", \"yaw\": " + QuadcontrolActivity.this.yaw + ", \"pitch\": " + QuadcontrolActivity.this.pitch + ", \"roll\": " + QuadcontrolActivity.this.roll + "}\n";
                int msg_length = msg.length();
                QuadcontrolActivity.this.f0s.send(new DatagramPacket(msg.getBytes(), msg_length, QuadcontrolActivity.this.local, 7000));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* renamed from: aztux.quadcontrol.QuadcontrolActivity$4 */
    class C00074 implements OnTouchListener {
        C00074() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            int pointerCount = event.getPointerCount();
            for (int i = 0; i < pointerCount; i++) {
                int act = event.getAction();
                int y;
                if (act == 2 || act == 0) {
                    int x = (int) event.getX(i);
                    y = (int) event.getY(i);
                    if (((double) x) < ((double) QuadcontrolActivity.this.width) / 2.0d) {
                        int n_throttle = (int) ((((double) (QuadcontrolActivity.this.height - y)) / ((double) QuadcontrolActivity.this.height)) * 100.0d);
                        if (Math.abs(n_throttle - QuadcontrolActivity.this.throttle) < 20) {
                            QuadcontrolActivity.this.throttle = n_throttle;
                        }
                        int n_yaw = (int) ((((double) (x - QuadcontrolActivity.this.thrCenterX)) / (((double) QuadcontrolActivity.this.width) / 2.0d)) * 100.0d);
                        if (Math.sqrt(Math.pow((double) (n_throttle - QuadcontrolActivity.this.throttle), 2.0d) + Math.pow((double) (n_yaw - QuadcontrolActivity.this.yaw), 2.0d)) < 25.0d) {
                            QuadcontrolActivity.this.yaw = n_yaw;
                            QuadcontrolActivity.this.throttle = n_throttle;
                        }
                    } else {
                        int n_pitch = (int) ((((double) (QuadcontrolActivity.this.pitCenterY - y)) / ((double) QuadcontrolActivity.this.height)) * 100.0d);
                        int n_roll = (int) ((((double) (x - QuadcontrolActivity.this.pitCenterX)) / (((double) QuadcontrolActivity.this.width) / 2.0d)) * 100.0d);
                        if (Math.sqrt(Math.pow((double) (n_roll - QuadcontrolActivity.this.roll), 2.0d) + Math.pow((double) (n_pitch - QuadcontrolActivity.this.pitch), 2.0d)) < 25.0d) {
                            QuadcontrolActivity.this.roll = n_roll;
                            QuadcontrolActivity.this.pitch = n_pitch;
                        }
                    }
                } else if (act == 1) {
                    y = (int) event.getY(i);
                    if (((double) ((int) event.getX(i))) < ((double) QuadcontrolActivity.this.width) / 2.0d) {
                        QuadcontrolActivity.this.yaw = 0;
                    } else {
                        QuadcontrolActivity.this.pitch = 0;
                        QuadcontrolActivity.this.roll = 0;
                    }
                }
            }
            v.invalidate();
            return true;
        }
    }

    /* renamed from: aztux.quadcontrol.QuadcontrolActivity$5 */
    class C00085 implements Runnable {
        C00085() {
        }

        public void run() {
            Toast.makeText(QuadcontrolActivity.this.ctx, "PLEASE DISABLE PHONE CALLS", 500).show();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = this;
        Display display = getWindowManager().getDefaultDisplay();
        this.width = display.getWidth();
        this.height = display.getHeight();
        this.thrCenterX = this.width / 4;
        this.thrCenterY = this.height / 2;
        this.pitCenterX = (this.width / 4) * 3;
        this.pitCenterY = this.height / 2;
        try {
            this.f0s = new DatagramSocket();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        new Thread(new C00041()).start();
        this.udptask = new C00052();
        this.f1t.schedule(this.udptask, 50, 50);
        this.f2v = new View(this) {
            protected void onDraw(Canvas g) {
                int w = QuadcontrolActivity.this.width;
                int h = QuadcontrolActivity.this.height;
                Paint wpt = new Paint();
                wpt.setColor(-1);
                wpt.setStyle(Style.FILL);
                g.drawRect(0.0f, 0.0f, (float) w, (float) h, wpt);
                Paint p = new Paint();
                p.setColor(-16777216);
                p.setStrokeWidth(3.0f);
                int widthRange = w / 2;
                int heightRange = h;
                g.drawLine((float) QuadcontrolActivity.this.thrCenterX, 0.0f, (float) QuadcontrolActivity.this.thrCenterX, (float) h, p);
                g.drawLine(0.0f, (float) QuadcontrolActivity.this.thrCenterY, (float) (w / 2), (float) QuadcontrolActivity.this.thrCenterY, p);
                g.drawCircle((float) ((int) (((double) QuadcontrolActivity.this.thrCenterX) + ((((double) QuadcontrolActivity.this.yaw) / 50.0d) * ((double) (widthRange / 2))))), (float) ((int) (((double) h) - ((((double) QuadcontrolActivity.this.throttle) / 100.0d) * ((double) heightRange)))), 10.0f, p);
                g.drawCircle((float) ((int) (((double) QuadcontrolActivity.this.pitCenterX) + ((((double) QuadcontrolActivity.this.roll) / 50.0d) * ((double) (widthRange / 2))))), (float) ((int) (((double) QuadcontrolActivity.this.pitCenterY) - ((((double) QuadcontrolActivity.this.pitch) / 50.0d) * ((double) (heightRange / 2))))), 10.0f, p);
                g.drawLine((float) QuadcontrolActivity.this.pitCenterX, 0.0f, (float) QuadcontrolActivity.this.pitCenterX, (float) h, p);
                g.drawLine((float) (w / 2), (float) QuadcontrolActivity.this.pitCenterY, (float) w, (float) QuadcontrolActivity.this.pitCenterY, p);
                Canvas canvas = g;
                canvas.drawText("Thr " + QuadcontrolActivity.this.throttle + " Yaw " + QuadcontrolActivity.this.yaw + " Pitch " + QuadcontrolActivity.this.pitch + " Roll" + QuadcontrolActivity.this.roll, 10.0f, 10.0f, new Paint());
                Paint p3 = new Paint();
                p3.setTextSize(20.0f);
                g.drawText(QuadcontrolActivity.this.serverMsgs, 10.0f, 40.0f, p3);
            }
        };
        this.f2v.setOnTouchListener(new C00074());
        this.hand.post(new C00085());
        setContentView(this.f2v);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add("PID");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() == "PID") {
            startActivity(new Intent(this, PIDActivity.class));
        }
        return true;
    }

    protected void onPause() {
        super.onPause();
        this.pitch = 0;
        this.roll = 0;
        this.yaw = 0;
        this.throttle = 0;
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.udptask.cancel();
        this.listening = false;
    }
}
