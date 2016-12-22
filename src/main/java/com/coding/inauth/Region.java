package com.coding.inauth;

import java.awt.geom.Area;
import java.awt.geom.Path2D;

import com.coding.inauth.model.Location;

/**
 * Only handle continental US, Alaska and Hawaii
 * @author skuo
 *
 */
public class Region {

    private static Path2D continentialUsaBoundary;
    private static Area continentalUsa;
    private static Path2D alaskaBoundary;
    private static Area alaska;
    private static Path2D hawaiiBoundary;
    private static Area hawaii;    
    
    static {
        // define continentialUsaBoundary by a series of (latitude,longitude)
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

        // define alaskaBoundary by a series of (latitude,longitude)
        alaskaBoundary = new Path2D.Double();
        alaskaBoundary.moveTo(54.7256,-130.6054);
        alaskaBoundary.lineTo(59.7120,-135.3515);
        alaskaBoundary.lineTo(58.95,-137.6367);
        alaskaBoundary.lineTo(60.4138,-139.0429);
        alaskaBoundary.lineTo(60.2398,-140.9765);
        alaskaBoundary.lineTo(69.6265,-141.0644);
        alaskaBoundary.lineTo(71.3007,-156.7089);
        alaskaBoundary.lineTo(68.8476,-166.289);
        alaskaBoundary.lineTo(68.3343,-166.5966);
        alaskaBoundary.lineTo(67.1187,-163.7841);
        alaskaBoundary.lineTo(66.1782,-161.6967);
        alaskaBoundary.lineTo(66.0737,-163.7127);
        alaskaBoundary.lineTo(66.5963,-163.9599);
        alaskaBoundary.lineTo(66.2668,-166.0913);
        alaskaBoundary.lineTo(65.622,-168.2226);
        alaskaBoundary.lineTo(65.1553,-166.9702);
        alaskaBoundary.lineTo(64.5956,-166.3549);
        alaskaBoundary.lineTo(64.2063,-161.1035);
        alaskaBoundary.lineTo(63.2731,-164.3554);
        alaskaBoundary.lineTo(61.4387,-166.1132);
        alaskaBoundary.lineTo(58.6312,-161.8945);
        alaskaBoundary.lineTo(58.2632,-157.4121);
        alaskaBoundary.lineTo(54.6229,-164.7949);
        alaskaBoundary.lineTo(57.0885,-153.1054);
        alaskaBoundary.lineTo(59.933,-149.7656);
        alaskaBoundary.lineTo(60.0209,-144.0527);
        alaskaBoundary.lineTo(55.8259,-134.7363);
        alaskaBoundary.lineTo(54.7256,-130.6054);
        alaska = new Area(alaskaBoundary);

        // define hawaiiBoundary by a series of (latitude,longitude)
        hawaiiBoundary = new Path2D.Double();
        hawaiiBoundary.moveTo(21.7799f,-160.2246f);
        hawaiiBoundary.lineTo(21.8513f,-159.45f);
        hawaiiBoundary.lineTo(21.2842f,-158.1152f);
        hawaiiBoundary.lineTo(21.0742f,-157.3132f);
        hawaiiBoundary.lineTo(20.7252,-156.9781);
        hawaiiBoundary.lineTo(19.7253,-156.0937);
        hawaiiBoundary.lineTo(19.0932,-155.9179);
        hawaiiBoundary.lineTo(18.8958,-155.6762);
        hawaiiBoundary.lineTo(19.508,-154.7973);
        hawaiiBoundary.lineTo(19.8545,-155.083);
        hawaiiBoundary.lineTo(20.7715,-155.9564);
        hawaiiBoundary.lineTo(20.9357,-156.2585);
        hawaiiBoundary.lineTo(21.1511,-156.7089);
        hawaiiBoundary.lineTo(21.4581,-157.6702);
        hawaiiBoundary.lineTo(21.7135,-157.9669);
        hawaiiBoundary.lineTo(22.1467,-159.2866);
        hawaiiBoundary.lineTo(22.2395,-159.402);
        hawaiiBoundary.lineTo(22.2331,-159.5819);
        hawaiiBoundary.lineTo(22.1428,-159.7315);
        hawaiiBoundary.lineTo(21.7799f, -160.2246f);
        hawaii = new Area(hawaiiBoundary);
    }
    
    public static boolean inUSA(Location location) {
        if (continentalUsa.contains(location.getLatitude(), location.getLongitude()))
            return true;
        else if (alaska.contains(location.getLatitude(), location.getLongitude()))
            return true;
        else if (hawaii.contains(location.getLatitude(), location.getLongitude()))
            return true;
        else
            return false;
    }
    
}
