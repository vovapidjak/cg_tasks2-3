package ru.vsu.cs.kg2022.g61.kononov;


public class Star {

    private RealPoint center;
    private int coreR;
    private int rayR;
    private int rays;


    public Star(RealPoint center, int coreR, int rayR,  int rays) {
        this.center = center;
        this.coreR = coreR;
        this.rayR = rayR;
        this.rays = rays;
    }

    public RealPoint getCenter() {
        return center;
    }

    public void setCenter(RealPoint center) {
        this.center = center;
    }

    public int getCoreR() {
        return coreR;
    }

    public void setCoreR(int coreR) {
        this.coreR = coreR;
    }

    public int getRayR() {
        return rayR;
    }

    public void setRayR(int rayR) {
        this.rayR = rayR;
    }

    public int getRays() {
        return rays;
    }

    public void setRays(int rays) {
        this.rays = rays;
    }
}
