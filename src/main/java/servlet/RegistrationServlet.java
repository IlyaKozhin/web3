package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationServlet extends HttpServlet {

    BankClientService bankClientService = BankClientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", null));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long foo;
        try {
            foo = Integer.parseInt(req.getParameter("money"));
        } catch (NumberFormatException e) {
            foo = 0;
        }

        try {
            if (bankClientService.addClient(new BankClient(0, req.getParameter("name"), req.getParameter("password"), foo))) {
                createPageVariablesMap("Add client successful", resp);
            } else {
                createPageVariablesMap("Client not add", resp);
            }
        } catch (DBException e) {
            createPageVariablesMap("Client not add", resp);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private static Map<String, Object> createPageVariablesMap(String message, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("message", message);

        resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        return pageVariables;
    }
}
