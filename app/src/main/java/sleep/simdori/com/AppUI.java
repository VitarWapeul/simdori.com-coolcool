package sleep.simdori.com;

//import sleep.simdori.com.activity.GroupListActivity;
//import sleep.simdori.com.activity.GroupListActivity;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.activity.LoginActivity;
import sleep.simdori.com.activity.ModifyActivity;
import sleep.simdori.com.activity.RegisterActivity;
import sleep.simdori.com.activity.SplashActivity;

/**
 * 프로젝트에서 사용되는 액비티비를 호출한다.
 * 다른 액티비티에 있는 함수를 호출하기 위해 사용된다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class AppUI {
    private static SplashActivity splashActivity;
    private static LoginActivity loginActivity;
    private static RegisterActivity registerActivity;
    private static HomeActivity homeActivity;
//    private static GroupActivity groupActivity;
//    private static GroupEditActivity groupDeviceEditActivity;
//    private static DeviceActivity deviceActivity;
//    private static DeviceActivity2 deviceActivity2;
//    private static DevicePowerActivity devicePowerActivity;
//    private static DeviceSettingActivity deviceSettingActivity;
//    private static DeviceEnvironmentActivity deviceEnvironmentActivity;
//    private static DeviceEditActivity deviceEditActivity;
//    private static DeviceSocketEditActivity socketEditActivity;
//    private static DeviceSharingActivity deviceSharingActivity;
    private static ModifyActivity modifyActivity;
//    private static SleepActivity sleepActivity;
//    private static SleepDeviceActivity sleepDeviceActivity;
//    private static SleepGroupActivity sleepGroupActivity;
//    private static SleepGroupEditActivity sleepGroupEditActivity;
//    private static InformationActivity informationActivity;
//    private static NoticeActivity noticeActivity;
//    private static NoticeDetailActivity noticeDetailActivity;
//    private static MessageActivity messageActivity;
//    private static WebActivity webActivity;
//    private static WifiActivity wifiActivity;
//    private static WifiOverviewActivity overviewActivity;
//    private static WifiAdminActivity wifiAdminActivity;
//    private static WifiEditActivity wifiEditActivity;
//    private static WifiRepeaterActivity wifiRepeaterActivity;
//    private static WifiRepeaterActivity2 wifiRepeaterActivity2;
//    private static WifiAPModeActivity wifiAPModeActivity;
//    private static WifiFWupdateActivity wifiFWupdateAtivity;
//    private static PopUpActivity popUpActivity;
//    private static FirstPageActivity firstPageActivity;
//    private static GroupListActivity groupListActivity;

//    // 시작 화면
//    public static FirstPageActivity getFirstPageActivity() {
//        return AppUI.firstPageActivity;
//    }
//    public static void setFirstPageActivity(FirstPageActivity firstPageActivity) {
//        AppUI.firstPageActivity = firstPageActivity;
//    }
//
//    // 시작 화면
//    public static PopUpActivity getPopUpActivity() {
//        return AppUI.popUpActivity;
//    }
//    public static void setPopUpActivity(PopUpActivity popUpActivity) {
//        AppUI.popUpActivity = popUpActivity;
//    }
//
    // 시작 화면
    public static SplashActivity getSplashActivity() {
        return AppUI.splashActivity;
    }
    public static void setSplashActivity(SplashActivity splashActivity) {
        AppUI.splashActivity = splashActivity;
    }

    // 로그인 화면
    public static LoginActivity getLoginActivity() {
        return AppUI.loginActivity;
    }
    public static void setLoginActivity(LoginActivity loginActivity) {
        AppUI.loginActivity = loginActivity;
    }

    // 회원 가입
    public static RegisterActivity getRegisterActivity() {
        return sleep.simdori.com.AppUI.registerActivity;
    }
    public static void setRegisterActivity(RegisterActivity registerActivity) {
        sleep.simdori.com.AppUI.registerActivity = registerActivity;
    }

    // 기본 화면(Device)
    public static HomeActivity getHomeActivity() {
        return sleep.simdori.com.AppUI.homeActivity;
    }
    public static void setHomeActivity(HomeActivity homeActivity) {
        sleep.simdori.com.AppUI.homeActivity = homeActivity;
    }

//    // 그룹 화면(Group)
//    public static GroupActivity getGroupActivity() {
//        return AppUI.groupActivity;
//    }
//    public static void setGroupActivity(GroupActivity groupActivity) {
//        AppUI.groupActivity = groupActivity;
//    }

//    // 그룹 내 장치 편집
//    public static GroupEditActivity getGroupDeviceEditActivity() {
//        return AppUI.groupDeviceEditActivity;
//    }
//    public static void setGroupDeviceEditActivity(GroupEditActivity groupDeviceEditActivity) {
//        AppUI.groupDeviceEditActivity = groupDeviceEditActivity;
//    }
//
//    // 장치 제어(Socket)
//    public static DeviceActivity getDeviceActivity() {
//        return AppUI.deviceActivity;
//    }
//    public static void setDeviceActivity(DeviceActivity deviceActivity) {
//        AppUI.deviceActivity = deviceActivity;
//    }
//
//    // 장치 제어(Socket - LuCI)
//    public static DeviceActivity2 getDeviceActivity2() {
//        return AppUI.deviceActivity2;
//    }
//    public static void setDeviceActivity2(DeviceActivity2 deviceActivity2) {
//        AppUI.deviceActivity2 = deviceActivity2;
//    }
//
//    // 전력 사용량
//    public static DevicePowerActivity getDevicePowerActivity() {
//        return AppUI.devicePowerActivity;
//    }
//    public static void setDevicePowerActivity(DevicePowerActivity devicePowerActivity) {
//        AppUI.devicePowerActivity = devicePowerActivity;
//    }
//
//    // 장치 설정
//    public static DeviceSettingActivity getDeviceSettingActivity() {
//        return AppUI.deviceSettingActivity;
//    }
//    public static void setDeviceSettingActivity(DeviceSettingActivity deviceSettingActivity) {
//        AppUI.deviceSettingActivity = deviceSettingActivity;
//    }
//
//    // 실내 환경
//    public static DeviceEnvironmentActivity getDeviceEnvironmentActivity() {
//        return AppUI.deviceEnvironmentActivity;
//    }
//    public static void setDeviceEnvironmentActivity(DeviceEnvironmentActivity deviceEnvironmentActivity) {
//        AppUI.deviceEnvironmentActivity = deviceEnvironmentActivity;
//    }
//
//
//    // 소켓 편집
//    public static DeviceSocketEditActivity getSocketEditActivity() {
//        return AppUI.socketEditActivity;
//    }
//    public static void setSocketEditActivity(DeviceSocketEditActivity socketEditActivity) {
//        AppUI.socketEditActivity = socketEditActivity;
//    }
//
//    // 장치 편집
//    public static DeviceEditActivity getDeviceEditActivity() {
//        return AppUI.deviceEditActivity;
//    }
//    public static void setDeviceEditActivity(DeviceEditActivity deviceEditActivity) {
//        AppUI.deviceEditActivity = deviceEditActivity;
//    }
//
//    // 장치 공유
//    public static DeviceSharingActivity getDeviceSharingActivity() {
//        return AppUI.deviceSharingActivity;
//    }
//    public static void setDeviceSharingActivity(DeviceSharingActivity deviceSharingActivity) {
//        AppUI.deviceSharingActivity = deviceSharingActivity;
//    }
//
    // 개인정보 수정
    public static ModifyActivity getModifyActivity() {
        return AppUI.modifyActivity;
    }
    public static void setModifyActivity(ModifyActivity modifyActivity) {
        AppUI.modifyActivity = modifyActivity;
    }
//
//    // 고객센터
//    public static InformationActivity getInformationActivity() {
//        return AppUI.informationActivity;
//    }
//    public static void setInformationActivity(InformationActivity informationActivity) {
//        AppUI.informationActivity = informationActivity;
//    }
//
//    // 공지사항
//    public static NoticeActivity getNoticeActivity() {
//        return AppUI.noticeActivity;
//    }
//    public static void setNoticeActivity(NoticeActivity noticeActivity) {
//        AppUI.noticeActivity = noticeActivity;
//    }
//
//    // 공지사항
//    public static NoticeDetailActivity getNoticeDetailActivity() {
//        return AppUI.noticeDetailActivity;
//    }
//    public static void setNoticeDetailActivity(NoticeDetailActivity noticeDetailActivity) {
//        AppUI.noticeDetailActivity = noticeDetailActivity;
//    }
//
//    // 알림함
//    public static MessageActivity getMessageActivity() {
//        return AppUI.messageActivity;
//    }
//    public static void setMessageActivity(MessageActivity messageActivity) {
//        AppUI.messageActivity = messageActivity;
//    }
//
//    // 예약설정
//    public static SleepActivity getSleepActivity() {
//        return AppUI.sleepActivity;
//    }
//    public static void setSleepActivity(SleepActivity sleepActivity) {
//        AppUI.sleepActivity = sleepActivity;
//    }
//
//    // 예약설정
//    public static SleepGroupActivity getSleepGroupActivity() {
//        return AppUI.sleepGroupActivity;
//    }
//    public static void setSleepGroupActivity(SleepGroupActivity sleepGroupActivity) {
//        AppUI.sleepGroupActivity = sleepGroupActivity;
//    }
//
//    // 장치 제어(Socket)
//    public static SleepDeviceActivity getSleepDeviceActivity() {
//        return AppUI.sleepDeviceActivity;
//    }
//    public static void setSleepDeviceActivity(SleepDeviceActivity sleepDeviceActivity) {
//        AppUI.sleepDeviceActivity = sleepDeviceActivity;
//    }
//
//    // 그룹 내 장치 편집
//    public static SleepGroupEditActivity getSleepGroupEditActivity() {
//        return AppUI.sleepGroupEditActivity;
//    }
//    public static void setSleepGroupEditActivity(SleepGroupEditActivity sleepGroupEditActivity) {
//        AppUI.sleepGroupEditActivity = sleepGroupEditActivity;
//    }
//
//    // 웹뷰 페이지
//    public static WebActivity getWebActivity() {
//        return AppUI.webActivity;
//    }
//    public static void setWebActivity(WebActivity webActivity) {
//        AppUI.webActivity = webActivity;
//    }
//
//    // 공유기 설정
//    public static WifiActivity getWifiActivity() {
//        return AppUI.wifiActivity;
//    }
//    public static void setWifiActivity(WifiActivity wifiActivity) {
//        AppUI.wifiActivity = wifiActivity;
//    }
//
//    // LuCI Overview 페이지
//    public static WifiOverviewActivity getWifiOverviewActivity() {
//        return AppUI.overviewActivity;
//    }
//    public static void setOverviewActivity(WifiOverviewActivity overviewActivity) {
//        AppUI.overviewActivity = overviewActivity;
//    }
//
//    // LuCI Admin 페이지
//    public static WifiAdminActivity getWifiAdminActivity() {
//        return AppUI.wifiAdminActivity;
//    }
//    public static void setWifiAdminActivity(WifiAdminActivity wifiAdminActivity) {
//        AppUI.wifiAdminActivity = wifiAdminActivity;
//    }
//
//    // LuCI Wifi Edit 페이지
//    public static WifiEditActivity getWifiEditActivity() {
//        return AppUI.wifiEditActivity;
//    }
//    public static void setWifiEditActivity(WifiEditActivity wifiEditActivity) {
//        AppUI.wifiEditActivity = wifiEditActivity;
//    }
//
//    // LuCI Repeater Mode 페이지
//    public static WifiRepeaterActivity getWifiRepeaterActivity() {
//        return AppUI.wifiRepeaterActivity;
//    }
//    public static void setWifiRepeaterActivity(WifiRepeaterActivity wifiRepeaterActivity) {
//        AppUI.wifiRepeaterActivity = wifiRepeaterActivity;
//    }
//
//    // LuCI Repeater Mode 페이지
//    public static WifiRepeaterActivity2 getWifiRepeaterActivity2() {
//        return AppUI.wifiRepeaterActivity2;
//    }
//    public static void setWifiRepeaterActivity2(WifiRepeaterActivity2 wifiRepeaterActivity2) {
//        AppUI.wifiRepeaterActivity2 = wifiRepeaterActivity2;
//    }
//
//    // LuCI APMode 페이지
//    public static WifiAPModeActivity getWifiAPModeActivity() {
//        return AppUI.wifiAPModeActivity;
//    }
//    public static void setWifiAPModeActivity(WifiAPModeActivity wifiAPModeActivity) {
//        AppUI.wifiAPModeActivity = wifiAPModeActivity;
//    }
//
//    // LuCI FW Update 페이지
//    public static WifiFWupdateActivity getWifiFWupdateActivity() {
//        return AppUI.wifiFWupdateAtivity;
//    }
//    public static void setWifiFWupdateActivity(WifiFWupdateActivity wifiFWupdateAtivity) {
//        AppUI.wifiFWupdateAtivity = wifiFWupdateAtivity;
//    }
//
//    public static void setGroupListActivity(GroupListActivity groupListActivity){
//        AppUI.groupListActivity = groupListActivity;
//    }
//
//    public static GroupListActivity getGroupListActivity(){
//        return AppUI.groupListActivity;
//    }
}