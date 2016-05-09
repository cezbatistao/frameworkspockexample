package br.com.batistao

import br.com.batistao.model.Cliente
import br.com.batistao.model.Pedido
import br.com.batistao.model.Produto
import spock.lang.Specification
import spock.lang.Unroll

import java.text.SimpleDateFormat

/**
 * Created by ceb on 05/05/16.
 */
class PedidoUnitSpec extends Specification {

    def "Teste realizando um pedido para um determinado produto"() {
        given:
        Produto sabaoEmPo = new Produto("OMO", "Sabão em Pó OMO Progress 1,8 kg", 26.79)
        Produto detergente = new Produto("Ype", "Detergente Líquido Ype Neutro 500 Ml", 1.99)
        Produto esponja = new Produto("Scoth Brite", "Esponja Limpeza Scoth Brite 3M", 4.50)

        Cliente cliente = new Cliente("Carlos", new Date(), "cezbatistao@email.com.br")

        when:
        Pedido pedido = new Pedido(cliente)
        pedido.comprar(detergente, 3)

        then:
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

        assert pedido.valorTotal == 5.97d
        assert sdf.format(pedido.data) == sdf.format(new Date())

        assert pedido.itensPedido.size() == 1
        assert pedido.itensPedido[0].precoTotal == 5.97d
        assert pedido.itensPedido[0].precoUnitario == 1.99d
        assert pedido.itensPedido[0].quantidade == 3
    }

    def "Teste realizando um pedido para dois produto distintos"() {
        given:
        Produto sabaoEmPo = new Produto("OMO", "Sabão em Pó OMO Progress 1,8 kg", 26.79)
        Produto detergente = new Produto("Ype", "Detergente Líquido Ype Neutro 500 Ml", 1.99)
        Produto esponja = new Produto("Scoth Brite", "Esponja Limpeza Scoth Brite 3M", 4.50)

        Cliente cliente = new Cliente("Carlos", new Date(), "cezbatistao@email.com.br")

        Pedido pedido = new Pedido(cliente)
        pedido.comprar(detergente, 3)

        when:
        pedido.comprar(esponja, 1)

        then:
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

        assert pedido.valorTotal == 10.47d
        assert sdf.format(pedido.data) == sdf.format(new Date())

        assert pedido.itensPedido.size() == 2

        assert pedido.itensPedido.find { it.produto == detergente }.precoTotal == 5.97d
        assert pedido.itensPedido.find { it.produto == detergente }.precoUnitario == 1.99d
        assert pedido.itensPedido.find { it.produto == detergente }.quantidade == 3

        assert pedido.itensPedido.find { it.produto == esponja }.precoTotal == 4.50d
        assert pedido.itensPedido.find { it.produto == esponja }.precoUnitario == 4.50d
        assert pedido.itensPedido.find { it.produto == esponja }.quantidade == 1
    }

    def "Teste realizando um pedido para dois produto e depois comprando mais dos mesmos produtos"() {
        given:
        Produto sabaoEmPo = new Produto("OMO", "Sabão em Pó OMO Progress 1,8 kg", 26.79)
        Produto detergente = new Produto("Ype", "Detergente Líquido Ype Neutro 500 Ml", 1.99)
        Produto esponja = new Produto("Scoth Brite", "Esponja Limpeza Scoth Brite 3M", 4.50)

        Cliente cliente = new Cliente("Carlos", new Date(), "cezbatistao@email.com.br")

        Pedido pedido = new Pedido(cliente)
        pedido.comprar(detergente, 3)
        pedido.comprar(esponja, 1)

        when:
        pedido.comprar(detergente, 1)
        pedido.comprar(esponja, 1)

        then:
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

        assert pedido.valorTotal == 16.96d
        assert sdf.format(pedido.data) == sdf.format(new Date())

        assert pedido.itensPedido.size() == 2

        assert pedido.itensPedido.find { it.produto == detergente }.precoTotal == 7.96d
        assert pedido.itensPedido.find { it.produto == detergente }.precoUnitario == 1.99d
        assert pedido.itensPedido.find { it.produto == detergente }.quantidade == 4

        assert pedido.itensPedido.find { it.produto == esponja }.precoTotal == 9.00d
        assert pedido.itensPedido.find { it.produto == esponja }.precoUnitario == 4.50d
        assert pedido.itensPedido.find { it.produto == esponja }.quantidade == 2
    }

    @Unroll
    def "Teste realizando um pedido para o produto #nome com a quantidade #quantidade com o valor total da compra de #valorTotal"() {
        given:
        Cliente cliente = new Cliente("Carlos", new Date(), "cezbatistao@email.com.br")

        expect:
        Produto produto = new Produto(nome, descricao, precoUnitario)

        Pedido pedido = new Pedido(cliente)
        pedido.comprar(produto, quantidade)

        assert pedido.valorTotal == valorTotal
        assert pedido.itensPedido[0].quantidade == quantidade
        assert pedido.itensPedido[0].precoUnitario == precoUnitario

        where:
        nome           | descricao                               | precoUnitario  | quantidade  | valorTotal
        "Ype"          | "Detergente Líquido Ype Neutro 500 Ml"  | 1.99           | 3           | 5.97d
        "OMO"          | "Sabão em Pó OMO Progress 1,8 kg"       | 26.79          | 2           | 53.58d
        "Scoth Brite"  | "Esponja Limpeza Scoth Brite 3M"        | 4.50           | 3           | 13.5d
    }
}