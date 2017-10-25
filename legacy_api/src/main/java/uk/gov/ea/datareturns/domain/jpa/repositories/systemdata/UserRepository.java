package uk.gov.ea.datareturns.domain.jpa.repositories.systemdata;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

@Repository public interface UserRepository extends BaseRepository<User, Long> {

    User getUserByIdentifier(String userIdentifier);

    void removeUserByIdentifier(String userIdentifier);
}
