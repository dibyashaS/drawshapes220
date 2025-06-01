package drawshapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ShapeGroup implements IMoveableShape {
    private List<IShape> shapes;
    private boolean selected;
    private Color color;
    private Point anchorPoint;
    private double rotation;

    public ShapeGroup() {
        shapes = new ArrayList<>();
        selected = false;
        color = Color.BLACK;
        anchorPoint = new Point(0, 0);
        rotation = 0;
    }

    public void addShape(IShape shape) {
        shapes.add(shape);
    }

    public List<IShape> getShapes() {
        return shapes;
    }

    @Override
    public void draw(Graphics g) {
        for (IShape shape : shapes) {
            shape.draw(g);
        }
    }

    @Override
    public boolean contains(Point p) {
        for (IShape shape : shapes) {
            if (shape.contains(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean intersects(IShape other) {
        for (IShape shape : shapes) {
            if (shape.intersects(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        for (IShape shape : shapes) {
            shape.setSelected(selected);
        }
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public IShape clone() {
        ShapeGroup clone = new ShapeGroup();
        for (IShape shape : shapes) {
            clone.addShape(shape.clone());
        }
        clone.setSelected(selected);
        clone.setColor(color);
        clone.setAnchorPoint(anchorPoint);
        clone.rotation = rotation;
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GROUP,");
        for (IShape shape : shapes) {
            sb.append(shape.toString()).append(";");
        }
        return sb.toString();
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (shapes.isEmpty()) {
            return new BoundingBox(0, 0, 0, 0);
        }
        BoundingBox box = shapes.get(0).getBoundingBox();
        for (int i = 1; i < shapes.size(); i++) {
            box = box.union(shapes.get(i).getBoundingBox());
        }
        return box;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        for (IShape shape : shapes) {
            shape.setColor(color);
        }
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setAnchorPoint(Point point) {
        Point oldAnchor = anchorPoint;
        anchorPoint = point;
        int dx = point.x - oldAnchor.x;
        int dy = point.y - oldAnchor.y;
        move(dx, dy);
    }

    @Override
    public Point getAnchorPoint() {
        return anchorPoint;
    }

    @Override
    public void move(int dx, int dy) {
        for (IShape shape : shapes) {
            if (shape instanceof IMoveableShape) {
                ((IMoveableShape) shape).move(dx, dy);
            }
        }
        anchorPoint.x += dx;
        anchorPoint.y += dy;
    }

    @Override
    public void scale(double factor) {
        for (IShape shape : shapes) {
            if (shape instanceof IMoveableShape) {
                ((IMoveableShape) shape).scale(factor);
            }
        }
    }

    @Override
    public void rotate(double angle) {
        rotation += angle;
        for (IShape shape : shapes) {
            if (shape instanceof IMoveableShape) {
                ((IMoveableShape) shape).rotate(angle);
            }
        }
    }

    @Override
    public double getRotation() {
        return rotation;
    }
} 