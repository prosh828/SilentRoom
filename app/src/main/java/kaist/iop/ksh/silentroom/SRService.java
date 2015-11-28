package kaist.iop.ksh.silentroom;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by KSH on 2015-11-19.
 */
public class SRService extends Service {

    private DeviceStatusManager deviceStatusManager;

    @Override
    public void onCreate() {
        super.onCreate();
        this.deviceStatusManager = new DeviceStatusManager(this);
    }

    @Override
    //다른 구성요소가 service를 시작할 때 호출 됨
    //한번 시작하면 명시적으로 stop할때까지 무기한으로 동작
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.deviceStatusManager.setBluetoothOn();

        //signal 받는 반복문 혹은 thread 추가
        int recvSignal = 0;
        //일단 받는다고 가정하고 진행
        if(recvSignal==0)
        {
            this.deviceStatusManager.saveDeviceStatus();
            this.deviceStatusManager.setSilentRoomMode();
        }
        else
        {

        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    //서비스를 소멸할 경우(마지막 호출)
    public void onDestroy() {
        super.onDestroy();
    }
}
