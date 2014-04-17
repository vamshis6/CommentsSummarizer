/*
 * Author : Vamshi S.
 * 
 *POSTagger
 * Input ::  XML file, for which we are supposed to retreiver al the named entities i.e nouns
 * Output :: A text file, with the name same as the given input xml file but in text format
 *
 *It uses StanfordPOS tagger.
 *
 */
package com;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.nlp.io.OutDataStreamFile;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class POSTagger extends DefaultHandler {

	
	public static ArrayList<String> comments=new ArrayList<String>();
	public static boolean comments_section=false,isText=false;
	public static StringBuilder sbr=new StringBuilder();
	public static int comments_count=0;
	public static HashSet<String> stopwords=new HashSet<String>();
	public static int blog_id;
	public static StringBuilder blogText=new StringBuilder();
	public static boolean isBlogText=true,blog=false;
	
	public static String filename,opfile;
	
	public void startDocument()throws SAXException
    {
        System.out.println("Start Parsing Document:");
    }
	 
	public void endDocument()throws SAXException
	 {    	
			System.out.println("Document Parsed Completely:"); 
	 }
	
	 public void startElement(String namespaceURI, String localName, String qName, Attributes atts)throws SAXException
	 {
		 if(qName.equals("text") && isBlogText==true)
		 {
			 blog=true;
			 isText=true;
		 }
		 
		 if(qName=="comments")
		 {
			 comments_section=true;
		 }else if(qName=="text" && comments_section==true)
		 {
			comments_count++;
			isText=true;
		 }
	}
	 
	 public void endElement(String namespaceURI, String localName, String qName)throws SAXException
	 {
		 if(qName=="text" && isBlogText==true)
		 {
			 isBlogText=false;
			 blog=false;
			 isText=false;
		 }
		 if(qName=="comments")
		 {
			 comments_section=false;
		 }else if(qName=="text" && comments_section==true)
		 {
			 //System.out.println(sbr.toString());
			 comments.add(sbr.toString());
			 sbr.setLength(0);
			 //System.exit(0);
			 isText=false;
		 }
		 
	 }
	 
	 public void characters(char buffer [], int offset, int length)throws SAXException
	 {
		 
		 if(blog==true && isText==true)
		 {
			 blogText.append(buffer,offset,length);
		 }
		 
		 if(comments_section==true && isText==true)
		 {
			 sbr.append(buffer,offset,length);
		}
		 
		 
	 }
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			
			System.out.println(args[0]);
			filename=args[0];
			
			int blogid=0;
			if(filename.charAt(filename.indexOf('.')-1)>='0' && filename.charAt(filename.indexOf('.')-1)<='9')
				blogid=blogid*10+(filename.charAt(filename.indexOf('.')-1)-'0');
			if(filename.charAt(filename.indexOf('.')-2)>='0' && filename.charAt(filename.indexOf('.')-2)<='9')
				blogid=blogid*10+(filename.charAt(filename.indexOf('.')-2)-'0');
			
			opfile="/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/Entities/Blog"+blogid+".txt";
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			POSTagger pos=new POSTagger();
	        saxParser.parse( new File(filename), pos); 
	        // Load those stopwords !
	        File f=new File("/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/stopwords.txt");
	        
	        InputStream fs=new FileInputStream(f);
	        Reader fin=new InputStreamReader(fs);
	        //BufferedInputStream bis=new BufferedInputStream(fin);
	        BufferedReader br=new BufferedReader(fin);
	        String line;
	        while ((line = br.readLine()) != null) 
			{
	        	//System.out.println(line);
	        	stopwords.add(line);
	        }
	        pos.write_entities();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	
	}

	public void write_entities()
	{
		try
		{
			MaxentTagger tagger = new MaxentTagger("/media/vamshi/Vams/eclipse_work_space/IRE_MajorWebFINAL/CommentsSummarizer/taggers/left3words-wsj-0-18.tagger");
			Iterator<String> it=comments.iterator();
			ArrayList<String> ar=new ArrayList();
			Pattern wildchars=Pattern.compile("[^a-z ]");
			int no_of_comments=0;
			
			// Removing all the Waste Characters from the comments and Storing them in the Arraylist !!
			while(it.hasNext())
			{
				String s=it.next().toLowerCase();
				s=wildchars.matcher(s).replaceAll(" ");
				ar.add(s);
				no_of_comments++;
			}
			int i=0;
			
			// Calling each string to stanford POS Tagger !
			i=0;
			ArrayList<String> tagged_strings = new ArrayList<String>();
			while(i<no_of_comments)
			{
				String tagged = tagger.tagString(ar.get(i));
				tagged_strings.add(tagged);
				//System.out.println(tagged);
				i++;
			}
			
			i=0;
			HashMap<String,Integer> tokens=new HashMap<String,Integer>();
			while(i<no_of_comments)
			{
				//System.out.println(tagged_strings.get(i).trim());
				String[] local_tokens=tagged_strings.get(i).trim().split(" ");
				int length=local_tokens.length;
				int j=0;
				while(j<length)
				{
					if( local_tokens[j].length()>4 && local_tokens[j].contains("/NN"))
					{
						int pos=local_tokens[j].indexOf('/');
						String keyword=local_tokens[j].substring(0, pos);
						if(stopwords.contains(keyword))
						{
							j++;
							continue;
						}
						keyword=Stemming(keyword);
						
						if(tokens.containsKey(keyword))
						{
							int value=tokens.get(keyword);
							tokens.put(keyword,value+1);
						}else
							tokens.put(keyword,1);
					}
					j++;
				}
				i++;
			}
			File f=new File(opfile);
			System.out.println(opfile);
			System.out.println(filename);
			//FileWriter fr= new FileWriter(f);
			OutputStream out=new OutDataStreamFile(f);
			OutputStreamWriter osw=new OutputStreamWriter(out);
			Iterator<String> itr=tokens.keySet().iterator();
			while(itr.hasNext())
			{
				String s=itr.next();
				if(s.contains("http") || s.contains("html"))
				{
					continue;
				}
				StringBuilder sbr=new StringBuilder(s);
				sbr.append(':');
				sbr.append(tokens.get(s)+"\n");
				osw.write(sbr.toString());
			}
			osw.close();
			
			comments.clear();
			comments_section=false;
			isText=false;
			sbr.setLength(0);;
			comments_count=0;
			stopwords.clear();;
			blogText.setLength(0);
			isBlogText=true;
			blog=false;
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		

	}
	public static String Stemming(String str)
	{
		Stemmer stm=new Stemmer();
		String s = str.toLowerCase();
		stm.add(s.toLowerCase().toCharArray(),s.length());
		stm.stem();
		String s1=stm.toString();
		return s1;
	}
	
}
