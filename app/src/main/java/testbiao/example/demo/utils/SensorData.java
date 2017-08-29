package testbiao.example.demo.utils;

/**
 * Created by szylover on 2017/5/18.
 */

public class SensorData {
    public float accelerometerX;
    public float accelerometerY;
    public float accelerometerZ;
    public float gyroscopeX;
    public float gyroscopeY;
    public float gyroscopeZ;
    public float gravityX;
    public float gravityY;
    public float gravityZ;

    public String toString() {
        return "accelerometerX=" + accelerometerX + " accelerometerY=" +
                accelerometerY + " accelerometerZ=" + accelerometerZ + " gyroscopeX=" +
                gyroscopeX + " gyroscopeY=" + gyroscopeY + " gyroscopeZ=" + gyroscopeZ + " gravityX=" +
                gravityX + " gravityY=" + gravityY + " gravityZ  =" + gravityZ;
    }
}
