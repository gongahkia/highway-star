package highwaystar;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
// import com.google.firebase.database.*;

public class HistoryWindow extends JFrame {

    public HistoryWindow(String uid) {

        setTitle("Activity History");
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            new MainWindow(uid).setVisible(true);
            this.dispose();
        });
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        JTable historyTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Uncomment this block to fetch real data from Firebase ---
        /*
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<HistoryEntry> entries = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String date = child.getKey();
                    Integer steps = child.child("steps").getValue(Integer.class);
                    entries.add(new HistoryEntry(date, steps != null ? steps : 0));
                }
                updateTable(historyTable, entries);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                JOptionPane.showMessageDialog(HistoryWindow.this, "Failed to load history");
            }
        });
        */

        List<HistoryEntry> entries = new ArrayList<>();
        entries.add(new HistoryEntry("2025-04-29", 4500));
        entries.add(new HistoryEntry("2025-04-30", 5200));
        entries.add(new HistoryEntry("2025-05-01", 6000));
        entries.add(new HistoryEntry("2025-05-02", 7000));
        entries.add(new HistoryEntry("2025-05-03", 6500));
        entries.add(new HistoryEntry("2025-05-04", 7200));
        entries.add(new HistoryEntry("2025-05-05", 8000));
        entries.add(new HistoryEntry("2025-05-06", 0));

        updateTable(historyTable, entries);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
    }

    private void updateTable(JTable table, List<HistoryEntry> entries) {
        String[] columnNames = {"Date", "Steps", "Trophies"};
        Object[][] data = new Object[entries.size()][3];
        for (int i = 0; i < entries.size(); i++) {
            HistoryEntry entry = entries.get(i);
            data[i][0] = entry.date;
            data[i][1] = entry.steps;
            data[i][2] = getStars(entry.steps);
        }
        SwingUtilities.invokeLater(() -> 
            table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames))
        );
    }

    private String getStars(int steps) {
        int starCount = steps / 5000; 
        return "★".repeat(starCount);
    }

    private static class HistoryEntry {
        String date;
        int steps;
        public HistoryEntry(String date, int steps) {
            this.date = date;
            this.steps = steps;
        }
    }
}