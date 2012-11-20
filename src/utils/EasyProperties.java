/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 *
 * @author sanker
 */
public class EasyProperties {
    
    public static boolean writeProperty(String file,String key, String value,String comment)
    {
        boolean success=true;
        Writer writer=null;
        try
        {
            
            writer = new FileWriter(file);
            Properties prop = new Properties();
            prop.setProperty(key,value);
            
            prop.store(writer,comment);
        
             writer.close();
        }catch(Exception ex)
        {
            success=false;
        }finally
        {
            try { writer.close(); } catch ( Exception e ) { }
 

        }
        
        return success;
    }
    
    public static String getProperty(String file_,String key)
    {
        String s=null;
        Reader reader=null;
        try{
          File file  = new File(file_);
          boolean a=file.exists();
          boolean b=file.isFile();
          if(a&&b)
          {
               Properties prop = new Properties();
               reader=new FileReader(file);
               prop.load(reader);
               s=prop.getProperty(key);
          }
          
        }catch(FileNotFoundException ex)
        {
            
        }catch(IOException ex)
        {
        }finally
        {
            try { reader.close(); } catch ( Exception e ) { }
        }
        
        
        return s;
    }
    
    
    public static boolean login(String file_,String user, String pass)
    {
        boolean success=false;
        Writer writer=null;
        Reader reader=null;
        try
        {
            //create Md5 hash from password
            String encodedPass=EasySecure.encodeString(pass, "MD5");
            String readMd5Pass=null;
            Properties prop = new Properties();
            File file  = new File(file_);
            boolean a=file.exists();
            boolean b=file.isFile();
           
            if(a&&b)
            {
                reader=new FileReader(file);
                prop.load(reader);
                readMd5Pass=prop.getProperty(user);
                if(readMd5Pass==null)
                {
                    writer = new FileWriter(file);
                    prop.setProperty(user,encodedPass);
                    prop.store(writer,"M5 hash Algorithm");
                    success=true;
            
                    
                }else
                {
                   if(readMd5Pass.contentEquals(encodedPass)) 
                   {
                        success=true;
                   }
            
                }
            }else{
                    writer = new FileWriter(file);
                    prop.setProperty(user,encodedPass);
                    prop.store(writer,"M5 hash Algorithm");
                    success=true;
            
            }
  
        }catch(Exception ex)
        {
    
        }finally
        {
            try { writer.close();reader.close();} catch ( Exception e ) { }
 

        }
        
        return success;
    }
   
    
    
    
    
}