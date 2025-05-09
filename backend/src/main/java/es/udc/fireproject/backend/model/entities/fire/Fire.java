package es.udc.fireproject.backend.model.entities.fire;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "fire", schema = "public")
public class Fire extends BaseEntity {

    private static final long serialVersionUID = -4072358581910996914L;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "index", nullable = false)
    @Enumerated(EnumType.STRING)
    private FireIndex fireIndex;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "fire",
            fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Quadrant> quadrantGids;

    @Column(name = "extinguished_at")
    private LocalDateTime extinguishedAt;

    public Fire() {
    }

    public Fire(String description, String type, FireIndex fireIndex) {
        this.description = description;
        this.type = type;
        this.fireIndex = fireIndex;
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getExtinguishedAt() {
        return extinguishedAt;
    }

    public void setExtinguishedAt(LocalDateTime extinguishedAt) {
        this.extinguishedAt = extinguishedAt;
    }

    public List<Quadrant> getQuadrantGids() {
        return quadrantGids;
    }

    public void setQuadrantGids(List<Quadrant> quadrantGid) {
        this.quadrantGids = quadrantGid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public FireIndex getFireIndex() {
        return fireIndex;
    }

    public void setFireIndex(FireIndex fireIndex) {
        this.fireIndex = fireIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fire fire = (Fire) o;
        return Objects.equals(description, fire.description) && Objects.equals(type, fire.type) && fireIndex == fire.fireIndex && Objects.equals(createdAt, fire.createdAt) && Objects.equals(quadrantGids, fire.quadrantGids) && Objects.equals(extinguishedAt, fire.extinguishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, type, fireIndex, createdAt, quadrantGids, extinguishedAt);
    }

    @Override
    public String toString() {
        return "Fire{" +
                "description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", fireIndex=" + fireIndex +
                ", createdAt=" + createdAt +
                ", cuadrantGids=" + quadrantGids +
                ", extinguishedAt=" + extinguishedAt +
                '}';
    }
}
