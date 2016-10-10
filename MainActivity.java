package com.example.sd_data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.example.sd_data.Bluetooth.Connected;
import com.example.sd_data.Bluetooth.ReceiveHandler;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	public static TextView tv;
	TextView tv2;
	ScrollView sv;
	String mysdPath;
	public static String myFilePath;
	String tvcontent;
	public static String myPictureSourse;
	public String myMusicSourse;

	Bitmap bm;
	MediaPlayer mp;
	File myfile;
	 ImageView myiv;
	 String img_string;
	int btnmusic_n=0;
	 EditText et;
	 
	 int newWidth;
	 int newHeight;
	//
	public static Boolean isconnected = false;
	private String Address = "10:14:05:26:09:72";
	private static final UUID myUUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private HandlerThread receive = null;
	private ReceiveHandler receiveHandler = null;
	private Connected mreceive = null;
	public static BluetoothSocket mSocket = null;
	private static OutputStream mOutputStream = null;
//	private static InputStream mInputStream = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnmusic=(Button)findViewById(R.id.button1);
        Button btnlist=(Button)findViewById(R.id.List);
        Button btnpicture=(Button)findViewById(R.id.Picture);
		Button search_blue = (Button) this.findViewById(R.id.bt_search);
		Button connect_blue = (Button) this.findViewById(R.id.bt_connect);
		Button btnsend = (Button) this.findViewById(R.id.next);
        tv=(TextView)findViewById(R.id.textView1);
        tv2=(TextView)findViewById(R.id.textView2);
        myiv=(ImageView)findViewById(R.id.imageView1);
        sv=new ScrollView(this);
        et=(EditText)findViewById(R.id.EditText1);
		Openblue();
		tv2.setText("BlueTooth Open");
        
        mysdPath=getsdcardpath();
        myFilePath=mysdPath+"/A_MySdData1";
       
        tv.setText(myFilePath);
        mp=new MediaPlayer();
        
       
        myfile=new File(myFilePath);    //new my file
        if(!myfile.exists()) 	myfile.mkdir();//

        myPictureSourse=myFilePath+"/h2.png";
        myMusicSourse=myFilePath+"/music.mp3";
        /*
         * 
         */
		search_blue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// TODO Auto-generated method stub
				Searchblue();
				tv.setText("BlueTooth Scaned");
			}
		});
		/*
		 * 
		 */
		connect_blue.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				try {
					Connectblue();
					tv.setText("BlueTooth Connected");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		/*
		 * 
		 */
		btnsend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// TODO Auto-generated method stub
//				sendString();
				img_string=sendPhoto(myiv);
				tv2.setText("ͼƬ��ת��");
				sendPicture(img_string);
				tv2.setText("Picture Send");
			}
		});
		

        /*
         * 
         */
        btnmusic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
					try{
						newWidth+=50;
						newHeight+=50;
						Bitmap tempImg=Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
						myiv.setImageBitmap(tempImg);
						tv.setText("width:"+newWidth+"height:"+newHeight);
					}
					catch(Exception e){
					e.printStackTrace();				
					}	
			}
		});
        /*
         * 
         */
        btnpicture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try{
	
					tv.setText("ͼƬ�л�������");
					
					bm=BitmapFactory.decodeFile(myPictureSourse);
					myiv.setImageBitmap(bm);
//					int width=bm.getWidth();
//					int height=bm.getHeight();
					newWidth=bm.getWidth();
					newHeight=bm.getHeight();					
//					tv.setText("width:"+width+"height:"+height);
					tv.setText("width:"+newWidth+"height:"+newHeight);

//					tv2.setText(img_string);
//					sv.addView(tv2);
//					setContentView(sv);
					
				}
				catch(Exception e){
					e.printStackTrace();				}
			}
		});
	/* */   
        btnlist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {			
				try{
					newWidth-=50;
					newHeight-=50;
//					resizeImg(bm,newWidth,newHeight);
//					myiv.setImageBitmap(resizeImg(bm,newWidth,newHeight));
					Bitmap tempImg=Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
					myiv.setImageBitmap(tempImg);
					tv.setText("width:"+newWidth+"height:"+newHeight);
				}
				catch(Exception e){
				e.printStackTrace();				
				}			
			}
		});      
    }
    /**
    * ��ͼƬת����ʮ�������ַ���
    * @param photo
    * @return
    */
    public static String sendPhoto(ImageView photo) {
    Drawable d = photo.getDrawable();
    Bitmap bitmap=((BitmapDrawable)d).getBitmap();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);// (0 - 100)ѹ���ļ�//30ѹ��70%
    byte[] bt = stream.toByteArray();
    String photoStr = byte2hex(bt);
    return photoStr;
    }  
    /**
    * ������ת�ַ���
    * @param b
    * @return
    */
    public static String byte2hex(byte[] b) 
    {
    StringBuffer sb = new StringBuffer();
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
    stmp = Integer.toHexString(b[n] & 0XFF);
    if (stmp.length() == 1) {
    sb.append("0" + stmp);
    } else {
    sb.append(stmp);
    }
    }
    return sb.toString();
    }    
/*
 * 
 */
 // ��ȡ�ļ��б���
 	private void getFileDir(String path) {
 		//showXPath.setText(path);//��ʾ��ǰ·��
 		//items = new ArrayList<String>();
 		//paths = new ArrayList<String>();
 		// ��ȡ��ǰ·���µ��ļ�
 		File presentFile = new  File(path);
 		File[] files = presentFile.listFiles();
 	/*	
 		if (! path.equals(rootPath)) {
 			// ���ظ�Ŀ¼
 			items.add("back to /");
 			paths.add(rootPath);
 			// ������һ��Ŀ¼
 			items.add("back previous");
 			paths.add(presentFile.getParent());
 		}
 	*/
 		// ��ӵ�ǰ·���µ����е��ļ�����·��
 		for (File f : files) {
 			tvcontent+=(f.getName());
 			//paths.add(f.getPath());
 		}
 		
 		// �����б�������
 		//setListAdapter(new FileListAdapter(FileManagerActivity.this, items, paths));
 	}
/*
 * get SD path
 */
    public static String getsdcardpath(){
    	String sdPath="";
    	sdPath=Environment.getExternalStorageDirectory()
    			.getAbsolutePath();
    	return sdPath;
    }
/*
 * 
 */
	public  void Openblue() {
		//Button btopenblue = (Button) this.findViewById(R.id.open_blue);

		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

		if (!mAdapter.isEnabled()) {
			Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enabler, 0x1);
		}
		Toast.makeText(this, "��������", Toast.LENGTH_SHORT).show();
		while (!mAdapter.isEnabled()) {
			tv2.setText("�����ѿ�");
		}
	}
/*
 * (android.view.Menu)
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	tv.setText("Menu,Work");
        	getFileDir(myFilePath);
        	tv.setText(tvcontent);
        	
        	Intent serverIntent = new Intent(this, FileListActivity.class);
			startActivityForResult(serverIntent, 1);
            return true;
        }
        else if(id == R.id.BlueTeeth){
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }


/**
	 * @��д�ˣ���ΰ
	 * @���ܣ��������������豸
	 * @����ֵ����
	 * @����ֵ����
	 */
	public void Searchblue() {

		final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		// ��ʼ����
		mAdapter.startDiscovery();

		BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				// �ҵ��豸
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					Toast.makeText(MainActivity.this,
							device.getName() + "\n" + device.getAddress(),
							Toast.LENGTH_SHORT).show();
					Address = device.getAddress();

					mAdapter.cancelDiscovery();

					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {

					Toast.makeText(MainActivity.this, "�����豸����", Toast.LENGTH_SHORT)
							.show();
				}
			}
		};
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		IntentFilter filter2 = new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
		registerReceiver(mReceiver, filter2);
	}   
	/**
	 * @throws IOException
	 * @��д�ˣ���ΰ
	 * @���ܣ����������豸
	 * @����ֵ����
	 * @����ֵ����
	 */
	public void Connectblue() throws IOException {
		
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice mDevice = mAdapter.getRemoteDevice(Address);
		try {
			mSocket = mDevice.createRfcommSocketToServiceRecord(myUUID);
			Log.d("socket", "Connecting!!!");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("socket", "Connection failed...\n");
		}
		/* �ر����� */
		mAdapter.cancelDiscovery();
		/* ��ʼ�����豸 */
		try {
			mSocket.connect();
			Log.d("socket", "Connection complete!!\n");
			Toast.makeText(MainActivity.this, "�������", Toast.LENGTH_SHORT).show();
			isconnected = true;

			/* ���������߳� */
			receive = new HandlerThread("Bt_Receive");
			receive.start();
			receiveHandler = new ReceiveHandler(receive.getLooper());

			mreceive = new Connected(mSocket);
			mreceive.start();

		} catch (IOException e) {
			try {
				mSocket.close();
				Log.d("socket",
						"Connection failed... and then close bt_socket...\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "����ʧ��������", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * �����µ���
	 */
	public class ReceiveHandler extends Handler {
		public ReceiveHandler(Looper looper) {
			super(looper);
		}
	}
	/**
	 * �����µ���
	 */
	public class Connected extends Thread {

		final BluetoothSocket m_socket;
		final InputStream ips;

		public Connected(BluetoothSocket Bt_Socket) throws IOException {
			m_socket = Bt_Socket;
			InputStream tmpIn;
			tmpIn = m_socket.getInputStream();
			ips = tmpIn;
		}
	}
	/*
	 * 
	 */
	public static void SendBuffer(char buffer) {
		try {
			mOutputStream = mSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			mOutputStream.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 
	 */
	public void sendString() {
		char buf[] = { 0xAA, 0xAF, 0x00, 0x00, 0x00, 0xAA, 0XAF };
        String etContent;
        etContent=et.getText().toString();
        buf=etContent.toCharArray();
		//EditText settime = (EditText) this.findViewById(R.id.time);
		//EditText temperature = (EditText) this.findViewById(R.id.temperature);
		//int time = Integer.parseInt(settime.getText().toString());
		//int Temp = Integer.parseInt(temperature.getText().toString());
		
		//buf[2] = (char) (time / 100);
		//buf[3] = (char) (time % 100);
		//buf[4] = (char) (Temp);
        SendBuffer('O');
        SendBuffer('K');
		for (int i = 0; i<buf.length; i++) {
			SendBuffer(buf[i]);
		}
	}
	/*
	 * 
	 */
	public void sendPicture(String strPic) {
		char buf[] = { 0xAA, 0xAF, 0x00, 0x00, 0x00, 0xAA, 0XAF };
//        String etContent;
//        etContent=et.getText().toString();
        buf=strPic.toCharArray();

		for (int i = 0; i<buf.length; i++) {
			SendBuffer(buf[i]);
		}
	}
	/*
	 * 
	 */
public static Bitmap resizeImg(Bitmap orgImg,
					int newWidth,int newHeight){
		Bitmap resizedImg;
//		int width=orgImg.getWidth();
//		int height=orgImg.getHeight();
//		float scaleWidth=((float)newWidth)/width;
//		float scaleHeight=((float)newHeight)/height;
//		Matrix matrix=new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		resizedImg=Bitmap.createBitmap(orgImg, 0, 0, 
//				width, height, matrix, true);
		resizedImg=Bitmap.createScaledBitmap(orgImg, newWidth, newHeight, true);
		return resizedImg;
	}
//end of class
}