package control;

import java.awt.Color;

public class Pixel {

    private double red;
    private double green;
    private double blue;

    public Pixel() {
        red = 0;
        green = 0;
        blue = 0;
    }

    public Pixel(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public double getRed() {
        return red;
    }

    public void setRed(double red) {
        this.red = red;
    }

    public double getGreen() {
        return green;
    }

    public void setGreen(double green) {
        this.green = green;
    }

    public double getBlue() {
        return blue;
    }

    public void setBlue(double blue) {
        this.blue = blue;
    }

    @Override
    public String toString() {
        //return "Pixel [red=" + red + ", green=" + green + ", blue=" + blue + "]";
        return red + " " + green + " " + blue + " ";
    }

    public Color toColor() {
        return new Color((int) red, (int) green, (int) blue);
    }

}
