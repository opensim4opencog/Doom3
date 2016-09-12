package daxclr.doom.modules;


public class QuadmoreSR {
    static {
        System.loadLibrary("QuadSR");
    }
    public QuadmoreSR(){
    }

    public static void main(String args[]){
        QuadmoreSR sr = new QuadmoreSR();
        System.out.println(sr.TakeDictation());
    }

    public native String TakeDictation();

}