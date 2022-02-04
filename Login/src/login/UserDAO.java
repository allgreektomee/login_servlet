package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {
	final String JDBC_DRIVER = "org.h2.Driver";
	final String JDBC_URL = "jdbc:h2:tcp://localhost/~/test";
	
	
	// DB 연결을 가져오는 메서드, DBCP를 사용하는 것이 좋음
	public Connection open() {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(JDBC_URL,"sa","");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public void signUp(User u) throws Exception {
		//회원가입  
		Connection conn = open();
//		Insert into  MEMBER (userid, userpw, username) values('admin','1234','관리자')
		String sql = "insert into sns(userid,userpw,username) values(?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
	
		
		try(conn; pstmt) {
			pstmt.setString(1, u.getUserId() );
			pstmt.setString(2, u.getUserPW() );
			pstmt.setString(3, u.getUserName() );
			pstmt.executeUpdate();
		}
		
	}
	
	public User signIn(String userId, String userPw) throws Exception {
		//로그인 
		Connection conn = open();
		
		User u = new User();
		
//		SELECT * FROM MEMBER where userid = 'admin' and userpw = '1234'
		String sql = "select * from MEMBER where userid = ? and userpw = ?";
	
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, userId);
		pstmt.setString(2, userPw);
		ResultSet rs = pstmt.executeQuery();
		
		rs.next();
		
		try(conn; pstmt; rs) {
			u.setUserId(rs.getString("userId"));
			u.setUserPW(rs.getString("userPw"));
			u.setUserName(rs.getString("userName"));
			pstmt.executeQuery();
			return u;
		}
	}
	
	public List<User> getAllPost() throws Exception {
		//회원 전체목록  
		Connection conn = open();
		List<User> newsList = new ArrayList<>();
		
//		
		String sql = "SELECT * FROM MEMBER ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		
		try(conn; pstmt; rs) {
			while(rs.next()) {
				User u = new User();
				u.setUserId(rs.getString("userId"));
				u.setUserPW(rs.getString("userPw"));
				u.setUserName(rs.getString("userName"));
				
				newsList.add(u);
			}
			return newsList;			
		}
	}
}

/*
 
 Create table Member(
	id INT NOT NULL AUTO_INCREMENT,
	userid VARCHAR NOT NULL  PRIMARY KEY,
	userpw VARCHAR NOT NULL,
	username VARCHAR NOT NULL
)

SELECT * FROM MEMBER 

Insert into  MEMBER (userid, userpw, username) values('admin','1234','관리자')

SELECT * FROM MEMBER where userid = 'admin' and userpw = '1234'

$ cd ~/Downloads/h2/bin 
$ chmod +x h2.sh 
$ ./h2.sh

 */
