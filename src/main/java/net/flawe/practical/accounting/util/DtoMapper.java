package net.flawe.practical.accounting.util;

public interface DtoMapper<F, T>{

    F mapFromDto(T t);

    T mapToDto(F f);

}
