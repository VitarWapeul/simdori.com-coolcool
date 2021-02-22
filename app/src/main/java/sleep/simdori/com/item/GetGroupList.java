package sleep.simdori.com.item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 그룹 정보를 저장하여 OBJECT로 반환한다. 
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class GetGroupList implements Parcelable {
	protected int key_No, KEY_GROUP_STATUS, KEY_GROUP_INSOCKET, KEY_GROUP_OUTSOCKET = 0;
	protected String KEY_GROUP_NAME, KEY_GROUP_DEVICES, KEY_USER_ID = "";
	protected String KEY_GROUP_SLEEPTIME, KEY_GROUP_WAKEUPTIME, KEY_GROUP_SLEEPDAY, KEY_GROUP_WAKEUPDAY;

	public GetGroupList() { }
	public GetGroupList(Parcel in) {
		readFromParcel(in);
	}
	
	// 그룹 목록
	public GetGroupList(int key_No, String KEY_GROUP_NAME, String KEY_GROUP_DEVICES,
                        String KEY_GROUP_SLEEPTIME, String KEY_GROUP_WAKEUPTIME, String KEY_GROUP_SLEEPDAY, String KEY_GROUP_WAKEUPDAY,
                        int KEY_GROUP_STATUS, int KEY_GROUP_INSOCKET, int KEY_GROUP_OUTSOCKET, String KEY_USER_ID) {
		this.key_No = key_No;
		this.KEY_GROUP_NAME = KEY_GROUP_NAME;
		this.KEY_GROUP_DEVICES = KEY_GROUP_DEVICES;
		this.KEY_GROUP_STATUS = KEY_GROUP_STATUS;
		this.KEY_GROUP_SLEEPTIME = KEY_GROUP_SLEEPTIME;
		this.KEY_GROUP_WAKEUPTIME = KEY_GROUP_WAKEUPTIME;
		this.KEY_GROUP_SLEEPDAY = KEY_GROUP_SLEEPDAY;
		this.KEY_GROUP_WAKEUPDAY = KEY_GROUP_WAKEUPDAY;
		this.KEY_GROUP_INSOCKET = KEY_GROUP_INSOCKET;
		this.KEY_GROUP_OUTSOCKET = KEY_GROUP_OUTSOCKET;
		this.KEY_USER_ID = KEY_USER_ID;
	}
	
	// 그룹 추가
	public GetGroupList(String KEY_GROUP_NAME, String KEY_GROUP_DEVICES, int KEY_GROUP_STATUS,
                        String KEY_GROUP_SLEEPTIME, String KEY_GROUP_WAKEUPTIME, String KEY_GROUP_SLEEPDAY, String KEY_GROUP_WAKEUPDAY,
                        int KEY_GROUP_INSOCKET, int KEY_GROUP_OUTSOCKET, String KEY_USER_ID) {
		this.KEY_GROUP_NAME = KEY_GROUP_NAME;
		this.KEY_GROUP_DEVICES = KEY_GROUP_DEVICES;
		this.KEY_GROUP_STATUS = KEY_GROUP_STATUS;
		this.KEY_GROUP_SLEEPTIME = KEY_GROUP_SLEEPTIME;
		this.KEY_GROUP_WAKEUPTIME = KEY_GROUP_WAKEUPTIME;
		this.KEY_GROUP_SLEEPDAY = KEY_GROUP_SLEEPDAY;
		this.KEY_GROUP_WAKEUPDAY = KEY_GROUP_WAKEUPDAY;
		this.KEY_GROUP_INSOCKET = KEY_GROUP_INSOCKET;
		this.KEY_GROUP_OUTSOCKET = KEY_GROUP_OUTSOCKET;
		this.KEY_USER_ID = KEY_USER_ID;
	}
	
	// 그룹 편집
	public GetGroupList(String KEY_GROUP_NAME, int KEY_GROUP_STATUS,
                        String KEY_GROUP_SLEEPTIME, String KEY_GROUP_WAKEUPTIME, String KEY_GROUP_SLEEPDAY, String KEY_GROUP_WAKEUPDAY,
                        int KEY_GROUP_INSOCKET, int KEY_GROUP_OUTSOCKET, String KEY_USER_ID) {
		this.KEY_GROUP_NAME = KEY_GROUP_NAME;
		this.KEY_GROUP_STATUS = KEY_GROUP_STATUS;
		this.KEY_GROUP_SLEEPTIME = KEY_GROUP_SLEEPTIME;
		this.KEY_GROUP_WAKEUPTIME = KEY_GROUP_WAKEUPTIME;
		this.KEY_GROUP_SLEEPDAY = KEY_GROUP_SLEEPDAY;
		this.KEY_GROUP_WAKEUPDAY = KEY_GROUP_WAKEUPDAY;
		this.KEY_GROUP_INSOCKET = KEY_GROUP_INSOCKET;
		this.KEY_GROUP_OUTSOCKET = KEY_GROUP_OUTSOCKET;
		this.KEY_USER_ID = KEY_USER_ID;
	}
	
	// 그룹 변경
	public GetGroupList(String KEY_GROUP_NAME) {
		this.KEY_GROUP_NAME = KEY_GROUP_NAME;
	}

	public int getKeyNo(){
		return this.key_No;
	}
	public String getKeyGroupName(){
		return this.KEY_GROUP_NAME;
	}
	public int getKeyGroupStatus(){
		return this.KEY_GROUP_STATUS;
	}
	public String getKeyGroupDevices(){
		return this.KEY_GROUP_DEVICES;
	}
	public String getKeyGroupSleepTime(){
		return this.KEY_GROUP_SLEEPTIME;
	}
	public String getKeyGroupWakeupTime(){
		return this.KEY_GROUP_WAKEUPTIME;
	}
	public String getKeyGroupSleepDay(){
		return this.KEY_GROUP_SLEEPDAY;
	}
	public String getKeyGroupWakeupDay(){
		return this.KEY_GROUP_WAKEUPDAY;
	}
	public int getKeyGroupInSocket(){
		return this.KEY_GROUP_INSOCKET;
	}
	public int getKeyGroupOutSocket(){
		return this.KEY_GROUP_OUTSOCKET;
	}
	public String getKeyUserID(){
		return this.KEY_USER_ID;
	}
	
	public void setKeyNo(int key_No) {
		this.key_No = key_No;
	}
	public void setKeyGroupName(String KEY_GROUP_NAME) {
		this.KEY_GROUP_NAME = KEY_GROUP_NAME;
	}
	public void setKeyGroupStatus(int KEY_GROUP_STATUS) {
		this.KEY_GROUP_STATUS = KEY_GROUP_STATUS;
	}
	public void setKeyGroupDevices(String KEY_GROUP_DEVICES) {
		this.KEY_GROUP_DEVICES = KEY_GROUP_DEVICES;
	}
	public void setKeyGroupSleepTime(String KEY_GROUP_SLEEPTIME) {
		this.KEY_GROUP_SLEEPTIME = KEY_GROUP_SLEEPTIME;
	}
	public void setKeyGroupWakeupTime(String KEY_GROUP_WAKEUPTIME) {
		this.KEY_GROUP_WAKEUPTIME = KEY_GROUP_WAKEUPTIME;
	}
	public void setKeyGroupSleepDay(String KEY_GROUP_SLEEPDAY) {
		this.KEY_GROUP_SLEEPDAY = KEY_GROUP_SLEEPDAY;
	}
	public void setKeyGroupWakeupDay(String KEY_GROUP_WAKEUPDAY) {
		this.KEY_GROUP_WAKEUPDAY = KEY_GROUP_WAKEUPDAY;
	}
	public void setKeyGroupInSocket(int KEY_GROUP_INSOCKET) {
		this.KEY_GROUP_INSOCKET = KEY_GROUP_INSOCKET;
	}
	public void setKeyGroupOutSocket(int KEY_GROUP_OUTSOCKET) {
		this.KEY_GROUP_OUTSOCKET = KEY_GROUP_OUTSOCKET;
	}
	public void setKeyUserID(String KEY_USER_ID) {
		this.KEY_USER_ID = KEY_USER_ID;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(key_No);
		dest.writeString(KEY_GROUP_NAME);
		dest.writeString(KEY_GROUP_DEVICES);
		dest.writeInt(KEY_GROUP_STATUS);
		dest.writeString(KEY_GROUP_SLEEPTIME);
		dest.writeString(KEY_GROUP_WAKEUPTIME);
		dest.writeString(KEY_GROUP_SLEEPDAY);
		dest.writeString(KEY_GROUP_WAKEUPDAY);
		dest.writeInt(KEY_GROUP_INSOCKET);
		dest.writeInt(KEY_GROUP_OUTSOCKET);
		dest.writeString(KEY_USER_ID);
	}

	private void readFromParcel(Parcel in){
		key_No = in.readInt();
		KEY_GROUP_NAME = in.readString();
		KEY_GROUP_DEVICES = in.readString();
		KEY_GROUP_STATUS = in.readInt();
		KEY_GROUP_SLEEPTIME = in.readString();
		KEY_GROUP_WAKEUPTIME = in.readString();
		KEY_GROUP_SLEEPDAY = in.readString();
		KEY_GROUP_WAKEUPDAY = in.readString();
		KEY_GROUP_INSOCKET = in.readInt();
		KEY_GROUP_OUTSOCKET = in.readInt();
		KEY_USER_ID = in.readString();
	}
	
	public static final Creator CREATOR = new Creator() {
		public sleep.simdori.com.item.GetGroupList createFromParcel(Parcel in) {
			return new sleep.simdori.com.item.GetGroupList(in);
		}

		public sleep.simdori.com.item.GetGroupList[] newArray(int size) {
			return new sleep.simdori.com.item.GetGroupList[size];
		}
	};
}
