package login;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;



/**
 * Servlet implementation class LoginController
 */
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private UserDAO dao;
	private ServletContext ctx;
	
	private final String START_PAGE = "pages/sign-in.jsp";
//	private final String START_PAGE = "index.jsp";

	
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
	
	
	public String startPage(HttpServletRequest request) {
		return START_PAGE;
	}
	

	
	/*  
	 *  1.JSP 연결 
	 
	public String signIn(HttpServletRequest request) {
		//redirect:/SnsController?action=snsList
		//로그인 확인 후  index.js 이
//		return "index.jsp";
		
		
		return "redirect:/LoginController?action=main";
	}
	
	public String signUp(HttpServletRequest request) {
		//redirect:/SnsController?action=snsList
		//회원가입 완료 후 
		
//		return START_PAGE;
		return "redirect:/LoginController";
	}
	*/
	
	// 2.DB 연동 
	public String signUp(HttpServletRequest request) {
		//
		User u = new User();
		try {						
			// 이미지 파일 저장
//	        Part part = request.getPart("file");
//	        String fileName = getFilename(part);
//	        if(fileName != null && !fileName.isEmpty()){
//	            part.write(fileName);
//	        }	        
			
	        // 입력값을 User 객체로 매핑
			BeanUtils.populate(u, request.getParameterMap());
			
	        // 이미지 파일 이름을 sns 객체에도 저장
//	        n.setImg("/img/"+fileName);
	
			dao.signUp(u);
			request.setAttribute("result", "signUp 성공 ");
		} catch (Exception e) {
			e.printStackTrace();
			ctx.log("addPost 오류 ");
			request.setAttribute("result", "signUp 오류 ");
			return startPage(request);
		}
		return "redirect:/LoginController";
	}
	
	public String signIn(HttpServletRequest request)  {
		String userId = request.getParameter("userId");
		String userPw = request.getParameter("userPW");
		
        try {
			User u = dao.signIn(userId, userPw);
			request.setAttribute("user", u);
			HttpSession session = request.getSession();
			session.setAttribute("id", u.getUserId());
			session.setAttribute("pw", u.getUserPW());
			session.setAttribute("name", u.getUserName());
			return "index.jsp";
			
		} catch (SQLException e) {
			e.printStackTrace();
			ctx.log("signIn error ");
			request.setAttribute("error", "로그인 실패 id pw를 확인하세요. ");
			return startPage(request);
		}
	}
	
	public String main(HttpServletRequest request) {
		return signIn(request);
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
