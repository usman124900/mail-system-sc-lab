import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

/**
 * Main class - RUN THIS FILE.
 * Opens a window where the user types From, To, Subject and Message,
 * sends the mail through the MailServer, and then reads it as the recipient.
 */
public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            useNimbusLookAndFeel();
            JFrame frame = new JFrame("Mail System - Lab 05");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new MailPanel());
            frame.setSize(580, 540);
            frame.setMinimumSize(new java.awt.Dimension(480, 440));
            frame.setLocationRelativeTo(null);   // centre on screen
            frame.setVisible(true);
        });
    }

    /** Use the nicer Nimbus look if available, otherwise the default. */
    private static void useNimbusLookAndFeel()
    {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // keep the default look and feel
        }
    }

    /**
     * The whole user interface. It owns ONE MailServer and creates a
     * MailClient for every user name that is typed in.
     */
    @SuppressWarnings("serial")
    static class MailPanel extends JPanel
    {
        private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);
        private static final Font TEXT_FONT = new Font("SansSerif", Font.PLAIN, 14);

        // The model: one shared server, one client per user.
        private final MailServer server = new MailServer();
        private final HashMap<String, MailClient> clients = new HashMap<>();

        // Compose tab
        private JTextField fromField;
        private JTextField toField;
        private JTextField subjectField;
        private JTextArea messageArea;

        // Inbox tab
        private JTextField inboxUserField;
        private JTextArea readArea;
        private JLabel countLabel;

        // Bottom status line
        private final JLabel statusLabel;

        MailPanel()
        {
            setLayout(new BorderLayout());

            statusLabel = new JLabel("Ready. Write a mail in the Compose tab.");
            statusLabel.setBorder(new EmptyBorder(6, 12, 6, 12));

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Compose", buildComposeTab());
            tabs.addTab("Inbox", buildInboxTab());

            add(tabs, BorderLayout.CENTER);
            add(statusLabel, BorderLayout.SOUTH);
        }

        // ---------------------------------------------------------------
        // Building the screens
        // ---------------------------------------------------------------

        private JPanel buildComposeTab()
        {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(15, 15, 15, 15));

            fromField = newTextField("fromField");
            toField = newTextField("toField");
            subjectField = newTextField("subjectField");

            messageArea = new JTextArea(8, 20);
            messageArea.setName("messageArea");
            messageArea.setFont(TEXT_FONT);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);

            addRow(panel, 0, "From:", fromField, 0);
            addRow(panel, 1, "To:", toField, 0);
            addRow(panel, 2, "Subject:", subjectField, 0);
            addRow(panel, 3, "Message:", new JScrollPane(messageArea), 1);

            JButton clearButton = new JButton("Clear");
            clearButton.setName("clearButton");
            clearButton.addActionListener(e -> clearCompose());

            JButton sendButton = new JButton("Send Mail");
            sendButton.setName("sendButton");
            sendButton.addActionListener(e -> sendMail());

            JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
            buttons.add(clearButton);
            buttons.add(sendButton);

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 4;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(10, 5, 0, 5);
            panel.add(buttons, c);

            return panel;
        }

        private JPanel buildInboxTab()
        {
            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.setBorder(new EmptyBorder(15, 15, 15, 15));

            inboxUserField = newTextField("inboxUserField");
            inboxUserField.setColumns(14);

            JButton checkButton = new JButton("Check Mail Count");
            checkButton.setName("checkButton");
            checkButton.addActionListener(e -> checkMailCount());

            JButton readButton = new JButton("Read Next Mail");
            readButton.setName("readButton");
            readButton.addActionListener(e -> readNextMail());

            JPanel top = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
            JLabel who = new JLabel("Mailbox of:");
            who.setFont(LABEL_FONT);
            top.add(who);
            top.add(inboxUserField);
            top.add(checkButton);
            top.add(readButton);

            readArea = new JTextArea(10, 20);
            readArea.setName("readArea");
            readArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            readArea.setEditable(false);
            readArea.setLineWrap(true);
            readArea.setWrapStyleWord(true);
            JScrollPane scroll = new JScrollPane(readArea);
            scroll.setBorder(BorderFactory.createTitledBorder("Mail"));

            countLabel = new JLabel("Type a user name, then check or read mail.");
            countLabel.setName("countLabel");
            countLabel.setFont(LABEL_FONT);

            panel.add(top, BorderLayout.NORTH);
            panel.add(scroll, BorderLayout.CENTER);
            panel.add(countLabel, BorderLayout.SOUTH);
            return panel;
        }

        private JTextField newTextField(String name)
        {
            JTextField field = new JTextField(20);
            field.setName(name);
            field.setFont(TEXT_FONT);
            return field;
        }

        /** Add one "label + input" row to a GridBag form. */
        private void addRow(JPanel panel, int row, String text, java.awt.Component input, double weighty)
        {
            JLabel label = new JLabel(text);
            label.setFont(LABEL_FONT);

            GridBagConstraints lc = new GridBagConstraints();
            lc.gridx = 0;
            lc.gridy = row;
            lc.anchor = GridBagConstraints.NORTHWEST;
            lc.insets = new Insets(8, 5, 5, 10);
            panel.add(label, lc);

            GridBagConstraints ic = new GridBagConstraints();
            ic.gridx = 1;
            ic.gridy = row;
            ic.weightx = 1;
            ic.weighty = weighty;
            ic.fill = (weighty > 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
            ic.insets = new Insets(5, 5, 5, 5);
            panel.add(input, ic);
        }

        // ---------------------------------------------------------------
        // What the buttons do (object interaction happens here)
        // ---------------------------------------------------------------

        /** Return the client for a user, creating it the first time. */
        private MailClient getClient(String user)
        {
            MailClient client = clients.get(user);
            if (client == null) {
                client = new MailClient(server, user);
                clients.put(user, client);
            }
            return client;
        }

        /** Send button: read the form, then ask the sender's client to send. */
        private void sendMail()
        {
            String from = fromField.getText().trim();
            String to = toField.getText().trim();
            String subject = subjectField.getText().trim();
            String message = messageArea.getText().trim();

            if (from.isEmpty() || to.isEmpty() || message.isEmpty()) {
                setStatus("Please fill in From, To and Message.", true);
                return;
            }
            if (subject.isEmpty()) {
                subject = "(no subject)";
            }

            getClient(from).sendMailItem(to, subject, message);

            subjectField.setText("");
            messageArea.setText("");
            setStatus("Mail sent from " + from + " to " + to + ".", false);
        }

        private void clearCompose()
        {
            fromField.setText("");
            toField.setText("");
            subjectField.setText("");
            messageArea.setText("");
            setStatus("Form cleared.", false);
        }

        /** Check button: show how many mails are waiting for the user. */
        private void checkMailCount()
        {
            String user = inboxUserField.getText().trim();
            if (user.isEmpty()) {
                setStatus("Please enter the user name whose mailbox you want.", true);
                return;
            }
            showCount(user);
            setStatus("Checked mailbox of " + user + ".", false);
        }

        /** Read button: receive the next mail for the user and display it. */
        private void readNextMail()
        {
            String user = inboxUserField.getText().trim();
            if (user.isEmpty()) {
                setStatus("Please enter the user name whose mailbox you want.", true);
                return;
            }

            MailItem item = getClient(user).getNextMailItem();
            if (item == null) {
                readArea.setText("No new mail for " + user + ".");
                setStatus("Mailbox of " + user + " is empty.", false);
            }
            else {
                readArea.setText(item.toString());
                setStatus("Mail received by " + user + ".", false);
            }
            showCount(user);
        }

        private void showCount(String user)
        {
            int count = getClient(user).howManyMailItems();
            countLabel.setText("Mails waiting for " + user + ": " + count);
        }

        private void setStatus(String text, boolean error)
        {
            statusLabel.setText(text);
            statusLabel.setForeground(error ? new Color(180, 30, 30) : new Color(20, 110, 40));
        }
    }
}
