package br.com.batistao

import br.com.batistao.config.DatabaseApplication
import br.com.batistao.model.Cliente
import br.com.batistao.model.Pedido
import br.com.batistao.model.Produto
import br.com.batistao.repository.ClienteRepository
import br.com.batistao.repository.PedidoRepository
import br.com.batistao.repository.ProdutoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.ConfigFileApplicationContextInitializer
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.text.SimpleDateFormat

/**
 * Created by ceb on 05/05/16.
 */
@ActiveProfiles(profiles = "test")
@IntegrationTest
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = DatabaseApplication, initializers = ConfigFileApplicationContextInitializer.class)
class PedidoIntegrationSpec extends Specification {

    @Autowired
    private ClienteRepository clienteRepository

    @Autowired
    private ProdutoRepository produtoRepository

    @Autowired
    private PedidoRepository pedidoRepository

    def "Teste para salvar e recuperar o pedido"() {
        given:
        Produto sabaoEmPo = new Produto("OMO", "Sabão em Pó OMO Progress 1,8 kg", 26.79)
        produtoRepository.save(sabaoEmPo)
        Produto detergente = new Produto("Ype", "Detergente Líquido Ype Neutro 500 Ml", 1.99)
        produtoRepository.save(detergente)
        Produto esponja = new Produto("Scoth Brite", "Esponja Limpeza Scoth Brite 3M", 4.50)
        produtoRepository.save(esponja)

        Cliente cliente = new Cliente("Carlos", new Date(), "cezbatistao@email.com.br")
        clienteRepository.save(cliente)

        Pedido pedido = new Pedido(cliente)
        pedido.comprar(detergente, 4)
        pedido.comprar(esponja, 2)

        when:
        pedidoRepository.save(pedido)

        then:
        Pedido pedido1Salvo = pedidoRepository.findOneFetch(pedido.id)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

        assert pedido1Salvo.valorTotal == 16.96d
        assert sdf.format(pedido1Salvo.data) == sdf.format(new Date())

        assert pedido1Salvo.itensPedido.size() == 2

        assert pedido1Salvo.itensPedido.find { it.produto == detergente }.precoTotal == 7.96d
        assert pedido1Salvo.itensPedido.find { it.produto == detergente }.precoUnitario == 1.99d
        assert pedido1Salvo.itensPedido.find { it.produto == detergente }.quantidade == 4

        assert pedido1Salvo.itensPedido.find { it.produto == esponja }.precoTotal == 9.00d
        assert pedido1Salvo.itensPedido.find { it.produto == esponja }.precoUnitario == 4.50d
        assert pedido1Salvo.itensPedido.find { it.produto == esponja }.quantidade == 2
    }
}
