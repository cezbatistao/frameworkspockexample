package br.com.batistao.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ceb on 30/04/16.
 */
@Entity
@Table(name = "PEDIDO")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name="DATA_PEDIDO")
    private Date data;

    @NotNull
    @Column(name="VALOR_TOTAL")
    private Double valorTotal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente", referencedColumnName = "id")
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido")
    private List<ItemPedido> itensPedido;

    Pedido() {
        // default constructor
    }

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.data = new Date();
    }

    public void comprar(final Produto produto, final Integer quantidade) {
        if(this.itensPedido == null) {
            this.itensPedido = new ArrayList<>();
        }

        ItemPedido itemPedido = Iterables.find(this.itensPedido, new Predicate<ItemPedido>() {
            public boolean apply(ItemPedido itemPedido) {
                return itemPedido.getProduto().equals(produto);
            }
        }, null);

        if(itemPedido == null) {
            itemPedido = new ItemPedido();
            itemPedido.setPrecoUnitario(produto.getPreco());
            itemPedido.setQuantidade(quantidade);
            itemPedido.setPrecoTotal(produto.getPreco() * quantidade);
            itemPedido.setProduto(produto);
            itemPedido.setPedido(this);
            this.itensPedido.add(itemPedido);
        } else {
            itemPedido.setQuantidade(itemPedido.getQuantidade() + quantidade);
            Double precoDaNovaCompra = produto.getPreco() * quantidade;
            precoDaNovaCompra += itemPedido.getPrecoTotal();
            itemPedido.setPrecoTotal(valorComDuasCasasDecimais(precoDaNovaCompra));
        }

        List<Pedido> pedidosDoCliente = new ArrayList<>();
        if(cliente.getPedidos() != null) {
            pedidosDoCliente = cliente.getPedidos();
        }
        pedidosDoCliente.add(this);
        cliente.setPedidos(pedidosDoCliente);

        List<ItemPedido> itensPedidosProduto = new ArrayList<>();
        if(produto.getItensPedido() != null) {
            itensPedidosProduto = produto.getItensPedido();
        }
        itensPedidosProduto.add(itemPedido);
        produto.setItensPedido(itensPedidosProduto);

        Double valorTotalCompra = 0.0d;
        for (ItemPedido itemPedidosRealizados : this.itensPedido) {
            valorTotalCompra += itemPedidosRealizados.getPrecoTotal();
        }
        this.setValorTotal(valorComDuasCasasDecimais(valorTotalCompra));
    }

    private Double valorComDuasCasasDecimais(Double precoDaNovaCompra) {
        BigDecimal bd = new BigDecimal(precoDaNovaCompra);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemPedido> getItensPedido() {
        return itensPedido;
    }

    public void setItensPedido(List<ItemPedido> itensPedido) {
        this.itensPedido = itensPedido;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", data=" + data +
                ", valorTotal=" + valorTotal +
                ", itensPedido=" + itensPedido +
                '}';
    }
}
