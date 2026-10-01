package src.util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LineGraph extends JPanel {

    private final List<double[]> dataPoints=new ArrayList<>();
    private static final int padding=60;
    private double maxValue=0;
    private double minValue=Double.MAX_VALUE;
    private int compressedData=0;

    public LineGraph(){

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2=((Graphics2D) g);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        int graphWidth=getWidth()-2*padding;
        int graphHeight=getHeight()-2*padding;
        g2.setColor(Color.WHITE);
        g2.fillRect(padding,padding,graphWidth,graphHeight);
        g2.setColor(Color.LIGHT_GRAY);

        if (dataPoints.size()>graphWidth/50&&compressedData>dataPoints.size()-10){
            compressData();
        }

        int gridDivisions=10;
        for (int i = 0; i < gridDivisions; i++) {
            int y=getHeight()-padding-(i*graphHeight/gridDivisions);
            g2.drawLine(padding,y,getWidth()-padding,y);

            double a=i*((maxValue+(minValue>=0?0:-minValue))*1.05/gridDivisions);
            String label=String.valueOf((int)(a-(minValue>=0?0:minValue)));
            g2.setColor(Color.BLACK);
            g2.drawString(label,padding/2,y+5);
            g2.setColor(Color.LIGHT_GRAY);
        }

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawString(String.valueOf(dataPoints.size()+1),getWidth()-padding/2,getHeight()-10);
        g2.drawLine(padding,getHeight()-padding,getWidth()-padding,getHeight()-padding);
        g2.drawLine(padding,padding,padding,getHeight()-padding);

        drawGraph(0,g2,graphWidth,graphHeight,Color.BLUE);
        drawGraph(1,g2,graphWidth,graphHeight,Color.CYAN);
        drawGraph(2,g2,graphWidth,graphHeight,Color.GREEN);
    }

    private void compressData() {
        compressedData++;
        double a=0;
        double b=0;
        double c=0;
        for (int i = compressedData; i < 10+compressedData; i++) {
            a+=dataPoints.get(i)[0];
            b+=dataPoints.get(i)[1];
            c+=dataPoints.get(i)[2];
        }
        a/=10;
        b/=10;
        c/=10;
        dataPoints.get(compressedData)[0]=a;
        dataPoints.get(compressedData)[1]=b;
        dataPoints.get(compressedData)[2]=c;
        dataPoints.subList(compressedData + 1, compressedData+10).clear();
    }

    public void addDataPoint(double[] value){
        dataPoints.add(value);
        for (double v : value) {
            if (v>maxValue)maxValue=v;
            if (v<minValue)minValue=v;
        }
        repaint();
    }

    private void drawGraph(int index,Graphics2D g2,int graphWidth,int graphHeight,Color color){

        double xStep= (double) graphWidth / dataPoints.size();
        int[] xPoints=new int[dataPoints.size()];
        int[] yPoints=new int[dataPoints.size()];

        for (int i = 0; i < dataPoints.size(); i++) {
            xPoints[i]=padding+(int)(i*xStep);

            yPoints[i]=getHeight()-padding-(int)((dataPoints.get(i)[index]/(maxValue+(minValue>=0?0:-minValue)))*graphHeight);
        }
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawPolyline(xPoints,yPoints,dataPoints.size());
    }

    public void clearData(){
        dataPoints.clear();
        maxValue=0;
        minValue=Double.MAX_VALUE;
        compressedData=0;
    }
}