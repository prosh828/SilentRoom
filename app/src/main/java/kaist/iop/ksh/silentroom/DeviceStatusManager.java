package kaist.iop.ksh.silentroom;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by KSH on 2015-11-23.
 */
public class DeviceStatusManager {
    private static final String TAG = DeviceStatusManager.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private DeviceStatus mDeviceStatus;
    private AudioManager audioManager;
    private Context context;
    private Window window;

    public DeviceStatusManager(Context context)
    {
        //parent의 context를 가져옴
        this.context = context;
        this.window = ((Activity)this.context).getWindow();

        //audio manager instance 생성
        audioManager = (AudioManager)this.context.getSystemService(this.context.AUDIO_SERVICE);

        //BluetoothAdapter 인스턴스를 받아옴
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //device가 BT를 지원하지 않을 경우
        if (mBluetoothAdapter == null)
        {

        }
        //Device의 상태를 저장할 구조체 생성
        this.mDeviceStatus = new DeviceStatus();
    }

    public BluetoothAdapter getBluetoothAdapter()
    {
        return this.mBluetoothAdapter;
    }

    /*Bluetooth가 켜져 있는지 확인한다.
    *defaul로는 user에게 dialog를 띄우고 BT를 켜도록 한다.
    * 추후 자동으로 켜지는 방법 고려해야 함.
     */
    public boolean checkBluetoothIsOn()
    {
        //BT가 disabled 상태이면 false를 return
        if(mBluetoothAdapter.isEnabled())
        {
            return true;
        }

        return false;
    }

    public boolean setBluetoothOn()
    {
        //RESULT_OK = -1, RESULT_CANCELED = 0
        //Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //일단 여기에 activity를 만들지 않아서 함수 자체를 사용할 수 없음
        //startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);

        //위의 것은 가능하면 하는 것으로 하고, 일단 자동으로 바꿔주는 것으로 시도
        if(checkBluetoothIsOn())
        {
            //BT가 이미 켜져있을 경우
            Log.d(TAG, "BT is already enabled");
            return false;
        }
        else
        {
            //BT가 꺼져있을 경우 켠다.
            this.mBluetoothAdapter.enable();
            Log.d(TAG, "BT is enabled");
            return true;
        }
    }

    public void saveDeviceStatus()
    {
        this.mDeviceStatus.setDeviceStatus(getSilentFlag(),getVibrationFlag(),getVolumeLevel(),getBrightness());
        Log.d(TAG, "device status is saved");
    }

    //만약 자동 밝기 설정을 해놨다면? 그것도 처리해야하나..
    public float getBrightness()
    {
        try{
            float curBrightnessValue = android.provider.Settings.System.getInt(
                    this.context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            Log.d(TAG, "get brightness:"+curBrightnessValue);
            return curBrightnessValue;
        } catch(Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean getSilentFlag()
    {
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean getVibrationFlag()
    {
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //ringtone volume을 return 받는다.
    //Media의 volume을 얻으려면 STREAM_RING 대신 STREAM_MUSIC 사용
    public int getVolumeLevel()
    {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        Log.d(TAG, "get volume level:"+currentVolume);
        return currentVolume;
    }

    //SilentRoom 모드로 변경한다.
    public void setSilentRoomMode()
    {
        Log.d(TAG, "change to silent room mode");
        //Silent모드로 변경
        this.audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        //this.audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

        //Brightness 줄임
        Settings.System.putInt(this.context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, 10);
        WindowManager.LayoutParams layoutParams = this.window.getAttributes();
        layoutParams.screenBrightness = (float) 10 / (float) 255;
        this.window.setAttributes(layoutParams);
    }

    //SilentRoom 모드를 해제한다.
    public void releaseSilentRoomMode()
    {
        if(mDeviceStatus.getSilentFlag())
        {
            this.audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        if(mDeviceStatus.getVibrationFlag())
        {
            this.audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
        if(!mDeviceStatus.getSilentFlag()&&!mDeviceStatus.getVibrationFlag())
        {
            this.audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

        //Brightness 원래대로
        Settings.System.putInt(this.context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, (int)mDeviceStatus.getBrightness());
        WindowManager.LayoutParams layoutParams = this.window.getAttributes();
        layoutParams.screenBrightness = mDeviceStatus.getBrightness() / (float) 255;
        this.window.setAttributes(layoutParams);
    }

}
