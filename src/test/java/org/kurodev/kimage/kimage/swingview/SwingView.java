package org.kurodev.kimage.kimage.swingview;

import org.kurodev.kimage.kimage.font.Drawable;
import org.kurodev.kimage.kimage.font.KFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SwingView extends Canvas {
    private static final JFrame frame = new JFrame();
    private static final JTextArea textArea = new JTextArea();
    private static final Canvas canvas = new SwingView();
    private static KFont font;

    private static void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(canvas), BorderLayout.CENTER);
        panel.add(textArea, BorderLayout.NORTH);
        frame.setContentPane(panel);
        initialize();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        loadFont();
        createAndShowGUI();

    }

    private static void loadFont() throws IOException {
        font = KFont.getFont(Files.newInputStream(Path.of("./testfonts/Catways.ttf")));
    }

    private static void initialize() {
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                canvas.repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Drawable d = (x, y, color) -> {
            g.setColor(color);
            g.drawRect(x, y, 1, 1);
            return null;
        };
        font.drawString(d, 10, 55, 50, Color.BLACK, textArea.getText());
    }
}
