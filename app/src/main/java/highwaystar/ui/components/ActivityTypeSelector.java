package highwaystar.ui.components;

import highwaystar.models.Activity;

import javax.swing.*;
import java.awt.*;

public class ActivityTypeSelector extends JPanel {
    private ButtonGroup buttonGroup;
    private Activity.ActivityType selectedType;

    public ActivityTypeSelector() {
        setLayout(new GridLayout(1, 4, 10, 0));
        setOpaque(false);
        buttonGroup = new ButtonGroup();
        selectedType = Activity.ActivityType.WALK;

        for (Activity.ActivityType type : Activity.ActivityType.values()) {
            JToggleButton button = createTypeButton(type);
            buttonGroup.add(button);
            add(button);

            if (type == Activity.ActivityType.WALK) {
                button.setSelected(true);
            }
        }
    }

    private JToggleButton createTypeButton(Activity.ActivityType type) {
        JToggleButton button = new JToggleButton(
            "<html><center>" + type.getIcon() + "<br>" + type.getDisplayName() + "</center></html>"
        );
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.addActionListener(e -> selectedType = type);

        return button;
    }

    public Activity.ActivityType getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(Activity.ActivityType type) {
        this.selectedType = type;
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JToggleButton) {
                JToggleButton button = (JToggleButton) components[i];
                button.setSelected(Activity.ActivityType.values()[i] == type);
            }
        }
    }
}
