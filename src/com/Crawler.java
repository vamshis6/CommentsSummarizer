package com;
import java.io.OutputStreamWriter;
import java.io.*;

public class Crawler {

	public static Process p;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			File f= new File("/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/src/com/InputCrawler");
			if(!f.exists())
			{
				f.createNewFile();
			}
			FileWriter fw=new FileWriter(f);
			fw.write("1\n");
			fw.write(args[0]);
			fw.flush();
			fw.close();
			
			
			String[] command = {"bash","run.sh"};
			
			long startTime=System.currentTimeMillis();
			ProcessBuilder probuilder = new ProcessBuilder( command );
			probuilder.directory(new File("/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/src/com"));
	        p=probuilder.start();
	        OutputStream os=p.getOutputStream();
	        OutputStreamWriter osw=new OutputStreamWriter(os);
	        BufferedWriter bw=new BufferedWriter(osw);
	        bw.write(args[0]);
	        bw.write("\n");
	        
			InputStream is = p.getInputStream();
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
		    String line;
		    System.out.println("Output of Crawler !");
		    while((line=br.readLine())!=null)
		    	System.out.println(line);
	       System.out.println("Crawling time::"+(System.currentTimeMillis()-startTime)/1000+"sec"); 
	       String[] argu=new String[1];
	       argu[0]="/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/Blogs/Blog1.xml";
	       
	       startTime=System.currentTimeMillis();
	       POSTagger.main(argu);
	       System.out.println("POS Tagger Time"+(System.currentTimeMillis()-startTime)/1000+"sec");
	       startTime=System.currentTimeMillis();
	       CommentSummarizer_Main.main(argu);
	       System.out.println("Summarizing Time"+(System.currentTimeMillis()-startTime)/1000+"sec");
	      // System.exit(0);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
