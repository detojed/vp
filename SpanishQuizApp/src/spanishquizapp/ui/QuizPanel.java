package spanishquizapp.ui;

import spanishquizapp.Question;
import spanishquizapp.QuizSession;
import spanishquizapp.StatsManager;
import spanishquizapp.User;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class QuizPanel extends JPanel {

    public interface QuizListener {
        void onExitQuiz();
    }

    private final QuizListener listener;

    private User user;
    private QuizSession session;
    private StatsManager statsManager;

    private final JLabel headerLabel = new JLabel();
    private final JLabel difficultyLabel = new JLabel();
    private final JTextArea promptArea = new JTextArea(4, 40);
    private final JRadioButton[] optionButtons = new JRadioButton[4];
    private final ButtonGroup group = new ButtonGroup();
    private final JLabel feedbackLabel = new JLabel(" ");
    private final JButton submitButton = new JButton("Responder");
    private final JButton nextButton = new JButton("Siguiente");

    private Question currentQuestion;

    public QuizPanel(QuizListener listener) {
        this.listener = listener;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(headerLabel);
        top.add(difficultyLabel);
        add(top, BorderLayout.NORTH);

        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setEditable(false);
        promptArea.setBorder(BorderFactory.createTitledBorder("Pregunta"));
        add(new JScrollPane(promptArea), BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i] = new JRadioButton();
            group.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Opciones"));
        add(optionsPanel, BorderLayout.EAST);

        JPanel bottom = new JPanel(new GridLayout(3, 1));
        bottom.add(feedbackLabel);
        JPanel buttonsPanel = new JPanel();
        JButton backButton = new JButton("Volver");
        buttonsPanel.add(submitButton);
        buttonsPanel.add(nextButton);
        buttonsPanel.add(backButton);
        bottom.add(buttonsPanel);
        add(bottom, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> submitAnswer());
        nextButton.addActionListener(e -> loadQuestion());
        backButton.addActionListener(e -> listener.onExitQuiz());

        nextButton.setEnabled(false);
    }

    public void startSession(User user, QuizSession session, StatsManager statsManager) {
        this.user = user;
        this.session = session;
        this.statsManager = statsManager;
        headerLabel.setText("Tema: " + session.getTopic() + " > " + session.getSubtopic());
        loadQuestion();
    }

    private void loadQuestion() {
        if (session == null) {
            return;
        }
        currentQuestion = session.nextQuestion();
        if (currentQuestion == null) {
            JOptionPane.showMessageDialog(this, "No hay más preguntas disponibles.", "Fin", JOptionPane.INFORMATION_MESSAGE);
            listener.onExitQuiz();
            return;
        }
        promptArea.setText(currentQuestion.getPrompt());
        group.clearSelection();
        for (int i = 0; i < optionButtons.length; i++) {
            if (i < currentQuestion.getOptions().size()) {
                optionButtons[i].setText(currentQuestion.getOptions().get(i));
                optionButtons[i].setVisible(true);
            } else {
                optionButtons[i].setVisible(false);
            }
            optionButtons[i].setSelected(false);
            optionButtons[i].setEnabled(true);
        }
        feedbackLabel.setText(" ");
        submitButton.setEnabled(true);
        nextButton.setEnabled(false);
        difficultyLabel.setText("Dificultad actual: " + session.getCurrentDifficulty().name());
    }

    private void submitAnswer() {
        if (currentQuestion == null || session == null || statsManager == null) {
            return;
        }
        int selectedIndex = -1;
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isVisible() && optionButtons[i].isSelected()) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una respuesta.", "Respuesta requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean correct = currentQuestion.isCorrect(selectedIndex);
        statsManager.recordAttempt(user, currentQuestion, correct);
        session.recordAnswer(correct);
        feedbackLabel.setText(correct ? "¡Correcto!" : "Incorrecto. Respuesta correcta: " + currentQuestion.getOptions().get(currentQuestion.getCorrectIndex()));
        for (JRadioButton button : optionButtons) {
            button.setEnabled(false);
        }
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
        difficultyLabel.setText("Dificultad actual: " + session.getCurrentDifficulty().name());
    }
}
