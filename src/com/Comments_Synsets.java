package com;

import java.util.HashMap;
import java.util.Iterator;




public class Comments_Synsets {
	
	public static HashMap<String,Integer> tfidf=new HashMap<String,Integer>();
	public static int nComments=0;

	public static String[] expandWords(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void tfidf_calc()
	{
		Iterator<Integer> comments_iterator=Helper_Class.CommentWordCount.keySet().iterator();
		
		while(comments_iterator.hasNext())
		{
			int comment_id=comments_iterator.next();
			nComments++;
			Iterator<String> strins=Helper_Class.CommentWordCount.get(comment_id).keySet().iterator();
			while(strins.hasNext())
			{
				String str=strins.next();
				if(tfidf.containsKey(str))
				{
					int value=tfidf.get(str);
					value++;
					tfidf.put(str,value);
				}else
					tfidf.put(str,Helper_Class.CommentWordCount.get(comment_id).get(str) );
			}
		}
		System.out.println("Vocabulary::");
		Iterator<String> iter=tfidf.keySet().iterator();
		while(iter.hasNext())
		{
			String u=iter.next();
			System.out.println(u+" : "+tfidf.get(u));
		}
		
		
		
	}

}
