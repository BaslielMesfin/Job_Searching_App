package view;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.List;

public class ApplicantsListView extends JFrame {
    private JButton backToDashboardButton;

    public ApplicantsListView(List<User> applicants, String jobTitle, Runnable backToDashboardAction) {
        setTitle("Applicants for: " + jobTitle);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Heading
        JLabel heading = new JLabel("Applicants for: " + jobTitle, SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Container for all applicant cards
        JPanel applicantsPanel = new JPanel();
        applicantsPanel.setLayout(new BoxLayout(applicantsPanel, BoxLayout.Y_AXIS));

        for (User applicant : applicants) {
            JPanel applicantCard = new JPanel(new BorderLayout());
            applicantCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // Name and Email
            JLabel infoLabel = new JLabel("<html><b>" + applicant.getName() + "</b><br/>" + applicant.getEmail() + "</html>");
            applicantCard.add(infoLabel, BorderLayout.CENTER);

            // Resume button
            JButton resumeButton = new JButton("View Resume");
            resumeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            resumeButton.addActionListener(e -> {
                try {
                    String link = applicant.getResumeLink();
                    System.out.println("Opening resume link: " + link);

                    if (link == null || link.isBlank()) {
                        throw new IllegalArgumentException("Resume link is empty.");
                    }

                    File file = new File(link);
                    if (file.exists()) {
                        Desktop.getDesktop().browse(file.toURI());
                    } else {
                        Desktop.getDesktop().browse(new URI(link));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to open resume link: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            applicantCard.add(resumeButton, BorderLayout.EAST);

            applicantsPanel.add(applicantCard);
            applicantsPanel.add(Box.createVerticalStrut(10)); // spacing between cards
        }

        JScrollPane scrollPane = new JScrollPane(applicantsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Back button
        backToDashboardButton = new JButton("Back to Dashboard");
        backToDashboardButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backToDashboardButton.setFocusPainted(false);
        backToDashboardButton.setBackground(Color.WHITE);
        backToDashboardButton.setForeground(Color.BLACK);
        backToDashboardButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        backToDashboardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToDashboardButton.addActionListener(e -> {
            if (backToDashboardAction != null) {
                backToDashboardAction.run();
            }
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        buttonPanel.add(backToDashboardButton);

        // Layout
        setLayout(new BorderLayout());
        add(heading, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
