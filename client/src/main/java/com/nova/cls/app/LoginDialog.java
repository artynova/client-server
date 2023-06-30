package com.nova.cls.app;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.nova.cls.exceptions.ClientFailureException;
import com.nova.cls.exceptions.NoConnectionException;
import com.nova.cls.exceptions.RequestFailureException;
import com.nova.cls.network.LoginClient;
import com.nova.cls.network.Session;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.http.HttpClient;

public class LoginDialog extends JDialog {
    public static final Dimension MIN_WINDOW_SIZE = new Dimension(500, 350);

    static {
        FlatMacDarkLaf.registerCustomDefaultsSource("style");
        FlatMacDarkLaf.setup();
    }

    private final LoginClient client;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField loginField;
    private JPasswordField passwordField;

    public LoginDialog(LoginClient client) {
        this.client = client;
        setContentPane(contentPane);
        setModal(true);
        setSize(MIN_WINDOW_SIZE);
        setMinimumSize(MIN_WINDOW_SIZE);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onLogin());

        buttonCancel.addActionListener(e -> onExit());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onExit(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void main(String[] args) {
        LoginDialog dialog =
            new LoginDialog(new LoginClient(HttpClient.newHttpClient(), new Encryptor(), new Decryptor()));
        dialog.setVisible(true);
    }

    private void onLogin() {
        Session session;
        try {
            session = client.login(loginField.getText(), new String(passwordField.getPassword()));
        } catch (RequestFailureException | ClientFailureException | NoConnectionException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login unsuccessful", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
        new AppWindow(client, session).setVisible(true);
    }

    private void onExit() {
        dispose();
        System.exit(0);
    }
}
