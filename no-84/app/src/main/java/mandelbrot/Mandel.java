package mandelbrot;

import mandelbrot.contexts.MandelbrotContext;

import java.math.BigDecimal;

public class Mandel implements Runnable {

    private final Pixel p;
    private final MandelbrotContext context;
    private final Pixel start;
    private final Pixel end;
    private final int width;
    private final int height;
    private final int maxIterations;
    private final Object synchObject;

    public Mandel(MandelbrotContext context, Pixel start, Pixel end, Pixel p, int width, int height, int maxIterations) {
        this.context = context;
        this.start = start;
        this.end = end;
        this.p = p;
        this.width = width;
        this.height = height;
        this.maxIterations = maxIterations;
        this.synchObject = new Object();
    }

    @Override
    public void run() {
        double xmin = this.start.x, xmax = this.end.x;
        double ymin = this.start.y, ymax = this.end.y;

        double a = xmin + p.x * (xmax - xmin) / (width - 1);
        double b = ymin + p.y * (ymax - ymin) / (height - 1);
        int iterations = mandelbrot(a, b);

        this.context.draw(iterations, p.i);

     }

    private int mandelbrot(double a, double b) {
        double zr = 0.0, zi = 0.0, zr2 = 0.0, zi2 = 0.0;
        int iter = 0;
        while (zr2 + zi2 <= 4.0 && iter < maxIterations) {
            zi = 2.0 * zr * zi + b;
            zr = zr2 - zi2 + a;
            zr2 = zr * zr;
            zi2 = zi * zi;
            iter++;
        }
        return iter;
    }
}