package com.hongtaili.helicopter.tool;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class JpgFrame {

	 /**
	  * ������Ƶ���������JPG��֡����
	  */
  // public static Vector<Bitmap> bitMaps = new Vector<Bitmap>(1);
	  
	public static List<Bitmap> bitMaps = new ArrayList<Bitmap>();
	
	/**
	 * ��ǰ����֡������
	 */
   public static int index = -1;
	public JpgFrame(){
		/*bitMaps = new Vector<Bitmap>(1);
       index = -1;	*/	
	}
	
	/**
	 * ���һ֡
	 */
	public   void addImage(Bitmap image){
		
		//bitMaps.addElement(image);
		bitMaps.add(image);
	}
	
	/**
	 * ����֡��
	 */
	
	public int size(){
		
		return bitMaps.size();
	}
	/**
	 * �õ���ǰ֡��ͼƬ
	 */
	
	public Bitmap getImage(){
		
		if(size() == 0){
			return null;
		}
		if(index >= size()|| index == -1){
			return null;
		}
		else{
         //  return  bitMaps.elementAt(index);			
	      return bitMaps.get(index);
		}
	}
	
	public void nextImage(){
		if(index + 1 < size()){
			index ++;
		}
		else {
			index = size()-1;
		}
	}
	
	public static JpgFrame createJPGImage(byte abyte[]){
		
		JpgFrame frame = new JpgFrame();
		/**
		 * �����ֽ�������λͼ
		 */
       Bitmap bitmap = BitmapFactory.decodeByteArray(abyte, 0, abyte.length);
       frame.addImage(bitmap);
       System.out.println(bitMaps.size());
		return frame;
	}
}
