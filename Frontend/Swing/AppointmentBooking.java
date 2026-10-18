import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    private static final Color TEAL = new Color(8, 127, 140);
    private static final Color BG = new Color(234, 246, 247);
    private static final Color TEXT = new Color(45, 55, 60);
    private static final Color BORDER = new Color(200, 229, 231);
    private static final Color SLOT_BG = new Color(245, 251, 251);

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
        setSize(650, 620);
        setMinimumSize(new Dimension(550, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 30, 22, 30));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG);

        JLabel logo = new JLabel("GlucoSync");
        logo.setFont(new Font("Arial", Font.BOLD, 27));
        logo.setForeground(TEAL);

        JLabel heading = new JLabel("Book an Appointment");
        heading.setFont(new Font("Arial", Font.BOLD, 21));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Choose an available date and time.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        header.add(logo);
        header.add(Box.createVerticalStrut(10));
        header.add(heading);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        // Appointment slot list
        JPanel slotList = new JPanel();
        slotList.setLayout(new BoxLayout(slotList, BoxLayout.Y_AXIS));
        slotList.setBackground(Color.WHITE);
        slotList.setBorder(new EmptyBorder(12, 12, 12, 12));

        for (int i = 0; i < slots.length; i++) {
            final int index = i;

            JPanel slotCard = new JPanel(new BorderLayout(8, 0));
            slotCard.setBackground(SLOT_BG);
            slotCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    new EmptyBorder(7, 12, 7, 10)
            ));

            // Compact, consistent card height
            slotCard.setPreferredSize(new Dimension(500, 64));
            slotCard.setMinimumSize(new Dimension(300, 64));
            slotCard.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 64));
            slotCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Date and time details
            JPanel details = new JPanel();
            details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
            details.setBackground(SLOT_BG);

            JLabel slotLabel = new JLabel(slots[i][0]);
            slotLabel.setFont(new Font("Arial", Font.BOLD, 10));
            slotLabel.setForeground(TEAL);

            JLabel dateLabel = new JLabel("Date: " + slots[i][1]);
            dateLabel.setFont(new Font("Arial", Font.BOLD, 13));
            dateLabel.setForeground(TEXT);

            JLabel timeLabel = new JLabel("Time: " + slots[i][2]);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            timeLabel.setForeground(new Color(90, 100, 105));

            details.add(slotLabel);
            details.add(dateLabel);
            details.add(timeLabel);

            // Radio button
            choices[i] = new JRadioButton();
            choices[i].setBackground(SLOT_BG);
            choices[i].setFocusPainted(false);
            choices[i].setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            group.add(choices[i]);

            slotCard.add(details, BorderLayout.CENTER);
            slotCard.add(choices[i], BorderLayout.EAST);

            slotCard.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            slotCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    choices[index].setSelected(true);
                }
            });

            slotList.add(slotCard);

            if (i < slots.length - 1) {
                slotList.add(Box.createVerticalStrut(8));
            }
        }

        choices[0].setSelected(true);

        JScrollPane scrollPane = new JScrollPane(slotList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        // Booking section
        JButton bookButton = new JButton("Book Appointment");
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setForeground(Color.WHITE);
        bookButton.setBackground(TEAL);
        bookButton.setFocusPainted(false);
        bookButton.setBorderPainted(false);
        bookButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookButton.setPreferredSize(new Dimension(200, 40));
        bookButton.setMaximumSize(new Dimension(200, 40));
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bookButton.setBackground(new Color(6, 101, 112));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bookButton.setBackground(TEAL);
            }
        });

        status.setFont(new Font("Arial", Font.PLAIN, 12));
        status.setForeground(TEAL);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookButton.addActionListener(e -> {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i].isSelected()) {
                    String confirmation =
                            slots[i][0]
                            + "\nDate: " + slots[i][1]
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
        bottom.add(Box.createVerticalStrut(6));
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