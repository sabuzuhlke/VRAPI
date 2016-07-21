package VRAPI.Entities;

import VRAPI.XMLClasses.ContainerEmployees.ProjectWorker;

public class Employee {

    private Long id;
    private String email;
    private String name;
    private Boolean active;

    public Employee() {
    }

    public Employee(ProjectWorker projectWorker) {
        id = projectWorker.getObjid();
        email = projectWorker.getEmail();
        name = projectWorker.getName();
        active = projectWorker.getActive();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
