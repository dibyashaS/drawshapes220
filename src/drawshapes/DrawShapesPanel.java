package drawshapes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * The Panel owned by the DrawShapes frame.  This code
 * doesn't need to be changed.
 * 
 * @author jspacco
 *
 */
@SuppressWarnings("serial")
public class DrawShapesPanel extends JPanel
{
    private int width;
    private int height;
    private Scene scene;
    
    public DrawShapesPanel(int width, int height, Scene scene)
    {
        this.width = width;
        this.height = height;
        this.scene = scene;
        setFocusable(true);
        requestFocusInWindow();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        scene.draw(g2d);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getMaximumSize()
     */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(width, height);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
    
    /* (non-Javadoc)
     * @see java.awt.Component#isFocusable()
     */
    @Override
    public boolean isFocusable() {
        return true;
    }
}
