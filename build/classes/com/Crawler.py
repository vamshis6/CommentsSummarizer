from bs4 import BeautifulSoup
import urllib2, json
import os, errno

# Function to check whether the path exists or not
def make_sure_path_exists(path):
    try:
        os.makedirs(path)
    except OSError as exception:
        if exception.errno != errno.EEXIST:
            raise
#function to removw & signs in xml
def remove_noise(s):
	len123=len(s)
	message=""
	for x in xrange(0,len123):
		if s[x]=='&':
			message+="&amp;"
		else:
			message+=s[x]
	return message	
Nblogs= int(raw_input())
URLs=[]
graphURLs=[]
for xit in range(0,Nblogs):
	s1=raw_input()
	URLs.append(s1)
	graphURLs.append("https://graph.facebook.com/comments/?limit=500&ids="+str(s1))

i = len(URLs)
j = 0
datasetPath = "../../Blogs"

print "Let the crawling begin!\nTotal no. of blogs = " + str(i)

while j < i:
	netDirPath = os.path.join(datasetPath, "Blog " + str(j+1))
	make_sure_path_exists(netDirPath)

	# blogFile contains all the blogs with title, body etc.
	netBlogPath = os.path.join(netDirPath, "blogDataset.json")
	if os.path.exists(netBlogPath):
		os.remove(netBlogPath)
	blogFile = open(netBlogPath, 'w')

	# commentsFile contains all the comments
	netCommentsPath = os.path.join(netDirPath, "commentsDataset.json")
	if os.path.exists(netCommentsPath):
		os.remove(netCommentsPath)
	commentsFile = open(netCommentsPath, 'w')

	blogFile.write('{\n\t"blog" : {\n')
	#commentsFile.write('{\n\t"comments" : [\n')
	
	responseComments = urllib2.urlopen(graphURLs[j])
	dataComments = json.loads(responseComments.read())
	commentsFile.write((json.dumps(dataComments, indent = 2)))

	
	responseBlog = urllib2.urlopen(URLs[j])
	html = responseBlog.read()
	soup = BeautifulSoup(html)

	blogFile.write('\n\t\t\t"url": "' + URLs[j] + '",\n' + '\t\t\t"title": "' + soup.h1.contents[0].encode('utf-8') + '",\n')

	for links in soup.find_all('div'):
		if links.get('class')!=None:
		  if links.get('class')[0]=="title-left":
		  	dateAuthor = links.get_text()
			dateAuthor = dateAuthor.strip().split(' ')			
			#print dateAuthor
			indexBy = dateAuthor.index("by")
			k = 1
			blogDateParts = ""
			while k < indexBy:
				blogDateParts += dateAuthor[k] + " "
				k = k + 1
			blogDate = blogDateParts

			blogAuthorList = dateAuthor[indexBy + 2].split('\n')
			blogAuthorLastName = ""
			nextStr = 0
			while nextStr < len(blogAuthorList):				
				if len(blogAuthorList[nextStr]) != 0 and "More" not in blogAuthorList[nextStr]:
					blogAuthorLastName = blogAuthorList[nextStr]
				nextStr = nextStr + 1
			blogAuthor = dateAuthor[indexBy + 1]+ ' ' + blogAuthorLastName

			blogFile.write('\t\t\t"date" : "' + blogDate + '",\n')
			blogFile.write('\t\t\t"author" : "' + blogAuthor + '",\n')

	
	blogBody = ""
	for links in soup.find_all('p'):
			blogBodyPart = links.get_text().strip()
			if len(blogBodyPart) > 0 and blogBodyPart != "More" and not "Latest headlines delivered to you daily" in blogBodyPart:
				blogBody += blogBodyPart.encode('utf-8' + '\n')
	blogFile.write('\t\t\t"body" : "' + blogBody + '"\n')			
	blogFile.write('\t}\n}')


	blogFile.close()
	commentsFile.close()

	j = j + 1
	print "Blog " + str(j) + " done"

print "Cool. Done with Blogs and Comments!\nblogDataset.json => blog title, date, author, body\ncommentsDataset.json => comments from graph API"

print "\nOnto the replies part now..."


i=len(URLs)
j=0
path = "replies"

while j < i:
	print "Getting replies for all the comments in Blog " + str(j + 1) + " " + URLs[j]
	# Reading commentsDataset to get the replies for each comment
	netDirPath = os.path.join(datasetPath, "Blog " + str(j+1))
	netCommentsPath = os.path.join(netDirPath, "commentsDataset.json")
	commentsFile = open(netCommentsPath,'r')
	commentsJSON = json.load(commentsFile)
	netBlogFilePath=os.path.join(netDirPath,"blogDataset.json")
	blogFile1= open(netBlogFilePath,'r')
	blogFile1JSON = json.load(blogFile1)
	XMLstring = "<blog id =" + " \"" + str(j+1) + "\">" +"\n<title>"
	XMLstring+="\n"+blogFile1JSON["blog"]["title"].encode('utf-8')+"\n</title>\n"
	XMLstring+="<text>\n"+blogFile1JSON["blog"]["body"].encode('utf-8')+"\n</text>\n<comments>\n"
	blogFile1.close()
	commentsIDs = []
	numberOfComments = len(commentsJSON[URLs[j]]["comments"]["data"])
	for k in range(numberOfComments):
		commentsIDs.append(commentsJSON[URLs[j]]["comments"]["data"][k]["id"].encode('ascii'))
	comment_counter=1
	cnt=1
	for commentsID in commentsIDs:
		repliesURL = "https://graph.facebook.com/" + commentsID + "/comments/?limit=500"
		responseReplies = urllib2.urlopen(repliesURL)
		dataReplies = json.loads(responseReplies.read())
		main_comment="\n<comment id=" +"\"" +str(cnt) +"\"" +" likes=" +"\"" + str(commentsJSON[URLs[j]]["comments"]["data"][comment_counter-1]["like_count"]) +"\">"
		parent_comment_id=cnt
		temp=""
		if "from" in commentsJSON[URLs[j]]["comments"]["data"][comment_counter-1]:
			temp+=commentsJSON[URLs[j]]["comments"]["data"][comment_counter-1]["from"]["name"]
		main_comment+="\n<writer>\n"+temp +"\n</writer>"
		main_comment+="\n<text>\n" + commentsJSON[URLs[j]]["comments"]["data"][comment_counter-1]["message"]+ "\n</text>\n</comment>\n"
		comment_counter=comment_counter+1
		cnt=cnt+1
		XMLstring+=main_comment.encode('utf-8')
		numberofreplies = len(dataReplies["data"])
		for iter1 in range(0,numberofreplies):
			reply_comment="\n<comment id=" +"\"" +str(cnt) +"\"" + " reply_id=" + "\"" + str(parent_comment_id) + "\"" + " likes=" + "\""+ str(dataReplies["data"][iter1]["like_count"])+ "\">"
			temp=""
			if "from" in dataReplies["data"][iter1]:
				temp+=dataReplies["data"][iter1]["from"]["name"]
			reply_comment+="\n<writer>\n" + temp +"\n</writer>"
			reply_comment+="\n<text>\n" + dataReplies["data"][iter1]["message"] + "\n</text>\n</comment>\n"
			XMLstring+=reply_comment.encode('utf-8')
			cnt=cnt+1
	commentsFile.close()
	XMLstring+="\n</comments>\n</blog>\n"
	XMLstring = remove_noise(XMLstring)
	BlogXML="Blog"+str(j+1)+".xml"
	BlogXMLPath=os.path.join(datasetPath,BlogXML)
	BlogXMLPathOpen = open((BlogXMLPath),'w')
	BlogXMLPathOpen.write(XMLstring)
	BlogXMLPathOpen.close()
	j=j+1
print "Done! Crawling the Blog !!"

print "Running the POS TAGGER NOW !!"
