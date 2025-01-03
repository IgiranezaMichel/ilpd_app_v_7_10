package models;

import java.io.File;

/**
 * Created by SISI on 10/19/2017.
 */
public class Vld {
    public static int limit = 10;
    public static int Adlimit = 200;
    public static Integer superLimit = 1000000000;
    public static String key = "::count";
    public static String split = "::page";
    public static String cons = "images/boys.jpg";
    public static String validExt = "pdf,jpg,png,gif,bmp,jpeg";
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
    public static String imagePro( String image ){
        File tmpDir = new File("/uploads/"+image);
        boolean exists = tmpDir.exists();
        return  ( image.equals("") && !exists ) ? cons : "uploads/"+image;
    }
    public static int counter(int a){
        int integer = (int) Math.ceil((float) a/Vld.limit);
        return ( integer > 1 ) ? integer : 1;
    }
    public static int AdCounter(int a){
        int integer = (int) Math.ceil((float) a/Vld.Adlimit);
        return ( integer > 1 ) ? integer : 1;
    }
    public static int t(int s) {
        return (s - 1) * Vld.limit;
    }
    public static String identifier( Verification idnt ){

        String col1 = "bg-aqua";
        if( idnt != null && !idnt.status ){
            col1 = "bg-red";
        }else if( idnt != null && idnt.status ){
            col1 = "bg-green";
        }
        return  "<i class='fa fa-file-photo-o "+col1+"'></i>";
    }
    public static Boolean inArray( String[] hayStack , String needle ){
        Boolean bld = false;
        for(String i : hayStack ){
            if( needle.equals(i) ){
                bld = true;
                break;
            }
        }
        return bld;
    }
    public static String getExt(String name){
        return name.substring(name.lastIndexOf('.') + 1);
    }
    public static Boolean checkValid( String ext ){
        String[] app = validExt.split(",");
        return inArray(app, ext.toLowerCase());
    }
}
