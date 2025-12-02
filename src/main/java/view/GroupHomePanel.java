package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Group level Home tab.
 * Shows quick cards that jump to People, Meets, Tasks and Sched tabs.
 */
public class GroupHomePanel extends JPanel {

    private static final Color CARD_BG = new Color(56, 60, 68);
    private static final Color CARD_BG_HOVER = new Color(68, 73, 82);
    private static final Color CARD_BORDER = new Color(80, 84, 92);
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(185, 189, 199);

    public GroupHomePanel(JTabbedPane tabs, String groupName) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 32, 16, 32));
        setOpaque(false);

        add(buildHeader(groupName), BorderLayout.NORTH);
        add(buildCardsGrid(tabs), BorderLayout.CENTER);
    }

    private JComponent buildHeader(String groupName) {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to " + groupName);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel("Jump straight to what you need in this group");
        subtitle.setForeground(TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);

        return header;
    }

    private JComponent buildCardsGrid(JTabbedPane tabs) {
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 16));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(16, 0, 0, 0));

        grid.add(createHomeCard(
                tabs,
                "People",
                "Manage members, roles, and join requests.",
                "/assets/group-home-people.png",
                "People"
        ));
        grid.add(createHomeCard(
                tabs,
                "Meetings",
                "Create and review group meetings.",
                "/assets/group-home-meets.png",
                "Meets"
        ));
        grid.add(createHomeCard(
                tabs,
                "Tasks",
                "View and manage group tasks.",
                "/assets/group-home-tasks.png",
                "Tasks"
        ));
        grid.add(createHomeCard(
                tabs,
                "Schedules",
                "See availability and events in one place.",
                "/assets/group-home-sched.png",
                "Sched"
        ));

        return grid;
    }

    // create card + helpers

    private JPanel createHomeCard(JTabbedPane tabs,
                                  String titleText,
                                  String description,
                                  String iconPath,
                                  String targetTabName) {

        RoundedCard card = buildBaseCardPanel();
        JPanel content = buildCardContent(titleText, description, iconPath);
        card.add(content, BorderLayout.CENTER);

        attachCardMouseListener(card, tabs, targetTabName);
        return card;
    }

    private RoundedCard buildBaseCardPanel() {
        RoundedCard card = new RoundedCard(14);
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        card.setPreferredSize(new Dimension(0, 130));
        return card;
    }

    private JPanel buildCardContent(String titleText,
                                    String description,
                                    String iconPath) {

        // top row: icon + title, description
        JPanel topRow = new JPanel();
        topRow.setOpaque(false);
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));

        Icon icon = loadIcon(iconPath, 130, 130);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            topRow.add(iconLabel);
            topRow.add(Box.createHorizontalStrut(8));
        }

        JPanel text = buildTextBlock(titleText, description);
        topRow.add(text);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(topRow);
        content.add(Box.createVerticalStrut(6));

        return content;
    }

    private JPanel buildTextBlock(String titleText, String description) {
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText + "\n");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        text.add(title);
        text.add(Box.createHorizontalGlue());

        JLabel desc =
                new JLabel("<html><body style='width:120px'>" + description + "</body></html>");
        desc.setForeground(TEXT_SECONDARY);
        text.add(desc);

        return text;
    }

    private void attachCardMouseListener(JPanel card,
                                         JTabbedPane tabs,
                                         String targetTabName) {

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // single left click
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    int idx = tabs.indexOfTab(targetTabName);
                    if (idx != -1) {
                        tabs.setSelectedIndex(idx);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(CARD_BG_HOVER);
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                card.repaint();
            }
        });
    }

    // utils

    private Icon loadIcon(String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.out.println("Could not find icon resource: " + path);
            return null;
        }
        ImageIcon base = new ImageIcon(url);
        Image scaled = base.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static class RoundedCard extends JPanel {
        private final int arc;

        RoundedCard(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Shape round = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(getBackground());
            g2.fill(round);

            g2.setColor(CARD_BORDER);
            g2.draw(round);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
