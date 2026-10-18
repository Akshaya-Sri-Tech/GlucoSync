
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AppointmentBooking extends JFrame {

    // GlucoSync colour palette
    private static final Color TEAL = new Color(8, 127, 140);
    private static final Color BG = new Color(234, 246, 247);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(45, 55, 60);
    private static final Color BORDER = new Color(200, 229, 231);

    // Hardcoded slots: slot number, date, time
    // Later, replace this with records fetched from the database.
    private final String[][] slots = {
        {"Slot 01", "20 Oct 2026", "09:00 AM"},
        {"Slot 02", "20 Oct 2026", "10:30 AM"},
        {"Slot 03", "21 Oct 2026", "02:00 PM"},
        {"Slot 04", "21 Oct 2026", "03:30 PM"}
    };

    private final JRadioButton[] choices =
            new JRadioButton[slots.length];

    private final ButtonGroup group = new ButtonGroup();
    private final JLabel status = new JLabel(" ");

    public AppointmentBooking() {
        setTitle("GlucoSync | Appointment Booking");
        setSize(720, 760);
        setMinimumSize(new Dimension(620, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 22));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(28, 38, 28, 38));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG);

        JLabel logo = new JLabel("GlucoSync");
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setForeground(TEAL);

        JLabel heading = new JLabel("Book an Appointment");
        heading.setFont(new Font("Arial", Font.BOLD, 23));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Select a date and time from the available slots.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        header.add(logo);
        header.add(Box.createVerticalStrut(18));
        header.add(heading);
        header.add(Box.createVerticalStrut(7));
        header.add(subtitle);

        // Slot list: one card per row
        JPanel slotList = new JPanel();
        slotList.setLayout(new BoxLayout(slotList, BoxLayout.Y_AXIS));
        slotList.setBackground(CARD);
        slotList.setBorder(new EmptyBorder(18, 18, 18, 18));

        for (int i = 0; i < slots.length; i++) {
            final int index = i;

            JPanel slotCard = new JPanel(new BorderLayout(15, 0));
            slotCard.setBackground(new Color(245, 251, 251));
            slotCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    new EmptyBorder(12, 16, 12, 16)
            ));

            // Keep every card tall enough for all three text lines.
            slotCard.setPreferredSize(new Dimension(600, 75));
            slotCard.setMinimumSize(new Dimension(400, 75));
            slotCard.setMaximumSize(new Dimension(
                    Integer.MAX_VALUE, 100));
            slotCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Left side: slot number, date, and time
            JPanel details = new JPanel();
            details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
            details.setBackground(slotCard.getBackground());

            JLabel slotLabel = new JLabel(slots[i][0]);
            slotLabel.setFont(new Font("Arial", Font.BOLD, 11));
            slotLabel.setForeground(TEAL);

            JLabel dateLabel = new JLabel("Date: " + slots[i][1]);
            dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
            dateLabel.setForeground(TEXT);

            JLabel timeLabel = new JLabel("Time: " + slots[i][2]);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            timeLabel.setForeground(new Color(90, 100, 105));

            // Explicitly add all labels to the visible panel.
            details.add(slotLabel);
            details.add(Box.createVerticalStrut(4));
            details.add(dateLabel);
            details.add(Box.createVerticalStrut(4));
            details.add(timeLabel);

            // Right side: radio selection
            choices[i] = new JRadioButton();
            choices[i].setBackground(slotCard.getBackground());
            choices[i].setFocusPainted(false);
            choices[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            group.add(choices[i]);

            slotCard.add(details, BorderLayout.CENTER);
            slotCard.add(choices[i], BorderLayout.EAST);

            // Clicking the card also selects it.
            slotCard.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            slotCard.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    choices[index].setSelected(true);
                }
            });

            slotList.add(slotCard);

            if (i < slots.length - 1) {
                slotList.add(Box.createVerticalStrut(12));
            }
        }

        choices[0].setSelected(true);

        // Allow scrolling if the window is resized.
        JScrollPane scrollPane = new JScrollPane(slotList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BG);
        scrollPane.getViewport().setBackground(CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Bottom section
        JButton bookButton = new JButton("Book Appointment");
        bookButton.setFont(new Font("Arial", Font.BOLD, 15));
        bookButton.setForeground(Color.WHITE);
        bookButton.setBackground(TEAL);
        bookButton.setFocusPainted(false);
        bookButton.setBorderPainted(false);
        bookButton.setPreferredSize(new Dimension(230, 46));
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        status.setFont(new Font("Arial", Font.PLAIN, 14));
        status.setForeground(TEAL);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookButton.addActionListener(e -> {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i].isSelected()) {
                    String confirmation =
                            slots[i][0] + "\nDate: " + slots[i][1]
                            + "\nTime: " + slots[i][2];

                    status.setText("Selected: " + slots[i][1]
                            + " at " + slots[i][2]);

                    JOptionPane.showMessageDialog(
                            this,
                            "Appointment slot selected!\n\n"
                                    + confirmation,
                            "GlucoSync",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            }

            status.setText("Please select a slot.");
        });

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(BG);
        bottom.add(bookButton);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(status);

        root.add(header, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new AppointmentBooking().setVisible(true));
    }
}
