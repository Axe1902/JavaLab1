import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    private DefaultTableModel model;

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
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{
                        Double.parseDouble(TextField_UpperLimit.getText()),
                        Double.parseDouble(TextField_LowerLimit.getText()),
                        Double.parseDouble(TextField_Step.getText()),
                        0
                });

                TextField_UpperLimit.setText("");
                TextField_LowerLimit.setText("");
                TextField_Step.setText("");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table1.getSelectedRow() != -1){
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
                }

                table1.repaint();
            }
        });

        setContentPane(WorkPlace);
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createUIComponents() {
        table1 = new JTable() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col != 3);
            }
        };
    }
}