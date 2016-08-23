package api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A utility class to handle session related security logics.
 */
public class SessionValid {
	/**
	 * @see check if Session is valid
	 */
	public static boolean isSessionValid(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String user = (String) session.getAttribute("user");
		String user_id = request.getParameter("user_id");
		if (user == null || (user_id != null && !user.equals(user_id))) {
			return false;
		}
		return true;
	}

}
