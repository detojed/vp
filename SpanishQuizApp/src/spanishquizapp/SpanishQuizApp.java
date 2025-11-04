package spanishquizapp;

import javax.swing.SwingUtilities;

public class SpanishQuizApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStore dataStore = DataStore.getInstance();
            dataStore.init();
            StatsManager statsManager = new StatsManager();
            spanishquizapp.ui.MainFrame frame = new spanishquizapp.ui.MainFrame(dataStore, statsManager);
            frame.setVisible(true);
        });
    }
}
