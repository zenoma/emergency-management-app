package es.udc.fireproject.backend.model.entities.organization;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "organization", schema = "public")
public class Organization extends BaseEntity {

    private static final long serialVersionUID = -4807580144244128235L;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    @Column(name = "headquarters_address")
    private String headquartersAddress;

    @Type(type = "org.locationtech.jts.geom.Point")
    @Column(name = "location")
    private Point location;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "organization_type_id")
    private OrganizationType organizationType;

    public Organization() {
    }

    public Organization(String code, String name, String headquartersAddress, Point location, OrganizationType organizationType) {
        this.code = code;
        this.name = name;
        this.headquartersAddress = headquartersAddress;
        this.location = location;
        this.organizationType = organizationType;
        this.createdAt = LocalDateTime.now();
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadquartersAddress() {
        return headquartersAddress;
    }

    public void setHeadquartersAddress(String headquartersAddress) {
        this.headquartersAddress = headquartersAddress;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return name.equals(that.name) && organizationType.equals(that.organizationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, organizationType);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", headquartersAddress='" + headquartersAddress + '\'' +
                ", location=" + location +
                ", organizationType=" + organizationType +
                '}';
    }
}
