package com.logic.mes.entity.base;


import com.logic.mes.entity.process.Process;

import java.util.List;

public class UserData extends NetData{


    private Long id;
    private String userName;
    List<Process> processes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }
}
