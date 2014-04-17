package com;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStore.Entry;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;


public class Helper_Class {

	
	// Number of Sentences in the Blog !!
	public static HashMap<Integer, String> BlogSentences=new HashMap<Integer,String>();
	// Word's in each Sentence in the Blog !!
	public static HashMap<Integer,HashMap<String, Integer>> BlogSentenceWordCount=new HashMap<Integer,HashMap<String,Integer>>();
	
	public static HashMap<Integer,HashMap<String,Integer>> CommentWordCount=new HashMap<Integer,HashMap<String,Integer>>();
	// Total number of words in the blog
	public static int number_of_words=0;
	
	public static float maxValue=0;
	public static HashMap<Integer,Integer> Topic=new HashMap<Integer,Integer>();
	public static Pattern pat=Pattern.compile("[ :\'’”“![0-9]{}\\&()\'\"\\-*\\|_=,;.\n\t?]+");
	public static List<Integer> LineWordCount = new ArrayList<Integer>();
	
	public static HashMap<Integer,Float> multiComment=new HashMap<Integer,Float>();
	
	public static HashMap<String,Integer> namedEntity = new HashMap<String,Integer>();
	
	//public static HashMap<Integer,Float> =new HashMap<Integer,Float>();
	
	public static ArrayList<CommentInfo> topComments=new ArrayList<CommentInfo>();
	
	public static HashMap<Integer,Float> sentenceScore=new HashMap<Integer,Float>();
	
	public static StringBuilder summaryGenerated = new StringBuilder("");
	
	
	
	
	
	public static PriorityQueue<CommentInfo> TopScoreSentences = new PriorityQueue<CommentInfo>(10,	new Comparator<CommentInfo>() 
			{	@Override
		public int compare(CommentInfo s1,CommentInfo s2) 
	    {			
			if(s1.score < s2.score)
				return -1;
			if(s1.score > s2.score)
				return 1;
			return 0;
	    }
	});

	
	
	
	
	public static void Initiate() {
		// TODO Auto-generated method stub
		
		// Divide Sentences into a set of Sentences !! and Maintain word Count for Sentences !!
		loadEntityFile();
		
		// Tokenizing the blog contents and the Comment Contents into words :) :)
		blogTexttokenizer();
		
		
		commentWordTokenizer();
		
		// Cosine Similarity on the CommentWords
		cosineSimilarityCW();
		// Comments are provided Weightages based on 3 factors, Topic,Mention,Quoatation
		comments_weightage();
		
		finalScoring(); // Scoring is a fun of entity based score and topic and mention scores !
		
		findTopcomments();
		generateSummary();
		
	
	}
	
	/**
	 * Function:  Generates Summary, By Asssigning weightages to Sentences in the blog by assigning cosine 
	 * similarity over all the top comments
	 *
	 */
	
	public static void generateSummary() 
	{
		CommentInfo commentsData;
		Set<String> Set1 = new HashSet<String>();
		Set<String> Set2 = new HashSet<String>();
		int noTopcomments=topComments.size();
		Iterator<Integer> sentence_iterator=BlogSentenceWordCount.keySet().iterator();
		while(sentence_iterator.hasNext())
		{
			
			
			int senten_ID=sentence_iterator.next();
			// Blog Sentence Words
			Set1=BlogSentenceWordCount.get(senten_ID).keySet();
			for(int i=0;i<noTopcomments;i++)
			{
				
				double similarity=0,product=0;
				double sumA=0,sumB=0;
				
				Vector<Integer> vecA=new Vector<Integer>();
				Vector<Integer> vecB=new Vector<Integer>();
				
				CommentInfo comment=topComments.get(i);
				int comment_id=comment.comment_id;
				// Comment Words
				Set2=CommentWordCount.get(comment_id).keySet();
				Iterator<String> w1=Set1.iterator();
				Iterator<String> w2=Set2.iterator();
				HashSet<String> imp=new HashSet<String>();
				while(w1.hasNext())
				{
					String s=w1.next();
					imp.add(s);
				}
				while(w2.hasNext())
				{
					String s=w2.next();
					imp.add(s);
				}
				for(String s : imp)
				{
					if(Set1.contains(s))
					{
						vecA.add(BlogSentenceWordCount.get(senten_ID).get(s));
					}else
						vecA.add(0);
					
					if(Set2.contains(s))
					{
						vecB.add(CommentWordCount.get(comment_id).get(s));
					}else
						vecB.add(0);
				}
				for(int p=0;p<vecA.size();p++)
				{
					product+=vecA.get(p)*vecB.get(p);
					sumA+=vecA.get(p)*vecA.get(p);
					sumB+=vecB.get(p)*vecB.get(p);
				}
				sumA=Math.sqrt(sumA);
				sumB=Math.sqrt(sumB);
				similarity=product/(sumA*sumB);
				similarity=Math.acos(similarity)*180/Math.PI;
				// each of the top comments count is included here
				if(sentenceScore.containsKey(senten_ID))
				{
					float result=sentenceScore.get(senten_ID);
					result+=similarity;
					sentenceScore.put(senten_ID, result);
				}else
					sentenceScore.put(senten_ID, (float)similarity);
			}
		}
			
		
		
		Iterator<Integer> iter_sen=sentenceScore.keySet().iterator();
		
		while(iter_sen.hasNext())
		{
			int id=iter_sen.next();
			CommentInfo cin = new CommentInfo();
			cin.comment_id=id;
			cin.score=sentenceScore.get(id);
			TopScoreSentences.add(cin);
		}
		
		List<Integer> FinalSentencesList = new ArrayList<Integer>();
		int i=0,count =0;
		int wordCount=(int) (number_of_words*0.10);
		while(count < wordCount)
		{			
			CommentInfo obj = TopScoreSentences.poll();
			FinalSentencesList.add(obj.comment_id);
			count += LineWordCount.get(obj.comment_id);
		}
		Collections.sort(FinalSentencesList);
		count = 0;
	
		summaryGenerated.setLength(0);
		for(i=0;i<FinalSentencesList.size() && count<wordCount;i++)
		{			
			//System.out.print(BlogSentences.get(FinalSentencesList.get(i)).trim());
			if(LineWordCount.get(FinalSentencesList.get(i))>8)
			{	
				
				summaryGenerated.append(BlogSentences.get(FinalSentencesList.get(i)).trim());
				summaryGenerated.append("\n");
				count += LineWordCount.get(FinalSentencesList.get(i));
			
			}
		}
		System.out.println("Generated Summary");
		System.out.println("----------------------------------------------------------------------------------");
		SummarizerServlet.Sumary=summaryGenerated.toString();
		System.out.println(summaryGenerated);
		summaryGenerated.setLength(0);
		System.out.println("----------------------------------------------------------------------------------");
		FinalSentencesList.clear();
		
		try
		{
			File f=new File(CommentSummarizer_Main.outputFilename);
			if(!f.exists())
			{
				f.createNewFile();
			}
			BufferedWriter br=new BufferedWriter(new FileWriter(f));
			br.write(summaryGenerated.toString());
			br.flush();
			
		}catch(Exception e)
		{
			System.out.println("OP Error");
			e.printStackTrace();
		}
	}
	
	public static void loadEntityFile()
	{
		String file_name=CommentSummarizer_Main.entityFilename;
		try
		{
			File f=new File(file_name);
			BufferedReader br=new BufferedReader(new FileReader(f));
			String str;
			while((str=br.readLine())!=null)
			{
				String[] tokens=str.split(":");
				tokens[0]=Stemming(tokens[0]);
				namedEntity.put(tokens[0],Integer.parseInt(tokens[1].trim()));
			}
		}catch(Exception e)
		{
			System.out.println("Error in Opening the Entity File");
		}
		
		/**
		 * Iterator<String> str=namedEntity.keySet().iterator();
		while(str.hasNext())
		{
			System.out.println(str.next());
		}
		 * 
		 */
	}
	
	public static void blogTexttokenizer()
	{
		Locale locale = Locale.US;
		BreakIterator breakIterator =BreakIterator.getSentenceInstance(locale);
		String Sblog_text=CommentSummarizer_Main.blog_text.trim();
		breakIterator.setText(Sblog_text);
		int sentenceId=0;
		int firstIndex=0;
		int boundaryIndex=breakIterator.next();
		//System.out.println("Blog Sentences");
		while(boundaryIndex!=breakIterator.DONE)
		{
			String subString=Sblog_text.substring(firstIndex,boundaryIndex).trim();
			firstIndex=boundaryIndex;
			boundaryIndex=breakIterator.next();
			//System.out.println(sentenceId+"  "+"   "+subString.trim());
			BlogSentences.put(sentenceId,subString);
			subString=subString.toLowerCase();
			String[] words=pat.split(subString);
			HashMap<String,Integer> word_count=new HashMap<String,Integer>();
			number_of_words+=words.length;
			LineWordCount.add(words.length);
			for(int k=0;k<words.length;k++)
			{
				if(words[k].length()>2  && !CommentSummarizer_Main.stopwords.contains(words[k]))
				{
					
					String str=Stemming(words[k]);
					if(word_count.containsKey(str))
					{
						int count=word_count.get(str);
						count++;
						word_count.put(str,count);
					}else
						word_count.put(str,1);
				}
			}
			BlogSentenceWordCount.put(sentenceId, word_count);
			sentenceId++;
		}
		
		/*
		 * Code to display all the word and counts in each sentence !!
		 * 
		 * 
		
		Iterator<Integer> iter=BlogSentenceWordCount.keySet().iterator();
		while(iter.hasNext())
		{
			int id=iter.next();
			System.out.println("Sentence ID :"+id);
			Iterator<String> str=BlogSentenceWordCount.get(id).keySet().iterator();
			while(str.hasNext())
			{
				String st=str.next();
				System.out.println("word :"+st+" count :"+BlogSentenceWordCount.get(id).get(st));
			}
		}
		*/
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
	
	
	public static void commentWordTokenizer()
	{
		String comment_Text;
		int comment_Id;
		Iterator<Integer> iter=CommentSummarizer_Main.comments.keySet().iterator();
		while(iter.hasNext())
		{
			comment_Id=iter.next();
			comment_Text=CommentSummarizer_Main.comments.get(comment_Id).trim().toLowerCase();
			// check here once !!
			comment_Text=comment_Text.replaceAll("[:\'//’”“![0-9]{}@%\\()\'\"\\-*\\|_=,;.\n\t?]+"," ");
			//System.out.println(comment_Id+"\n"+comment_Text);
			String[] words=comment_Text.split(" ");
			String[] expWords;
			int count=0;
			HashMap<String,Integer> map_word=new HashMap<String,Integer>();
			for(int i=0;i<words.length;i++)
			{
				if(words[i].length()>2 && !CommentSummarizer_Main.stopwords.contains(words[i]))
				{
					// Synset's Code for each Word in words!!
					String str=Stemming(words[i]);
					if(map_word.containsKey(str))
					{
						int val=map_word.get(str);
						val++;
						map_word.put(str, val);
					}else
						map_word.put(str, 1);
				}
			}
			CommentWordCount.put(comment_Id, map_word);
		}
		// Calculating Tf-IDF VAlues !!
	//	Comments_Synsets.tfidf_calc();
		/*
		 * 
		 *Displays all the words !! 
		 * 
		 *
		 
		Iterator<Integer> cit=CommentWordCount.keySet().iterator();
		while(cit.hasNext())
		{
			int com_id=cit.next();
			System.out.println("comment Id: "+com_id);
			Iterator<String> strin=CommentWordCount.get(com_id).keySet().iterator();
			while(strin.hasNext())
			{
				String st=strin.next();
				System.out.println(" word: "+st+" count: "+CommentWordCount.get(com_id).get(st));
			}
		}
 		**/
	}
	
	
	/*
	 * 
	 * cos(theta)=a.b/sqrt(a).sqrt(b);
	 * 
	 */
	public static void cosineSimilarityCW()
	{
		Iterator<Integer> ids=CommentWordCount.keySet().iterator();
		while(ids.hasNext())
		{
			int com_id=ids.next();
			Set<String> words1;
			words1=CommentWordCount.get(com_id).keySet();
			Iterator<Integer> com_iter=CommentWordCount.keySet().iterator();
			while(com_iter.hasNext())
			{
				int id=com_iter.next();
				if(com_id<id)
				{
					Set<String> words2;
					words2=CommentWordCount.get(id).keySet();
					
					Vector<Integer> vecA=new Vector<Integer>();
					Vector<Integer> vecB=new Vector<Integer>();
					
					Iterator<String> w1=words1.iterator();
					Iterator<String> w2=words2.iterator();
					
					
					HashSet<String> imp=new HashSet<String>();
					while(w1.hasNext())
					{
						String s=w1.next();
						imp.add(s);
					}
					while(w2.hasNext())
					{
						String s=w2.next();
						imp.add(s);
					}
					for(String s:imp)
					{
						if(CommentWordCount.get(com_id).containsKey(s))
						{
							vecA.add(CommentWordCount.get(com_id).get(s));
						}else
							vecA.add(0);
					
						if(CommentWordCount.get(id).containsKey(s))
						{
							vecB.add(CommentWordCount.get(id).get(s));
						}else
							vecB.add(0);
					}
					
					//System.out.println("Size : A"+vecA.size()+" Size: B"+vecB.size()+"maxLen:"+maxlength);
					double similarity;
					int product=0;
					double sumA=0;
					double sumB=0;
					for(int i=0;i<vecA.size();i++)
					{
						product+=vecA.elementAt(i)*vecB.elementAt(i) ;
						sumA+=vecA.elementAt(i)*vecA.elementAt(i);
						sumB+=vecB.elementAt(i)*vecB.elementAt(i);
					}
					sumA=Math.sqrt(sumA);
					sumB=Math.sqrt(sumB);
					similarity=product/(sumA*sumB);
					similarity=Math.acos(similarity)*180/Math.PI;
					//System.out.println("Result "+com_id+" "+id+" :"+similarity);
					
					if(similarity<75)
					{
						//System.out.println("Result "+com_id+" "+id);
						if(Topic.containsKey(com_id))
						{
							int val=Topic.get(com_id);
							val++;
							Topic.put(com_id, val);
						}else
							Topic.put(com_id, 1);
						if(Topic.containsKey(id))
						{
							int val=Topic.get(id);
							val++;
							Topic.put(id, val);
						}else
							Topic.put(id, 1);
					}
						
				}
			}
		}
	}

	
	// this is purely base on my assumption--> Topic*0.45+Mention*0.20+Likes*0.15+Quot*0.20
	public static void comments_weightage()
	{
		Iterator<Integer> iterator=Topic.keySet().iterator();
		Iterator<Integer> qit=CommentsQuotat.quotations.keySet().iterator();
		
		// Providing Weightage for TOPIC Comments
		while(iterator.hasNext())
		{
			int comment_id=iterator.next();
			int value=Topic.get(comment_id);
			double score=value*0.45;
			multiComment.put(comment_id, (float)score);
		}
		// providing Weightage for Mention
		Iterator<Integer> mention_iter=CommentSummarizer_Main.mention.keySet().iterator();
		while(mention_iter.hasNext())
		{
			int id=mention_iter.next();
			int value=CommentSummarizer_Main.mention.get(id);
			if(multiComment.containsKey(id))
			{
				double ans=multiComment.get(id);
				ans+=value*0.5;
				multiComment.put(id, (float)ans);
			
			}else
			{
				double score=value*0.5;
				multiComment.put(id,(float)score);
			}
		}
		// providing Weightage for Quoatations !
		while(qit.hasNext())
		{
			int co_id=qit.next();
			int val=CommentsQuotat.quotations.get(co_id);
			if(multiComment.containsKey(co_id))
			{
				double ans=multiComment.get(co_id);
				ans+=val*0.05;
				multiComment.put(co_id,(float)ans);
			}else
			{
				double a=val*0.05;
				multiComment.put(co_id,(float)a);
			}
		}
		
		// Giving weightage for the likes of each comment !
		Iterator<Integer> lik=CommentSummarizer_Main.likes.keySet().iterator();
		while(lik.hasNext())
		{
			int co_id=lik.next();
			int val=CommentSummarizer_Main.likes.get(co_id);
			if(multiComment.containsKey(co_id))
			{
				double ans=val*0.15;
				ans+=multiComment.get(co_id);
				multiComment.put(co_id,(float)ans);
			}else
				multiComment.put(co_id, (float)(0.15*val));
		}
		
		/*
		 	Displaying all the Comments_weightage
		 
		Iterator<Integer> it=multiComment.keySet().iterator();
		while(it.hasNext())
		{
			int id=it.next();
			System.out.println("Comment ID:"+id+"Score+"+multiComment.get(id));
		}
		*/
	}


	public static void finalScoring()
	{
		System.out.println("Final Scoring");
		Iterator<Integer> iterator=CommentWordCount.keySet().iterator();
		float totalScore=0;
		int numEntitys;
		
		while(iterator.hasNext())
		{
			numEntitys=0;
			int commentId=iterator.next();
			Iterator<String> commentWorditerator=CommentWordCount.get(commentId).keySet().iterator();
			while(commentWorditerator.hasNext())
			{
				String str=commentWorditerator.next();
				if(namedEntity.containsKey(str))
				{
					numEntitys++;
				}
			}
			
			if(numEntitys!=0)
			{
				if(multiComment.containsKey(commentId))
				{
					totalScore=(float) (numEntitys*5);
					totalScore+=(multiComment.get(commentId)*5);
					if(maxValue<totalScore)
						maxValue=totalScore;
					multiComment.put(commentId, totalScore);
				}else
				{
					totalScore=numEntitys*5;
					multiComment.put(commentId, totalScore);
				}
			}
		}
		
		/**
		 * 
		 
		Iterator<Integer> itr=multiComment.keySet().iterator();
		while(itr.hasNext())
		{
			int id=itr.next();
			System.out.println("Commend Id: "+id+"Value"+multiComment.get(id));
		}
		*/
	}
	
	public static void findTopcomments()
	{
		// put a threshold value for getting max_comments;
		// select comments with 50 per of threshold-max value !!
		
		float thresholdValue=(float)(maxValue*0.30);
		Iterator<Integer> iterator=multiComment.keySet().iterator();
		while(iterator.hasNext())
		{
			int commentId=iterator.next();
			if(multiComment.get(commentId)>=thresholdValue)
			{
				CommentInfo comm=new CommentInfo();
				comm.comment_id=commentId;
				comm.score=multiComment.get(commentId);
				topComments.add(comm);
			}
		}
		
		// Displaying top comments !
		for(int i=0;i<topComments.size();i++)
		{
			CommentInfo com=topComments.get(i);
			System.out.println("Commend Id: "+com.comment_id+" Score: "+com.score);
		}
		
	}
}
