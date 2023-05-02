import java.io.Serializable;

public class RecIntegral implements Serializable {
    public Double upLim;
    public Double lowLim;
    public Double st;
    public Double res;
    public int Size = 4;

    public RecIntegral(Double upLim, Double lowLim, Double st, Double res) {
        this.upLim = upLim;
        this.lowLim = lowLim;
        this.st = st;
        this.res = res;
    }
}
