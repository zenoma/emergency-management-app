package es.udc.fireproject.backend.model.entities.user;

public enum UserRole {
    // Highest rol functions will have the bigger number
    COORDINATOR(0), MANAGER(1), USER(2);

    public final Integer priority;

    UserRole(Integer priority) {
        this.priority = priority;
    }

    public boolean isHigherThan(UserRole userRole) {
        return this.priority > userRole.priority;
    }

    public boolean isEqualThan(UserRole userRole) {
        return this.priority.equals(userRole.priority);
    }

    public boolean isLowerThan(UserRole userRole) {
        return this.priority < userRole.priority;
    }

}
