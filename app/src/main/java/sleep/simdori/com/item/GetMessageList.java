package sleep.simdori.com.item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 장치 정보를 저장하여 OBJECT로 반환한다. 
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class GetMessageList implements Parcelable {
	protected int key_No, key_SharingPopup = 0;
	protected String key_SharingID, key_SharedID, key_SharingMAC, key_UserID = "";

	public GetMessageList() { }
	public GetMessageList(Parcel in) {
		readFromParcel(in);
	}
	
	// 메시지박스 목록
	public GetMessageList(int key_No, String key_SharingID, String key_SharedID, String key_SharingMAC, int key_SharingPopup, String key_UserID) {
		this.key_No = key_No;
		this.key_SharingID = key_SharingID;
		this.key_SharedID = key_SharedID;
		this.key_SharingMAC = key_SharingMAC;
		this.key_SharingPopup = key_SharingPopup;
		this.key_UserID = key_UserID;
	}
	
	// 메시지박스 추가
	public GetMessageList(String key_SharingID, String key_SharedID, String key_SharingMAC, int key_SharingPopup, String key_UserID) {
		this.key_SharingID = key_SharingID;
		this.key_SharedID = key_SharedID;
		this.key_SharingMAC = key_SharingMAC;
		this.key_SharingPopup = key_SharingPopup;
		this.key_UserID = key_UserID;
	}
		
	public int getKeyNo(){
		return this.key_No;
	}
	public String getKeySharingID(){
		return this.key_SharingID;
	}
	public String getKeySharedID(){
		return this.key_SharedID;
	}
	public String getKeySharingMAC(){
		return this.key_SharingMAC;
	}
	public int getKeySharingPopup(){
		return this.key_SharingPopup;
	}
	public String getKeyUserID(){
		return this.key_UserID;
	}
	
	public void setKeyNo(int key_No) {
		this.key_No = key_No;
	}
	public void setKeySharingID(String key_SharingID) {
		this.key_SharingID = key_SharingID;
	}
	public void setKeySharedID(String key_SharedID) {
		this.key_SharedID = key_SharedID;
	}
	public void setKeySharingMAC(String key_SharingMAC) {
		this.key_SharingMAC = key_SharingMAC;
	}
	public void setKeySharingPopup(int key_SharingPopup) {
		this.key_SharingPopup = key_SharingPopup;
	}
	public void setKeyUserID(String key_UserID) {
		this.key_UserID = key_UserID;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(key_No);
		dest.writeString(key_SharingID);
		dest.writeString(key_SharedID);
		dest.writeString(key_SharingMAC);
		dest.writeInt(key_SharingPopup);
		dest.writeString(key_UserID);
	}

	private void readFromParcel(Parcel in){
		key_No = in.readInt();
		key_SharingID = in.readString();
		key_SharedID = in.readString();
		key_SharingMAC = in.readString();
		key_SharingPopup = in.readInt();
		key_UserID = in.readString();
	}
	
	public static final Creator CREATOR = new Creator() {
		public sleep.simdori.com.item.GetMessageList createFromParcel(Parcel in) {
			return new sleep.simdori.com.item.GetMessageList(in);
		}

		public sleep.simdori.com.item.GetMessageList[] newArray(int size) {
			return new sleep.simdori.com.item.GetMessageList[size];
		}
	};
}
