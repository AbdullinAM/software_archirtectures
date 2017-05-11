/**
 * Created by kivi on 11.05.17.
 */

package com.spbpu.storage.project;

import com.spbpu.project.Project;
import com.spbpu.storage.Mapper;
import com.spbpu.user.Manager;

import java.sql.SQLException;
import java.util.List;

public class ProjectMapper implements Mapper<Project> {

    public List<Project> findAllManagerProjects(Manager manager) {
        return null;
    }

    @Override
    public Project findByID(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Project> findAll() throws SQLException {
        return null;
    }

    @Override
    public void update(Project item) throws SQLException {

    }

    @Override
    public void closeConnection() throws SQLException {

    }
}
