package drawshapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
    protected double opacity = 1.0;
    protected double rotation = 0.0;
    protected BorderStyle borderStyle = BorderStyle.SOLID;
    
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
        Graphics2D g2d = (Graphics2D) g.create();
        applyTransform(g2d);
        // Draw filled shape
        g2d.setColor(new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            (int)(opacity * 255)
        ));
        drawShape(g2d);
        // Draw border if selected
        if (selected) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.0f));
            BoundingBox bbox = getBoundingBox();
            g2d.drawRect(bbox.getLeft() - 2, bbox.getTop() - 2, bbox.getWidth() + 4, bbox.getHeight() + 4);
        }
        g2d.dispose();
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
    public void rotate(double degrees) {
        this.rotation = (this.rotation + degrees) % 360;
    }

    @Override
    public double getRotation() {
        return rotation;
    }

    @Override
    public IShape clone() {
        try {
            AbstractShape clone = (AbstractShape) super.clone();
            clone.color = new Color(color.getRGB());
            clone.selected = false;
            clone.opacity = this.opacity;
            clone.rotation = this.rotation;
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public double getOpacity() {
        return opacity;
    }

    @Override
    public void setOpacity(double opacity) {
        this.opacity = Math.max(0, Math.min(1, opacity));
    }

    @Override
    public void setBorderStyle(BorderStyle style) {
        this.borderStyle = style;
    }

    @Override
    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    protected void applyTransform(Graphics2D g) {
        g.rotate(Math.toRadians(rotation), getBoundingBox().getCenter().x, getBoundingBox().getCenter().y);
        Color currentColor = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            (int)(opacity * 255)
        );
        g.setColor(currentColor);

        switch (borderStyle) {
            case SOLID:
                g.setStroke(new BasicStroke(2.0f));
                break;
            case DASHED:
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                break;
            case DOTTED:
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{2}, 0));
                break;
        }
    }
}
