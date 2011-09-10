package petrglad.labyrinth;

import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class Data {

    public static final Iterable<Line2D> loadLines(Reader reader) {
        final BufferedReader in = new BufferedReader(reader);
        final Collection<Line2D> result = Lists.newArrayList();
        final Splitter splitter = Splitter.onPattern("\\s*,\\s*");
        try {
            while (true) {
                String ln = in.readLine();
                if (ln == null)
                    break;
                Iterator<String> fields = splitter.split(ln).iterator();
                result.add(new Line2D.Double(
                        getNextValue(fields), getNextValue(fields),
                        getNextValue(fields), getNextValue(fields)));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static double getNextValue(Iterator<String> fields) {
        return Double.parseDouble(fields.next());
    }

    public static void storeLines(Writer out, Collection<Line2D> walls) {
        PrintWriter printWriter = new PrintWriter(out);
        for (Line2D line : walls) {
            printWriter.println(line.getX1() + ", " + line.getY1()
                    + ", " + line.getX2() + ", " + line.getY2());
        }        
    }
}
