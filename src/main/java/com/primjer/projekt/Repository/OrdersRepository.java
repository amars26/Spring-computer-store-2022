package com.primjer.projekt.Repository;

import com.primjer.projekt.Model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o WHERE o.userId = :id AND o.completed = 0 AND o.created = 0")
    List<Orders> findCartOrders(int id);

    @Query("SELECT o FROM Orders o WHERE o.userId = :id AND o.completed = 0 AND o.created = 1")
    List<Orders> findCartOrdersCreated(int id);

    @Query("SELECT o FROM Orders o WHERE o.userId = :id AND o.completed = 1 AND o.created = 1")
    List<Orders> findCartOrdersCompleted(int id);


}
