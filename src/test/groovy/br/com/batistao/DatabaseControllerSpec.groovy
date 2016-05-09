package br.com.batistao

import br.com.batistao.config.DatabaseApplication
import br.com.batistao.model.Cliente
import br.com.batistao.model.Pedido
import br.com.batistao.model.Produto
import br.com.batistao.repository.ClienteRepository
import br.com.batistao.repository.PedidoRepository
import br.com.batistao.repository.ProdutoRepository
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import java.text.SimpleDateFormat

import static com.jayway.restassured.RestAssured.when

/**
 * Created by ceb on 05/05/16.
 */
@IntegrationTest
@WebAppConfiguration
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = DatabaseApplication)
class DatabaseControllerSpec extends Specification {

    @Autowired
    private ClienteRepository clienteRepository

    @Autowired
    private ProdutoRepository produtoRepository

    @Autowired
    private PedidoRepository pedidoRepository

    def "Teste para salvar e recuperar o pedido pela API REST"() {
        given:
        Produto sabaoEmPo = new Produto("OMO", "Sabão em Pó OMO Progress 1,8 kg", 26.79)
        produtoRepository.save(sabaoEmPo)
        Produto detergente = new Produto("Ype", "Detergente Líquido Ype Neutro 500 Ml", 1.99)
        produtoRepository.save(detergente)
        Produto esponja = new Produto("Scoth Brite", "Esponja Limpeza Scoth Brite 3M", 4.50)
        produtoRepository.save(esponja)

        Cliente cliente = new Cliente("Carlos", new Date(), "cezbatistao@email.com.br")
        clienteRepository.save(cliente)

        int quantidade = 3

        when:
        when().post("/supermercado/comprar/${cliente.id}/${sabaoEmPo.id}/${quantidade}").then().statusCode(HttpStatus.SC_OK)

        then:
        List<Pedido> pedidos = pedidoRepository.findAllFetch()
        assert pedidos.size() == 1

        Pedido pedido = pedidos[0]

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

        assert pedido.valorTotal == 80.37d
        assert sdf.format(pedido.data) == sdf.format(new Date())

        assert pedido.itensPedido.size() == 1

        assert pedido.itensPedido[0].precoTotal == 80.37d
        assert pedido.itensPedido[0].precoUnitario == 26.79d
        assert pedido.itensPedido[0].quantidade == quantidade
    }
}
