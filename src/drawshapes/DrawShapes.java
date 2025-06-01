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
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { 
                    if (shapeType == ShapeType.SQUARE) {
                        scene.addShape(new Square(color, e.getX(), e.getY(), 100));
                    } else if (shapeType == ShapeType.CIRCLE) {
                        scene.addShape(new Circle(color, e.getPoint(), 100));
                    } else if (shapeType == ShapeType.RECTANGLE) {
                        scene.addShape(new Rectangle(e.getPoint(), 100, 200, color));
                    }
                    repaint();
                }
            }
        };
        shapePanel.addMouseListener(a);
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

        JMenuItem redColorItem = new JMenuItem("Red");
        colorMenu.add(redColorItem);
        redColorItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                color = Color.RED;
            }
        });
        
        JMenuItem blueColorItem = new JMenuItem("Blue");
        colorMenu.add(blueColorItem);
        blueColorItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                color = Color.BLUE;
            }
        });

        JMenuItem greenColorItem = new JMenuItem("Green");
        colorMenu.add(greenColorItem);
        greenColorItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                color = Color.GREEN;
            }
        });
        
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

        this.setJMenuBar(menuBar);
    }
    
    private void initializeKeyListener() {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                List<IShape> selected = scene.getSelectedShapes();
                if (!selected.isEmpty()) {
                    int moveAmount = 5;
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
    }
    
    public static void main(String[] args) {
        DrawShapes shapes = new DrawShapes(700, 600);
        shapes.setVisible(true);
    }
}
