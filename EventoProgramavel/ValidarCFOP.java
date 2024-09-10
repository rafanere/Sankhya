package EventoProgramavel;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.util.Collection;

public class ValidarCFOP implements EventoProgramavelJava {
// Evento para a TGFCAB. Vai pesquisar cada item incluído e verificará se o CFOP está preenchido.
    JapeWrapper itemNotaDAO = JapeFactory.dao("ItemNota");

    private void validaPreenchimentoCFOP(BigDecimal numeroUnico) throws Exception {
        // pesquisa todos os itens desse número único
        Collection<DynamicVO> itensNotaVOs = itemNotaDAO.find("NUNOTA = ?", numeroUnico);
        // para cada item
        for (DynamicVO itemNotaVO : itensNotaVOs) {
            // puxa o código do cfop
            BigDecimal codigoCFOP = itemNotaVO.asBigDecimalOrZero("CODCFO");
            // puxa o código do produto
            BigDecimal codigoProduto = itemNotaVO.asBigDecimal("CODPROD");
            // se o código do CFOP for zero ou vazio
            if (codigoCFOP == null || codigoCFOP.equals(BigDecimal.ZERO)) {
                // manda mensagem de erro
                throw new Exception("Necessário preencher o CFOP do produto " + codigoProduto + " antes de prosseguir com a confirmação.");
            }
        }
    }

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {
        // declaração de variável de confirmação
        boolean confirmando = JapeSession.getPropertyAsBoolean("CabecalhoNota.confirmando.nota", Boolean.FALSE);
        DynamicVO eventoVO = (DynamicVO) event.getVo();
        // pega o número único
        BigDecimal numeroUnico = eventoVO.asBigDecimal("NUNOTA");
        // pega o tipo de movimento
        String tipoMovimento = eventoVO.asString("TIPMOV");
        // se estiver confirmando e for nota de venda ou compra
        if (confirmando && (tipoMovimento.equals("C") || tipoMovimento.equals("V"))) {
            // chama o método de validação
            validaPreenchimentoCFOP(numeroUnico);
        }
    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext tranCtx) throws Exception {

    }
}
