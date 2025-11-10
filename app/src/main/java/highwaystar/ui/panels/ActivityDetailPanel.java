package highwaystar.ui.panels;

import highwaystar.models.Activity;
import highwaystar.services.ActivityService;
import highwaystar.ui.MainFrame;
import highwaystar.utils.DateUtils;
import highwaystar.utils.DistanceCalculator;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityDetailPanel extends JPanel {
    private final MainFrame mainFrame;
    private final String userId;
    private final String activityId;
    private Activity activity;

    private JLabel typeLabel;
    private JLabel dateLabel;
    private JLabel durationLabel;
    private JLabel stepsLabel;
    private JLabel distanceLabel;
    private JLabel paceLabel;
    private JLabel caloriesLabel;
    private JTextArea notesArea;
    private JXMapViewer mapViewer;

    public ActivityDetailPanel(MainFrame mainFrame, String userId, String activityId) {
        this.mainFrame = mainFrame;
        this.userId = userId;
        this.activityId = activityId;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        createTopBar();
        loadActivity();
    }

    private void createTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Activity Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(220, 20, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteActivity());
        buttonPanel.add(deleteBtn);

        JButton saveBtn = new JButton("Save Notes");
        saveBtn.setBackground(new Color(70, 130, 180));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveNotes());
        buttonPanel.add(saveBtn);

        JButton backBtn = new JButton("Back to History");
        backBtn.addActionListener(e -> mainFrame.showHistory());
        buttonPanel.add(backBtn);

        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void loadActivity() {
        ActivityService.getInstance().getActivity(userId, activityId).thenAccept(act -> {
            this.activity = act;
            SwingUtilities.invokeLater(this::createContent);
        });
    }

    private void createContent() {
        if (activity == null) {
            add(new JLabel("Activity not found", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);

        // Left panel - Activity info
        JPanel infoPanel = new JPanel(new BorderLayout(0, 20));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Activity header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        typeLabel = new JLabel(activity.getType().getIcon() + " " + activity.getType().getDisplayName());
        typeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(typeLabel, BorderLayout.NORTH);

        dateLabel = new JLabel(DateUtils.formatDateTime(activity.getTimestamp()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(100, 100, 100));
        headerPanel.add(dateLabel, BorderLayout.SOUTH);

        infoPanel.add(headerPanel, BorderLayout.NORTH);

        // Stats grid
        JPanel statsPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        statsPanel.setBackground(Color.WHITE);

        addStatRow(statsPanel, "Duration:", DistanceCalculator.formatDuration(activity.getDuration()));
        addStatRow(statsPanel, "Steps:", String.format("%,d", activity.getSteps()));
        addStatRow(statsPanel, "Distance:", String.format("%.2f km", activity.getDistance()));

        String pace = activity.getDuration() > 0 && activity.getDistance() > 0 ?
            DistanceCalculator.calculatePace(activity.getDistance(), activity.getDuration(), true) : "-";
        addStatRow(statsPanel, "Pace:", pace);

        int calories = activity.calculateCalories(70.0); // Default weight
        addStatRow(statsPanel, "Calories:", calories + " kcal");

        double avgSpeed = activity.getDuration() > 0 ?
            (activity.getDistance() / (activity.getDuration() / 3600.0)) : 0;
        addStatRow(statsPanel, "Avg Speed:", String.format("%.2f km/h", avgSpeed));

        infoPanel.add(statsPanel, BorderLayout.CENTER);

        // Notes
        JPanel notesPanel = new JPanel(new BorderLayout(0, 5));
        notesPanel.setBackground(Color.WHITE);

        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notesPanel.add(notesLabel, BorderLayout.NORTH);

        notesArea = new JTextArea(activity.getNotes() != null ? activity.getNotes() : "");
        notesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(0, 100));
        notesPanel.add(notesScroll, BorderLayout.CENTER);

        infoPanel.add(notesPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(infoPanel);

        // Right panel - Map with route
        createMapPanel();
        splitPane.setRightComponent(mapViewer);

        add(splitPane, BorderLayout.CENTER);
    }

    private void addStatRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        labelComponent.setForeground(new Color(100, 100, 100));
        panel.add(labelComponent);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(valueComponent);
    }

    private void createMapPanel() {
        mapViewer = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setZoom(12);

        // If activity has route, display it
        if (activity.getRoute() != null && !activity.getRoute().isEmpty()) {
            List<GeoPosition> track = new ArrayList<>();
            for (Activity.RoutePoint point : activity.getRoute()) {
                track.add(new GeoPosition(point.getLatitude(), point.getLongitude()));
            }

            // Set map center to first point
            mapViewer.setAddressLocation(track.get(0));

            // Create waypoint set
            Set<Waypoint> waypoints = new HashSet<>();
            for (GeoPosition pos : track) {
                waypoints.add(new DefaultWaypoint(pos));
            }

            WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(waypoints);

            // Create route painter
            RoutePainter routePainter = new RoutePainter(track);

            List<Painter<JXMapViewer>> painters = new ArrayList<>();
            painters.add(routePainter);
            painters.add(waypointPainter);

            CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(compoundPainter);

        } else {
            // Default location
            mapViewer.setAddressLocation(new GeoPosition(1.3521, 103.8198));
        }
    }

    private void saveNotes() {
        activity.setNotes(notesArea.getText());
        ActivityService.getInstance().updateActivity(activity).thenAccept(success -> {
            SwingUtilities.invokeLater(() -> {
                if (success) {
                    JOptionPane.showMessageDialog(this, "Notes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save notes", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private void deleteActivity() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this activity?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            ActivityService.getInstance().deleteActivity(userId, activityId).thenAccept(success -> {
                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Activity deleted", "Success", JOptionPane.INFORMATION_MESSAGE);
                        mainFrame.showHistory();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete activity", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            });
        }
    }

    // Simple route painter
    private class RoutePainter implements Painter<JXMapViewer> {
        private final List<GeoPosition> track;

        public RoutePainter(List<GeoPosition> track) {
            this.track = track;
        }

        @Override
        public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
            g = (Graphics2D) g.create();
            g.setColor(new Color(255, 0, 0, 150));
            g.setStroke(new BasicStroke(3));

            for (int i = 0; i < track.size() - 1; i++) {
                Point2D pt1 = map.getTileFactory().geoToPixel(track.get(i), map.getZoom());
                Point2D pt2 = map.getTileFactory().geoToPixel(track.get(i + 1), map.getZoom());

                Rectangle viewportBounds = map.getViewportBounds();
                g.drawLine(
                    (int) (pt1.getX() - viewportBounds.x),
                    (int) (pt1.getY() - viewportBounds.y),
                    (int) (pt2.getX() - viewportBounds.x),
                    (int) (pt2.getY() - viewportBounds.y)
                );
            }

            g.dispose();
        }
    }
}
