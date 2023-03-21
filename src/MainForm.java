import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Vector;

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
    private DefaultTableModel model;

    public LinkedList<RecIntegral> list = new LinkedList<>();

    private Double Calculate(Double upper, Double low, Double step)
    {
        Double result = 0.0;
        for(int i = 0; i < (upper - low)/step; i ++)
        {
            if (low + (i+1) * step < upper)
                result += 0.5 * step * (Math.sqrt(low + i * step) + Math.sqrt(low + (i+1) * step));
            else
                result += 0.5 * step * (Math.sqrt(low + i * step) + Math.sqrt(upper));
        }
        return result;
    }

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
                    if (Double.parseDouble(TextField_LowerLimit.getText()) > 1000000 ||
                            Double.parseDouble(TextField_LowerLimit.getText()) < 0.000001)
                    {
                        throw new MyException("Выход за пределы диапазона", "Нижний лимит");
                    }

                    if (Double.parseDouble(TextField_UpperLimit.getText()) > 1000000 ||
                            Double.parseDouble(TextField_UpperLimit.getText()) < 0.000001)
                    {
                        throw new MyException("Выход за пределы диапазона", "Верхний лимит");
                    }

                    if (Double.parseDouble(TextField_Step.getText()) > 1000000 ||
                            Double.parseDouble(TextField_Step.getText()) < 0.000001)
                    {
                        throw new MyException("Выход за пределы диапазона", "Шаг");
                    }

                    if (Double.parseDouble(TextField_Step.getText()) != 0)
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
                for (int i = 0; i < model.getRowCount(); i++)
                {
                    Vector data = model.getDataVector().get(i);

                    Double upper = (Double)data.get(0);
                    Double low = (Double)data.get(1);
                    Double step = (Double)data.get(2);

                    Double result = Calculate(upper, low, step);
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