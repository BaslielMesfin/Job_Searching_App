package controller;

import model.Job;
import model.User;
import database.JobDAO;
import view.PostJobView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PostJobController {

    private PostJobView view;
    private User employer;
    private EmployerDashboardController dashboardController;

    public PostJobController(User employer, EmployerDashboardController dashboardController) {
        this.employer = employer;
        this.dashboardController = dashboardController;
        this.view = new PostJobView(dashboardController.getView());
        initController();
        view.setVisible(true);
    }

    private void initController() {
        view.getPostJobButton().addActionListener(new PostJobListener());
        view.getCancelButton().addActionListener(e -> view.dispose());
    }

    private class PostJobListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = view.getJobTitleField().getText().trim();
            String description = view.getDescriptionArea().getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Job title cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Description cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = JobDAO.insertJob(title, description, employer.getUserId());

            if (success) {
                JOptionPane.showMessageDialog(view, "Job posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                view.dispose();

                // Refresh the employer dashboard job list after posting new job
                dashboardController.refreshJobsList();
            } else {
                JOptionPane.showMessageDialog(view, "Failed to post job. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
