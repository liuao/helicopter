package com.hongtaili.helicopter.activity;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.hongtaili.helicopter.myView.ShowView;
import com.hongtaili.helicopter.tool.JpgFrame;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class HelicopterActivity extends Activity {

    private Socket socket ;
	
	private InputStream is;
    
    private ShowView mShowView;
   
    byte[] imageByte = new byte[65536];
    byte[] imageByte2 = new byte[65536];
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * �����Ļ������ Ȼ�󴫵ݵ�surfeceview��
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        /**
         * ���ڵĿ�Ⱥ͸߶�
         */
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        
        mShowView = new ShowView(this,screenWidth,screenHeight);
        
        System.out.println("wwekjwhekjwh");
        /**
         * ����Ϊ�ޱ�����
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /**
         * ����Ϊȫ��ģʽ
         */
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(mShowView);
        
        createSocket();
        new ReadJpgStreamThread(is).start();
    }
    
    public void createSocket(){
    	String serverIp = this.getResources().getString(R.string.server_ip);
    	String serverPort = this.getResources().getString(R.string.server_port);
    	
    	try {
			socket = new Socket(serverIp, Integer.parseInt(serverPort));
			is = new DataInputStream(socket.getInputStream());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    class ReadJpgStreamThread extends Thread{
        
        boolean  flag = true;
        InputStream is ;
        
        public ReadJpgStreamThread(InputStream is){
        	this.is = is;
        }
		@Override
		public void run() {
			 while(flag == true){
				   
				   int startByteLocation = 0 , endByteLocation = 0 , i =0;
	        	   /**
	        	    * ÿ��JPGͼƬ���ᳬ��30kb ���Բ��ᳬ��33000�ֽ�
	        	    */
				   for(i = 0 ; i < 33000; i++){
	        		   int temp = -1;
					   try {
						   /**
						    * ���ζ�ȡ�������е�ÿһ���ֽ�
						    */
					       temp = is.read();
					    } catch (IOException e) {
					       e.printStackTrace();
					    }
	        		   /**
	        		    * ����ȡ��ÿһ���ֽ����δ浽imageByte�ֽ�������
	        		    */
					   imageByte[i] = (byte)temp;
	        		   
	        		   if(temp == -1){
	        			   /**
	        			    * ��ʾ�Ѿ�����ĩβ û�����ݿ��Զ���
	        			    */
	        			   flag = false;
	        			   break;
	        		   }
	        		   /**
	        		    *  ��ʾJPG���ݵĿ�ʼ�ĵط�
	        		    */
	        		   if(imageByte[i] == (byte)0xd8){
	             		  if(i > 0){
	             			   if(imageByte[i-1] == (byte)0xff){
	             				  startByteLocation = i-1;
	             			   } 
	             		  }
	             	  }
	        		   /**
	        		    * ��ʾJPG���ݽ����ĵط�
	        		    */
	             	  if(imageByte[i] == (byte)0xd9){
	             		  if(i>0){
	             			  if(imageByte[i-1] == (byte)0xff){
	             				  endByteLocation = i;
	             				  System.arraycopy(imageByte, startByteLocation, imageByte2, 0, endByteLocation - startByteLocation +1);
	             				  /**
	             				   * ���õ����ֽ�����ת����λͼ��ʽ
	             				   */
	             				  JpgFrame.createJPGImage(imageByte2);
	             				  i = 0 ;
	             				  break;
	             			  }
	             		  }
	             	  }
	        	   }
	          }
		}
    }
}
