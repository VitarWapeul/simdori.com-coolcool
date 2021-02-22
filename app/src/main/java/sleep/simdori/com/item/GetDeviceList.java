package sleep.simdori.com.item;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import sleep.simdori.com.AppConst;

/**
 * 장치 정보를 저장하여 OBJECT로 반환한다. 
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class GetDeviceList implements Parcelable {
	protected int key_No, key_Device_Status, key_Status;
	protected String key_Device_Mac, key_Device_Name, key_Device_Wakeup, key_Device_Sleep, key_Device_WakeupDay, key_Device_SleepDay;
	protected String key_Socket_Name, key_Wakeup, key_Sleep, key_WakeupDay, key_SleepDay, key_FWtime = "";
	protected int key_Socket, key_GPIO = AppConst.OFF, key_Socket_Img = AppConst.device_icon, key_Socket_Active=AppConst.device_acitve_icon;
	//protected float key_Socket_Img = 4.0F;
	protected int key_MasterPlug, key_PlugControl, key_PlugBlocker;
	protected int key_StanByPower, key_StanByPower_Threshold, key_VoiceAlert, key_Version = 0;
	protected int key_GasAlert, key_GasAlert_CO, key_GasAlert_LPG, key_GasAlert_SMOKE, key_FireAlert, key_FireAlert_Threshold = 0;
	protected double key_Latitude, key_Longtitude = 0;

	public GetDeviceList() { }
	public GetDeviceList(Parcel in) {
		readFromParcel(in);
	}
	
	//String key_Device_WakeupDay, String key_Device_SleepDay;
	
	// 장치 목록
	public GetDeviceList(int key_No, String key_Device_Mac, String key_Device_Name, int key_Device_Status, int key_Status,
                         int key_Socket, String key_Socket_Name, int key_GPIO, int key_Socket_Img, int key_Socket_Active,
                         String key_Wakeup, String key_Sleep, String key_WakeupDay, String key_SleepDay,
                         String key_Device_Wakeup, String key_Device_Sleep, String key_Device_WakeupDay, String key_Device_SleepDay,
                         int key_MasterPlug, int key_PlugControl, int key_PlugBlocker, int key_StanByPower, int key_StanByPower_Threshold,
                         int key_VoiceAlert, int key_FireAlert, int key_FireAlert_Threshold, int key_GasAlert, int key_GasAlert_CO, int key_GasAlert_LPG, int key_GasAlert_SMOKE,
                         double key_Latitude, double key_Longtitude, int key_Version, String key_FWtime) {
		this.key_No = key_No;
		this.key_Device_Mac = key_Device_Mac;
		this.key_Device_Name = key_Device_Name;
		this.key_Device_Status = key_Device_Status;
		this.key_Status = key_Status;
		this.key_Socket = key_Socket;
		this.key_Socket_Name = key_Socket_Name;
		this.key_GPIO = key_GPIO;
		this.key_Socket_Img = key_Socket_Img;
		this.key_Socket_Active = key_Socket_Active;
		this.key_Wakeup = key_Wakeup;
		this.key_Sleep = key_Sleep;
		this.key_WakeupDay = key_WakeupDay;
		this.key_SleepDay = key_SleepDay;
		this.key_Device_Wakeup = key_Device_Wakeup;
		this.key_Device_Sleep = key_Device_Sleep;
		this.key_Device_WakeupDay = key_Device_WakeupDay;
		this.key_Device_SleepDay = key_Device_SleepDay;
		this.key_MasterPlug = key_MasterPlug;
		this.key_PlugControl = key_PlugControl;
		this.key_PlugBlocker = key_PlugBlocker;
		this.key_StanByPower = key_StanByPower;
		this.key_StanByPower_Threshold = key_StanByPower_Threshold;
		this.key_VoiceAlert = key_VoiceAlert;
		this.key_FireAlert = key_FireAlert;
		this.key_FireAlert_Threshold = key_FireAlert_Threshold;
		this.key_GasAlert = key_GasAlert;
		this.key_GasAlert_CO = key_GasAlert_CO;
		this.key_GasAlert_LPG = key_GasAlert_LPG;
		this.key_GasAlert_SMOKE = key_GasAlert_SMOKE;
		this.key_Latitude = key_Latitude;
		this.key_Longtitude = key_Longtitude;
		this.key_Version = key_Version;
		this.key_FWtime = key_FWtime;
	}
	
	// 장치 추가
	public GetDeviceList(String key_Device_Mac, String key_Device_Name, int key_Device_Status, int key_Status,
                         int key_Socket_Img, int key_Socket_Active,
//			String key_Wakeup, String key_Sleep, String key_WakeupDay, String key_SleepDay,
                         String key_Device_Wakeup, String key_Device_Sleep, String key_Device_WakeupDay, String key_Device_SleepDay,
                         int key_MasterPlug, int key_PlugControl, int key_PlugBlocker, int key_StanByPower, int key_StanByPower_Threshold,
                         int key_VoiceAlert, int key_FireAlert, int key_FireAlert_Threshold, int key_GasAlert, int key_GasAlert_CO, int key_GasAlert_LPG, int key_GasAlert_SMOKE,
                         double key_Latitude, double key_Longtitude, int key_Version, String key_FWtime) {
		this.key_Device_Mac = key_Device_Mac;
		this.key_Device_Name = key_Device_Name;
		this.key_Device_Status = key_Device_Status;
		this.key_Status = key_Status;
//		this.key_Socket = key_Socket;
//		this.key_Socket_Name = key_Socket_Name;
//		this.key_GPIO = key_GPIO;
		this.key_Socket_Img = key_Socket_Img;
		this.key_Socket_Active = key_Socket_Active;		
//		this.key_Wakeup = key_Wakeup;
//		this.key_Sleep = key_Sleep;
//		this.key_WakeupDay = key_WakeupDay;
//		this.key_SleepDay = key_SleepDay;
		this.key_Device_Wakeup = key_Device_Wakeup;
		this.key_Device_Sleep = key_Device_Sleep;
		this.key_Device_WakeupDay = key_Device_WakeupDay;
		this.key_Device_SleepDay = key_Device_SleepDay;
		this.key_MasterPlug = key_MasterPlug;
		this.key_PlugControl = key_PlugControl;
		this.key_PlugBlocker = key_PlugBlocker;
		this.key_StanByPower = key_StanByPower;
		this.key_StanByPower_Threshold = key_StanByPower_Threshold;
		this.key_VoiceAlert = key_VoiceAlert;
		this.key_FireAlert = key_FireAlert;
		this.key_FireAlert_Threshold = key_FireAlert_Threshold;
		this.key_GasAlert = key_GasAlert;
		this.key_GasAlert_CO = key_GasAlert_CO;
		this.key_GasAlert_LPG = key_GasAlert_LPG;
		this.key_GasAlert_SMOKE = key_GasAlert_SMOKE;
		this.key_Latitude = key_Latitude;
		this.key_Longtitude = key_Longtitude;
		this.key_Version = key_Version;
		this.key_FWtime = key_FWtime;
	}
	
	// 소켓 편집
	public GetDeviceList(int key_Socket_Img, String key_Socket_Name) {
		this.key_Socket_Img = key_Socket_Img;
		this.key_Socket_Name = key_Socket_Name;
	}
	
	// 장치 편집
	public GetDeviceList(int key_No, String key_Device_Mac, String key_Device_Name) {
		this.key_No = key_No;
		this.key_Device_Mac = key_Device_Mac;
		this.key_Device_Name = key_Device_Name;
	}
	
	// 가스 알람
	public GetDeviceList(String key_Device_Name, int key_No, int key_GasAlert) {
		this.key_Device_Name = key_Device_Name;
		this.key_No = key_No;
		this.key_GasAlert = key_GasAlert;
	}
	
	// 권한 설정
	public GetDeviceList(String key_Device_Mac, String key_Device_Name, int key_Device_Status) {
		this.key_Device_Mac = key_Device_Mac;
		this.key_Device_Name = key_Device_Name;
		this.key_Device_Status = key_Device_Status;
	}
	
	// 예약그룹 장치추가
	public GetDeviceList(String key_Device_Mac, String key_Device_Name, String key_Socket_Name, int key_Socket) {
		this.key_Device_Mac = key_Device_Mac;
		this.key_Device_Name = key_Device_Name;
		this.key_Socket_Name = key_Socket_Name;
		this.key_Socket = key_Socket;
	}
	
	// 그룹 장치추가
	public GetDeviceList(String key_Device_Name, String key_Device_Mac) {
		this.key_Device_Name = key_Device_Name;
		this.key_Device_Mac = key_Device_Mac;
	}
	
	// 그룹 장치추가
	public GetDeviceList(String key_Device_Name, int key_Socket_Img, String key_Socket_Name) {
		this.key_Device_Name = key_Device_Name;
		this.key_Socket_Img = key_Socket_Img;
		this.key_Socket_Name = key_Socket_Name;
	}
	
	// 장치맥
	/*public GetDeviceList(int key_No, String key_Device_Mac) {
		this.key_No = key_No;
		this.key_Device_Mac = key_Device_Mac;
	}*/
	
	// 장치맥 및 소켓
	public GetDeviceList(String key_Device_Mac, int key_Socket) {
		this.key_Device_Mac = key_Device_Mac;
		this.key_Socket = key_Socket;
	}
	
	// 가스알람 설정목록
	public GetDeviceList(int key_GasAlert, int key_GasAlert_CO, int key_GasAlert_LPG, int key_GasAlert_SMOKE) {
		this.key_GasAlert = key_GasAlert;
		this.key_GasAlert_CO = key_GasAlert_CO;
		this.key_GasAlert_LPG = key_GasAlert_LPG;
		this.key_GasAlert_SMOKE = key_GasAlert_SMOKE;
	}
	
	// 소켓 추가
	public GetDeviceList(String key_Device_Mac, int key_Socket, int key_Socket_Img, String key_Socket_Name, int key_GPIO) {
		this.key_Device_Mac = key_Device_Mac;
		this.key_Socket = key_Socket;
		this.key_Socket_Img = key_Socket_Img;
		this.key_Socket_Name = key_Socket_Name;
		this.key_GPIO = key_GPIO;
	}
	
	// 소켓 편집
	public GetDeviceList(int key_No, String key_Device_Mac, int key_Socket, int key_Socket_Img, String key_Socket_Name) {
		this.key_No = key_No;
		this.key_Device_Mac = key_Device_Mac;
		this.key_Socket = key_Socket;
		this.key_Socket_Img = key_Socket_Img;
		this.key_Socket_Name = key_Socket_Name;
	}
	
	// 예약 설정 
	public GetDeviceList(int key_No, String key_Device_Mac, int key_Socket,
                         String key_Wakeup, String key_Sleep, String key_WakeupDay, String key_SleepDay) {
		this.key_No = key_No;
		this.key_Device_Mac = key_Device_Mac;
		this.key_Socket = key_Socket;
		this.key_Wakeup = key_Wakeup;
		this.key_Sleep = key_Sleep;
		this.key_WakeupDay = key_WakeupDay;
		this.key_SleepDay = key_SleepDay;
	}
		
	// 1. 장치 목록
	public int getKeyNo(){
		return this.key_No;
	}
	public String getKeyDeviceMac(){
		return this.key_Device_Mac;
	}
	public String getKeyDeviceName(){
		return this.key_Device_Name;
	}
	public int getKeyDeviceStatus(){
		return this.key_Device_Status;
	}
	public int getKeyStatus(){
		return this.key_Status;
	}
	
	// 2. 소켓 목록
	public int getKeySocket(){
		return this.key_Socket;
	}
	public String getKeySocketName(){
		return this.key_Socket_Name;
	}
	public int getKeyGPIO(){
		if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - StatusReceiver() gpio_Value - " + key_GPIO);
		return this.key_GPIO;
	}
	public int getKeySocketImg(){
		return this.key_Socket_Img;
	}
	public int getKeySocketActive(){
		return this.key_Socket_Active;
	}

	// 3. 예약 설정
	public String getKeyWakeUp(){
		return key_Wakeup;
	}
	public String getKeySleep(){
		return key_Sleep;
	}
	public String getKeyWakeUpDay(){
		return key_WakeupDay;
	}
	public String getKeySleepDay(){
		return key_SleepDay;
	}
	public String getKeyDevice_WakeUp(){
		return key_Device_Wakeup;
	}
	public String getKeyDevice_Sleep(){
		return key_Device_Sleep;
	}
	public String getKeyDevice_WakeUpDay(){
		return key_Device_WakeupDay;
	}
	public String getKeyDevice_SleepDay(){
		return key_Device_SleepDay;
	}

	// 4. 권한 설정
	public int getKeyMasterPlug(){
		return this.key_MasterPlug;
	}
	public int getKeyPlugControl(){
		return this.key_PlugControl;
	}
	public int getKeyPlugBlocker(){
		return this.key_PlugBlocker;
	}
	public int getKeyStanByPower(){
		return this.key_StanByPower;
	}
	public int getKeyStanByPower_Threshold(){
		return this.key_StanByPower_Threshold;
	}
	public int getKeyVoiceAlert(){
		return this.key_VoiceAlert;
	}
	public int getKeyFireAlert(){
		return this.key_FireAlert;
	}
	public int getKeyFireAlertThreshold(){
		return this.key_FireAlert_Threshold;
	}
	public int getKeyGasAlert(){
		return this.key_GasAlert;
	}
	public int getKeyGasAlertCO(){
		return this.key_GasAlert_CO;
	}
	public int getKeyGasAlertLPG(){
		return this.key_GasAlert_LPG;
	}
	public int getKeyGasAlertSMOKE(){
		return this.key_GasAlert_SMOKE;
	}

	// 5. 장치 설정
	public double getKeyLatitude(){
		return this.key_Latitude;
	}
	public double getKeyLongtitude(){
		return this.key_Longtitude;
	}
	public int getKeyVersion(){
		return this.key_Version;
	}
	public String getKeyFWtime(){
		return this.key_FWtime;
	}
	
	
	// 1. 장치 목록
	public void setKeyNo(int key_No) {
		this.key_No = key_No;
	}
	public void setKeyDeviceMac(String key_Device_Mac) {
		this.key_Device_Mac = key_Device_Mac;
	}
	public void setKeyDeviceName(String key_Device_Name) {
		this.key_Device_Name = key_Device_Name;
	}
	public void setKeyDeviceStatus(int key_Device_Status) {
		this.key_Device_Status = key_Device_Status;
	}
	public void setKeyStatus(int key_Status) {
		this.key_Status = key_Status;
	}
	
	// 2. 소켓 목록
	public void setKeySocket(int key_Socket) {
		this.key_Socket = key_Socket;
	}
	public void setKeySocketName(String key_SocketName) {
		this.key_Socket_Name = key_SocketName;
	}
	public void setKeyGPIO(int key_GPIO) {
		this.key_GPIO = key_GPIO;
	}
	public void setKeySocketImg(int key_Socket_Img) {
		this.key_Socket_Img = key_Socket_Img;
	}
	public void setKeySocketActive(int key_Socket_Active) {
		this.key_Socket_Active = key_Socket_Active;
	}

	
	// 3. 예약 설정
	public void setKeyWakeUp(String key_Wakeup) {
		this.key_Wakeup = key_Wakeup;
	}
	public void setKeySleep(String key_Sleep) {
		this.key_Sleep = key_Sleep;
	}
	public void setKeyWakeUpDay(String key_WakeupDay) {
		this.key_WakeupDay = key_WakeupDay;
	}
	public void setKeySleepDay(String key_SleepDay) {
		this.key_SleepDay = key_SleepDay;
	}
	public void setKeyDevice_WakeUp(String key_Device_Wakeup) {
		this.key_Device_Wakeup = key_Device_Wakeup;
	}
	public void setKeyDevice_Sleep(String key_Device_Sleep) {
		this.key_Device_Sleep = key_Device_Sleep;
	}
	public void setKeyDevice_WakeUpDay(String key_Device_WakeupDay) {
		this.key_Device_WakeupDay = key_Device_WakeupDay;
	}
	public void setKeyDevice_SleepDay(String key_Device_SleepDay) {
		this.key_Device_SleepDay = key_Device_SleepDay;
	}

	// 4. 권한 설정
	public void setKeyMasterPlug(int key_MasterPlug) {
		this.key_MasterPlug = key_MasterPlug;
	}
	public void setKeyPlugControl(int key_PlugControl) {
		this.key_PlugControl = key_PlugControl;
	}
	public void setKeyPlugBlocker(int key_PlugBlocker) {
		this.key_PlugBlocker = key_PlugBlocker;
	}
	public void setKeyStanByPower(int key_StanByPower) {
		this.key_StanByPower = key_StanByPower;
	}
	public void setKeyStanByPowerThreshold(int key_StanByPower_Threshold) {
		this.key_StanByPower_Threshold = key_StanByPower_Threshold;
	}
	public void setKeyVoiceAlert(int key_VoiceAlert) {
		this.key_VoiceAlert = key_VoiceAlert;
	}
	public void setKeyFireAlert(int key_FireAlert) {
		this.key_FireAlert = key_FireAlert;
	}
	public void setKeyFireAlertThreshold(int key_FireAlert_Threshold) {
		this.key_FireAlert_Threshold = key_FireAlert_Threshold;
	}
	public void setKeyGasAlert(int key_GasAlert) {
		this.key_GasAlert = key_GasAlert;
	}
	public void setKeyGasAlertCO(int key_GasAlert_CO) {
		this.key_GasAlert_CO = key_GasAlert_CO;
	}
	public void setKeyGasAlertLPG(int key_GasAlert_LPG) {
		this.key_GasAlert_LPG = key_GasAlert_LPG;
	}
	public void setKeyGasAlertSMOKE(int key_GasAlert_SMOKE) {
		this.key_GasAlert_SMOKE = key_GasAlert_SMOKE;
	}
	
	// 5. 장치 설정
	public void setKeyLatitude(double key_Latitude) {
		this.key_Latitude = key_Latitude;
	}
	public void setKeyLongtitude(double key_Longtitude) {
		this.key_Longtitude = key_Longtitude;
	}
	public void setKeyVersion(int key_Version) {
		this.key_Version = key_Version;
	}
	public void setKeyFWtime(String key_FWtime) {
		this.key_FWtime = key_FWtime;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(key_No);
		dest.writeString(key_Device_Mac);
		dest.writeString(key_Device_Name);
		dest.writeInt(key_Device_Status);
		dest.writeInt(key_Status);
		
		dest.writeInt(key_Socket);
		dest.writeString(key_Socket_Name);
		dest.writeInt(key_GPIO);
		dest.writeInt(key_Socket_Img);
		dest.writeInt(key_Socket_Active);

		dest.writeString(key_Wakeup);
		dest.writeString(key_Sleep);
		dest.writeString(key_WakeupDay);
		dest.writeString(key_SleepDay);
		dest.writeString(key_Device_Wakeup);
		dest.writeString(key_Device_Sleep);
		dest.writeString(key_Device_WakeupDay);
		dest.writeString(key_Device_SleepDay);

		dest.writeInt(key_MasterPlug);
		dest.writeInt(key_PlugControl);
		dest.writeInt(key_PlugBlocker);
		dest.writeInt(key_VoiceAlert);
		dest.writeInt(key_FireAlert);
		dest.writeInt(key_FireAlert_Threshold);
		dest.writeInt(key_StanByPower);
		dest.writeInt(key_StanByPower_Threshold);
		dest.writeInt(key_GasAlert);
		dest.writeInt(key_GasAlert_CO);
		dest.writeInt(key_GasAlert_LPG);
		dest.writeInt(key_GasAlert_SMOKE);
		dest.writeDouble(key_Latitude);
		dest.writeDouble(key_Longtitude);
		dest.writeInt(key_Version);
		dest.writeString(key_FWtime);
	}

	private void readFromParcel(Parcel in){
		key_No = in.readInt();
		key_Device_Mac = in.readString();
		key_Device_Name = in.readString();
		key_Device_Status = in.readInt();
		key_Status = in.readInt();
		key_Socket = in.readInt();
		key_Socket_Name = in.readString();
		key_GPIO = in.readInt();
		key_Socket_Img = in.readInt();
		key_Socket_Active = in.readInt();
		key_Wakeup = in.readString();
		key_Sleep = in.readString();
		key_WakeupDay = in.readString();
		key_SleepDay = in.readString();
		key_Device_Wakeup = in.readString();
		key_Device_Sleep = in.readString();
		key_Device_WakeupDay = in.readString();
		key_Device_SleepDay = in.readString();
		key_MasterPlug = in.readInt();
		key_PlugControl = in.readInt();
		key_PlugBlocker = in.readInt();
		key_VoiceAlert = in.readInt();
		key_FireAlert = in.readInt();
		key_FireAlert_Threshold = in.readInt();
		key_StanByPower = in.readInt();
		key_StanByPower_Threshold = in.readInt();
		key_GasAlert = in.readInt();
		key_GasAlert_CO = in.readInt();
		key_GasAlert_LPG = in.readInt();
		key_GasAlert_SMOKE = in.readInt();
		key_Latitude = in.readDouble();
		key_Longtitude = in.readDouble();
		key_Version = in.readInt();
		key_FWtime = in.readString();
	}
	
	public static final Creator CREATOR = new Creator() {
		public sleep.simdori.com.item.GetDeviceList createFromParcel(Parcel in) {
			return new sleep.simdori.com.item.GetDeviceList(in);
		}

		public sleep.simdori.com.item.GetDeviceList[] newArray(int size) {
			return new sleep.simdori.com.item.GetDeviceList[size];
		}
	};
}
