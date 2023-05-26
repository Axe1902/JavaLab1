import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MainForm extends JFrame {
    private JTextField TextField_UpperLimit;
    private JTextField TextField_LowerLimit;
    private JPanel WorkPlace;
    private JButton addButton;
    private JButton deleteButton;
    private JTable table1;
    private JButton calculateButton;
    private JLabel JLabel_UpperLimit;
    private JLabel JLabel_LowerLimit;
    private JLabel JLabel_Step;
    private JTextField TextField_Step;
    private JButton fillButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton serializationButton;
    private JButton deserializationButton;
    private DefaultTableModel model;

    public LinkedList<RecIntegral> list = new LinkedList<>();



    public MainForm(){
        model = (DefaultTableModel)table1.getModel();
        model.addColumn("Верхний предел");
        model.addColumn("Нижний предел");
        model.addColumn("Шаг");
        model.addColumn("Результат");
        table1.setModel(model);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)  {

                try {
                    if (Double.parseDouble(TextField_UpperLimit.getText()) > 1000000 ||
                            Double.parseDouble(TextField_UpperLimit.getText()) < 0.000001)
                    {
                        throw new MyException("Выход за пределы диапазона", "Верхний лимит");
                    }

                    if (Double.parseDouble(TextField_LowerLimit.getText()) > 1000000 ||
                            Double.parseDouble(TextField_LowerLimit.getText()) < 0.000001)
                    {
                        throw new MyException("Выход за пределы диапазона", "Нижний лимит");
                    }

                    if (Double.parseDouble(TextField_Step.getText()) > 1000000 ||
                            Double.parseDouble(TextField_Step.getText()) < 0.000001)
                    {
                        throw new MyException("Выход за пределы диапазона", "Шаг");
                    }

                    if (Double.parseDouble(TextField_Step.getText()) == 0)
                    {
                        throw new MyException("Шаг не может быть равен нулю", "Шаг");
                    }
                } catch (MyException ex)
                {
                    ex.ShowMessage(WorkPlace);
                    return;
                }

                model.addRow(new Object[]{
                        Double.parseDouble(TextField_UpperLimit.getText()),
                        Double.parseDouble(TextField_LowerLimit.getText()),
                        Double.parseDouble(TextField_Step.getText()),
                        0
                });
                list.add(new RecIntegral(Double.parseDouble(TextField_UpperLimit.getText()),
                        Double.parseDouble(TextField_LowerLimit.getText()),
                        Double.parseDouble(TextField_Step.getText()),
                        0.0));

                TextField_UpperLimit.setText("");
                TextField_LowerLimit.setText("");
                TextField_Step.setText("");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table1.getSelectedRow() != -1){
                    list.remove(table1.getSelectedRow());
                    model.removeRow(table1.getSelectedRow());
                }
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 6; i++) //просчёт первых 6-и строк шестью потоками
                {
                    Vector data = model.getDataVector().get(i);

                    Double upper = (Double)data.get(0);
                    Double low = (Double)data.get(1);
                    Double step = (Double)data.get(2);
                    Double result;


                    Callable<Double> thread = new MyThread(low, upper, step);
                    FutureTask futureTask = new FutureTask(thread);
                    new Thread(futureTask).start();

                    try {
                        result = (double) futureTask.get();
                        System.out.println("Поток " + i + " Результат: " + result);

                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }

                    data.set(3, result);
                    list.get(i).res = result;
                }

                table1.repaint();
            }
        });

        setContentPane(WorkPlace);
        setSize(1000, 600);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        fillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (RecIntegral memList :
                        list) {
                    model.addRow(new Object[]{
                            memList.upLim,
                            memList.lowLim,
                            memList.st,
                            memList.res
                    });
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int modelSize = model.getRowCount() - 1;
                for (int i = modelSize; i >= 0; i--)
                {
                    model.removeRow(i);
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("Data");
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(file);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                for (RecIntegral itVar : list)
                {
                    pw.print(itVar.upLim.toString() + " ");
                    pw.print(itVar.lowLim.toString() + " ");
                    pw.print(itVar.st.toString() + " ");
                    pw.print(itVar.res.toString() + " ");
                    pw.print("\n");
                }
                pw.close();

            }
        });
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser window = new JFileChooser(); //Создание JFileChooser с указанием директории пользователя по умолчанию
                window.setCurrentDirectory(new File(".")); //Установка директории по умолчанию
                window.setDialogTitle("Загрузка из файла"); //Заголовок окна
                window.setMultiSelectionEnabled(false); //Невозможность выбора сразу нескольких файлов
                window.setFileSelectionMode(JFileChooser.FILES_ONLY); //Выбор только файлов
                window.setSelectedFile(new File("Data")); //Выбор файла по умолчанию
                window.showDialog(window, "Загрузить из файла"); //Открытие окна выбора файла с настроенным наименованием кнопки

                File MyFile = window.getSelectedFile(); //Чтение выделенного файла

                int modelSize = model.getRowCount() - 1;
                for (int i = modelSize; i >= 0; i--)
                {
                    model.removeRow(i);
                }

                try {
                    FileReader myReader = new FileReader(MyFile); //Создание объекта класса FileReader для чтения из файла на основе имени файла в файловой системе
                    BufferedReader buf = new BufferedReader(myReader); //BufferedReader записывает текст в поток вывода символов, буферизуя символы, чтобы обеспечить эффективную запись отдельных символов, массивов и строк

                    Object[] lines = buf.lines().toArray(); //Метод lines() — метод, который возвращает поток строк, извлеченных из заданной многострочной строки

                    for (int i = 0; i < lines.length; i++) {
                        String[] row = lines[i].toString().split(" "); //Метод split разделяет строку на подстроки, используя разделитель, который определяется с помощью регулярного выражения
                        model.addRow(new Object[]{Double.parseDouble(row[0]), Double.parseDouble(row[1]), Double.parseDouble(row[2]), Double.parseDouble(row[3])});
                    }

                    //Поток закрыт
                    buf.close();
                    myReader.close();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        serializationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("RecInt.dat"));
                    oos.writeObject(list);
                    oos.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        deserializationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser window = new JFileChooser(); //Создание JFileChooser с указанием директории пользователя по умолчанию
                window.setCurrentDirectory(new File(".")); //Установка директории по умолчанию
                window.setDialogTitle("Загрузка из файла"); //Заголовок окна
                window.setMultiSelectionEnabled(false); //Невозможность выбора сразу нескольких файлов
                window.setFileSelectionMode(JFileChooser.FILES_ONLY); //Выбор только файлов
                window.setSelectedFile(new File("RecInt.dat")); //Выбор файла по умолчанию
                window.showDialog(window, "Загрузить из файла"); //Открытие окна выбора файла с настроенным наименованием кнопки

                File MyFile = window.getSelectedFile(); //Чтение выделенного файла

                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MyFile.getName()))) {
                    list.clear();
                    list = ((LinkedList<RecIntegral>)ois.readObject());//бла бла бла
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    private void createUIComponents() {
        table1 = new JTable() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col != 3);
            }
        };
    }

};
