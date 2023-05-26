import java.util.concurrent.Callable;

public class MyThread implements Callable<Double> {
    public Double upper;
    public Double low;
    public Double step;
    public Double result = 0.0;

    public MyThread(double low, double upper, double step) {
        this.low = low;
        this.upper = upper;
        this.step = step;

    }

    public synchronized Double call() {

        {
            for (int i = 0; i < (upper - low) / step; i++) {
                if (low + (i + 1) * step < upper)
                    result += 0.5 * step * (Math.sqrt(low + i * step) + Math.sqrt(low + (i + 1) * step));
                else
                    result += 0.5 * step * (Math.sqrt(low + i * step) + Math.sqrt(upper));
            }
            return result;
        }


    }

}
