package cn.decom.customertoolbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esri.arcgis.geoprocessing.GeoProcessor;
import com.esri.arcgis.geoprocessing.IGeoProcessorResult;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.VarArray;
import com.esri.arcgis.system.esriJobStatus;
import com.esri.arcgis.system.esriLicenseExtensionCode;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;

/**
 * ��ʾ����ArcObject 10.5 �����Զ��� toolbox ����
 * �����и�ʾ�����̴���ǰ��һ��Ҫ���ú� ArcObject ���������� jdk 32 λ������װ ArcObject for java������
 * @author DaiH 
 * @date 2018-4-17
 */
public class CustomerToolbox {
	private GeoProcessor gp = null;

	public CustomerToolbox() {
		// TODO Auto-generated constructor stub
		try {
			gp = new GeoProcessor();
			gp.setOverwriteOutput(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * �����Զ���arctoolbox���Ӵ���ͶӰ��Ϣ��tifͼ������json
	 * @param tiffPath
	 */
	public void produceFormattedJSONFileFromTiff() {
		LocalDateTime start = LocalDateTime.now();
		
		String projectRoot = new File("").getAbsolutePath();
		
		try {
			// Add the BestPath toolbox.
			gp.addToolbox(projectRoot + "/resource/customertoolbox/ZCustomer.tbx");
			// Generate the array of parameters.
			VarArray parameters = new VarArray();
			//����tif·��
			parameters.add(projectRoot + "/resource/data/rain_2016.flt");
			//�ز��������б�
			parameters.add("0 0.013435 1;"
					+ "0.013435 0.037422 2;0.037422 0.080247 3;"
					+ "0.080247 0.156709 4;0.156709 0.293223 5;"
					+ "0.293223 0.536956 6;0.536956 0.972118 7;"
					+ "0.972118 1.749056 8;1.749056 3.136204 9;"
					+ "3.136204 5.612822 10");
			//���json·��
			parameters.add(projectRoot + "/resource/result/rain_2016.json");
			// Execute the model tool by name.
			IGeoProcessorResult result = gp.execute("ProduceJsonFromFltWithNoProject", parameters, null);
			while (result.getStatus() == esriJobStatus.esriJobSucceeded){
				
				System.out.println(result.getOutputCount());
				
				String resultJsonPath = (String) result.getReturnValue();
				System.out.println(resultJsonPath);
				
				//��ȡjson�ļ�
				BufferedReader reader = new BufferedReader(new FileReader(new File(resultJsonPath)));
				StringBuffer sb = new StringBuffer();
				String line = reader.readLine();
				while(line != null) {
					sb.append(line);
					line = reader.readLine();
				}
				
				JSONObject jsonObject = JSONObject.parseObject(sb.toString());
				
			    System.out.println(result.getMessageCount());
			    break;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LocalDateTime end = LocalDateTime.now();
		Duration duration = Duration.between(start, end);
		
		System.out.println("�Զ���gp���к�ʱ��" + duration.toMillis() + "����");
		
	}
	
	
	public static void main(String[] args) {
		try {
			// Initialize the engine and licenses.
			EngineInitializer.initializeEngine();
			AoInitialize aoInit = new AoInitialize();
			initializeArcGISLicenses(aoInit);
			CustomerToolbox customerToolbox = new CustomerToolbox();
			customerToolbox.produceFormattedJSONFileFromTiff();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the lowest available ArcGIS License
	 */
	private static void initializeArcGISLicenses(AoInitialize aoInit)
	{
		try
		{
			if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeEngine) 
					== esriLicenseStatus.esriLicenseAvailable)
			{
				aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeEngine);
			}
			if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeAdvanced) 
					== esriLicenseStatus.esriLicenseAvailable)
			{
				aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeAdvanced);
			}
			else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeBasic) 
					== esriLicenseStatus.esriLicenseAvailable)
			{
				aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeBasic);
			}
			else
			{
				System.err.println("Could not initialize an Engine or Basic License. Exiting application.");
				System.exit(-1);
			}
			
			aoInit.checkOutExtension(esriLicenseExtensionCode.esriLicenseExtensionCode3DAnalyst);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}	
}
