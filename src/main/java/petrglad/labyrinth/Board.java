package petrglad.labyrinth;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Board extends JComponent {

    private static final String DEFAULT_WALLS_FILE = "saved-walls.txt";

    private static final long serialVersionUID = 1L;

    public static final Color WALL_COLOR = Color.BLACK;
    public static final Color PATH_COLOR = Color.GREEN;

    final Map<Color, Collection<Shape>> shapes = new HashMap<Color, Collection<Shape>>();

    private Collection<Line2D> walls;

    final JFrame mainFrame;

    public Board(JFrame mainFrame, Iterable<Line2D> walls) {
        this.mainFrame = mainFrame;
        this.walls = Lists.newArrayList(walls);
        clean();
    }

    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }

    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (Color c : shapes.keySet()) {
            g2.setColor(c);
            for (Shape s : shapes.get(c))
                g2.draw(s);
        }
    }

    public void addShapes(Iterable<? extends Shape> newShapes, Color color) {
        Collection<Shape> c = shapes.get(color);
        if (c == null) {
            c = new ArrayList<Shape>();
            shapes.put(color, c);
        }
        Iterables.addAll(c, newShapes);
        repaint();
    }

    protected void clean() {
        shapes.clear();
        addShapes(walls, WALL_COLOR);
    }

    protected void addWall(Line2D wall) {
        walls.add(wall);
        clean();
    }

    private void addSolution(Point2D fromPoint, Point2D toPoint) {
        final Iterable<Line2D> path = new Labyrinth(walls).findLinePath(fromPoint, toPoint);
        if (path == null) {
            mainFrame.setTitle("No path");
            addShapes(Lists.newArrayList(new Line2D.Double(fromPoint, toPoint)), Color.RED);
        } else
            addShapes(path, PATH_COLOR);
    }

    public static void main(String args[]) throws IOException {
        final JFrame mainFrame = new JFrame("Shortest path");

        File dataFile = new File(args.length > 0 ? args[0] : DEFAULT_WALLS_FILE);
        final Board board = new Board(mainFrame,
                dataFile.isFile() ?
                        Data.loadLines(new FileReader(dataFile))
                        : new LinkedList<Line2D>());

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(board);
        mainFrame.pack();
        addListeners(mainFrame, board);
        mainFrame.setVisible(true);
    }

    private static void addListeners(final JFrame mainFrame, final Board board) {
        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (KeyEvent.VK_S == e.getKeyCode() && e.isControlDown()) {
                    try {
                        FileWriter out = new FileWriter(DEFAULT_WALLS_FILE);
                        try {
                            Data.storeLines(out, board.walls);
                        } finally {
                            out.close();
                        }
                    } catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        board.addMouseListener(new MouseListener() {

            Point2D fromPoint;
            Point2D toPoint;
            Point2D wallBeginPoint;

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                if (e.isControlDown()) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (wallBeginPoint == null)
                            wallBeginPoint = point;
                        else {
                            board.addWall(new Line2D.Double(wallBeginPoint, point));
                            wallBeginPoint = null;
                        }
                    }
                } else {
                    if (e.getButton() == MouseEvent.BUTTON1)
                        fromPoint = point;
                    else if (e.getButton() == MouseEvent.BUTTON3)
                        toPoint = point;
                    else if (e.getButton() == MouseEvent.BUTTON2) {
                        fromPoint = null;
                        toPoint = null;
                        wallBeginPoint = null;
                        board.clean();
                    }
                    if (fromPoint != null && toPoint != null)
                        board.addSolution(fromPoint, toPoint);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
    }
}
