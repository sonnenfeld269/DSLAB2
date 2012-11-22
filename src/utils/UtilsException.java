/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Marko
 */

public class UtilsException extends Exception {
    
    public UtilsException(){ }
    public UtilsException(String s){super(s); }
    /*
     * Create Exception with own message and pass the exception that 
     * caused the creation of the own exception
     * The exception CHAINING CAPABILITY is built into the base class 
     * because it is defi ned in Throwable. Therefore you can just pass 
     * the Throwable argument that is passed to your constructor 
     * to the base class constructor.
     */
    public UtilsException(String s,Throwable cause){super(s,cause); }
    
}
