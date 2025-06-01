package controller;

import database.ApplicationsDAO;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.Job;
import model.User;
import view.JobSeekerDashboardView;

public class JobSeekerDashboardController {
    private JobSeekerDashboardView view;
    private User user;

    public JobSeekerDashboardController(User user, List<Job> jobs) {
        this.user = user;

        // Load jobs asynchronously (Thread)
        new Thread(() -> {
            try {
                // Simulate loading jobs (you can replace this with your DAO call)
                Thread.sleep(500); // simulate delay

                // Update GUI on Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    view = new JobSeekerDashboardView(user.getName(), jobs);
                    addListeners();
                    view.setVisible(true);
                });

                // Start network notification listener (Thread + Network)
                startNotificationListener();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addListeners() {
        this.view.getViewProfileButton().addActionListener(new ViewProfileListener());
        this.view.getSearchJobsButton().addActionListener(new SearchJobsListener());
        setApplyButtonListeners(view.getApplyButtonsMap());
    }

    // Network client example: listen for messages from a local TCP server
    private void startNotificationListener() {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 12345);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                System.out.println("Connected to notification server...");

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Notification: " + message);
                    // Optionally, update GUI notifications here with SwingUtilities.invokeLater()
                }

            } catch (Exception e) {
                System.out.println("Notification listener stopped: " + e.getMessage());
            }
        }).start();
    }

    private void setApplyButtonListeners(Map<JButton, Job> applyButtonsMap) {
        for (Map.Entry<JButton, Job> entry : applyButtonsMap.entrySet()) {
            JButton button = entry.getKey();
            Job job = entry.getValue();

            button.addActionListener(e -> {
                int jobId = job.getJobId();
                boolean success = ApplicationsDAO.applyToJob(user.getUserId(), jobId, user.getResumeLink());

                if (success) {
                    button.setText("Applied");
                    button.setEnabled(false);
                    JOptionPane.showMessageDialog(view,
                            "You have successfully applied to \"" + job.getTitle() + "\" job.",
                            "Application Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view,
                            "You have already applied to this job.",
                            "Already Applied",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
        }
    }

    private class ViewProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new UserProfileController(user);
            view.dispose();
        }
    }

    private class SearchJobsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new JobSearchController(user);
        }
    }
}
