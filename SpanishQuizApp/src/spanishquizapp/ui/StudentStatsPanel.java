package spanishquizapp.ui;

import spanishquizapp.StatsEntry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

public class StudentStatsPanel extends JPanel {

    public interface StatsListener {
        void onBack();
    }

    private final StatsListener listener;
    private final DefaultTableModel model;
    private final JTable table;

    public StudentStatsPanel(StatsListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"Categoría", "Intentos", "Correctas", "Precisión %", "Progreso"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.getColumnModel().getColumn(4).setCellRenderer(new ProgressRenderer());

        add(new JLabel("Desempeño por categoría"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> listener.onBack());
        add(backButton, BorderLayout.SOUTH);
    }

    public void showStats(List<StatsEntry> entries) {
        model.setRowCount(0);
        for (StatsEntry entry : entries) {
            double accuracy = entry.getAccuracy() * 100;
            model.addRow(new Object[]{
                    entry.getTopic() + " > " + entry.getSubtopic(),
                    entry.getAttempts(),
                    entry.getCorrect(),
                    String.format("%.1f", accuracy),
                    (int) Math.round(accuracy)
            });
        }
    }

    private static class ProgressRenderer extends javax.swing.JProgressBar implements TableCellRenderer {
        public ProgressRenderer() {
            super(0, 100);
            setStringPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int percent = value instanceof Number ? ((Number) value).intValue() : 0;
            setValue(percent);
            setString(percent + "%");
            return this;
        }
    }
}
