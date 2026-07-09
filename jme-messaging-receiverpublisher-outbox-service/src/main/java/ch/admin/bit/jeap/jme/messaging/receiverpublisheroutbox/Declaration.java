package ch.admin.bit.jeap.jme.messaging.receiverpublisheroutbox;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for JPA
@AllArgsConstructor(access = PRIVATE)
@Getter
@ToString
public class Declaration {

    @Id
    private UUID id;

    private String text;

    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;

    public static Declaration from(String text) {
        ZonedDateTime now = ZonedDateTime.now();
        return new Declaration(UUID.randomUUID(), text, now, now);
    }

    void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Declaration that = (Declaration) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
