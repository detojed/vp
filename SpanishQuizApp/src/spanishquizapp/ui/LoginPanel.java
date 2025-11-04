package spanishquizapp.ui;

import spanishquizapp.DataStore;
import spanishquizapp.Role;
import spanishquizapp.User;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Optional;

public class LoginPanel extends JPanel {

    public interface LoginSuccessListener {
        void onLoginSuccess(User user);
    }

    private final DataStore dataStore;
    private final LoginSuccessListener listener;

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public LoginPanel(DataStore dataStore, LoginSuccessListener listener) {
        this.dataStore = dataStore;
        this.listener = listener;
        buildUi();
    }

    private void buildUi() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        add(new JLabel("Usuario"), gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Contraseña"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Iniciar sesión");
        JButton registerButton = new JButton("Registrarse (Alumno)");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(loginButton, gbc);

        gbc.gridy = 3;
        add(registerButton, gbc);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce usuario y contraseña.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Optional<User> userOpt = dataStore.findUser(username);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        listener.onLoginSuccess(userOpt.get());
        clearFields();
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce usuario y contraseña.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Optional<User> existing = dataStore.findUser(username);
        if (existing.isPresent()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario ya existe.", "Registro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            dataStore.addStudent(username, password);
            JOptionPane.showMessageDialog(this, "Registro exitoso. Ahora puedes iniciar sesión.", "Registro", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
