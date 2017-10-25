package uk.gov.ea.datareturns.domain.jpa.dao.userdata;

public interface UserDataDao<E> {
    E persist(E entity);

    void remove(Long id);

    void merge(E entity);
}
