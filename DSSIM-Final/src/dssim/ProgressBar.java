package dssim;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.util.Random;

public class ProgressBar extends javax.swing.JPanel implements ActionListener, PropertyChangeListener {

    private JProgressBar progressBar;
    private JTextArea taskOutput;
    private Task task;
    JFrame frame = null;

    class Task extends SwingWorker<Void, Void> {

        @Override
        public Void doInBackground() {
            Random random = new Random();
            int progress = 0;
            setProgress(0);
            while (progress < 100) {
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException ignore) {
                }
                progress = getProgress();
                setProgress(Math.min(progress, 100));
            }
            return null;
        }

        @Override
        public void done() {
            taskOutput.append("Done!\n");
        }
    }

    public ProgressBar() {
        super(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    public boolean isFrameAvailable() {
        return frame != null;
    }

    public void setFrame(JFrame modelFrame) {
        frame = modelFrame;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void actionPerformed(ActionEvent evt) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            taskOutput.append(String.format(
                    "Completed %d%% of calculations.\n", task.getProgress()));
        }
    }

    private static void GUI() {
        JFrame frame = new JFrame("Performing Calculations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JComponent newContentPane = new ProgressBar();
        newContentPane.setOpaque(true); //Must be opaque**
        frame.setContentPane(newContentPane);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI();
            }
        });
    }
}
