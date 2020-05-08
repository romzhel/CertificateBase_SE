package ui_windows;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

public class ExecutionIndicator {
    public static double INDETERMINATE = -1.0;
    public static double NO_OPERATION = 0.0;
    public static double COMPLETE = 1.0;
    private static ExecutionIndicator instance;
    private ProgressBar progressBar;

    private ExecutionIndicator() {
    }

    public static ExecutionIndicator getInstance() {
        if (instance == null) {
            instance = new ExecutionIndicator();
        }
        return instance;
    }

    public void init(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public synchronized double getProgress() {
        return progressBar.getProgress();
    }

    public synchronized void setProgress(double value) {
        Platform.runLater(() -> {
            if (progressBar != null) {
                progressBar.setProgress(value);
                progressBar.setVisible(progressBar.getProgress() != NO_OPERATION);
                progressBar.setProgress(value);
            }
        });
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public boolean hasActiveProcess() {
        return progressBar.getProgress() != NO_OPERATION;
    }

    public void start() {
        setProgress(INDETERMINATE);
    }

    public void stop() {
        setProgress(NO_OPERATION);
    }

    public void complete() {
        setProgress(COMPLETE);
    }
}
