import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/** Render recorded program output as readable PNG evidence; these are not desktop screenshots. */
public final class OutputCapture {
    public static void main(String[] args) throws Exception {
        String title = args[2];
        List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of(args[0]), StandardCharsets.UTF_8)) {
            // Wrap long menu/error lines while preserving the original log on disk.
            if (line.isEmpty()) lines.add("");
            for (int offset = 0; offset < line.length(); offset += 100) {
                lines.add(line.substring(offset, Math.min(offset + 100, line.length())));
            }
        }
        int width = 1160, height = 112 + Math.max(1, lines.size()) * 24;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setColor(new Color(15, 23, 42)); graphics.fillRect(0, 0, width, height);
        graphics.setColor(new Color(30, 41, 59)); graphics.fillRect(0, 0, width, 76);
        graphics.setColor(new Color(226, 232, 240)); graphics.setFont(new Font("SansSerif", Font.BOLD, 22));
        graphics.drawString(title, 24, 32);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 14));
        graphics.drawString("Recorded Java execution | rendered console output", 24, 58);
        graphics.setFont(new Font("Monospaced", Font.PLAIN, 17));
        FontMetrics metrics = graphics.getFontMetrics();
        int y = 102;
        for (String line : lines) { graphics.drawString(line, 24, y); y += Math.max(24, metrics.getHeight()); }
        graphics.dispose();
        ImageIO.write(image, "png", Path.of(args[1]).toFile());
    }
}
