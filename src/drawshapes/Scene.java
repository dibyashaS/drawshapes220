package drawshapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A scene of shapes.  Uses the Model-View-Controller (MVC) design pattern,
 * though note that model knows something about the view, as the draw() 
 * method both in Scene and in Shape uses the Graphics object. That's kind of sloppy,
 * but it also helps keep things simple.
 * 
 * This class allows us to talk about a "scene" of shapes,
 * rather than individual shapes, and to apply operations
 * to collections of shapes.
 * 
 * @author jspacco
 *
 */
public class Scene implements Iterable<IShape>
{
    private List<IShape> shapes;
    private Stack<List<IShape>> undoStack;
    
    public Scene() {
        shapes = new ArrayList<>();
        undoStack = new Stack<>();
        saveState();
    }
    
    private void saveState() {
        List<IShape> stateCopy = new ArrayList<>();
        for (IShape shape : shapes) {
            stateCopy.add(shape.clone());
        }
        undoStack.push(stateCopy);
    }
    
    public void undo() {
        if (undoStack.size() > 1) {
            shapes = undoStack.pop();
        }
    }
    
    public void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (IShape shape : shapes) {
                writer.write(shape.toString());
                writer.newLine();
            }
        }
    }
    
    public void loadFromFile(String filename) throws IOException {
        shapes.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                IShape shape = parseShape(line);
                if (shape != null) {
                    shapes.add(shape);
                }
            }
        }
        saveState();
    }
    
    private IShape parseShape(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) return null;

        String type = parts[0];
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        Color color = new Color(Integer.parseInt(parts[3]));

        switch (type) {
            case "SQUARE":
                return new Square(color, x, y, Integer.parseInt(parts[4]));
            case "CIRCLE":
                return new Circle(color, new Point(x, y), Integer.parseInt(parts[4]));
            case "RECTANGLE":
                return new Rectangle(new Point(x, y), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), color);
            default:
                return null;
        }
    }
    
    public void draw(Graphics g) {
        for (IShape s : shapes) {
            if (s != null) {
                s.draw(g);
            }
        }
    }
    
    public java.util.Iterator<IShape> iterator() {
        return shapes.iterator();
    }
    
    public List<IShape> select(Point point) {
        List<IShape> selected = new ArrayList<>();
        for (IShape s : shapes) {
            if (s.contains(point)) {
                selected.add(s);
            }
        }
        return selected;
    }
    
    public void addShape(IShape s) {
        shapes.add(s);
        saveState();
    }
    
    public void removeSelectedShapes() {
        shapes.removeIf(IShape::isSelected);
        saveState();
    }
    
    public List<IShape> getSelectedShapes() {
        List<IShape> selected = new ArrayList<>();
        for (IShape shape : shapes) {
            if (shape.isSelected()) {
                selected.add(shape);
            }
        }
        return selected;
    }
    
    @Override   
    public String toString() {
        StringBuilder shapeText = new StringBuilder();
        for (IShape s : shapes) {
            shapeText.append(s.toString()).append("\n");
        }
        return shapeText.toString();
    }
}
