package drawshapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Square extends AbstractShape {
    private int size;

    public Square(Color color, int x, int y, int size) {
        super(color, new Point(x, y));
        this.size = size;
    }

    @Override
    protected void drawShape(Graphics g) {
        g.setColor(color);
        g.drawRect(anchorPoint.x - size/2, anchorPoint.y - size/2, size, size);
        if (selected) {
            g.drawRect(anchorPoint.x - size/2 - 2, anchorPoint.y - size/2 - 2, size + 4, size + 4);
        }
    }

    @Override
    public boolean contains(Point p) {
        return p.x >= anchorPoint.x - size/2 && p.x <= anchorPoint.x + size/2 &&
               p.y >= anchorPoint.y - size/2 && p.y <= anchorPoint.y + size/2;
    }

    @Override
    public boolean intersects(IShape other) {
        BoundingBox thisBox = getBoundingBox();
        BoundingBox otherBox = other.getBoundingBox();
        return thisBox.intersects(otherBox);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(
            anchorPoint.x - size/2,
            anchorPoint.y - size/2,
            anchorPoint.x + size/2,
            anchorPoint.y + size/2
        );
    }

    @Override
    public void scale(double factor) {
        size = (int) (size * factor);
    }

    @Override
    public IShape clone() {
        Square clone = new Square(color, anchorPoint.x, anchorPoint.y, size);
        clone.setSelected(selected);
        clone.rotation = rotation;
        return clone;
    }

    @Override
    public String toString() {
        return String.format("SQUARE,%d,%d,%d,%d", anchorPoint.x, anchorPoint.y, color.getRGB(), size);
    }
}
