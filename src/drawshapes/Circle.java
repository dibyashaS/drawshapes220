package drawshapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Circle extends AbstractShape
{
    private int radius;
    
    public Circle(Color color, Point center, int radius) {
        super(color, center);
        this.radius = radius;
    }

    @Override
    protected void drawShape(Graphics g) {
        g.setColor(color);
        g.drawOval(anchorPoint.x - radius, anchorPoint.y - radius, radius * 2, radius * 2);
        if (selected) {
            g.drawRect(anchorPoint.x - radius - 2, anchorPoint.y - radius - 2, radius * 2 + 4, radius * 2 + 4);
        }
    }

    @Override
    public boolean contains(Point p) {
        int dx = p.x - anchorPoint.x;
        int dy = p.y - anchorPoint.y;
        return dx * dx + dy * dy <= radius * radius;
    }

    @Override
    public boolean intersects(IShape other) {
        if (other instanceof Circle) {
            Circle c = (Circle) other;
            int dx = c.anchorPoint.x - anchorPoint.x;
            int dy = c.anchorPoint.y - anchorPoint.y;
            int distance = (int) Math.sqrt(dx * dx + dy * dy);
            return distance <= radius + c.radius;
        }
        return false;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(
            anchorPoint.x - radius,
            anchorPoint.y - radius,
            anchorPoint.x + radius,
            anchorPoint.y + radius
        );
    }

    @Override
    public void scale(double factor) {
        radius = (int) (radius * factor);
    }

    @Override
    public IShape clone() {
        Circle clone = new Circle(color, new Point(anchorPoint), radius);
        clone.setSelected(selected);
        clone.rotation = rotation;
        return clone;
    }

    @Override
    public String toString() {
        return String.format("CIRCLE,%d,%d,%d,%d", anchorPoint.x, anchorPoint.y, color.getRGB(), radius);
    }

    @Override
    public void setAnchorPoint(Point p) {
        // TODO: move bounding box
        this.anchorPoint = p;
    }
}
