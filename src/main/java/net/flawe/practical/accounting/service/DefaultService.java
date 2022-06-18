package net.flawe.practical.accounting.service;

import org.springframework.data.domain.Sort;

import java.util.List;

public interface DefaultService<T, ID> {

    T getById(ID id);
    T save(T value);
    void delete(T value);
    List<T> findAll();
    List<T> findAll(Sort sort);

}
