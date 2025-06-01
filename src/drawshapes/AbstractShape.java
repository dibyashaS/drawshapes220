package drawshapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Abstract shape class.
 * 
 * Lots of the data and methods for a shape are in here
 * and can be inherited by subclass shapes.
 * 
 * 
 * 
 */
public abstract class AbstractShape implements IMoveableShape
{
    protected BoundingBox boundingBox;
    protected boolean selected;
    protected Color color;
    protected Point anchorPoint;
    
    protected AbstractShape(Color color, Point anchorPoint) {
        this.color = color;
        this.anchorPoint = anchorPoint;
        this.selected = false;
    }
    
    protected void setBoundingBox(int left, int right, int top, int bottom) {
        this.boundingBox = new BoundingBox(left, right, top, bottom);
    }

    /* (non-Javadoc)
     * @see drawshapes.sol.Shape#intersects(drawshapes.sol.Shape)
     */
    @Override
    public boolean intersects(IShape other) {
        if (this == other || other == null) {
            return false;
        }
        return this.boundingBox.intersects(other.getBoundingBox());
    }

    /* (non-Javadoc)
     * @see drawshapes.sol.Shape#contains(java.awt.Point)
     */
    @Override
    public boolean contains(Point point) {
        return this.boundingBox.contains(point);
    }

    /* (non-Javadoc)
     * @see drawshapes.sol.Shape#getBoundingBox()
     */
    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    /* (non-Javadoc)
     * @see drawshapes.sol.Shape#getColor()
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /* (non-Javadoc)
     * @see drawshapes.sol.Shape#setColor(java.awt.Color)
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /* (non-Javadoc)
     * @see drawshapes.sol.Shape#isSelected()
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /* (non-Javadoc)
     * @see drawshapes.sol.Shape#setSelected(boolean)
     */
    @Override
    public void setSelected(boolean b) {
        this.selected = b;
    }
    
    @Override
    public Point getAnchorPoint() {
        return this.anchorPoint;
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        drawShape(g);
        if (selected) {
            g.setColor(Color.BLACK);
            g.drawRect(boundingBox.getLeft() - 2, boundingBox.getTop() - 2, 
                      boundingBox.getWidth() + 4, boundingBox.getHeight() + 4);
        }
    }

    protected abstract void drawShape(Graphics g);

    @Override
    public void setAnchorPoint(Point point) {
        this.anchorPoint = point;
    }

    @Override
    public void move(int dx, int dy) {
        anchorPoint.x += dx;
        anchorPoint.y += dy;
    }

    @Override
    public void scale(double factor) {
        // Implement scaling in subclasses
    }

    @Override
    public void rotate(double angle) {
        // Rotation removed for simplicity
    }

    @Override
    public double getRotation() {
        return 0; // No rotation
    }

    @Override
    public abstract IShape clone();
}
