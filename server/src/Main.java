import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Main {
//    public Double down;
//    private Double up;
//    public Double step;
//    public Double result;

    public static void main(String[] args) throws IOException {

        //Double[] result = new Double[6];
        ServerSocket server = new ServerSocket(8081);

        Socket client = server.accept();

        DataInputStream input_ = new DataInputStream(client.getInputStream());
        DataOutputStream output_ = new DataOutputStream(client.getOutputStream());

        for (int i = 0; i < 6; i++) {
            Double down = input_.readDouble();
            System.out.println(down);
            Double up = input_.readDouble();
            System.out.println(up);
            Double step = input_.readDouble();
            System.out.println(step);
            Double result;

            Callable<Double> calc = new MainThread(down, up, step);
            FutureTask futureTask = new FutureTask(calc);
            new Thread(futureTask).start();


            try {
                result = (Double)futureTask.get();
                System.out.println(result);

            } catch (ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            output_.writeDouble(result);

        }

        server.close();
        //Scanner input = new Scanner(client.getInputStream());
        //PrintWriter output = new PrintWriter(client.getOutputStream());

        //String TableRow = input.nextLine();

//        String[] tmp = TableRow.split(",");
//        for(String a : tmp)
//            System.out.println(Arrays.toString(a.getBytes()));
//        System.out.println(tmp);
//
//        for (int i = 0; i < 6; i++) {
//            Double[] numbers = new Double[3];
//
//            for (int j = 0; j < 3; j++)
//            {
//                numbers[j] = Double.parseDouble(tmp[i*j + j]);
//            }
//
//            Callable<Double> thread = new MainThread(numbers[0], numbers[1], numbers[2]);
//            FutureTask futureTask = new FutureTask(thread);
//            new Thread(futureTask).start();
//
//            try {
//                result[i] = (double)futureTask.get();
//
//            } catch (ExecutionException | InterruptedException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
//
//        String res = new String();
//
//        for (int j = 0; j < 6; j++)
//        {
//            res += result[j].toString();
//            if (j != 5)
//                res += ',';
//        }
//        output.println(res);
//        System.out.println(res);
    }
}