package net.relinc.libraries.staticClasses;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.relinc.libraries.sample.CompressionSample;
import net.relinc.libraries.sample.LoadDisplacementSample;
import net.relinc.libraries.sample.Sample;
import net.relinc.libraries.sample.ShearCompressionSample;
import net.relinc.libraries.sample.TensionRectangularSample;
import net.relinc.libraries.sample.TensionRoundSample;
import net.relinc.libraries.application.BarSetup;
import net.relinc.libraries.application.FileFX;
public final class SPOperations {

	//public static String folderImageLocation = "/images/folderIcon.jpeg";
	public static String folderImageLocation = "/net/relinc/libraries/images/folder.png";
	public static String compressionImageLocation = "/net/relinc/libraries/images/Steel Cylinder.jpg";
	public static String tensionRectImageLocation = "/net/relinc/libraries/images/Tensile Icon.png";
	public static String tensionRoundImageLocation = "/net/relinc/libraries/images/Tensile Round Sample.png";
	public static String loadDisplacementImageLocation = "/net/relinc/libraries/images/LD.png";
	public static String strainGaugeImageLocation = "/net/relinc/libraries/images/strainGaugeImage.png";
	public static String relLogoImageLocation = "/net/relinc/libraries/images/rel-logo.png";
	public static String surePulseLogoImageLocation = "/net/relinc/libraries/images/SURE-Pulse_DP_Logo.png";

	public static Node getIcon(String location){
		return getIcon(location, 16);
	}
	
	public static Node getIcon(String location, int size){
		ImageView rootIcon = new ImageView(
				new Image(SPOperations.class.getResourceAsStream(location))//"/images/folderIcon.jpeg"))
				);
		double height = rootIcon.getImage().getHeight();
		double width = rootIcon.getImage().getWidth();
		if(height / width > 1){
			//it's taller than wide
			rootIcon.setFitWidth(size * (width / height));
			rootIcon.setFitHeight(size);
		}
		else{
			//it's wider than tall
			rootIcon.setFitHeight(size * (height / width));
			rootIcon.setFitWidth(size);
		}
		Node a = rootIcon;
		return a;
	}

	public static boolean launchSureAnalyze(Stage stage, URL fxmlLocation) throws IOException {
		Stage primaryStage = new Stage();
		FXMLLoader root = new FXMLLoader(fxmlLocation);
		Scene scene = new Scene(root.load());
		primaryStage.setScene(scene);
		primaryStage.showAndWait();
		
		return true;
	}

	public static boolean writeExcelFileUsingEpPlus(String jobFileLocation) {
		Runtime runTime = Runtime.getRuntime();
		String cmd = null;		
		if(SPSettings.currentOS.contains("Win")) {
			cmd = "libs/ExcelMakerConsole.exe \""+jobFileLocation+"\"";
			System.out.println(cmd);
			try {
				Process p = runTime.exec(cmd);
				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false; //mac
	}

	public static void writeStringToFile(String file, String path){
		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(file);

			//Close writer
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeListToFile(ArrayList<String> list, String path){
		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
			for(String s : list) {
				writer.write(s);
			}

			//Close writer
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String readStringFromFile(String path){
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			text = sb.toString();
			br.close();
		}
		catch(IOException a){
			a.printStackTrace();
		}
		return text;
	}

	public static void deleteFolder(File folder) {

	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}

	public static void findFiles(File dir, TreeItem<String> parent, TreeView<String> treeView, String folderPath, String filePath) {
		TreeItem<String> root = new TreeItem<>(dir.getName(), getIcon(folderPath));
		root.setExpanded(true);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				findFiles(file,root,treeView,folderPath,filePath);
			} else {
				if(file.getName().endsWith(".txt"))
					root.getChildren().add(new TreeItem<>(file.getName().substring(0, file.getName().length() - 4), getIcon(filePath)));
			}
		}
		if(parent==null){
			treeView.setRoot(root);
		} else {

			parent.getChildren().add(root);
		}
	} 

	// This method is never used.
	public static double[] getFittedData(double[] rawXData, double[] rawYData, int inclusiveBegin, int inclusiveEnd, int degree){
		//		if(rawXData.length != rawYData.length)
		//			throw new Exception("Cannot fit data where the xdata is differnt length than ydata");

		double[] fittedData = new double[rawYData.length];
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
		final WeightedObservedPoints obs = new WeightedObservedPoints();
		for(int i = inclusiveBegin; i <= inclusiveEnd; i++){
			obs.add(rawXData[i], rawYData[i]);
		}


		final double[] coeff = fitter.fit(obs.toList());

		PolynomialFunction func = new PolynomialFunction(coeff);

		for(int i = 0; i < fittedData.length; i++){
			if(i < inclusiveBegin || i > inclusiveEnd){
				fittedData[i] = rawYData[i];
			}
			else{
				fittedData[i] = func.value(rawXData[i]);
			}
		}

		return fittedData;
	}

	public static String getPathFromTreeViewItem(TreeItem<String> item) {
		if(item == null)
		{
			System.out.println("cannot get path from null tree object.");
			return "";
		}
		String path = item.getValue();
		while(item.getParent() != null){
			item = item.getParent();
			path = item.getValue() + "/" + path;
		}
		return path;
	}

	public static String getPathFromFXTreeViewItem(TreeItem<FileFX> item) {
		if(item == null)
		{
			System.out.println("cannot get path from null tree object.");
			return "";
		}
		return item.getValue().file.getPath();
	}

	public static void copyFolder(File src, File dest)
			throws IOException{

		if(!src.exists())
			System.out.println("Tried to copy source: " + src.getPath() + " but it does not exist");

		if(src.isDirectory()){

			//if directory not exists, create it
			if(!dest.exists()){
				dest.mkdir();
				System.out.println("Directory copied from " 
						+ src + "  to " + dest);
			}


			//list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile,destFile);
			}

		}else{
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest); 

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes 
			while ((length = in.read(buffer)) > 0){
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
		}
	}

	public static String getSampleType(String samplePath){
		//Sample sample = null;
		File tempUnzippedSample = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse" + "/TempUnzipLocation2");
		ZipFile zippedSample = null;
		try {
			zippedSample = new ZipFile(samplePath);
		} catch (ZipException e) {
			e.printStackTrace();
		}
		if(tempUnzippedSample.exists())
			SPOperations.deleteFolder(tempUnzippedSample);
		tempUnzippedSample.mkdir();

		try {
			zippedSample.extractAll(tempUnzippedSample.getPath());
		} catch (ZipException e) {
			e.printStackTrace();
		}

		if(!new File(tempUnzippedSample + "/Parameters.txt").exists())
			return null;

		String parametersString = SPOperations.readStringFromFile(tempUnzippedSample + "/Parameters.txt");

		return getSampleTypeFromSampleParametersString(parametersString);

	}

	public static Sample loadSampleParametersOnly(String samplePath) throws ZipException{
		Sample sample = null;
		File tempUnzippedSample = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse" + "/TempUnzipLocation");
		ZipFile zippedSample = new ZipFile(samplePath);
		if(tempUnzippedSample.exists())
			SPOperations.deleteFolder(tempUnzippedSample);
		tempUnzippedSample.mkdir();

		zippedSample.extractAll(tempUnzippedSample.getPath());

		String parametersString = SPOperations.readStringFromFile(tempUnzippedSample + "/Parameters.txt");

		String sampleType = getSampleTypeFromSampleParametersString(parametersString);


		switch (sampleType.trim()) {
		case "Compression Sample":
			sample = new CompressionSample();
			break;
		case "Tension Rectangular Sample":
			sample = new TensionRectangularSample();
			break;
		case "Tension Round Sample":
			sample = new TensionRoundSample();
			break;
		case "Shear Compression Sample":
			sample = new ShearCompressionSample();
			break;
		case "Load Displacement Sample":
			sample = new LoadDisplacementSample();
			break;
		default:
			System.out.println("Invalid sample type: " + sampleType);
		}
		if(sample == null)
			return sample;

		String parameters = SPOperations.readStringFromFile(tempUnzippedSample + "/Parameters.txt");
		sample.setParametersFromString(parameters);

		if(new File(tempUnzippedSample + "/Descriptors.txt").exists()){
			String descriptors = SPOperations.readStringFromFile(tempUnzippedSample + "/Descriptors.txt");
			sample.setDescriptorsFromString(descriptors);
		}

		return sample;
	}
	
	public static Sample loadSample(String samplePath) throws Exception { //samplePath is a zipfile.
		Sample sample = null;
		File tempUnzippedSample = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse" + "/TempUnzipLocation");
		ZipFile zippedSample = new ZipFile(samplePath);
		if(tempUnzippedSample.exists())
			SPOperations.deleteFolder(tempUnzippedSample);
		tempUnzippedSample.mkdir();

		zippedSample.extractAll(tempUnzippedSample.getPath());

		String parametersString = SPOperations.readStringFromFile(tempUnzippedSample + "/Parameters.txt");

		String sampleType = getSampleTypeFromSampleParametersString(parametersString);


		switch (sampleType.trim()) {
		case "Compression Sample":
			sample = new CompressionSample();
			break;
		case "Tension Rectangular Sample":
			sample = new TensionRectangularSample();
			break;
		case "Tension Round Sample":
			sample = new TensionRoundSample();
			break;
		case "Shear Compression Sample":
			sample = new ShearCompressionSample();
			break;
		case "Load Displacement Sample":
			sample = new LoadDisplacementSample();
			break;
		default:
			System.out.println("Invalid sample type: " + sampleType);
		}
		if(sample == null)
			return sample;
		
		if(new File(tempUnzippedSample.getPath() + "/Images.zip").exists())
			sample.hasImages = true;
		
		sample.loadedFromLocation = new File(samplePath);
			
		String parameters = SPOperations.readStringFromFile(tempUnzippedSample + "/Parameters.txt");
		sample.setParametersFromString(parameters);

		if(new File(tempUnzippedSample + "/Descriptors.txt").exists()){
			String descriptors = SPOperations.readStringFromFile(tempUnzippedSample + "/Descriptors.txt");
			sample.setDescriptorsFromString(descriptors);
		}

		//find the zip file
		String barSetupZipFile = "";
		for(File f : tempUnzippedSample.listFiles()){
			if(f.getName().endsWith(".zip") && !f.getName().equals("Images.zip"))
				barSetupZipFile = f.getPath();
		}

		if(barSetupZipFile != "")
			sample.barSetup = new BarSetup(barSetupZipFile);

		try {
			sample.populateSampleDataFromDataFolder(tempUnzippedSample + "/Data", sample.barSetup);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//SPOperations.deleteFolder(tempUnzippedSample);
		return sample;
	}

	private static String getSampleTypeFromSampleParametersString(String parametersString) {
		for(String line : parametersString.split(SPSettings.lineSeperator)){
			if(line.split(":").length > 1){
				String description = line.split(":")[0];
				String val = line.split(":")[1];
				if(description.equals("Sample Type")){
					return val;
				}
			}
		}
		return null;
	}

	public static double[] getDerivative(double[] time, double[] data) {
		if(time.length==0){
			return null;
		}
		double[] deriv = new double[time.length];
		deriv[0]=0;
		for(int i = 1; i < deriv.length; i++){
			deriv[i] = (data[i] - data[i-1]) / (time[i] - time[i-1]);
		}
		return deriv;
	}

	public static void prepareAppDataDirectory(){
		File appDataFolder = new File(SPSettings.applicationSupportDirectory + "/RELFX");
		if(!appDataFolder.exists()){
			System.out.println(appDataFolder.mkdir());
		}
		
		File logFolder = new File(SPSettings.applicationSupportDirectory + "/RELFX/Log");
		if(!logFolder.exists())
			System.out.println(logFolder.mkdir());

		File SPfolder = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse");
		if(!SPfolder.exists()){
			System.out.println(SPfolder.mkdir());
		}

		File tempFileDir = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse/Temp");
		if(!tempFileDir.exists()){
			System.out.println(tempFileDir.mkdir());
		}
		File tempSampleDataDir = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse/TempSampleData");

		SPOperations.deleteFolder(tempSampleDataDir);
		if(!tempSampleDataDir.mkdir())
			System.out.println("Failed to create tempSampleDataDir");
			
		File globalBarSetups = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse/Bar Setups");
		if(!globalBarSetups.exists())
			globalBarSetups.mkdir();
		
		File globalStrainGauges = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse/Strain Gauges");
		if(!globalStrainGauges.exists())
			globalStrainGauges.mkdir();
		
		File tempDICStrainExportLocation = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse/TempDICStrainExport");
		if(!tempDICStrainExportLocation.exists())
			tempDICStrainExportLocation.mkdir();
	}

	public static void prepareWorkingDirectory(){
		String path = SPSettings.Workspace.getPath();
		File homefolder = new File(path);
		
		if(!homefolder.exists()){
			return;
		}

		File SGfolder = new File(path + "/Strain Gauges");
		if(!SGfolder.exists()){
			System.out.println(SGfolder.mkdir());
		}
		File barSetupFolder = new File(path + "/Bar Setups");
		if(!barSetupFolder.exists()){
			System.out.println(barSetupFolder.mkdir());
		}
		File dataFileInterpreterDir = new File(path + "/Data File Interpreters");
		if(!dataFileInterpreterDir.exists()){
			System.out.println(dataFileInterpreterDir.mkdir());
		}


		File sampleDataDir = new File(path + "/Sample Data");
		if(!sampleDataDir.exists()){
			System.out.println(sampleDataDir.mkdir());
		}

		convertSampleZips(sampleDataDir);
	}

	public static void convertSampleZips(File dir){

		for(File file : dir.listFiles()){
			if(file.isDirectory()){
				convertSampleZips(file);
			}
			else{
				if(file.getName().endsWith(".zip"))
					changeSampleZipExtension(file);
			}
		}
	}

	public static void changeSampleZipExtension(File sampleZip){
		String type = SPOperations.getSampleType(sampleZip.getPath());
		if(type != null){
			if(type.equals("Compression Sample"))
				sampleZip.renameTo(new File(stripExtension(sampleZip.getPath()) + SPSettings.compressionExtension));
			if(type.equals("Shear Compression Sample"))
				sampleZip.renameTo(new File(stripExtension(sampleZip.getPath()) + SPSettings.shearCompressionExtension));
			if(type.equals("Tension Rectangular Sample"))
				sampleZip.renameTo(new File(stripExtension(sampleZip.getPath()) + SPSettings.tensionRectangularExtension));
			if(type.equals("Tension Round Sample"))
				sampleZip.renameTo(new File(stripExtension(sampleZip.getPath()) + SPSettings.tensionRoundExtension));
			if(type.equals("Load Displacement Sample"))
				sampleZip.renameTo(new File(stripExtension(sampleZip.getPath()) + SPSettings.loadDisplacementExtension));
		}
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static int findFirstIndexGreaterorEqualToValue(double[] data, double val){
		for(int i = 0; i < data.length; i++){
			if(data[i] >= val){
				return i;
			}
		}
		return -1;
	}

	public static int findFirstIndexGreaterThanValue(double[] data, double val){
		for(int i = 0; i < data.length; i++){
			if(data[i] > val){
				return i;
			}
		}
		return -1;
	}

	public static double[] integrate(double[] x, double[] y, int beginInclusive, int endInclusive){
		int arraySize = endInclusive - beginInclusive + 1;
		arraySize = Math.max(0, arraySize);
		double[] integral = new double[arraySize];
		if(!(x.length == y.length && beginInclusive < y.length && endInclusive < y.length))
			return integral; // zeroed array.
		for(int i = 0; i < integral.length; i++){

			if(i == 0){
				integral[i] = 0;
			}
			else{
				integral[i] = integral[i - 1] + y[i + beginInclusive] * (x[i + beginInclusive] - x[i + beginInclusive - 1]);
			}
		}
		return integral;
	}


	public static boolean specialCharactersAreNotInTextField(TextField tf) {
		char[] chars = tf.getText().toCharArray();

		for (char c : chars) {
			if(!Character.isLetter(c) && !Character.isDigit(c) && !Character.isWhitespace(c) && c != '-' && c != '(' && c != ')') {
				return false;
			}
		}

		return true;
	}
	
	public static boolean tfContainsNumbers(TextField tf) {
		char[] chars = tf.getText().toCharArray();

		for (char c : chars) {
			if(Character.isDigit(c)) {
				return true;
			}
		}

		return false;
	}

	public static List<Integer> countContentsInFolder(File folder){
		ArrayList<Integer> filesFolders = new ArrayList<Integer>();
		filesFolders.add(new Integer(0));
		filesFolders.add(new Integer(0));
		if(folder == null || !folder.exists() || !folder.isDirectory())
			return filesFolders;
		return recursivelyCountFolderContents(folder, filesFolders);
	}

	private static List<Integer> recursivelyCountFolderContents(File folder, List<Integer> filesFolders) {
		for(File f : folder.listFiles()){
			if(!f.isDirectory())
				filesFolders.set(0, filesFolders.get(0) + 1);
		}
		for(File f : folder.listFiles()){
			if(f.isDirectory()){
				filesFolders.set(1, filesFolders.get(1) + 1);
				recursivelyCountFolderContents(f, filesFolders);
			}
		}
		return filesFolders;
	}

	public static String stripExtension (String str) {
		if (str == null) 
			return null;

		int pos = str.lastIndexOf(".");

		if (pos == -1) 
			return str;

		return str.substring(0, pos);
	}
	
	public static String getExtension (String str) {
		if (str == null) 
			return null;

		int pos = str.lastIndexOf(".");

		if (pos == -1) 
			return str;

		return str.substring(pos, str.length());
	}
	
	public final static String toHexString(Color color) throws NullPointerException {
		return String.format( "#%02X%02X%02X",
	            (int)( color.getRed() * 255 ),
	            (int)( color.getGreen() * 255 ),
	            (int)( color.getBlue() * 255 ) );
		}
	
	public static String getLatestDataProcessorVersionAvailable(){
		URL u;
		String line = "";
		try {
			u = new URL("http://www.relinc.net/surepulse/latestversions.php");
			URLConnection c = u.openConnection();
			InputStream r = c.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(r));
			line = reader.readLine();
			//for(String line; (line = reader.readLine()) != null;) System.out.println(line);
		} catch (MalformedURLException e) {
			//no internet connnection
			e.printStackTrace();
		} catch (IOException e) {
			//no internet
			e.printStackTrace();
		}
		if(line.equals(""))
			return null;
		JsonObject jsonObject = new JsonParser().parse(line).getAsJsonObject();

		String b = jsonObject.get("windows").getAsJsonObject().get("Data Processor").toString(); //John
		b = b.replaceAll("\\[", "");
		b = b.replaceAll("\\]", "");
		b = b.replaceAll("\"", "");
		return b;
	}
	
	// Viewer and processor are shipped together
	public static String getLatestDataViewerVersionAvailable(){
		URL u;
		String line = "";
		try {
			u = new URL("http://www.relinc.net/software/latestversions.php");
			URLConnection c = u.openConnection();
			InputStream r = c.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(r));
			line = reader.readLine();
			//for(String line; (line = reader.readLine()) != null;) System.out.println(line);
		} catch (MalformedURLException e) {
			//no internet connnection
			e.printStackTrace();
		} catch (IOException e) {
			//no internet
			e.printStackTrace();
		}
		if(line.equals(""))
			return null;
		JsonObject jsonObject = new JsonParser().parse(line).getAsJsonObject();
		//jsonObject.get("windows").getAsString();

		String b = jsonObject.get("windows").getAsJsonObject().get("Viewer").toString(); //John
		b = b.replaceAll("\\[", "");
		b = b.replaceAll("\\]", "");
		b = b.replaceAll("\"", "");
		return b;
	}
	
	public static String getLatestDataProcessorVersionNumber(){
		String latest = getLatestDataProcessorVersionAvailable();
		if(latest == null)
			return null;
		return latest.split("-")[1].split(".exe")[0];
	}
	
	public static String getLatestDataViewerVersionNumber(){
		String latest = getLatestDataViewerVersionAvailable();
		if(latest == null)
			return null;
		return latest.split("-")[1].split(".exe")[0];
	}

	public static String getDataProcessorVersion() {
		String s = SPOperations.readStringFromFile("libs/surepulseversioninfo.txt");
		if(s == null || s.trim().equals(""))
			return null;
		return s.split(SPSettings.lineSeperator)[0].split(":").length > 1 ? s.split(SPSettings.lineSeperator)[0].split(":")[1] : null;
	}
	
	public static void exportWorkspaceToZipFile(File workspace, File dir) {
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(dir.getPath() + "/" + workspace.getName() + ".zip");

			// Initiate Zip Parameters which define various properties such
			// as compression method, etc.
			ZipParameters parameters = new ZipParameters();
			// set compression method to store compression
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			// Set the compression level
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

			// Add folder to the zip file
			for(File f : workspace.listFiles()){
				if(f.isDirectory())
					zipFile.addFolder(f, parameters);
				else
					zipFile.addFile(f, parameters);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportImagesToVideo(String imagesString, String videoExportString, double frameRate) {
		
    	String[] command = {SPSettings.getFFMpegBinary(), "-framerate", Double.toString(frameRate), "-i", imagesString, "-pix_fmt", "yuv420p", videoExportString};
  
        for(int i = 0; i < command.length; i++)
        	System.out.println(command[i]);
        File errorFile = new File(SPSettings.applicationSupportDirectory + "/RELFX/ffmpegErrorFile.txt"); 
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectError(errorFile);
        Process p;
		try {
			p = pb.start();
			p.waitFor();
			p.destroyForcibly();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void exportVideoToImages(String videoLocation, String tempImagesExportLocation, double frameRate) {
		//ffmpeg -i video.webm -vf fps=1 image-%03d.png 
		String[] command = {SPSettings.getFFMpegBinary(),"-i", videoLocation, "-vf", "fps=" + Double.toString(frameRate), tempImagesExportLocation + "/im-%04d.png"};
		for(int i = 0; i < command.length; i++)
        	System.out.println(command[i]);
        File errorFile = new File(SPSettings.applicationSupportDirectory + "/RELFX/ffmpegErrorFile.txt"); 
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectError(errorFile);
        Process p;
		try {
			p = pb.start();
			p.waitFor();
			p.destroyForcibly();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void copyImagesToSampleFile(File savedImagesLocation, String sampleZipPath) {
		File tempDir = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse/Temp/");
		SPOperations.deleteFolder(tempDir);
		tempDir.mkdirs();
		
		//zipFile = new ZipFile(tempDir + "/" + barSetup.barSetupName + ".zip");
		
		//create image zip file
		ZipFile imageZipFile = null;// TODO this sucks

		try {
			File imagesZipFile = new File(savedImagesLocation.getParent() + "/Images" + ".zip");
			if(imagesZipFile.exists())
				imagesZipFile.delete();
			
			imageZipFile = new ZipFile(imagesZipFile);
			// Initiate Zip Parameters which define various properties such
			// as compression method, etc.
			ZipParameters imageZipParameters = new ZipParameters();

			// set compression method to store compression
			imageZipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

			// Set the compression level
			imageZipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			
			for(File image : savedImagesLocation.listFiles())
				imageZipFile.addFile(image, imageZipParameters);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		ZipParameters parameters = new ZipParameters();

		// set compression method to store compression
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

		// Set the compression level
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		ZipFile sampleZip;
		try {
			sampleZip = new ZipFile(sampleZipPath);
			sampleZip.addFile(imageZipFile.getFile(), parameters);
//			if(barSetup.IncidentBar != null && barSetup.TransmissionBar != null)
//				sampleZip.addFile(barSetup.createZipFile(tempDir + "/" + barSetup.name).getFile(), parameters);
		} catch (ZipException e) {
			e.printStackTrace();
		}
		
		imageZipFile.getFile().delete();
	}

	public static File extractSampleImagesToDirectory(Sample sampleZipFile, File tempImageLoadLocation) {
		
		File tempUnzippedSample = new File(SPSettings.applicationSupportDirectory + "/RELFX/SUREPulse" + "/TempViewerUnzipLocation");
		
		if(tempUnzippedSample.exists())
			SPOperations.deleteFolder(tempUnzippedSample);
		tempUnzippedSample.mkdir();
		
		ZipFile zippedSample;
		File imagesUnzipLocation = null;
		try {
			zippedSample = new ZipFile(sampleZipFile.loadedFromLocation);
			zippedSample.extractAll(tempUnzippedSample.getPath());
			
			ZipFile imagesZipFile = new ZipFile(new File(tempUnzippedSample + "/Images.zip"));
			imagesUnzipLocation = new File(tempUnzippedSample.getPath() + "/Images");
			
			imagesZipFile.extractAll(imagesUnzipLocation.getPath());
			
		} catch (ZipException e) {
			e.printStackTrace();
		}
		
		return imagesUnzipLocation;
		
	}

	public static BufferedImage getRgbaImage(File imageFile) throws IOException {
		BufferedImage img = ImageIO.read(imageFile);
		BufferedImage rgbImg = new BufferedImage(img.getWidth(),img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		rgbImg.getGraphics().drawImage(img,0,0,null);
		return rgbImg;
	}

}
