package com;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sun.security.jgss.wrapper.SunNativeProvider;



public class CommentSummarizer_Main extends DefaultHandler
{
	
	public static HashSet<String> stopwords = new HashSet<String>();
	
	// will be used in the helper class
	public static String blog_text;
	public static HashMap<Integer,String> comments=new HashMap<Integer,String>();
	public static HashMap<Integer,Integer> mention = new HashMap<Integer,Integer>();
	public static HashMap<Integer,Integer> likes=new HashMap<Integer,Integer>();
	
	public static int comments_count=0;
	
	public static String blogFilename; 
	public static String entityFilename;
	public static String outputFilename;
	
	
	public static int comment_id;
	public static boolean isBlogtext=false,isCommenttext=false,isComments=false,isBlog=true;
	
	
	public static StringBuffer sbr=new StringBuffer();
	// SAX Parser Methods Are Overloaded !!
	
	public void startDocument()throws SAXException
    {
        System.out.println("Started Parsing the Blog !!");
    }

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)throws SAXException
	{
		if(qName.equals("text") && isBlog==true)
		{
			isBlogtext=true;
		}
		else if(qName.equals("comments"))
		{
			isComments=true;
		
		}
		else if(qName.equals("comment"))
		{
			comments_count++;
			comment_id=Integer.parseInt(atts.getValue("id"));
			if(atts.getValue("reply_id")!=null)
			{
				int reply_id=Integer.parseInt(atts.getValue("reply_id"));
				if(mention.containsKey(comment_id))
				{
					int val=mention.get(comment_id);
					val++;
					mention.put(comment_id, val);
				}else
					mention.put(comment_id, 1);
				
				if(mention.containsKey(reply_id))
				{
					int val=mention.get(reply_id);
					val++;
					mention.put(reply_id, val);
				}else
					mention.put(reply_id, 1);
			
			}
			likes.put(comment_id, Integer.parseInt(atts.getValue("likes")));
			
		}
		else if(qName.equals("text") && isComments==true)
		{
			isCommenttext=true;
			
		}
		
	}
	
	 public void endElement(String namespaceURI, String localName, String qName)throws SAXException
	 {
		 	if(qName.equals("text") && isBlog==true)
			{
				isBlogtext=false;
				isBlog=false;
				blog_text=sbr.toString();
				sbr.setLength(0);
			}
		 	else if(qName.equals("comments"))
			{
				isComments=false;
			}
			else if(qName.equals("text") && isComments==true)
			{
				isCommenttext=false;
				comments.put(comment_id,sbr.toString());
				sbr.setLength(0);
			}
	}
	
	 public void characters(char buffer [], int offset, int length)throws SAXException
	 {
		 if(isBlogtext)
		 {
			 sbr.append(buffer,offset,length);
		 }else if(isCommenttext)
		 {
			 sbr.append(buffer,offset,length);
		 }
	 }
	 
	 
	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			loadStopwords();
			// Blogfile is passed as a command line argument
			blogFilename=args[0];
			int index=blogFilename.indexOf('.');
			int blog_id=0;
			if(blogFilename.charAt(index-1)>='0' && blogFilename.charAt(index-1)<='9') 
			{	
				blog_id=(blogFilename.charAt(index-1)-'0');
				
			}
			if(blogFilename.charAt(index-2)>='0' && blogFilename.charAt(index-2)<='9') 
			{	
				blog_id=(blogFilename.charAt(index-2)-'0')*10+blog_id;
			}	
			
			outputFilename="Blog"+blog_id+".txt";
			entityFilename="/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/Entities/Blog"+blog_id+".txt";
			System.out.println(entityFilename);
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			CommentSummarizer_Main summarizer=new CommentSummarizer_Main();
			saxParser.parse(blogFilename,summarizer);
			//summarizer.display();
			summarizer.computation();
			//summarizer.deleteFiles();
			
			
			
			
			Helper_Class.BlogSentences.clear();
			Helper_Class.BlogSentenceWordCount.clear();
			Helper_Class.CommentWordCount.clear();
			Helper_Class.number_of_words=0;
			Helper_Class.maxValue=0;
			Helper_Class.Topic.clear();
			Helper_Class.LineWordCount.clear();
			Helper_Class.multiComment.clear();
			Helper_Class.namedEntity.clear();
			Helper_Class.topComments.clear();
			Helper_Class.sentenceScore.clear();
			Helper_Class.summaryGenerated.setLength(0);
			Helper_Class.TopScoreSentences.clear();
			
		
			stopwords.clear();
			blog_text=null;
			comments.clear();;
			mention.clear();;
			likes.clear();;
			comments_count=0;
			isBlogtext=false;
			isCommenttext=false;
			isComments=false;
			isBlog=true;
			sbr.setLength(0);
			
			
			CommentsQuotat.quotations.clear();
			CommentsQuotat.quotes.clear();
		
			
			
			
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void display()
	{
		System.out.println(blog_text);
		System.out.println("--------------------");
		
		Iterator<Integer> itr = comments.keySet().iterator();
		while(itr.hasNext())
		{
			int id=itr.next();
			System.out.println("Comment ID :: "+id+"\n"+comments.get(id));
		}
		
		System.out.println("Mention ");
		Iterator<Integer> iter=mention.keySet().iterator();
		while(iter.hasNext())
		{
			int id=iter.next();
			System.out.println("Comment ID  :: "+id+" Mention Id "+mention.get(id));
		}
	}
	
	
	
	
	
	public void computation()
	{
		CommentsQuotat.findQuotations();
		Helper_Class.Initiate();
		
	}
	
	
	
	public void deleteFiles()
	{
		try
		{
		File bf=new File("/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/Blogs/Blog 1");
		String[] files=bf.list();
		if(files!=null)
		{
			for(String s: files)
			{
				File f=new File(bf.getAbsoluteFile()+"/"+s);
				f.delete();
			}
		}
		File bf2=new File("/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/Blogs/Blog1.xml");
		bf.delete();
		bf2.delete();
		// Entity File
		File ef=new File("/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/Entities/Blog1.txt");
		ef.delete();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void loadStopwords()
	{
		try
		{
			File f=new File("/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/stopwords.txt");
			BufferedReader br=new BufferedReader(new FileReader(f));
			String stop;
			while((stop=br.readLine())!=null)
			{
				stopwords.add(stop);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	// Inner Class mention for Storing MentionComments !
	

}


