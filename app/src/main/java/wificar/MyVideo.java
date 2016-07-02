package wificar;

/*小R科技（www.xiao-r.com）旗下
 * 中国FPV-WIFI机器人网·机器人创意工作室（www.wifi-robots.com）对此源码拥有所有权
 * 此源码仅供用户学习之用，严禁用作商业牟利
 * 如发现侵权行为，小R科技将择机通过法律途径予以起诉！
 * */

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import my.wificar.R;

public class MyVideo extends Activity {
    public boolean runningToFront = false;
    public boolean runningToBack = false;
    private final long intervalTime = 100;
    private Button TakePhotos;
    private Button ViewPhotos;
    //控制车的方向按钮
    private Button BtnForward, BtnBackward, BtnLeft, BtnRight, BtnStop;
    //控制摄像头方向按钮
    private Button BtnTurnUp, BtnTurnDown, BtnTurnLeft, BtnTurnRight, BtnReset;
    //水平角度和垂直角度
    private static byte horiAngle = 0x00, virAngle = 0x00;

    private URL videoUrl;
    public static String CameraIp;
    public static String CtrlIp;
    public static String CtrlPort;
    MySurfaceView mySurfaceView;
    private Socket socket;
    //发送指令
    OutputStream sendSocket;
    //读取数据
    InputStream receiveSocket;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐去标题（应用的名字必须要写在setContentView之前，否则会有异常）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.myvideo);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        CameraIp = intent.getStringExtra("CameraIp");
        CtrlIp = intent.getStringExtra("ControlUrl");
        CtrlPort = intent.getStringExtra("Port");
        Log.d("wifirobot", "IP address is : " + CtrlIp);
        Log.d("wifirobot", "Port is : " + CtrlPort);
        initSocket();
        initView();
        initEvents();
        // 从Intent当中根据key取得value
        mySurfaceView.GetCameraIP(CameraIp);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public boolean initSocket() {
        Exception exception = null;

        try {
            socket = new Socket(InetAddress.getByName(CtrlIp), Integer.parseInt(CtrlPort));
        } catch (UnknownHostException e) {
            exception = e;
            e.printStackTrace();
        } catch (IOException e) {
            exception = e;
            e.printStackTrace();
        }
        try {
            sendSocket = socket.getOutputStream();
            //初始化socket数据流
            this.receiveSocket = socket.getInputStream();
        } catch (IOException e) {
            exception = e;
            e.printStackTrace();
        }

        if (exception != null) {
            Toast.makeText(this, "初始化网络失败！", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void onDestroy() {
        super.onDestroy();

    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2500) // System.currentTimeMillis()无论何时调用，肯定大于2500
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        mySurfaceView = (MySurfaceView) findViewById(R.id.mySurfaceViewVideo);
        TakePhotos = (Button) findViewById(R.id.TakePhoto);
        ViewPhotos = (Button) findViewById(R.id.ViewPhoto);

        BtnForward = (Button) findViewById(R.id.button_forward);
        BtnBackward = (Button) findViewById(R.id.button_backward);
        BtnLeft = (Button) findViewById(R.id.button_left);
        BtnRight = (Button) findViewById(R.id.button_right);
        BtnStop = (Button) findViewById(R.id.button_stop);

        /**
         * 摄像头控制按钮初始化
         */
        this.BtnTurnLeft = (Button) this.findViewById(R.id.button_turnLeft);
        this.BtnReset = (Button) this.findViewById(R.id.button_resume);
        this.BtnTurnRight = (Button) this.findViewById(R.id.button_turnRight);
        this.BtnTurnUp = (Button) this.findViewById(R.id.button_turnUp);
        this.BtnTurnDown = (Button) this.findViewById(R.id.button_turnDown);

    }

    private void initEvents() {
        /**
         * 控制小车方向
         */

        BtnForward.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                Log.e("log", "forward a little");
                checkeSocket();
                try {
                    sendSocket.write(Commands.CARFORWARD);
                    new Thread().sleep(intervalTime);
                    sendSocket.write(Commands.CARSTOP);
                    sendSocket.flush();
                    runningToBack = false;
                    runningToFront = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", "forward exception = " + e);
                }
            }
        });

        BtnForward.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                checkeSocket();
                Log.e("log", "forward continued");
                try {
                    sendSocket.write(Commands.CARFORWARD);
                    sendSocket.flush();
                    runningToFront = true;
                    runningToBack = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", "forward exception = " + e);
                }
                return true;
            }
        });

        BtnBackward.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                checkeSocket();
                Log.e("log", "backward a little");
                try {
                    sendSocket.write(Commands.CARRETREAT);
                    new Thread().sleep(intervalTime);
                    sendSocket.write(Commands.CARSTOP);
                    sendSocket.flush();
                    runningToFront = false;
                    runningToBack = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", "backward exception = " + e);
                }
            }

        });


        BtnBackward.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                checkeSocket();
                Log.e("log", "backward");
                try {
                    sendSocket.write(Commands.CARRETREAT);
                    sendSocket.flush();
                    runningToBack = true;
                    runningToFront = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", "backward exception = " + e);
                }
                return true;
            }
        });

        BtnRight.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("log", "action_down");
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("log", "action_up");
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.i("log", "action_move");
                    checkeSocket();
                    Log.i("log", "Trun right in a big angle ");
                    try {
                        sendSocket.write(Commands.CARRIGHT);
                        new Thread().sleep(intervalTime);
                        if (runningToFront == true) {
                            sendSocket.write(Commands.CARFORWARD);
                        } else if (runningToBack == true) {
                            sendSocket.write(Commands.CARRETREAT);
                        } else {
                            sendSocket.write(Commands.CARSTOP);
                        }
                        sendSocket.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("log", "Trun right exception = " + e);
                    }
                    return true;
                }
                return false;
            }
        });

        BtnRight.setOnClickListener(new OnClickListener() {
                                        public void onClick(View arg0) {
                                            checkeSocket();
                                            Log.e("log", "Turn right");
                                            try {
                                                sendSocket.write(Commands.CARRIGHT);
                                                new Thread().sleep(intervalTime);
                                                if (runningToFront == true) {
                                                    sendSocket.write(Commands.CARFORWARD);
                                                } else if (runningToBack == true) {
                                                    sendSocket.write(Commands.CARRETREAT);
                                                } else {
                                                    sendSocket.write(Commands.CARSTOP);
                                                }
                                                sendSocket.flush();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Log.e("log", "Trun right exception = " + e);
                                            }
                                        }

                                    }

        );

        BtnLeft.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        BtnLeft.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("log", "BtnLeft ACTION_DOWN");
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("log", "BtnLeft ACTION_UP");
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.i("log", "Trun left in a big angle");
                    checkeSocket();
                    Log.e("log", "Trun left");
                    try {
                        sendSocket.write(Commands.CARLEFT);
                        new Thread().sleep(intervalTime);
                        if (runningToFront == true) {
                            sendSocket.write(Commands.CARLEFTFRONT);
                        } else if (runningToBack == true) {
                            sendSocket.write(Commands.CARRETREAT);
                        } else {
                            sendSocket.write(Commands.CARSTOP);
                        }
                        sendSocket.flush();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("error", "Trun left exception = " + e);
                    }

                    return true;
                }
                return false;
            }
        });

        BtnLeft.setOnClickListener(new OnClickListener() {

                                       public void onClick(View arg0) {
                                           checkeSocket();
                                           Log.e("log", "Trun left");
                                           try {
                                               sendSocket.write(Commands.CARLEFT);
                                               new Thread().sleep(intervalTime);
                                               if (runningToFront == true) {
                                                   sendSocket.write(Commands.CARLEFTFRONT);
                                               } else if (runningToBack == true) {
                                                   sendSocket.write(Commands.CARRETREAT);
                                               } else {
                                                   sendSocket.write(Commands.CARSTOP);
                                               }
                                               sendSocket.flush();
                                           } catch (Exception e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                               Log.e("error", "Trun left exception = " + e);
                                           }
                                       }

                                   }

        );
        BtnStop.setOnClickListener(new

                                           OnClickListener() {

                                               public void onClick(View arg0) {
                                                   checkeSocket();
                                                   Log.e("log", "stop");
                                                   // TODO Auto-generated method stub
                                                   try {
                                                       sendSocket.write(Commands.CARSTOP);
                                                       sendSocket.flush();
                                                       runningToBack = false;
                                                       runningToFront = false;
                                                   } catch (Exception e) {
                                                       // TODO Auto-generated catch block
                                                       e.printStackTrace();
                                                       Log.e("error", "stop exception = " + e);
                                                   }
                                               }

                                           }

        );
        TakePhotos.setOnClickListener(new

                                              OnClickListener() {

                                                  public void onClick(View arg0) {
                                                      checkeSocket();
                                                      Log.e("log", "TakePhotos");
                                                      // TODO Auto-generated method stub
                                                      if (null != Constant.handler) {
                                                          Message message = new Message();
                                                          message.what = 1;
                                                          Constant.handler.sendMessage(message);
                                                      }
                                                  }

                                              }

        );

        ViewPhotos.setOnClickListener(new

                                              OnClickListener() {

                                                  public void onClick(View arg0) {
                                                      checkeSocket();
                                                      Log.e("error", "ViewPhotos");
                                                      // TODO Auto-generated method stub
                                                      Intent intent = new Intent();
                                                      intent.setClass(MyVideo.this, BgPictureShowActivity.class);
                                                      // 通过Intent对象启动另外一个Activity
                                                      MyVideo.this.startActivity(intent);

                                                  }

                                              }

        );

        /**
         * 控制摄像头方向
         */
        //复位
        this.BtnReset.setOnClickListener(new

                                                 OnClickListener() {
                                                     public void onClick(View arg0) {
                                                         checkeSocket();
                                                         Log.e("log", "BtnReset");
                                                         try {
                                                             //舵机7
                                                             sendSocket.write(new byte[]{(byte) 0xff, (byte) 0x01,
                                                                     (byte) 0x07, (byte) 0x00, (byte) 0xff});
                                                             //舵机8
                                                             sendSocket.write(new byte[]{(byte) 0xff, (byte) 0x01,
                                                                     (byte) 0x08, (byte) 0x00, (byte) 0xff});
                                                             sendSocket.flush();
                                                         } catch (Exception e) {
                                                             e.printStackTrace();
                                                             Log.e("error", "BtnReset exception = " + e);
                                                         }
                                                     }
                                                 }

        );

        //上转
        this.BtnTurnUp.setOnClickListener(new OnClickListener()

                                          {
                                              public void onClick(View arg0) {
                                                  checkeSocket();
                                                  Log.e("log", "BtnTurnUp");
                                                  try {
                                                      //舵机8
                                                      virAngle += 10;
                                                      sendSocket.write(new byte[]{(byte) 0xff, (byte) 0x01,
                                                              (byte) 0x08, virAngle, (byte) 0xff});
                                                      sendSocket.flush();
                                                  } catch (Exception e) {
                                                      e.printStackTrace();
                                                      Log.e("error", "BtnTurnUp exception = " + e);
                                                  }
                                              }
                                          }

        );

        //下转
        this.BtnTurnDown.setOnClickListener(new OnClickListener()

                                            {
                                                public void onClick(View arg0) {
//				try {
//					socket.setSoTimeout(3000);
//					Log.e("test", "read begin");
//					Log.e("test", "read Data = "+receiveSocket.read());
//				} catch (IOException e1) {
//					e1.printStackTrace();
//					Log.e("test", "read Data = " + e1);
//				}
//				Log.e("test", "read Data end");


                                                    Log.e("test", "BtnTurnDown");
                                                    try {
                                                        //舵机8
                                                        virAngle -= 15;
                                                        sendSocket.write(new byte[]{(byte) 0xff, (byte) 0x01,
                                                                (byte) 0x08, virAngle, (byte) 0xff});
                                                        sendSocket.flush();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Log.e("error", "BtnTurnDown exception = " + e);
                                                    }
                                                }
                                            }

        );

        //左转
        this.BtnTurnLeft.setOnClickListener(new OnClickListener()

                                            {
                                                public void onClick(View arg0) {
                                                    checkeSocket();
                                                    Log.e("log", "BtnTurnLeft");
                                                    try {
                                                        //舵机7
                                                        horiAngle -= 15;
                                                        sendSocket.write(new byte[]{(byte) 0xff, (byte) 0x01,
                                                                (byte) 0x07, horiAngle, (byte) 0xff});
                                                        sendSocket.flush();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Log.e("error", "BtnTurnLeft exception = " + e);
                                                    }
                                                }
                                            }

        );

        //右转
        this.BtnTurnRight.setOnClickListener(new OnClickListener()

                                             {
                                                 public void onClick(View arg0) {
                                                     checkeSocket();
                                                     Log.e("log", "BtnTurnRight");
                                                     try {
                                                         //舵机7
                                                         horiAngle += 15;
                                                         sendSocket.write(new byte[]{(byte) 0xff, (byte) 0x01,
                                                                 (byte) 0x07, horiAngle, (byte) 0xff});
                                                         sendSocket.flush();
                                                     } catch (Exception e) {
                                                         e.printStackTrace();
                                                         Log.e("error", "BtnTurnRight exception = " + e);
                                                     }
                                                 }
                                             }

        );

    }

    private void checkeSocket() {
        if (socket.isConnected() == false) {
            initSocket();
        }
    }

}

