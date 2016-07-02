package wificar;

import android.util.Log;

/**
 * Created by lxhao on 16-7-1.
 * 小车的控制指令都写在这个类
 */
public class Commands {

    //开车系列

    //小车刹车
    public static final byte[] CARSTOP = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF};
    //小车向前
    public static final byte[] CARFORWARD = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0xFF};
    //小车倒车
    public static final byte[] CARRETREAT = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0xFF};
    //小车向左方开
    public static final byte[] CARLEFT = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0xFF};
    //小车向右方开
    public static final byte[] CARRIGHT = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0xFF};
    //小车向左前方开
    public static final byte[] CARLEFTFRONT = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0xFF};
    //小车向左后方开
    public static final byte[] CARLEFTREAR = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0xFF};
    //小车向右前方开
    public static final byte[] CARRIGHTFRONT = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0xFF};
    //小车向有后方开
    public static final byte[] CARRIGHTREAR = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0xFF};


    //拍照系列

    /**
     * @param steering 舵机号，现有7号和8号舵机
     * @param angle    旋转角度, 0-180度
     * @return 返回相应操作的指令
     */
    public static String getCmdOfCamera(int steering, int angle) {
        String cmd = null;
        if (steering == 7) {
            cmd = "FF0107" + Integer.toHexString(angle) + "FF";
        } else if (steering == 8) {
            cmd = "FF0108" + Integer.toHexString(angle) + "FF";
        } else {
            Log.e("TAG", "舵机号不存在!");
        }
        return cmd;
    }
}
