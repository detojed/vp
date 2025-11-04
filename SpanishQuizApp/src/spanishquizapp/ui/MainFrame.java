package spanishquizapp.ui;

import spanishquizapp.DataStore;
import spanishquizapp.Question;
import spanishquizapp.QuizSession;
import spanishquizapp.Role;
import spanishquizapp.StatsManager;
import spanishquizapp.User;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.List;

public class MainFrame extends JFrame implements
        LoginPanel.LoginSuccessListener,
        StudentHomePanel.HomeListener,
        QuizPanel.QuizListener,
        StudentStatsPanel.StatsListener,
        TeacherDashboardPanel.TeacherListener {

    private final DataStore dataStore;
    private final StatsManager statsManager;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final LoginPanel loginPanel;
    private final StudentHomePanel studentHomePanel;
    private final QuizPanel quizPanel;
    private final StudentStatsPanel studentStatsPanel;
    private final TeacherDashboardPanel teacherDashboardPanel;

    private User currentUser;
    private QuizSession currentSession;

    public MainFrame(DataStore dataStore, StatsManager statsManager) {
        super("Spanish Quiz App");
        this.dataStore = dataStore;
        this.statsManager = statsManager;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        loginPanel = new LoginPanel(dataStore, this);
        studentHomePanel = new StudentHomePanel(dataStore, this);
        quizPanel = new QuizPanel(this);
        studentStatsPanel = new StudentStatsPanel(this);
        teacherDashboardPanel = new TeacherDashboardPanel(this);

        cards.add(loginPanel, "login");
        cards.add(studentHomePanel, "studentHome");
        cards.add(quizPanel, "quiz");
        cards.add(studentStatsPanel, "studentStats");
        cards.add(teacherDashboardPanel, "teacher");

        add(cards);
        showLogin();
    }

    private void showLogin() {
        setTitle("Spanish Quiz App - Inicio de sesión");
        cardLayout.show(cards, "login");
        currentUser = null;
    }

    private void showStudentHome() {
        if (currentUser == null) {
            showLogin();
            return;
        }
        setTitle("Spanish Quiz App - Alumno");
        studentHomePanel.setStudent(currentUser);
        cardLayout.show(cards, "studentHome");
    }

    private void showQuiz() {
        setTitle("Spanish Quiz App - Quiz");
        cardLayout.show(cards, "quiz");
    }

    private void showStudentStats() {
        setTitle("Spanish Quiz App - Estadísticas");
        cardLayout.show(cards, "studentStats");
    }

    private void showTeacherDashboard() {
        setTitle("Spanish Quiz App - Profesor");
        teacherDashboardPanel.showData(statsManager.getAllStats());
        cardLayout.show(cards, "teacher");
    }

    @Override
    public void onLoginSuccess(User user) {
        this.currentUser = user;
        if (user.getRole() == Role.TEACHER) {
            showTeacherDashboard();
        } else {
            showStudentHome();
        }
    }

    @Override
    public void onStartQuiz(String topic, String subtopic) {
        if (currentUser == null) {
            return;
        }
        List<Question> questions = dataStore.getQuestionsFor(topic, subtopic);
        currentSession = new QuizSession(topic, subtopic, questions);
        quizPanel.startSession(currentUser, currentSession, statsManager);
        showQuiz();
    }

    @Override
    public void onViewStats() {
        if (currentUser == null) {
            return;
        }
        studentStatsPanel.showStats(statsManager.getStudentStats(currentUser));
        showStudentStats();
    }

    @Override
    public void onLogout() {
        showLogin();
    }

    @Override
    public void onExitQuiz() {
        if (currentUser == null) {
            showLogin();
        } else {
            showStudentHome();
        }
    }

    @Override
    public void onBack() {
        showStudentHome();
    }

    @Override
    public void onRefresh() {
        teacherDashboardPanel.showData(statsManager.getAllStats());
        JOptionPane.showMessageDialog(this, "Datos actualizados.", "Profesor", JOptionPane.INFORMATION_MESSAGE);
    }
}
