package sleep.simdori.com.DB;

public class HeartbeatDB {
    private double greenavg;
    private double redavg;
    private int bpm;

    public int getBpm() { return bpm; }

    public double getGreenavg() { return greenavg; }

    public double getRedavg() { return redavg;   }

    public void setGreenavg(double greenavg) { this.greenavg = greenavg; }

    public void setRedavg(double redavg) { this.redavg = redavg;  }

    public void setBpm(int bpm) { this.bpm = bpm; }
}
