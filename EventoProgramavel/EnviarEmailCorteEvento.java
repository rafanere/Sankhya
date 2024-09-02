package EventoProgramavel;

import Utilitarios.EmailUtils;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class EnviarEmailCorteEvento implements EventoProgramavelJava {
    // Inicializa os DAOs para acessar dados do Produto, BH_RECCOR (Corte de itens no recebimento), Vendedor (que neste caso é comprador) e Usuário
    // DAO significa Data Access Object. É um objeto para acesso ao banco de dados
    private final JapeWrapper produtoDAO = JapeFactory.dao("Produto");
    private final JapeWrapper bh_recCor = JapeFactory.dao("BHReccor");
    private final JapeWrapper compradorDAO = JapeFactory.dao("Vendedor");
    private final JapeWrapper usuarioDAO = JapeFactory.dao("Usuario");
    private final JapeWrapper parceiroDAO = JapeFactory.dao("Parceiro");

    // Obtém o código do comprador baseado no código do produto
    private BigDecimal getCodigoComprador(BigDecimal codigoProduto) throws Exception {
        DynamicVO compradorVO = produtoDAO.findByPK(codigoProduto);
        return compradorVO.asBigDecimalOrZero("AD_CODCOMPRADOR");
    }

    // Obtém a descrição do produto baseado no código do produto
    private String getDescricaoProduto(BigDecimal codigoProduto) throws Exception {
        DynamicVO produtoVO = produtoDAO.findByPK(codigoProduto);
        return codigoProduto + " - " + produtoVO.asString("DESCRPROD");
    }

    // Obtém o nome do parceiro fornecedor baseado no código do parceiro
    private String getNomeParceiro(BigDecimal codigoParceiro) throws Exception {
        DynamicVO parceiroVO = parceiroDAO.findByPK(codigoParceiro);
        return codigoParceiro + " - " + parceiroVO.asString("NOMEPARC");
    }

    // Obtém o e-mail do comprador baseado no código do comprador
    private String getEmailComprador(BigDecimal codigoComprador) throws Exception {
        DynamicVO compradorVO = compradorDAO.findByPK(codigoComprador);
        String emailComprador = compradorVO.asString("EMAIL");
        // Se o e-mail não estiver disponível na tabela de Compradores (TGFVEN), tenta buscar no Usuario (TSIUSU)
        if (emailComprador == null || emailComprador.isEmpty()) {
            compradorVO = usuarioDAO.findOne("CODVEND = ?", codigoComprador);
            emailComprador = compradorVO.asString("EMAIL");
        }
        // Retorna o e-mail do comprador
        return emailComprador;
    }

    // Configura e envia o e-mail com os detalhes do corte do pedido
    private void configurarEmailAEnviar(String emailDestinatario, BigDecimal numeroUnico, String descricaoProduto, String nomeParceiro) throws Exception {
        // Inicializa o utilitário para enviar e-mail.
        EmailUtils emailUtils = new EmailUtils();
        // Define o destinatário
        emailUtils.setDestinatarios(emailDestinatario);
        // Define o assunto do e-mail
        emailUtils.setAssunto("Corte no pedido de número único " + numeroUnico);
        // Define a mensagem do e-mail
        String mensagem = "Prezado(a), <br><br>Espero que este e-mail o(a) encontre bem.<br><br>" + "O produto do pedido de número único <b>" + numeroUnico + "</b> do fornecedor <b>" + nomeParceiro + "</b> foi cutiado:<br><b>" + descricaoProduto + "</b><br><br>Por gentileza verificar.";
        // Cria a mensagem do e-mail
        emailUtils.setMensagem(mensagem);
        // Envia o e-mail
        emailUtils.processar();
    }

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {
        // Método vazio - sem ação antes de inserir
    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {
        // Método vazio - sem ação antes de atualizar
    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {
        // Método vazio - sem ação antes de deletar
    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {
        // Executa após a inserção de um registro

        // Declaração e definição de variáveis
        DynamicVO cutiaVO = (DynamicVO) event.getVo();
        BigDecimal numeroUnico = cutiaVO.asBigDecimal("NUNOTA");
        BigDecimal sequencia = cutiaVO.asBigDecimal("SEQUENCIA");

        // Busca o registro na tabela BHReccor baseado no número único e na sequência
        DynamicVO bh_recCorVO = bh_recCor.findOne("NUNOTA = ? AND SEQUENCIA = ?", numeroUnico, sequencia);

        // Se o registro for encontrado, continua para buscar detalhes do produto e comprador
        if (bh_recCorVO != null) {
            BigDecimal codigoProduto = bh_recCorVO.asBigDecimalOrZero("CODPROD");
            BigDecimal codigoParceiro = bh_recCorVO.asBigDecimalOrZero("CODPARC");
            BigDecimal codigoComprador = getCodigoComprador(codigoProduto);
            String emailDestinatario = getEmailComprador(codigoComprador);
            String descricaoProduto = getDescricaoProduto(codigoProduto);
            String nomeParceiro = getNomeParceiro(codigoParceiro);

            // Utilizando os detalhes do produto, comprador e fornecedor, configura e envia o e-mail
            configurarEmailAEnviar(emailDestinatario, numeroUnico, descricaoProduto, nomeParceiro);
        }
    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        // Método vazio - sem ação após atualizar
    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {
        // Método vazio - sem ação após deletar
    }

    @Override
    public void beforeCommit(TransactionContext tranCtx) throws Exception {
        // Método vazio - sem ação antes de commit
    }
}
