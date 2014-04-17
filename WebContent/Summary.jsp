<%@page import="com.SummarizerServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Summary</title>

<style>

	#text_q
	{
		margin:30px;
	}
	#finalText 
	{
		margin:30px 30px 30px 30px;
		padding:10px 10px 10px 10px;
		font-style:
	}
	

</style>
</head>
<%@ include file="summarizer.jsp" %> 
<%@ page import="com.SummarizerServlet" %>

<body>
	<div id="output">
		<div id="text_q">
		
		<i><b>	Here's your summary ! </i> </b>
		</div>
		<div id="finalText">
			<p id="text">
				
				<%
				String[] tokens=SummarizerServlet.split_tokens();
				for(String s:tokens)
				{%>
					<p>
					<%= s %>
					</p>
				<% }%>
					
				
				
							
			</p>
		
		
		</div>

</body>
</html>