package kaist.iop.ksh.silentroom;

/**
 * Created by KSH on 2015-11-26.
 */
public class DeviceStatus {

    //Device의 상태를 저장하는 데 필요한 변수들 필요시 추가
    private boolean silent;
    private boolean vibration;
    private int volume;
    private float brightness;

    public void setDeviceStatus(boolean silent,boolean vibration, int volume,float brightness)
    {
        this.silent = silent;
        this.vibration = vibration;
        this.volume = volume;
        this.brightness = brightness;
    }

    public boolean getSilentFlag()
    {
        return this.silent;
    }

    public boolean getVibrationFlag()
    {
        return this.vibration;
    }

    public int getVolume()
    {
        return this.volume;
    }

    public float getBrightness()
    {
        return this.brightness;
    }
}
