package drawshapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Rectangle extends AbstractShape
{
    private int width;
    private int height;
    
    public Rectangle(Point center, int width, int height, Color color) {
        super(color, center);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void drawShape(Graphics g) {
        g.setColor(color);
        g.fillRect(anchorPoint.x - width/2, anchorPoint.y - height/2, width, height);
        if (selected) {
            g.setColor(Color.BLACK);
            g.drawRect(anchorPoint.x - width/2 - 2, anchorPoint.y - height/2 - 2, width + 4, height + 4);
        }
    }

    @Override
    public boolean contains(Point p) {
        return p.x >= anchorPoint.x - width/2 && p.x <= anchorPoint.x + width/2 &&
               p.y >= anchorPoint.y - height/2 && p.y <= anchorPoint.y + height/2;
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
            anchorPoint.x - width/2,
            anchorPoint.y - height/2,
            anchorPoint.x + width/2,
            anchorPoint.y + height/2
        );
    }

    @Override
    public void scale(double factor) {
        width = (int) (width * factor);
        height = (int) (height * factor);
    }

    @Override
    public IShape clone() {
        Rectangle clone = new Rectangle(new Point(anchorPoint), width, height, color);
        clone.setSelected(selected);
        return clone;
    }

    @Override
    public String toString() {
        return String.format("RECTANGLE,%d,%d,%d,%d,%d", anchorPoint.x, anchorPoint.y, color.getRGB(), width, height);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
