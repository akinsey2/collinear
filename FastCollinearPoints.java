/* *****************************************************************************
 *  Name: Adam Kinsey
 *  Date: 4 January 2022
 *  Description: FastCollinearPoints
 **************************************************************************** */


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
public class FastCollinearPoints {

    private int numSegments;
    private Point[] points;
    private ColinPoints[] colinPoints;


    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {

        numSegments = 0;
        this.points = points;
        
        // Input validation
        if (points == null) {
            throw new IllegalArgumentException("Points array is null");
        }

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("Points array is null at index " + i);
            }
        }

        // Sort points according to "Natural Order"
        sort(points, 0, points.length - 1);

        int n = points.length;
        PointSlopes[] pointSlopes = new PointSlopes[n];
        double slope;
        int count = 2;
        int startIdx = 0, endIdx;
        boolean sameSlopes;

        // With each point as "origin"
        for (int i = 0; i < n; i++) {

            // DEBUG
            // StdOut.print("\n \"Origin\" Point: " + points[i]);

            // Calculate slope with all remaining points and save
            for (int j = i + 1; j < n; j++) {

                slope = points[i].slopeTo(points[j]);

                // DEBUG
                // StdOut.print("\n slopeTo( " + points[j] + " ) = " + slope);

                if (j != i && slope == Double.NEGATIVE_INFINITY) {
                    throw new IllegalArgumentException("Input points contain repeated points.");
                }
                pointSlopes[j] = new PointSlopes(points[j], slope);
            }

            // Sort points BY SLOPES for this iteration
            sort(pointSlopes, i + 1, n - 1);

            // DEBUG
            // for (int s = 0; s < pointSlopes.length; s++) {
            //     StdOut.print("\n pointSlopes[" + s + "].point = " + pointSlopes[s].toPoint
            //                          + "  .slope = " + pointSlopes[s].slope);
            // }


            // Find all unique groups of 4+ collinear points with points[i] for this iteration
            for (int k = i + 2; k < n; k++) {

                // Count groups of points with equal slope

                // DEBUG
                // StdOut.print("\n [" + (k - 1) + "].toPoint = " + pointSlopes[k - 1].toPoint +
                //                      " [" + (k - 1) + "].slope = " + pointSlopes[k - 1].slope +
                //                      " [" + k + "].toPoint" + pointSlopes[k].toPoint +
                //                      " [" + k + "].slope = " + pointSlopes[k].slope);

                // KEY DISCOVERY: If next point from origin is LESS than origin, segment is not unique.
                if (points[i].compareTo(pointSlopes[k - 1].toPoint) > 0) {
                    count = 2;
                    continue;
                }

                sameSlopes = pointSlopes[k].slope.equals(pointSlopes[k - 1].slope);

                // If NO LONGER a matching set or end of array
                if (!sameSlopes || k == n - 1) {

                    if (sameSlopes) count++;

                    // If a qualifying group has been found
                    if (count >= 4) {

                        // Record the end of the matching group
                        if (!sameSlopes) endIdx = k - 1;
                        else endIdx = k;

                        // DEBUG
                        // StdOut.print("    End Idx = " + endIdx);
                        // StdOut.print("\n Found newColinPoints group. ");

                        // create new colinPoints array
                        Point[] newColinPoints = new Point[endIdx - startIdx + 2];
                        newColinPoints[0] = points[i];
                        int idx2 = 1;
                        for (int m = startIdx; m <= endIdx; m++) {
                            newColinPoints[idx2++] = pointSlopes[m].toPoint;
                        }

                        // If this is a new segment, save it
                        double newSlope = newColinPoints[0].slopeTo(newColinPoints[1]);
                        if (!segAlreadySaved(newColinPoints, newSlope)) {
                            addColinPoints(newColinPoints,
                                           newColinPoints[0].slopeTo(newColinPoints[1]));
                        }
                        count = 2;
                    }
                    // If no qualifying group has been found
                    else {
                        count = 2;
                    }
                    // --------------------------------------------------
                }
                // If two segments ARE collinear
                else {
                    // If this is the FIRST matching set
                    if (count == 2) {
                        startIdx = k - 1;   // Record start of set
                        // StdOut.print("    Start Idx = " + startIdx);
                    }
                    count++;
                    // continue;


                }
            }
        }
    }

    private class PointSlopes implements Comparable<PointSlopes> {

        private Point toPoint;
        private Double slope;

        public PointSlopes(Point toPoint, Double slope) {
            this.toPoint = toPoint;
            this.slope = slope;
        }

        public int compareTo(PointSlopes other) {
            return this.slope.compareTo(other.slope);
        }
    }

    private class ColinPoints {

        private Point[] points;
        private double slope;

        public ColinPoints(Point[] points, Double slope) {
            this.points = points;
            this.slope = slope;
        }
    }

    private void addColinPoints(Point[] newColinPoints, double slope) {

        assert (!segAlreadySaved(newColinPoints, slope));

        // If this is the first segment found
        if (numSegments == 0) {

            sort(newColinPoints, 0, newColinPoints.length - 1);
            colinPoints = new ColinPoints[1];
            colinPoints[0] = new ColinPoints(newColinPoints, slope);
            numSegments++;
            // StdOut.print(" Added. numSegments = " + numSegments);
            return;
        }

        // Otherwise, add
        sort(newColinPoints, 0, newColinPoints.length - 1);
        int arraySize = colinPoints.length;

        // If there is space in colinPoints
        if (numSegments < colinPoints.length) {
            colinPoints[numSegments] = new ColinPoints(newColinPoints, slope);
        }
        // If there is not space in colinPoints, dynamically resize, and add new
        else {
            ColinPoints[] oldColinPoints = colinPoints;
            colinPoints = new ColinPoints[arraySize * 2];
            System.arraycopy(oldColinPoints, 0, colinPoints, 0, arraySize);
            // Add new
            colinPoints[numSegments] = new ColinPoints(newColinPoints, slope);
        }

        numSegments++;
        // StdOut.print(" Added. numSegments = " + numSegments);
    }

    private boolean segAlreadySaved(Point[] newColinPoints, double newSlope) {

        if (numSegments == 0) {
            return false;
        }

        // sort(newColinPoints, 0, newColinPoints.length - 1);
        //
        // int oldEndIdx;
        // int newEndIdx = newColinPoints.length - 1;
        // boolean exactMatch, flippedMatch;
        // Point savedStartPoint, savedEndPoint;

        // Search through saved segments to find a match
        for (int i = 0; i < numSegments; i++) {

            // If slope matches
            if (newSlope == colinPoints[i].slope) {

                // See if point also matches
                int len = colinPoints[i].points.length;
                for (int j = 0; j < len; j++) {

                    boolean areEqual = newColinPoints[0].compareTo(colinPoints[i].points[j]) == 0;

                    if (areEqual) return true;
                }
            }
        }

        return false;


        //     oldEndIdx = colinPoints[i].points.length - 1;
        //     savedStartPoint = colinPoints[i].points[0];
        //     savedEndPoint = colinPoints[i].points[oldEndIdx];
        //
        //     exactMatch = (savedStartPoint.compareTo(newColinPoints[0]) == 0) &&
        //             (savedEndPoint.compareTo(newColinPoints[newEndIdx]) == 0);
        //
        //     flippedMatch = (savedEndPoint.compareTo(newColinPoints[0]) == 0) &&
        //             (savedStartPoint.compareTo(newColinPoints[newEndIdx]) == 0);
        //
        //     if (exactMatch || flippedMatch) {
        //         // StdOut.print(" Already Found.");
        //         return true;
        //     }
        // }
        // // StdOut.print(" Not Found.");
    }


    private static void sort(Comparable[] a, int startIdx, int endIdx) {

        // "aux" array: create and initialize
        int n = endIdx - startIdx + 1;
        Comparable[] aux = new Comparable[a.length];
        System.arraycopy(a, 0, aux, 0, a.length);

        // Iterate through array in "2*sz" chunks...2,4,8,16,32.....
        for (int sz = 1; sz < n; sz = sz + sz) {

            // Start at beginning of array, and move forward in "2*sz" steps
            for (int lo = startIdx; lo < endIdx + 1 - sz; lo += sz + sz) {

                int mid = lo + sz - 1;
                int hi = Math.min(lo + sz + sz - 1, endIdx);

                if (less(a[mid + 1], a[mid])) {  // If these two are out of order
                    merge(a, aux, lo, mid, hi);
                }
            }
        }
    }


    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {

        assert isSorted(a, lo, mid);
        assert isSorted(a, mid + 1, hi);

        int i = lo;         // Index for "left half" in aux array
        int j = mid + 1;    // Index for "right half" in aux array
        for (int k = lo; k <= hi; k++) {    // "k" is for sorted placement in "a" array

            // If the "left half" is exhausted, pull from right
            if (i > mid) a[k] = aux[j++];
                // If the "right half" is exhausted, pull from left
            else if (j > hi) a[k] = aux[i++];
                // If right item is LESS (only), pull it...critical for stability
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
                // If left item is less, pull it...critical for stability!
            else a[k] = aux[i++];
        }

        // Copy new result into Aux
        System.arraycopy(a, lo, aux, lo, (hi - lo + 1));
    }


    private static boolean isSorted(Comparable[] a, int lo, int hi) {

        for (int i = lo + 1; i <= hi; i++) {
            if (less(a[i], a[i - 1])) { return false; } // If out of order
        }
        return true;
    }

    private static boolean less(Comparable a, Comparable b) {
        return a.compareTo(b) < 0;
    }


    // the number of line segments
    public int numberOfSegments() {
        return numSegments;
    }


    // the line segments
    public LineSegment[] segments() {

        LineSegment[] lines = new LineSegment[numSegments];

        for (int i = 0; i < numSegments; i++) {

            int lastIdx = colinPoints[i].points.length - 1;
            lines[i] = new LineSegment(colinPoints[i].points[0], colinPoints[i].points[lastIdx]);
        }
        return lines;
    }

    // ----------------------------------------
    /* Utility function read in points data from input .txt file to Points Array
     * Output: Point[] Array
     */
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


    public static void main(String[] args) {

        // Test MergeSort using numbers

        // int n = 10000;
        // StdOut.print("\n Num = " + n + "\nBefore\n      ");
        // Integer[] a = new Integer[n];
        // String s;
        // for (int i = 0; i < n; i++) {
        //     a[i] = StdRandom.uniform(100000);
        // }
        // sort(a);
        //
        // if (isSorted(a, 0, a.length - 1)) StdOut.print("\n\n  PASSED\n");
        // else StdOut.print("\n\n  FAILED\n");


        // Test FastCollinearPoints
        String filename = null;

        if (args.length == 1) filename = args[0];
        else StdOut.print("Please input only a single command-line argument.");

        FastCollinearPoints f = new FastCollinearPoints(readPoints(filename));

        LineSegment[] segs = f.segments();

        StdOut.print("\nNumber of Segments = " + f.numberOfSegments());

        // Print line segments and their constituent points
        StdOut.print("\n\nLine Segments: ");
        for (int i = 0; i < f.numSegments; i++) {

            StdOut.print("\n  " + (i + 1) + ": " + segs[i]);
            // StdOut.print("    Points:");
            // for (int j = 0; j < f.colinPoints[i].points.length; j++) {
            //     StdOut.print("  " + f.colinPoints[i].points[j]);
            // }
        }
        StdOut.print("\n");


        double[] limits = findLimits(f.points);
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


        drawPoints(f.points);

        drawLines(segs, f.numberOfSegments());
        StdDraw.show();

    }
}
