/**
*
* A simple Logger that prints debugging messages if it is true.
*
* @author Jackie Luc
* @version 1.0, Nov 11, 2015
*
*/

public class Logger{
	
	public static boolean debug = false;

    public static void log(Object o){
    	if(debug) {
    		System.out.println(o.toString());
    	}
    }
}