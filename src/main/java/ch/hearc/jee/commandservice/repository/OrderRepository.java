package ch.hearc.jee.commandservice.repository;

import org.springframework.data.repository.CrudRepository;

import ch.hearc.jee.commandservice.model.Order;

public interface OrderRepository extends CrudRepository<Order,Long> {

}
