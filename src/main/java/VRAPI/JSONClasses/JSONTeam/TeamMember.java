package VRAPI.JSONClasses.JSONTeam;

/**
 * POJO for returning an individual team member
 */
public class TeamMember {

    private String email;
    private Long id;

    public TeamMember() {

    }

    public TeamMember(String email, Long id) {
        this.email = email;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}