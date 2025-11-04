package spanishquizapp.ui;

import spanishquizapp.DataStore;
import spanishquizapp.Question;
import spanishquizapp.User;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Set;

public class StudentHomePanel extends JPanel {

    public interface HomeListener {
        void onStartQuiz(String topic, String subtopic);
        void onViewStats();
        void onLogout();
    }

    private final DataStore dataStore;
    private final HomeListener listener;

    private final JLabel welcomeLabel = new JLabel();
    private final JComboBox<String> topicCombo = new JComboBox<>();
    private final JComboBox<String> subtopicCombo = new JComboBox<>();

    public StudentHomePanel(DataStore dataStore, HomeListener listener) {
        this.dataStore = dataStore;
        this.listener = listener;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(welcomeLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        center.add(new JLabel("Tema"), gbc);
        gbc.gridx = 1;
        center.add(topicCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        center.add(new JLabel("Subtema"), gbc);
        gbc.gridx = 1;
        center.add(subtopicCombo, gbc);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Iniciar Quiz");
        JButton statsButton = new JButton("Mis Estadísticas");
        JButton logoutButton = new JButton("Cerrar sesión");
        bottom.add(startButton);
        bottom.add(statsButton);
        bottom.add(logoutButton);
        add(bottom, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startQuiz());
        statsButton.addActionListener(e -> listener.onViewStats());
        logoutButton.addActionListener(e -> listener.onLogout());

        topicCombo.addActionListener(e -> updateSubtopics());
    }

    public void setStudent(User user) {
        welcomeLabel.setText("Hola, " + user.getUsername() + "!");
        refreshTopics();
    }

    private void refreshTopics() {
        topicCombo.removeAllItems();
        Set<String> topics = dataStore.getTopics();
        for (String topic : topics) {
            topicCombo.addItem(topic);
        }
        subtopicCombo.removeAllItems();
    }

    private void updateSubtopics() {
        Object selectedTopic = topicCombo.getSelectedItem();
        subtopicCombo.removeAllItems();
        if (selectedTopic == null) {
            return;
        }
        Set<String> subtopics = dataStore.getSubtopics(selectedTopic.toString());
        for (String subtopic : subtopics) {
            subtopicCombo.addItem(subtopic);
        }
    }

    private void startQuiz() {
        Object topic = topicCombo.getSelectedItem();
        Object subtopic = subtopicCombo.getSelectedItem();
        if (topic == null || subtopic == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un tema y un subtema.", "Selección incompleta", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Question> available = dataStore.getQuestionsFor(topic.toString(), subtopic.toString());
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay preguntas para esta categoría.", "Sin preguntas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        listener.onStartQuiz(topic.toString(), subtopic.toString());
    }
}
