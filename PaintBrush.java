import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.File;
import javax.imageio.ImageIO;






// Abstract Shape class
abstract class Shape {
    protected int x1, y1, x2, y2;
    protected Color color;
    protected Stroke stroke;
    protected boolean filled;
    protected boolean isEraser;

    public Shape(int x1, int y1, int x2, int y2, Color color, Stroke stroke, boolean filled, boolean isEraser) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.stroke = stroke;
        this.filled = filled;
        this.isEraser = isEraser;
    }

    public abstract void draw(Graphics2D g2d);
    public abstract boolean contains(Point p);



    // GETTERS & SETTERS
    public Color getColor() { return color; }
    public Stroke getStroke() { return stroke; }
    public boolean isFilled() { return filled; }
    public boolean isEraser() { return isEraser; }
}




//LINE
class MyLine extends Shape {
    public MyLine(int x1, int y1, int x2, int y2, Color color, Stroke stroke, boolean isEraser) {
        super(x1, y1, x2, y2, color, stroke, false, isEraser);
    }

    
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(stroke);
        g2d.drawLine(x1, y1, x2, y2);
    }

    
    public boolean contains(Point p) {
        return new Line2D.Double(x1, y1, x2, y2).intersects(p.x, p.y, 2, 2);
    }
}







//RECTANGLE
class MyRectangle extends Shape {
    public MyRectangle(int x1, int y1, int x2, int y2, Color color, Stroke stroke, boolean filled, boolean isEraser) {
        super(x1, y1, x2, y2, color, stroke, filled, isEraser);
    }

    
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(stroke);
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);
        
        if (filled) g2d.fillRect(x, y, width, height);
        else g2d.drawRect(x, y, width, height);
    }

    
    public boolean contains(Point p) {
        Rectangle2D rect = new Rectangle2D.Double(
            Math.min(x1, x2), Math.min(y1, y2),
            Math.abs(x1 - x2), Math.abs(y1 - y2)
        );
        return rect.contains(p);
    }
}








//OVAL
class MyOval extends Shape {
    public MyOval(int x1, int y1, int x2, int y2, Color color, Stroke stroke, boolean filled, boolean isEraser) {
        super(x1, y1, x2, y2, color, stroke, filled, isEraser);
    }

    
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(stroke);
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);
        
        if (filled) g2d.fillOval(x, y, width, height);
        else g2d.drawOval(x, y, width, height);
    }

    
    public boolean contains(Point p) {
        Ellipse2D oval = new Ellipse2D.Double(
            Math.min(x1, x2), Math.min(y1, y2),
            Math.abs(x1 - x2), Math.abs(y1 - y2)
        );
        return oval.contains(p);
    }
}

class ShapeInfo {
    Shape shape;
    BufferedImage image;

    public ShapeInfo(Shape shape) {
        this.shape = shape;
        this.image = null;
    }

    public ShapeInfo(BufferedImage image) {
        this.image = image;
        this.shape = null;
    }
}




//DRAWWING
class DrawingPanel extends JPanel {
    private ArrayList<ShapeInfo> shapes = new ArrayList<>();
    private ArrayList<ShapeInfo> undone = new ArrayList<>();
    private Color currentColor = Color.BLACK;
    private String currentTool = "Line";
    private Point startPoint;
    private boolean filled = false;
    private boolean dotted = false;
    private float strokeWidth = 2.0f;

    public void setCurrentColor(Color color) {
        currentColor = color;
        repaint();
    }

    public void setCurrentTool(String tool) {
        currentTool = tool;
        repaint();
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
        repaint();
    }

    public void setDotted(boolean dotted) {
        this.dotted = dotted;
        repaint();
    }

    public void clearAll() {
        shapes.clear();
        undone.clear();
        repaint();
    }

    public void undo() {
        if (!shapes.isEmpty()) {
            undone.add(shapes.remove(shapes.size() - 1));
            repaint();
        }
    }






//SAVE
    public void saveImage() {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            paintAll(g2d);
            try {
                File file = fc.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getParentFile(), file.getName() + ".png");
                }
                ImageIO.write(image, "PNG", file);
                JOptionPane.showMessageDialog(this, "Image saved successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage());
            } finally {
                g2d.dispose();
            }
        }
    }





//OPEN
    public void openImage() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage image = ImageIO.read(fc.getSelectedFile());
                if (image != null) {
                    shapes.clear();
                    undone.clear();
                    shapes.add(new ShapeInfo(image));
                    repaint();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error opening image: " + e.getMessage());
            }
        }
    }

    public DrawingPanel() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                if (startPoint != null && !currentTool.equals("Pencil") && !currentTool.equals("Eraser")) {
                    Point endPoint = e.getPoint();
                    if (endPoint != null) {
                        Shape shape = createShape(endPoint);
                        if (shape != null) {
                            shapes.add(new ShapeInfo(shape));
                        }
                    }
                    startPoint = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (startPoint == null) return;

                if (currentTool.equals("Pencil") || currentTool.equals("Eraser")) {
                    Point currentPoint = e.getPoint();
                    if (currentPoint != null) {
                        shapes.add(new ShapeInfo(createShape(currentPoint)));
                        startPoint = currentPoint;
                        repaint();
                    }
                } else {
                    repaint();
                }
            }
        });
    }





//SHAPES
    private Shape createShape(Point endPoint) {
        if (startPoint == null || endPoint == null) return null;

        Stroke stroke = dotted ? 
            new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                1.0f, new float[]{10f, 5f}, 0f) : 
            new BasicStroke(strokeWidth);

        boolean actuallyFilled = filled && (currentTool.equals("Rectangle") || currentTool.equals("Oval"));
        boolean isEraser = currentTool.equals("Eraser");

        switch (currentTool) {
            case "Rectangle":
                return new MyRectangle(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                    isEraser ? getBackground() : currentColor, stroke, actuallyFilled, isEraser);
            case "Oval":
                return new MyOval(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                    isEraser ? getBackground() : currentColor, stroke, actuallyFilled, isEraser);
            case "Line":
            case "Eraser":
                return new MyLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                    isEraser ? getBackground() : currentColor, stroke, isEraser);
            case "Pencil":
                return new MyLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                    currentColor, new BasicStroke(strokeWidth), false);
            default:
                return null;
        }
    }




    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (ShapeInfo shapeInfo : shapes) {
            if (shapeInfo.image != null) {
                g2d.drawImage(shapeInfo.image, 0, 0, null);
            } else if (shapeInfo.shape != null) {
                shapeInfo.shape.draw(g2d);
            }
        }

        if (startPoint != null && !currentTool.equals("Pencil") && !currentTool.equals("Eraser")) {
            Point currentPoint = getMousePosition();
            if (currentPoint == null) currentPoint = startPoint;
            
            Shape preview = createShape(currentPoint);
            if (preview != null) {
                preview.draw(g2d);
            }
        }
    }
}





public class PaintBrush {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Paint Brush");
        DrawingPanel drawingPanel = new DrawingPanel();
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));






        // COLOR
        JPanel colorPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        colorPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
        colorPanel.setBackground(new Color(245, 245, 245));
        String[] colors = {"Red", "Green", "Blue"};
        for (String color : colors) {
            JButton btn = new JButton(color);
            btn.setBackground(getColor(color));
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.addActionListener(e -> drawingPanel.setCurrentColor(getColor(color)));
            colorPanel.add(btn);
        }







        // TOOLS
        JPanel toolsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Tools"));
        toolsPanel.setBackground(new Color(245, 245, 245));
        String[] tools = {"Rectangle", "Oval", "Line", "Pencil", "Eraser"};
        for (String tool : tools) {
            JButton btn = new JButton(tool);
            btn.setBackground(tool.equals("Eraser") ? new Color(220, 220, 220) : new Color(240, 240, 240));
            btn.addActionListener(e -> drawingPanel.setCurrentTool(tool));
            toolsPanel.add(btn);
        }







        // OPTIONS
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        optionsPanel.setBackground(new Color(245, 245, 245));
        JCheckBox dottedCheck = new JCheckBox("Dotted");
        JCheckBox filledCheck = new JCheckBox("Filled");
        
        ItemListener checkBoxListener = e -> {
            JCheckBox source = (JCheckBox) e.getSource();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (source == dottedCheck) {
                    filledCheck.setSelected(false);
                    drawingPanel.setFilled(false);
                } else {
                    dottedCheck.setSelected(false);
                    drawingPanel.setDotted(false);
                }
            }
        };
        
        dottedCheck.addItemListener(checkBoxListener);
        filledCheck.addItemListener(checkBoxListener);
        dottedCheck.addItemListener(e -> drawingPanel.setDotted(e.getStateChange() == ItemEvent.SELECTED));
        filledCheck.addItemListener(e -> drawingPanel.setFilled(e.getStateChange() == ItemEvent.SELECTED));
        optionsPanel.add(dottedCheck);
        optionsPanel.add(filledCheck);

        // FILE
        JPanel filePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        filePanel.setBorder(BorderFactory.createTitledBorder("File"));
        filePanel.setBackground(new Color(245, 245, 245));
        JButton saveBtn = new JButton("💾  Save");
        JButton openBtn = new JButton("📂  Open");
        JButton clearBtn = new JButton("❌  Clear");
        JButton undoBtn = new JButton("↩️  Undo");
        
        saveBtn.addActionListener(e -> drawingPanel.saveImage());
        openBtn.addActionListener(e -> drawingPanel.openImage());
        clearBtn.addActionListener(e -> drawingPanel.clearAll());
        undoBtn.addActionListener(e -> drawingPanel.undo());
        
        filePanel.add(openBtn);
        filePanel.add(saveBtn);
        filePanel.add(clearBtn);
        filePanel.add(undoBtn);

        controlPanel.add(colorPanel);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(toolsPanel);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(optionsPanel);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(filePanel);

        frame.setLayout(new BorderLayout());
        frame.add(controlPanel, BorderLayout.WEST);
        frame.add(drawingPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setVisible(true);
    }

    private static Color getColor(String name) {
        switch (name) {
            case "Red": return new Color(200, 50, 50);
            case "Green": return new Color(50, 150, 50);
            case "Blue": return new Color(50, 100, 200);
            default: return Color.BLACK;
        }
    }
}
