package com.hongtaili.helicopter.activity;

import com.hongtaili.helicopter.myView.Control;
import com.hongtaili.helicopter.myView.Control.ControlListener;

import android.os.Bundle;
import android.app.Activity;

public class ControlActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);
        final Control Control = (Control) findViewById(R.id.control);
        Control.setControlListener(new ControlListener() {
        	
        //@Override
        	public void onControllerChanged(int action, int change) {
        		if(action == Control.ACTION_POWER) {
        			//TODO:������ʱ���ʵ��   changeΪ�����ĳ���
        			}
        		else if(action == Control.ACTION_RUDDER){
        			//TODO:ҡ���¼���ʵ��   changeΪҡ�˵Ļ���0-360
        		}
        		}
        });
    }
   
}
