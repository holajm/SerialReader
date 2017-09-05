package com.wy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

//import javafx.beans.binding.StringBinding;
//import javafx.scene.shape.Line;

import java.lang.*;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
/**
 * ���ݷ�����洢
 * @author Administrator
 *
 */
public class getGain {
	//bytes���������Ѿ��Ӵ��ڶ��������ݣ���01 01 01 01 00 10 6D FA
	public void ParseWave(byte[] bytes){
		StringBuffer buff;
		//����CRCУ���� ����һ���ַ�����16���ƣ�
		String oldCrc=String.format("%02x",bytes[bytes.length-2])+String.format("%02x",bytes[bytes.length-1]);
		System.out.println("ԭʼCRC:"+oldCrc);
		//��ȥCRC�����Ч����
		byte[] crcbytes=new byte[bytes.length-2];
		
		for(int i=0;i<bytes.length-2;i++){
			crcbytes[i]=bytes[i];
		}
		//�ж�ԭʼ����CRC�ͽ������CRC�Ƿ����
		if(oldCrc.equals(CalcCrc(crcbytes))){
			
			//������ݳ���Ϊ8
			if(bytes.length==8){
				String num1=(bytes[4]<<8)+bytes[5]+"";
			    buff=new StringBuffer();
			    buff=new StringBuffer("�豸��ַ��"+bytes[0]+"\t"+"���������"+bytes[1]
			    		+"\t"+"��"+bytes[2]+"��ͨ��"+"\t\t"+"��"+bytes[3]+"��������"+"\t"+"������������"
					   +num1);
			    System.out.println(buff.toString());
			    WerteFile(buff.toString(),"serialData.txt");
			 //�ж����ݳ���Ϊ37
			}else if(bytes.length==37){
				//�ж϶�ȡ�����Ƿ���ȷ
				if(bytes[1]==0x01){
					//��ȡ��ǰ�¶�
					double num1=(bytes[3]<<8)+bytes[4];
					double temp=num1/500+1500;
					temp=25+(temp-1546.988)/0.0279;
					buff=new StringBuffer("�豸��ַ��"+bytes[0]+"\t"+"����Ϊ��"+bytes[2]+"\t"+"��ǰ�¶�Ϊ��"+temp+"\t"
							+"�¶Ȳ�����"+bytes[5]+"\t"+"Ӧ������Ϊ��"+bytes[6]);
					System.out.println(buff.toString());
					WerteFile(buff.toString(),"serialData.txt");
				}else{
					System.out.println("��ȡE2ROM�����ݴ���");
				}
			}
			
		}else{
			System.out.println("���ݴ������");
		}	
	}
	//�����Ѿ��Ӵ��ڶ��������ݣ���01 01 01 01 00 10 6D FA
	//����CRC
	public String CalcCrc(byte[] bytes) {
		int flag;
		int wcrc=0x0000ffff;
		for(int i=0;i<bytes.length;i++){
			wcrc=wcrc^((int)bytes[i] & 0x000000ff);
			for(int j=0;j<8;j++){
				flag=wcrc & 0x00000001;
				wcrc=wcrc>>1;
			if(flag==1){
				wcrc=wcrc ^  0x0000a001;
			}
			}
		}
		int b1=wcrc & 0x0000ff00;
		int b2=wcrc & 0x000000ff;
		b2=b2<<8;
		b1=b1>>8;
		wcrc=b1+b2;
		System.out.println("CRC����"+Integer.toHexString(wcrc));
		return Integer.toHexString(wcrc);
	}
	
	//������д���ļ��б���
	public void WerteFile(String bytes,String filename){
		
		FileOutputStream fos=null;
		PrintStream ps=null;
		File file;
		try {
			file=new File(filename);
			fos=new FileOutputStream(file,true);
			ps=new PrintStream(fos);
		Date now=new Date();
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String str=dateformat.format(now)+": "+bytes;
			ps.println(str);
			/*fos=new FileOutputStream(file,true);
			if(!file.exists()){
				file.createNewFile();
			}
			fos.write(str.getBytes());
			fos.flush();
			fos.close();*/
			System.out.println("���ݴ洢�ɹ���");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if (ps != null) {
					ps.close();
				}
				if(fos!=null){
					fos.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	
	//��ȡ�ļ��е�����
	public void readTxtFile(String filePath){
    	try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader buff = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = buff.readLine()) != null) {
					System.out.println("�ļ����ݣ�"+lineTxt);
				}
				read.close();
			}else{
				System.out.println("�Ҳ���ָ�����ļ���");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
    }
}
