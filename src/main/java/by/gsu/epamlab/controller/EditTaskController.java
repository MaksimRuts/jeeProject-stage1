package by.gsu.epamlab.controller;

import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.beans.User;
import by.gsu.epamlab.model.dao.ITaskDao;
import by.gsu.epamlab.model.factories.AbstractDaoFactory;
import by.gsu.epamlab.requestparser.FileManagement;
import by.gsu.epamlab.requestparser.UploadedFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditTaskController extends AbstractController {

    @Override
    protected void performLogic(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkRequestType(req);
        String action = getParameter(ControllerConst.Actions.ACTION);

        if (ControllerConst.Actions.REMOVE.equals(action)) {
            jumpTo(ControllerConst.Controllers.MANAGE_TASK, req, resp);
        } else if (ControllerConst.Actions.REMOVE_FILE.equals(action)) {
            ITaskDao taskDao = AbstractDaoFactory.getFactory(ControllerConst.FACTORY)
                    .getTaskDao();
            String taskId = getParameter(ControllerConst.Fields.TASK_ID);
            User user = (User) req.getSession().getAttribute(ControllerConst.Fields.USER);
            Task task = taskDao.read(user.getId(), Integer.parseInt(taskId));
            FileManagement.deleteFile(task.getFilename(), ControllerConst.FilePath.getAbsolutePath(getServletContext()));
            task.setFilename("");
            taskDao.update(task);
            showTask(taskId, req, resp);
        } else if (ControllerConst.Actions.CONFIRM.equals(action)) {
            String name = getParameter(ControllerConst.Fields.TASK_NAME);
            String date = getParameter(ControllerConst.Fields.TASK_DATE);
            String description = getParameter(ControllerConst.Fields.TASK_DESCRIPTION);
            String tasksId = getParameter(ControllerConst.Fields.TASK_ID);
            UploadedFile file = getFile(ControllerConst.Fields.FILE);

            validateField(name);
            validateField(date);
            validateField(description);
            validateField(tasksId);

            ITaskDao taskDao = AbstractDaoFactory.getFactory(ControllerConst.FACTORY)
                    .getTaskDao();

            User user = (User) req.getSession().getAttribute(ControllerConst.Fields.USER);
            Task task = taskDao.read(user.getId(), Integer.parseInt(tasksId));

            task.setName(name);
            task.setDescription(description);
            task.setDateEnding(date);

            if (file != null) {
                FileManagement.saveFile(file, ControllerConst.FilePath.getAbsolutePath(getServletContext()));
                task.setFilename(file.getFilename());
            }

            taskDao.update(task);
            redirectTo(ControllerConst.Controllers.TASKS, req, resp);
        } else {
            String taskId = getParameter(ControllerConst.Fields.TASK_ID);
            showTask(taskId, req, resp);
        }
    }

    private void showTask(String taskId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (taskId != null) {
            User user = (User) req.getSession().getAttribute(ControllerConst.Fields.USER);
            ITaskDao taskDao = AbstractDaoFactory.getFactory(ControllerConst.FACTORY)
                    .getTaskDao();
            Task task = taskDao.read(user.getId(), Integer.parseInt(taskId));
            req.setAttribute(ControllerConst.Fields.TASK, task);
            jumpTo(ControllerConst.Pages.EDIT_TASK, req, resp);
        } else {
            jumpTo(ControllerConst.Controllers.TASKS, req, resp);
        }
    }
}
