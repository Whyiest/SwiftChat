package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public  class RoundedButton extends JButton {

    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape shape = getRoundShape();
        g2.setColor(getBackground());
        g2.fill(shape);
        g2.setColor(getForeground());
        g2.draw(shape);
        g2.dispose();
        super.paintComponent(g);
    }

    protected Shape getRoundShape() {
        int arc = 20;
        int width = getWidth() - 1;
        int height = getHeight() - 1;
        return new RoundRectangle2D.Double(0, 0, width, height, arc, arc);
    }

}