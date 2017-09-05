package com.wy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.TooManyListenersException;


import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
/**
 * 串口测试
 * @author Administrator
 *
 */
public class SimpleRead implements SerialPortEventListener 
{ 
    static CommPortIdentifier portId; 	//串口通信管理类
    static Enumeration portList;   	//已经连接上的端口的枚举
    InputStream inputStream; 		//从串口来的输入流
    OutputStream outputStream;		//向串口输出的流
    SerialPort serialPort;     		//串口的引用

    public static String name;    //串口名称
    public static int baudRate;  //波特率
    public static int dataBits;  //数据位
    public static int parityBit;  //校验位
    public static int stopBit;    //停止位
    
    public static void main(String[] args) { 
    	Scanner input =new Scanner(System.in);
    	List<String> list=new ArrayList<String>();
    	try { 
    		portList = CommPortIdentifier.getPortIdentifiers();   //查询获取所有的端口
    	    while (portList.hasMoreElements())
    	    { 
    	    	portId = (CommPortIdentifier) portList.nextElement();  //取出一个端口
    	    	if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) //判断是为串口
    	    	{
    	    		//把串口名字添加到list中
    	    		list.add(portId.getName());
    	    	}
    	    }
    	    //串口名称
    	    int i=0;
    	    for (String mo : list) {
    	    	i++;
				System.out.println(i+":"+mo+"\t");
			}
    	    System.out.print("请选择串口:");
    		name=input.next().trim().toUpperCase(); 
    		//波特率
			System.out.println("请选择波特率:1:9600;\t2:19200;\t3:38400;\t4:57600;\t5:115200");
			int	intBaudRate=input.nextInt();  
			switch (intBaudRate) {
			case 1:
				baudRate=9600;
				break;
			case 2:
				baudRate=19200;
				break;
			case 3:
				baudRate=38400;
				break;
			case 4:
				baudRate=57600;
				break;
			case 5:
				baudRate=115200;
				break;
			default:
				System.out.println("输入错误！");
				break;
			}
			//数据位
			System.out.println("请选择数据位：1:5;\t2:6;\t3:7;\t4:8");
			int intDataBits=input.nextInt();    
			switch (intDataBits) {
			case 1:
				dataBits=SerialPort.DATABITS_5;
			case 2:
				dataBits=SerialPort.DATABITS_6;
			case 3:
				dataBits=SerialPort.DATABITS_7;
			case 4:
				dataBits=SerialPort.DATABITS_8;
				break;
			default:
				System.out.println("选择错误！");
				break;
			}
			//校验位
			System.out.println("校验位：1:None;\t2:Even;\t3:Odd;\t4:Mark;\t5:Space");
			int intParityBit=input.nextInt();   
			switch (intParityBit) {
			case 1:
				parityBit=SerialPort.PARITY_NONE;
				break;
			case 2:
				parityBit=SerialPort.PARITY_EVEN;
				break;
			case 3:
				parityBit=SerialPort.PARITY_ODD;
				break;
			case 4:
				parityBit=SerialPort.PARITY_MARK;
				break;
			case 5:
				parityBit=SerialPort.PARITY_SPACE;
				break;
			default:
				System.out.println("选择错误！");
				break;
			}
			//停止位
			System.out.println("停止位：1:1;\t2:1.5;\t3:2");
				int intStopBit=input.nextInt();   
				switch (intStopBit) {
				case 1:
					stopBit=SerialPort.STOPBITS_1;
					break;
				case 2:
					stopBit=SerialPort.STOPBITS_1_5;
					break;
				case 3:
					stopBit=SerialPort.STOPBITS_2;
					break;
				default:
					System.out.println("选择错误！");
					break;
				}
				SimpleRead reader = new SimpleRead();
				//配置并打开端口
				reader.ConfigAndOpenPort();
			}
			catch (Exception e){
				e.printStackTrace();
			System.out.println("按任意键退出");
			input.next();
			}

    	}

    public SimpleRead() { 
    }

    public void ConfigAndOpenPort()
    {
    	try { 
    		//打开串口并取别名为myapp,延迟为2毫秒
    		portList = CommPortIdentifier.getPortIdentifiers();
    		//判断portList中是否还有端口
    	    while (portList.hasMoreElements())
    	    { 
    	    	//取出一个端口
    	    	portId = (CommPortIdentifier) portList.nextElement();
    	    	//判断端口类型是否为串口
    	    	if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
    	    	{
    	    		//如果是串口（获取串口名与输入的串口名比较是否相等）
    	    		if (portId.getName().equals(name))
    	    		{	
						//打开端口取别名"myApp" 延迟2秒
    	    			serialPort = (SerialPort) portId.open("myApp", 2000);
    	    			System.out.println("打开了串口"+name+"别名叫:myapp");
					}
    	    	}
    	    }
			} 
    		catch (PortInUseException e) { 
    			System.out.println(name+"正在使用！");
    		} 
    	try { 
    		//获取串口中的输入输出流
		    inputStream = serialPort.getInputStream(); 
		    outputStream = serialPort.getOutputStream(); 
		    System.out.println("获得输入输出流！");
    	} 
    	catch (IOException e) { 
    		System.out.println("获得输入输出流错误！");
    	}
    	try { 
    		//给当前串口添加一个监听器
		    serialPort.addEventListener(this);      
		    System.out.println("添加了一个监听者！");
    	} 
    	catch (TooManyListenersException e) { 
    		System.out.println("监听者过多！");
    	} 
    	//当有数据时通知
    	serialPort.notifyOnDataAvailable(true); 
    	try { 
		    //设置串口读写参数
		    serialPort.setSerialPortParams( baudRate,dataBits,stopBit,parityBit);
		    System.out.println("将 "+name+"配置为："+ baudRate+"-"+parityBit+"-"+dataBits+"-"+stopBit);
    	} 
    	catch (UnsupportedCommOperationException e) { 
    		System.out.println("不支持此操作！");
    	} 
    }

    public void serialEvent(SerialPortEvent event){	
    	switch (event.getEventType()) { 
    	    case SerialPortEvent.BI: 
    	    case SerialPortEvent.OE: 
    	    case SerialPortEvent.FE: 
    	    case SerialPortEvent.PE: 
    	    case SerialPortEvent.CD: 
    	    case SerialPortEvent.CTS: 
    	    case SerialPortEvent.DSR: 
    	    case SerialPortEvent.RI: 
    	    case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
    	    	break;
    	    //当有可用数据时读取数据,并且给串口返回数据
    	    case SerialPortEvent.DATA_AVAILABLE:
    	    	System.out.println("读取到数据了！");
    	        byte[] readBuffer = new byte[20]; 
    	        byte[] buffer=new byte[40];
    	        int num=0;
    	        try { 
    	        	while (inputStream.available() > 0) { 
    	        		int numBytes = inputStream.read(readBuffer); 
    	        		for(int j=0;j<numBytes;j++){
    	        			buffer[num]=readBuffer[j];
    	        			num++;
    	        		}
    	        		readBuffer=new byte[20];
    	        	} 
    	        	byte[] data =new byte[num];
			String hexStr="";
    	        	for(int k=0; k<num;k++){
    	        	    data[k]=buffer[k];
			    hexStr+=String.format("%02x",data[k]) + " ";
			    //System.out.print(String.format("%02x",data[k]));
    	        	}
			System.out.println(hexStr);
	
    	        	getGain gain=new getGain();
			gain.WerteFile(hexStr,"serialLog.txt");
    	        	gain.ParseWave(data);	
    	        } 
    	        catch (IOException e) 
    	        { 
    	        	System.out.println("输入输出错误！");
    	        } 
    	        break; 
    		}
    	} 
    
    
}
