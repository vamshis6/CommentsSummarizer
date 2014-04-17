
package com;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


// find the quotations in the comments

public class CommentsQuotat {
	
	public static HashMap<Integer,Integer> quotations=new HashMap<Integer,Integer>();
	public static HashMap<Integer,HashMap<String,Integer>> quotes=new HashMap<Integer,HashMap<String,Integer>>();
	
	public static void findQuotations()
	{
		try
		{
			Iterator<Integer> iter=CommentSummarizer_Main.comments.keySet().iterator();
			while(iter.hasNext())
			{
				int comment_id=iter.next();
				String comments_srt=CommentSummarizer_Main.comments.get(comment_id).trim();
				int length=comments_srt.length();
				int nex_pos=0;
				boolean quotat=false;
				int first_pos=comments_srt.indexOf('"',nex_pos);
				HashMap<String,Integer> set=new HashMap<String,Integer>();
				while(first_pos!=-1)
				{
					quotat=true;
					nex_pos=comments_srt.indexOf('"', first_pos+1);
					//System.out.println("CommentId"+comment_id);
					String st=null;
					if(nex_pos<0)
						break;
					st=comments_srt.substring(first_pos+1,nex_pos);
					StringBuilder stt=new StringBuilder();
					st=st.toLowerCase().replaceAll("[^a-z]"," ");
					//System.out.println(st);
					if(set.containsKey(st))
					{
						int val=set.get(st);
						val++;
						set.put(st, val);
					}else
						set.put(st, 1);
					first_pos=comments_srt.indexOf('"',nex_pos+1);
				}	
				if(quotat)
					quotes.put(comment_id, set);
			}
			//System.out.println("Calling hIm");
			quotations_doit();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * 
	 * function_name::quotations_doit
	 * 
	 	All the comments which have related quoted comments will be found out and maintained in quotes map
	 */
	
	public static void quotations_doit()
	{
		Iterator<Integer> iter=quotes.keySet().iterator();
		Iterator<Integer> iter2=quotes.keySet().iterator();
		while(iter.hasNext())
		{
			int comment_id=iter.next();
			Iterator<String> str_iter=quotes.get(comment_id).keySet().iterator();
			HashSet<String> temp=new HashSet<String>();
			while(str_iter.hasNext())
			{
				String str=str_iter.next();
				String[] tokens=str.split(" ");
				int i=0;
				while(i<tokens.length)
				{
					if(tokens[i].length()>2 && !CommentSummarizer_Main.stopwords.contains(tokens[i]))
						temp.add(tokens[i]);
					i++;
				}
				while(iter2.hasNext())
				{
					int cid=iter2.next();
					if(comment_id<cid)
					{
						Iterator<String> sier=quotes.get(cid).keySet().iterator();
						while(sier.hasNext())
						{
							String str2=sier.next();
							String[] tokens2=str2.split(" ");
							int k=0;
							int count=0;
							while(k<tokens2.length)
							{
								if(temp.contains(tokens2[k]))
								{
									count++;
								}
								k++;
							}
							if(count>0)
							{
								if(quotations.containsKey(comment_id))
								{
									int val=quotations.get(comment_id);
									val++;
									quotations.put(comment_id, val);
								}else
									quotations.put(comment_id, 1);
							
								if(quotations.containsKey(cid))
								{
									int val=quotations.get(cid);
									val++;
									quotations.put(cid, val);
								}else
									quotations.put(cid, 1);
							}
						}
					}
				}
				temp.clear();
			}
		}
		//System.out.println(" HHh");
		
		/**
		
		System.out.println("Quotation analysis");
		Iterator<Integer> itter=quotations.keySet().iterator();
		while(itter.hasNext())
		{
			int id=itter.next();
			System.out.println("comment_id: "+id+"  value:"+quotations.get(id));
		}
		
		*/
	}
		
}
	
	

