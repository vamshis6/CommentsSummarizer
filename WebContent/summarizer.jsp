<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml"><head>

<title>Tech Crunch Summarizer</title>
 <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Tangerine:bold,bolditalic|Inconsolata:italic|Droid+Sans">
<style>

    body {
        background-color:#ecf0f1;
       
        font-size: 16px; 
    }
    #wrapper 
    {
    	width: 888px;
    	margin: 40px auto;
    
   }
  
  #box
  {
  font-family: Verdana, Geneva, sans-serif;
  }
  
    #u 
    {
    	width:600px;
    	height:20px;
    	margin:10px 10px 10px 10px; 
    	padding:15px 15px 15px 15px;
    }
   
   input[type=url] {
    
    border: 5px solid white; 
    -webkit-box-shadow: 
      inset 0 0 8px  rgba(0,0,0,0.1),
            0 0 16px rgba(0,0,0,0.1); 
    -moz-box-shadow: 
      inset 0 0 8px  rgba(0,0,0,0.1),
            0 0 16px rgba(0,0,0,0.1); 
    box-shadow: 
     	inset 0 0 8px  rgba(0,0,0,0.1),
            0 0 16px rgba(0,0,0,0.1); 
    padding: 5px;
    background: rgba(255,255,255,0.5);
    font-weight:24px;
    }
  
 #title_wrapper{
 	
 	margin-top:30px;
 	height:80%;
 	left:10%;
 	right:10%;
 	margin-bottom:30px;
 	background-color:#2ecc71;
 	padding:20px 20px 20px 20px;
 	
    -moz-border-radius: 10px;
    -webkit-border-radius: 10px;
    border-radius: 10px;
 }
 
 #title
 {
	margin-top:30px;
	margin-bottom:30px;
	margin-left:20px;
	margin-right:20px; 
 
 
 }
 	#blog_url
 	{
 	position:relative;
 	
 	}
  

    /***FIRST STYLE THE BUTTON***/
    input#bigbutton {
    width:400px;
    background: #3e9cbf; /*the colour of the button*/
    padding: 8px 14px 10px; /*apply some padding inside the button*/
    border:1px solid #3e9cbf; /*required or the default border for the browser will appear*/
    cursor:pointer; /*forces the cursor to change to a hand when the button is hovered*/
    /*style the text*/
    font-size:1.5em;
    font-family:Oswald, sans-serif; /*Oswald is available from http://www.google.com/webfonts/specimen/Oswald*/
    letter-spacing:.1em;
    text-shadow: 0 -1px 0px rgba(0, 0, 0, 0.3); /*give the text a shadow - doesn't appear in Opera 12.02 or earlier*/
    color: #fff;
    /*use box-shadow to give the button some depth - see cssdemos.tupence.co.uk/box-shadow.htm#demo7 for more info on this technique*/
    -webkit-box-shadow: inset 0px 1px 0px #3e9cbf, 0px 5px 0px 0px #205c73, 0px 10px 5px #999;
    -moz-box-shadow: inset 0px 1px 0px #3e9cbf, 0px 5px 0px 0px #205c73, 0px 10px 5px #999;
    box-shadow: inset 0px 1px 0px #3e9cbf, 0px 5px 0px 0px #205c73, 0px 10px 5px #999;
    /*give the corners a small curve*/
    -moz-border-radius: 10px;
    -webkit-border-radius: 10px;
    border-radius: 10px;
    }
    /***SET THE BUTTON'S HOVER AND FOCUS STATES***/
    input#bigbutton:hover, input#bigbutton:focus {
    color:#dfe7ea;
    /*reduce the size of the shadow to give a pushed effect*/
    -webkit-box-shadow: inset 0px 1px 0px #3e9cbf, 0px 2px 0px 0px #205c73, 0px 2px 5px #999;
    -moz-box-shadow: inset 0px 1px 0px #3e9cbf, 0px 2px 0px 0px #205c73, 0px 2px 5px #999;
    box-shadow: inset 0px 1px 0px #3e9cbf, 0px 2px 0px 0px #205c73, 0px 2px 5px #999;
    }

	#output
	{
		margin-top:50px;
		font-weight:24px;
		
	
	}
	
	#finalText
	{
		background-color:#FFF;
		padding:10px 10px 10px 10px;
	}
    
    
</style>
</head>

<body>
	<div id="wrapper">
		<div id="title_wrapper">
			<div id="title">
				<center>
					<h1>
					"<i>Tech Crunch</i>" Blog Summarizer
					</h1>
					<h2 style="
  font-family: 'Gotham Rounded A', 'Gotham Rounded B', proxima-nova-soft, sans-serif;
 ">
						<i> Tech is cool ... Clear the drool! </i>
					</h2>
 				</center>
			</div>
		</div>
		
		<div id="blog-url">
			<center>
			<div id="box">
				<form action="SummarizerServlet" method="post">
					<div>
					<i>	Enter  URL  </i>
					</div>	
					
					<div id="link_plese">
						<input type="url" name="blog_link" id="u"/>
					</div>
					<div>
						<input id="bigbutton" type="submit" value="Summarize Now !!" />
					</div>
				</form>
			</div>
			</center>
		</div>
	
	
	
	
	
	</div>
	
	
	
	</div>	
	
	
</body>	