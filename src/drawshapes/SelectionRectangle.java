package drawshapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class SelectionRectangle implements IShape {
    private int x, y, width, height;
    private boolean selected;
    private Color color;
    private Point anchorPoint;

    public SelectionRectangle() {
        this(0, 0, 0, 0);
    }

    public SelectionRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.selected = false;
        this.color = new Color(1, 1, 1, 0.5f);
        this.anchorPoint = new Point(x, y);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.drawRect(x, y, width, height);
    }

    @Override
    public boolean contains(Point p) {
        return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
    }

    @Override
    public boolean intersects(IShape other) {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public IShape clone() {
        SelectionRectangle clone = new SelectionRectangle(x, y, width, height);
        clone.setSelected(selected);
        clone.setColor(color);
        clone.setAnchorPoint(anchorPoint);
        return clone;
    }

    @Override
    public String toString() {
        return String.format("SELECTION,%d,%d,%d,%d", x, y, width, height);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(x, y, x + width, y + height);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setAnchorPoint(Point point) {
        this.anchorPoint = point;
        this.x = point.x;
        this.y = point.y;
    }

    @Override
    public Point getAnchorPoint() {
        return anchorPoint;
    }
}
