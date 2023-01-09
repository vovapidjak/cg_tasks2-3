package ru.vsu.cs.kg2022.g61.kononov;

import ru.vsu.cs.kg2022.g61.kononov.drawers.*;

//import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;

public class DrawPanel extends JPanel {
    private final ScreenConverter converter;
    private Line current = null;
    private Point lastP;
    private final java.util.List<Line> lines = new ArrayList<>();
    private static final java.util.List<Star> starList = new ArrayList<>();

    private final RealPoint[] points = new RealPoint[]{new RealPoint(0,0), new RealPoint(1,1), new RealPoint(2,2)};

    private int count = 0;

    private double SIZE = 15;
    private boolean isInit = true;
    GridDrawing drawing;


    public DrawPanel() {



        converter = new ScreenConverter(800,600, -SIZE, SIZE, 2 * SIZE, 2 * SIZE);
        drawing = new GridDrawing(converter);
        Star s = new Star(new RealPoint(0,0), 2, 20, 12);
        starList.add(s);
        repaint();
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(lastP != null){
                    Point curP = e.getPoint();
                    ScreenPoint delta = new ScreenPoint(-curP.x + lastP.x,-curP.y + lastP.y);
                    RealPoint deltaR = converter.s2r(delta);
                    converter.setX(deltaR.getX());
                    converter.setY(deltaR.getY());
                    lastP = curP;
                    converter.moveCorner(deltaR);
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
//                ScreenPoint sp = new ScreenPoint(e.getX(), e.getY());
//                current.setP2(converter.s2r(sp));
//                repaint();
            }
        });

        this.addMouseWheelListener(e -> {
            int count = e.getWheelRotation();
            double base = count < 0 ? 0.99 : 1.01;
            double coef = 1;
            for (int i = Math.abs(count); i > 0 ; i--) {
                coef *= base;
            }
            converter.changeScale(coef);
            repaint();
        });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {

                    points[count%3] = (converter.s2r(new ScreenPoint(e.getX(), e.getY())));

                    if (count%3 == 2){
                        Star star = new Star(points[0], (int) (Pifagor(points[0], points[1])), (int) (Pifagor(points[0], points[2])), 12 );
                        starList.add(star);
                    }
                    count +=1;
                    repaint();
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    lastP = e.getPoint();
                }
                if(SwingUtilities.isLeftMouseButton(e)){
                    points[count%3] = (converter.s2r(new ScreenPoint(e.getX(), e.getY())));
                    if (count%3 == 2){
                        Star star = new Star(points[0], (int) (Pifagor(points[0], points[1])), (int) (Pifagor(points[0], points[2])), 12 );
                        starList.add(star);
                    }
                    count +=1;
                    repaint();
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    lastP = null;
                }
                if(SwingUtilities.isLeftMouseButton(e)){
//                    lines.add(current);
//                    current = null;
//                    repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private double Pifagor(RealPoint p1, RealPoint p2){
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();

        return Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
    }

    @Override
    protected void paintComponent(Graphics g) {


        Graphics2D g2d = (Graphics2D) g;
        converter.setsHeight(getHeight());
        converter.setsWidth(getWidth());

        BufferedImage bi = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D biG = bi.createGraphics();
        biG.setColor(Color.WHITE);
        biG.fillRect(0,0,getWidth(),getHeight());

        //LineDrawer ld = new DDALineDrawer(new GraphicsPixelDrawer(biG));
        LineDrawer ld = new BresenhamLineDrawer(new GraphicsPixelDrawer(biG));
        //LineDrawer ld = new WULineDrawer(new GraphicsPixelDrawer(biG));

        biG.setColor(Color.BLACK);


        drawing.draw(ld,biG);
        biG.drawOval((int) points[0].getX()-5, (int) points[0].getY()-5, 10, 10);
        biG.drawOval((int) points[1].getX()-5, (int) points[1].getY()-5, 10, 10);


//        System.out.println("Введите количество лучиков");
//        int n = sc.nextInt();

//        if (isInit) {
//            for (int i = 0; i < starList.size(); i++) {
//
//                currStar = new Star(new RealPoint(x, y), 2, 20, 12);
//                starList.add(currStar);
//            }
//
//            isInit = false;
//        }




        for (int i = 0; i < starList.size(); i++){

            drawStar(ld, converter, starList.get(i));
        }


        g2d.drawImage(bi,0,0,null);
        biG.dispose();
    }

    private void drawStar(LineDrawer drawer, ScreenConverter converter, Star star){
        ScreenPoint center = converter.r2s(star.getCenter());

        int coreR = star.getCoreR();
        int rayR = star.getRayR();


        int n = star.getRays();

        ScreenPoint rpR = converter.r2s(new RealPoint(star.getCenter().getX() + rayR, star.getCenter().getY()));
        ScreenPoint rpr = converter.r2s(new RealPoint(star.getCenter().getX() + coreR, star.getCenter().getY()));




        double R = rpR.getX() - center.getX();
        double r = rpr.getX() - center.getX();





        double da = 2 * Math.PI / n;

        for (int i = 0; i < n; i++){
            double a = da * i;
            drawer.drawLine(
                    (int) (center.getX() + r * Math.cos(a)),
                    (int) (center.getY() + r * Math.sin(a)),
                    (int) (center.getX() + R * Math.cos(a)),
                    (int) (center.getY() + R * Math.sin(a)));
        }

        double da2 = 2 * Math.PI / 360;

        for (int i = 0; i < 360; i++){
            double a = da2 * i;
            drawer.drawLine(
                    (int) (center.getX()),
                    (int) (center.getY()),
                    (int) (center.getX() + r * Math.cos(a)),
                    (int) (center.getY() + r * Math.sin(a)));
        }
    }
}
