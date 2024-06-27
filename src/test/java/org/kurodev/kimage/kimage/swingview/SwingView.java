package org.kurodev.kimage.kimage.swingview;

import org.kurodev.kimage.kimage.font.Drawable;
import org.kurodev.kimage.kimage.font.KFont;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Ugly but functional UI that can render text to the screen as you're typing it.
 * Includes a font loading functionality
 */
public class SwingView extends Canvas {
    private static final JFrame frame = new JFrame();
    private static final JTextArea textArea = new JTextArea();
    private static final Canvas canvas = new SwingView();
    private static KFont font;
    private static Path defaultFile = Path.of("./testfonts/JetBrainsMono-Regular.ttf");

    private static void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel(new BorderLayout());
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        JToolBar toolBar = createToolbar();
        toolbarPanel.add(toolBar, BorderLayout.NORTH);
        toolbarPanel.add(new JLabel("Text:"), BorderLayout.WEST);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toolbarPanel.add(textArea, BorderLayout.CENTER);
        panel.add(new JScrollPane(canvas), BorderLayout.CENTER);
        panel.add(toolbarPanel, BorderLayout.NORTH);
        frame.setContentPane(panel);
        initialize();
        frame.setVisible(true);
        textArea.setText("The Quick Brown fox jumps over\nthe lazy dog\n");
        textArea.setCaretPosition(textArea.getText().length());
        canvas.repaint();
        textArea.grabFocus();
    }

    private static JToolBar createToolbar() {
        JToolBar toolBar = new JToolBar();
        JFileChooser fileChooser = new JFileChooser(Path.of("./testfonts").toFile());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(defaultFile.toFile());
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".ttf");
            }

            @Override
            public String getDescription() {
                return "TTF file";
            }
        });
        fileChooser.addActionListener(e -> {
            try {
                font = KFont.getFont(new FileInputStream(fileChooser.getSelectedFile()));
                canvas.repaint();
                textArea.grabFocus();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        toolBar.add(fileChooser);
        return toolBar;
    }

    public static void main(String[] args) throws Exception {
        loadFont();
        createAndShowGUI();

    }

    private static void loadFont() throws IOException {
        font = KFont.getFont(Files.newInputStream(defaultFile));
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
        font.drawString(d, 10, 55, 50, Color.BLACK, textArea.getText(), true);
    }
}
