/* *****************************************************************************
 *  Name: Adam Kinsey
 *  Date: 4 January 2022
 *  Description: Brute CollinearPoints
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;

public class BruteCollinearPoints {

    private Point[] points;
    private int numSegments;
    private LineSegment[] lineSegments;
    private Point[][] lineEndpoints;
    private Point[][] colinPoints;

    // ----------------------------------------
    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {

        numSegments = 0;
        this.points = points;

        if (points == null) {
            String msg = "No input points were provided.";
            throw new IllegalArgumentException(msg);
        }

        // Verify that none of "points" are null
        for (Point pt : points) {
            if (pt == null) {
                String msg = "\"points\" contains a null value.";
                throw new IllegalArgumentException(msg);
            }
        }

        if (points.length < 4) {
            String msg = "Less than 4 input points were provided.";
            StdOut.println(msg);
            return;
        }


        // long numCombsIter = 0;
        double slopeIJ, slopeIK, slopeIM;

        // Select all unique combinations of 3 points
        for (int i = 0; i < points.length - 3; i++) {

            for (int j = i + 1; j < points.length - 2; j++) {

                slopeIJ = points[i].slopeTo(points[j]);

                for (int k = j + 1; k < points.length - 1; k++) {

                    slopeIK = points[i].slopeTo(points[k]);

                    if (slopeIJ == Double.NEGATIVE_INFINITY
                            || slopeIK == Double.NEGATIVE_INFINITY) {
                        throw new IllegalArgumentException();
                    }

                    // if first two line segments are collinear
                    if (slopeIJ == slopeIK) {

                        // Continue to try to find a final point that is collinear
                        for (int m = k + 1; m < points.length; m++) {

                            slopeIM = points[i].slopeTo(points[m]);
                            if (slopeIM == Double.NEGATIVE_INFINITY) {
                                throw new IllegalArgumentException();
                            }

                            if (slopeIJ == slopeIK && slopeIJ == slopeIM) {

                                int[] indxs = { i, j, k, m };
                                Point[] newColinPoints = findEndpoints(points, indxs);

                                if (!alreadyFound(newColinPoints)) {

                                    // DEBUG
                                    // StdOut.print("\n  New Segment: " + newColinPoints[0] + " --> "
                                    //                      + newColinPoints[3]);

                                    addSegment(newColinPoints);
                                }
                            }
                            // numCombsIter++;
                        }
                    }
                    // if first two line segments NOT collinear, continue
                }
            }
        }

        // DEBUG
        // StdOut.print("\n\nBruteCollinearPoints Combinations Checked: " + numCombsIter);

    }


    // ----------------------------------------
    /* Utility function to find the ENDPOINTS of a line segment

     * Input 1: Point Array of all points
     * Input 2: Size 4 Array containing indeces to Point Array (above).
     *     This specifies the 4 particular points to evaluate
     * Output: Point Array containing the two endpoints.
     */
    private Point[] findEndpoints(Point[] pts, int[] indxs) {

        Point[] fourPoints = { pts[indxs[0]], pts[indxs[1]], pts[indxs[2]], pts[indxs[3]] };

        // DEBUG Print
        // StdOut.print("\n fourPoints before: ");
        // for (int idx = 0; idx < fourPoints.length; idx++) {
        //     StdOut.print(fourPoints[idx]);
        // }

        int isSmaller;

        // Insertion Sort points into order using "compareTo()" logic
        for (int idx = 0; idx < 4; idx++) {
            for (int idx2 = idx; idx2 > 0; idx2--) {

                isSmaller = fourPoints[idx2].compareTo(fourPoints[idx2 - 1]);

                // DEBUG
                // StdOut.print("\n " + fourPoints[idx2] + ".compareTo(" + fourPoints[idx2 - 1] + " = "
                //                      + isSmaller);

                // If current item is smaller than left, swap with left
                if (isSmaller < 0) {
                    exch(fourPoints, idx2, idx2 - 1);

                    // DEBUG
                    // StdOut.print("\n");
                    // for (Point pt : fourPoints) {
                    //     StdOut.print(pt);
                    //}

                }
                else {  // Then this item is in the proper position.
                    break;
                }
            }
        }
        assert pointsAreSorted(fourPoints);

        // StdOut.print("\n fourPoints after: ");
        // for (int idx = 0; idx < fourPoints.length; idx++) {
        //     StdOut.print(fourPoints[idx]);
        // }
        return fourPoints;
    }


    // ----------------------------------------
    /* Utility function to verify that an Point Array is sorted ASCENDING

     * Input 1: Point Array
     * Output: TRUE if Array is sorted ascending, false otherwise
     */
    private boolean pointsAreSorted(Point[] pts) {

        boolean isSorted = true;
        int rightOrder;

        for (int i = 0; i < pts.length - 1; i++) {
            rightOrder = pts[i].compareTo(pts[i + 1]);
            if (rightOrder > 0) {
                isSorted = false;
                break;
            }
        }
        return isSorted;
    }


    // ----------------------------------------
    /* Utility function to SWAP two points in a Point Array
     * Input: Point Array, and two indeces in that array
     */
    private void exch(Point[] pts, int i, int j) {
        Point copy = pts[i];
        pts[i] = pts[j];
        pts[j] = copy;
    }


    // ----------------------------------------
    /* Utility function see if a line segment has previously bee identified
     * Input: Point Array containing endpoints of a line segment
     * Output: TRUE if input is already present in line segment array, FALSE otherwise
     */
    private boolean alreadyFound(Point[] newColinPoints) {

        // DEBUG
        // StdOut.print("\n are these alreadyFound?: newColinPoints = ");
        // for (Point pt : newColinPoints) {
        //     StdOut.print(pt.toString());
        // }

        if (numSegments == 0) {
            return false;
        }
        boolean exactMatch = false;
        boolean flippedMatch = false;

        for (int i = 0; i < numSegments; i++) {

            exactMatch = (newColinPoints[0].compareTo(lineEndpoints[i][0]) == 0 &&
                    newColinPoints[3].compareTo(lineEndpoints[i][1]) == 0);
            flippedMatch = (newColinPoints[3].compareTo(lineEndpoints[i][0]) == 0 &&
                    newColinPoints[0].compareTo(lineEndpoints[i][1]) == 0);

            if (exactMatch || flippedMatch) {
                break;
            }
        }

        return (exactMatch || flippedMatch);
    }


    // ----------------------------------------
    /* Utility function add a newly found line segment to array
     * Input: Point Array containing endpoints of a new line segment
     */
    private void addSegment(Point[] newColinPoints) {

        // If no lineSegments are yet present
        if (this.numSegments == 0) {

            // Add to local endpoints array
            lineEndpoints = new Point[1][2];
            lineEndpoints[0][0] = newColinPoints[0];
            lineEndpoints[0][1] = newColinPoints[3];

            colinPoints = new Point[1][4];
            colinPoints[0] = newColinPoints;

            // Add to lineSegments "official" array
            lineSegments = new LineSegment[1];
            lineSegments[0] = new LineSegment(newColinPoints[0], newColinPoints[3]);
            this.numSegments++;
            return;
        }

        if (lineEndpoints.length != colinPoints.length
                || lineEndpoints.length != lineSegments.length) {
            throw new IllegalStateException("Unknown Error: Class arrays not same length.");
        }

        // Otherwise, add it to the local endpoints array and lineSegments "official" array
        int arraySize = lineEndpoints.length;

        // If no space remains, dynamically reallocate arrays
        if (this.numSegments + 1 > arraySize) {
            Point[][] oldLineEndpoints = lineEndpoints;
            lineEndpoints = new Point[arraySize * 2][2];

            Point[][] oldColinPoints = colinPoints;
            colinPoints = new Point[arraySize * 2][4];

            LineSegment[] oldLineSegments = lineSegments;
            lineSegments = new LineSegment[arraySize * 2];

            // Copy old lineSegments into new array
            System.arraycopy(oldLineSegments, 0, lineSegments, 0, numSegments);
            System.arraycopy(oldColinPoints, 0, colinPoints, 0, numSegments);
            System.arraycopy(oldLineEndpoints, 0, lineEndpoints, 0, numSegments);
        }

        // Append newly found line segment
        this.numSegments++;
        lineEndpoints[numSegments - 1][0] = newColinPoints[0];
        lineEndpoints[numSegments - 1][1] = newColinPoints[3];
        colinPoints[numSegments - 1] = newColinPoints;
        lineSegments[numSegments - 1] = new LineSegment(newColinPoints[0], newColinPoints[3]);
    }


    private static Point[] readPoints(String filename) {

        if (filename == null) {
            throw new IllegalArgumentException("No input filename was provided.");
        }

        In in = new In(filename);

        if (in.isEmpty()) {
            throw new IllegalArgumentException("Input file is empty.");
        }

        int size = in.readInt();
        Point[] pts = new Point[size];
        int x, y;

        // DEBUG
        // StdOut.print("\nReading " + size + " Points: ");

        for (int i = 0; i < size; i++) {

            if (!in.isEmpty()) x = in.readInt();
            else throw new IllegalArgumentException("File contains error.");

            if (!in.isEmpty()) y = in.readInt();
            else throw new IllegalArgumentException("File contains error.");

            pts[i] = new Point(x, y);

            // DEBUG
            // StdOut.print(pts[i]);
        }

        return pts;
    }


    // the number of line segments
    public int numberOfSegments() {
        return numSegments;
    }

    // returns the line segments found in the input
    public LineSegment[] segments() {

        LineSegment[] toReturn = new LineSegment[numSegments];
        if (numSegments > 0) System.arraycopy(lineSegments, 0, toReturn, 0, numSegments);
        return toReturn;
    }

    // ----------------------------------------
    /* Utility method to draw Points from 2D Array
     * Output: Drawing of points
     */
    private static void drawPoints(Point[][] points) {

        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);

        for (Point[] pts : points) {
            for (Point pt : pts) {
                pt.draw();
                String s = pt.toString().replaceAll("[(),]", "");
                String[] s2 = s.split(" ");
                // StdOut.print("\nNum Strs: x=\"" + s2[0] + "\"  y=\"");
                // StdOut.print(s2[1] + "\"");
                StdDraw.textLeft(Double.parseDouble(s2[0]), Double.parseDouble(s2[1]),
                                 pt.toString());
            }
        }
    }


    // ----------------------------------------
    /* Utility method to draw Points from 1D Array
     * Output: Drawing of points
     */
    private static void drawPoints(Point[] points) {

        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);

        for (Point pt : points) {
            pt.draw();
            String s = pt.toString().replaceAll("[(),]", "");
            String[] s2 = s.split(" ");
            // StdOut.print("\nNum Strs: x=\"" + s2[0] + "\"  y=\"");
            // StdOut.print(s2[1] + "\"");
            StdDraw.textLeft(Double.parseDouble(s2[0]), Double.parseDouble(s2[1]),
                             pt.toString());
        }
    }

    private static void drawLines(LineSegment[] lines, int numSegments) {

        StdDraw.setPenRadius(0.004);
        Color[] colors = {
                StdDraw.BOOK_BLUE, StdDraw.BOOK_LIGHT_BLUE, StdDraw.BOOK_RED,
                StdDraw.CYAN, StdDraw.GREEN, StdDraw.MAGENTA, StdDraw.ORANGE, StdDraw.PINK,
                StdDraw.PRINCETON_ORANGE, StdDraw.YELLOW
        };

        // Draw each line, changing color with "i" each time
        int i = 0;
        for (int j = 0; j < numSegments; j++) {
            StdDraw.setPenColor(colors[i++]);
            lines[j].draw();
            i %= 10;
        }
    }

    private static double[] findLimits(Point[] points) {

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        int x, y;

        for (Point pt : points) {
            String s = pt.toString().replaceAll("[(),]", "");
            String[] s2 = s.split(" ");
            x = Integer.parseInt(s2[0]);
            y = Integer.parseInt(s2[1]);

            if (x > maxX) {
                maxX = x;
            }
            if (x < minX) {
                minX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
            if (y < minY) {
                minY = y;
            }
        }

        double[] toReturn = { minX, maxX, minY, maxY };
        return toReturn;
    }

    private static long combo(int n, int r) {
        long result1 = 1, result2 = 1;
        for (int factor = n; factor > (n - r); factor--) {
            result1 *= factor;
        }
        for (int factor = r; factor > 0; factor--) {
            result2 *= factor;
        }
        return result1 / result2;
    }

    private static void testComboCalc(int arrSizeMin, int arrSizeMax, int interval, int choose) {

        for (int arrSize = arrSizeMin; arrSize <= arrSizeMax; arrSize += interval) {

            long numIter2 = 0;

            for (int i = 0; i < arrSize - 3; i++) {
                for (int j = i + 1; j < arrSize - 2; j++) {
                    for (int k = j + 1; k < arrSize - 1; k++) {
                        for (int m = k + 1; m < arrSize; m++) {
                            numIter2++;
                        }
                    }
                }
            }
            long combo1 = combo(arrSize, choose);
            StdOut.print(
                    "\narrSize = " + arrSize + "  numIter = " + numIter2 + "  Combos = " + combo1 +
                            "  Match = " + (numIter2 == combo1));
        }
    }


    public static void main(String[] args) {

        String filename = null;

        if (args.length == 1) filename = args[0];
        else StdOut.print("Please input only a single command-line argument.");

        BruteCollinearPoints b = new BruteCollinearPoints(readPoints(filename));
        LineSegment[] segs = b.segments();

        StdOut.print("\nNumber of Segments = " + b.numSegments);

        // Print line segments and their constituent points
        StdOut.print("\n\nLine Segments: ");
        for (int i = 0; i < b.numSegments; i++) {

            StdOut.print("\n  " + (i + 1) + ": " + segs[i]);
            // StdOut.print("    Points:");
            // for (int j = 0; j < b.colinPoints[i].length; j++) {
            //     StdOut.print("  " + b.colinPoints[i][j]);
            // }
        }
        StdOut.print("\n");


        double[] limits = findLimits(b.points);
        double delX, delY;
        if (limits[1] != limits[0]) delX = limits[1] - limits[0];
        else delX = 10.0;

        if (limits[3] != limits[2]) delY = limits[3] - limits[2];
        else delY = 10.0;

        double minX = limits[0] - 0.2 * delX;
        double maxX = limits[1] + 0.2 * delX;
        double minY = limits[2] - 0.2 * delY;
        double maxY = limits[3] + 0.2 * delY;

        // Setup Drawing Canvas
        StdDraw.setCanvasSize(1024, 1024);
        StdDraw.setXscale(minX, maxX);
        StdDraw.setYscale(minY, maxY);
        StdDraw.enableDoubleBuffering();

        // Draw Axes
        StdDraw.setPenRadius(0.001);
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        StdDraw.line(minX, 0.0, maxX, 0);
        StdDraw.line(0.0, minY, 0.0, maxY);


        drawPoints(b.points);

        drawLines(b.lineSegments, b.numSegments);
        StdDraw.show();

    }


}
