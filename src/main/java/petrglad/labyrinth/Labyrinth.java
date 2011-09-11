package petrglad.labyrinth;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Algorithm that finds shortest path through 2d labyrinth. Labyrinth walls are
 * defined by line segments.
 */
public class Labyrinth {

    public static class Step {
        public final Step back;
        public final Point2D point;
        public final double pathLength;

        public Step(Step back, Point2D point) {
            this.back = back;
            this.point = point;
            this.pathLength = back.pathLength + back.point.distance(point);
        }

        public Step(Point2D point) {
            this.back = null;
            this.point = point;
            this.pathLength = 0;
        }
    }

    final Iterable<Line2D> walls;

    public Labyrinth(Iterable<Line2D> walls) {
        this.walls = walls;
    }

    /**
     * Find shortest path from 'from' point to 'to' point given list of walls
     * that can not be crossed.
     * 
     * @return null if no path found or sequence of lines connecting 'from' to
     *         'to'
     */
    public Iterable<Line2D> findLinePath(final Point2D from, final Point2D to) {
        Step result = findPath(from, to);
        if (result == null)
            return null;
        else
            return stepToLines(result);
    }

    private Iterable<Line2D> stepToLines(Step result) {
        final List<Line2D> lines = Lists.newLinkedList();
        Step step = result;
        while (step != null && step.back != null) {
            lines.add(0, new Line2D.Double(step.back.point, step.point));
            step = step.back;
        }
        return lines;
    }

    private Step findPath(final Point2D from, final Point2D to) {
        final Map<Point2D, Step> bestPaths = Maps.newHashMap();
        final Queue<Step> queue = Lists.newLinkedList(); // search breadth-first
        queue.add(new Step(from));
        while (!queue.isEmpty()) {
            final Step here = queue.poll();
            for (final Step nextStep : getWays(here, to)) {
                final Step saved = bestPaths.get(nextStep.point);
                if (saved == null || saved.pathLength > nextStep.pathLength) {
                    bestPaths.put(nextStep.point, nextStep);
                    queue.add(nextStep);
                }
            }
        }
        return bestPaths.get(to);
    }

    /**
     * @param to
     *            required final destination point.
     * @return list of possible lines that can continue path from 'from' point.
     */
    public Iterable<Step> getWays(Step from, Point2D to) {
        final Collection<Step> ways = Lists.newArrayList();
        if (allowedPathSegment(from.point, to, walls))
            ways.add(new Step(from, to));
        for (Line2D wall : walls) {
            if (allowedPathSegment(from.point, wall.getP1(), walls))
                ways.add(new Step(from, wall.getP1()));
            if (allowedPathSegment(from.point, wall.getP2(), walls))
                ways.add(new Step(from, wall.getP2()));
        }
        return ways;
    }

    /**
     * @return true if line [from,to] does not intersect a wall
     */
    private boolean allowedPathSegment(Point2D from, Point2D to, Iterable<Line2D> walls) {
        Line2D.Double segment = new Line2D.Double(from, to);
        for (Line2D wall : walls)
            if (wall.intersectsLine(segment)
                    // Not on a wall's end:
                    && !wall.getP1().equals(from) && !wall.getP1().equals(to)
                    && !wall.getP2().equals(from) && !wall.getP2().equals(to))
                return false;
        return true;
    }
}
