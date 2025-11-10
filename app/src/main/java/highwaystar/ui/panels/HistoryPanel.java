package highwaystar.ui.panels;

import highwaystar.models.Activity;
import highwaystar.services.ActivityService;
import highwaystar.ui.MainFrame;
import highwaystar.utils.DateUtils;
import highwaystar.utils.DistanceCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    private final MainFrame mainFrame;
    private final String userId;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;

    public HistoryPanel(MainFrame mainFrame, String userId) {
        this.mainFrame = mainFrame;
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        createTopBar();
        createFilterPanel();
        createTable();
        loadActivities();
    }

    private void createTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Activity History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        backBtn.addActionListener(e -> mainFrame.showDashboard());
        topPanel.add(backBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterPanel.add(filterLabel);

        String[] filterOptions = {"All Time", "Last 7 Days", "Last 30 Days", "This Month"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> loadActivities());
        filterPanel.add(filterComboBox);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadActivities());
        filterPanel.add(refreshBtn);

        // Wrap in BorderLayout container
        JPanel wrapper = (JPanel) getComponent(0);
        JPanel newWrapper = new JPanel(new BorderLayout());
        newWrapper.add(wrapper, BorderLayout.NORTH);
        newWrapper.add(filterPanel, BorderLayout.SOUTH);
        remove(0);
        add(newWrapper, BorderLayout.NORTH);
    }

    private void createTable() {
        String[] columnNames = {"Date", "Type", "Duration", "Steps", "Distance", "Pace", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 13));
        historyTable.setRowHeight(35);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        historyTable.getTableHeader().setBackground(new Color(70, 130, 180));
        historyTable.getTableHeader().setForeground(Color.WHITE);

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add view button to actions column
        historyTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        historyTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadActivities();
    }

    private void loadActivities() {
        tableModel.setRowCount(0);

        ActivityService.getInstance().getActivities(userId).thenAccept(activities -> {
            List<Activity> filtered = filterActivities(activities);

            SwingUtilities.invokeLater(() -> {
                for (Activity activity : filtered) {
                    Object[] row = new Object[7];
                    row[0] = DateUtils.formatDisplayDate(activity.getTimestamp());
                    row[1] = activity.getType().getIcon() + " " + activity.getType().getDisplayName();
                    row[2] = DistanceCalculator.formatDuration(activity.getDuration());
                    row[3] = String.format("%,d", activity.getSteps());
                    row[4] = String.format("%.2f km", activity.getDistance());
                    row[5] = activity.getDuration() > 0 && activity.getDistance() > 0 ?
                        DistanceCalculator.calculatePace(activity.getDistance(), activity.getDuration(), true) : "-";
                    row[6] = activity.getActivityId(); // Store activity ID for button action

                    tableModel.addRow(row);
                }

                if (filtered.isEmpty()) {
                    Object[] emptyRow = new Object[7];
                    emptyRow[0] = "No activities found";
                    for (int i = 1; i < 7; i++) {
                        emptyRow[i] = "";
                    }
                    tableModel.addRow(emptyRow);
                }
            });
        });
    }

    private List<Activity> filterActivities(List<Activity> activities) {
        String filter = (String) filterComboBox.getSelectedItem();
        long currentTime = System.currentTimeMillis();

        return switch (filter) {
            case "Last 7 Days" -> activities.stream()
                .filter(a -> a.getTimestamp() >= currentTime - (7L * 24 * 60 * 60 * 1000))
                .toList();
            case "Last 30 Days" -> activities.stream()
                .filter(a -> a.getTimestamp() >= currentTime - (30L * 24 * 60 * 60 * 1000))
                .toList();
            case "This Month" -> activities.stream()
                .filter(a -> {
                    java.time.LocalDate activityDate = java.time.Instant.ofEpochMilli(a.getTimestamp())
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    java.time.LocalDate now = java.time.LocalDate.now();
                    return activityDate.getYear() == now.getYear() &&
                           activityDate.getMonth() == now.getMonth();
                })
                .toList();
            default -> activities;
        };
    }

    // Button renderer for table
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("View");
            setBackground(new Color(70, 130, 180));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
            return this;
        }
    }

    // Button editor for table
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String activityId;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            activityId = (String) value;
            button.setText("View");
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked && activityId != null && !activityId.isEmpty()) {
                mainFrame.showActivityDetail(activityId);
            }
            clicked = false;
            return activityId;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
