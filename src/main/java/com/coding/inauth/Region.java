package com.coding.inauth;

import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

import com.coding.inauth.model.Location;

public class Region {

    private static Path2D continentialUsaBoundary;
    private static Area continentalUsa;
    
    
    static {
        // define usaBoundary by a series of (latitude,longitude)
        continentialUsaBoundary = new Path2D.Double();
        continentialUsaBoundary.moveTo(44.7742,-67.0262);
        continentialUsaBoundary.lineTo(47.2195, -68.0273);
        continentialUsaBoundary.lineTo(43.3251,-79.2773);
        continentialUsaBoundary.lineTo(41.7057,-82.9687);
        continentialUsaBoundary.lineTo(45.2130,-82.4414);
        continentialUsaBoundary.lineTo(48.2246,-88.4179);
        continentialUsaBoundary.lineTo(49.2104,-94.8339);
        continentialUsaBoundary.lineTo(48.9802,-123.3104);
        continentialUsaBoundary.lineTo(48.1953,-123.3984);
        continentialUsaBoundary.lineTo(48.3124,-124.9365);
        continentialUsaBoundary.lineTo(40.3465,-124.4091);
        continentialUsaBoundary.lineTo(36.4566,-122.0361);
        continentialUsaBoundary.lineTo(34.4522,-120.5859);
        continentialUsaBoundary.lineTo(32.5468,-117.2021);
        continentialUsaBoundary.lineTo(31.278551, -110.9619);
        continentialUsaBoundary.lineTo(31.3536,-105.9521);
        continentialUsaBoundary.lineTo(28.96,-103.1616);
        continentialUsaBoundary.lineTo(29.7643,-101.7773);
        continentialUsaBoundary.lineTo(25.8394,-97.4047);
        continentialUsaBoundary.lineTo(29.2863,-94.812);
        continentialUsaBoundary.lineTo(29.1137,-89.143);
        continentialUsaBoundary.lineTo(29.7071,-85.3298);
        continentialUsaBoundary.lineTo(30.1071,-84.0454);
        continentialUsaBoundary.lineTo(27.8,-82.9028);
        continentialUsaBoundary.lineTo(25.1651,-81.101);
        continentialUsaBoundary.lineTo(24.5671,-81.7712);
        continentialUsaBoundary.lineTo(25.3142,-80.2331);
        continentialUsaBoundary.lineTo(26.8532,-80.0024);
        continentialUsaBoundary.lineTo(30.3728,-81.3647);
        continentialUsaBoundary.lineTo(32.7041,-79.8706);
        continentialUsaBoundary.lineTo(34.6874,-76.4758);
        continentialUsaBoundary.lineTo(35.2545,-75.5310);
        continentialUsaBoundary.lineTo(36.8708,-75.9375);
        continentialUsaBoundary.lineTo(38.3675,-75.0695);
        continentialUsaBoundary.lineTo(40.5555,-73.85);
        continentialUsaBoundary.lineTo(41.2778,-69.9499);
        continentialUsaBoundary.lineTo(42.0737,-70.2355);
        continentialUsaBoundary.lineTo(41.7958,-70.5322);
        continentialUsaBoundary.lineTo(42.2854,-70.8288);
        continentialUsaBoundary.lineTo(42.6420,-70.5871);
        continentialUsaBoundary.lineTo(43.6917,-69.851);
        continentialUsaBoundary.lineTo(44.7935,-66.8957);
        continentialUsaBoundary.lineTo(44.7742,-67.0262);
        continentalUsa = new Area(continentialUsaBoundary);
    }
    
    public static boolean inUSA(Location location) {
        return continentalUsa.contains(location.getLatitude(), location.getLongitude());
    }
    
}
