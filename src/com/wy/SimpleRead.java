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
 * ���ڲ���
 * @author Administrator
 *
 */
public class SimpleRead implements SerialPortEventListener 
{ 
    static CommPortIdentifier portId; 	//����ͨ�Ź�����
    static Enumeration portList;   	//�Ѿ������ϵĶ˿ڵ�ö��
    InputStream inputStream; 		//�Ӵ�������������
    OutputStream outputStream;		//�򴮿��������
    SerialPort serialPort;     		//���ڵ�����

    public static String name;    //��������
    public static int baudRate;  //������
    public static int dataBits;  //����λ
    public static int parityBit;  //У��λ
    public static int stopBit;    //ֹͣλ
    
    public static void main(String[] args) { 
    	Scanner input =new Scanner(System.in);
    	List<String> list=new ArrayList<String>();
    	try { 
    		portList = CommPortIdentifier.getPortIdentifiers();   //��ѯ��ȡ���еĶ˿�
    	    while (portList.hasMoreElements())
    	    { 
    	    	portId = (CommPortIdentifier) portList.nextElement();  //ȡ��һ���˿�
    	    	if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) //�ж���Ϊ����
    	    	{
    	    		//�Ѵ���������ӵ�list��
    	    		list.add(portId.getName());
    	    	}
    	    }
    	    //��������
    	    int i=0;
    	    for (String mo : list) {
    	    	i++;
				System.out.println(i+":"+mo+"\t");
			}
    	    System.out.print("��ѡ�񴮿�:");
    		name=input.next().trim().toUpperCase(); 
    		//������
			System.out.println("��ѡ������:1:9600;\t2:19200;\t3:38400;\t4:57600;\t5:115200");
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
				System.out.println("�������");
				break;
			}
			//����λ
			System.out.println("��ѡ������λ��1:5;\t2:6;\t3:7;\t4:8");
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
				System.out.println("ѡ�����");
				break;
			}
			//У��λ
			System.out.println("У��λ��1:None;\t2:Even;\t3:Odd;\t4:Mark;\t5:Space");
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
				System.out.println("ѡ�����");
				break;
			}
			//ֹͣλ
			System.out.println("ֹͣλ��1:1;\t2:1.5;\t3:2");
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
					System.out.println("ѡ�����");
					break;
				}
				SimpleRead reader = new SimpleRead();
				//���ò��򿪶˿�
				reader.ConfigAndOpenPort();
			}
			catch (Exception e){
				e.printStackTrace();
			System.out.println("��������˳�");
			input.next();
			}

    	}

    public SimpleRead() { 
    }

    public void ConfigAndOpenPort()
    {
    	try { 
    		//�򿪴��ڲ�ȡ����Ϊmyapp,�ӳ�Ϊ2����
    		portList = CommPortIdentifier.getPortIdentifiers();
    		//�ж�portList���Ƿ��ж˿�
    	    while (portList.hasMoreElements())
    	    { 
    	    	//ȡ��һ���˿�
    	    	portId = (CommPortIdentifier) portList.nextElement();
    	    	//�ж϶˿������Ƿ�Ϊ����
    	    	if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
    	    	{
    	    		//����Ǵ��ڣ���ȡ������������Ĵ������Ƚ��Ƿ���ȣ�
    	    		if (portId.getName().equals(name))
    	    		{	
						//�򿪶˿�ȡ����"myApp" �ӳ�2��
    	    			serialPort = (SerialPort) portId.open("myApp", 2000);
    	    			System.out.println("���˴���"+name+"������:myapp");
					}
    	    	}
    	    }
			} 
    		catch (PortInUseException e) { 
    			System.out.println(name+"����ʹ�ã�");
    		} 
    	try { 
    		//��ȡ�����е����������
		    inputStream = serialPort.getInputStream(); 
		    outputStream = serialPort.getOutputStream(); 
		    System.out.println("��������������");
    	} 
    	catch (IOException e) { 
    		System.out.println("����������������");
    	}
    	try { 
    		//����ǰ�������һ��������
		    serialPort.addEventListener(this);      
		    System.out.println("�����һ�������ߣ�");
    	} 
    	catch (TooManyListenersException e) { 
    		System.out.println("�����߹��࣡");
    	} 
    	//��������ʱ֪ͨ
    	serialPort.notifyOnDataAvailable(true); 
    	try { 
		    //���ô��ڶ�д����
		    serialPort.setSerialPortParams( baudRate,dataBits,stopBit,parityBit);
		    System.out.println("�� "+name+"����Ϊ��"+ baudRate+"-"+parityBit+"-"+dataBits+"-"+stopBit);
    	} 
    	catch (UnsupportedCommOperationException e) { 
    		System.out.println("��֧�ִ˲�����");
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
    	    //���п�������ʱ��ȡ����,���Ҹ����ڷ�������
    	    case SerialPortEvent.DATA_AVAILABLE:
    	    	System.out.println("��ȡ�������ˣ�");
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
    	        	System.out.println("�����������");
    	        } 
    	        break; 
    		}
    	} 
    
    
}
