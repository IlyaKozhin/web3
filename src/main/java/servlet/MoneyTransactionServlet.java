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

public class MoneyTransactionServlet extends HttpServlet {

    private BankClientService bankClientService = BankClientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("moneyTransactionPage.html", null));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long foo;
        try {
            foo = Integer.parseInt(req.getParameter("count"));
        }
        catch (NumberFormatException e)
        {
            foo = 0;
        }
        try {
            BankClient client = bankClientService.getClientByName(req.getParameter("senderName"));
            client.setPassword(req.getParameter("senderPass"));
            if(bankClientService.sendMoneyToClient(client, req.getParameter("nameTo"),foo)) {
                createPageVariablesMap("The transaction was successful",resp);
            } else {
                createPageVariablesMap("transaction rejected",resp);
            }
        } catch (DBException e) {
            createPageVariablesMap("transaction rejected",resp);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    private static Map<String, Object> createPageVariablesMap(String message, HttpServletResponse resp) throws ServletException, IOException{
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("message", message);

        resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        return pageVariables;
    }
}
