/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import java.util.concurrent.Executor;

/**
 *
 * @author sanker
 */
public class AnalyticsManagementSystem implements Runnable {
    
    private Executor pool=null;
    
    
    public AnalyticsManagementSystem(Executor pool)
    {
        this.pool=pool;
    }
    
    public void run()
    {
    
    }
}
