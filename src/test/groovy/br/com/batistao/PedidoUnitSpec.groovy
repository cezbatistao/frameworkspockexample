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
        Produto detergente = new Produto("Ype", "Detergente Líquido Ype Neutro 500 Ml", 1.99)

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
        "Ype"          | "Detergente Líquido Ype Neutro 500 Ml"  | 1.99d          | 3           | 5.97d
        "OMO"          | "Sabão em Pó OMO Progress 1,8 kg"       | 26.79d         | 2           | 53.58d
        "Scoth Brite"  | "Esponja Limpeza Scoth Brite 3M"        | 4.50d          | 3           | 13.5d
    }

    def "Teste dois clientes realizando seus pedidos de compra"() {
        given: "que eu tenha os produtos e os clientes já cadastrados"
        Produto sabaoEmPo = new Produto("OMO", "Sabão em Pó OMO Progress 1,8 kg", 26.79)
        Produto detergente = new Produto("Ype", "Detergente Líquido Ype Neutro 500 Ml", 1.99)
        Produto esponja = new Produto("Scoth Brite", "Esponja Limpeza Scoth Brite 3M", 4.50)

        Cliente clienteCarlos = new Cliente("Carlos", new Date(), "carlos@email.com.br")
        Cliente clienteEduardo = new Cliente("Eduardo", new Date(), "eduardo@email.com.br")

        when: "eu efetuo a compra para alguns produtos para um cliente"
        Pedido pedidoClienteCarlos = new Pedido(clienteCarlos)
        pedidoClienteCarlos.comprar(detergente, 4)
        pedidoClienteCarlos.comprar(esponja, 2)

        and: "depois eu efetuo a compra para outro cliente"
        Pedido pedidoClienteEduardo = new Pedido(clienteEduardo)
        pedidoClienteEduardo.comprar(sabaoEmPo, 2)
        pedidoClienteEduardo.comprar(detergente, 2)
        pedidoClienteEduardo.comprar(esponja, 1)

        then: "devo ter o valor total de acordo com os produtos selecionados para um cliente"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

        assert pedidoClienteCarlos.valorTotal == 16.96d
        assert sdf.format(pedidoClienteCarlos.data) == sdf.format(new Date())

        assert pedidoClienteCarlos.itensPedido.size() == 2

        assert pedidoClienteCarlos.itensPedido.find { it.produto == detergente }.precoTotal == 7.96d
        assert pedidoClienteCarlos.itensPedido.find { it.produto == detergente }.precoUnitario == 1.99d
        assert pedidoClienteCarlos.itensPedido.find { it.produto == detergente }.quantidade == 4

        assert pedidoClienteCarlos.itensPedido.find { it.produto == esponja }.precoTotal == 9.00d
        assert pedidoClienteCarlos.itensPedido.find { it.produto == esponja }.precoUnitario == 4.50d
        assert pedidoClienteCarlos.itensPedido.find { it.produto == esponja }.quantidade == 2

        and: "o valor total de acordo com os produtos selecionados pelo outro cliente"
        assert pedidoClienteEduardo.valorTotal == 62.06d
        assert sdf.format(pedidoClienteEduardo.data) == sdf.format(new Date())

        assert pedidoClienteEduardo.itensPedido.size() == 3

        assert pedidoClienteEduardo.itensPedido.find { it.produto == detergente }.precoTotal == 3.98d
        assert pedidoClienteEduardo.itensPedido.find { it.produto == detergente }.precoUnitario == 1.99d
        assert pedidoClienteEduardo.itensPedido.find { it.produto == detergente }.quantidade == 2

        assert pedidoClienteEduardo.itensPedido.find { it.produto == sabaoEmPo }.precoTotal == 53.58d
        assert pedidoClienteEduardo.itensPedido.find { it.produto == sabaoEmPo }.precoUnitario == 26.79d
        assert pedidoClienteEduardo.itensPedido.find { it.produto == sabaoEmPo }.quantidade == 2

        assert pedidoClienteEduardo.itensPedido.find { it.produto == esponja }.precoTotal == 4.50d
        assert pedidoClienteEduardo.itensPedido.find { it.produto == esponja }.precoUnitario == 4.50d
        assert pedidoClienteEduardo.itensPedido.find { it.produto == esponja }.quantidade == 1
    }
}
