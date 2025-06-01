package drawshapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class DrawShapes extends JFrame {
    public enum ShapeType {
        SQUARE,
        CIRCLE,
        RECTANGLE
    }
    
    private DrawShapesPanel shapePanel;
    private Scene scene;
    private ShapeType shapeType = ShapeType.SQUARE;
    private Color color = Color.RED;

    public DrawShapes(int width, int height) {
        setTitle("Draw Shapes!");
        scene = new Scene();
        
        shapePanel = new DrawShapesPanel(width, height, scene);
        this.getContentPane().add(shapePanel, BorderLayout.CENTER);
        this.setResizable(false);
        this.pack();
        this.setLocation(100, 100);
        
        initializeMouseListener();
        initializeKeyListener();
        initializeMenu();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    
    private void initializeMouseListener() {
        MouseAdapter a = new MouseAdapter() {
            private Point startPoint;
            private IShape currentShape;
            private boolean isResizing = false;
            private int resizeCorner = -1;

            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                boolean shapeSelected = false;
                
                // First, deselect all shapes unless Ctrl is pressed
                if (!e.isControlDown()) {
                    for (IShape shape : scene.getShapes()) {
                        shape.setSelected(false);
                    }
                }
                
                // Check shapes in reverse order (top to bottom)
                List<IShape> shapes = scene.getShapes();
                for (int i = shapes.size() - 1; i >= 0; i--) {
                    IShape shape = shapes.get(i);
                    
                    // First check if we're clicking on a corner for resizing
                    BoundingBox bbox = shape.getBoundingBox();
                    Point[] corners = bbox.getCorners();
                    for (int j = 0; j < corners.length; j++) {
                        if (distance(startPoint, corners[j]) < 10) {
                            isResizing = true;
                            resizeCorner = j;
                            currentShape = shape;
                            shape.setSelected(true);
                            shapeSelected = true;
                            break;
                        }
                    }
                    if (shapeSelected) break;
                    
                    // If not resizing, check if we're clicking on the shape itself
                    if (shape.contains(startPoint)) {
                        currentShape = shape;
                        shape.setSelected(true);
                        shapeSelected = true;
                        break;
                    }
                }
                
                // If we didn't click on any existing shape, create a new one
                if (!shapeSelected) {
                    if (shapeType == ShapeType.SQUARE) {
                        currentShape = new Square(color, e.getX(), e.getY(), 100);
                    } else if (shapeType == ShapeType.CIRCLE) {
                        currentShape = new Circle(color, e.getPoint(), 50);
                    } else if (shapeType == ShapeType.RECTANGLE) {
                        currentShape = new Rectangle(e.getPoint(), 150, 100, color);
                    }
                    currentShape.setSelected(true);
                    scene.addShape(currentShape);
                }
                
                // Request focus after any mouse interaction
                shapePanel.requestFocusInWindow();
                repaint();
            }

            public void mouseDragged(MouseEvent e) {
                if (currentShape != null) {
                    if (isResizing) {
                        // Handle resizing
                        BoundingBox bbox = currentShape.getBoundingBox();
                        Point[] corners = bbox.getCorners();
                        Point newCorner = e.getPoint();
                        
                        // Update shape size based on the dragged corner
                        if (currentShape instanceof Rectangle) {
                            Rectangle rect = (Rectangle) currentShape;
                            int width = Math.abs(newCorner.x - corners[(resizeCorner + 2) % 4].x);
                            int height = Math.abs(newCorner.y - corners[(resizeCorner + 2) % 4].y);
                            rect.setSize(width, height);
                        } else if (currentShape instanceof Square) {
                            Square square = (Square) currentShape;
                            int size = Math.max(
                                Math.abs(newCorner.x - corners[(resizeCorner + 2) % 4].x),
                                Math.abs(newCorner.y - corners[(resizeCorner + 2) % 4].y)
                            );
                            square.setSize(size);
                        } else if (currentShape instanceof Circle) {
                            Circle circle = (Circle) currentShape;
                            int radius = (int) Math.sqrt(
                                Math.pow(newCorner.x - circle.getCenter().x, 2) +
                                Math.pow(newCorner.y - circle.getCenter().y, 2)
                            );
                            circle.setRadius(radius);
                        }
                    } else if (currentShape.isSelected()) {
                        // Move the selected shape
                        int dx = e.getX() - startPoint.x;
                        int dy = e.getY() - startPoint.y;
                        currentShape.move(dx, dy);
                        startPoint = e.getPoint();
                    }
                    repaint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (currentShape != null) {
                    currentShape = null;
                    isResizing = false;
                    resizeCorner = -1;
                    repaint();
                }
            }

            private double distance(Point p1, Point p2) {
                return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
            }
        };
        shapePanel.addMouseListener(a);
        shapePanel.addMouseMotionListener(a);
    }
    
    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        JMenuItem saveItem = new JMenuItem("Save");
        fileMenu.add(saveItem);
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(".");
                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        scene.saveToFile(jfc.getSelectedFile().getAbsolutePath());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error saving file: " + ex.getMessage());
                    }
                }
            }
        });

        JMenuItem loadItem = new JMenuItem("Load");
        fileMenu.add(loadItem);
        loadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(".");
                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        scene.loadFromFile(jfc.getSelectedFile().getAbsolutePath());
                        repaint();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error loading file: " + ex.getMessage());
                    }
                }
            }
        });

        JMenuItem undoItem = new JMenuItem("Undo");
        fileMenu.add(undoItem);
        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scene.undo();
                repaint();
            }
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Color menu
        JMenu colorMenu = new JMenu("Color");
        menuBar.add(colorMenu);

        // Add more colors
        Color[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, 
            Color.YELLOW, Color.MAGENTA, Color.CYAN,
            Color.ORANGE, Color.PINK, Color.BLACK
        };
        
        String[] colorNames = {
            "Red", "Blue", "Green", 
            "Yellow", "Magenta", "Cyan",
            "Orange", "Pink", "Black"
        };
        
        for (int i = 0; i < colors.length; i++) {
            final Color c = colors[i];
            JMenuItem colorItem = new JMenuItem(colorNames[i]);
            colorMenu.add(colorItem);
            colorItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    color = c;
                    // Update color of selected shapes
                    for (IShape shape : scene.getSelectedShapes()) {
                        shape.setColor(c);
                    }
                    repaint();
                }
            });
        }
        
        // Shape menu
        JMenu shapeMenu = new JMenu("Shape");
        menuBar.add(shapeMenu);
        
        JMenuItem squareItem = new JMenuItem("Square");
        shapeMenu.add(squareItem);
        squareItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapeType = ShapeType.SQUARE;
            }
        });
        
        JMenuItem circleItem = new JMenuItem("Circle");
        shapeMenu.add(circleItem);
        circleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapeType = ShapeType.CIRCLE;
            }
        });

        JMenuItem rectangleItem = new JMenuItem("Rectangle");
        shapeMenu.add(rectangleItem);
        rectangleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapeType = ShapeType.RECTANGLE;
            }
        });

        // New: Layer menu
        JMenu layerMenu = new JMenu("Layer");
        menuBar.add(layerMenu);

        JMenuItem bringToFrontItem = new JMenuItem("Bring to Front");
        layerMenu.add(bringToFrontItem);
        bringToFrontItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scene.bringToFront(scene.getSelectedShapes());
                repaint();
            }
        });

        JMenuItem sendToBackItem = new JMenuItem("Send to Back");
        layerMenu.add(sendToBackItem);
        sendToBackItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scene.sendToBack(scene.getSelectedShapes());
                repaint();
            }
        });

        // New: Border menu
        JMenu borderMenu = new JMenu("Border");
        menuBar.add(borderMenu);

        JMenuItem solidBorderItem = new JMenuItem("Solid");
        borderMenu.add(solidBorderItem);
        solidBorderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (IShape shape : scene.getSelectedShapes()) {
                    shape.setBorderStyle(BorderStyle.SOLID);
                }
                repaint();
            }
        });

        JMenuItem dashedBorderItem = new JMenuItem("Dashed");
        borderMenu.add(dashedBorderItem);
        dashedBorderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (IShape shape : scene.getSelectedShapes()) {
                    shape.setBorderStyle(BorderStyle.DASHED);
                }
                repaint();
            }
        });

        JMenuItem dottedBorderItem = new JMenuItem("Dotted");
        borderMenu.add(dottedBorderItem);
        dottedBorderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (IShape shape : scene.getSelectedShapes()) {
                    shape.setBorderStyle(BorderStyle.DOTTED);
                }
                repaint();
            }
        });

        this.setJMenuBar(menuBar);
    }
    
    private void initializeKeyListener() {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                List<IShape> selected = scene.getSelectedShapes();
                if (!selected.isEmpty()) {
                    int moveAmount = e.isShiftDown() ? 20 : 5; // Larger movement with Shift
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            for (IShape shape : selected) {
                                if (shape instanceof IMoveableShape) {
                                    ((IMoveableShape) shape).move(-moveAmount, 0);
                                }
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            for (IShape shape : selected) {
                                if (shape instanceof IMoveableShape) {
                                    ((IMoveableShape) shape).move(moveAmount, 0);
                                }
                            }
                            break;
                        case KeyEvent.VK_UP:
                            for (IShape shape : selected) {
                                if (shape instanceof IMoveableShape) {
                                    ((IMoveableShape) shape).move(0, -moveAmount);
                                }
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            for (IShape shape : selected) {
                                if (shape instanceof IMoveableShape) {
                                    ((IMoveableShape) shape).move(0, moveAmount);
                                }
                            }
                            break;
                        case KeyEvent.VK_DELETE:
                            scene.removeSelectedShapes();
                            break;
                        case KeyEvent.VK_PLUS:
                        case KeyEvent.VK_EQUALS:
                            for (IShape shape : selected) {
                                shape.scale(1.1);
                            }
                            break;
                        case KeyEvent.VK_MINUS:
                            for (IShape shape : selected) {
                                shape.scale(0.9);
                            }
                            break;
                        case KeyEvent.VK_G:
                            if (e.isControlDown()) {
                                scene.groupSelectedShapes();
                            }
                            break;
                        case KeyEvent.VK_U:
                            if (e.isControlDown()) {
                                scene.ungroupSelectedShapes();
                            }
                            break;
                        case KeyEvent.VK_OPEN_BRACKET:
                            for (IShape shape : selected) {
                                shape.setOpacity(Math.max(0, shape.getOpacity() - 0.1));
                            }
                            break;
                        case KeyEvent.VK_CLOSE_BRACKET:
                            for (IShape shape : selected) {
                                shape.setOpacity(Math.min(1, shape.getOpacity() + 0.1));
                            }
                            break;
                    }
                    repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}
        };
        shapePanel.addKeyListener(keyListener);
        shapePanel.setFocusable(true);
        shapePanel.requestFocusInWindow();
        
        // Add focus listener to ensure panel keeps focus
        shapePanel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                shapePanel.requestFocusInWindow();
            }
        });
    }
    
    public static void main(String[] args) {
        DrawShapes shapes = new DrawShapes(700, 600);
        shapes.setVisible(true);
    }
}
