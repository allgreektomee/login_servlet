package main;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import login.User;
import login.UserDAO;


/**
 * Servlet implementation class MainController
 */
@WebServlet("/MainController")
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	private UserDAO dao;
	private ServletContext ctx;
	
	private final String START_PAGE = "index.jsp";

      
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = new UserDAO();
		ctx = getServletContext();		
	}
	
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String action = request.getParameter("action");
		dao = new UserDAO();
		
		Method m;
		String view = null;
		
		if (action == null) {
			action = "startPage";
		}
		
		try {
			
			m = this.getClass().getMethod(action, HttpServletRequest.class);
			view = (String)m.invoke(this, request);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
	
			ctx.log("요청 action 없음!!");
			request.setAttribute("error", "action 파라미터가 잘못 되었습니다!!");
			view = START_PAGE;
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	
		if(view.startsWith("redirect:/")) {

			String rview = view.substring("redirect:/".length()); 
			response.sendRedirect(rview);
		} else {
			
			RequestDispatcher dispatcher = request.getRequestDispatcher(view);
			dispatcher.forward(request, response);	
		}
	}
	
	public String main(HttpServletRequest request) {
		
		
		return START_PAGE;
	}
	
	public String userList(HttpServletRequest request) {
    	List<User> list;
		try {
			list = dao.getAllUser();
	    	request.setAttribute("userList", list);
		} catch (Exception e) {
			e.printStackTrace();
			ctx.log("SnsList error");
			request.setAttribute("error", "오류발생 목록 조회 실패");
		}
    	return "pages/table.jsp";
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
