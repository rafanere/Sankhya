package EventoProgramavel;

import Utilitarios.NativeSqlDecorator;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class AlteraNumeroNotaNFSeEvento implements EventoProgramavelJava {
    private BigDecimal numeroUnicoNota;
    private BigDecimal numeroNota;
    private String numeroNFSe;
    private String numeroPedido;
    private Boolean camposValidados;
    private DynamicVO notaVO;
    private DynamicVO tipoOperacaoVO;
    private NativeSqlDecorator nativeSqlDecorator;
    private final JapeWrapper tipoOperacaoDAO = JapeFactory.dao("TipoOperacao");

    private void validaNumeroNota(DynamicVO validaVO) throws Exception {
        numeroUnicoNota = validaVO.asBigDecimal("NUNOTA");
        numeroNota = validaVO.asBigDecimal("NUMNOTA");
        numeroPedido = validaVO.asString("NUMPEDIDO2");
        numeroNFSe = (validaVO.asString("NUMNFSE") == null) ? "0" : validaVO.asString("NUMNFSE").substring(5);

        BigDecimal tipoOperacao = validaVO.asBigDecimal("CODTIPOPER");
        tipoOperacaoVO = tipoOperacaoDAO.findOne("ATIVO = 'S' AND NFSE = 'N' AND CODTIPOPER = ?", tipoOperacao);

        if (tipoOperacaoVO != null) {
            validaCampos(numeroUnicoNota, numeroNota, numeroNFSe);
            if (camposValidados) {
                numeroPedido = String.valueOf(numeroNota);
                numeroNota = BigDecimal.valueOf(Long.parseLong(numeroNFSe));

                nativeSqlDecorator = new NativeSqlDecorator("UPDATE TGFCAB SET NUMNOTA = :NUMNOTA, NUMPEDIDO2 = :NUMPEDIDO2 WHERE NUNOTA = :NUNOTA");
                nativeSqlDecorator.setParametro("NUMNOTA", numeroNota);
                nativeSqlDecorator.setParametro("NUMPEDIDO2", numeroPedido);
                nativeSqlDecorator.setParametro("NUNOTA", numeroUnicoNota);
                nativeSqlDecorator.atualizar();
            }
        }
    }

    private void validaCampos(BigDecimal numeroUnicoNota, BigDecimal numeroNota, String numeroNFSe) throws Exception {
        if (numeroUnicoNota == null || numeroUnicoNota.equals(BigDecimal.ZERO)) {
            camposValidados = false;
        } else if (numeroNFSe.equals("0")) {
            camposValidados = false;
        } else if (numeroNota == null || numeroNota.equals(BigDecimal.ZERO)) {
            camposValidados = false;
        } else if (numeroNFSe.equals(numeroNota.toString())) {
            camposValidados = false;
        } else {
            camposValidados = true;
        }
    }

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        notaVO = (DynamicVO) event.getVo();
        try {
            validaNumeroNota(notaVO);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext tranCtx) throws Exception {

    }
}
