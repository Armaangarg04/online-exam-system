package com.examsystem.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;

import javax.swing.JPanel;

public class AnimatedTimerPanel extends JPanel {

    private final int totalSeconds;
    private int secondsLeft;

    private static final Color COLOR_NORMAL  = new Color(140, 130, 220);
    private static final Color COLOR_WARNING = new Color(239, 159, 39);
    private static final Color COLOR_DANGER  = new Color(210, 50, 50);
    private static final Color COLOR_TRACK   = new Color(255, 255, 255, 35);

    public AnimatedTimerPanel(int totalSeconds) {
        this.totalSeconds = totalSeconds;
        this.secondsLeft  = totalSeconds;
        setOpaque(false);
        setPreferredSize(new Dimension(88, 88));
    }

    public void setSecondsLeft(int s) {
        this.secondsLeft = Math.max(0, s);
        repaint();
    }

    public int getSecondsLeft() { return secondsLeft; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w    = getWidth(), h = getHeight();
        int pad  = 8;
        int size = Math.min(w, h) - pad * 2;
        int x    = (w - size) / 2;
        int y    = (h - size) / 2;

        float ratio = totalSeconds > 0 ? (float) secondsLeft / totalSeconds : 0f;

        Color arcColor = ratio > 0.25f ? COLOR_NORMAL
                       : ratio > 0.10f ? COLOR_WARNING
                                       : COLOR_DANGER;

        // Track ring
        g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(COLOR_TRACK);
        g2.drawOval(x, y, size, size);

        // Progress arc
        g2.setColor(arcColor);
        g2.draw(new Arc2D.Float(x, y, size, size, 90f, -(ratio * 360f), Arc2D.OPEN));

        // Danger pulse
        if (ratio <= 0.10f) {
            float pulse = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 200.0));
            g2.setColor(new Color(210, 50, 50, (int)(50 * pulse)));
            int inner = size - 16;
            g2.fillOval((w - inner) / 2, (h - inner) / 2, inner, inner);
        }

        // Time label
        String text = String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(ratio <= 0.10f ? COLOR_DANGER : Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text,
            (w - fm.stringWidth(text)) / 2,
            (h + fm.getAscent() - fm.getDescent()) / 2);

        g2.dispose();
    }
}