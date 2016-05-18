package be.ttime;

import java.util.Date;

/**
 * Created by fabricecipolla on 16/05/16.
 */
public class test {

    public void main() {
        Date d = new Date();
        long time = d.getTime();
        System.out.println("Date du jour : ");
        System.out.println(d.toString());
        System.out.println("Time en ms : ");
        System.out.println(time);
        System.out.println("Date reconstitué : ");
        Date f = new Date(time);
        System.out.println(f.toString());



    }
}
