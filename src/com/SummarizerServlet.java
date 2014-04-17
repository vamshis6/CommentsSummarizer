package com;



import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SummarizerServlet
 */
@WebServlet("/SummarizerServlet")
public class SummarizerServlet extends HttpServlet {
	
	public static String Sumary;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SummarizerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static String[] split_tokens()
	{
		String[] tokens=Sumary.split("\n");
		return tokens;
	}
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String url_pattern=request.getParameter("blog_link");
		System.out.println(url_pattern);
		String[] arguments=new String[1];
		arguments[0]=url_pattern;
		
		Crawler.main(arguments);
		
		response.sendRedirect(request.getContextPath()+"/Summary.jsp");
		
		// Blog File
		
		
		/*
		request.setAttribute("sum",Sumary);
		RequestDispatcher rd=request.getRequestDispatcher("/Summary.jsp");
		rd.forward( request, response ) ;
	*/
	}

}
