package br.com.batistao.repository;

import br.com.batistao.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ceb on 30/04/16.
 */
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("FROM Pedido p JOIN FETCH p.itensPedido WHERE p.id = (:id)")
    Pedido findOneFetch(@Param("id") final Long id);

    @Query("FROM Pedido p JOIN FETCH p.itensPedido")
    List<Pedido> findAllFetch();
}
