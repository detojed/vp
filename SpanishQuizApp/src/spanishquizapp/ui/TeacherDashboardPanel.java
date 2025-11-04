package spanishquizapp.ui;

import spanishquizapp.StatsEntry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public class TeacherDashboardPanel extends JPanel {

    public interface TeacherListener {
        void onRefresh();
        void onLogout();
    }

    private final TeacherListener listener;
    private final DefaultTableModel model;

    public TeacherDashboardPanel(TeacherListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"Alumno", "Categoría", "Intentos", "Correctas", "Precisión %", "Bandera"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);

        add(new JLabel("Resumen de alumnos"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton refreshButton = new JButton("Actualizar");
        JButton logoutButton = new JButton("Cerrar sesión");
        bottom.add(refreshButton);
        bottom.add(logoutButton);
        add(bottom, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> listener.onRefresh());
        logoutButton.addActionListener(e -> listener.onLogout());
    }

    public void showData(List<StatsEntry> entries) {
        model.setRowCount(0);
        for (StatsEntry entry : entries) {
            double accuracy = entry.getAccuracy() * 100;
            String flag = accuracy < 60 ? "⚠ Necesita refuerzo" : "";
            model.addRow(new Object[]{
                    entry.getUsername(),
                    entry.getTopic() + " > " + entry.getSubtopic(),
                    entry.getAttempts(),
                    entry.getCorrect(),
                    String.format("%.1f", accuracy),
                    flag
            });
        }
    }
}
