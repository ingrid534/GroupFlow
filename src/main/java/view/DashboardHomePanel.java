package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Home panel for the dashboard.
 * Lets the user create or join a group and see their personal tasks.
 */
public class DashboardHomePanel extends JPanel {

    private static final Color BG_MAIN = new Color(245, 247, 250);
//    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(126, 126, 129);
    private static final Color TEXT_PRIMARY = new Color(26, 28, 35);
    private static final Color TEXT_SECONDARY = new Color(112, 118, 135);
    private static final Color BUTTON_BG = new Color(64, 120, 255);
//    private static final Color BUTTON_TEXT = Color.WHITE;

    public DashboardHomePanel(ViewTasksView viewTasksView,
                              ActionListener createGroupListener,
                              ActionListener joinGroupListener) {

        setLayout(new BorderLayout(24, 24));
        setBorder(new EmptyBorder(20, 20, 20, 20));
//        setBackground(BG_MAIN);

        add(buildLeftColumn(createGroupListener, joinGroupListener), BorderLayout.CENTER);

        if (viewTasksView != null) {
            add(buildTasksCard(viewTasksView), BorderLayout.EAST);
        }
    }

    /* ---------------- main column ---------------- */

    private JComponent buildLeftColumn(ActionListener createGroupListener,
                                       ActionListener joinGroupListener) {

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        column.add(buildHeroCard());
        column.add(Box.createVerticalStrut(16));
        column.add(buildActionsRow(createGroupListener, joinGroupListener));
        column.add(Box.createVerticalStrut(12));

        return column;
    }

    /* ---------------- hero card ---------------- */

    private JComponent buildHeroCard() {
        RoundedPanel hero = new RoundedPanel(22);
//        hero.setBackground(CARD_BG);
        hero.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        hero.setLayout(new BorderLayout(12, 12));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(buildHeroTextBlock());
        content.add(Box.createVerticalStrut(10));
        content.add(buildHeroImageBlock());

        hero.add(content, BorderLayout.CENTER);
        hero.add(buildAccentStrip(), BorderLayout.SOUTH);

        return hero;
    }

    private JComponent buildHeroTextBlock() {
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Find or create your group");
//        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JLabel subtitle = wrapLabel(
                "Collaborate with your team, track tasks, and manage members in one place.",
                280
        );
//        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setBorder(new EmptyBorder(4, 0, 0, 0));

        text.add(title);
        text.add(subtitle);

        return text;
    }

    // Creates a center block that displays the hero GIF.
    // The image is scaled to fit inside the available space without escaping,
    // and keeps its aspect ratio.
    private JComponent buildHeroImageBlock() {
        HeroGifPanel gifPanel = new HeroGifPanel();
        gifPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gifPanel.setBorder(new EmptyBorder(4, 0, 4, 0));
        return gifPanel;
    }

    private JComponent buildAccentStrip() {
        JComponent strip = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(51, 204, 204),
                        getWidth(), getHeight(), new Color(20, 40, 100)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        strip.setPreferredSize(new Dimension(10, 10));
        strip.setOpaque(false);
        strip.setBorder(new EmptyBorder(4, 0, 0, 0));
        return strip;
    }

    /* ---------------- actions row ---------------- */

    private JComponent buildActionsRow(ActionListener createGroupListener,
                                       ActionListener joinGroupListener) {

        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);

        row.add(createActionCard(
                "Create a group",
                "Start a new space.",
                "Create group",
                createGroupListener
        ));

        row.add(createActionCard(
                "Join a group",
                "Use a join code.",
                "Join group",
                joinGroupListener
        ));

        return row;
    }

    /* ---------------- cards ---------------- */

    private JComponent createActionCard(String titleText,
                                        String descriptionText,
                                        String buttonText,
                                        ActionListener listener) {

        RoundedPanel card = new RoundedPanel(18);
        card.setLayout(new BorderLayout(10, 10));
//        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
//        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));

        JLabel desc = wrapLabel(descriptionText, 220);
//        desc.setForeground(TEXT_SECONDARY);
        desc.setBorder(new EmptyBorder(4, 0, 0, 0));

        textPanel.add(title);
        textPanel.add(desc);

        JButton button = new JButton(buttonText);
        stylePrimaryButton(button);
        button.addActionListener(listener);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(button, BorderLayout.SOUTH);

        return card;
    }

    private JComponent buildTasksCard(ViewTasksView viewTasksView) {
        RoundedPanel wrapper = new RoundedPanel(18);
        wrapper.setLayout(new BorderLayout(8, 8));
//        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        wrapper.setPreferredSize(new Dimension(320, 0));

        // header with label + refresh button
        wrapper.add(buildTasksHeader(viewTasksView), BorderLayout.NORTH);

        // actual tasks view
        wrapper.add(viewTasksView, BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 8, 0, 0));
        outer.add(wrapper, BorderLayout.CENTER);

        return outer;
    }

    /* ---------------- small helpers ---------------- */

    private JComponent buildTasksHeader(ViewTasksView viewTasksView) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(4, 4, 8, 4));

        JLabel label = new JLabel("Your tasks");
//        label.setForeground(TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));

        JButton refresh = new JButton("Refresh");
        styleSecondaryButton(refresh);
        refresh.addActionListener(evt -> viewTasksView.execute());

        header.add(label, BorderLayout.WEST);
        header.add(refresh, BorderLayout.EAST);

        return header;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
//        button.setBackground(BUTTON_BG);
//        button.setForeground(BUTTON_TEXT);
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 13f));
        button.setBorder(new EmptyBorder(6, 14, 6, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
//        button.setBackground(Color.WHITE);
//        button.setForeground(TEXT_PRIMARY);
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 12f));
        button.setBorder(new EmptyBorder(4, 10, 4, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Creates a label that wraps text to a fixed width using HTML.
     *
     * @param text  the text to display
     * @param width the desired wrap width in pixels
     * @return a JLabel that wraps the text
     */
    private JLabel wrapLabel(String text, int width) {
        return new JLabel("<html><body style='width:" + width + "px;'>" + text + "</body></html>");
    }

    /**
     * Simple rounded panel used to get a modern card look.
     */
    private static class RoundedPanel extends JPanel {
        private final int arc;

        RoundedPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
            setLayout(new BorderLayout());
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
            g2.dispose();

            super.paintComponent(g);
        }
    }

    /**
     * Panel that draws the hero GIF scaled to fit without cropping.
     */
    private static class HeroGifPanel extends JComponent {
        private final Image image;

        HeroGifPanel() {
            java.net.URL url = DashboardHomePanel.class.getResource("/assets/home-icon.gif");
            if (url != null) {
                image = new ImageIcon(url).getImage();
            } else {
                System.out.println("Could not find image resource.");
                image = null;
            }
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) {
                return;
            }

            int pw = getWidth();
            int ph = getHeight();
            if (pw <= 0 || ph <= 0) {
                return;
            }

            int iw = image.getWidth(this);
            int ih = image.getHeight(this);
            if (iw <= 0 || ih <= 0) {
                return;
            }

            double scale = Math.min((double) pw / iw, (double) ph / ih);
            int dw = (int) Math.round(iw * scale);
            int dh = (int) Math.round(ih * scale);

            int x = (pw - dw) / 2;
            int y = (ph - dh) / 2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image, x, y, dw, dh, this);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(140, 140);
        }
    }
}
