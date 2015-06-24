package by.gsu.epamlab.controller;

import by.gsu.epamlab.model.beans.User;
import by.gsu.epamlab.model.dao.IUserDao;
import by.gsu.epamlab.model.exceptions.DataSourceException;
import by.gsu.epamlab.model.factories.AbstractDaoFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginController extends AbstractController {

    @Override
    protected void performLogic(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ControllerConst.Actions.ACTION);
        String login = req.getParameter(ControllerConst.Fields.LOGIN);
        String password = req.getParameter(ControllerConst.Fields.PASSWORD);

        IUserDao userDao = AbstractDaoFactory.getFactory(ControllerConst.FACTORY).getUserDao();

        if (ControllerConst.Actions.LOGIN.equals(action)) {
            try {
                validateField(login);
                validateField(password);

                User user = userDao.read(login, password);
                req.getSession(true).setAttribute(ControllerConst.Fields.USER, user);
                jumpTo(ControllerConst.Controllers.TASKS, req, resp);
            } catch (DataSourceException e) {
                req.setAttribute(ControllerConst.Fields.ERROR_MESSAGE, ControllerConst.Errors.INVALID_LOGIN_OR_PASSWORD);
                jumpTo(ControllerConst.Pages.ERROR, req, resp);
            } catch (IllegalArgumentException e) {
                req.setAttribute(ControllerConst.Fields.ERROR_MESSAGE, ControllerConst.Errors.EMPTY_FIELDS);
                jumpTo(ControllerConst.Pages.ERROR, req, resp);
            }
        } else {
            jumpTo(ControllerConst.Pages.LOGIN, req, resp);
        }
    }
}
