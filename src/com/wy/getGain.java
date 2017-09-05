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
 * 数据分析与存储
 * @author Administrator
 *
 */
public class getGain {
	//bytes假设这里已经从串口读到了数据，是01 01 01 01 00 10 6D FA
	public void ParseWave(byte[] bytes){
		StringBuffer buff;
		//解析CRC校验码 返回一个字符串（16进制）
		String oldCrc=String.format("%02x",bytes[bytes.length-2])+String.format("%02x",bytes[bytes.length-1]);
		System.out.println("原始CRC:"+oldCrc);
		//除去CRC后的有效数据
		byte[] crcbytes=new byte[bytes.length-2];
		
		for(int i=0;i<bytes.length-2;i++){
			crcbytes[i]=bytes[i];
		}
		//判断原始数据CRC和解析后的CRC是否相等
		if(oldCrc.equals(CalcCrc(crcbytes))){
			
			//如果数据长度为8
			if(bytes.length==8){
				String num1=(bytes[4]<<8)+bytes[5]+"";
			    buff=new StringBuffer();
			    buff=new StringBuffer("设备地址："+bytes[0]+"\t"+"读波长命令："+bytes[1]
			    		+"\t"+"第"+bytes[2]+"个通道"+"\t\t"+"第"+bytes[3]+"个传感器"+"\t"+"传感器数量："
					   +num1);
			    System.out.println(buff.toString());
			    WerteFile(buff.toString(),"serialData.txt");
			 //判断数据长度为37
			}else if(bytes.length==37){
				//判断读取数据是否正确
				if(bytes[1]==0x01){
					//获取当前温度
					double num1=(bytes[3]<<8)+bytes[4];
					double temp=num1/500+1500;
					temp=25+(temp-1546.988)/0.0279;
					buff=new StringBuffer("设备地址："+bytes[0]+"\t"+"长度为："+bytes[2]+"\t"+"当前温度为："+temp+"\t"
							+"温度波长："+bytes[5]+"\t"+"应力波长为："+bytes[6]);
					System.out.println(buff.toString());
					WerteFile(buff.toString(),"serialData.txt");
				}else{
					System.out.println("读取E2ROM里数据错误！");
				}
			}
			
		}else{
			System.out.println("数据传输错误！");
		}	
	}
	//假设已经从串口读到了数据，是01 01 01 01 00 10 6D FA
	//计算CRC
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
		System.out.println("CRC计算"+Integer.toHexString(wcrc));
		return Integer.toHexString(wcrc);
	}
	
	//将数据写入文件中保存
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
			System.out.println("数据存储成功！");
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
	
	//读取文件中的内容
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
					System.out.println("文件内容："+lineTxt);
				}
				read.close();
			}else{
				System.out.println("找不到指定的文件！");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("读取文件内容出错！");
			e.printStackTrace();
		}
    }
}
