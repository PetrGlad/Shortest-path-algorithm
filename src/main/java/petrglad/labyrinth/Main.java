package petrglad.labyrinth;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 5)
        {
            System.err.println("Args expected: wall-lines-file x1 y1 x2 y2");
            System.exit(1);
        }
        Iterable<Line2D> walls = Data.loadLines(new FileReader(args[0]));
        OutputStreamWriter out = new OutputStreamWriter(System.out);
        Data.storeLines(out,
                new Labyrinth(walls).findLinePath(
                        makePoint(args[1], args[2]),
                        makePoint(args[3], args[4])));
        out.flush();
    }

    private static java.awt.geom.Point2D.Double makePoint(String x, String y) {
        return new Point2D.Double(Double.parseDouble(x), Double.parseDouble(y));
    }
}
